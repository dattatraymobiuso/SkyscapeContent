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
	static String file = "File_Name";
	static String anchor = "Anchor";
	static String anchor_type = "Anchor_Type";
	static String protocol_status = "Protocol_Status";
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
    static String  mediaUrl;
    
    static String sourceReport="report";
    static String destinationReport="logs";
    
	
    @Test(description = "Download zip content for", dataProvider = "getDownloadParameteres", priority=0)
	public void downloadRequiredZip(String TestDataId, String productName, String zipType,String baseURL,
			String UUID) throws Exception {
    	product = productName;
    	ReusableCode.deleteExistingData(fileSource);
		String productManifestName = productName+".manifest";
		String productManifestUrl = formURL(baseURL, UUID, productManifestName);
		String manifestFilePath = fileSource + "/" + productManifestName;
		productManifestfile = new File(manifestFilePath);
		
		boolean checkProductManifestExistance = exists(productManifestUrl);
		Assert.assertTrue(checkProductManifestExistance, productManifestName+" file is not available on server");
		
		downloadTitleManifest(productManifestUrl, productManifestfile, manifestFilePath, productManifestName);
		
		Object[] titleManifestData = readInformationFromManifest(manifestFilePath);
		
		String currentVersion=(String) titleManifestData[1];
		String sampleZipName=(String) titleManifestData[2];
		mediaUrl = (String) titleManifestData[3];		
		String zip;
		if(zipType.equalsIgnoreCase(zipType)){
		zip = productName + "." + sampleZipName + ".zip";
		}else{
		zip = productName + "." + currentVersion + ".zip";
		}
			
		String zipPath = fileSource + "/" + zip;
		zipFilePath = new File(zipPath);
		String zipUrl = baseURL + "/" + UUID + "/" + zip;
			
		Assert.assertTrue(exists(zipUrl), zip+" file is not available on server");
		
		if (!zipFilePath.exists()) {
			
		downloadZip(zipUrl,zipFilePath,  zipPath, zip);
		unzip(zipPath);
			
	} else {
			System.out.println(zip + " already downloaded");
		}
	}	
   
	@Test(description = "Search all HTML files to test",priority=1, dependsOnMethods={"downloadRequiredZip"})
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

	@Test(description="Checking all href links", priority=2, dependsOnMethods={"downloadRequiredZip", "getHTMLFiles"})
	public void getAndVerifyLinksFromHTML() throws InterruptedException,
			IOException {

		System.out.println("Testing " + htmlFileList.size()
				+ " html files one by one");

		for (int i = 0; i < htmlFileList.size(); i++) {
			Document doc = null;
			String File = htmlFileList.get(i).trim();
			String htmlFile = getFilePath(File);
			getFileName(File);
			File input = new File(htmlFile);
			String htmlSplittedPath = getRequiredFilePath(input, fileSource);

			doc = Jsoup.parse(input, "UTF-8");
			Elements links = doc.select("a");
			System.out.println("Element :"+links.size());
			for (Element link : links) {
				String linkHref = link.attr("href");
				if (!linkHref.isEmpty()) {
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
								+ "error" + "::" + getResponseCodeIfFail(responseCode));
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

						
						boolean isFileExist = checkFileExistance(input);
						boolean iSAnchorExist = getIDStatus(linkHref, htmlFile,
								doc);
						
						Object[] status = getTestStatus(isFileExist, iSAnchorExist);
						String result = (String) status[0];
						String error = (String) status[1];
						
						
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::"
								+ "Internal-Anchor On Same HTML" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::"
								+ result + "," + "error"
								+ "::" + error);
						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					} else if ((linkHref.startsWith("../"))
							&& ((linkHref.endsWith(".htm")))
							|| (linkHref.endsWith(".html"))) {
						
						Object[] status = iSHTMLFileExits(htmlFile, linkHref);
						String result = (String)status[0];
						String error = (String) status[1];
						
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::"
								+ "Internal-Link Points Other HTML" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::"
								+ result + ","
								+ "error" + "::" + error);

						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					} else if ((linkHref.startsWith("../"))) {
						
						boolean isFileExist = checkFileExistance(input);
						boolean isAnchorExist=iSAnchorInOtherHTMLFileExits(htmlFile,linkHref);
						Object[] status = getTestStatus(isFileExist, isAnchorExist);
						String result = (String) status[0];
						String error = (String) status[1];
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::"
								+ "Internal-Points Other HTML Anchor" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::"
								+ result
								+ "," + "error" + "::"
								+ error);

						// Pushing status as html file, anchor, anchor_type,
						// protocol_status, result, error

					} else if (linkHref.trim().startsWith("artinart:kaud:url")) {
						
						String mediaLink = formLink(linkHref, mediaUrl); 
						int responseCode = ReusableCode.getResponseCodeInt(mediaLink);
						results.add(file + "::" + "" + htmlSplittedPath + ","
								+ anchor + "::" + "" + linkHref + ","
								+ anchor_type + "::" + "File on server" + ","
								+ "protocol_status" + "::" + "NA" + ","
								+ "result" + "::" + getExternalURLStatus(responseCode) + "," + "error" + "::"
								+ getResponseCodeIfFail(responseCode));

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

				}else{
				results.add(file + "::" + "" + htmlSplittedPath + ","
						+ anchor + "::" + "" + linkHref + ","
						+ anchor_type + "::"
						+ "No href" + ","
						+ "protocol_status" + "::" + "NA" + ","
						+ "result" + "::"
						+ "NA" + ","
						+ "error" + "::" + "Not tested");
				}
				}
		}

	}

