package client.commands;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RedditLink extends Link {

	public RedditLink(String url) {
		super(url);
	}

	@Override
	public String summary() {
		try {
			Elements e = doc.select("div#siteTable");
			Element firstChild = e.get(0);
			String upvotes = firstChild.select("div.likes").get(0).html();
			String sub = getTitle().split(":")[1];
			return "7(Reddit 7| " + upvotes + " upvotes 7| /r/" + sub.trim() + "7) " + getTitle().split(":")[0];
		} catch (Exception e) {
			return "7(Reddit7) There was an error opening the Reddit link. Try again in a few seconds...";
		}
	}

}
