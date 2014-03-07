package nl.knaw.dans.common.lang.pdf;

import static com.lowagie.text.pdf.ColumnText.showTextAligned;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import nl.knaw.dans.common.lang.mail.ApplicationMailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class PdfPageLayouter extends PdfPageEventHelper
{

    private static final Logger logger = LoggerFactory.getLogger(ApplicationMailer.class);

    private static final int PAGE_NUMBER_TABLE_WIDTH = 100;
    private static final int HEADER_PADDING = 40;
    private static final int HEADER_POSITION = -10;
    private static final int FOOTER_POSITION = 10;
    private static final int MARGIN_BOTTOM = 72;
    private static final int MARGIN_TOP = 64;
    private static final int MARGIN_LEFT = 36;
    private static final int MARGIN_RIGHT = 36;

    private static final Color BLACK = new Color(0, 0, 0, 64);
    private static final Color BLUE = new Color(0, 0, 255, 64);

    private static final Font FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, BLACK);
    private static final Font AFONT = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, BLUE);

    private static final Phrase TO_FIRST = createPageAction("<<", PdfAction.FIRSTPAGE);
    private static final Phrase TO_PREV = createPageAction("<", PdfAction.PREVPAGE);
    private static final Phrase TO_NEXT = createPageAction(">", PdfAction.NEXTPAGE);
    private static final Phrase TO_LAST = createPageAction(">>", PdfAction.LASTPAGE);

    private Phrase footerPhrase;
    private Image headerImage;

    public class HeaderImageException extends Exception
    {
        private static final long serialVersionUID = 1L;

        // anyone can catch, only owner can throw
        private HeaderImageException(final String id, final Throwable cause)
        {
            super(id, cause);
        }
    }

    /**
     * Usage example
     *
     * <pre>
     *   final Document document = new Document();
     *   final PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("doc.pdf"));
     *   writer.setPageEvent(new PdfPageLayouter(document,"footer", new URL("logo.png")));
     * </pre>
     *
     * @param document
     * @param footerText optional text above the page number in the footer of the page
     * @param headerImage optional image for the page header
     * @throws HeaderImageException in case of problems with the image
     */
    public PdfPageLayouter(final Document document, final String footerText, final URL headerImage) throws HeaderImageException
    {
        setFooterText(footerText);
        document.setMargins(MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, MARGIN_BOTTOM);
        setHeaderImage(headerImage);
    }

    public void onStartPage(final PdfWriter writer, final Document document)
    {
        if (headerImage == null)
            return;

        final float top = document.top();
        final PdfContentByte canvas = writer.getDirectContent();
        float resizeFactor = // TODO make calculation logic clearer
        headerImage.getHeight() / (MARGIN_TOP - HEADER_PADDING);
        final float width = headerImage.getWidth() / resizeFactor;
        final float height = headerImage.getHeight() / resizeFactor;

        try
        {
            canvas.addImage(headerImage, width, 0, 0, height, MARGIN_LEFT, top - HEADER_POSITION);
        }
        catch (final Exception cause)
        {
            logger.error("can't add header image to PDF page " + document.getPageNumber(), cause);
        }

    }

    public void onEndPage(final PdfWriter writer, final Document document)
    {
        final float bottom = document.bottom();
        final float centerX = getCenterX(document);
        final PdfContentByte canvas = writer.getDirectContent();
        final int rotation = 0;

        final PdfPTable table = new PdfPTable(5);
        table.setTotalWidth(PAGE_NUMBER_TABLE_WIDTH);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setBorder(0);

        table.addCell(TO_FIRST);
        table.addCell(TO_PREV);
        table.addCell(new Phrase(new Chunk("" + document.getPageNumber(), FONT)));
        table.addCell(TO_NEXT);
        table.addCell(TO_LAST);

        table.writeSelectedRows(0, -1, centerX - (table.getTotalWidth() / 2), bottom - FOOTER_POSITION, canvas);
        if (footerPhrase == null)
            return;
        showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase(footerPhrase), centerX, bottom - FOOTER_POSITION, rotation);
    }

    public void setHeaderImage(final URL url) throws HeaderImageException
    {
        if (url == null)
        {
            headerImage = null;
            return;
        }
        try
        {
            headerImage = Image.getInstance(url);
        }
        catch (final BadElementException e)
        {
            throw new HeaderImageException(url.toString(), e);
        }
        catch (final MalformedURLException e)
        {
            throw new HeaderImageException(url.toString(), e);
        }
        catch (final IOException e)
        {
            throw new HeaderImageException(url.toString(), e);
        }
    }

    public void setFooterText(final String value)
    {
        if (value == null)
        {
            footerPhrase = null;
            return;
        }
        footerPhrase = new Phrase(new Chunk(value, FONT));
    }

    private static Phrase createPageAction(final String caption, final int action)
    {
        return new Phrase(new Chunk(caption, AFONT).setAction(new PdfAction(action)));
    }

    private float getCenterX(final Document document)
    {
        return (document.right() - document.left()) / 2 + document.leftMargin();
    }
}