//	private String getError(boolean isAnchorExist) {
//		String status="";
//			if(isAnchorExist==false){
//			status="Anchor not found";
//			}
//		return status;
//	}

	private String formLink(String linkHref, String url) {
		String part2 = null;
		String[] temp = linkHref.split("=");
		if(temp.length>1){
		part2 = 	temp[1];
			
		}
		return url+"/"+part2;
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

	
	private String getResponseCodeIfFail(int responseCode) {
		String resCode = Integer.toString(responseCode);
		String status;
		if (resCode.equals("200")) {
			status = "200";
		} else if(resCode.equals("0")){
			status = "Malformed URL";
		}else {
			status = resCode;
		}
		return status;
	}

	
	private String getParentDirectory(String htmlFile) {
		File f1 = new File(htmlFile);
		String s1 = f1.getParent();
		File f2 = new File(s1);
		return f2.getParent();
	}

//	private boolean iSOtherHTMLFileExits(String htmlFile, String linkHref) {
//		boolean iSFileExist = false;
//		String parentDiretory = getParentDirectory(htmlFile);
//		String urlFilePath = getNewPath(parentDiretory, linkHref);
//		iSFileExist = checkFileExistance(new File(urlFilePath));
//		return iSFileExist;
//	}

	private Object[] iSHTMLFileExits(String htmlFile, String linkHref) {
		boolean iSFileExist = false;
		String status="";
		String error="";
		String parentDiretory = getParentDirectory(htmlFile);
		String urlFilePath = getNewPath(parentDiretory, linkHref);
		iSFileExist = checkFileExistance(new File(urlFilePath));
		if (iSFileExist == true) {
			status = "Pass";
		
		} else {
			status = "Fail";
			error= "HTML file does not exist";
		}
		Object[] obj = new Object[]{status, error};
		return obj;
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

	 private boolean iSAnchorInOtherHTMLFileExits(String htmlFile,
	 String linkHref) throws IOException {
	
	 boolean iSFileExist = false;
	 boolean iSAnchorInFileExist = false;
	 boolean status = false;
	 String parentDiretory = getParentDirectory(htmlFile);
	 String urlFullFilePath = getNewPath(parentDiretory, linkHref);
	 // System.out.println(urlFilePath);
	 String anchor = getAnchorFromURL(urlFullFilePath);
	 String fileURL = getFileURL(urlFullFilePath);
	 iSFileExist = checkFileExistance(new File(fileURL));
	 if(iSFileExist==true)
	 iSAnchorInFileExist = checkAnchorInFile(new File(fileURL), anchor);
	 if(iSFileExist==true && iSAnchorInFileExist==true){
	
	 status = true;
	 }
	 return status;
	 }

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

		String status = "";
		String parentDiretory = getParentDirectory(htmlFile);
		String urlFullFilePath = getNewPath(parentDiretory, linkHref);
		// System.out.println(urlFilePath);
		String anchor = getAnchorFromURL(urlFullFilePath);
		String fileURL = getFileURL(urlFullFilePath);
		iSFileExist = checkFileExistance(new File(fileURL));
		if (iSFileExist == true) {
			iSAnchorInFileExist = checkAnchorInFile(new File(fileURL), anchor);
			if (iSAnchorInFileExist == false) {
				status = "Anchor tag missing";
			}
		}else{
			status = "HTML file does not exist";
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
		el = doc.getElementById(linkHref);

		if (el != null) {
			status = true;
		} else {
			status = false;
		}
		return status;
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
		head.add(new Tag("script",
				"src=https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"));
		head.add(new Tag("script", "src=./js/customscript.js"));

		Tag title = new Tag("title");
		title.add("Skyscape Content Test Report");
		head.add(title);
		Tag body = new Tag("body");
		Tag main = new Tag("div", "align=center");
		body.add(main);
		Tag h2 = new Tag("h2");
		main.add(h2);
		h2.add("Consolidated report of "+product);
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
				
				String result = results.get(i);
				
				if (result.contains("Fail")) {
					trAttrs.add(new Attribute("class", "fail"));
					trAttrs.add(new Attribute("style=color:#FF6961"));
				} else if (result.contains("Pass")) {
					trAttrs.add(new Attribute("class", "pass"));
				} else if(result.contains("NA")){
					trAttrs.add(new Attribute("class", "notTested"));
				}
				else {
					trAttrs.add(new Attribute("class", "noresult"));
				}

				tr.add(cell);
				
			}

			table.add(tr);
		}
		
		Tag table2 = new Tag("table",
				"id=countTable border=1 cellpadding=3 cellspacing=0 width=500");
		Tag tr = new Tag("tr", "align=center valign=center ");
		for(int i =0; i<4; i++){
		Tag cell1 = new Tag("td", "align=center vertical-align=middle");
		Attributes attrs1 = cell1.getAttributes();
		attrs1.add("15");
		}
		
		
		main.add(table2);
		main.add(table);
		
		html.add(head);
		html.add(body);
		System.out.println(html);

		FileWriter writer = new FileWriter(htmlFileName);
		writer.write(html.toString());
		writer.close();
		File htmlFile = new File(htmlFileName);
		Desktop.getDesktop().browse(htmlFile.toURI());
		copyReportsToLogs(sourceReport, destinationReport);
		

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

//	private String getTestStatus(boolean value) {
//
//		String status;
//		if (value == true) {
//			status = "Pass";
//		} else {
//			status = "Fail";
//		}
//		return status;
//
//	}
	
	private Object[] getTestStatus(boolean file, boolean anchor) {
		
		String status="";
		String error="";
		if(file==true){
			if(anchor==false){
				error="Anchor not found";
				status="Fail";
		}else{
			status="Pass";
		}
		}else{
			error="HTML file does not exist";
			status="Fail";
		}	
		
		Object[] obj = new Object[] {status, error};
		
		return obj;

	}
	
	@DataProvider(name = "getDownloadParameteres", parallel = false)
	public Object[][] data1() throws Exception {
		Object[][] retObjArrlogin = getTableArray(xlFilePathdownload,
				"downloadParameters", "Start", "End");
		return (retObjArrlogin);
	}
	public String[][] getTableArray(String xlFilePathlogin, String sheetName,
			String tableStartName, String tableEndName) throws Exception {
		String[][] tabArraylogin = null;

		Workbook workbook = Workbook.getWorkbook(new File(xlFilePathlogin));
		Sheet sheet = workbook.getSheet(sheetName);

		int startRow, startCol, endRow, endCol, ci, cj;
		Cell tableStart = sheet.findCell(tableStartName);
		startRow = tableStart.getRow();
		startCol = tableStart.getColumn();

		Cell tableEnd = sheet.findCell(tableEndName, startCol + 1,
				startRow + 1, 100, 100, false);

		endRow = tableEnd.getRow();
		endCol = tableEnd.getColumn();
		tabArraylogin = new String[endRow - startRow - 1][endCol - startCol - 1];
		ci = 0;

		for (int i = startRow + 1; i < endRow; i++, ci++) {
			cj = 0;
			for (int j = startCol + 1; j < endCol; j++, cj++) {
				tabArraylogin[ci][cj] = sheet.getCell(j, i).getContents();
			}
		}

		return (tabArraylogin);
	}
	
	public static void downloadTitleManifest(String url, File path, String name, String manifestName) throws FileNotFoundException{
		
		try {
			saveFileFromUrlWithCommonsIO(url, path, name,manifestName);

		} catch (Exception e) {

			throw new FileNotFoundException(" Unable to download "+ manifestName);
		}
		Assert.assertTrue(path.exists(), "Unable to download "+ manifestName);
	}
	public static void saveFileFromUrlWithCommonsIO(String fileUrl,
			File file, String fileName, String name) throws MalformedURLException,
			IOException, InterruptedException {

		FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
		long filesize1;
		long filesize2;
		do {

			filesize1 = file.length(); // check file size
			Thread.sleep(5); // wait for 5 seconds
			filesize2 = file.length(); // check file size again

		} while (filesize2 != filesize1);
		
		Assert.assertTrue(file.exists(), "Unable to download file "+ name);
		if(file.exists())
		System.out.println(name + " downloaded successfully");	
	}
	
public static Object[] readInformationFromManifest(String path) throws UnsupportedEncodingException, FileNotFoundException, IOException, ParseException{
		
		JSONParser jsonParser = new JSONParser();
		
		JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(
				new FileInputStream(path), "UTF8"));
		String shortName = (String) jsonObject.get("shortname").toString()
				.trim();
		String currentVersion = (String) jsonObject.get("current").toString()
				.trim();
		String sampleZipName = (String) jsonObject.get("sample").toString()
				.trim();
		String media = (String) jsonObject.get("mediaURL").toString().trim();
		
		Object[] obj = new Object[] { shortName, currentVersion, sampleZipName, media};
		return obj;
	}

