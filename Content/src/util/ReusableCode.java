package util;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

public class ReusableCode {

	// public boolean iSOtherHTMLFileExits(String htmlFile, String linkHref) {
	// boolean iSFileExist = false;
	// String parentDiretory = getParentDirectory(htmlFile);
	// String urlFilePath = getNewPath(parentDiretory, linkHref);
	// iSFileExist = checkFileExistance(new File(urlFilePath));
	// return iSFileExist;
	// }

	public void deleteExistingData(String filePath) {

		File directory = new File(filePath);

		// make sure directory exists
		if (!directory.exists()) {

			System.out.println("Directory does not exist.");
			directory.mkdir();

		} else {

			try {

				delete(directory, filePath);

			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		Assert.assertTrue(directory.exists(),
				"Downloads folder is not available in the project");
	}

	public void delete(File file, String fileSource) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {
				File mainFolder = new File(fileSource);
				if (!(file.equals(mainFolder))) {
					file.delete();
					System.out.println("Directory is deleted 1 : "
							+ file.getAbsolutePath());
				}
			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete, fileSource);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					File mainFolder = new File(fileSource);
					if (!(file.equals(mainFolder))) {
						file.delete();
						System.out.println("Directory is deleted 2 : "
								+ file.getAbsolutePath());
					}
				}

			}

		} else {
			// if file, then delete it
			file.delete();
			System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}

	public boolean getResponseCode(String url) throws InterruptedException {

		boolean isValid = false;
		try {
			URL u = new URL(url);
			HttpURLConnection h = (HttpURLConnection) u.openConnection();
			h.setRequestMethod("GET");
			h.connect();
			if (h.getResponseCode() == 200)
				isValid = true;
		} catch (Exception e) {
			System.out.println("MalFormed URL : " + url);
		}
		return isValid;
	}

	public String getExternalURLStatus(String url) throws InterruptedException,
			IOException {

		String isValid = "Fail";
		URL u = new URL(url);
		HttpURLConnection h = (HttpURLConnection) u.openConnection();
		h.setRequestMethod("GET");
		h.connect();
		if (h.getResponseCode() == 200) {
			isValid = "Pass";
		} else {
			isValid = "Fail";
		}
		return isValid;
	}

	public int getResponseCodeInt(String url) throws InterruptedException,
			IOException {

		try {
			URL u = new URL(url);
			HttpURLConnection h = (HttpURLConnection) u.openConnection();

			h.setRequestMethod("GET");
			h.connect();
			return h.getResponseCode();
		} catch (Exception e) {
			System.out.println("MalFormed URL : " + url);
		}
		return 0;
	}

	public boolean getIDStatus(String linkHref, String file, Document doc) throws IOException {
		boolean status = false;
		Document doc2 = null;
		
		if (linkHref.startsWith("#")) {
			// System.out.println(linkHref);
			linkHref = removeChar(linkHref, '#');
			// System.out.println(linkHref);
		}
		
		doc2 = Jsoup.parse(new File(file), "UTF-8");
		Elements e2 = doc2.getElementsByAttributeValue("id", linkHref);
			
		if (e2.size() >= 1) {
			status = true;
		} else {
			Elements e3 = doc2.getElementsByAttributeValue("name", linkHref);
			if (e3.size() >= 1) {
				status = true;
			} else {
				status = false;
			}

		}
	
		return status;
	}

	public boolean getPopupIDStatus(String linkHref, String file, Document doc) throws IOException {
		boolean status = false;
		Element el;
		if (linkHref.startsWith("popup:#")) {
			// System.out.println(linkHref);
			linkHref = removeChar(linkHref, '#');
			// System.out.println(linkHref);
		}

		Document doc2 = Jsoup.parse(new File(file), "UTF-8");
		Elements e2 = doc2.getElementsByAttributeValue("id", linkHref);
		if (e2.size() >= 1) {
			status = true;
		} else {
			Elements e3 = doc2.getElementsByAttributeValue("name", linkHref);
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

	public String removeChar(String s, char c) {
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

	public String getProperPopupAnchor(String linkHref) {
		String id = "";
		if (linkHref.contains(":")) {
			String[] temp = linkHref.split(":");
			if (temp.length > 0) {
				id = temp[1];
			}

		}
		return id;
	}

	public String formLink(String linkHref, String url) {
		String part2 = null;
		String[] temp = linkHref.split("=");
		if (temp.length > 1) {
			part2 = temp[1];

		}
		return url + part2;
	}

	public String getExternalURLStatus(int responseCode) {
		String status;
		if (responseCode == 200) {
			status = "Pass";
		} else {
			status = "Fail";
		}
		return status;
	}

	public String getResponseCodeIfFail(int responseCode) {
		String resCode = Integer.toString(responseCode);
		String status;
		if (resCode.equals("200")) {
			status = "200";
		} else if (resCode.equals("0")) {
			status = "Malformed URL";
		} else {
			status = resCode;
		}
		return status;
	}

	public String getParentDirectory(String htmlFile) {
		File f1 = new File(htmlFile);
		String s1 = f1.getParent();
		File f2 = new File(s1);
		return f2.getParent();
	}

	public Object[] iSHTMLFileExits(String htmlFile, String linkHref) {
		boolean iSFileExist = false;
		String status = "-";
		String error = "-";
		String parentDiretory = getParentDirectory(htmlFile);
		String urlFilePath = getNewPath(parentDiretory, linkHref);
		iSFileExist = checkFileExistance(new File(urlFilePath));
		if (iSFileExist == true) {
			status = "Pass";

		} else {
			status = "Fail";
			error = "HTML file does not exist";
		}
		Object[] obj = new Object[] { status, error };
		return obj;
	}

	public String getRequiredFilePath(File files, String fileSource)
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

	public boolean iSAnchorInOtherHTMLFileExits(String htmlFile, String linkHref)
			throws IOException {

		boolean iSFileExist = false;
		boolean iSAnchorInFileExist = false;
		boolean status = false;
		String parentDiretory = getParentDirectory(htmlFile);
		String urlFullFilePath = getNewPath(parentDiretory, linkHref);
		// System.out.println(urlFilePath);
		String anchor = getAnchorFromURL(urlFullFilePath);
		String fileURL = getFileURL(urlFullFilePath);
		iSFileExist = checkFileExistance(new File(fileURL));
		if (iSFileExist == true)
			iSAnchorInFileExist = checkAnchorInFile(new File(fileURL), anchor);
		if (iSFileExist == true && iSAnchorInFileExist == true) {

			status = true;
		}
		return status;
	}

	public String iSAnchorInOtherHTMLStatus(String htmlFile, String linkHref)
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

	public String iSAnchorInOtherHTMLExits(String htmlFile, String linkHref)
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
		} else {
			status = "HTML file does not exist";
		}

		return status;
	}

	public String getFileURL(String urlFullFilePath) {
		String[] temp = null;
		if (urlFullFilePath.contains("#")) {
			temp = urlFullFilePath.split("#");
		}
		return temp[0];
	}

	public String getAnchorFromURL(String urlFilePath) {
		String[] temp = null;
		if (urlFilePath.contains("#")) {
			temp = urlFilePath.split("#");
		}
		return temp[1];
	}

//	public boolean checkAnchorInFile(File file, String anchor)
//			throws IOException {
//
//		boolean status = false;
//		Document doc2 = Jsoup.parse(file, "UTF-8");
//		
//		Elements eId= doc2.getElementsByAttributeValueContaining("id", anchor);
//		
//		
//		if (eId!=null) {
//			status = true;
//		} else {
//			Elements eName = doc2.getElementsByAttributeValueContaining("name", anchor);
//			
//			
//			if (eName!=null) {
//				status = true;
//			} else {
//				status = false;
//			}
//		}
//		return status;
//
//	}
	
	
	public boolean checkAnchorInFile(File file, String anchor)
			throws IOException {
		
		boolean status = false;
		Document doc2 = Jsoup.parse(file, "UTF-8");
		
		Elements e1 = doc2.getElementsByAttributeValue("id", anchor);
		
		if((e1.size()>= 1)){
			status = true;
		}else{
			
			Elements e2 = doc2.getElementsByAttributeValue("name", anchor);
		
			if((e2.size()>= 1)){
				status = true;
			}else{
				status = false;
			}
			}
	
		return status;

	}
	




	public boolean checkFileExistance(File file) {
		return file.exists();
	}

	public String getNewPath(String parentDiretory, String linkHref) {
		if (linkHref.startsWith("../")) {
			linkHref = linkHref.replace("..", "");
		}
		String finalPath = linkHref.replace("/", "\\");
		return parentDiretory + finalPath.trim();
	}

	public void copyReportsToLogs(String source, String destination)
			throws IOException {

		Date dNow = new Date();
		SimpleDateFormat ftFile = new SimpleDateFormat("yyyy-MM-dd_hh.mm.ss");

		Zip zip = new Zip();

		String dateTime = ftFile.format(dNow).toString();

		String Zipname = dateTime + ".zip";
		zip.zip(new File(source), new File(Zipname));

		File dir = new File(destination + "/" + dateTime);
		dir.mkdir();

		File sourceFile = new File(Zipname);
		File destinationDir = new File(destination + "/" + dateTime);
		FileUtils.moveFileToDirectory(sourceFile, destinationDir, true);

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

	public String[][] getTableArrayFromText(String textFilePath)
			throws Exception {
		String[][] tabArray = null;

		try (BufferedReader br = new BufferedReader(
				new FileReader(textFilePath))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
				break;
			}
			String everything = sb.toString();
			tabArray = new String[][] { { textFilePath, everything } };
		}

		return (tabArray);
	}

	public void downloadTitleManifest(String url, File path, String name,
			String manifestName) throws FileNotFoundException {

		try {
			saveFileFromUrlWithCommonsIO(url, path, name, manifestName);

		} catch (Exception e) {

			throw new FileNotFoundException(" Unable to download "
					+ manifestName);
		}
		Assert.assertTrue(path.exists(), "Unable to download " + manifestName);
	}

	public void saveFileFromUrlWithCommonsIO(String fileUrl, File file,
			String fileName, String name) throws MalformedURLException,
			IOException, InterruptedException {

		FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
		long filesize1;
		long filesize2;
		do {

			filesize1 = file.length(); // check file size
			Thread.sleep(5); // wait for 5 seconds
			filesize2 = file.length(); // check file size again

		} while (filesize2 != filesize1);

		Assert.assertTrue(file.exists(), "Unable to download file " + name);
		if (file.exists())
			System.out.println(name + " downloaded successfully");
	}

	public Object[] readInformationFromManifest(String path)
			throws UnsupportedEncodingException, FileNotFoundException,
			IOException, ParseException {

		JSONParser jsonParser = new JSONParser();

		JSONObject jsonObject = (JSONObject) jsonParser
				.parse(new InputStreamReader(new FileInputStream(path), "UTF8"));
		String shortName = "";
		if (jsonObject.containsKey("shortname")) {
			shortName = (String) jsonObject.get("shortname").toString().trim();
		}
		String currentVersion = "";
		if (jsonObject.containsKey("current")) {
			currentVersion = (String) jsonObject.get("current").toString()
					.trim();
		}
		String sampleZipName = "";
		if (jsonObject.containsKey("sample")) {
			sampleZipName = (String) jsonObject.get("sample").toString().trim();
		}
		String media = "";
		if (jsonObject.containsKey("mediaURL")) {
			media = (String) jsonObject.get("mediaURL").toString().trim();
		}
		Object[] obj = new Object[] { shortName, currentVersion, sampleZipName,
				media };
		return obj;
	}

	public void downloadZip(String url, File path, String name, String zipName)
			throws FileNotFoundException {

		System.out.println("Download started for " + zipName
				+ " sample product content");
		try {
			saveFileFromUrlWithCommonsIO(url, path, name, zipName);

		} catch (Exception e) {

			throw new FileNotFoundException(" Unable to download " + zipName);
		}
		Assert.assertTrue(path.exists(), "Unable to download " + zipName);
		System.out.println("Downloading of sample zip content " + zipName
				+ " is finished");
	}

	public void unzip(String strZipFile) throws Exception {
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
			throw new IOException(ioe + " of " + strZipFile
					+ " Zip file is not available or corrupted");

		} catch (Exception e) {
			throw new Exception(e + " of " + strZipFile);
		}
		File f = new File(folderName);
		Assert.assertTrue(f.exists(),
				"Extracted folder not present inside download folder for "
						+ folderName);
	}

	public void downloadProductManifest(String url, File path, String name,
			String manifestName) throws FileNotFoundException {

		try {
			saveFileFromUrlWithCommonsIO(url, path, name, manifestName);

		} catch (Exception e) {

			throw new FileNotFoundException(" Unable to download "
					+ manifestName);
		}
		Assert.assertTrue(path.exists(), "Unable to download " + manifestName);
	}

