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
import java.net.URISyntaxException;
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
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebElement;
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

public class ImageChecker {

	public static List<String> htmlFileList = null;
	public static List<String> results = null;
	public static List<String> noprotocol = null;
	public static List<String> thead = new ArrayList<String>();
	public static List<String> theadStatus = new ArrayList<String>();
	static String file = "File Name";
	static String topic_name = "Topic";
	static String anchor = "Anchor";
	static String anchor_type = "Anchor Type";
	static String protocol_status = "Protocol\nStatus";
	static String result = "Result";
	static String comment = "Comment";
	static String pageTitle = "Title";
	static String htmlFileName = "report/index.html";
	static String fileSource = "Source";
	// static String xlFilePathdownload = "downloadProduct.xls";
	static String textFilePath = "inputUrl.txt";
	static String textFileFullPath = "input" + "/" + textFilePath;
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
	static String mediaUrl;
	static String allResults = "All Results";
	static String extLink = "External link";
	static String anchoronsamehtml = "Anchor On Same HTML";
	static String pointsOtherHTML = "Cross referance HTML";
	static String pointsOtherHTMLAnchor = "Points Other HTML Anchor";
	static String image = "Image";
	static String artinart = "artinart";
	static String popup = "Pop-up";
	static String nottested = "Not Tested";
	static String total = "Total Fail";
	static HashMap<String, Element> uniqueDate;
	// static String mediaUrl =
	// "http://download.skyscape.com/download/tabers22/1.0/";

	static String sourceReport = "report";
	static String destinationReport = "logs";
	static ReusableCode func = new ReusableCode();

	@DataProvider(name = "getDownloadParameteres", parallel = false)
	public Object[][] data1() throws Exception {
		Object[][] retObjArrlogin = func
				.getTableArrayFromText(textFileFullPath);
		return (retObjArrlogin);
	}

	// @Test(description = "Download zip content for", dataProvider =
	// "getDownloadParameteres", priority=0)
	public void downloadRequiredZip(String filePath, String url)
			throws Exception {

		Assert.assertTrue(!url.isEmpty(),
				"Please provide url in the text file to download zip");

		String productName;

		String extension = FilenameUtils.getExtension(url).trim().toLowerCase();
		Assert.assertTrue(extension.contains("zip"), "Please provide zip url");
		String baseUrl = FilenameUtils.getPathNoEndSeparator(url);
		String zipName = FilenameUtils.getName(url).trim();

		String[] temp = zipName.split("\\.");
		productName = temp[0];

		product = productName;
		func.deleteExistingData(fileSource);
		String productManifestName = productName + ".manifest";
		String productManifestUrl = func.formNewURL(baseUrl,
				productManifestName);
		String manifestFilePath = fileSource + "/" + productManifestName;
		productManifestfile = new File(manifestFilePath);

		boolean checkProductManifestExistance = func.exists(productManifestUrl);
		Assert.assertTrue(checkProductManifestExistance, productManifestName
				+ " file is not available on server");

		func.downloadTitleManifest(productManifestUrl, productManifestfile,
				manifestFilePath, productManifestName);

		Object[] titleManifestData = func
				.readInformationFromManifest(manifestFilePath);

		String currentVersion = (String) titleManifestData[1];
		String sampleZipName = (String) titleManifestData[2];
		mediaUrl = (String) titleManifestData[3];

		String zipPath = fileSource + "/" + zipName;
		zipFilePath = new File(zipPath);
		String zipUrl = url;

		Assert.assertTrue(func.exists(zipUrl), zipName
				+ " file is not available on server");

		if (!zipFilePath.exists()) {

			func.downloadZip(zipUrl, zipFilePath, zipPath, zipName);
			func.unzip(zipPath);

		} else {
			System.out.println(zipName + " already downloaded");
		}
	}

	@Test(description = "Search all HTML files to test", priority = 1)
	public void getHTMLFiles() throws IOException {
		htmlFileList = new ArrayList<String>();
		results = new ArrayList<String>();
		uniqueDate = new HashMap<String, Element>();
		noprotocol = new ArrayList<String>();
		thead.add(file);
		thead.add(pageTitle);
		thead.add(topic_name);
		thead.add(anchor);
		thead.add(anchor_type);
		thead.add(protocol_status);
		thead.add(result);
		thead.add(comment);

		theadStatus.add(allResults);
		theadStatus.add(extLink);
		theadStatus.add(anchoronsamehtml);
		theadStatus.add(pointsOtherHTML);
		theadStatus.add(pointsOtherHTMLAnchor);
		theadStatus.add(image);
		theadStatus.add(artinart);
		theadStatus.add(popup);
		theadStatus.add(nottested);
		theadStatus.add(total);

		File dir = new File(fileSource);
		String[] extensions = new String[] { "html", "htm" };
		System.out.println("Getting all .html in " + dir.getCanonicalPath()
				+ " including those in subdirectories");
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions,
				true);
		for (File file : files) {
			// System.out.println(file.getCanonicalPath().trim() + ":::"
			// + file.getName());
			// htmlFileList.add(file.getCanonicalPath().trim() + ":::"
			// + file.getName());
			htmlFileList.add(file.getCanonicalPath().trim());

		}
		Collections.sort(htmlFileList);

