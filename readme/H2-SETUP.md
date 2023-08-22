
H2 startup fails with togglz enabled and when spring.datasource.name is set to any schema (i.e. YNaMi) other
than the PUBLIC schema -> table not found error


```
INFO 6578 --- [  restartedMain] o.f.c.i.database.base.BaseDatabaseType   : Database: jdbc:h2:mem:YNaMi (H2 2.2)
22-08-2023 14:49:56.267 [restartedMain] INFO  org.flywaydb.core.internal.database.base.BaseDatabaseType.info - Database: jdbc:h2:mem:YNaMi (H2 2.2)
2023-08-22T14:49:56.312+05:00  INFO 6578 --- [  restartedMain] o.f.core.internal.database.base.Schema   : Creating schema "YNaMi" ...
22-08-2023 14:49:56.312 [restartedMain] INFO  org.flywaydb.core.internal.database.base.Schema.info - Creating schema "YNaMi" ...
2023-08-22T14:49:56.315+05:00  INFO 6578 --- [  restartedMain] o.f.c.i.s.JdbcTableSchemaHistory         : Creating Schema History table "YNaMi"."flyway_schema_history" ...
22-08-2023 14:49:56.315 [restartedMain] INFO  org.flywaydb.core.internal.schemahistory.JdbcTableSchemaHistory.info - Creating Schema History table "YNaMi"."flyway_schema_history" ...
2023-08-22T14:49:56.341+05:00  INFO 6578 --- [  restartedMain] o.f.core.internal.command.DbMigrate      : Current version of schema "YNaMi": null
22-08-2023 14:49:56.341 [restartedMain] INFO  org.flywaydb.core.internal.command.DbMigrate.info - Current version of schema "YNaMi": null
.....
INFO  org.flywaydb.core.internal.command.DbMigrate.info - Successfully applied 4 migrations to schema "YNaMi", now at version v004 (execution time 00:00.028s)


ERROR org.springframework.boot.SpringApplication.reportFailure - Application run failed
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name


Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException: Table "FeatureToggles" not found; SQL statement:
```

```
jdbc:h2:mem:${spring.datasource.name};MODE=MySQL;DB_CLOSE_ON_EXIT=FALSE;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;INIT=CREATE SCHEMA IF NOT EXISTS ${spring.datasource.name}\\;SET SCHEMA ${spring.datasource.name};

i.e.


```


```
spring.flyway.default-schema=YNaMi
or
spring.flyway.baseline-on-migrate = true
or
#spring.jpa.defer-datasource-initialization=true
or
spring.flyway.enabled = false => and configure flyway manually
```

```
or Update url with:

CASE_INSENSITIVE_IDENTIFIERS=TRUE;
or
SCHEMA_SEARCH_PATH=INFORMATION_SCHEMA;
or
DB_CLOSE_DELAY=-1;
or
DB_CLOSE_ON_EXIT=FALSE;
or
INIT=CREATE SCHEMA IF NOT EXISTS ${spring.datasource.name}\\;SET SCHEMA ${spring.datasource.name};

or instead of all that, simply use the H2's default schema: i.e.
spring.datasource.name=PUBLIC
```
