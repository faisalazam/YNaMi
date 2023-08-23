CREATE TABLE IF NOT EXISTS `${schema_name}`.`AuditEntry`
(
    `id`                VARCHAR(50)  NOT NULL,
    `changedAt`         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `changedBy`         VARCHAR(255) NOT NULL,
    `changedEntityId`   VARCHAR(255) NOT NULL,
    `changedEntityName` VARCHAR(255) NOT NULL,
    `fieldChanged`      VARCHAR(255) NOT NULL,
    `fromValue`         LONGTEXT     NULL,
    `toValue`           LONGTEXT     NULL,
    CONSTRAINT `PK_AuditEntry` PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `${schema_name}`.`AuditEntryArchive`
(
    `id`                VARCHAR(50)  NOT NULL,
    `changedAt`         TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    `changedBy`         VARCHAR(255) NOT NULL,
    `changedEntityId`   VARCHAR(255) NOT NULL,
    `changedEntityName` VARCHAR(255) NOT NULL,
    `fieldChanged`      VARCHAR(255) NOT NULL,
    `fromValue`         LONGTEXT     NULL,
    `toValue`           LONGTEXT     NULL,
    CONSTRAINT `PK_AuditEntryArchive` PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;