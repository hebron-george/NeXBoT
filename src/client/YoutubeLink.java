package client;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;



public class YoutubeLink {
	public String title = "";
	public String duration = "";
	
	YoutubeLink(String url)
	{
		try {
			
			Document doc = Jsoup.connect(url).get();
			title = doc.title();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	YoutubeLink()
	{
		
	}
}
