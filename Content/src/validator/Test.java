package validator;

import java.text.*;
import java.util.*;

import org.apache.commons.io.FilenameUtils;

import util.Attribute;
import util.Attributes;
import util.Tag;

class Test {
	 private static final int NROWS = 7;
	    private static final int NDAYS = 7;

	    public static void main(String argv[]) {
//	    	
//	    	Tag html = new Tag("html");
//			Tag head = new Tag("head");
//
//			// head.add(new Tag("link",
//			// "rel=stylesheet type=text/css href=http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"));
//			// head.add(new Tag("src",
//			// "src=http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"));
//			// head.add(new Tag("src",
//			// "src=http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"));
//			head.add(new Tag("link",
//					"rel=stylesheet type=text/css href=./js/styles.css"));
//			head.add(new Tag("script",
//					"src=./js/jquery.min.js"));
//			head.add(new Tag("script", "src=./js/customscript.js"));
//
//			Tag title = new Tag("title");
//			title.add("Skyscape Content Test Report");
//			head.add(title);
//			Tag body = new Tag("body");
//			Tag main = new Tag("div", "align=center");
//			body.add(main);
//			Tag headStatus = new Tag("div", "id=headStatus"); 
//			Tag headname = new Tag("div", "id=headname");
//			Tag h2 = new Tag("h2");
//			headname.add(h2);
//			h2.add("Consolidated report of ");
//			Tag p = new Tag("h3");
//			p.add("Results for all href links");
//			headStatus.add(headname);
//			Tag statusdiv = new Tag("div", "id=cellData");
//			headStatus.add(statusdiv);
//			body.add(headStatus);
//			Tag p1 = new Tag("h3");
//			p1.add("<button id='btn1' type='button' class='btn btn-success'><span id='show'>Show Fail</span></button>");
//			main.add(p1);
//	    		 			html.add(head);
//	    			html.add(body);
//	    			System.out.println(html);
//	    	  String url = "http://www.example.com/some/path/to/a/file.xml";
//
//	          String baseName = FilenameUtils.getBaseName(url);
//	          String extension = FilenameUtils.getExtension(url);
//	          String extension1 = FilenameUtils.getPathNoEndSeparator(url);
//	          String extension2 = FilenameUtils.getName(url);
//	          System.out.println(baseName);
	    	String a="section4dd";
	       //	System.out.print(a.charAt(a.length() -1));
	       	char b = a.charAt(a.length() -1);
	       	if (Character.isDigit(b)) { 
	       	    System.out.println("Digit"); 
	       	} else{
	       	 System.out.println("Letter"); 
	       	}
	       	
	    }	
	}

