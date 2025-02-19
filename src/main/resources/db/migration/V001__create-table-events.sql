CREATE TABLE IF NOT EXISTS events (
    event_id                INT             AUTO_INCREMENT      NOT NULL    UNIQUE,
    title                   VARCHAR(255)    NOT NULL,
    pretty_name             VARCHAR(50)     NOT NULL            UNIQUE,
    location                VARCHAR(255)    NOT NULL,
    price                   FLOAT           NOT NULL,
    start_date              DATE            NOT NULL,
    end_date                DATE            NOT NULL,
    start_time              TIME            NOT NULL,
    end_time                TIME            NOT NULL,

    PRIMARY KEY (event_id)
);

DELIMITER $$

CREATE TRIGGER validate_times_before_insert
BEFORE INSERT ON events
FOR EACH ROW
BEGIN
    IF NEW.end_time <= NEW.start_time THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A hora de fim não pode ser antes da hora de início';
    END IF;
END$$

CREATE TRIGGER validate_times_before_update
BEFORE UPDATE ON events
FOR EACH ROW
BEGIN
    IF NEW.end_time <= NEW.start_time THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A hora de fim não pode ser antes da hora de início';
    END IF;
END$$

DELIMITER ;