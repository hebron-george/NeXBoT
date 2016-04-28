package client;

import client.commands.YoutubeLink;

import java.io.BufferedWriter;
import java.io.IOException;

class LineHandler implements  Runnable {
/***
 * This is a thread focused on parsing a line
 * to potentially perform a task
 *
 * This extension will also collect some stats
 *
 * http://stackoverflow.com/a/877113/1496918
 */
    private String line;
    private String fullLine;
    private BufferedWriter writer;
    LineHandler(String fullLine, String line, BufferedWriter writer){
        System.out.println("Creating LineHandler Thread ");
        this.line = line;
        this.writer = writer;
        this.fullLine = fullLine;
    }
    public void run(){
        System.out.println("Starting LineHandler Thread ");
        System.out.println("Line: " + line);
        if (line.trim().isEmpty()){
            System.out.println("Line is empty ");
        }
        else if (line.startsWith(":http://www.youtube.com/watch?v=") || line.startsWith(":https://www.youtube.com/watch?v=")
                || line.startsWith("http://www.youtube.com/watch?v=") || line.startsWith("https://www.youtube.com/watch?v=")) {
            String channel = getChannel(fullLine);
            System.out.println("Got Channel ");
            YoutubeLink y;
            if (line.charAt(0) == ':')
                y = new YoutubeLink(line.substring(1));
            else
                y = new YoutubeLink(line);
            try {
                System.out.println("Going to try printing ");
                writer.write("PRIVMSG " + channel + " :" + y.summary() + "\r\n");
                writer.flush();
                System.out.println("Channel: " + channel);
                System.out.println("Summary: " + y.summary());
                System.out.println("Did it print? ");
            } catch (IOException ex) {
                System.out.println("Exception in LineHandler Thread");
                System.out.println("line=" + line);
                System.out.println(ex.getMessage());
            }

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
