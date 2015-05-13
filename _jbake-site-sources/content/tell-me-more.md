date=2015-05-08
type=page
status=published
~~~~~~

## What is Jerkar ?
Jerkar is both a Java framework for building project and an automation tool. It falls in the same category than <i>Ant/Ivy</i>,
 <i>Maven</i> or <i>Gradle</i>.<br/>

As a **framework** Jerkar provides :

* compact **fluent style API** for performing all kind of build related stuff (file manipulation, compilation, testing, managing dependencies, read/publish to _Maven/Ivy_ repo, ...)
* a set of template classes for defining builds
* a **pluggable architecture**
* full compatibility with **_Maven_ and _Ivy_**
* a set of **conventions** that, in common cases, allows Jerkar to deduce everything from your project structure (not a single line of script/configuration required for building your project)
* built-in plugins for ***Eclipse***, ***Jacoco*** or ***SonarQube*** analysis
* ability to get project build information from the **IDE meta-data** (Eclipse _.classpath_ for example)
* **scaffolding** for creating projects from scratch

As an **automation tool** it provides :

* the ability to threat Java sources as scripts : Java sources are **compiled on-the-fly** prior to be instantiated then method invoked
* full dependency handling to compile scripts : script dependencies can be made on Maven repositories, local jars or external projects
* multi-level parameter injection (via user-local, system-wide property files or by passing argument in the command line)

These two parts are seamlessly integrated to form a unique product that make **incredibly easy** to build Java projects.<br/>
Of course Jerkar can be used for any automation purpose such as a generic build tool.


## What makes Jerkar special ?
With Jerkar, build scripts are **plain old java classes**. This bare metal approach leads in tremendous benefits :

* for Java developers, it's quite **easy to add logic** in build scripts
* when editing script, Java developers leverage of **compilation**, **code-completion** and **debug** facilities provided by their **IDE**
* Java developers don't have to master an extra language or XML soup
* scripts can be launched/debugged directly from the IDE as any class providing a Main method 
* the tool is quite **simple and fast** : in essence, Jerkar engine simply performs direct method invocations on build classes
* scripts can directly leverage of any Java **3rd party libraries** without needing to wrap it in a plugin or a specific component
* it's straightforward to extend
* **refactoring** build script is easy and safe (thanks to statically typed nature of Java) 
* scripts can be developers can leverage the regular Java mechanisms (Inheritance, composition, jar module dependency) to **re-use build elements** or share settings

## What about Java cons ?
So far, the two main reasons invoked why Java not favored to write build scripts is that :

1. build scripts written in Java would need to be compiled themselves prior to be executed
2. Java is verbose

Jerkar overcomes these two points by :

* **compiling scripts on-the-fly** prior to execute them
* providing compact fluent API and sensitive templates to minimize developers efforts and script verbosity.

Jerkar scripts reveals to be **much more concise than their equivalent XML** based tool and roughly equivalent to script written with dynamic languages.
Beside the script compilation phase duration is not significant (less than 1 second in most of cases) .  
 