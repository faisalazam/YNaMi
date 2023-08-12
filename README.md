# YNaMi

#### Running tests from IDE

I guess, since Java 17, we need to add the following to VM options in order to use reflection.

[pom.xml](pom.xml) has already been updated accordingly, but don't forget to add the following to VM options
if running this application from IDE:

`--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED`

### Issues faced during the upgrade

[Click here for the details](readme/upgrade-readme.md)

