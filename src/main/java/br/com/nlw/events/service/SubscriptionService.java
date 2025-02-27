package br.com.nlw.events.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import br.com.nlw.events.dto.SubscriptionOut;
import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.UserIn;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicatorNotFoundException;
import br.com.nlw.events.mapper.ISubscriptionMapper;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepo;
import br.com.nlw.events.repository.SubscriptionRepo;
import br.com.nlw.events.repository.UserRepo;

@Service
public class SubscriptionService {

    private final EventRepo eventRepo;
    private final UserRepo userRepo;
    private final SubscriptionRepo subRepo;
    private final ISubscriptionMapper subscriptionMapper;
    private final ApplicationContext applicationContext;

    public SubscriptionService(EventRepo eventRepo, UserRepo userRepo, 
            SubscriptionRepo subRepo, ISubscriptionMapper subscriptionMapper, 
            ApplicationContext applicationContext) {
        this.eventRepo = eventRepo;
        this.userRepo = userRepo;
        this.subRepo = subRepo;
        this.subscriptionMapper = subscriptionMapper;
        this.applicationContext = applicationContext;
    }

    public SubscriptionOut createSubscription(String eventName, Integer userId, UserIn userIn) {
        Event event = eventRepo.findByPrettyName(eventName);
        if (event == null) {
            throw new EventNotFoundException("Evento " + eventName + " nao existe.");
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
            throw new UserIndicatorNotFoundException("Usuario " + userId + " indicador nao existe.");
        }

        Subscription subs = new Subscription();
        subs.setEvent(event);
        subs.setSubscriber(userRec);
        subs.setIndication(indicator);

        Subscription sub = subRepo.findByEventAndSubscriber(event, userRec);
        if (sub != null) {
            throw new SubscriptionConflictException("Ja existe inscricao para o usuario " + userRec.getEmail() + " no evento " + event.getTitle());
        }
        subRepo.save(subs);

        return new SubscriptionOut(subs.getSubscriptionNumber(), "http://codecraft.com/subscription/"
                + subs.getEvent().getPrettyName() + "/" + subs.getSubscriber().getId());

    }

    @Cacheable(value = "ranking", key = "'rankingList'", cacheManager = "cacheManager")
    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
        Event event = eventRepo.findByPrettyName(prettyName);
        if (event == null) {
            throw new EventNotFoundException("Ranking do evento " + prettyName + " não existe.");
        }
        return subRepo.generateRanking(event.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
        SubscriptionService self = applicationContext.getBean(SubscriptionService.class);
        List<SubscriptionRankingItem> ranking = self.getCompleteRanking(prettyName);
        SubscriptionRankingItem item = ranking.stream()
                .filter(i -> i.userId().equals(userId)).findFirst().orElse(null);
        if (item == null) {
            throw new UserIndicatorNotFoundException("Não há inscrições com indicação para o usuario " + userId);
        }

        int position = IntStream.range(0, ranking.size())
                .filter(pos -> ranking.get(pos).userId().equals(userId))
                .findFirst().orElse(0);

        return new SubscriptionRankingByUser(item, position + 1);
    }
}
