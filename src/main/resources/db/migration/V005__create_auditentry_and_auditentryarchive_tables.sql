CREATE TABLE IF NOT EXISTS `AuditEntry` (
  `id`                bigint(20)   NOT NULL AUTO_INCREMENT,
  `changedAt`         TIMESTAMP(6) NOT NULL,
  `changedBy`         varchar(255) NOT NULL,
  `changedEntityId`   varchar(255) NOT NULL,
  `changedEntityName` varchar(255) NOT NULL,
  `fieldChanged`      varchar(255) NOT NULL,
  `fromValue`         longtext     NULL,
  `toValue`           longtext     NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `AuditEntryArchive` (
  `id`                bigint(20)   NOT NULL AUTO_INCREMENT,
  `changedAt`         TIMESTAMP(6) NOT NULL,
  `changedBy`         varchar(255) NOT NULL,
  `changedEntityId`   varchar(255) NOT NULL,
  `changedEntityName` varchar(255) NOT NULL,
  `fieldChanged`      varchar(255) NOT NULL,
  `fromValue`         longtext     NULL,
  `toValue`           longtext     NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;