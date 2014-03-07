package nl.knaw.dans.easy.business.dataset;

import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Small test to ask questions on iText mailing list see
 * http://news.gmane.org/gmane.comp.java.lib.itext.general
 * http://article.gmane.org/gmane.comp.java.lib.itext.general/50972
 */
public class TinyTable
{
    private static final String HTML = "<table>" + "<tr><td>a</td><td colspan='2'>b</td></tr>" + "<tr><td>X</td><td colspan='2'>Y</td></tr>" + "</table>";

    public static void main(final String[] args)
    {
        try
        {
            final Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("target/TestOutputTinyTable.pdf"));
            document.open();
            final ArrayList<Element> elements = HTMLWorker.parseToList(new StringReader(HTML), createStyles());
            for (final Element element : elements)
                document.add(element);
            document.close();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    private static StyleSheet createStyles()
    {
        final StyleSheet styles = new StyleSheet();
        styles.loadTagStyle("td", "valign", "top");
        return styles;
    }
}
