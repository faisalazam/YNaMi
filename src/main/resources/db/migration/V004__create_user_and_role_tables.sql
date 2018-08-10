CREATE TABLE IF NOT EXISTS `Users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
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
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `createdBy` varchar(255) NOT NULL,
  `createdDate` TIMESTAMP(6) NOT NULL,
  `lastModifiedBy` varchar(255) NOT NULL,
  `lastModifiedDate` TIMESTAMP(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ROLES_NAME` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `UserRoles` (
  `userId` bigint(20) NOT NULL,
  `roleId` bigint(20) NOT NULL,
  PRIMARY KEY (`userId`,`roleId`),
  KEY `FK_USER_ROLES_ROLE_ID` (`roleId`),
  CONSTRAINT `FK_USER_ROLES_ROLE_ID` FOREIGN KEY (`roleId`) REFERENCES `Roles` (`id`),
  CONSTRAINT `FK_USER_ROLES_USER_ID` FOREIGN KEY (`userId`) REFERENCES `Users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

INSERT INTO roles(name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES('ROLE_USER', 'Test', now(), 'Test', now());
INSERT INTO roles(name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES('ROLE_ADMIN', 'Test', now(), 'Test', now());
INSERT INTO roles(name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES('ROLE_SUPPORT', 'Test', now(), 'Test', now());