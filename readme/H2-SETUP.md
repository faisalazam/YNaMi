[Go Back](../README.md)

# H2 Database Engine

[H2][h2-url] is a Java SQL database with following main features (from the docs):

* Very fast, open source, JDBC API
* Embedded and server modes; in-memory databases
* Browser based Console application

[H2][h2-url] is an open-source lightweight Java database, which can run in the `client-server`, `file-based` as well as
`in-memory/embedded` database modes in Java applications. The data will not persist on the disk `in-memory/embedded`
mode.

## How to use in Spring Boot/Maven project

It's quite easy to set it up in a maven project. Just add the following dependency in the [pom.xml](../pom.xml) file:

```xml
<!-- In-memory database to help with testing and development activities -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>${h2.version}</version>
</dependency>
```

## Connection Modes

The following connection modes are supported:

* Embedded mode (local connections using JDBC)
* Server mode (remote connections using JDBC or ODBC over TCP/IP)
* Mixed mode (local and remote connections at the same time)

Read more about the [available modes][h2-modes-url]

## How to connect?

To connect to h2 database, here is a [list][h2-connection-url], but in our application, we are connecting it as:

```properties
spring.datasource.url=jdbc:h2:file:./h2_db/${spring.datasource.name}\
  ;MODE=MySQL\
  ;AUTO_SERVER=TRUE\
  ;DB_CLOSE_DELAY=-1\
  ;DB_CLOSE_ON_EXIT=TRUE\
  ;DATABASE_TO_UPPER=FALSE\
  ;INIT=CREATE SCHEMA IF NOT EXISTS ${spring.datasource.name}\\\
  ;SET SCHEMA ${spring.datasource.name}
```

We are setting it up as a file based db (`jdbc:h2:file`) which will be located at `./h2_db/${spring.datasource.name}`.

#### Connection Parameters

There are quite a few parameters set in that url but you may not need all of them in your setup.

* `MODE=MySQL` - o use the `MySQL` mode, use the database URL `jdbc:h2:~/test;MODE=MySQL;DATABASE_TO_LOWER=TRUE`.
  When case-insensitive identifiers are needed append `;CASE_INSENSITIVE_IDENTIFIERS=TRUE` to URL. Do not change value
  of `DATABASE_TO_LOWER` after creation of database.
