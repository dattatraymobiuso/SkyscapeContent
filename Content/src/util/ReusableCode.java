package util;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.testng.Assert;

public class ReusableCode {
	
	
	
	
public static void deleteExistingData(String filePath){
		
		File directory = new File(filePath);
		 
    	//make sure directory exists
    	if(!directory.exists()){
 
           System.out.println("Directory does not exist.");
           directory.mkdir();
 
        }else{
 
           try{
        	   
               delete(directory, filePath);
        	
           }catch(IOException e){
               e.printStackTrace();
               System.exit(0);
           }
        }
  
    	Assert.assertTrue(directory.exists(), "Downloads folder is not available in the project");
	}

   public static void delete(File file, String fileSource)
    	throws IOException{
 
    	if(file.isDirectory()){
 
    		//directory is empty, then delete it
    		if(file.list().length==0){
    			 File mainFolder = new File(fileSource);  
        		 if(!(file.equals(mainFolder))){  
    		   file.delete();
    		   System.out.println("Directory is deleted 1 : " 
                                                 + file.getAbsolutePath());
    			}
    		}else{
    			
    		   //list all the directory contents
        	   String files[] = file.list();
     
        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(file, temp);
        		 
        	      //recursive delete
        	     delete(fileDelete, fileSource);
        	   }
        		
        	   //check the directory again, if empty then delete it
        	   if(file.list().length==0){
        		 File mainFolder = new File(fileSource);  
        		 if(!(file.equals(mainFolder))){  
           	     file.delete();
        	     System.out.println("Directory is deleted 2 : " 
                                                  + file.getAbsolutePath());
        		 }
        		 }
        		 
        		 
    		}
    		
    	}else{
    		//if file, then delete it
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
