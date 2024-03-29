[Go Back](../README.md)

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

But since the spring boot upgrade, seems like we don't need to set them in the VM options nor in the 
`spring-boot-maven-plugin` in pom.xml file.

</details>
</blockquote>



<blockquote>
<details>
    <summary><strong>Click to see details of InaccessibleObjectException</strong></summary>

### Application run failed: java.lang.reflect.InaccessibleObjectException

Right after fixing the issues with compiling the project,

`mvn clean failsafe:integration-test -Pit` started failing with `Application run failed` with 
`InaccessibleObjectException` and similar exceptions.

<blockquote>
<details>
    <summary><strong>Click here for stacktrace</strong></summary>

```exception
java.lang.reflect.InaccessibleObjectException: Unable to make protected final 
java.lang.Class java.lang.ClassLoader.findLoadedClass(java.lang.String) accessible: module java.base does not 
"opens java.lang" to unnamed module
```

</details>
</blockquote>

### Fix

I guess, since Java 17, we need to add the following `add-opens` to VM options in order to use reflection.

So fix for this problem in my setup/environment was just to set the `argLine` with `add-opens` in 
the `maven-failsafe-plugin` maven plugin in the [pom.xml](../pom.xml) file.

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${maven-surefire-plugin.version}</version>
    <configuration>
        <!--suppress UnresolvedMavenProperty -->
        <argLine>
            --add-opens java.base/java.util=ALL-UNNAMED
            --add-opens java.base/java.text=ALL-UNNAMED
            --add-opens java.base/java.lang=ALL-UNNAMED
            --add-opens java.base/java.lang.reflect=ALL-UNNAMED
            --add-opens java.desktop/java.awt.font=ALL-UNNAMED
            -D${failsafe.jacoco.exec.file.name.arg}
        </argLine>
        <test>it.pk.lucidxpo.**/*IntegrationTest.class</test>
        <skipTests>${skip.integration.tests}</skipTests>
        <testFailureIgnore>${ignore.test.failures}</testFailureIgnore>
        <reportsDirectory>${integration.tests.results.directory}</reportsDirectory>
    </configuration>
</plugin>
```

NOTE the use of `-D` with `-D${testArgLine}` as without `-D`, we'll end up with `java.lang.ClassNotFoundException: ${testArgLine}`

Apparently, the use of `-D` with `-D${testArgLine}` has fixed the `java.lang.ClassNotFoundException: ${testArgLine}` error,
but it has caused another problem, and that is the reason the jacoco is not able to generate `it.exec` data file.

So, we'll have to use `@` for placeholder replacement instead of `-D$` or just `$` for the proper jacoco results.

But don't forget to add the following to VM options if running the integration tests from IDE:

```
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/java.text=ALL-UNNAMED
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.lang.reflect=ALL-UNNAMED
--add-opens java.desktop/java.awt.font=ALL-UNNAMED
```

But since the spring boot upgrade, seems like we just need to set `--add-opens java.base/java.lang=ALL-UNNAMED` 
in the VM options and the following in pom.xml file:

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${maven-surefire-plugin.version}</version>
    <configuration>
        <!--suppress UnresolvedMavenProperty -->
        <argLine>
            @{failsafe.jacoco.exec.file.name.arg}
            --add-opens java.base/java.lang=ALL-UNNAMED
        </argLine>
        <test>it.pk.lucidxpo.**/*IntegrationTest.class</test>
        <skipTests>${skip.integration.tests}</skipTests>
        <testFailureIgnore>${ignore.test.failures}</testFailureIgnore>
        <reportsDirectory>${integration.tests.results.directory}</reportsDirectory>
    </configuration>
</plugin>
```

#### How do I use properties set by other plugins in argLine?
Maven does property replacement for `${...}` values in pom.xml before any plugin is run. So Surefire would never see 
the place-holders in its argLine property.

Since the Version 2.17 using an alternate syntax for these properties, `@{...}` allows late replacement of properties 
when the plugin is executed, so properties that have been modified by other plugins will be picked up correctly.


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

<blockquote>
<details>
    <summary><strong>Click to see details of ReflectionsException</strong></summary>

### ReflectionsException: could not create class file

After fixing the server startup problems,

now running the unit tests from IDE, are failing with `could not create class file` with `ReflectionsException`,
`IOException`, `bad magic number` etc. exceptions.

<blockquote>
<details>
    <summary><strong>Click here for stacktrace</strong></summary>

```exception
Caused by: org.reflections.ReflectionsException: could not create class file from Base.css
	at org.reflections.adapters.JavassistAdapter.getOfCreateClassObject(JavassistAdapter.java:102)
	at org.reflections.adapters.JavassistAdapter.getOfCreateClassObject(JavassistAdapter.java:24)
	at org.reflections.scanners.AbstractScanner.scan(AbstractScanner.java:30)
	... 61 common frames omitted
