package client;

import java.io.*;

import java.net.*;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cq2.CQ2;

public class HackBot implements Runnable {
	private String server = stringAccessor.getString("HackBot.server"); //$NON-NLS-1$
	private String nick = stringAccessor.getString("HackBot.nick"); //$NON-NLS-1$
	private String login = stringAccessor.getString("HackBot.user"); //$NON-NLS-1$
	private String cq2User = stringAccessor.getString("HackBot.cq2User"); //$NON-NLS-1$
	private String cq2Pass = stringAccessor.getString("HackBot.cq2Pass"); //$NON-NLS-1$
	private String channel = stringAccessor.getString("HackBot.channel"); //$NON-NLS-1$
	private String chanPass = stringAccessor.getString("HackBot.channelPass"); //$NON-NLS-1$
	
	private static Socket socket;
	private static BufferedWriter writer;
	private static BufferedReader reader;
	private static Logger logger;
	private static CQ2 cq;
	
	protected HackBot() {}
	
	public static void main(String[] args) throws Exception {
		logger = LogManager.getLogger(HackBot.class.getName());
		logger.trace("Starting program."); //$NON-NLS-1$
		
        try {
        	new HackBot().start();
        } catch (java.io.IOException e) {
        	logger.trace(e.getMessage());
        }
	}
	
