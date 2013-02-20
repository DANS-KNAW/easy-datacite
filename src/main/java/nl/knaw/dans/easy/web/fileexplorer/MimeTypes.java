package nl.knaw.dans.easy.web.fileexplorer;

import java.util.HashMap;

// TODO: expand this

public class MimeTypes
{
    private final static HashMap<String, String> map = new HashMap<String, String>();

    static
    {
        map.put("image/bmp", "bmp");
        map.put("image/x-windows-bmp", "bmp");
        //map.put("", "clo");
        map.put("application/x-msdownload", "dll");
        map.put("application/msword", "doc");
        map.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        //map.put("", "fla");
        map.put("image/gif", "gif");
        map.put("text/html", "html");
        map.put("image/jpeg", "jpg");
        map.put("image/pjpeg", "jpg");
        map.put("application/x-msaccess", "mdb");
        map.put("application/pdf", "pdf");
        map.put("image/png", "png");
        map.put("application/mspowerpoint", "ppt");
        map.put("application/powerpoint", "ppt");
        map.put("application/vnd.ms-powerpoint", "ppt");
        map.put("application/x-mspowerpoint", "ppt");
        map.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");
        map.put("application/x-mspublisher", "pub");
        map.put("application/rtf", "rtf");
        map.put("application/x-rtf", "rtf");
        map.put("text/richtext", "rtf");
        map.put("text/rtf", "rtf");
        map.put("text/x-rtf", "rtf");
        //map.put("", "sav");
        //map.put("", "sct");
        //map.put("", "spo");
        //map.put("", "spp");
        //map.put("", "sps");
        map.put("image/tiff", "tif");
        map.put("image/x-tiff", "tif");
        //map.put("", "tlo");
        map.put("text/plain", "txt");
        map.put("application/plain", "txt");
        map.put("audio/wav", "wav");
        map.put("audio/x-wav", "wav");
        map.put("video/x-ms-wmv", "wmv");
        map.put("application/excel", "xls");
        map.put("application/vnd.ms-excel", "xls");
        map.put("application/x-excel", "xls");
        map.put("application/x-msexcel", "xls");
        map.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        map.put("application/xml", "xml");
        map.put("text/xml", "xml");
        map.put("application/xsl", "xsl");
        map.put("text/xsl", "xsl");
        map.put("application/x-compressed", "zip");
        map.put("application/x-zip-compressed", "zip");
        map.put("application/zip", "zip");
        map.put("multipart/x-zip", "zip");
    }

    public static String get(String mimeType)
    {
        return map.containsKey(mimeType) ? map.get(mimeType) : "unknown";
    }
}
