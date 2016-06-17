package validator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import util.ReusableCode;

public class FindCertainExtension {

	public static List<String> htmlFileList = null;
	public static List<String> results = null;
	public static List<String> noprotocol = null;

	@BeforeTest
	public void getHTMLFiles() throws IOException {
		htmlFileList = new ArrayList<String>();
		results = new ArrayList<String>();
		noprotocol = new ArrayList<String>();

		File dir = new File("Source");
		String[] extensions = new String[] { "html", "htm" };
		System.out.println("Getting all .html in " + dir.getCanonicalPath()
				+ " including those in subdirectories");
		List<File> files = (List<File>) FileUtils.listFiles(dir, extensions,
				true);
		for (File file : files) {
//			System.out.println(file.getCanonicalPath().trim() + ","
//					+ file.getName());
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
			doc = Jsoup.parse(input, "UTF-8");
						Elements links = doc.select("a");
			for (Element link : links) {
				String linkHref = link.attr("href");
				// System.out.println(htmlFile+":::"+linkHref);
				boolean noProtocol = false;
				if (linkHref.startsWith("http") || linkHref.startsWith("https")
						|| linkHref.startsWith("www")) {

					if (linkHref.startsWith("www")) {
						linkHref = linkHref.replace("www", "http://www");
						noProtocol = true;
					}
					int responseCode = ReusableCode.getResponseCodeInt(linkHref);
					results.add("file::"+htmlFile + "," + "anchor::"+linkHref +","+ "anchorType::External"
							+ "," + "protocolStatus::"+noProtocol + "," + "result::"+getExternalURLStatus(responseCode) + "," + "error::"+responseCode);
					// Pushing status as file, anchor, anchorType, protocolStatus, result, error
					if (noProtocol == true)
						noprotocol.add("file::"+htmlFile + "," + "anchor::"+linkHref +","+ "anchorType::External"
								+ "," + "protocolStatus::"+noProtocol + "," + "result::"+getExternalURLStatus(responseCode) + "," + "error::"+responseCode);
					// Pushing status as html file, anchor, anchorType, protocolStatus, result, error
					 System.out.println("file::"+htmlFile + "," + "anchor::"+linkHref +","+ "anchorType::External"
								+ "," + "protocolStatus::"+noProtocol + "," + "result::"+getExternalURLStatus(responseCode) + "," + "error::"+responseCode);

				} else if (linkHref.startsWith("#")) {

					boolean iSAnchorExist = getIDStatus(linkHref, htmlFile, doc);
					results.add("file::"+htmlFile + "," + "anchor::"+linkHref +","+ "anchorType::Internal-AnchorSameHTML"
							+ "," + "protocolStatus::NA" + "result::"+getTestStatus(iSAnchorExist) + "," + "error::"+"");
					// Pushing status as html file, anchor, anchorType, protocolStatus, result, error
					
					System.out.println("file::"+htmlFile + "," + "anchor::"+linkHref +","+ "anchorType::Internal-AnchorSameHTML"
							+ "," + "protocolStatus::NA" + "result::"+getTestStatus(iSAnchorExist) + "," + "error::"+"");

				} else if ((linkHref.startsWith("../"))
						&& ((linkHref.endsWith(".htm")))
						|| (linkHref.endsWith(".html"))) {
				
					results.add("file::"+htmlFile + "," + "anchor::"+linkHref +","+ "anchorType::Internal-PointsOtherHTML"
							+ "," + "protocolStatus::NA" + "result::"+iSOtherHTMLFileExits(htmlFile, linkHref) + "," + "error::"+"");
					
					// Pushing status as html file, anchor, anchorType, protocolStatus, result, error
					System.out.println("file::"+htmlFile + "," + "anchor::"+linkHref +","+ "anchorType::Internal-PointsOtherHTML"
							+ "," + "protocolStatus::NA" + "result::"+iSOtherHTMLFileExits(htmlFile, linkHref) + "," + "error::"+"");
				} else if ((linkHref.startsWith("../"))) {
					
					results.add(htmlFile + "," + linkHref + "," + "Internal-AnchorOtherHTML" + "," + "NA" + ","	+ iSAnchorInOtherHTMLFileExits(htmlFile, linkHref));
					
					// Pushing status as html file, anchor, anchorType, protocolStatus, result, error
					System.out.println(htmlFile + "," + linkHref + "," + "Internal-AnchorOtherHTML"
							+ "," + "NA" + ","
							+ iSAnchorInOtherHTMLFileExits(htmlFile, linkHref));
					
					
					
				}else {
					results.add(htmlFile + "," + linkHref + "," + "No Tested"
							+ "," + "NA");
					System.out.println(htmlFile + "," + linkHref + "," + "No Tested"
							+ "," + "NA");
				}

			}

		}

	}

	private String getExternalURLStatus(int responseCode) {
		String status;
		if(responseCode==200){
			status = "Pass";
		}else{
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
		
		FileWriter writer = new FileWriter("output.txt"); 
		for(String str: htmlFileList) {
		  writer.write(str+"\n");
		}
		writer.close();
		System.out
				.println("--------------------------------Total Results--------------------------------------");

		for (int i = 0; i < results.size(); i++) {

			System.out.println(results.get(i));
		}

		System.out
				.println("-----------------------------------No protocol----------------------------------------");
		if (noprotocol.size() > 0)
			for (int i = 0; i < noprotocol.size(); i++) {

				System.out.println(noprotocol.get(i));
			}

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
	
	
	private String getTestStatus(boolean value){
		
		String status;
		if(value==true){
			status = "Pass";
		}else{
			status = "Fail";
		}
		return status;
		
	}
	
}
