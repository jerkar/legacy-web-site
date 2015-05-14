title=Tour
date=2015-05-11
type=page
status=published
~~~~~~

# Welcome to Jerkar tour

With Jerkar you can write free form build definition (ala _Ant_), templated ones (ala _Maven_) or rely on conventions only (no build script needed). In the following section, 
we will illustrate different different approach to use Jerkar. 


### Principles
Jerkar is quite simpe in its principle. You write a class extending `org.jerkar.JkBuild` in the _build/def_ folder of your project then you can execute any public zero arg methods from the command line by executing `jerkar myMethod1 myOtherMethod` at the root folder of your project.
To accomplish this, Jerkar :

* compiles every java sources found under the _build/def_ folder
* instantiates the first compiled class found implementing `org.jerkar.JkBuild`. If none the `org.jerkar.builtins.javabuild.JkJavaBuild`class is instantiated
* invokes specified methods on the created instance. If no method is specified then the `doDefault`method is invoked 

You can also set any instance field annotated with `JkOption` from the command line by typing `jerkar myMethod -myField=foo`.
<br/>

#### Ant style
If you like to have complete control over your build, you may prefere the _Ant_ build style. 
The price is that you have to *write explicitly* what your build is doing. 

This example mimics the [tutorial ANT build script](http://ant.apache.org/manual/tutorial-HelloWorldWithAnt.html) 

```Java
public class AntStyleBuild extends JkBuild {

	@JkOption("Skip test if true")
	boolean skipTest;
	
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
		if (skipTest) {
			jar();
		} else {
			junit();
		}
	}
	
	public void junit() {
		jar();
		JkUnit.ofFork(classpath.and(jarFile))
				.withClassesToTest(JkFileTree.of(classDir).include("**/*Test.class"))
				.withReportDir(reportDir)
				.withReport(JunitReportDetail.FULL)
				.run();
	}
	
	public static void main(String[] args) {
		new AntStyleBuild().doDefault();
	}
	
}
```

From this build definition, we can execute Jerkar the following way :

- launch/debug the `AntStyleBuild main` within your IDE
- launch/debug the `org.jerkar.JkMain main` method within your IDE. In this mode you
can pass arguments as you would for the command line
- execute a command line in a shell (or on a build server)  as `jerkar doDefault` or `jerkar cleanBuild -skipTest=true`.

<br/>
#### Maven style
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

#### Conventional style

If you follow conventions (project folder named as _groupName.projectName_ ), the above script is reduced to :

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
<br/>
#### 100% Conventional style !!!

If you use only local dependencies (jar dependencies located as bellow), you don't even need to write a build file.
Note that local dependencies have to be located in subfolder corresponding to its scope (build, compile, runtime,...).

![Project layout full convention](img/full-convention-project.png)

----

### What can you do now ?

From a java project having a build definition as above (or just fully conventional), you can perform many tasks :

#### Basic tasks
    
- `jerkar help` : outputs on console available methods and option for Jerkar in the current project
- `jerkar doDefault publish` : cleans, compiles, unit tests and produces artifacts then `publish` produced artifacts on a remote repository.
- `jerkar -fatJar=true -forkTests=true` : same but also produces a fat-jar (jar file containg all the runtime dependencies) and runs unit tests in a forked process.
- `jerkar -fatJar -forkTests` : same, when field values are not mentioned, Jerkar uses a default value (true for boolean fields)

The last will result in the following artifact creation :
![Created artifacts](img/output.png)

#### Pluggin tasks

Template classes (`JkBuild` and `JkJavaBuild`) enable plugability by providing hooks on several methods. 
A plugin is just a class extending `JkBuildPlugin`  or `JkJavaBuildPlugin` and overriding default hook methods. Plugins can also provide their own methods.

- To activate a plugin on the command line, just mention the name of the plugin followed by a `#`.
- To parameter a plugin, just mention `-pluginName#fieldName=value`.
- To launch a method of a plugin, just mention `-pluginName#methodName`, note that the plugin does not need to be activated for invoking a plugin method.
- To display information about available plugins in current Jerkar instance, simply execute `jerkar helpPlugins`.

##### Examples

Jerkar is shipped with <a href="http://www.eclemma.org/jacoco">Jacoco</a> and <a href="http://www.sonarqube.org/">SonarQube</a> plugins out of the box.
This is how you can leverage these plugins : 

- `jerkar jacoco#` : does `doDefault` but unit tests will be instrumented by Jacoco code coverage tool  
- `jerkar jacoco# -jacoco#produceHtml` : same but also set the `JkBuildPluginJacoco#produceHtml` field to `true`. It leads in producing 
an html report along the standard jacoco.exec binary report

- `jerkar doDefault sonar#verify` : does the default + execute the `verify` method located in the sonar plugin (launch a sonar analysis)
Analysis is launched on a local SonarQube server unless you specify specific Sonar settings. Sonar will leverage of jacoco report
- `jerkar doDefault verify sonar# jacoco#` : launches the `doDefault` and `verify` methods and activates the jacoco and sonar plugins. Sonar plugin hooks the `JkBuild verify` method by launching a SonarQube analysis



