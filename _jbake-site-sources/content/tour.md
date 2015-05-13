title=Tour
date=2015-05-11
type=page
status=published
~~~~~~

# Welcome to Jerkar tour

With Jerkar you can write free form build definition (ala _Ant_) or templated ones (ala _Maven_). In the following section, 
we will illustrate different different approach to use Jerkar. 
As Jerkar uses a very small set of concept, at the end of this section, you should have well understood most of the Jerkar principles.
<br/>

#### Ant style build
If you like to have complete control over your build, you may prefere the _Ant_ build style. 
The price is that you have to *write explicitly* what your build is doing. 

This example mimics the [tutorial ANT build script](http://ant.apache.org/manual/tutorial-HelloWorldWithAnt.html) 

```Java
public class AntStyleBuild extends JkBuild {
	
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
		clean();run();
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
	
	public void run() {
		jar();
		JkJavaProcess.of(jarFile).andClasspath(classpath).runSync();
	}
	
	public void cleanBuild() {
		clean();jar();
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

- launch the `AntStyleBuild.main` within your IDE
- launch the `org.jerkar.JkMain.main` method within your IDE. In this mode you
can pass arguments as you would do with the command line
- execute a command line in a shell (or on a build server)  

To execute command line, open a shell and go under the project root directory. From there you can :

- execute `jerkar doDefault` => instantiate `JkJavaBuild` and invoke the `doDefault` method.
- execute `jerkar` => do the same, the `doDefault` method is invoked when none is specified
- execute `jerkar clean junit`=> instantiate `JkJavaBuild` and invoke the `clean` then `junit` method.

Note that Jerkar can look quite similar to _Ant_. All zero-arg public method returning `void` are callable by Jerkar and 
all build definition must extends `org.jerkar.JkBuild`.

<br/>
#### Maven style build
For Java project you may directly extend `JkJavaBuild` template class which implements common methods for you. 
All you need is to implement what is specific.

```
public class MavenStyleBuild extends JkJavaBuild {
	
	@Override
	public JkModuleId moduleId() {
		return JkModuleId.of("org.jerkar", "script-samples");
	}

	@Override
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

#### Conventional style build

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

From here you can :

- execute `jerkar doDefault` => invoke the `JkJavaBuild#doDefault` method which lead in clean, compile, compile tests, run tests and pack (produce jar and source jar).
- execute `jerkar -fatJar=true -forkTests=true` => do the same but inject the `true` value to `JkJavaBuild#fatJar` and `JkJavaBuild#forkTests` fields. It leads in producing a fat-jar 
(jar file containg all the runtime dependencies) and running unit tests in a forked process.
- execute `jerkar -fatJar -forkTests` => do the same, when field values are not mentioned, Jerkar uses a default value (true for boolean fields)

- execute `jerkar jacoco#` => will instantiate the [jacoco plugin](org.jerkar.plugins-jacoco/src/main/java/org/jerkar/plugins/jacoco/JkBuildPluginJacoco.java) and bind it to the `BuidSampleClassic` instance. This plugin alter the `JkJavaBuild#unitTest` method 
in such a way that tests are run with Jacoco to produce a test coverage report. '#' is the mark of plugin in Jerkar command line.
- execute `jerkar jacoco# -jacoco#produceHtml` => will do the same but also set the `JkBuildPluginJacoco#produceHtml`field to `true`. It leads in producing 
an html report along the standard jacoco.exec binary report

- execute `jerkar doDefault sonar#verify jacoco#` => do the default + execute the method `verify` method located in the [sonar plugin] (org.jerkar.plugins-sonar/src/main/java/org/jerkar/plugins/sonar/JkBuildPluginSonar.java).
Analysis is launched on a local SonarQube server unless you specify specific Sonar settings. Sonar will leverage of jacoco report.
- execute `jerkar doDefault sonar#verify -sonar.host.url=http://my.sonar.host:8080` to specify a SonarQube server host. `-myProp=value` is the way
in Jerkar to pass parameters (called options) through the command line.

If you want the full method along available options on any build, simply type `jerkar help` and/or `jerkar helpPlugins`.

Note that there is other way for passing option than using the command line. You can define them at three other level :
- Coded in the build script itself
- In option.properties file located in Jerkar install directory
- In option.properties file located in [user home]/.jerkar directory

Note that in the complete source code, you'll find a `main` method. It's mainly intended to run the whole script friendly in your favorite IDE.
It's even faster cause you skip the script compile phase.


#### Parametrized build
___
You can set parameter in the build script itself and add your own custom parameters.. 
The following example define three possible sonar servers to run analysis on. It also forces the sonar project branch.
````java
public class BuildSampleSonarParametrized extends JkJavaBuild {
	
	@JkOption("Sonar server environment")
	protected SonarEnv sonarEnv = SonarEnv.DEV;
	
	@Override
	protected void init() {
		JkBuildPluginSonar sonarPlugin = new JkBuildPluginSonar()
			.prop(JkSonar.HOST_URL, sonarEnv.url)  // set one of the predefined host
			.prop(JkSonar.BRANCH, "myBranch");  // set the project branch
		this.plugins.activate(sonarPlugin);
	}
	
	@Override  
	protected JkDependencies dependencies() {
		return JkDependencies.builder()
			.on(GUAVA, "18.0")  
			.on(JUNIT, "4.11").scope(TEST)
		.build();
	}
	
	@Override
	public void doDefault() {
		clean();compile();unitTest();
		verify(); 
	}
	
	public enum SonarEnv {
		DEV("dev.myhost:81"),
		QA("qa.myhost:81"),
		PROD("prod.myhost:80");
		
		public final String url;
		
		SonarEnv(String url) {
			this.url = url;
		}
	}
}
``` 

The Sonar plugin is activated programatically in the script so it is not required anymore to mention it in the build script.
So `jerkar` alone performs a clean, compile, test and sonar analysis on the default sonar environment (DEV).
`jerkar -sonarEnv=PROD`run it upon the the PROD environment.  