Caused by: java.io.IOException: bad magic number: 68746d6c
	at javassist.bytecode.ClassFile.read(ClassFile.java:825)
	at javassist.bytecode.ClassFile.<init>(ClassFile.java:154)
	at org.reflections.adapters.JavassistAdapter.getOfCreateClassObject(JavassistAdapter.java:100)
	... 63 common frames omitted
21:14:18.761 [main] DEBUG org.reflections.Reflections - could not scan file static/css/Base.css in url 
file:/Users/muhammadfaisal/Documents/projects/YNaMi/target/classes/ with scanner SubTypesScanner
org.reflections.ReflectionsException: could not create class object from file static/css/Base.css
	at org.reflections.scanners.AbstractScanner.scan(AbstractScanner.java:32)
	at org.reflections.Reflections.scan(Reflections.java:253)
	at org.reflections.Reflections.scan(Reflections.java:202)
	at org.reflections.Reflections.<init>(Reflections.java:123)
	at pk.lucidxpo.ynami.utils.ReflectionHelper.getTypesAnnotatedWith(ReflectionHelper.java:222)
	at ut.pk.lucidxpo.ynami.EntityExtendsVerifierTest.shouldVerifyThatAllTheEntitiesAreExtendedFromAuditable(EntityExtendsVerifierTest.java:24)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:578)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:436)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:55)
Caused by: org.reflections.ReflectionsException: could not create class file from Base.css
	at org.reflections.adapters.JavassistAdapter.getOfCreateClassObject(JavassistAdapter.java:102)
	at org.reflections.adapters.JavassistAdapter.getOfCreateClassObject(JavassistAdapter.java:24)
	at org.reflections.scanners.AbstractScanner.scan(AbstractScanner.java:30)
	... 61 common frames omitted
Caused by: java.io.IOException: bad magic number: 68746d6c
	at javassist.bytecode.ClassFile.read(ClassFile.java:825)
	at javassist.bytecode.ClassFile.<init>(ClassFile.java:154)
	at org.reflections.adapters.JavassistAdapter.getOfCreateClassObject(JavassistAdapter.java:100)
	... 63 common frames omitted
21:14:18.761 [main] DEBUG org.reflections.Reflections - could not scan file static/css/main.css in url 
file:/Users/muhammadfaisal/Documents/projects/YNaMi/target/classes/ with scanner TypeAnnotationsScanner
org.reflections.ReflectionsException: could not create class object from file static/css/main.css
	at org.reflections.scanners.AbstractScanner.scan(AbstractScanner.java:32)
	at org.reflections.Reflections.scan(Reflections.java:253)
	at org.reflections.Reflections.scan(Reflections.java:202)
	at org.reflections.Reflections.<init>(Reflections.java:123)
	at pk.lucidxpo.ynami.utils.ReflectionHelper.getTypesAnnotatedWith(ReflectionHelper.java:222)

```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was just to set the latest version (i.e. `0.10.2`) for the
`org.reflections` maven plugin in the [pom.xml](../pom.xml) file.

</details>
</blockquote>

<blockquote>
<details>
    <summary><strong>Click to see details of UnsupportedOperationException</strong></summary>

### UnfinishedMockingSessionException, UnsupportedOperationException, IllegalStateException: Could not find sun.misc.Unsafe

After fixing the reflection problems faced in tests,

now running the unit tests from IDE, are failing with `Could not find sun.misc.Unsafe` with `IllegalStateException`,
`UnsupportedOperationException`, `UnfinishedMockingSessionException` etc. exceptions.

<blockquote>
<details>
    <summary><strong>Click here for stacktrace</strong></summary>

```exception
org.mockito.exceptions.base.MockitoException: 
Mockito cannot mock this class: class org.modelmapper.ModelMapper.

Mockito can only mock non-private & non-final classes.
If you're not sure why you're getting this error, please report to the mailing list.


Java               : 20
JVM vendor name    : Oracle Corporation
JVM vendor version : 20.0.1+9-29
JVM name           : Java HotSpot(TM) 64-Bit Server VM
JVM version        : 20.0.1+9-29
JVM info           : mixed mode, sharing
OS name            : Mac OS X
OS version         : 13.4.1


Underlying exception : java.lang.UnsupportedOperationException: Cannot define class using reflection

	at org.mockito.junit.jupiter.MockitoExtension.beforeEach(MockitoExtension.java:165)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachCallbacks$0(TestMethodTestDescriptor.java:129)
	at org.junit.jupiter.engine.execution.ThrowableCollector.execute(ThrowableCollector.java:40)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:155)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachCallbacks(TestMethodTestDescriptor.java:128)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:107)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:58)
	Suppressed: java.lang.NullPointerException: Cannot invoke "org.mockito.MockitoSession.finishMocking()" 
	because the return value of "org.junit.jupiter.api.extension.ExtensionContext$Store.remove(Object, java.lang.Class)" is null
		at org.mockito.junit.jupiter.MockitoExtension.afterEach(MockitoExtension.java:211)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeAfterEachCallbacks$11(TestMethodTestDescriptor.java:217)
		at org.junit.jupiter.engine.execution.ThrowableCollector.execute(ThrowableCollector.java:40)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeAllAfterMethodsOrCallbacks$13(TestMethodTestDescriptor.java:229)
		at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAllAfterMethodsOrCallbacks(TestMethodTestDescriptor.java:227)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAfterEachCallbacks(TestMethodTestDescriptor.java:216)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:119)
		... 48 more
