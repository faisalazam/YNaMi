# YNaMi

#### Running tests from IDE

I guess, since Java 17, we need to add the following to VM options in order to use reflection.

[pom.xml](pom.xml) has already been updated accordingly, but don't forget to add the following to VM options
if running this application from IDE:

`--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED`

### Issues faced during the JAVA upgrade

[Click here for the details](readme/upgrade-to-java20-readme.md)

### Issues faced during the SpringBoot upgrade

[Click here for the details](readme/upgrade-springboot-to-3.1.2-readme.md)


#### Checksums for SQL files

Checksums are used in order to ensure that the SQL db migration scripts are not changed. It's to encourage to write
a new migration script if any more change/update is required instead of touching the existing migration scripts.

To generate checksum for the newly add SQL db migration script, just run the `DBMigrationScriptsChecksumTest` test.
The test will fail printing on the console something like below:

```
*****************************************************************************************
New DB migration/s has/ve been added. The following line/s MUST be added to checksums.txt

V004__create_auditentry_and_auditentryarchive_tables.sql,370a9d48d1ba9fcd47515b6d223727a0

*****************************************************************************************
You MUST add the new checksum/s value/s to the checksums.txt file
*****************************************************************************************
```

Copy the file name along with the checksum from that output and add it to the end of the 
[checksums.txt](src/main/resources/db/migration/checksums.txt) file.