package jerkar.github.io;

import java.io.File;

import org.jerkar.JkBuild;
import org.jerkar.JkDoc;
import org.jerkar.JkFileTree;
import org.jerkar.JkJavaProcess;
import org.jerkar.JkLog;
import org.jerkar.utils.JkUtilsFile;
import org.jerkar.utils.JkUtilsString;
import org.jerkar.utils.JkUtilsTime;

public class SiteBuild extends JkBuild {
	
	JkFileTree jbakeDir = baseDir().sub("build/binaries/jbake-2.3.2");
	JkFileTree siteBase = JkFileTree.of(baseDir("..")); 
	
	JkFileTree currentDocDir = JkFileTree.of(baseDir("../../jerkar/org.jerkar.core/src/main/doc"));
	JkFileTree currentJavadocDir = JkFileTree.of(baseDir("../../jerkar/org.jerkar.distrib-all/build/output/javadoc-all"));
	File jerkarDist = siteBase.file("../jerkar/org.jerkar.distrib-all/build/output/jerkar-distrib.zip");
	
	JkFileTree siteDocumentation = baseDir().sub("src/content/documentation");
	File siteCurrentDistFolder = siteBase.file("binaries");
	JkFileTree siteCurrentDocDir = baseDir().sub("content/documentation");
	
	@Override
	public void clean() {
		super.clean();
		siteBase.exclude("_*/**", ".*").deleteAll();
	}
	
	@Override
	public void doDefault() {
		jbake();
	}
	
	@JkDoc({"Generates the site and imports documentation inside.", 
		    "You must have the Jerkar repo (containing the documentation) in your git home."})
	public void full() {
		copyCurrentDoc();
		jbake();
		copyCurrentDist();
		copyCurrentJavadoc();
	}
	
	public void copyCurrentDoc() {
		JkFileTree targetDocDir = siteDocumentation.sub("latest");
		for (File file : currentDocDir.include("**/*.md")) {
			String relativePath = currentDocDir.relativePath(file);
			File copied = targetDocDir.file(relativePath);
			JkLog.startln("Importing doc file " + file + " to " + copied.getPath());
			JkUtilsFile.writeString(copied, header(copied), false);
			String content = JkUtilsFile.read(file);
			JkUtilsFile.writeString(copied, content, true);
			JkLog.done();
		}
	}
	
	public void copyCurrentDist() {
		JkUtilsFile.copyFileToDir(jerkarDist, siteCurrentDistFolder, JkLog.infoStream());
	}
	
	public void copyCurrentJavadoc() {
		JkUtilsFile.copyDirContent(currentJavadocDir.root(), siteBase.file("javadoc/latest"), false);
	}
	
	public void jbake() {
		JkJavaProcess.of().withClasspath(jbakeDir.include("lib.*.jar"))
		.runJarSync(jbakeDir.file("jbake-core.jar"), "src", "..");
	}
	
	private static String header(File file) {
		String title = JkUtilsString.substringBeforeLast(file.getName(), ".md");
		title = title.replace("_", " ");
		StringBuilder result = new StringBuilder();
		result.append("title=").append(title).append("\n")
			.append("date="+ JkUtilsTime.now("yyyy-MM-dd")).append("\n")
			.append("type=page\n")
			.append("status=published\n")
			.append("~~~~~~\n\n");
		return result.toString();
	}

}
