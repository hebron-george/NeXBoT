package client;

import java.io.*;

import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HackBot implements Runnable {
	private String server = "irc.quakenet.org";
	private String nick = "botnick";
	private String login = "botlogin";
	private String cq2User = "";
	private String cq2Pass = "";
	private String channel = "#siralim";
	
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private Logger logger;
	private CQ2 cq;
	
	protected HackBot() {}
	
	public static void main(String[] args) throws Exception {
        System.out.println("Starting program.");
		try {
        	new HackBot().start();
        } catch (java.io.IOException e) {
        	System.out.println(e.getMessage());
        }
	}
	
	private void start() throws Exception {
		System.out.println("Initializing.");
		this.socket = new Socket(server, 6667);
		this.logger = LogManager.getLogger(HackBot.class.getName());
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream( )));
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream( )));

        // Log on to the server.
        logger.trace("Logging into IRC server.");
        writer.write("NICK " + this.nick + "\r\n");
        logger.trace("NICK " + this.nick + "\r\n");
        writer.write("USER " + this.login + " 8 * : Java IRC Hacks Bot\r\n");
        logger.trace("USER " + this.login + " 8 * : Java IRC Hacks Bot\r\n");
        writer.flush( );
        
        // Read lines from the server until it tells us we have connected.
        String line = null;
        while ((line = reader.readLine( )) != null) {
        	System.out.println(line);
            if (line.indexOf("004") >= 0) {
                // We are now logged in.
                break;
            }
            else if (line.indexOf("433") >= 0) {
                System.out.println("Nickname is already in use.");
                return;
            }

            if (line.startsWith("PING ")) {
                // We must respond to PINGs to avoid being disconnected.
                writer.write("PONG " + line.substring(5) + "\r\n");
                System.out.println("PONG " + line.substring(5) + "\r\n");
                writer.flush( );
            }
        }
        
        logger.trace("Logged into server successfully.");
        
        // Join the channel.
        writer.write("JOIN " + channel + "\r\n");
        writer.flush( );
        
        new Thread(this).start();
    }
	
	public void run() {
		String line;
		try {
			while ((line = reader.readLine( )) != null) {
	        	// Print the raw line received by the bot.
	            System.out.println(line);
	            
	            if (line.startsWith("PING ")) {
	                // We must respond to PINGs to avoid being disconnected.
	            	logger.trace("Bot was pinged by server: " + line);
	                writer.write("PONG " + line.substring(5) + "\r\n");
	                writer.flush( );
	            }
	            
	            
	            for (String i : line.split("\\s+"))
	            {
	            	if (i.startsWith(":http://www.youtube.com/watch?v=") || i.startsWith(":https://www.youtube.com/watch?v="))
	            	{
	            		logger.trace("Youtube link posted.");
	            		YoutubeLink y = new YoutubeLink(i.substring(1));
	            		writer.write("PRIVMSG " + channel + " :" + "7(Youtube7) " + y.getTitle() + " " + y.getDuration() + "\r\n");
	            		writer.write("PRIVMSG " + channel + " :" + "7(Youtube7) " + y.getDescription() + "\r\n");
	            		writer.flush();
	            	}
	            	else if (i.startsWith(":!online") && line.split("\\s+")[3].equals(":!online"))
	            	{
	            		logger.trace("!online command posted");
	                    // Connect to CQ2
	            		if (cq == null)
	            			cq = new CQ2(cq2User, cq2Pass);
	            		String user = line.split("\\s+")[4];
	            		String x = cq.isOnline(user);
	            		if (null == x)
	            		{
	            			writer.write("PRIVMSG " + channel + " :" + user + "? I'm having issues finding that mage right now. I might be having some sort of connection issue. Try again in a few seconds..."+ "\r\n");
	            			logger.trace("PRIVMSG " + channel + " :" + user + "? I'm having issues finding that mage right now. I might be having some sort of connection issue. Try again in a few seconds..."+ "\r\n");
	            			writer.flush();
	            			cq = null;
	            		}
	            		else
	            		{
	            			writer.write("PRIVMSG " + channel + " :" + x + "\r\n");
	            			logger.trace("PRIVMSG " + channel + " :" + x + "\r\n");
	            			writer.flush();
	            		}
	            	}
	            	else if (i.startsWith(":!rescheck") && line.split("\\s+")[3].equals(":!rescheck"))
	            	{
	            		logger.trace("!rescheck command posted");
	            		// Connect to CQ2
	            		if (cq == null)
	            			cq = new CQ2(cq2User, cq2Pass);
	            		String user = line.split("\\s+")[4];
	            		cq.resCheck(user);
	            	}
	            }
	            
	        }
		} catch (Exception e) {
			
		}
	
	}
}