* `AUTO_SERVER=TRUE` - Use this if you want to connect to this h2 from multiple places (which could be different
  processes, or VM's on the same machine or remote connections from other machines) without having to start the
  server manually. You can use the same database URL independent of whether the database is already open or not.
  This feature doesn't work with in-memory databases. But here, we set it up like that in order to be able to connect
  to h2 console from browser and from within Intellij. It'll be accessible as long as your spring boot application is up
  and running.
* `DB_CLOSE_DELAY=-1` - By default, closing the last connection to a database closes the database.
  For an in-memory database, this means the content is lost. To keep the database open, add `;DB_CLOSE_DELAY=-1` to the
  database URL. To keep the contents of an in-memory database as long as the virtual machine is alive,
  use `jdbc:h2:mem:test;DB_CLOSE_DELAY=-1`.
  This may create a memory leak, when you need to remove the database, use the [SHUTDOWN][h2-shutdown-url] command. It
  can be executed from the `Query Console`.
* `DB_CLOSE_ON_EXIT=TRUE` - By default, a database is closed when the last connection is closed. However, if it is
  never closed, the database is closed when the virtual machine exits normally, using a shutdown hook.
* `INIT` - we can write any sql statements to execute or can also use *.sql scripts for this purpose
* `SET SCHEMA` - Lastly, we setting the schema to be used.

Settings in the url are coming from `INFORMATION_SCHEMA.SETTINGS` table.

## Issues faced after upgrading

After Java and Spring Boot upgrade, application startup with H2 fails.

<blockquote>
<details>
    <summary><strong>Click to see details of `Table not found`</strong></summary>


### Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException

Application startup with H2 fails. when `togglz` enabled and when `spring.datasource.name` is set to any
schema (i.e. `YNaMi`) other than the `PUBLIC` schema.

It's quite strange to see such an error even when the `Flyway` migrations have completed successfully.
So, there is no way to believe that the tables mentioned in the logs don't exist in the database.


<blockquote>
<details>
    <summary><strong>Click here for stacktrace</strong></summary>


```exception
INFO 6578 --- [  restartedMain] o.f.c.i.database.base.BaseDatabaseType   : Database: jdbc:h2:mem:YNaMi (H2 2.2)
22-08-2023 14:49:56.267 [restartedMain] INFO  org.flywaydb.core.internal.database.base.BaseDatabaseType.info - 
Database: jdbc:h2:mem:YNaMi (H2 2.2)
2023-08-22T14:49:56.312+05:00  INFO 6578 --- [  restartedMain] o.f.core.internal.database.base.Schema   : 
Creating schema "YNaMi" ...
22-08-2023 14:49:56.312 [restartedMain] INFO  org.flywaydb.core.internal.database.base.Schema.info - 
Creating schema "YNaMi" ...
2023-08-22T14:49:56.315+05:00  INFO 6578 --- [  restartedMain] o.f.c.i.s.JdbcTableSchemaHistory         : 
Creating Schema History table "YNaMi"."flyway_schema_history" ...
22-08-2023 14:49:56.315 [restartedMain] INFO  org.flywaydb.core.internal.schemahistory.JdbcTableSchemaHistory.info - 
Creating Schema History table "YNaMi"."flyway_schema_history" ...
2023-08-22T14:49:56.341+05:00  INFO 6578 --- [  restartedMain] o.f.core.internal.command.DbMigrate      : 
Current version of schema "YNaMi": null
22-08-2023 14:49:56.341 [restartedMain] INFO  org.flywaydb.core.internal.command.DbMigrate.info - 
Current version of schema "YNaMi": null
.....
INFO  org.flywaydb.core.internal.command.DbMigrate.info - Successfully applied 4 migrations to schema "YNaMi", 
now at version v004 (execution time 00:00.028s)

ERROR org.springframework.boot.SpringApplication.reportFailure - Application run failed
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name

Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException: Table "FeatureToggles" not found; SQL statement:
```


</details>
</blockquote>


### Suggested Fixes

There are lots of suggested fixes like setting the following in the properties file:


```
spring.flyway.default-schema=YNaMi
or
spring.flyway.baseline-on-migrate = true
or
#spring.jpa.defer-datasource-initialization=true
or
spring.flyway.enabled = false => and configure flyway manually
```


Or, append the following in the datasource url:


```
CASE_INSENSITIVE_IDENTIFIERS=TRUE;
or
SCHEMA_SEARCH_PATH=INFORMATION_SCHEMA;
or
DB_CLOSE_DELAY=-1;
or
DB_CLOSE_ON_EXIT=FALSE;
```


Or instead of all that, simply use the H2's default schema: i.e. `spring.datasource.name=PUBLIC`.


### Fix which actually worked


I had to append the following in the datasource url:


```
;INIT=CREATE SCHEMA IF NOT EXISTS ${spring.datasource.name}\\\
;SET SCHEMA ${spring.datasource.name}
```


Problem was, `Flyway` was doing its job alright but H2 wasn't connecting to that database which was used by `Flyway`
and hence `table not found` errors. It was all working fine if we use the H2's default schema i.e. the `PUBLIC` schema.
But, we had to use our own schema, and the way to tell hibernate to stick to our schema, was to append `CREATE SCHEMA`
and `SET SCHEMA` to the datasource url.

</details>
</blockquote>


#### H2 creating column with character varying instead of varchar

[H2 Change Log](https://github.com/jOOQ/jOOQ/issues/9609)

#### Getting this error when trying to connect to H2 from within IntelliJ:

```
org.h2.message.DbException: General error: "org.h2.mvstore.MVStoreException: 
The file is locked: /Users/muhammadfaisal/Documents/projects/YNaMi/YNaMi.mv.db [2.1.210/7]"
```

As the documentation says; ( http://h2database.com/html/features.html#auto_mixed_mode ), used `AUTO_SERVER=TRUE` in the
url.

Changed H2 storage strategy from in-memory to file based as H2 in embedded mode allows only one database connection at
a time. So to support multiple connections, need to start H2 in server mode.

Here, multiple connections mean, connecting to our local db/H2 console from browser as well as from within IntelliJ.
With embedded mode we could connect just from browser.


[Go Back](../README.md)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[h2-url]:http://www.h2database.com/html/main.html

[h2-modes-url]:http://www.h2database.com/html/features.html#connection_modes

[h2-connection-url]:http://www.h2database.com/html/features.html#database_url

[h2-shutdown-url]:http://www.h2database.com/html/commands.html#shutdown