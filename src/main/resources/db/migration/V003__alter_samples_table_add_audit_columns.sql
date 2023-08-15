ALTER TABLE ${schema_name}.`Sample`
    ADD (
        `createdBy` VARCHAR(255) NOT NULL default '',
        `createdDate` TIMESTAMP(6) NOT NULL default '',
        `lastModifiedBy` VARCHAR(255) NOT NULL default '',
        `lastModifiedDate` TIMESTAMP(6) DEFAULT NULL
        );