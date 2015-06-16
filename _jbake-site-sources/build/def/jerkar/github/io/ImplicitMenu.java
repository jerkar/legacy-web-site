package jerkar.github.io;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.api.utils.JkUtilsString;

public class ImplicitMenu {
	
	private static final String IDENT = "    ";
	
	private List<Item> items;
	
	public static ImplicitMenu ofMdFile(File file, int startLevel) {
		List<Item> items = new LinkedList<ImplicitMenu.Item>();
		for (String line : JkUtilsFile.readLines(file)) {
			for (int i = startLevel; i <= 6; i++) {
				String header = JkUtilsString.repeat("#", i) + " ";
				if (line.startsWith(header)) {
					String name = line.substring(header.length());
					name = name.contains(">") ? JkUtilsString.substringAfterLast(name, ">") : name;
					Item item = new Item(name, i);
					boolean found = bindLastParent(items, item);
					if (!found) {
						items.add(item);
					}
				}
			}
		}
		return new ImplicitMenu(items);
	}
	
	private static boolean bindLastParent(List<Item> items, Item item) {
		List<Item> list = new LinkedList<ImplicitMenu.Item>(items);
		Collections.reverse(list);
		boolean found = false;
		for (Item parentCandidate : list) {
			if (parentCandidate.level < item.level) {
				parentCandidate.children.add(item);
				found = true;
				break;
			}
		}
		return found;
	}  
	
	private ImplicitMenu(List<Item> items) {
		this.items = Collections.unmodifiableList(items);
	}
	
	public String html() {
		StringBuilder builder = new StringBuilder();
		builder.append("<div id=\"sidebar-wrapper\"><ul class=\"sidebar-nav\">\n");
		for (Item item : items) {
			builder.append(htmlList(item));
		}
		builder.append("</ul></div>");
		return builder.toString();
	}
	
	private String htmlList(Item item) {
		StringBuilder builder = new StringBuilder();
		builder.append(JkUtilsString.repeat(IDENT, item.level));
		builder.append("<li><a href=\""+action(item)).append("\">").append(item.name).append("</a>");
		if (item.children.isEmpty()) {
			builder.append("</li>\n");
		} else {
			builder.append("\n").append(JkUtilsString.repeat(IDENT, item.level)).append("  ")
			.append("<ul class=\"nav\">\n");
			for (Item child : item.children) {
				builder.append(htmlList(child));
			}
			builder.append(JkUtilsString.repeat(IDENT, item.level)).append("  ")
			.append("</ul>\n").append(JkUtilsString.repeat(IDENT, item.level)).append("</li>\n");
		}
		return builder.toString();
		
	}
	
	private String action(Item item) {
		return "#" + item.name;
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
		
	}

}
