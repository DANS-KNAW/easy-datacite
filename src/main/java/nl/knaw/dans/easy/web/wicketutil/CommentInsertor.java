/**
 *
 */
package nl.knaw.dans.easy.web.wicketutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @author Eko Indarto
 */
public class CommentInsertor
{
    private static int counterHtmlFile;
    private static int counterFileModified;

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        String dirname = "/Users/akmi/Documents/workspace4/other/easy";
        System.out.println("BEGIN");// NOPMD
        File dirfile = new File(dirname);
        File[] files = dirfile.listFiles();
        getContent(files);
        System.out.println("\nTOTAL HTML file: " + counterHtmlFile + " files");// NOPMD
        System.out.println("TOTAL file has been modified: " + counterFileModified + " files");// NOPMD
        System.out.println("END");// NOPMD
    }

    private static void getContent(File[] files)
    {
        int len = files.length;

        for (int i = 0; i < len; i++)
        {
            String name = files[i].getName();
            if (files[i].isDirectory())
            {
                // System.out.println("DIRECTORY NAME: " + name);// NOPMD
                getContent(files[i].listFiles());
            }
            else
            {
                if (name.endsWith(".html"))
                {
                    counterHtmlFile++;
                    System.out.println("Modified File: " + name);// NOPMD
                    modifyContentFile(files[i], "<!--  Begin of " + name + " -->", "<!--  End of " + name + " -->");
                }
            }
        }

    }

    public static void modifyContentFile(File inFile, String beginLineToBeInserted, String endLineToBeInserted)
    {
        // temp file
        File outFile = new File("$$$$$$$$.tmp");
        try
        {
            // input
            FileInputStream fis = new FileInputStream(inFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            // output
            FileOutputStream fos = new FileOutputStream(outFile);
            PrintWriter out = new PrintWriter(fos);

            String thisLine = "";
            while ((thisLine = in.readLine()) != null)
            {
                if (thisLine.startsWith("<wicket:extend>") || thisLine.startsWith("<wicket:panel>"))
                {
                    String nextLine = in.readLine();
                    if (nextLine != null && nextLine.startsWith(beginLineToBeInserted))
                    { // remove comment
                        System.out.println("REMOVING (begin) COMMENT from " + inFile.getName());// NOPMD
                        out.println(thisLine);
                        nextLine = in.readLine();// skip
                        thisLine = nextLine;
                        out.println(thisLine);
                    }
                    else
                    { // insert comment
                        System.out.println("ADDING (begin) COMMENT to " + inFile.getName());// NOPMD
                        out.println(thisLine);
                        out.println(beginLineToBeInserted);
                        out.println(nextLine);
                    }
                    counterFileModified++;
                }
                else if (thisLine.startsWith(endLineToBeInserted))
                {
                    System.out.println("REMOVING (end) COMMENT from " + inFile.getName());// NOPMD
                    // removing end comment, skip this line and add the nextline.
                    String nextLine = in.readLine();
                    out.println(nextLine);
                }
                else if (thisLine.startsWith("</wicket:extend>") || thisLine.startsWith("</wicket:panel>"))
                {
                    // insert comment
                    System.out.println("ADDING (end) COMMENT to " + inFile.getName());// NOPMD
                    out.println(endLineToBeInserted);
                    out.println(thisLine);
                }
                else
                {
                    out.println(thisLine);
                    //                    previousLine = thisLine;// remember the previous line for the last line.
                }

            }

            out.flush();
            out.close();
            in.close();

            boolean deleted = inFile.delete();
            boolean renamed = outFile.renameTo(inFile);
            if (!deleted)
                System.out.println("Delete file is failed.");// NOPMD
            if (!renamed)
                System.out.println("Rename file is failed.");// NOPMD
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
