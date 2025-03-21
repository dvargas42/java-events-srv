package br.com.nlw.events.service;

import br.com.nlw.events.dto.SubscriptionOut;
import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.UserIn;
import br.com.nlw.events.email.EmailSubscrionCompleted;
import br.com.nlw.events.exception.RankingEventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.SubscriptionEventNotFoundException;
import br.com.nlw.events.exception.UserIndicatorNotFoundException;
import br.com.nlw.events.mapper.ISubscriptionMapper;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepo;
import br.com.nlw.events.repository.SubscriptionRepo;
import br.com.nlw.events.repository.UserRepo;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

  private final EventRepo eventRepo;
  private final UserRepo userRepo;
  private final SubscriptionRepo subRepo;
  private final EmailSubscrionCompleted emailSubscrionCompleted;
  private final ISubscriptionMapper subscriptionMapper;
  private final ApplicationContext applicationContext;

  public SubscriptionService(
      EventRepo eventRepo,
      UserRepo userRepo,
      SubscriptionRepo subRepo,
      EmailSubscrionCompleted emailSubscrionCompleted,
      ISubscriptionMapper subscriptionMapper,
      ApplicationContext applicationContext) {
    this.eventRepo = eventRepo;
    this.userRepo = userRepo;
    this.subRepo = subRepo;
    this.emailSubscrionCompleted = emailSubscrionCompleted;
    this.subscriptionMapper = subscriptionMapper;
    this.applicationContext = applicationContext;
  }

  public SubscriptionOut createSubscription(String prettyName, Integer userId, UserIn userIn) {
    Event event = eventRepo.findByPrettyName(prettyName);
    if (event == null) {
      throw new SubscriptionEventNotFoundException("Event " + prettyName + " not found.");
    }
    User userRec = userRepo.findByEmail(userIn.email());
    if (userRec == null) {
      userRec = userRepo.save(subscriptionMapper.toEntity(userIn));
    }
    User indicator = null;
    if (userId != null) {
      indicator = userRepo.findById(userId).orElse(null);
    }
    if (indicator == null && userId != null) {
      throw new UserIndicatorNotFoundException("User indicator " + userId + " not found.");
    }

    Subscription subs = new Subscription();
    subs.setEvent(event);
    subs.setSubscriber(userRec);
    subs.setIndication(indicator);

    Subscription sub = subRepo.findByEventAndSubscriber(event, userRec);
    if (sub != null) {
      throw new SubscriptionConflictException(
          "There is already a registration for the user "
              + userRec.getEmail()
              + " in event "
              + event.getTitle());
    }

    subRepo.save(subs);
    emailSubscrionCompleted.execute(
        userRec.getEmail(),
        "Registration for event " + event.getTitle(),
        userRec.getName() + ", your registration has been successfully confirmed.");

    return new SubscriptionOut(
        subs.getSubscriptionNumber(),
        "http://codecraft.com/subscription/"
            + subs.getEvent().getPrettyName()
            + "/"
            + subs.getSubscriber().getId());
  }

  @Cacheable(value = "ranking", key = "#prettyName", cacheManager = "cacheManager")
  public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
    Event event = eventRepo.findByPrettyName(prettyName);
    if (event == null) {
      throw new RankingEventNotFoundException("Ranking of event " + prettyName + " not found.");
    }
    return subRepo.generateRanking(event.getEventId());
  }

  public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
    SubscriptionService self = applicationContext.getBean(SubscriptionService.class);
    List<SubscriptionRankingItem> ranking = self.getCompleteRanking(prettyName);
    SubscriptionRankingItem item =
        ranking.stream().filter(i -> i.userId().equals(userId)).findFirst().orElse(null);
    if (item == null) {
      throw new UserIndicatorNotFoundException(
          "There are no entries indicating the user " + userId);
    }
    int position =
        IntStream.range(0, ranking.size())
            .filter(pos -> ranking.get(pos).userId().equals(userId))
            .findFirst()
            .orElse(0);
    return new SubscriptionRankingByUser(item, position + 1);
  }
}
