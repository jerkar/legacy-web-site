title=Tour
date=2015-05-11
type=page
status=published
~~~~~~

# Enjoy the Jerkar tour !
--------------------------

This page will give you a concrete idea on how Jerkar is working and what you can do with.

## Principles
Jerkar is quite simpe in its principle. You write a class extending `org.jerkar.JkBuild` in the _build/def_ folder of your project then you can execute any public zero arg methods from the command line by executing `jerkar myMethod1 myOtherMethod` at the root folder of your project.
To accomplish this, Jerkar :

* compiles every java sources found under the _build/def_ folder
* instantiates the first compiled class found implementing `org.jerkar.JkBuild`. If none the `org.jerkar.builtins.javabuild.JkJavaBuild` class is instantiated
* invokes specified methods on the created instance. If no method is specified then the `doDefault` method is invoked 

You can also set any instance field annotated with `JkOption` from the command line by typing `jerkar myMethod -myField=foo`.
<br/>

## Build styles

With Jerkar you can write task based build definition (ala _Ant_), templated ones (ala _Maven_) or rely on conventions only (no build script needed). In the following section, we will illustrate different different approach to use Jerkar. 

### Task based builds (ala *Ant*)
If you like to have complete control over your build, you may prefere the _Ant_ build style. 
The price is that you have to *write explicitly* what your build is doing. 

