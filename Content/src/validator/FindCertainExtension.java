package validator;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.print.Doc;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import util.Attribute;
import util.Attributes;
import util.ReusableCode;
import util.Tag;

public class FindCertainExtension {

	public static List<String> htmlFileList = null;
	public static List<String> results = null;
	public static List<String> noprotocol = null;
	private static final int NCOL = 6;
	public static List<String> thead = new ArrayList<String>();
	static String file = "File_Name";
	static String anchor = "Anchor";
	static String anchor_type = "Anchor_Type";
	static String protocol_status = "Protocol_Status";
	static String result = "Result";
	static String comment = "Comment";
	static String htmlFileName = "report/Result.html";
	static String fileSource = "Source";

	@BeforeTest
	public void getHTMLFiles() throws IOException {
		htmlFileList = new ArrayList<String>();
		results = new ArrayList<String>();
		noprotocol = new ArrayList<String>();
		thead.add(file);
		thead.add(anchor);
		thead.add(anchor_type);
		thead.add(protocol_status);
		thead.add(result);
		thead.add(comment);

		File dir = new File(fileSource);
		String[] extensions = new String[] { "html", "htm" };
		System.out.println("Getting all .html in " + dir.getCanonicalPath()
				+ " including those in subdirectories");
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions,
				true);
		for (File file : files) {
			// System.out.println(file.getCanonicalPath().trim() + ","
			// + file.getName());
			htmlFileList.add(file.getCanonicalPath().trim() + ","
					+ file.getName());

		}
		Collections.sort(htmlFileList);

