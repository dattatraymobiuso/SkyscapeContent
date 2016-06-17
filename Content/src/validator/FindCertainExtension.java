package validator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
			System.out.println(file.getCanonicalPath().trim() + "::"
					+ file.getName());
			htmlFileList.add(file.getCanonicalPath().trim() + "::"
					+ file.getName());

		}
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
			try {
				doc = Jsoup.parse(input, "UTF-8");

			} catch (IOException e) {
				e.printStackTrace();
			}

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
					int responseCode = ReusableCode
							.getResponseCodeInt(linkHref);
					results.add(htmlFile + "::" + linkHref + "::External"
							+ "::" + noProtocol + "::" + responseCode);
					if (noProtocol == true)
						noprotocol.add(htmlFile + "::" + linkHref
								+ "::External" + "::" + noProtocol + "::"
								+ responseCode);
					// System.out.println(htmlFile+ " : " +linkHref +
					// " : External Link :"
					// + responseCode);

				} else if (linkHref.startsWith("#")) {

					boolean iSIdExist = getIDStatus(linkHref, htmlFile, doc);
					results.add(htmlFile + "::" + linkHref + "::" + "Internal"
							+ "::" + "NA" + "::" + iSIdExist);
					// System.out.println(htmlFile+ ":Internal:"+"NA");

				} else if ((linkHref.startsWith("../"))
						&& ((linkHref.endsWith(".htm")))
						|| (linkHref.endsWith(".html"))) {
					System.out
							.println(iSOtherHTMLFileExits(htmlFile, linkHref));
					results.add(htmlFile + "::" + linkHref + "::" + "Internal"
							+ "::" + "NA" + "::"
							+ iSOtherHTMLFileExits(htmlFile, linkHref));
					System.out.println(htmlFile + "::" + linkHref + "::"
							+ "Internal" + "::" + "NA" + "::"
							+ iSOtherHTMLFileExits(htmlFile, linkHref));
				} else if ((linkHref.startsWith("../"))) {
					System.out
							.println("You are inside cross referance link section ");
					results.add(htmlFile + "::" + linkHref + "::" + "Internal"
							+ "::" + "NA" + "::"
							+ iSAnchorInOtherHTMLFileExits(htmlFile, linkHref));
					System.out.println(htmlFile + "::" + linkHref + "::"+ "Internal" + "::" + "NA" + "::"+ iSAnchorInOtherHTMLFileExits(htmlFile, linkHref));
					System.out.println("Stop");
				}

			}

		}

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
		System.out.println(urlFilePath);
		iSFileExist = checkFileExistance(new File(urlFilePath));
		return iSFileExist;
	}

	private boolean iSAnchorInOtherHTMLFileExits(String htmlFile,
			String linkHref) throws IOException {
		boolean iSAnchorInFileExist = false;

		String parentDiretory = getParentDirectory(htmlFile);
		String urlFullFilePath = getNewPath(parentDiretory, linkHref);
		// System.out.println(urlFilePath);
		String anchor = getAnchorFromURL(urlFullFilePath);
		String fileURL = getFileURL(urlFullFilePath);
		iSAnchorInFileExist = checkAnchorInFile(new File(fileURL), anchor);
		return iSAnchorInFileExist;
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
		System.out.println("Null issue : " + file + " : " + linkHref);
		if (el != null) {
			status = true;
		} else {
			status = false;
		}
		return status;
	}

	@AfterTest
	public static void printResults() {

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
		String temp[] = getFile.split("::");
		return temp[0].trim();
	}

	private String getFileName(String getFile) {
		String temp[] = getFile.split("::");
		return temp[1].trim();
	}
}
