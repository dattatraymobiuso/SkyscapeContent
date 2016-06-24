package validator;

import java.io.File;
import java.io.IOException;
import java.text.*;
import java.util.*;

import util.Attribute;
import util.Attributes;
import util.Tag;

class Test1 {
    private static final int NROWS = 7;
    private static final int NDAYS = 7;
    public static List<String> results = new ArrayList<String>();
    public static void main(String arg[]) throws IOException {
    	
//    	results.add("A");
//    	results.add("A1");
//    	results.add("A2");
//    	
//    	
//    	Tag html = new Tag("html");
//		Tag head = new Tag("head");
//		head.add(new Tag("link", "rel=stylesheet type=text/css href=./styles.css"));
//		Tag title = new Tag("title");
//		title.add("Skyscape Content Test Report");
//		head.add(title);
//		Tag body = new Tag("body");
//		Tag main = new Tag("div", "align=center");
//		body.add(main);
//		Tag h2 = new Tag("h2");
//		main.add(h2);
//		h2.add("Consolidated report for all links");
//		Tag p = new Tag("h3");
//		p.add("Results for all href links");
//		main.add(p);
//
//		Tag table = 
//		    new Tag("table", "border=0 cellpadding=3 cellspacing=0 width=500");
//
//		// create seven rows of seven columns each
//		Tag header = new Tag("tr");
//		Tag th = new Tag("th", "class=header width=60");
//			th.add("Tag");
//		    header.add(th);
//		    table.add(header);
//
//		// fill table with empty cells for days
//		for (int i = 0; i <results.size(); i++) {
//		    Tag tr = new Tag("tr", "align=center");
//		    for (int j = 0; j <2 ; j++) {
//			Tag cell = new Tag("td", "align=center valign=center");
//			 Attributes attrs = cell.getAttributes();
//			    if ((j % 2) != 0) {
//				attrs.add(new Attribute("bgcolor", "#a0e0e0"));
//			    } else {
//				attrs.add(new Attribute("bgcolor", "#ccf0f0"));
//			    }
//			cell.add("&nbsp;");
//			cell.add("<br>\n");
//			Tag fonttag = new Tag("font", "size=+1");
//			fonttag.add(results.get(i));
//			fonttag.add("&nbsp;");
//			cell.add(fonttag);
//			cell.add("<br>\n");
//			cell.add("&nbsp;");
//			tr.add(cell);
//			
//		    }
//		 
//		    table.add(tr);
//		}
//
//		main.add(table);
//		html.add(head);
//		html.add(body);
//		System.out.println(html);
    	String txt = "artinart:kaud:url";
    	String data = "artinart:kaud:url=c/c02-138780.mp3";
    	if(txt.startsWith(txt)){
    		System.out.println("inside");
    	}else{
    		System.out.println("outside");
    	}
    	
//    	File dir = new File("Source");
//    	String aa = dir.getAbsolutePath();
//    	String ab = dir.getName();
//    	String ab1 = dir.getCanonicalPath();
//    	String ab2= dir.getParent();
//    	String ab3 = dir.getPath();
//    	String ab4 = dir.getPath();
//    	System.out.println(ab);
    }
}