		System.out.println("Total HTML files " + htmlFileList.size());
		System.out
				.println("----------------------------------------------------------------------------------");
	}

	@Test(description = "Check all href links", priority = 2)
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
			Element titleLink = doc.select("title").first();
			String title = func.getTitle(titleLink.text());
			Elements links = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
			Elements htmllinks = doc.select("a[href]");
			links.addAll(htmllinks);
			// System.out.println("Element :"+links.size());
			if (links.size() != 0) {
				for (Element link : links) {
					String linkHref = null;
					if (link.hasAttr("href")) {
						linkHref = link.attr("href");
					} else if (link.hasAttr("src")) {
						linkHref = link.attr("src");
					}

					boolean iSLinkDuplicate = uniqueDate.containsKey(linkHref);
					if (!linkHref.isEmpty() && iSLinkDuplicate == false) {
						// System.out.println(htmlFile+"::"+":"+linkHref);
						boolean noProtocol = false;
						System.out
								.println(htmlSplittedPath + " :: " + linkHref);

						uniqueDate.put(linkHref, link);
						String topic = func.getTopicName(link);
					if(!linkHref.contains("\\")){
						if (linkHref.startsWith("http")
								|| linkHref.startsWith("https")
								|| linkHref.startsWith("www")) {
							String proStatus = "-";
							if (linkHref.startsWith("www")) {
								linkHref = linkHref
										.replace("www", "http://www");
								noProtocol = true;
								proStatus = "Protocol issue";
							}

							int responseCode = func
									.getResponseCodeInt(linkHref);
							results.add(file + "::" + htmlSplittedPath + ":::"
									+ pageTitle + "::" + title + ":::"
									+ topic_name + "::" + "-" + ":::" + anchor
									+ "::" + linkHref + ":::" + anchor_type
									+ "::" + extLink + ":::"
									+ "protocol_status" + "::" + proStatus
									+ ":::" + "result" + "::"
									+ func.getExternalURLStatus(responseCode)
									+ ":::" + "error" + "::"
									+ func.getResponseCodeIfFail(responseCode));
							// Pushing status as file, anchor, anchor_type,
							// protocol_status, result, error
							// if (noProtocol == true)
							// noprotocol.add(file+"::"+""+htmlSplittedPath +
							// ":::" +
							// anchor+"::"+""+linkHref +":::"+
							// anchor_type+"::"+"External"
							// + ":::" + "protocol_status::"+noProtocol + ":::"
							// +
							// "result"+"::"+getExternalURLStatus(responseCode)
							// +
							// ":::" + "error"+"::"+responseCode);
							// Pushing status as html file, anchor, anchor_type,
							// protocol_status, result, error
						} else if (linkHref.startsWith("#")) {

							boolean isFileExist = func
									.checkFileExistance(input);
							boolean isAnchorExist = func.getIDStatus(linkHref,
									htmlFile, doc);

							Object[] status = func.getTestStatus(isFileExist,
									isAnchorExist);
							String result = (String) status[0];
							String error = (String) status[1];

							results.add(file + "::" + "" + htmlSplittedPath
									+ ":::" + pageTitle + "::" + title + ":::"
									+ topic_name + "::" + topic + ":::"
									+ anchor + "::" + linkHref + ":::"
									+ anchor_type + "::" + anchoronsamehtml
									+ ":::" + "protocol_status" + "::" + "NA"
									+ ":::" + "result" + "::" + result + ":::"
									+ "error" + "::" + error);
							// Pushing status as html file, anchor, anchor_type,
							// protocol_status, result, error

						} else if ((linkHref.startsWith("../"))
								&& ((linkHref.endsWith(".htm")))
								|| (linkHref.endsWith(".html"))
								&& (!linkHref.contains("blocked.html"))) {

							Object[] status = func.iSHTMLFileExits(htmlFile,
									linkHref);
							String result = (String) status[0];
							String error = (String) status[1];

							results.add(file + "::" + "" + htmlSplittedPath
									+ ":::" + pageTitle + "::" + title + ":::"
									+ topic_name + "::" + topic + ":::"
									+ anchor + "::" + linkHref + ":::"
									+ anchor_type + "::" + pointsOtherHTML
									+ ":::" + "protocol_status" + "::" + "NA"
									+ ":::" + "result" + "::" + result + ":::"
									+ "error" + "::" + error);

							// Pushing status as html file, anchor, anchor_type,
							// protocol_status, result, error

						} else if ((linkHref.startsWith("../"))) {

							int count = func.numberOfOccurrences(linkHref,
									"../");
							if (count == 1) {
								boolean isFileExist = func
										.checkFileExistance(input);
								boolean isAnchorExist = func
										.iSAnchorInOtherHTMLFileExits(htmlFile,
												linkHref);
								Object[] status = func.getTestStatus(
										isFileExist, isAnchorExist);
								String result = (String) status[0];
								String error = (String) status[1];
								results.add(file + "::" + htmlSplittedPath
										+ ":::" + pageTitle + "::" + title
										+ ":::" + topic_name + "::" + topic
										+ ":::" + anchor + "::" + linkHref
										+ ":::" + anchor_type + "::"
										+ pointsOtherHTMLAnchor + ":::"
										+ "protocol_status" + "::" + "NA"
										+ ":::" + "result" + "::" + result
										+ ":::" + "error" + "::" + error);
							} else if (count == 2) {

								boolean imageStatus = func.iSImageExits(
										htmlFile, linkHref);
								Object[] status = func
										.getTestStatus(imageStatus);
								String result = (String) status[0];
								String error = (String) status[1];
								results.add(file + "::" + htmlSplittedPath
										+ ":::" + pageTitle + "::" + title
										+ ":::" + topic_name + "::" + topic
										+ ":::" + anchor + "::" + linkHref
										+ ":::" + anchor_type + "::" + image
										+ ":::" + "protocol_status" + "::"
										+ "NA" + ":::" + "result" + "::"
										+ result + ":::" + "error" + "::"
										+ error);
							}

							// Pushing status as html file, anchor, anchor_type,
							// protocol_status, result, error

						} else if (linkHref.trim().startsWith(
								"artinart:kaud:url")) {

							String mediaLink = func
									.formLink(linkHref, mediaUrl);
							int responseCode = func
									.getResponseCodeInt(mediaLink);
							results.add(file + "::" + htmlSplittedPath + ":::"
									+ pageTitle + "::" + title + ":::"
									+ topic_name + "::" + topic + ":::"
									+ anchor + "::" + linkHref + ":::"
									+ anchor_type + "::" + artinart + ":::"
									+ "protocol_status" + "::" + "NA" + ":::"
									+ "result" + "::"
									+ func.getExternalURLStatus(responseCode)
									+ ":::" + "error" + "::"
									+ func.getResponseCodeIfFail(responseCode));

						} else if (linkHref.trim().startsWith("popup:")) {
							int count = 0;
							boolean isFileExist = func
									.checkFileExistance(input);
							String anchorId = func
									.getProperPopupAnchor(linkHref);
							count = func.numberOfOccurrences(linkHref, "../");
							if (count == 0) {
								boolean iSAnchorExist = func.getIDStatus(
										anchorId, htmlFile, doc);
								Object[] status = func.getTestStatus(
										isFileExist, iSAnchorExist);
								String result = (String) status[0];
								String error = (String) status[1];
								results.add(file + "::" + htmlSplittedPath
										+ ":::" + pageTitle + "::" + title
										+ ":::" + topic_name + "::" + topic
										+ ":::" + anchor + "::" + linkHref
										+ ":::" + anchor_type + "::" + popup
										+ ":::" + "protocol_status" + "::"
										+ "NA" + ":::" + result + "::" + result
										+ ":::" + "error" + "::" + error);
							} else {
								String result;
								String error;
								if (!((anchorId.endsWith(".htm")) || (anchorId
										.endsWith(".html")))) {
									boolean isAnchorExist = func
											.iSAnchorInOtherHTMLFileExits(
													htmlFile, anchorId);
									Object[] status = func.getTestStatus(
											isFileExist, isAnchorExist);
									result = (String) status[0];
									error = (String) status[1];
								} else {

									Object[] status = func.iSHTMLFileExits(
											htmlFile, anchorId);
									result = (String) status[0];
									error = (String) status[1];
								}
								results.add(file + "::" + htmlSplittedPath
										+ ":::" + pageTitle + "::" + title
										+ ":::" + topic_name + "::" + topic
										+ ":::" + anchor + "::" + linkHref
										+ ":::" + anchor_type + "::" + popup
										+ ":::" + "protocol_status" + "::"
										+ "NA" + ":::" + "result" + "::"
										+ result + ":::" + "error" + "::"
										+ error);

							}

						} else {
							results.add(file + "::" + htmlSplittedPath + ":::"
									+ pageTitle + "::" + title + ":::"
									+ topic_name + "::" + topic + ":::"
									+ anchor + "::" + linkHref + ":::"
									+ anchor_type + "::" + nottested + ":::"
									+ "protocol_status" + "::" + "NA" + ":::"
									+ "result" + "::" + "-" + ":::" + "error"
									+ "::" + "");
							// Pushing status as html file, anchor, anchor_type,
							// protocol_status, result, error

						}
					}else{
						results.add(file + "::" + htmlSplittedPath + ":::"
								+ pageTitle + "::" + title + ":::"
								+ topic_name + "::" + topic + ":::"
								+ anchor + "::" + linkHref + ":::"
								+ anchor_type + "::" + "Path Issue(Backslash)" + ":::"
								+ "protocol_status" + "::" + "NA" + ":::"
								+ "result" + "::" + "Fail" + ":::" + "error"
								+ "::" + "");
						
					}
					}
					// else {
					// results.add(file + "::" + "" + htmlSplittedPath + ":::"
					// + pageTitle + "::" + title + ":::"
					// + topic_name + "::" + "" + ":::" + anchor + "::" + ""
					// + linkHref + ":::" + anchor_type + "::" + "No href"
					// + ":::" + "protocol_status" + "::" + "NA" + ":::"
					// + "result" + "::" + "NA" + ":::" + "error" + "::"
					// + "Not tested");
					// }
				}
			}
			// else{
			// results.add(file + "::" + "" + htmlSplittedPath + ":::"
			// + pageTitle + "::" + title + ":::"
			// + topic_name + "::" + "" + ":::" + anchor + "::" + ""
			// + "" + ":::" + anchor_type + "::" + "No href"
			// + ":::" + "protocol_status" + "::" + "NA" + ":::"
			// + "result" + "::" + "NA" + ":::" + "error" + "::"
			// + "Not tested");
			// }

		}

	}

	@SuppressWarnings("unchecked")
	@AfterTest
	public static void printResults() throws IOException, URISyntaxException {

		Tag html = new Tag("html");
		Tag head = new Tag("head");
		head.add(new Tag("script",
				"src=https://code.jquery.com/jquery-1.11.1.min.js"));

		head.add(new Tag("script",
				"src=https://cdn.datatables.net/1.10.4/js/jquery.dataTables.min.js"));

		head.add(new Tag("script", "src=./js/customscript.js"));
		head.add(new Tag(
				"script",
				"src=https://cdn.datatables.net/plug-ins/9dcbecd42ad/integration/jqueryui/dataTables.jqueryui.js"));
		head.add(new Tag("script",
				"src=http://cdnjs.cloudflare.com/ajax/libs/modernizr/2.8.2/modernizr.js"));

		head.add(new Tag(
				"link",
				"rel=stylesheet type=text/css href=https://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"));
		head.add(new Tag("link",
				"rel=stylesheet type=text/css href=./js/styles.css"));
		head.add(new Tag(
				"link",
				"rel=stylesheet type=text/css href=https://cdn.datatables.net/plug-ins/9dcbecd42ad/integration/jqueryui/dataTables.jqueryui.css"));

		Tag title = new Tag("title");
		title.add("Skyscape Content Test Report");
		head.add(title);
		Tag body = new Tag("body");
		Tag loader = new Tag("div", "class=se-pre-con");
		body.add(loader);
		Tag main = new Tag("div", "align=center");
		body.add(main);
		Tag headStatus = new Tag("div", "id=headStatus");
		// Tag headname = new Tag("div", "id=headname");
		// Tag h2 = new Tag("h2");
		// h2.add("Consolidated report of " + product);
		// headname.add(h2);
		// Tag p = new Tag("h3");
		// p.add("Results for all href links");
		// headname.add(p);
		// headStatus.add(headname);
		Tag table0 = new Tag("table",
				"id=stats border=1 cellpadding=3 cellspacing=0");
		Tag theadTag0 = new Tag("thead");
		Tag header0 = new Tag("tr");
		for (int j = 0; j < theadStatus.size(); j++) {
			String cTitle = theadStatus.get(j).trim();
			String colClass = "class=head" + j;
			Tag theadTitle = new Tag("th", colClass);
			String value = theadStatus.get(j);		
			theadTitle.add(value);
			header0.add(theadTitle);
		}
		theadTag0.add(header0);
		table0.add(theadTag0);
		Tag tBodyTag0 = new Tag("tbody");
		Tag tr0 = new Tag("tr", "align=center valign=center ");
		for (int j = 0; j < theadStatus.size(); j++) {
			Tag cell0 = new Tag("td", "align=center vertical-align=middle");
			Attributes attrs0 = cell0.getAttributes();
			attrs0.add(new Attribute("id", "d" + j));
			attrs0.add(new Attribute("class", "count"));
			attrs0.add(new Attribute("theadvalue", theadStatus.get(j)));
			cell0.add("&nbsp;");
			tr0.add(cell0);
		}
		tBodyTag0.add(tr0);
		table0.add(tBodyTag0);
		main.add(table0);
		// //////
		Tag statusdiv = new Tag("div", "id=onelineright");
		headStatus.add(statusdiv);
		main.add(headStatus);
		// Tag p1 = new Tag("div", "id=statusbutton");
		// p1.add("<button id='btn1' type='button'><span id='show'>Show Fail</span></button>");
		// main.add(p1);
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
		Tag tBodyTag = new Tag("tbody");
		// fill table with empty cells for days
		Tag tr;
		for (int i = 0; i < results.size(); i++) {
			String result = results.get(i);
			tr = new Tag("tr", "align=center valign=center ");
			Attributes trAttrs = tr.getAttributes();
			String statusclass = "";
			if (result.contains("Fail")) {
				statusclass = "statusfail";
				// trAttrs.add(new Attribute("class", "statusfail"));
			} else if (result.contains("Pass")) {
				statusclass = "statuspass";
				// trAttrs.add(new Attribute("class", "statuspass"));
			}
			if (result.contains(allResults)) {
				trAttrs.add(new Attribute("class", statusclass + " v0 "
						+ statusclass + "v0"));
			} else if (result.contains(extLink)) {
				trAttrs.add(new Attribute("class", statusclass + " v1 "
						+ statusclass + "v1"));
			} else if (result.contains(anchoronsamehtml)) {
				trAttrs.add(new Attribute("class", statusclass + " v2 "
						+ statusclass + "v2"));
			} else if (result.contains(pointsOtherHTML)) {
				trAttrs.add(new Attribute("class", statusclass + " v3 "
						+ statusclass + "v3"));
			} else if (result.contains(pointsOtherHTMLAnchor)) {
				trAttrs.add(new Attribute("class", statusclass + " v4 "
						+ statusclass + "v4"));
			} else if (result.contains(image)) {
				trAttrs.add(new Attribute("class", statusclass + " v5 "
						+ statusclass + "v5"));
			} else if (result.contains(artinart)) {
				trAttrs.add(new Attribute("class", statusclass + " v6 "
						+ statusclass + "v6"));
			} else if (result.contains(popup)) {
				trAttrs.add(new Attribute("class", statusclass + " v7 "
						+ statusclass + "v7"));
			} else if (result.contains(nottested)) {
				trAttrs.add(new Attribute("class", statusclass + " v8 "
						+ statusclass + "v8"));
			} else if (result.contains(total)) {
				trAttrs.add(new Attribute("class", statusclass + " v9 "
						+ statusclass + "v9"));
			}

			for (int j = 0; j < thead.size(); j++) {
				Tag cell = new Tag("td", "align=center vertical-align=middle");
				Attributes attrs = cell.getAttributes();
				if ((i % 2) == 0) {
					attrs.add(new Attribute("bgcolor", "#FFFFFF"));

				} else {

					attrs.add(new Attribute("bgcolor", "#ECF0F1"));

				}
				cell.add("&nbsp;");
				// cell.add("<br>\n");
				Tag fonttag = new Tag("font", "size=+0.5");
				fonttag.add(func.getAndAppendResult(results.get(i), j));
				// fonttag.add("&nbsp;");
				cell.add(fonttag);
				// cell.add("<br>\n");
				cell.add("&nbsp;");

				tr.add(cell);

			}

			tBodyTag.add(tr);

		}
		table.add(tBodyTag);
		main.add(table);

		html.add(head);
		html.add(body);
		// System.out.println(html);

		FileWriter writer = new FileWriter(htmlFileName);
		writer.write(html.toString());
		writer.close();

		// Desktop.getDesktop().browse(htmlFile.toURI());
		func.openInBrowser(htmlFileName);
		func.copyReportsToLogs(sourceReport, destinationReport);

	}

}
