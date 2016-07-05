package validator;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.print.Doc;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.io.Zip;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import util.Attribute;
import util.Attributes;
import util.ReusableCode;
import util.Tag;

public class FindCertainExtension {

	public static List<String> htmlFileList = null;
	public static List<String> results = null;
	public static List<String> noprotocol = null;
	public static List<String> thead = new ArrayList<String>();
	static String file = "File Name";
	static String topic_name = "Topic";
	static String anchor = "Anchor";
	static String anchor_type = "Anchor Type";
	static String protocol_status = "Protocol\nStatus";
	static String result = "Result";
	static String comment = "Comment";
	static String htmlFileName = "report/Result.html";
	static String fileSource = "Source";
	static String xlFilePathdownload = "downloadProduct.xls";
	static String product = "";
	static File productManifestfile;

	static File zipFilePath;
	static File sampleProductManifestfile;
	static String sampleProductManifestFilePath;
	static String sampleProductManifestURL;

	static File fullProductZipFilePath;
	static File fullProductManifestfile;
	static String fullProductZipUrl;
	static String fullProductZipPath;
	// static String mediaUrl;
	static String mediaUrl = "http://download.skyscape.com/download/tabers22/1.0/";

	static String sourceReport = "report";
	static String destinationReport = "logs";
	static ReusableCode func = new ReusableCode();

	// @Test(description = "Download zip content for", dataProvider =
	// "getDownloadParameteres", priority=0)
	// public void downloadRequiredZip(String TestDataId, String productName,
	// String zipType,String baseURL,
	// String UUID) throws Exception {
	// product = productName;
	// ReusableCode.deleteExistingData(fileSource);
	// String productManifestName = productName+".manifest";
	// String productManifestUrl = func.formURL(baseURL, UUID,
	// productManifestName);
	// String manifestFilePath = fileSource + "/" + productManifestName;
	// productManifestfile = new File(manifestFilePath);
	//
	// boolean checkProductManifestExistance = func.exists(productManifestUrl);
	// Assert.assertTrue(checkProductManifestExistance,
	// productManifestName+" file is not available on server");
	//
	// func.downloadTitleManifest(productManifestUrl, productManifestfile,
	// manifestFilePath, productManifestName);
	//
	// Object[] titleManifestData =
	// func.readInformationFromManifest(manifestFilePath);
	//
	// String currentVersion=(String) titleManifestData[1];
	// String sampleZipName=(String) titleManifestData[2];
	// mediaUrl = (String) titleManifestData[3];
	// String zip;
	// if(zipType.equalsIgnoreCase(zipType)){
	// zip = productName + "." + sampleZipName + ".zip";
	// }else{
	// zip = productName + "." + currentVersion + ".zip";
	// }
	//
	// String zipPath = fileSource + "/" + zip;
	// zipFilePath = new File(zipPath);
	// String zipUrl = baseURL + "/" + UUID + "/" + zip;
	//
	// Assert.assertTrue(func.exists(zipUrl),
	// zip+" file is not available on server");
	//
	// if (!zipFilePath.exists()) {
	//
	// func.downloadZip(zipUrl,zipFilePath, zipPath, zip);
	// func.unzip(zipPath);
	//
	// } else {
	// System.out.println(zip + " already downloaded");
	// }
	// }

	@Test(description = "Search all HTML files to test", priority = 1)
	public void getHTMLFiles() throws IOException {
		htmlFileList = new ArrayList<String>();
		results = new ArrayList<String>();
		noprotocol = new ArrayList<String>();
		thead.add(file);
		thead.add(topic_name);
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
			// htmlFileList.add(file.getCanonicalPath().trim() + ","
			// + file.getName());
			htmlFileList.add(file.getCanonicalPath().trim());

		}
		Collections.sort(htmlFileList);

