package jerkar.github.io;

import java.io.File;

import org.jerkar.JkBuild;
import org.jerkar.JkJavaProcess;
import org.jerkar.JkLog;
import org.jerkar.annotation.JkDoc;
import org.jerkar.file.JkFileTree;
import org.jerkar.utils.JkUtilsFile;
import org.jerkar.utils.JkUtilsString;
import org.jerkar.utils.JkUtilsTime;

public class SiteBuild extends JkBuild {
	
	String jbakeSrcPath = "jbake-src";
	JkFileTree contentSource = baseDir().from("src-content");
	
	JkFileTree jbakeDir = baseDir().from("build/binaries/jbake-2.3.2");
	JkFileTree siteBase = JkFileTree.of(baseDir("..")); 
	
	JkFileTree latestDocDir = JkFileTree.of(baseDir("../../jerkar/org.jerkar.core/src/main/doc"));
	JkFileTree latestJavadocDir = JkFileTree.of(baseDir("../../jerkar/org.jerkar.distrib-all/build/output/javadoc-all"));
	File latestDist = siteBase.file("../jerkar/org.jerkar.distrib-all/build/output/jerkar-distrib.zip");
	
	JkFileTree jbakeSrcContent = baseDir().from(jbakeSrcPath + "/content");
	JkFileTree siteSourceDocDir = jbakeSrcContent.from("documentation");
	File siteDistDir = siteBase.file("binaries");
	JkFileTree siteTargetDocDir = baseDir().from("content/documentation");
	
	@Override
	public void clean() {
		siteBase.exclude(".*/**", "_*/**", "binaries/**").deleteAll();
		jbakeSrcContent.deleteAll();
	}
	
	@Override
	public void doDefault() {
		jbake();
	}
	
	@JkDoc({"Generates the site and imports documentation inside.", 
		    "You must have the Jerkar repo (containing the documentation) in your git home."})
	public void full() {
		clean();
		importContent();
		addMenu();
		jbake();
		copyCurrentDist();
		copyCurrentJavadoc();
	}
	
	public void importContent() {
		importProjectDoc();
		importSiteDoc();
	}
	
	public void importProjectDoc() {
		JkFileTree targetDocDir = siteSourceDocDir.from("latest");
		for (File file : latestDocDir.include("**/*.md")) {
			String relativePath = latestDocDir.relativePath(file);
			File copied = targetDocDir.file(relativePath);
			JkLog.startln("Importing doc file " + file + " to " + copied.getPath());
			JkUtilsFile.writeString(copied, header(copied), false);
			String content = JkUtilsFile.read(file);
			JkUtilsFile.writeString(copied, content, true);
			JkLog.done();
		}
	}
	
	public void importSiteDoc() {
		contentSource.copyTo(new File(this.jbakeSrcPath, "content"));
	}
	
	public void copyCurrentDist() {
		JkUtilsFile.copyFileToDir(latestDist, siteDistDir, JkLog.infoStream());
	}
	
	public void copyCurrentJavadoc() {
		JkUtilsFile.copyDirContent(latestJavadocDir.root(), siteBase.file("javadoc/latest"), false);
	}
	
	
	public void jbake() {
		JkJavaProcess.of().withClasspath(jbakeDir.include("lib.*.jar"))
		.runJarSync(jbakeDir.file("jbake-core.jar"), jbakeSrcPath, "..");
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
	
	public static void main(String[] args) {
		new SiteBuild().clean();
	}
	
	private void addMenu() {
		for (File file : jbakeSrcContent.include("**/*.md").exclude("about.md", "download.md")) {
			String menuHtml = ImplicitMenu.ofMdFile(file, 2).html();
			JkUtilsFile.writeString(file, menuHtml, true);
		}
	}

}
