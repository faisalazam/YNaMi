ALTER TABLE `Sample` ADD (
  `createdBy` varchar(255) NOT NULL,
  `createdDate` TIMESTAMP(6) NOT NULL,
  `lastModifiedBy` varchar(255) NOT NULL,
  `lastModifiedDate` TIMESTAMP(6) DEFAULT NULL
);