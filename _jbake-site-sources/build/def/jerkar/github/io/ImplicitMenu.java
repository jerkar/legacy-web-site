package jerkar.github.io;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.api.utils.JkUtilsIO;
import org.jerkar.api.utils.JkUtilsString;

public class ImplicitMenu {
	
	private static final String INDENT = "    ";
	
	private List<Item> items;
	
	public static ImplicitMenu ofMdFile(File file, int startLevel) {
		List<Item> items = new LinkedList<ImplicitMenu.Item>();
		List<Item> parents = new LinkedList<ImplicitMenu.Item>();
		for (String line : JkUtilsFile.readLines(file)) {
			if (!line.startsWith("#")) {
				continue;
			}
			for (int i = startLevel; i <= 6; i++) {
				String header = JkUtilsString.repeat("#", i) + " ";
				if (line.startsWith(header)) {
					String name = line.substring(header.length());
					name = name.contains(">") ? JkUtilsString.substringAfterLast(name, ">") : name;
					Item item = new Item(name, i);
					Item parent = findParent(parents, item);
					System.out.println("-------------------------------parent found " + parent + " for item " + item);
					if (parent == null) {
						items.add(item);
					} else {
						parent.children.add(item);
					}
					parents.add(item);
					
				}
			}
		}
		return new ImplicitMenu(items);
	}
	
	private static Item findParent(List<Item> parents, Item item) {
		if (parents.isEmpty()) {
			return null;
		}
		List<Item> list = new LinkedList<ImplicitMenu.Item>(parents);
		Collections.reverse(list);
		for (Item candidate : list) {
			if (candidate.level < item.level) {
				return candidate;
			}
		}
		return null;
	}  
	
	private ImplicitMenu(List<Item> items) {
		this.items = Collections.unmodifiableList(items);
	}
	
	public String html() {
		StringBuilder builder = new StringBuilder();
		builder.append("<div id=\"sidebar-wrapper\">\n" + INDENT + "<ul class=\"sidebar-nav\">\n");
		for (Item item : items) {
			builder.append(htmlList(item));
		}
		builder.append(INDENT + "</ul>\n</div>\n");
		builder.append(htmlScript());
		return builder.toString();
	}
	
	public String htmlScript() {
		return JkUtilsIO.readAsString(ImplicitMenu.class.getResourceAsStream("js.txt"));
	}
	
	private String htmlList(Item item) {
		StringBuilder builder = new StringBuilder();
		String indent = JkUtilsString.repeat(INDENT, item.level);
		builder.append(indent);
		builder.append("<li><a href=\""+action(item)).append("\">").append(item.name).append("</a>");
		if (item.children.isEmpty()) {
			builder.append("</li>\n");
		} else {
			builder.append("\n").append(indent).append("  ")
			.append("<ul class=\"nav\">\n");
			for (Item child : item.children) {
				builder.append(htmlList(child));
			}
			
			builder.append(indent).append("  ")
			.append("</ul>\n").append(indent).append("</li>\n");
		}
		return builder.toString();
		
	}
	
	private String action(Item item) {
		return  "javascript:jerkarMoveTo('"+item.name+"',"+item.level+");";
	}
	
	static class Item {
		
		final String name;
		
		final int level;
		
		final List<Item> children;

		public Item(String name, int level) {
			super();
			this.name = name;
			this.level = level;
			this.children = new LinkedList<ImplicitMenu.Item>();
		}
		
		@Override
		public String toString() {
			return name + "(" + level + ")";
		}
		
	}

}
