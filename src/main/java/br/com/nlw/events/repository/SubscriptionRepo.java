package br.com.nlw.events.repository;

import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepo extends CrudRepository<Subscription, Integer> {

  public Subscription findByEventAndSubscriber(Event event, User user);

  @Query(
      value =
          "SELECT s.indication_user_id, u.user_name , COUNT(s.indication_user_id ) AS quantidade "
              + "FROM subscriptions s "
              + "INNER JOIN users u ON s.indication_user_id = u.user_id "
              + "WHERE s.event_id = :eventId "
              + "GROUP BY s.indication_user_id "
              + "ORDER BY quantidade DESC",
      nativeQuery = true)
  public List<SubscriptionRankingItem> generateRanking(@Param("eventId") Integer eventId);
}