	private void start() throws Exception {
		logger.trace("Initializing."); //$NON-NLS-1$
		HackBot.socket = new Socket(server, 6667);
        HackBot.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream( )));
        HackBot.reader = new BufferedReader(new InputStreamReader(socket.getInputStream( )));

        // Log on to the server.
        logger.trace("Logging into IRC server."); //$NON-NLS-1$
        writer.write("NICK " + this.nick + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
        logger.trace("NICK " + this.nick + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
        writer.write("USER " + this.login + " 8 * : Java IRC Hacks Bot\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
        logger.trace("USER " + this.login + " 8 * : Java IRC Hacks Bot\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
        writer.flush( );
        
        // Read lines from the server until it tells us we have connected.
        String line = null;
        while ((line = reader.readLine( )) != null) {
        	System.out.println(line);
            if (line.indexOf("004") >= 0) { //$NON-NLS-1$
                // We are now logged in.
                break;
            }
            else if (line.indexOf("433") >= 0) { //$NON-NLS-1$
                System.out.println("Nickname is already in use."); //$NON-NLS-1$
                return;
            }

            if (line.startsWith("PING ")) { //$NON-NLS-1$
                // We must respond to PINGs to avoid being disconnected.
                writer.write("PONG " + line.substring(5) + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("PONG " + line.substring(5) + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
                writer.flush( );
            }
        }
        
        logger.trace("Logged into server successfully."); //$NON-NLS-1$
        
        // Join the channel.
        writer.write("JOIN " + channel + " " + chanPass +  "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
        writer.flush( );
        
        new Thread(this).start();
    }
	
	public void run() {
		String line;
		try {
			while ((line = reader.readLine( )) != null) {
	        	// Print the raw line received by the bot.
	            System.out.println(line);
	            
	            if (line.startsWith("PING ")) { //$NON-NLS-1$
	                // We must respond to PINGs to avoid being disconnected.
	            	logger.trace("Bot was pinged by server: " + line); //$NON-NLS-1$
	                writer.write("PONG " + line.substring(5) + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
	                writer.flush( );
	            }
	            
	            
	            for (String i : line.split("\\s+")) //$NON-NLS-1$
	            {
	            	if (i.startsWith(":http://www.youtube.com/watch?v=") || i.startsWith(":https://www.youtube.com/watch?v=")) //$NON-NLS-1$ //$NON-NLS-2$
	            	{
	            		logger.trace("Youtube link posted."); //$NON-NLS-1$
	            		YoutubeLink y = new YoutubeLink(i.substring(1));
	            		writer.write("PRIVMSG " + channel + " :" + "7(Youtube7) " + y.getTitle() + " " + y.getDuration() + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	            		writer.flush();
	            	}
	            	else if (i.startsWith(":!online") && line.split("\\s+")[3].equals(":!online")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	            	{
	            		logger.trace("!online command posted"); //$NON-NLS-1$
	            		try
	            		{
		                    // Connect to CQ2
		            		if (cq == null)
		            			cq = new CQ2(cq2User, cq2Pass);
		            		String user = line.split("\\s+")[4]; //$NON-NLS-1$
		            		String x = cq.isOnline(user);
		            		if (null == x || x.equals(""))
		            		{
		            			cq = new CQ2(cq2User, cq2Pass);
		            			x = cq.isOnline(user);
		            		}
		            		String channel = "#siralim";
		                    writer.write("JOIN " + channel +  "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
		                    writer.flush( );
	            			writer.write("PRIVMSG " + channel + " :" + x + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	            			logger.trace("PRIVMSG " + channel + " :" + x + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	            			writer.flush();
		            		
	            		}
	            		catch (ArrayIndexOutOfBoundsException ex)
	            		{
	            			System.out.println(ex);
	            			writer.write("PRIVMSG " + channel + " :" + "ENTER A VALID USER XD" + "\r\n");
	            			writer.flush();	           	            			
	            		}
	            		catch (Exception ex)
	            		{
	            			System.out.println(ex);
	            			writer.write("PRIVMSG " + channel + " :" + "THAT COMMAND CAUSED AN ERROR XD CHECK LOGS." + "\r\n");
	            			writer.flush();	           	  
	            		}
	            	}
	            	else if (i.startsWith(":!rescheck") && line.split("\\s+")[3].equals(":!rescheck")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	            	{
	            		logger.trace("!rescheck command posted"); //$NON-NLS-1$
	            		// Connect to CQ2
	            		if (cq == null)
	            			cq = new CQ2(cq2User, cq2Pass);
	            		String user = line.split("\\s+")[4]; //$NON-NLS-1$
	            		cq.resCheck(user);
	            	}
	            	else if (i.startsWith(":!reveal") && line.split("\\s+")[3].equals(":!reveal")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	            	{
	            		try
	            		{
		            		String user = line.split("\\s+")[4]; //$NON-NLS-1$
		            		logger.trace("!reveal " + user + " posted");
		            		
		            		if (cq == null)
		            			cq = new CQ2(cq2User, cq2Pass);
		            		String x = cq.findReveal(user);
		            		
		            		if (x.equals(stringAccessor.getString("CQ2.db_error")))
		            		{
		            			writer.write("PRIVMSG " + channel + " :" + "WHOOPSIE, I CAN'T CONNECT TO THE DATABASE RIGHT NOW XD." + "\r\n");
		            			writer.flush();
		            		}
		            		else if (x.equals("") || x.equals(null))
		            		{
		            			writer.write("PRIVMSG " + channel + " :" + user + " COULD NOT BE FOUND XD." + "\r\n");
		            			writer.flush();	            			
		            		}
		            		else
		            		{
		            			writer.write("PRIVMSG " + channel + " :" + stringAccessor.getString("CQ2.website") + stringAccessor.getString("CQ2.FindReveals") + x + "\r\n");
		            			writer.flush();
		            		}
	            		}
	            		catch (ArrayIndexOutOfBoundsException ex)
	            		{
	            			System.out.println(ex);
	            			writer.write("PRIVMSG " + channel + " :" + "ENTER A VALID USER XD" + "\r\n");
	            			writer.flush();	           	            			
	            		}
	            		catch (Exception ex)
	            		{
	            			System.out.println(ex);
	            			writer.write("PRIVMSG " + channel + " :" + "THAT COMMAND CAUSED AN ERROR XD CHECK LOGS." + "\r\n");
	            			writer.flush();	           	  
	            		}
	            		
	            	}
	            	else if (i.startsWith(":!shard") && line.split("\\s+")[3].equals(":!shard"))
	            	{
	            		int size = line.split("\\s+").length;
	            		String lineArray[] = line.split("\\s+");
	            		try
	            		{
	            			String shard = "";
	            			for (int j = 4; j < size; j++)
	            			{
	            				shard += lineArray[j] + " ";
	            			}
	            			shard = shard.trim();
	            			if (cq == null)
	            				cq = new CQ2(cq2User, cq2Pass);
	            			
	            			ArrayList<String> users = cq.findShard(shard);
	            			if (users == null || users.isEmpty())
	            			{
		            			writer.write("PRIVMSG " + channel + " :" + "NOBODY HAS THAT SHARD XD" + "\r\n");
		            			writer.flush();	   	            				
	            			}
	            			else
	            			{
		            			writer.write("PRIVMSG " + channel + " :" + users + "\r\n");
		            			writer.flush();	   
	            			}
	            		}
	            		catch (ArrayIndexOutOfBoundsException ex)
	            		{
	            			System.out.println(ex);
	            		}
	            		catch (Exception ex)
	            		{
	            			System.out.println(ex);
	            		}
	            	}

	            }
	            
	        }
		} catch (Exception e) {
			logger.trace(e.getMessage());
		}
	
	}
}