public static void downloadZip(String url, File path, String name, String zipName) throws FileNotFoundException{
	
	System.out.println("Download started for "+ zipName +" sample product content");
	try {
		saveFileFromUrlWithCommonsIO(url, path, name, zipName);

	} catch (Exception e) {

		throw new FileNotFoundException(" Unable to download "+ zipName);
	}
	Assert.assertTrue(path.exists(), "Unable to download "+ zipName);
	System.out.println("Downloading of sample zip content "+zipName+" is finished");
}

public static void unzip(String strZipFile) throws Exception {
	String folderName;
		try {
			/*
			 * STEP 1 : Create directory with the name of the zip file
			 * 
			 * For e.g. if we are going to extract c:/demo.zip create c:/demo
			 * directory where we can extract all the zip entries
			 */
			File fSourceZip = new File(strZipFile);
			String zipPath = strZipFile.substring(0, strZipFile.length() - 4);
			String[] folder = zipPath.split("/");
			folderName = folder[0];
			/*
			 * STEP 2 : Extract entries while creating required sub-directories
			 */
			ZipFile zipFile = new ZipFile(fSourceZip);
			Enumeration e = zipFile.entries();

			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				File destinationFilePath = new File(folderName, entry.getName());

				// create directories if required.
				destinationFilePath.getParentFile().mkdirs();

				// if the entry is directory, leave it. Otherwise extract it.
				if (entry.isDirectory()) {
					continue;
				} else {
					System.out.println("Extracting " + destinationFilePath);

					/*
					 * Get the InputStream for current entry of the zip file
					 * using
					 * 
					 * InputStream getInputStream(Entry entry) method.
					 */
					BufferedInputStream bis = new BufferedInputStream(
							zipFile.getInputStream(entry));

					int b;
					byte buffer[] = new byte[1024];

					/*
					 * read the current entry from the zip file, extract it and
					 * write the extracted file.
					 */
					FileOutputStream fos = new FileOutputStream(
							destinationFilePath);
					BufferedOutputStream bos = new BufferedOutputStream(fos,
							1024);

					while ((b = bis.read(buffer, 0, 1024)) != -1) {
						bos.write(buffer, 0, b);
					}

					// flush the output stream and close it.
					bos.flush();
					bos.close();

					// close the input stream.
					bis.close();
					System.out.println("Unzipped successfully");
				}
			}
		} catch (IOException ioe) {
			throw new IOException(ioe+ " of "+strZipFile + " Zip file is not available or corrupted");
			
		}catch (Exception e){
			throw new Exception(e+ " of "+strZipFile);
		}
		File f = new File(folderName);
		Assert.assertTrue(f.exists(), "Extracted folder not present inside download folder for "+folderName);
	}
