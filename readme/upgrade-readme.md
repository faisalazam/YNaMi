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
plugin.

</details>
</blockquote>