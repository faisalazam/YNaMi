[Go Back](../README.md)

## Flyway - Database Migrations



<blockquote>
<details>
    <summary><strong>Click to see details of `Migrations have failed validation`</strong></summary>

### Unresolved dependency

`mvn clean spring-boot:run` can fail errors such as `Validate failed: Migrations have failed validation` if the previous
run of `mvn clean spring-boot:run` had resulted in failed migrations, as `flyway` would have made an entry in the
`flyway_schema_history;` db table.

And then we see the following error when we run the `mvn clean spring-boot:run` command next time.

<blockquote>
<details>
    <summary><strong>Click here for errors</strong></summary>

```exception
ERROR org.springframework.boot.SpringApplication.reportFailure - Application run failed
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'flywayInitializer' defined 
in class path resource [org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayConfiguration.class]: 
Validate failed: Migrations have failed validation
Detected failed migration to version 001 (create baseline).
Please remove any half-completed changes then run repair to fix the schema history.

Caused by: org.flywaydb.core.api.exception.FlywayValidateException: Validate failed: Migrations have failed validation
Detected failed migration to version 001 (create baseline).
Please remove any half-completed changes then run repair to fix the schema history.
```

</details>
</blockquote>

### Fix

`flyway` clearly telling us to repair the failed migrations in the logs. There are few ways to handle this issue,
including both the manual and automated solutions. But let's focus on the automated solution for now 
using `flyway callbacks` as we don't want manual intervention. 

We could consider an approach to automatically clean the failed entries from the `flyway_schema_history` after a 
failed migration. For this purpose, we can use the `afterMigrateError` Flyway callback.

Let's first create the SQL callback file `db/callback/afterMigrateError__repair.sql`:

```sql
DELETE FROM flyway_schema_history WHERE success=false;
```

This will automatically remove any failed entry from the Flyway state history, whenever a migration error occurs.

But to make `flyway` find and execute this script, we have to add it to the path like below:

```properties
spring.flyway.locations=classpath:db/migration,classpath:db/callback
```

Although it can be added to some existing properties file, but, we'll add it to a new 
`application-flyway-callback.properties` and activate this new `flyway-callback` spring profile.

`flyway-callback` spring profile should be activated only when the `flyway` spring profile is also activated.

</details>
</blockquote>



[Go Back](../README.md)
