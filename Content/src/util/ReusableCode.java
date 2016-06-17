package util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ReusableCode {

	
public boolean getResponseCode(String url) throws InterruptedException {

	boolean isValid = false;
try {
	URL u = new URL(url);
	HttpURLConnection h = (HttpURLConnection) u.openConnection();
	h.setRequestMethod("GET");
	h.connect();
	if(h.getResponseCode()==200)
		isValid=true;
	} catch (Exception e) {
	System.out.println("MalFormed URL : " + url);
}
return isValid;
}

public String getExternalURLStatus(String url) throws InterruptedException, IOException {

	String isValid = "Fail";
	URL u = new URL(url);
	HttpURLConnection h = (HttpURLConnection) u.openConnection();
	h.setRequestMethod("GET");
	h.connect();
	if(h.getResponseCode()==200){
		isValid="Pass";
}else{
	isValid = "Fail";
}
return isValid;
}

	

public static int getResponseCodeInt(String url) throws InterruptedException, IOException {

	try {
	URL u = new URL(url);
	HttpURLConnection h = (HttpURLConnection)u.openConnection();
	
	h.setRequestMethod("GET");
	h.connect();
	return h.getResponseCode();
	} catch (Exception e) {
		System.out.println("MalFormed URL : " + url);
	}
	return 0;
}

}
