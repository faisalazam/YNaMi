CREATE TABLE IF NOT EXISTS `${schema_name}`.`Sample`
(
    `id`        VARCHAR(50) NOT NULL,
    `active`    BIT(1)       DEFAULT NULL,
    `address`   VARCHAR(255) DEFAULT NULL,
    `firstName` VARCHAR(255) DEFAULT NULL,
    `lastName`  VARCHAR(255) DEFAULT NULL,
    CONSTRAINT `PK_Sample` PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;