ALTER TABLE `${schema_name}`.`Sample`
    ADD (
        `createdBy` VARCHAR(255) NOT NULL DEFAULT '',
        `createdDate` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
        `lastModifiedBy` VARCHAR(255) NOT NULL DEFAULT '',
        `lastModifiedDate` TIMESTAMP(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6)
        );