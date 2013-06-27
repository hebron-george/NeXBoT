package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class CQ2 {
	HttpClient c = new DefaultHttpClient();
	HttpPost post = null;
	HttpResponse response = null;
	String line = "";

	CQ2(String user, String password) {
		String submit = "Login";
		String url = "http://www.castlequest.be/index.php?action=login";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("name", user));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("submit", submit));

		try {
			post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			response = c.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String page = "";
			while ((line = rd.readLine()) != null) {
				// System.out.println(line);
				page = page + line;
			}
			System.out.println(page);
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex.getMessage());
		}
	}

	public String isOnline(String user) {
		String url = "http://www.castlequest.be/index.php?page=playersinfo&action=viewinfo";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("name", user));
		nameValuePairs.add(new BasicNameValuePair("action", "viewinfo"));
		try {
			post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			response = c.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String page = "";
			while ((line = rd.readLine()) != null) {
				// System.out.println(line);
				page = page + line;
			}
			System.out.println(page);
			if (page.contains("Invalid player.")) {
				return "Invalid player.";
			} else if (page.contains(user + " is currently online.")) {
				return user + " is currently online.";
			} else {
				String pattern = "(" + user + " is currently offline \\(.+\\)\\.)<br>";
				Pattern p = Pattern.compile(pattern);
				
				Matcher m = p.matcher(page);
				boolean matchFound  = m.find();
				
				if (matchFound)
				{
					return m.group(1);
				}
				
				return user + "? I'm having issues finding them right now.";
			}
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex.getMessage());
		}
		return null;
	}

}