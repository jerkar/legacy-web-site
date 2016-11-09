title=Tell me more
date=2015-08-05
type=page
status=published
~~~~~~

## What is it ?

**Jerkar** is both an **automation tool** and a **Java build framework**. You can use it as a replacement of ***Gradle***, ***Ant/Ivy*** or ***Maven*** to build your **Java or multi-technology** projects.

The special feature of Jerkar is that build definitions are **pure Java classes** instead of XML files or scripts written in dynamic languages.

## Motivation

As a **Java developer** you may have already been frustrated of not being able to write your build scripts with your favorite language and, moreover, with the **same language as the project to build**.
Indeed most of mainstream and JVM languages have first class build tool where definitions are expressed using the language itself : **Gradle** for **Groovy**, **Nodejs** based tools for **Javascript**, **SBT** for **Scala**,  **PyBuilder** for **Python**, **Rake** for **Ruby**, **Leiningen** for **Clojure**, **Kobalt** for **Kotlin** ...
But for **Java** ... nothing except some experimental tools. So for building their projects, **Java people** are prayed to : 

* use an XML soup or a foreign language for defining builds
* abandon IDE code completion, refactoring, debugger, compilation checks and well known Java mechanism for reusing elements
* learn new languages and concepts to add simple logic in their builds or extend the tool
* install/use cumbersome plugins into their IDE to ease extra concepts and languages
 
**Jerkar** purposes to fill the gap by providing a **full-featured build tool** allowing Java developers to build their projects by just writing **regular Java classes** as they are so familiar with. 

### The benefits

As said, with Jerkar, build definitions are **plain old java classes**. This bare metal approach brings concrete benefits :

* for Java developers, it's **trivial to add logic** in build scripts
* when editing build definition, Java developers leverage of **compilation**, **code-completion** and **debug** facilities provided by their **IDE**
* Java developers have **no extra language** or **XML soup** to master
* build definitions can be **launched/debugged** directly from the IDE as any class providing a Main method 
* the tool is quite **simple and fast** : in essence, Jerkar engine simply performs direct method invocations on build classes. **No black box** : it's quite easy to discover what the build is actually doing under the hood. **Jerkar source code and javadoc** are a primary source of documentation.
* scripts can directly leverage of any Java **3rd party libraries** without needing to wrap it in a plugin or a specific component
* it's straightforward to **extend**
* **refactoring** build definition is easy and safe (thanks to statically typed nature of Java) 
* build definitions leverage the regular Java mechanisms (Inheritance, composition, jar module dependency) to **re-use build elements** or share settings

## See it !

<div class="container">
	<div class="col-md-6">
<pre><code>
public class BuildSampleClassic extends JkJavaBuild {
	
    @Override protected JkDependencies dependencies() {
        return JkDependencies.builder() 
            .on(GUAVA, "18.0")  
            .on(JERSEY_SERVER, "1.19")
            .on("com.orientechnologies:orientdb-client:2.0.8")
            .on(JUNIT, "4.11").scope(TEST)
            .on(MOCKITO_ALL, "1.9.5").scope(TEST).build();
	}	
}
</code></pre>
	<legend class="small">Build class for a Java project producing a jar file. The build class extends JkJavaBuild so you need to define only what is specific. Group/Artifact name are inferred from the project folder name and version is defaulted to 'trunk-SNAPSHOT' unless injected by the caller.</legend> 
	</div>
	<div class="col-md-6">
<pre><code>
public class MavenStyleBuild extends JkJavaBuild {
	
    @Override public JkModuleId moduleId() {
        return JkModuleId.of("org.jerkar", "script-samples");
    }

    @Override protected JkVersion version() {
        return JkVersion.ofName("0.3-SNAPSHOT");
    }

    @Override protected JkDependencies dependencies() {
        return JkDependencies.builder()
            .on(GUAVA, "18.0") 
            .on(JERSEY_SERVER, "1.19")
            .on("com.orientechnologies:orientdb-client:2.0.8")
            .on(JUNIT, "4.11").scope(TEST)
            .on(MOCKITO_ALL, "1.9.5").scope(TEST).build();
    }

}
</code></pre>
	<legend class="small">Same as previous but explicitly define group/artifact name and current version.</legend> 
	</div>
	<div class="col-md-6">
<pre><code>
public class AntStyleBuild extends JkBuild {
	
