# Motivation
So far, for building their projects, java developers generally use an XML based (_Ant_, _Maven_) or a foreign language DSL (_Gradle_, _Rake_, _SBT_, ...) tool.
They just can't use Java to create organization scalable builds. **Jerkar** fills this gap. In jerkar build scripts are plain old java classes, bringing great benefits :

* power, flexibility and robustness of Java
* compilation, code-completion and debug facilities provided by your IDE without installing 3rd party plugins/tools
* lightness, simplicity, transparency, speed : in essence, Jerkar engine simply performs method invocations on your build class
* no extra language or XML soup to master, no required configuration file
* any Java 3rd party libraries without needing to wrap it in a plugin or a specific component
* complexity tackling the same way you do for regular code (ie utility classes, SoC, inheritance, composition,...) 
* fluent and compact syntax documented through Javadoc

Additionally the following features were missing from mainstream existing tools :
* run pluggable extra features (test coverage, special packaging, static code analysis,...) without editing the build file
* write nothing-at-all for building simple/standard projects (just relying on convention and/or IDE meta-data files, even to launch static analysis tools or generate ear/war files)

See [how Jerkar adresses Java shortcomings](doc/readme-parts/java-adress-shortcomings.md) 

# Main features
Jerkar provides what a self respecting modern, enterprise scale, build system should and more :
* provides both APIs and a command line tool
* multi-level configuration (system, user, project, command line)
* powerfull dependency management (back-ended by Ivy so compatible with Maven repositories)
* publication on Ivy or Maven repositories
* multi-project support
* powerfull fluent API to manipulate files, perform  compilations, tests, archives and all build related stuff
* choice between free form builds (ala Ant) and enforced build templates (ala Maven)
* hierarchical log output monitoring execution time for each step
* pluggable architecture
* scaffolding to get projects started quickly
* ability to get information from naming convention and Eclipse files, so in simpler cases you won't need to write script at all (even to test coverage or perform SonarQube analysis) !!!