public static void downloadProductManifest(String url, File path, String name, String manifestName) throws FileNotFoundException{
	
	try {
		saveFileFromUrlWithCommonsIO(url, path, name, manifestName);

	} catch (Exception e) {

		throw new FileNotFoundException(" Unable to download "+ manifestName);
	}
	Assert.assertTrue(path.exists(), "Unable to download "+ manifestName);
}
public static String formURL(String baseURL, String UUID, String fileName){
	
	return baseURL + "/" + UUID + "/" + fileName;
}

public static boolean exists(String URLName){
    try { 
      HttpURLConnection.setFollowRedirects(false);
      System.setProperty("jsse.enableSNIExtension", "false");
      // note : you may also need 
      //        HttpURLConnection.setInstanceFollowRedirects(false) 
      HttpURLConnection con =
         (HttpURLConnection) new URL(URLName).openConnection();
      con.setRequestMethod("HEAD");
      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
    } 
    catch (Exception e) {
       e.printStackTrace();
       return false; 
    } 
}

public static void copyReportsToLogs(String source, String destination) throws IOException{
	
	Date dNow = new Date();
	SimpleDateFormat ftFile = new SimpleDateFormat("yyyy-MM-dd_hh.mm.ss");
	
	Zip zip = new Zip();

	String dateTime = ftFile.format(dNow).toString();
		
	String Zipname = dateTime + ".zip";
	zip.zip(new File(source), new File(Zipname));

	File dir = new File(destination+"/"+dateTime);
	dir.mkdir();

	File sourceFile = new File(Zipname);
	File destinationDir = new File(destination+ dateTime);
	FileUtils.moveFileToDirectory(sourceFile, destinationDir, true);

}


}
