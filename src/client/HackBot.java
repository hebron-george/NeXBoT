package client;

import client.commands.RedditLink;
import client.commands.YoutubeLink;

import java.io.*;
import java.io.FileInputStream;

import java.net.*;
import java.util.Properties;

public class HackBot implements Runnable {

    private static Socket socket;
    private static BufferedWriter writer;
    private static BufferedReader reader;

    protected HackBot() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting program.");

        try {
            new HackBot().start();
            System.out.println("After Start");
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void start() throws Exception {
        Properties prop = new Properties();
        InputStream in = null;
        in = new FileInputStream(System.getProperty("user.dir") + "/" + "config.properties");

        prop.load(in);

        String server = prop.getProperty("HackBot.server");
        String nick = prop.getProperty("HackBot.nick");
        String login = prop.getProperty("HackBot.user");
        String channels[] = prop.getProperty("HackBot.channels").split(",");

        System.out.println("Attempting connection (" + server + ") ... ");
        HackBot.socket = new Socket(server, 6667);
        System.out.println("Socket Initialized.");
        HackBot.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        System.out.println("Writer Initialized.");
        HackBot.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Reader Initialized.");

        // Log on to the server.
        writer.write("NICK " + nick + "\r\n");
        writer.write("USER " + login + " 8 * : Java IRC Hacks Bot\r\n");
        writer.flush();

        // Read lines from the server until it tells us we have connected.
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (line.indexOf("004") >= 0) {
                // We are now logged in.
                break;
            } else if (line.indexOf("433") >= 0) {
                System.out.println("Nickname is already in use.");
                return;
            }

            if (line.startsWith("PING ")) {
                // We must respond to PINGs to avoid being disconnected.
                writer.write("PONG " + line.substring(5) + "\r\n");
                System.out.println("PONG " + line.substring(5) + "\r\n");
                writer.flush();
            }
        }


        // Join the channel.
        for (String channel : channels) {
            System.out.println("JOIN " + channel + "\r\n");
            writer.write("JOIN " + channel + "\r\n");
            writer.flush();
        }

        new Thread(this).start();
    }

    public void run() {
        String line, channel = "";
        try {
            while ((line = reader.readLine()) != null) {
                // Print the raw line received by the bot.
                System.out.println(line);

                if (line.startsWith("PING ")) {
                    // We must respond to PINGs to avoid being disconnected.
                    writer.write("PONG " + line.substring(5) + "\r\n");
                    writer.flush();
                } else {

                    for (String i : line.split(" "))
                    {
                        if (i.startsWith(":http://www.youtube.com/watch?v=") || i.startsWith(":https://www.youtube.com/watch?v=")
                                || i.startsWith("http://www.youtube.com/watch?v=") || i.startsWith("https://www.youtube.com/watch?v=")) //$NON-NLS-1$ //$NON-NLS-2$
                        {
                            channel = getChannel(line);
                            YoutubeLink y = null;
                            if (i.charAt(0) == ':')
                                y = new YoutubeLink(i.substring(1));
                            else
                                y = new YoutubeLink(i);
                            writer.write("PRIVMSG " + channel + " :" + y.summary() + "\r\n");
                            writer.flush();
                        } else if (i.startsWith(":http://www.reddit.com/") || i.startsWith(":https://www.reddit.com/")
                                || i.startsWith("http://www.reddit.com/") || i.startsWith("https://www.reddit.com/")) {
                            channel = getChannel(line);
                            RedditLink r;
                            if (i.charAt(0) == ':')
                                r = new RedditLink(i.substring(1));
                            else
                                r = new RedditLink(i);
                            writer.write("PRIVMSG " + channel + " :" + r.summary() + "\r\n");
                            writer.flush();
                        }
                    }
                }
            }
        } catch (SocketException se) {
            se.printStackTrace();
            System.out.println(se.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    private String getChannel(String line) {
        try {
            String portions[] = line.split(" ");
            if (portions[2].startsWith("#"))
                return portions[2];
        } catch (IndexOutOfBoundsException ex) {
            //Not a channel message
            return null;
        } catch (Exception ex) {
            //Unexpected Exception
            System.out.println(ex + " - " + ex.getMessage());
            return null;
        }

        return null;
    }
}