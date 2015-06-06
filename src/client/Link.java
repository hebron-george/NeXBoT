package client;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class Link {
	private String title;
	Document doc;
	Link(String url) {
		try {
			doc = Jsoup.connect(url).get();
			title = doc.title();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to open link: " + url);
		}
	}
	public String getTitle() {
		return title;
	}
	
	public abstract String summary();
}
