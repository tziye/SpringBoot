drop table if exists user;
CREATE TABLE `user`
(
    `id`          int(11) auto_increment NOT NULL,
    `name`        varchar(255) DEFAULT NULL,
    `age`         int(11)      DEFAULT NULL,
    `gender`      varchar(255) DEFAULT NULL,
    `time`        datetime(6)  DEFAULT NULL,
    `enabled`     bit(1)       DEFAULT 1,
    `version`     int(11)      DEFAULT 0,
    `deleted`     bit(1)       DEFAULT 0,
    `create_time` datetime(6)  DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime(6)  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;