		System.out.println("Total HTML files " + htmlFileList.size());
		System.out
				.println("----------------------------------------------------------------------------------");
	}

	@Test(description = "Chec all href links", priority = 2)
	public void getAndVerifyLinksFromHTML() throws InterruptedException,
			IOException {

		System.out.println("Testing " + htmlFileList.size()
				+ " html files one by one");

		for (int i = 0; i < htmlFileList.size(); i++) {
			Document doc = null;
			String htmlFile = htmlFileList.get(i).trim();
			// String htmlFile = getFilePath(File);
			// getFileName(File);
			File input = new File(htmlFile);
			String htmlSplittedPath = func.getRequiredFilePath(input,
					fileSource);
			// //
			doc = Jsoup.parse(input, "UTF-8");
			Elements links = doc.select("a");
			// System.out.println("Element :"+links.size());
			for (Element link : links) {
				String linkHref = link.attr("href");
				String topic = link.text();
				if (!linkHref.isEmpty()) {
					// System.out.println(htmlFile+"::"+":"+linkHref);
					boolean noProtocol = false;
					if (linkHref.startsWith("http")
							|| linkHref.startsWith("https")
							|| linkHref.startsWith("www")) {
						String proStatus = "";
						if (linkHref.startsWith("www")) {
							linkHref = linkHref.replace("www", "http://www");
							noProtocol = true;
							proStatus = "Protocol issue";
						}

						int responseCode = func.getResponseCodeInt(linkHref);
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ topic_name + "::" + "" + "," + anchor + "::"
								+ "" + linkHref + "," + anchor_type + "::"
								+ "External link" + "," + "protocol_status::"
								+ proStatus + "," + "result" + "::"
								+ func.getExternalURLStatus(responseCode) + ","
								+ "error" + "::"
								+ func.getResponseCodeIfFail(responseCode));
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

						boolean isFileExist = func.checkFileExistance(input);
						boolean isAnchorExist = func.getIDStatus(linkHref,
								htmlFile, doc);

						Object[] status = func.getTestStatus(isFileExist,
								isAnchorExist);
						String result = (String) status[0];
						String error = (String) status[1];

						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ topic_name + "::" + topic + "," + anchor
								+ "::" + "" + linkHref + "," + anchor_type
								+ "::" + "Anchor On Same HTML" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::" + result + "," + "error"
								+ "::" + error);
						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					} else if ((linkHref.startsWith("../"))
							&& ((linkHref.endsWith(".htm")))
							|| (linkHref.endsWith(".html"))) {

						Object[] status = func.iSHTMLFileExits(htmlFile,
								linkHref);
						String result = (String) status[0];
						String error = (String) status[1];

						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ topic_name + "::" + topic + "," + anchor
								+ "::" + "" + linkHref + "," + anchor_type
								+ "::" + "Points Other HTML" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::" + result + "," + "error"
								+ "::" + error);

						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					} else if ((linkHref.startsWith("../"))) {

						boolean isFileExist = func.checkFileExistance(input);
						boolean isAnchorExist = func
								.iSAnchorInOtherHTMLFileExits(htmlFile,
										linkHref);
						Object[] status = func.getTestStatus(isFileExist,
								isAnchorExist);
						String result = (String) status[0];
						String error = (String) status[1];
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ topic_name + "::" + topic + "," + anchor
								+ "::" + "" + linkHref + "," + anchor_type
								+ "::" + "Points Other HTML Anchor" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::" + result + "," + "error"
								+ "::" + error);

						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					} else if (linkHref.trim().startsWith("artinart:kaud:url")) {

						String mediaLink = func.formLink(linkHref, mediaUrl);
						int responseCode = func.getResponseCodeInt(mediaLink);
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ topic_name + "::" + topic + "," + anchor
								+ "::" + "" + linkHref + "," + anchor_type
								+ "::" + "File on server" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::"
								+ func.getExternalURLStatus(responseCode) + ","
								+ "error" + "::"
								+ func.getResponseCodeIfFail(responseCode));

					} else if (linkHref.trim().startsWith("popup:")) {
						boolean isFileExist = func.checkFileExistance(input);
						String anchorId = func.getProperPopupAnchor(linkHref);
						boolean iSAnchorExist = func.getIDStatus(anchorId,
								htmlFile, doc);
						Object[] status = func.getTestStatus(isFileExist,
								iSAnchorExist);
						String result = (String) status[0];
						String error = (String) status[1];
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ topic_name + "::" + topic + "," + anchor
								+ "::" + "" + linkHref + "," + anchor_type
								+ "::" + "Pop-up" + "," + "protocol_status"
								+ "::" + "NA" + "," + result + "::" + "No"
								+ "," + "error" + "::" + error);

					} else {
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ topic_name + "::" + topic + "," + anchor
								+ "::" + "" + linkHref + "," + anchor_type
								+ "::" + "Not Tested" + "," + "protocol_status"
								+ "::" + "NA" + "," + "result" + "::" + ""
								+ "," + "error" + "::" + "");
						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					}

				} else {
					results.add(file + "::" + "" + htmlSplittedPath + ","
							+ topic_name + "::" + "" + "," + anchor + "::" + ""
							+ linkHref + "," + anchor_type + "::" + "No href"
							+ "," + "protocol_status" + "::" + "NA" + ","
							+ "result" + "::" + "NA" + "," + "error" + "::"
							+ "Not tested");
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
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
				"rel=stylesheet type=text/css href=./js/styles.css"));
		head.add(new Tag("script", "src=./js/jquery.min.js"));
		head.add(new Tag("script", "src=./js/customscript.js"));

		Tag title = new Tag("title");
		title.add("Skyscape Content Test Report");
		head.add(title);
		Tag body = new Tag("body");
		Tag main = new Tag("div", "align=center");
		body.add(main);
		Tag headStatus = new Tag("div", "id=headStatus");
		Tag headname = new Tag("div", "id=headname");
		Tag h2 = new Tag("h2");
		h2.add("Consolidated report of " + product);
		headname.add(h2);
		Tag p = new Tag("h3");
		p.add("Results for all href links");
		headname.add(p);
		headStatus.add(headname);
		Tag statusdiv = new Tag("div", "id=cellData");
		headStatus.add(statusdiv);
		main.add(headStatus);
		Tag p1 = new Tag("div", "id=statusbutton");
		p1.add("<button id='btn1' type='button'><span id='show'>Show Fail</span></button>");
		main.add(p1);
		Tag table = new Tag("table",
				"id=result border=1 cellpadding=3 cellspacing=0");
		Tag theadTag = new Tag("thead");
		Tag header = new Tag("tr");
		for (int j = 0; j < thead.size(); j++) {

			String cTitle = thead.get(j).trim();
			String colClass = "class=header" + j;
			Tag theadTitle = new Tag("th", colClass);
			theadTitle.add(thead.get(j));
			header.add(theadTitle);
		}
		theadTag.add(header);
		table.add(theadTag);

		// fill table with empty cells for days
		for (int i = 0; i < results.size(); i++) {
			Tag tr = new Tag("tr", "align=center valign=center ");
			Attributes trAttrs = tr.getAttributes();
			trAttrs.add(new Attribute("class", "status"));
			// String result = results.get(i);
			//
			// if (result.contains("Fail")) {
			// trAttrs.add(new Attribute("class", "fail"));
			// trAttrs.add(new Attribute("style=color:#FF6961"));
			// } else if (result.contains("Pass")) {
			// trAttrs.add(new Attribute("class", "pass"));
			// } else if(result.contains("NA")){
			// trAttrs.add(new Attribute("class", "notTested"));
			// }
			// else {
			// trAttrs.add(new Attribute("class", "noresult"));
			// }
			for (int j = 0; j < thead.size(); j++) {
				Tag cell = new Tag("td", "align=center vertical-align=middle");
				Attributes attrs = cell.getAttributes();
				if ((i % 2) == 0) {
					attrs.add(new Attribute("bgcolor", "#FFFFFF"));
					
				} else {
					
					attrs.add(new Attribute("bgcolor", "#ECF0F1"));

				}
				cell.add("&nbsp;");
				cell.add("<br>\n");
				Tag fonttag = new Tag("font", "size=+1");
				fonttag.add(func.getAndAppendResult(results.get(i), j));
				fonttag.add("&nbsp;");
				cell.add(fonttag);
				cell.add("<br>\n");
				cell.add("&nbsp;");

				tr.add(cell);

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
		func.copyReportsToLogs(sourceReport, destinationReport);

	}



	@DataProvider(name = "getDownloadParameteres", parallel = false)
	public Object[][] data1() throws Exception {
		Object[][] retObjArrlogin = func.getTableArray(xlFilePathdownload,
				"downloadParameters", "Start", "End");
		return (retObjArrlogin);
	}

}
