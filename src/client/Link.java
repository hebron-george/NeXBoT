package client;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class Link {
	private String title;
	Document doc;
	Link(String url) {
		try {
			/* I set a very long time out here because the raspberry pi
			 * this is running off of has a very slow network interface card
			 */
			doc = Jsoup.connect(url).timeout(30*1000).get();
			title = doc.title();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to open link: " + url);
		}
	}
	public String getTitle() {
		return title;
	}

	/**
	 * Each link type should have it's own summary to print to the channel
	 * @return String
     */
	public abstract String summary();
}
