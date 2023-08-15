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

`mvn clean compile` started failing with Non-resolvable import POM after the upgrade.

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



[Go Back](../README.md)
