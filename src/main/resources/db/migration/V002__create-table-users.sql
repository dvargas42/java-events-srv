CREATE TABLE IF NOT EXISTS users (
    user_id         INT             AUTO_INCREMENT      NOT NULL    UNIQUE,
    user_email      VARCHAR(255)    NOT NULL            UNIQUE,
    user_name       VARCHAR(255)    NOT NULL,

    PRIMARY KEY (user_id)
);