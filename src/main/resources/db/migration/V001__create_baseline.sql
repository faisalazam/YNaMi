-- Used by Hibernate
CREATE TABLE IF NOT EXISTS `${schema_name}`.`hibernate_sequence`
(
    `id`       INT AUTO_INCREMENT PRIMARY KEY,
    `next_val` bigint(20) DEFAULT NULL
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = UTF8MB4;

INSERT INTO `${schema_name}`.`hibernate_sequence` (next_val)
VALUES (0);


-- Used by Togglz
CREATE TABLE IF NOT EXISTS `${schema_name}`.`FeatureToggles`
(
    `FEATURE_NAME`    VARCHAR(100) NOT NULL UNIQUE,
    `FEATURE_ENABLED` INTEGER       DEFAULT NULL,
    `STRATEGY_ID`     VARCHAR(200)  DEFAULT NULL,
    `STRATEGY_PARAMS` VARCHAR(2000) DEFAULT NULL,
    PRIMARY KEY (`FEATURE_NAME`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `${schema_name}`.`Users`
(
    `id`               VARCHAR(50)  NOT NULL,
    `name`             VARCHAR(40)  NOT NULL,
    `username`         VARCHAR(40)  NOT NULL,
    `email`            VARCHAR(40)  NOT NULL,
    `password`         VARCHAR(100) NOT NULL,
    `createdBy`        VARCHAR(255) NOT NULL,
    `createdDate`      TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `lastModifiedBy`   VARCHAR(255) NOT NULL,
    `lastModifiedDate` TIMESTAMP(6)          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_USERS_USERNAME` (`username`),
    UNIQUE KEY `UK_USERS_EMAIL` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `${schema_name}`.`Roles`
(
    `id`               VARCHAR(50)  NOT NULL,
    `name`             VARCHAR(60)  NOT NULL,
    `createdBy`        VARCHAR(255) NOT NULL,
    `createdDate`      TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `lastModifiedBy`   VARCHAR(255) NOT NULL,
    `lastModifiedDate` TIMESTAMP(6)          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ROLES_NAME` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `${schema_name}`.`UserRoles`
(
    `userId` VARCHAR(50) NOT NULL,
    `roleId` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`userId`, `roleId`),
    KEY `FK_USER_ROLES_ROLE_ID` (`roleId`),
    CONSTRAINT `FK_USER_ROLES_ROLE_ID` FOREIGN KEY (`roleId`) REFERENCES `Roles` (`id`),
    CONSTRAINT `FK_USER_ROLES_USER_ID` FOREIGN KEY (`userId`) REFERENCES `Users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;


INSERT INTO `${schema_name}`.`Roles`(`id`, `name`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`)
VALUES ('rol_767e2726d31d4881a9a61a8e8118843f', 'ROLE_USER', 'Test', now(), 'Test', now());

INSERT INTO `${schema_name}`.`Roles`(`id`, `name`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`)
VALUES ('rol_2690ac87d2134c05940bf7437111d3c6', 'ROLE_ADMIN', 'Test', now(), 'Test', now());

INSERT INTO `${schema_name}`.`Roles`(`id`, `name`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`)
VALUES ('rol_4594b5bb580c4e558b9dc304ccee967a', 'ROLE_SUPPORT', 'Test', now(), 'Test', now());
