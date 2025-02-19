CREATE TABLE IF NOT EXISTS subscriptions (
    subscription_number     INT     AUTO_INCREMENT  NOT NULL    UNIQUE,
    event_id                INT     NOT NULL,
    subscribed_user_id      INT     NOT NULL,
    indication_user_id      INT,

    PRIMARY KEY (subscription_number),
    FOREIGN KEY (event_id)              REFERENCES events(event_id),
    FOREIGN KEY (subscribed_user_id)    REFERENCES users(user_id),
    FOREIGN KEY (indication_user_id)    REFERENCES users(user_id)
);