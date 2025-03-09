ALTER TABLE subscriptions
DROP FOREIGN KEY subscriptions_ibfk_2;

ALTER TABLE subscriptions
ADD CONSTRAINT fk_subscriptions_users
FOREIGN KEY (subscribed_user_id) 
REFERENCES users(user_id) 
ON DELETE CASCADE;