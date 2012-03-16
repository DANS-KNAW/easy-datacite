package nl.knaw.dans.easy.web.doc;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceListCache;
import nl.knaw.dans.easy.domain.form.FormDescriptorLoader;
import nl.knaw.dans.easy.web.editabletexts.EasyEditablePanel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.file.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmdDocPage extends AbstractEasyNavPage
{
    private static final String                       CHOICE_LIST_XSL      = "choicelist.xsl";
    private static final String                       META_DATA_FORMAT_XSL = "metaDataFormat.xsl";

    private transient static Map<String, Transformer> transformers;
    private static final Logger                       logger               = LoggerFactory.getLogger(EmdDocPage.class);

    public EmdDocPage(final PageParameters parameters) throws URISyntaxException, TransformerException, TransformerFactoryConfigurationError,
            MalformedURLException
    {
        super(parameters);
        add(new EasyEditablePanel("intro", "/editable/emdIntro.template"));
        add(createTabbedPanel("format", createFormatTabs()));
        add(createTabbedPanel("translation", createTranslationTabs()));
    }

    private TabbedPanel createTabbedPanel(final String wicketId, final List<ITab> tabs)
    {
        final TabbedPanel tabbedPanel = new TabbedPanel(wicketId, tabs){
            private static final long serialVersionUID = 1L;

            @Override
            protected WebMarkupContainer newLink(final String linkId, final int index)
            {
                final PageParameters params = new PageParameters();
                params.add(wicketId, tabs.get(index).getTitle().getObject());
                return new BookmarkablePageLink<String>(linkId, EmdDocPage.class, params);
            }
        };
        setSelectedTab(wicketId, tabs, tabbedPanel);
        return tabbedPanel;
    }

    /** counterpart of the {@link BookmarkablePageLink} in {@link #createTabbedPanel(String, List)  */
    private void setSelectedTab(final String wicketId, final List<ITab> tabs, final TabbedPanel tabbedPanel)
    {
        final String linkId = getPageParameters().getString(wicketId);
        for (int i=0;i<tabs.size();i++){
            if (tabs.get(i).getTitle().getObject().equals(linkId)){
                tabbedPanel.setSelectedTab(i);
            }
        }
    }

    private List<ITab> createTranslationTabs() throws URISyntaxException, MalformedURLException
    {
        final List<ITab> tabs = new ArrayList<ITab>();
        final URI uri = ResourceLocator.getURL(ChoiceListCache.BASE_FOLDER).toURI();
        final String replace = uri.toString().replace("file:", "");
        for (final File file : new Folder(uri).getNestedFiles())
        {
            if (file.getName().toLowerCase().endsWith(".xml"))
            {
                final String title = file.getPath().replace(replace, "").replace(".xml", "").replaceAll("/", ".");
                final String content = transform(file.toURI().toURL(), CHOICE_LIST_XSL);
                tabs.add(createTab("translation", title, "Translation table: "+title, content));
            }
        }
        return tabs;
    }

    private List<ITab> createFormatTabs() throws URISyntaxException, MalformedURLException
    {
        final List<ITab> tabs = new ArrayList<ITab>();
        final String path = FormDescriptorLoader.class.getResource(FormDescriptorLoader.FORM_DESCRIPTIONS).getPath();
        for (final File file : new Folder(path).getNestedFiles())
        {
            final String title = file.getName().toUpperCase().replace(".XML", "");
            final String content = transform(file.toURI().toURL(), META_DATA_FORMAT_XSL);
            tabs.add(createTab("format", title, "Easy-II Meta Data Format: "+title, content));
        }
        return tabs;
    }

    private AbstractTab createTab(final String group, final String tabLinkTitle, final String contentTitle, final String content)
    {
        return (AbstractTab) new AbstractTab(new Model<String>(tabLinkTitle))
        {
            private static final long serialVersionUID = 1L;

            public Panel getPanel(final String panelId)
            {
                return new TitledPanel(panelId, contentTitle,content);
            }
        };
    }

    private String transform(final URL url, final String transformerKey)
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final Transformer transformer = getTransformer(transformerKey);
        final URI uri;
        if (url == null)
            return "???";
        if (transformer == null)
            return transformerKey + " not loaded";
        try
        {
            uri = url.toURI();
        }
        catch (final URISyntaxException e)
        {
            logger.equals(e);
            return (e.getMessage());
        }
        try
        {
            transformer.transform(new StreamSource(new File(uri)), new StreamResult(outputStream));
        }
        catch (final TransformerException e)
        {
            logger.equals(e);
            return (e.getMessage());
        }
        return outputStream.toString();
    }

    private static Transformer getTransformer(final String transformerKey)
    {
        if (transformers == null)
        {
            transformers = new HashMap<String, Transformer>();
            for (final String key : new String[] {META_DATA_FORMAT_XSL, CHOICE_LIST_XSL})
            {
                try
                {
                    transformers.put(key, createTransformer(key));
                }
                catch (final TransformerException e)
                {
                    logger.equals(e);
                }
                catch (final TransformerFactoryConfigurationError e)
                {
                    logger.equals(e);
                }
                catch (final URISyntaxException e)
                {
                    logger.equals(e);
                }
            }
        }
        return transformers.get(transformerKey);
    }

    private static Transformer createTransformer(final String string) throws TransformerException, TransformerFactoryConfigurationError, URISyntaxException
    {
        String path = EmdDocPage.class.getResource(string).getPath();
        final StreamSource source = new StreamSource(new File(path));
        return TransformerFactory.newInstance().newTransformer(source);
    }
}
