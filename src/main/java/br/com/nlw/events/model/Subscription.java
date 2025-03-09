package br.com.nlw.events.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions")
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "subscription_number")
  private Integer subscriptionNumber;

  @ManyToOne
  @JoinColumn(name = "event_id")
  private Event event;

  @ManyToOne
  @JoinColumn(name = "subscribed_user_id")
  private User subscriber;

  @ManyToOne
  @JoinColumn(name = "indication_user_id", nullable = true)
  private User indication;

  public Integer getSubscriptionNumber() {
    return subscriptionNumber;
  }

  public void setSubscriptionNumber(Integer subscriptionNumber) {
    this.subscriptionNumber = subscriptionNumber;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public User getSubscriber() {
    return subscriber;
  }

  public void setSubscriber(User subscriber) {
    this.subscriber = subscriber;
  }

  public User getIndication() {
    return indication;
  }

  public void setIndication(User indication) {
    this.indication = indication;
  }
}
