[Go Back](../README.md)

## Upgrade to SpringBoot 3.1.2 from 2.0.5-Release

#### Issues faced during the upgrade

Right after changing the `spring-boot-starter-parent` version from `2.0.5.RELEASE` to `3.1.2` in 
the [pom.xml](../pom.xml) file, we're going to face a lot of `cannot find symbol` and `cannot find import` kind of 
errors along-with many others.

Following are the details of the issues encountered during the upgrade process

<blockquote>
<details>
    <summary><strong>Click to see details of `Non-resolvable import POM - Selenium`</strong></summary>

### Fatal error compiling: java.lang.ExceptionInInitializerError

[pom.xml](../pom.xml) file started showing errors such as `Non-resolvable import POM after the upgrade`.

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
    <summary><strong>Click to see details of `Unresolved dependency`</strong></summary>

### Unresolved dependency

[pom.xml](../pom.xml) file started showing errors such as `Unresolved dependency`.

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
    <summary><strong>Click to see details of `Plugin not found`</strong></summary>

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



<blockquote>
<details>
    <summary><strong>Click to see details of `package does not exist`</strong></summary>

### Unresolved dependency

`mvn clean compile` started failing with errors such as `package does not exist`.

<blockquote>
<details>
    <summary><strong>Click here for errors</strong></summary>

```errors
java: package javax.persistence does not exist
java: package javax.servlet.http does not exist
java: package javax.validation.constraints does not exist

java: cannot find symbol
  symbol:   class HttpServletResponse
  
java: cannot find symbol
  symbol: class Entity
  
java: cannot find symbol
  symbol:   class Column
  
etc.
```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was to add the latest `jakarta.validation-api` dependency
in the [pom.xml](../pom.xml) file and migrating the `javax` imports to `jakarta` imports.

</details>
</blockquote>




<blockquote>
<details>
    <summary><strong>Click to see details of `BeanCreationException`</strong></summary>

### Unresolved dependency

`mvn clean compile` started failing with errors such as `Error creating bean with name 'flyway'`.

<blockquote>
<details>
    <summary><strong>Click here for errors</strong></summary>

```exception
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'flyway' defined in class path 
resource [org/springframework/boot/autoconfigure/flyway/FlywayAutoConfiguration$FlywayConfiguration.class]: 
Unexpected exception during bean creation
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:533)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:326)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:324)
	at org.springframework.boot.devtools.restart.RestartLauncher.run(RestartLauncher.java:50)
Caused by: java.lang.TypeNotPresentException: Type org.flywaydb.core.api.migration.JavaMigration not present
	at java.base/sun.reflect.generics.factory.CoreReflectionFactory.makeNamedType(CoreReflectionFactory.java:117)
	at java.base/sun.reflect.generics.visitor.Reifier.visitClassTypeSignature(Reifier.java:125)
	at java.base/sun.reflect.generics.tree.ClassTypeSignature.accept(ClassTypeSignature.java:49)
	at java.base/sun.reflect.generics.visitor.Reifier.reifyTypeArguments(Reifier.java:68)
	at java.base/sun.reflect.generics.visitor.Reifier.visitClassTypeSignature(Reifier.java:138)
	... 19 common frames omitted
Caused by: java.lang.ClassNotFoundException: org.flywaydb.core.api.migration.JavaMigration
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521)
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Class.java:496)
	at java.base/java.lang.Class.forName(Class.java:475)
	at java.base/sun.reflect.generics.factory.CoreReflectionFactory.makeNamedType(CoreReflectionFactory.java:114)
```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was just to update the latest `9.21.0` version for the
`flyway-core` maven dependencies in the [pom.xml](../pom.xml) file.

Also, have to change the way in which `Flyway` is instantiated, from something like this:

```
final Flyway flyway = new Flyway();
flyway.setDataSource(((JdbcTemplate) executor.getTemplate()).getDataSource());
```

to this:

```
final Flyway flyway = configure()
                .dataSource(((JdbcTemplate) executor.getTemplate()).getDataSource())
                .load();
```

</details>
</blockquote>



<blockquote>
<details>
    <summary><strong>Click to see details of `cannot find symbol WebSecurityConfigurerAdapter`</strong></summary>

### Unresolved dependency

`mvn clean compile` started failing with errors such as `cannot find symbol: WebSecurityConfigurerAdapter`.

<blockquote>
<details>
    <summary><strong>Click here for errors</strong></summary>

```errors
java: cannot find symbol
  symbol:   class WebSecurityConfigurerAdapter
  location: package org.springframework.security.config.annotation.web.configuration
```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was to .... WIP

</details>
</blockquote>



[Go Back](../README.md)