//	 public String formURL(String baseURL, String UUID, String fileName) {
//	
//	 return baseURL + "/" + UUID + "/" + fileName;
//	 }

	public String formNewURL(String baseURL, String fileName) {

		return baseURL + "/" + fileName;
	}

	public String getFilePath(String getFile) {
		String temp[] = getFile.split(":::");
		return temp[0].trim();
	}

	public String getFileName(String getFile) {
		String temp[] = getFile.split(":::");
		return temp[1].trim();
	}

	public Object[] getTestStatus(boolean file, boolean anchor) {

		String status = "-";
		String error = "-";
		if (file == true) {
			if (anchor == false) {
				error = "Anchor not found";
				status = "Fail";
			} else {
				status = "Pass";
			}
		} else {
			error = "HTML file does not exist";
			status = "Fail";
		}

		Object[] obj = new Object[] { status, error };

		return obj;

	}

	public Object getAndAppendResult(String result, int j) {
		String data;
		String[] split = result.split(":::");
		String key = split[j];
		String[] value = key.split("::");
		if (value.length > 1) {
			data = value[1].trim();
		} else {
			data = "";
		}

		return data;
	}

	public boolean exists(String URLName) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			System.setProperty("jsse.enableSNIExtension", "false");
			// note : you may also need
			// HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) new URL(URLName)
					.openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public String getTitle(String title) {
		
		String pageTitle = "-";
		if(!title.isEmpty()){
			pageTitle = title;
		}
		
		return pageTitle;
	}

	public void openInBrowser(String uri) throws URISyntaxException {
		
			File htmlFile = new File(uri);
	        if(Desktop.isDesktopSupported()){
	            Desktop desktop = Desktop.getDesktop();
	            try {
	                desktop.browse(htmlFile.toURI());
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }else{
	            Runtime runtime = Runtime.getRuntime();
	            try {
	                runtime.exec("xdg-open " + uri);
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
		
	}

	public String getTopicName(Element link) {
		String temp="-";
		if(link!=null){
			if(!link.text().isEmpty())
			temp = link.text();
		}
		
		return temp;
	}


}