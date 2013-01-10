package nl.knaw.dans.easy.servicelayer;

import static nl.knaw.dans.common.lang.pdf.ExtendedHtmlWorker.parseToList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.mail.ApplicationMailer;
import nl.knaw.dans.common.lang.mail.MailComposer;
import nl.knaw.dans.common.lang.mail.MailComposerException;
import nl.knaw.dans.common.lang.pdf.PdfPageLayouter;
import nl.knaw.dans.common.lang.pdf.PdfPageLayouter.HeaderImageException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.Term;
import nl.knaw.dans.easy.domain.model.emd.types.IsoDate;
import nl.knaw.dans.easy.domain.model.emd.types.MetadataItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfWriter;

public class LicenseComposer
{
    private static final Logger logger = LoggerFactory.getLogger(ApplicationMailer.class);

    public static class LicenseComposerException extends Exception
    {
        private static final long serialVersionUID = 1L;

        // anyone can catch, only owner can throw
        private LicenseComposerException(final String identifyingMessage, final Throwable cause)
        {
            super(identifyingMessage, cause);
            logger.error(identifyingMessage, cause);
        }

        public LicenseComposerException(final Throwable cause)
        {
            super(cause);
            logger.error("", cause);
        }
    }

    public class DatasetDates
    {
        private static final String DATE_FORMAT = "YYYY-MM-dd";

        public String getDateSubmitted()
        {
            if (generateSample)
                return new IsoDate().getValue().toString(DATE_FORMAT);
            return dataset.getDateSubmitted().getValue().toString(DATE_FORMAT);
        }

        public String getDateAvailable()
        {
            return dataset.getDateAvailable().toString(DATE_FORMAT);
        }
    }

    private static final String SNIPPET_FOLDER = "/license/templates/";

    private static final String LOGO = "/license/images/dans_logo.jpg";

    private static final String TERM_PROPERTIES = "MetadataTerms.properties";

    private final Map<Enum, String> snippets = new HashMap<Enum, String>();

    private static enum SnippetKey
    {
        body, embargo, tail, version
    };

    private final URL headerImage;

    private final HashMap parserProperties;

    private final Properties metadataNames;

    private final StyleSheet parserStyles;

    private final MailComposer composer;

    private final Dataset dataset;

    private final boolean generateSample;

    public static final int ESTIMATED_PDF_SIZE = 70 * 1024;

    /**
     * @param depositor
     * @param dataset
     * @param generateSample
     *        so we don't yet have/create a dateSubmitted
     * @throws LicenseComposerException
     * @throws MalformedURLException
     */
    public LicenseComposer(final EasyUser depositor, final Dataset dataset, final boolean generateSample) throws LicenseComposerException
    {
        this.dataset = dataset;
        this.generateSample = generateSample;
        composer = new MailComposer(dataset, depositor, new DatasetDates(), new DateTime().toString("YYYY-MM-dd HH:mm:ss"));
        headerImage = getLogoUrl();
        metadataNames = loadMetadataProperties();
        parserStyles = createStyles();
        parserProperties = null;

        final String other = getSnippetContent("OtherAccess.html");
        snippets.put(SnippetKey.version, getSnippetContent("LicenseVersion.txt"));
        final String body = generateSample ? "Body-sample.html" : "Body.html";
        snippets.put(SnippetKey.body, getSnippetContent(body));
        snippets.put(SnippetKey.embargo, getSnippetContent("Embargo.html"));
        snippets.put(SnippetKey.tail, getSnippetContent("Tail.html"));
        snippets.put(AccessCategory.ANONYMOUS_ACCESS, other);
        snippets.put(AccessCategory.OPEN_ACCESS, getSnippetContent("OpenAccess.html"));
        snippets.put(AccessCategory.GROUP_ACCESS, getSnippetContent("RestrictGroup.html"));
        snippets.put(AccessCategory.REQUEST_PERMISSION, getSnippetContent("RestrictRequest.html"));
        snippets.put(AccessCategory.ACCESS_ELSEWHERE, other);
        snippets.put(AccessCategory.NO_ACCESS, other);
        snippets.put(AccessCategory.FREELY_AVAILABLE, other);
        if (depositor == null || dataset == null)
            throw new NullPointerException();
    }

