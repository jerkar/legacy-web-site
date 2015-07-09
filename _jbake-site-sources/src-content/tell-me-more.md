date=2015-05-08
type=page
status=published
~~~~~~
# Tell me more
----

## What is Jerkar ?
Jerkar is both a **Java framework** for building project and an **automation tool**. It falls in the same category as <i>Ant/Ivy</i>,
 <i>Maven</i> or <i>Gradle</i>.
 In other words, it's a rich featured **pure Java** build tool.
 <br/>
 
As an **automation tool** Jerkar provides :

* the ability to threat Java sources as scripts : Java sources are **compiled on-the-fly** prior to be instantiated then method invoked
* **full dependency handling** to compile scripts : script dependencies can be made on Maven repositories, local jars or external projects
* multi-level **parameter injection** (via user-local, system-wide property files or by passing argument in the command line)
* a swiss-knife library generally usefull for performing automated task (file manipulation, logging, PGP signing, external tool launcher, ...)  

As a **build framework** it provides :

* compact **fluent style API** for performing all kind of build related stuff (file manipulation, compilation, testing, managing dependencies, read/publish to _Maven/Ivy_ repo, ...)
* a set of **template** classes for defining builds
* a **plugable architecture**
* full compatibility with **_Maven_ and _Ivy_** (Jerkar uses <a href="http://ant.apache.org/ivy">Ivy</a> under the hood)
* powerfull **multi-project** support
* a set of **conventions** that, in common cases, allows Jerkar to deduce everything from your project structure (not a single line of script/configuration required for building your project)
* built-in plugins for ***Eclipse***, ***Jacoco*** or ***SonarQube***
* ability to get project build information from the **IDE meta-data** (Eclipse _.classpath_ for example)
* **scaffolding** for creating projects from scratch


These two parts are seamlessly integrated to form a unique product that make **incredibly easy** to build Java projects.<br/>
Of course Jerkar can be used for any automation purpose , as an example, [Jerkar is used](https://github.com/jerkar/jerkar.github.io/blob/master/_jbake-site-sources/build/def/jerkar/github/io/SiteBuild.java) to generate this site.

As a library, Jerkar can be embedded in your product so you can leverage directly the the fluent API for manipulating files, launch external tools and so on.


## What makes Jerkar special ?
With Jerkar, build scripts are **plain old java classes**. This bare metal approach leads in tremendous benefits :

* for Java developers, it's **trivial to add logic** in build scripts
* when editing script, Java developers leverage of **compilation**, **code-completion** and **debug** facilities provided by their **IDE**
* Java developers have **no extra language** or **XML soup** to master
* scripts can be **launched/debugged** directly from the IDE as any class providing a Main method 
* the tool is quite **simple and fast** : in essence, Jerkar engine simply performs direct method invocations on build classes. **No black box** : it's quite easy to discover what the build is actually doing under the hood. **Jerkar source code and javadoc** is a primary source of documentation.
* scripts can directly leverage of any Java **3rd party libraries** without needing to wrap it in a plugin or a specific component
* it's straightforward to **extend**
* **refactoring** build script is easy and safe (thanks to statically typed nature of Java) 
* scripts leverage the regular Java mechanisms (Inheritance, composition, jar module dependency) to **re-use build elements** or share settings

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

 