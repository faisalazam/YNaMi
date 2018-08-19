-- Used by Hibernate
CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=UTF8MB4;

INSERT INTO hibernate_sequence (next_val) VALUES (0);

-- Used by Togglz
CREATE TABLE IF NOT EXISTS `FeatureToggles` (
  `FEATURE_NAME` VARCHAR(100) NOT NULL,
  `FEATURE_ENABLED` INTEGER DEFAULT NULL,
  `STRATEGY_ID` VARCHAR(200) DEFAULT NULL,
  `STRATEGY_PARAMS` VARCHAR(2000) DEFAULT NULL,
  PRIMARY KEY (`FEATURE_NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `Users` (
  `id` varchar(50) NOT NULL,
  `name` varchar(40) NOT NULL,
  `username` varchar(40) NOT NULL,
  `email` varchar(40) NOT NULL,
  `password` varchar(100) NOT NULL,
  `createdBy` varchar(255) NOT NULL,
  `createdDate` TIMESTAMP(6) NOT NULL,
  `lastModifiedBy` varchar(255) NOT NULL,
  `lastModifiedDate` TIMESTAMP(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_USERS_USERNAME` (`username`),
  UNIQUE KEY `UK_USERS_EMAIL` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `Roles` (
  `id` varchar(50) NOT NULL,
  `name` varchar(60) NOT NULL,
  `createdBy` varchar(255) NOT NULL,
  `createdDate` TIMESTAMP(6) NOT NULL,
  `lastModifiedBy` varchar(255) NOT NULL,
  `lastModifiedDate` TIMESTAMP(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ROLES_NAME` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `UserRoles` (
  `userId` varchar(50) NOT NULL,
  `roleId` varchar(50) NOT NULL,
  PRIMARY KEY (`userId`,`roleId`),
  KEY `FK_USER_ROLES_ROLE_ID` (`roleId`),
  CONSTRAINT `FK_USER_ROLES_ROLE_ID` FOREIGN KEY (`roleId`) REFERENCES `Roles` (`id`),
  CONSTRAINT `FK_USER_ROLES_USER_ID` FOREIGN KEY (`userId`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

INSERT INTO roles(id, name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES('767e2726d31d4881a9a61a8e8118843f', 'ROLE_USER', 'Test', now(), 'Test', now());
INSERT INTO roles(id, name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES('2690ac87d2134c05940bf7437111d3c6', 'ROLE_ADMIN', 'Test', now(), 'Test', now());
INSERT INTO roles(id, name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES('4594b5bb580c4e558b9dc304ccee967a', 'ROLE_SUPPORT', 'Test', now(), 'Test', now());