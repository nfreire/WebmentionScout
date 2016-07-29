package inescid.webmention.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ReceiverTesterScript {

	public static void main(String[] args) throws IOException {
		String endpoint="http://localhost:8080/inescid-webmentionscout/webmention-endpoint";
		String urlParameters = "source=http://www.myblog.com/article&target=http://www.europeana.eu/ABC";
		sendPost(endpoint, urlParameters);

		urlParameters = "source=invalid://www.myblog.com/article&target=http://www.europeana.eu/ABC";
			sendPost(endpoint, urlParameters);

		urlParameters = "source=http://www.myblog.com/article&target=http://www.europeana.pt/ABC";
			sendPost(endpoint, urlParameters);
	}
	
	
	private static void sendPost(String url, String urlParameters) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		
		try {
			con.getResponseCode();
		} catch (Exception e) {
			
		}
		
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		StringBuffer response = new StringBuffer();
		try {
		BufferedReader in;
		try {
			in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
		} catch (Exception e) {
			InputStream errorStream = con.getErrorStream();
			if(errorStream!=null)
				in = new BufferedReader(
						new InputStreamReader(errorStream));
			else { 
				e.printStackTrace();
				return;
			}
		}
		String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
		}
		
		//print result
		System.out.println(response.toString());
	}
}
