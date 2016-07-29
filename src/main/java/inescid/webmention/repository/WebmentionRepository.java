package inescid.webmention.repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import inescid.webmention.Webmention;
import inescid.webmention.WebmentionStatus;

public class WebmentionRepository {
	Set<Webmention> processing;
	Set<Webmention> stored;
	Set<Webmention> rejected;
	CSVPrinter storedWriter;

	public WebmentionRepository(String homeFolder) throws IOException {
		processing=new HashSet<>();
		stored=new HashSet<>();
		rejected=new HashSet<>();
		
		storedWriter=new CSVPrinter(new FileWriter(new File(homeFolder, "WebmentionsStored.csv"), true), CSVFormat.EXCEL);
	}
	
	public synchronized void storeRequest(Webmention mention) throws IOException {
		processing.add(mention);
			storedWriter.printRecord(mention.getSource(), mention.getTarget());
			storedWriter.flush();
	}

	public WebmentionStatus getStatus(Webmention mention) {
		if(processing.contains(mention))
			return WebmentionStatus.RECEIVED;
		if(stored.contains(mention))
			return WebmentionStatus.PROCESSED;
		if(rejected.contains(mention))
			return WebmentionStatus.REJECTED;
		return null;
	}
	
	public synchronized void storeVerified(Webmention mention) {
		processing.remove(mention);
		stored.add(mention);
	}	

	public synchronized void reject(Webmention mention) {
		processing.remove(mention);
		rejected.add(mention);
	}	
	
	protected static String hashId(String originalId ) {
		   try {
		        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(originalId.getBytes("UTF8"));
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (java.security.NoSuchAlgorithmException e) {
		    	throw new RuntimeException(e.getMessage(), e);
		    } catch (UnsupportedEncodingException e) {
				//ignore
			}
		    return null;
		}
}