Caused by: java.lang.UnsupportedOperationException: Cannot define class using reflection
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingReflection$Dispatcher$Unavailable.defineClass(ClassInjector.java:821)
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingReflection.inject(ClassInjector.java:185)
	at net.bytebuddy.dynamic.loading.ClassLoadingStrategy$Default$InjectionDispatcher.load(ClassLoadingStrategy.java:187)
	at net.bytebuddy.dynamic.TypeResolutionStrategy$Passive.initialize(TypeResolutionStrategy.java:79)
	at net.bytebuddy.dynamic.DynamicType$Default$Unloaded.load(DynamicType.java:4457)
	... 54 more
Caused by: java.lang.IllegalStateException: Could not find sun.misc.Unsafe
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingUnsafe$Dispatcher$Disabled.initialize(ClassInjector.java:1366)
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingUnsafe.inject(ClassInjector.java:1202)
	at net.bytebuddy.dynamic.loading.ClassLoadingStrategy$ForUnsafeInjection.load(ClassLoadingStrategy.java:458)
	at net.bytebuddy.dynamic.TypeResolutionStrategy$Passive.initialize(TypeResolutionStrategy.java:79)
	at net.bytebuddy.dynamic.DynamicType$Default$Unloaded.load(DynamicType.java:4457)
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingReflection$Dispatcher$Indirect.make(ClassInjector.java:684)
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingReflection$Dispatcher$CreationAction.run(ClassInjector.java:302)
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingReflection$Dispatcher$CreationAction.run(ClassInjector.java:290)
	at java.base/java.security.AccessController.doPrivileged(AccessController.java:319)
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingReflection.<clinit>(ClassInjector.java:70)
	at net.bytebuddy.dynamic.loading.ClassLoadingStrategy$Default$InjectionDispatcher.load(ClassLoadingStrategy.java:184)
	... 79 more
Caused by: java.lang.NoSuchMethodException: sun.misc.Unsafe.defineClass(java.lang.String,[B,int,int,java.lang.ClassLoader,java.security.ProtectionDomain)
	at java.base/java.lang.Class.getMethod(Class.java:2321)
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingUnsafe$Dispatcher$CreationAction.run(ClassInjector.java:1269)
	at net.bytebuddy.dynamic.loading.ClassInjector$UsingUnsafe$Dispatcher$CreationAction.run(ClassInjector.java:1257)

org.mockito.exceptions.misusing.UnfinishedMockingSessionException: 
Unfinished mocking session detected.
Previous MockitoSession was not concluded with 'finishMocking()'.
For examples of correct usage see javadoc for MockitoSession class.

	at org.mockito.junit.jupiter.MockitoExtension.beforeEach(MockitoExtension.java:165)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeBeforeEachCallbacks$0(TestMethodTestDescriptor.java:129)
	at org.junit.jupiter.engine.execution.ThrowableCollector.execute(ThrowableCollector.java:40)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeMethodsOrCallbacksUntilExceptionOccurs(TestMethodTestDescriptor.java:155)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeBeforeEachCallbacks(TestMethodTestDescriptor.java:128)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:107)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:58)
	Suppressed: java.lang.NullPointerException: Cannot invoke "org.mockito.MockitoSession.finishMocking()" 
	because the return value of "org.junit.jupiter.api.extension.ExtensionContext$Store.remove(Object, java.lang.Class)" is null
		at org.mockito.junit.jupiter.MockitoExtension.afterEach(MockitoExtension.java:211)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeAfterEachCallbacks$11(TestMethodTestDescriptor.java:217)
		at org.junit.jupiter.engine.execution.ThrowableCollector.execute(ThrowableCollector.java:40)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeAllAfterMethodsOrCallbacks$13(TestMethodTestDescriptor.java:229)
		at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAllAfterMethodsOrCallbacks(TestMethodTestDescriptor.java:227)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeAfterEachCallbacks(TestMethodTestDescriptor.java:216)
		at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:119)
		... 48 more

```

</details>
</blockquote>

### Fix

Fix for this problem in my setup/environment was just to add the `net.bytebuddy:byte-buddy` maven
dependency in `test` scope in the [pom.xml](../pom.xml) file.

```xml

<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy</artifactId>
    <version>1.14.5</version>
    <scope>test</scope>
</dependency>
```

So, now all the unit as well as integration tests are running and passing without any exceptions.

</details>
</blockquote>


[Go Back](../README.md)