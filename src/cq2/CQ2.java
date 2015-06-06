package cq2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


import client.stringAccessor;

public class CQ2 {
	HttpClient c = new DefaultHttpClient();
	HttpPost post = null;
	HttpResponse response = null;
	String line = "";

	private String DB_URL = "jdbc:mysql://108.167.163.247/vashy_cq2";
	//  Database credentials
	private String USER = stringAccessor.getString("CQ2.db_user");
	private String PASS = stringAccessor.getString("CQ2.db_pass");
	
	private String db_error = stringAccessor.getString("CQ2.db_error");
	

   private Connection conn = null;

	public CQ2(String user, String password) {
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

			if (page.contains("Invalid player.")) {
				return "Invalid player.";
			} else if (page.contains("Looking for yourself?")) {
				return "7(Online7) That's me!";
			} else if (page.contains("is currently online.")) {
				String pattern = "(" + user + " is currently online.)";
				Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

				Matcher m = p.matcher(page);
				if (m.find()) {
					return "7(Online7) " + m.group(1);
				}

			} else {
				String pattern = "("
						+ user
						+ " is currently offline \\(.+ last time online\\)\\.)<br>";
				Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

				Matcher m = p.matcher(page);
				boolean matchFound = m.find();

				if (matchFound) {
					return "7(Online7) " + m.group(1);
				}
				return null;
			}
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex.getMessage());
		}
		return null;
	}

	public String resCheck(String user) {
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
			String myResPattern = ".+(Brimstone:.+(d+)<br>)";
			Pattern p = Pattern.compile(myResPattern);

			Matcher m = p.matcher(page);
			boolean matchFound = m.find();
			System.out.println("Rescheck matcher: " + matchFound);
			if (matchFound) {
				// System.out.println("B: " + m.group(1) + "/C: " + m.group(2) +
				// "/E: " + m.group(3) + "/G: " + m.group(4) + "/P: " +
				// m.group(5));
				System.out.println("B: " + m.group(1));
			}

		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex.getMessage());
		}

		return null;
	}
	
	public String findReveal(String user)
	{
	
		if (user.equals("") || user.equals(null))
			return "";
		try
		{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to database... for user: " + user);
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			String sql = "SELECT user FROM reveals WHERE user LIKE ? LIMIT 1";
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			prepStmt.setString(1, "%" + user + "%");
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next())
			{
				return rs.getString("user");
			}
			
		}
		catch (Exception ex)
		{
			System.out.println("Could not connect to database.");
			return db_error;
		}
		
		return "";
	}
	
	public ArrayList<String> findShard(String shard)
	{
		if (shard.equals("") || shard.equals(null))
			return null;
		
		ArrayList<String> users = new ArrayList<String>();
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			String sql = "SELECT user, amount FROM shards WHERE shard LIKE ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "%" + shard + "%");
			ResultSet rs = ps.executeQuery();
			
			while (rs.next())
			{
				users.add(rs.getString("user") + "("+ rs.getString("amount") + ")");
			}
			
			return users;
		}
		catch (Exception ex)
		{
			System.out.println(ex);
			return null;
		}
	}

}