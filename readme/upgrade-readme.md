## Upgrade to JAVA 20 from JAVA 10

#### Issues faced during the upgrade

Following are the issues encountered during the upgrade process

<blockquote>
<details>
    <summary><strong>Click to see details of ExceptionInInitializerError</strong></summary>

### Fatal error compiling: java.lang.ExceptionInInitializerError

Right after changing the JAVA version from `10` to `20` in the [pom.xml](../pom.xml) file,

`mvn clean compile` started failing with fatal error.

<blockquote>
<details>
    <summary><strong>Click here for stacktrace</strong></summary>

```exception
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.7.0:testCompile (default-testCompile) on project ynami: 
Fatal error compiling: java.lang.ExceptionInInitializerError: 
Unable to make field private com.sun.tools.javac.processing.JavacProcessingEnvironment$DiscoveredProcessors 
com.sun.tools.javac.processing.JavacProcessingEnvironment.discoveredProcs accessible: 
module jdk.compiler does not "opens com.sun.tools.javac.processing" to unnamed module @11216e2e
```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was just to set the latest version (i.e. `1.18.28`) for the `lombok` maven
plugin in the [pom.xml](../pom.xml) file.

</details>
</blockquote>


<blockquote>
<details>
    <summary><strong>Click to see details of InaccessibleObjectException</strong></summary>

### Application run failed: java.lang.reflect.InaccessibleObjectException

Right after fixing the issues with compiling the project, 

`mvn clean spring-boot:run` started failing with `Application run failed` with `InaccessibleObjectException` exception.

<blockquote>
<details>
    <summary><strong>Click here for stacktrace</strong></summary>

```exception
Caused by: java.lang.reflect.InaccessibleObjectException: Unable to make protected final java.lang.Class 
java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain) 
throws java.lang.ClassFormatError accessible: module java.base does not "opens java.lang" to unnamed module @3f3e6f71
    at java.base/java.lang.reflect.AccessibleObject.throwInaccessibleObjectException(AccessibleObject.java:387)
    at java.base/java.lang.reflect.AccessibleObject.checkCanSetAccessible(AccessibleObject.java:363)
    at java.base/java.lang.reflect.AccessibleObject.checkCanSetAccessible(AccessibleObject.java:311)
    at java.base/java.lang.reflect.Method.checkCanSetAccessible(Method.java:201)
    at java.base/java.lang.reflect.Method.setAccessible(Method.java:195)
    at org.springframework.cglib.core.ReflectUtils$1.run(ReflectUtils.java:61)
    at java.base/java.security.AccessController.doPrivileged(AccessController.java:571)
    at org.springframework.cglib.core.ReflectUtils.<clinit>(ReflectUtils.java:52)
    at org.springframework.cglib.core.KeyFactory$Generator.generateClass(KeyFactory.java:243)
    at org.springframework.cglib.core.DefaultGeneratorStrategy.generate(DefaultGeneratorStrategy.java:25)
    at org.springframework.cglib.core.AbstractClassGenerator.generate(AbstractClassGenerator.java:329)
    ... 30 common frames omitted
2023-08-13 00:12:58.495 ERROR 96857 --- [  restartedMain] o.s.boot.SpringApplication               : Application run failed
```

</details>
</blockquote>

### Fix

I guess, since Java 17, we need to add the following `add-opens` to VM options in order to use reflection.

So fix for this problem in my setup/environment was just to set the `jvmArguments` with `add-opens` in the 
`spring-boot-maven-plugin` maven plugin in the [pom.xml](../pom.xml) file.

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <!-- The following 'add-opens' args are required for reflection -->
        <jvmArguments>
            --add-opens java.base/java.lang=ALL-UNNAMED
            --add-opens java.base/java.lang.reflect=ALL-UNNAMED
        </jvmArguments>
    </configuration>
</plugin>
```

But don't forget to add the following to VM options if running this application from IDE:

`--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED`

</details>
</blockquote>


<blockquote>
<details>
    <summary><strong>Click to see details of NoClassDefFoundError: javax/xml/bind/JAXBException</strong></summary>

### Application run failed: BeanCreationException, NoClassDefFoundError: javax/xml/bind/JAXBException

After fixing the `InaccessibleObjectException` exception,

`mvn clean spring-boot:run` now failing with `Application run failed` with `BeanCreationException`,
`NoClassDefFoundError`, `ClassNotFoundException` etc. exceptions.

<blockquote>
<details>
    <summary><strong>Click here for stacktrace</strong></summary>

```exception
Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
13-08-2023 00:36:07.144 [restartedMain] ERROR org.springframework.boot.SpringApplication.reportFailure - Application run failed
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' 
defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: 
Invocation of init method failed; nested exception is java.lang.NoClassDefFoundError: javax/xml/bind/JAXBException
        at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1699)
        at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:573)
        at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:495)
        at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:317)
        at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:222)
        at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:315)
......
Caused by: java.lang.NoClassDefFoundError: javax/xml/bind/JAXBException
        at org.hibernate.boot.spi.XmlMappingBinderAccess.<init>(XmlMappingBinderAccess.java:43)
        at org.hibernate.boot.MetadataSources.<init>(MetadataSources.java:87)
        at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.<init>(EntityManagerFactoryBuilderImpl.java:209)
        at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.<init>(EntityManagerFactoryBuilderImpl.java:164)
        at org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.createContainerEntityManagerFactory(SpringHibernateJpaPersistenceProvider.java:51)
        at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:365)
        at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:390)
        at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:377)
        at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:341)
        at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1758)
        at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1695)
        ... 19 common frames omitted
Caused by: java.lang.ClassNotFoundException: javax.xml.bind.JAXBException
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521)
        ... 30 common frames omitted
2023-08-13 00:36:07.144 ERROR 99605 --- [  restartedMain] o.s.boot.SpringApplication               : Application run failed
```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was just to set the latest version (i.e. `2.3.1`) for the `jaxb-api` maven
dependency as well as changing its scope from `test` to `default`  in the [pom.xml](../pom.xml) file.

```xml
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>${jaxb.api.version}</version>
</dependency>
```

So, now `mvn clean spring-boot:run` is happy and the server is up and running without any exceptions.

</details>
</blockquote>