    String name = "myProject";
    File src = file("src");
    File buildDir = file("build/output");
    File classDir = new File(buildDir, "classes");
    JkClasspath classpath = JkClasspath.of(baseDir().include("libs/*.jar"));
    File reportDir = new File(buildDir, "junitRreport");
	
    @Override public void doDefault() {
        clean();compile();test();
    }
	
    public void compile() {
        JkJavaCompiler.ofOutput(classDir).withClasspath(classpath).andSourceDir(src).compile();
        JkFileTree.of(src).exclude("**/*.java").copyTo(classDir);
    }
		
    public void test() {
        jar();
        JkUnit.of(classpath.and(jarFile))
            .withClassesToTest(JkFileTree.of(classDir).include("**/*Test.class"))
            .withReportDir(reportDir).run();
    }

}
</code></pre>
	<legend class="small">This build class does not extends JkJavaBuild so explicitly defines what does the build, as a ANT script.</legend> 
	</div>
	<div class="col-md-6">
<pre><code>
</code></pre>
	<legend class="small">If the project embedded all its dependencies in a conventional location, no build class/configuration is needed at all :-)</legend> 
	</div>
</div>

## Into the box

Jerkar is both an **automation tool** and a **build framework**. 

As an **automation tool** Jerkar provides :

* the ability to treat Java sources as scripts : Java sources are **compiled on-the-fly** prior to be instantiated then method invoked
* **full dependency handling** to compile scripts : script dependencies can be made on Maven repositories, local jars or external projects
* multi-level **parameter injection** (via user-local, system-wide property files or by passing argument in the command line)
* a swiss-knife library generally usefull for performing automated task (file and I/O  manipulation, logging, PGP signing, external tool launcher, ...)  

As a **build framework** it provides :

* compact **fluent style API** for performing all kind of build related stuff (file manipulation, compilation, testing, managing dependencies, read/publish to _Maven/Ivy_ repo, ...)
* a set of **template** classes to inherit from to create build classes with minimal typing
* a **plugable architecture**
* full compatibility with **_Maven_ and _Ivy_** (Jerkar uses <a href="http://ant.apache.org/ivy">Ivy</a> under the hood)
* powerfull **multi-project** support
* a set of **conventions** that, in common cases, allows Jerkar to deduce everything from your project structure (not a single line of script/configuration required for building your project)
* built-in plugins for ***Eclipse***, ***Jacoco*** and ***SonarQube***
* ability to get project build information from the **IDE meta-data** (Eclipse _.classpath_ for example)
* **scaffolding** for creating projects from scratch


These two parts are seamlessly integrated to form a unique product. <br/>

## Multitechno projects

Beside **building Java projects**, Jerkar can be used for **any automation purpose**, for example, [Jerkar is used](https://github.com/jerkar/jerkar.github.io/blob/master/_jbake-site-sources/build/def/jerkar/github/io/SiteBuild.java) to generate this site.

For building **multi-techno** projects, we suggest the following approach : 

* Each **sub-project** builds using its **own 'native' tool** (e.g. *nodejs/Webpack* for web-client, *Jerkar* for java server and *Haskell Cabal* for *Haskell* modules)
* **Jerkar** performs the **master build** by **delegating sub-builds** and binds all together to pack the whole distribution. 

## Library

Jerkar can also be **embedded** in your product as a simple jar library, to leverage directly the fluent API for manipulating files, launch external tools or other. It is available on [Maven Central](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.jerkar%22%20AND%20a%3A%22core%22). 

Icing on the cake : Jerkar has **zero dependency**.



## What about Java cons ?
So far, the two main reasons invoked why Java not favored to write build scripts are :

1. build scripts written in Java would need to be compiled themselves prior to be executed
2. Java is verbose 

Jerkar overcomes these two points by :

* **compiling scripts on-the-fly** prior to execute them
* providing **compact fluent API and sensitive templates** to minimize developers efforts and script verbosity.

Jerkar scripts reveal to be **much more concise than their equivalent XML** based tool and roughly equivalent to script written with dynamic languages. Have a look at [Jerkar tour](./tour.html).
Beside the script compilation phase duration is not significant (less than 1 second in most of cases).  

Another point is that **all** Jerkar public class are prefixed with `Jk`. So it makes convenient to distinguish them when using IDE auto-completion.