This example mimics the [tutorial ANT build script](http://ant.apache.org/manual/tutorial-HelloWorldWithAnt.html) 

```Java
public class AntStyleBuild extends JkBuild {

	@JkOption("Run tests in forked process if true")
	boolean forkTests;
	
	String name = "myProject";
	File src = baseDir("src");
	File buildDir = baseDir("build/output");
	File classDir = new File(buildDir, "classes");
	File jarFile = new File(buildDir, "jar/" + name + ".jar");
	String className = "my.mainClass";
	JkClasspath classpath = JkClasspath.of(baseDir().include("libs/*.jar"));
	File reportDir = new File(buildDir, "junitRreport");
	
	@Override
	public void doDefault() {
		clean();jar();run();
	}
	
	public void compile() {
		JkJavaCompiler.ofOutput(classDir).withClasspath(classpath).andSourceDir(src).compile();
		JkFileTree.of(src).exclude("**/*.java").copyTo(classDir);
	}
	
	public void jar() {
		compile();
		JkManifest.empty().addMainClass("my.main.RunClass").writeToStandardLocation(classDir);
		JkZipper.of(classDir).to(jarFile);
	}	
	
	@JkDoc("Run the application")
	public void run() {
		JkJavaProcess.of(jarFile).andClasspath(classpath).runSync();
	}
	
	public void cleanBuild() {
		clean();
		junit();
	}
	
	public void junit() {
		jar();
		JkUnit.of(classpath.and(jarFile))
				.withClassesToTest(JkFileTree.of(classDir).include("**/*Test.class"))
				.withReportDir(reportDir)
				.withReport(JunitReportDetail.FULL)
				.forked(forkTest)
				.run();
	}
	
	public static void main(String[] args) {
		new AntStyleBuild().doDefault();
	}
	
}
```

From this build definition, we can execute Jerkar the following way :

- launch/debug the `AntStyleBuild main` within your IDE
- launch/debug the `org.jerkar.JkMain main` method within your IDE. In this mode you can pass arguments as you would for the command line
- execute a command line in a shell (or on a build server)  as `jerkar doDefault` or `jerkar cleanBuild -forkTests=true`.

<br/>
### Templated builds (ala *Maven*)
For Java project you may directly extend `JkJavaBuild` template class which implements common methods for you. 
All you need is to implement what is specific.

```
public class MavenStyleBuild extends JkJavaBuild {
	
	@Override  // optional
	public JkModuleId moduleId() {
		return JkModuleId.of("org.jerkar", "script-samples");
	}

	@Override  // optional
	protected JkVersion defaultVersion() {
		return JkVersion.ofName("0.3-SNAPSHOT");
	}

	@Override
	// Optional : needless if you use only local dependencies
	protected JkDependencies dependencies() {
		return JkDependencies
				.builder()
				.on(GUAVA, "18.0")  // Popular modules are available as Java constant
				.on(JERSEY_SERVER, "1.19")
				.on("com.orientechnologies:orientdb-client:2.0.8")
				.on(JUNIT, "4.11").scope(TEST).on(MOCKITO_ALL, "1.9.5")
				.scope(TEST).build();
	}

}
```

This example is for demo purpose. Some settings can be omitted by respecting naming conventions...
<br/>

### Templated builds with conventions

If you follow conventions (as project folder named as _groupName.projectName_ and version stored in a _version.txt_ file), the above script is reduced to :

```
public class BuildSampleClassic extends JkJavaBuild {
	
	@Override  // Optional :  needless if you use only local dependencies
	protected JkDependencies dependencies() {
		return JkDependencies.builder() 
			.on(GUAVA, "18.0")  
			.on(JERSEY_SERVER, "1.19")
			.on("com.orientechnologies:orientdb-client:2.0.8")
			.on(JUNIT, "4.11").scope(TEST)
			.on(MOCKITO_ALL, "1.9.5").scope(TEST)
		.build();
	}	
}
```
### <a name="100conventional"></a><br/>100% conventional style !!!

If you use only local dependencies (jar dependencies located as bellow), you don't even need to write a build file.
Note that local dependencies have to be located in subfolder corresponding to its scope (build, compile, runtime,...).

![Project layout full convention](img/full-convention-project.png)

### Eclipse style

If Eclipse is your IDE, you can just reuse information from the _.classpath_ file by using the Eclipse plugin.
Project name, source folders and dependencies can be deducted from this file. Just activate the Eclipse plugin (see below).

### Custom builds with third parties

Your build class can depend itself from managed dependencies 

```
@JkImport("org.seleniumhq.selenium:selenium-java:2.45.0")
public class SeleniumTaskBuild extends JkJavaBuild {
	
	@JkDoc("Performs some load test using Selenium")
	public void seleniumLoadTest() {
		WebDriver driver = new FirefoxDriver();
		// ....
	}
}
```
### Multi-project builds
In a multi project context, build instances, from different projects, can use each other.
In general, the build dependency schema is the same than for the code.

```
// This is the master project for building the Jerkar full distribution
public class DistribAllBuild extends JkBuildDependencySupport {
	
	@JkProject("../org.jerkar.plugins-sonar")
	PluginsSonarBuild pluginsSonar;
	
	@JkProject("../org.jerkar.plugins-jacoco")
	PluginsJacocoBuild pluginsJacoco;
	
	public void doDefault() {
		super.doDefault();
		multiProjectDependencies().invokeDoDefaultMethodOnAllSubProjects();
		CoreBuild core = pluginsJacoco.core;  // The core project is got by transitivity
		
		JkFileTree sourceDir = ...;
		sourceDir.importFiles(pluginsSonar.packer().jarSourceFile(), pluginsJacoco.packer().jarSourceFile());
		...
	} 
	
	
}
```
<div class="alert alert-info" role="alert">
<strong>Note that you can reuse external build elements in a statically typed way !!! </<strong>
</div>

## Zero build file, out of the box features

This section answer to the following question : <blockquote>What Jerkar can do for me if I haven't written build file or have just a build file declaring dependencies only ?</blockquote>

Yep, with Jerkar, if you don't have written any build file or just have a build file containing dependency definition, you can yet perform pretty sophisticated tasks. 

### Basic tasks
    
- `jerkar help` : outputs on console available methods and options for Jerkar in the current project
- `jerkar` : cleans, compiles, unit tests and produces artifacts (this is what `JkJavaBuild#doDefault` method does)
- `jerkar doDefault publish` : same then `publish` produced artifacts on a remote repository
- `jerkar -fatJar=true -forkTests=true` : same but also produces a fat-jar (jar file containing all the runtime dependencies) and runs unit tests in a forked process
- `jerkar -fatJar -forkTests` : same, when field values are not mentioned, Jerkar uses a default value (true for boolean fields)

The last will result in the following artifact creation :
![Created artifacts](img/output.png)

### Plugin tasks

Template classes (`JkBuild` and `JkJavaBuild`) enable plugability by providing hooks on several methods. 
A plugin is just a class extending `JkBuildPlugin`  or `JkJavaBuildPlugin` and overriding default hook methods. Plugins can also provide their own methods.

- To activate a plugin on the command line, just mention the name of the plugin followed by a `#`.
- To parameter a plugin, just mention `-pluginName#fieldName=value`.
- To launch a method of a plugin, just mention `-pluginName#methodName`, note that the plugin does not need to be activated for invoking a plugin method.
- To display information about available plugins in current Jerkar instance, simply execute `jerkar helpPlugins`.

### Examples

Jerkar is shipped with [Eclipse](http://eclipse.org/), [Jacoco](http://www.eclemma.org/jacoco) and [SonarQube](http://www.sonarqube.org/) plugins out of the box.
This is how you can leverage these plugins : 

- `jerkar jacoco#` : does `doDefault` but unit tests will be instrumented by Jacoco code coverage tool  
- `jerkar jacoco# -jacoco#produceHtml` : same but also set the `JkBuildPluginJacoco#produceHtml` field to `true`. It leads in producing 
an html report along the standard jacoco.exec binary report

- `jerkar doDefault sonar#verify` : does the default + execute the `verify` method located in the sonar plugin (launch a sonar analysis)
Analysis is launched on a local SonarQube server unless you specify specific Sonar settings. Sonar will leverage of jacoco report
- `jerkar doDefault verify sonar# jacoco#` : launches the `doDefault` and `verify` methods and activates the jacoco and sonar plugins. Sonar plugin hooks the `JkBuild verify` method by launching a SonarQube analysis


## Power of the build API

Jerkar framework comes with a fluent style API making a joy to perform all kind of thing generally encountered in build domain.
Almost all classes coming from this API are <strong>immutable</strong> providing a high degree of robustness and reusability.

To have more insight, please visit [Javadoc](http://jerkar.github.io/javadoc/latest/index.html).

### File manipulation & selection

The `JkFileTree` class allow to define a set of files having a common root folder and to performs numerous operation on.
The following code, show how to construct a *war* file from dispersed elements.

```
JkFileTree war = JkFileTree.of(warDir).importDirContent(webappContentDir)
	.from("WEB-INF/classes").importDirContent(build.classDir())
	.from("../lib").importFiles(JkFileTree.of(libDir).include("**/*.jar");
war.zip().to(warFileDest);
```

`from` method returns another `JkFileTree` but rooted at the specified relative path. 
`importXxx` method copies specified element at the root of the file tree.

`JkFileTreeSet`, `JkPath` (sequence of files), `JkZipper`, `JkFileFilter` and `JkUtilsFile` are the other players for manipulate files.
All belong to `org.jerkar.file` package.