		System.out.println("Total HTML files " + htmlFileList.size());
		System.out
				.println("----------------------------------------------------------------------------------");
	}

	@Test
	public void getAndVerifyLinksFromHTML() throws InterruptedException,
			IOException {

		System.out.println("Testing " + htmlFileList.size()
				+ " html files one by one");

		for (int i = 0; i < htmlFileList.size(); i++) {
			Document doc = null;
			String File = htmlFileList.get(i).trim();
			String htmlFile = getFilePath(File);
			String htmlFileName = getFileName(File);
			File input = new File(htmlFile);
			String htmlSplittedPath = getRequiredFilePath(input, fileSource);

			doc = Jsoup.parse(input, "UTF-8");
			Elements links = doc.select("a");
			for (Element link : links) {
				String linkHref = link.attr("href");
				if (link.hasAttr("href")) {
					// System.out.println(htmlFile+"::"+":"+linkHref);
					boolean noProtocol = false;
					if (linkHref.startsWith("http")
							|| linkHref.startsWith("https")
							|| linkHref.startsWith("www")) {

						if (linkHref.startsWith("www")) {
							linkHref = linkHref.replace("www", "http://www");
							noProtocol = true;
						}
						int responseCode = ReusableCode
								.getResponseCodeInt(linkHref);
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::" + "External link" + ","
								+ "protocol_status::" + noProtocol + ","
								+ "result" + "::"
								+ getExternalURLStatus(responseCode) + ","
								+ "error" + "::" + responseCode);
						// Pushing status as file, anchor, anchor_type,
						// protocol_status, result, error
						// if (noProtocol == true)
						// noprotocol.add(file+"::"+""+htmlSplittedPath + "," +
						// anchor+"::"+""+linkHref +","+
						// anchor_type+"::"+"External"
						// + "," + "protocol_status::"+noProtocol + "," +
						// "result"+"::"+getExternalURLStatus(responseCode) +
						// "," + "error"+"::"+responseCode);
						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error
					} else if (linkHref.startsWith("#")) {

						boolean iSAnchorExist = getIDStatus(linkHref, htmlFile,
								doc);
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::"
								+ "Internal-Anchor On Same HTML" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::"
								+ getTestStatus(iSAnchorExist) + "," + "error"
								+ "::" + "");
						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					} else if ((linkHref.startsWith("../"))
							&& ((linkHref.endsWith(".htm")))
							|| (linkHref.endsWith(".html"))) {

						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::"
								+ "Internal-Link Points Other HTML" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::"
								+ iSHTMLFileExits(htmlFile, linkHref) + ","
								+ "error" + "::" + "");

						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					} else if ((linkHref.startsWith("../"))) {

						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::"
								+ "Internal-Points Other HTML Anchor" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::"
								+ iSAnchorInOtherHTMLStatus(htmlFile, linkHref)
								+ "," + "error" + "::"
								+ iSAnchorInOtherHTMLExits(htmlFile, linkHref));

						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					} else if (linkHref.startsWith("artinart:KAUD:url")) {
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::" + "File on server" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::" + "" + "," + "error" + "::"
								+ "");

					} else {
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::" + "Not Tested" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::" + "" + "," + "error" + "::"
								+ "");
						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					}

				}
			}
		}

	}

	private String getExternalURLStatus(int responseCode) {
		String status;
		if (responseCode == 200) {
			status = "Pass";
		} else {
			status = "Fail";
		}
		return status;
	}

	private String getParentDirectory(String htmlFile) {
		File f1 = new File(htmlFile);
		String s1 = f1.getParent();
		File f2 = new File(s1);
		return f2.getParent();
	}

	private boolean iSOtherHTMLFileExits(String htmlFile, String linkHref) {
		boolean iSFileExist = false;
		String parentDiretory = getParentDirectory(htmlFile);
		String urlFilePath = getNewPath(parentDiretory, linkHref);
		iSFileExist = checkFileExistance(new File(urlFilePath));
		return iSFileExist;
	}

	private String iSHTMLFileExits(String htmlFile, String linkHref) {
		boolean iSFileExist = false;
		String status;
		String parentDiretory = getParentDirectory(htmlFile);
		String urlFilePath = getNewPath(parentDiretory, linkHref);
		iSFileExist = checkFileExistance(new File(urlFilePath));
		if (iSFileExist == true) {
			status = "Pass";
		} else {
			status = "Fail";
		}

		return status;
	}

	private String getRequiredFilePath(File files, String fileSource)
			throws IOException {
		String fileName = "";
		String file = files.getCanonicalPath().trim();
		String[] temp = file.split(fileSource);
		if (temp.length > 1) {
			fileName = temp[1];
		} else {
			fileName = "fail to get file name";
		}
		return fileName;
	}

	// private boolean iSAnchorInOtherHTMLFileExits(String htmlFile,
	// String linkHref) throws IOException {
	//
	// boolean iSFileExist = false;
	// boolean iSAnchorInFileExist = false;
	// boolean status = false;
	// String parentDiretory = getParentDirectory(htmlFile);
	// String urlFullFilePath = getNewPath(parentDiretory, linkHref);
	// // System.out.println(urlFilePath);
	// String anchor = getAnchorFromURL(urlFullFilePath);
	// String fileURL = getFileURL(urlFullFilePath);
	// iSFileExist = checkFileExistance(new File(fileURL));
	// if(iSFileExist==true)
	// iSAnchorInFileExist = checkAnchorInFile(new File(fileURL), anchor);
	// if(iSFileExist==true && iSAnchorInFileExist==true){
	//
	// status = true;
	// }
	// return status;
	// }

	private String iSAnchorInOtherHTMLStatus(String htmlFile, String linkHref)
			throws IOException {

		boolean iSFileExist = false;
		boolean iSAnchorInFileExist = false;
		String status = "";
		String parentDiretory = getParentDirectory(htmlFile);
		String urlFullFilePath = getNewPath(parentDiretory, linkHref);
		// System.out.println(urlFilePath);
		String anchor = getAnchorFromURL(urlFullFilePath);
		String fileURL = getFileURL(urlFullFilePath);
		iSFileExist = checkFileExistance(new File(fileURL));
		if (iSFileExist == true)
			iSAnchorInFileExist = checkAnchorInFile(new File(fileURL), anchor);
		if (iSFileExist == true && iSAnchorInFileExist == true) {

			status = "Pass";
		} else {
			status = "Fail";
		}
		return status;
	}

	private String iSAnchorInOtherHTMLExits(String htmlFile, String linkHref)
			throws IOException {

		boolean iSFileExist = false;
		boolean iSAnchorInFileExist = false;

		String status = "HTML file does not exist";
		String parentDiretory = getParentDirectory(htmlFile);
		String urlFullFilePath = getNewPath(parentDiretory, linkHref);
		// System.out.println(urlFilePath);
		String anchor = getAnchorFromURL(urlFullFilePath);
		String fileURL = getFileURL(urlFullFilePath);
		iSFileExist = checkFileExistance(new File(fileURL));
		if (iSFileExist == true) {
			iSAnchorInFileExist = checkAnchorInFile(new File(fileURL), anchor);
			if (iSAnchorInFileExist == true) {
				status = "Anchor tag missing";
			}
		}

		return status;
	}

	private String getFileURL(String urlFullFilePath) {
		String[] temp = null;
		if (urlFullFilePath.contains("#")) {
			temp = urlFullFilePath.split("#");
		}
		return temp[0];
	}

	private String getAnchorFromURL(String urlFilePath) {
		String[] temp = null;
		if (urlFilePath.contains("#")) {
			temp = urlFilePath.split("#");
		}
		return temp[1];
	}

	private boolean checkAnchorInFile(File file, String anchor)
			throws IOException {

		boolean status = false;
		Document doc2 = Jsoup.parse(file, "UTF-8");
		Elements e2 = doc2.getElementsByAttributeValue("id", anchor);
		if (e2.size() >= 1) {
			status = true;
		} else {
			Elements e3 = doc2.getElementsByAttributeValue("name", anchor);
			if (e3.size() >= 1) {
				status = true;
			} else {
				status = false;
			}

		}
		return status;

	}

	private boolean checkFileExistance(File file) {
		return file.exists();
	}

	private String getNewPath(String parentDiretory, String linkHref) {
		if (linkHref.startsWith("../")) {
			linkHref = linkHref.replace("..", "");
		}
		String finalPath = linkHref.replace("/", "\\");
		return parentDiretory + finalPath.trim();
	}

	private boolean getIDStatus(String linkHref, String file, Document doc) {
		boolean status = false;
		Element el;
		if (linkHref.startsWith("#")) {
			// System.out.println(linkHref);
			linkHref = removeChar(linkHref, '#');
			// System.out.println(linkHref);
		}
		el = doc.getElementById(linkHref);

		if (el != null) {
			status = true;
		} else {
			status = false;
		}
		return status;
	}

	@AfterTest
	public static void printResults() throws IOException {

		Tag html = new Tag("html");
		Tag head = new Tag("head");

		// head.add(new Tag("link",
		// "rel=stylesheet type=text/css href=http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"));
		// head.add(new Tag("src",
		// "src=http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"));
		// head.add(new Tag("src",
		// "src=http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"));
		head.add(new Tag("link",
				"rel=stylesheet type=text/css href=./styles.css"));
		head.add(new Tag("script",
				"src=https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"));
		head.add(new Tag("script", "src=./customscript.js"));

		Tag title = new Tag("title");
		title.add("Skyscape Content Test Report");
		head.add(title);
		Tag body = new Tag("body");
		Tag main = new Tag("div", "align=center");
		body.add(main);
		Tag h2 = new Tag("h2");
		main.add(h2);
		h2.add("Consolidated report for all links");
		Tag p = new Tag("h3");
		p.add("Results for all href links");
		main.add(p);
		Tag p1 = new Tag("h3");
		p1.add("<button id='btn1' type='button' class='btn btn-success'><span id='show'>Show Fail</span></button>");
		main.add(p1);
		Tag table = new Tag("table",
				"id=result border=1 cellpadding=3 cellspacing=0 width=500");
		Tag theadTag = new Tag("thead");
		Tag header = new Tag("tr");
		for (int j = 0; j < thead.size(); j++) {
			// Tag theadTitle = new Tag("th",
			// "class=header width=60 bgcolor=#996633 color=#ffffff");
			Tag theadTitle = new Tag("th", "class=header width=60");
			theadTitle.add(thead.get(j));
			header.add(theadTitle);
		}
		theadTag.add(header);
		table.add(theadTag);

		// fill table with empty cells for days
		for (int i = 0; i < results.size(); i++) {
			Tag tr = new Tag("tr", "align=center valign=center ");
			Attributes trAttrs = tr.getAttributes();
			for (int j = 0; j < 6; j++) {
				Tag cell = new Tag("td", "align=center vertical-align=middle");
				Attributes attrs = cell.getAttributes();
				if ((i % 2) == 0) {
					attrs.add(new Attribute("bgcolor", "#FFFFFF"));
					// attrs.add(new Attribute("bgcolor", "#a0e0e0"));
					// attrs.add(new Attribute("bgcolor", "#e8ebc5"));
				} else {
					// attrs.add(new Attribute("bgcolor", "#ccf0f0"));
					// attrs.add(new Attribute("bgcolor", "#e7deab"));
					attrs.add(new Attribute("bgcolor", "#ECF0F1"));

				}
				cell.add("&nbsp;");
				cell.add("<br>\n");
				Tag fonttag = new Tag("font", "size=+1");
				fonttag.add(getAndAppendResult(results.get(i), j));
				fonttag.add("&nbsp;");
				cell.add(fonttag);
				cell.add("<br>\n");
				cell.add("&nbsp;");

				if (results.get(i).contains("Fail")) {
					trAttrs.add(new Attribute("class", "fail"));
					trAttrs.add(new Attribute("style=color:#FF6961"));
				} else if (results.get(i).contains("Pass")) {
					trAttrs.add(new Attribute("class", "pass"));
				} else {
					trAttrs.add(new Attribute("class", "noresult"));
				}

				tr.add(cell);
				System.out.println("Test");
			}

			table.add(tr);
		}

		main.add(table);
		html.add(head);
		html.add(body);
		System.out.println(html);

		FileWriter writer = new FileWriter(htmlFileName);
		writer.write(html.toString());
		writer.close();
		File htmlFile = new File(htmlFileName);
		Desktop.getDesktop().browse(htmlFile.toURI());

	}

	private static Object getAndAppendResult(String result, int j) {
		String data;
		String[] split = result.split(",");
		String key = split[j];
		String[] value = key.split("::");
		if (value.length > 1) {
			data = value[1].trim();
		} else {
			data = "";
		}

		return data;
	}

	private static String removeChar(String s, char c) {
		StringBuffer buf = new StringBuffer(s.length());
		buf.setLength(s.length());
		int current = 0;
		for (int i = 0; i < s.length(); i++) {
			char cur = s.charAt(i);
			if (cur != c)
				buf.setCharAt(current++, cur);
		}
		return buf.toString().trim();
	}

	private String getFilePath(String getFile) {
		String temp[] = getFile.split(",");
		return temp[0].trim();
	}

	private String getFileName(String getFile) {
		String temp[] = getFile.split(",");
		return temp[1].trim();
	}

	private String getTestStatus(boolean value) {

		String status;
		if (value == true) {
			status = "Pass";
		} else {
			status = "Fail";
		}
		return status;

	}

}
