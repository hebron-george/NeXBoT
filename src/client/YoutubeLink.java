package client;

import org.jsoup.nodes.Element;



public class YoutubeLink extends Link {
	private String duration = "";
	private int durationMin = 0, durationSec = 0;
	private String description = "", likes = "", views = "";

	YoutubeLink(String url) {
		super(url);
		duration = doc.select("meta[itemprop=duration]").attr("content");
		duration = duration.replace("PT", "");
		duration = duration.replace("S", "");

		durationMin = Integer.parseInt(duration.split("M")[0]);
		durationSec = Integer.parseInt(duration.split("M")[1]);

		description = doc.select("meta[name=description]").attr("content");
	}


	public String getDuration() {
		if (durationMin < 60)
			return String.format("(%d:%02d)", durationMin, durationSec);
		else
			return String.format("(%d:%d:%02d)", durationMin / 60,
					durationMin % 60, durationSec);
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String summary() {
		// TODO Auto-generated method stub
		String formattedDuration = getDuration().replace('(', ' ');
		formattedDuration = formattedDuration.replace(')', ' ');
		formattedDuration = formattedDuration.trim();
		
		String formattedTitle = getTitle();
		
		// Remove the "- Youtube" at the end of title
		String[] split = formattedTitle.split("-");
		formattedTitle = split[0];
		for (int i = 1; i < split.length-1; i++) {
			formattedTitle += " - " + split[i] ;
		}
		// Get number of views and likes
		views = doc.select(".watch-view-count").get(0).html();
		Element e = doc.select("button[title=I like this").get(0);
		likes = e.getAllElements().get(1).html();
		
		return "7(Youtube 7| " + formattedDuration + " 7| " + views + " views 7| " + likes + " likes" +"7) " + formattedTitle;
	}
}
