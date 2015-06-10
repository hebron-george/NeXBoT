package client;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RedditLink extends Link {

	RedditLink(String url) {
		super(url);
	}

	@Override
	public String summary() {
		Elements e = doc.select("div#siteTable");
		Element firstChild = e.get(0);
		String upvotes = firstChild.select("div.likes").get(0).html();
		String sub = getTitle().split(":")[1];
		return "7(Reddit 7| " + upvotes + " upvotes 7| /r/" + sub.trim() + "7) " + getTitle().split(":")[0];
	}

}
