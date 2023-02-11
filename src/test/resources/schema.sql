drop table if exists user;
CREATE TABLE `user`
(
    `id`          int(11) auto_increment NOT NULL,
    `create_time` datetime(6)  DEFAULT NULL,
    `deleted`     bit(1)       DEFAULT NULL,
    `update_time` datetime(6)  DEFAULT NULL,
    `version`     int(11)      DEFAULT NULL,
    `age`         int(11)      DEFAULT NULL,
    `enabled`     bit(1)       DEFAULT NULL,
    `gender`      varchar(255) DEFAULT NULL,
    `name`        varchar(255) DEFAULT NULL,
    `time`        datetime(6)  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;