    private URL getLogoUrl() throws LicenseComposerException
    {
        return ResourceLocator.getURL(LOGO);
    }

    public void createPdf(final OutputStream outputStream) throws LicenseComposerException
    {
        final Document document = new Document();
        try
        {
            final PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            writer.setPageEvent(new PdfPageLayouter(document, snippets.get(SnippetKey.version), headerImage));
            createContent(document);
        }
        catch (final DocumentException exception)
        {
            throw new LicenseComposerException("can not assemble license document", exception);
        }
        catch (final HeaderImageException exception)
        {
            throw new LicenseComposerException("can not add logo to license document", exception);
        }
    }

    public void createHtml(final OutputStream outputStream) throws LicenseComposerException
    {
        final Document document = new Document();
        try
        {
            HtmlWriter.getInstance(document, outputStream);
            createContent(document);
        }
        catch (final DocumentException exception)
        {
            throw new LicenseComposerException("can not assemble license document", exception);
        }
    }

    private void createContent(final Document document) throws LicenseComposerException, DocumentException
    {
        document.open();
        copyHtml(document, compose(SnippetKey.body));
        copyHtml(document, snippets.get(dataset.getAccessCategory()));
        if (dataset.isUnderEmbargo())
        {
            // TODO als gegenereerd na verstrijken zie je niet dat er ooit een embargo op zat
            copyHtml(document, compose(SnippetKey.embargo));
        }
        copyHtml(document, compose(SnippetKey.tail));
        document.add(formatMetaData(document));
        foramtUploadedFileNames(document);
        document.close();
    }

    private void foramtUploadedFileNames(final Document document) throws DocumentException, LicenseComposerException
    {
        final List<String> fileNames = getDatasetFileNames(dataset.getDmoStoreId());
        if (fileNames == null || fileNames.size() == 0)
        {
            document.add(new Paragraph("No uploaded files."));
            return;
        }
        final Paragraph paragraph = new Paragraph("Uploaded files:");
        paragraph.setSpacingAfter(3);
        document.add(paragraph);
        for (final String name : fileNames)
        {
            document.add(new Paragraph(name));
        }
    }

    protected Element formatMetaData(final Document document) throws LicenseComposerException
    {
        Table table;
        try
        {
            table = new Table(2);
        }
        catch (final BadElementException e)
        {
            throw new LicenseComposerException(e);
        }
        table.getDefaultCell();
        table.setPadding(3);
        // table.getDefaultCell().setMinimumHeight(22);
        // table.setSpacingBefore(4);
        // table.setWidthPercentage(100f);
        // table.setSplitRows(false);
        final EasyMetadata easyMetadata = dataset.getEasyMetadata();
        for (final Term term : easyMetadata.getTerms())
        {
            final List<MetadataItem> items = easyMetadata.getTerm(term);
            if (items.size() > 0)
            {
                final String name = term.getQualifiedName();
                try
                {
                    table.addCell(metadataNames.getProperty(name, name));
                }
                catch (final BadElementException e)
                {
                    throw new LicenseComposerException(e);
                }

                String string = "";

                // write exceptions to just putting the hard emd values
                // in the licence here
                if (term.getName().equals(Term.Name.AUDIENCE))
                {
                    string = formatAudience(easyMetadata);
                    try
                    {
                        table.addCell(string);
                    }
                    catch (final BadElementException e)
                    {
                        throw new LicenseComposerException(e);
                    }
                }
                else if (term.getName().equals(Term.Name.ACCESSRIGHTS))
                {
                    final MetadataItem item = items.get(0); // was non empty!

                    string = formatAccesRights(item);

                    try
                    {
                        table.addCell(string);
                    }
                    catch (final BadElementException e)
                    {
                        throw new LicenseComposerException(e);
                    }
                }
                else
                {
                    // generic approach for metadata items
                    string = Arrays.deepToString(items.toArray());
                    try
                    {
                        table.addCell(string.substring(1, string.length() - 1));
                    }
                    catch (final BadElementException e)
                    {
                        throw new LicenseComposerException(e);
                    }
                }
            }
        }
        return table;
    }

