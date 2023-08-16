UPDATE `${schema_name}`.`Sample`
SET `active` = 1
WHERE `active` = 0;

commit;