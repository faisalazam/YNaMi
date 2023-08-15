[Go Back](../README.md)

## Upgrade to SpringBoot 3.1.2 from 2.0.5-Release

#### Issues faced during the upgrade

Right after changing the `spring-boot-starter-parent` version from `2.0.5.RELEASE` to `3.1.2` in 
the [pom.xml](../pom.xml) file, we're going to face a lot of `cannot find symbol` and `cannot find import` kind of 
errors along-with many others.

Following are the details of the issues encountered during the upgrade process

<blockquote>
<details>
    <summary><strong>Click to see details of Non-resolvable import POM - Selenium</strong></summary>

### Fatal error compiling: java.lang.ExceptionInInitializerError

[pom.xml](../pom.xml) file started showing errors such as Non-resolvable import POM after the upgrade.

<blockquote>
<details>
    <summary><strong>Click here for stacktrace</strong></summary>

```exception
[ERROR] [ERROR] Some problems were encountered while processing the POMs:
[ERROR] Non-resolvable import POM: The following artifacts could not be resolved: 
org.seleniumhq.selenium:selenium-bom:pom:3.14.0 (absent): org.seleniumhq.selenium:selenium-bom:pom:3.14.0 was not 
found in https://repo.maven.apache.org/maven2 during a previous attempt. This failure was cached in the local repository 
and resolution is not reattempted until the update interval of central has elapsed or updates are 
forced @ org.springframework.boot:spring-boot-dependencies:3.1.2, 
~/.m2/repository/org/springframework/boot/spring-boot-dependencies/3.1.2/spring-boot-dependencies-3.1.2.pom, line 2275, column 19
```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was just to update the latest `4.11.0` version for the
`org.seleniumhq.selenium` maven dependencies in the [pom.xml](../pom.xml) file.

</details>
</blockquote>


<blockquote>
<details>
    <summary><strong>Click to see details of Unresolved dependency</strong></summary>

### Unresolved dependency

[pom.xml](../pom.xml) file started showing errors such as Unresolved dependency.

<blockquote>
<details>
    <summary><strong>Click here for errors</strong></summary>

```errors
'dependencies.dependency.version' for mysql:mysql-connector-java:jar is missing.
'dependencies.dependency.version' for joda-time:joda-time:jar is missing.
Unresolved dependency: 'mysql:mysql-connector-java:jar:unknown'
Unresolved dependency: 'joda-time:joda-time:jar:unknown'
```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was to add the latest `2.12.5` version for the
`joda-time` maven dependency, and to change the `mysql-connector-java` dependency to `mysql-connector-j`
with the latest `8.1.0` version in the [pom.xml](../pom.xml) file.

</details>
</blockquote>

<blockquote>
<details>
    <summary><strong>Click to see details of Plugin not found</strong></summary>

### Unresolved dependency

[pom.xml](../pom.xml) file started showing errors such as `Plugin not found`.

<blockquote>
<details>
    <summary><strong>Click here for errors</strong></summary>

```errors
Plugin 'maven-surefire-plugin:2.22.0' not found
Plugin 'org.apache.maven.plugins:maven-project-info-reports-plugin:3.0.0' not found
Plugin 'org.apache.maven.plugins:maven-jxr-plugin:2.5' not found
Plugin 'org.apache.maven.plugins:maven-checkstyle-plugin:3.0.0' not found
Plugin 'org.apache.maven.plugins:maven-surefire-report-plugin:2.22.0' not found
```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was just to add the latest versions for the above mentioned
maven plugins in the [pom.xml](../pom.xml) file.

</details>
</blockquote>



[Go Back](../README.md)