    protected String formatAccesRights(final MetadataItem item)
    {
        String accesRights = "";

        // AccessCategory cat = AccessCategory.valueOf(item.toString());
        final String categoryString = item.toString();

        // TODO use properties file for mapping these metadata values

        if (categoryString.equals("ANONYMOUS_ACCESS"))
        {
            accesRights = "Anonymous";
        }
        else if (categoryString.equals("OPEN_ACCESS"))
        {
            accesRights = "Open";
        }
        else if (categoryString.equals("GROUP_ACCESS"))
        {
            accesRights = "Restricted -'archaeology' group";
        }
        else if (categoryString.equals("REQUEST_PERMISSION"))
        {
            accesRights = "Restricted -request permission";
        }
        else if (categoryString.equals("ACCESS_ELSEWHERE"))
        {
            accesRights = "Elsewhere";
        }
        else if (categoryString.equals("NO_ACCESS"))
        {
            accesRights = "Other";
        }
        else
        {
            logger.warn("No available mapping; using acces category value directly");
            accesRights = categoryString;
        }

        return accesRights;
    }

    public static String formatAudience(final EasyMetadata easyMetadata) throws LicenseComposerException
    {
        final DisciplineCollectionService disciplineService = Services.getDisciplineService();
        if (disciplineService == null)
            throw new LicenseComposerException("discipline service not configured", null);

        StringBuffer string = new StringBuffer();
        for (String sid : easyMetadata.getEmdAudience().getValues())
        {
            try
            {
                string.append(", ");
                string.append(disciplineService.getDisciplineById(new DmoStoreId(sid)).getName());
            }
            catch (final ObjectNotFoundException e)
            {
                throw new LicenseComposerException("discipline not found: " + sid, e);
            }
            catch (final ServiceException e)
            {
                throw new LicenseComposerException("discipline service error: " + e.getMessage(), e);
            }
        }
        return string.substring(2);
    }

    private String compose(final SnippetKey snippet) throws LicenseComposerException
    {
        try
        {
            final byte[] bytes = snippets.get(snippet).getBytes();
            return composer.compose(new ByteArrayInputStream(bytes));
        }
        catch (final MailComposerException exception)
        {
            throw new LicenseComposerException("can not compose " + snippet + "; " + exception.getMessage(), exception);
        }
    }

    @SuppressWarnings("unchecked")
    protected void copyHtml(final Document document, final String snippet) throws LicenseComposerException
    {
        final StringReader reader = new StringReader(snippet);
        try
        {
            final ArrayList<Element> elements = parseToList(reader, parserStyles, parserProperties);
            for (final Element element : elements)
                document.add(element);
        }
        catch (final IOException exception)
        {
            throw new LicenseComposerException("can not parse license snippet", exception);
        }
        catch (final DocumentException exception)
        {
            throw new LicenseComposerException("can not convert license snippet", exception);
        }
    }

    private static StyleSheet createStyles()
    {
        final StyleSheet styles = new StyleSheet();
        styles.loadTagStyle("a", "color", "blue");
        styles.loadTagStyle("td", "valign", "top");
        return styles;
    }

    private static List<String> getDatasetFileNames(final DmoStoreId sid) throws LicenseComposerException
    {
        try
        {
            return Data.getFileStoreAccess().getFilenames(sid, true);
        }
        catch (final StoreAccessException e)
        {
            throw new LicenseComposerException("can not find uploaded filenames of dataset", e);
        }
    }

    private File getSnippetFile(final String fileName) throws ResourceNotFoundException
    {
        return ResourceLocator.getFile(SNIPPET_FOLDER + fileName);
    }

    private String getSnippetContent(final String fileName) throws LicenseComposerException
    {
        try
        {
            return FileUtils.readFileToString(getSnippetFile(fileName));
        }
        catch (final IOException exception)
        {
            throw new LicenseComposerException("can not read license snippet: " + fileName, exception);
        }
        catch (final ResourceNotFoundException exception)
        {
            throw new LicenseComposerException("can not find license snippet: " + fileName, exception);
        }
    }

    private Properties loadMetadataProperties()
    {
        final Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream(getSnippetFile(TERM_PROPERTIES)));
        }
        catch (final IOException exception)
        {
            logger.error("could not read names of meta data terms", exception);
        }
        catch (final ResourceNotFoundException exception)
        {
            logger.error("could not find file with names of meta data terms", exception);
        }
        return properties;
    }
}
