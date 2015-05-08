date=2015-05-08
type=page
status=published
~~~~~~

## What is Jerkar ?
Jerkar is both a Java framework for building project and an automation tool.<br/>

As a framework Jerkar provides :

* compact fluent style API for performing all kind of build related stuff (File manipulation, compiling, launching unit test, compute dependencies, read/publish to maven repo, ...)
* a set of pluggable template classes to inherit from for defining builds
* built-in plugins for Eclipse, test coverage or _SonarQube_ analysis
* scaffolding for creating quickly project from scratch
* set of conventions that, in common case, allow Jerkar to deduce everything from your project structure (not a single line of script/configuration required for building your project)
* full compatibility with _Maven_ and _Ivy_

As an automation tool it provides :

* the ability to threat Java sources as scripts : Java sources are compiled on-the-fly prior to be instantiated and method invoked
* full dependency management to compile "scripts" (script dependencies can be made upon Maven repositories)   
* multi-project handling to allow "script" dependency between projects
* multi-level parameter injection (via user-local, system-wide property files or by passing argument in the command line)

These two parts are seamlessly integrated to form a unique product that make incredibly easy to build Java projects.<br/>
Of course Jerkar can be used for any automation purpose so as a generic build tool


## What make Jerkar a simpler, stronger and faster tool for building Java project ?
With Jerkar, build script are plain old java class. This leads in tremendous benefits :

* it's quite easy to add logic in build scripts
* when editing script, Java developer leverage of compilation, code-completion and debug facilities provided by the IDE
* the script can be launched/debugged from the IDE as any class providing a Main method 
* the tool is quite simple and performant : in essence, Jerkar engine simply performs direct method invocations on build classes
* Java developers don't have an extra language or XML soup to master
* scripts can directly leverage of any Java 3rd party libraries without needing to wrap it in a plugin or a specific component
* it's straightforward to extend Jerkar
* refactoring build script is easy and safe (thanks to statically typed nature of Java) 
* developer can leverage of regular Java mechanism (Inheritance, composition, jar module dependency) to re-use build element or share settings



