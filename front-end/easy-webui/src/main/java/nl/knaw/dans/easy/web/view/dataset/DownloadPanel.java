package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

public class DownloadPanel extends Panel {

    public static final String DOWNLOAD_XML = "download_xml";

    public static final String DOWNLOAD_CSV = "download_csv";

    private static final long serialVersionUID = 9110250938647271835L;

    private final EasyMetadata easyMetadata;

    private boolean initiated;

    public DownloadPanel(final String id, final EasyMetadata easyMetadata) {
        super(id);
        this.easyMetadata = easyMetadata;
    }

    @Override
    protected void onBeforeRender() {
        if (!initiated) {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init() {
        add(new ResourceLink(DOWNLOAD_XML, getXMLWebResource(easyMetadata)));
        add(new ResourceLink(DOWNLOAD_CSV, getCSVWebResource(easyMetadata)));

    }

    private WebResource getXMLWebResource(final EasyMetadata emd) {
        WebResource export = new WebResource() {

            private static final long serialVersionUID = 2114665554680463199L;

            @Override
            public IResourceStream getResourceStream() {
                CharSequence xml = null;
                try {
                    xml = new EmdMarshaller(emd).getXmlString();
                }
                catch (XMLSerializationException e) {
                    error(e.getMessage());
                }
                return new StringResourceStream(xml, "text/xml");
            }

            @Override
            protected void setHeaders(WebResponse response) {
                super.setHeaders(response);
                response.setAttachmentHeader(emd.getPreferredTitle() + ".xml");
            }
        };
        export.setCacheable(false);

        return export;
    }

    private WebResource getCSVWebResource(final EasyMetadata emd) {
        WebResource export = new WebResource() {

            private static final long serialVersionUID = 2534427934241209655L;

            @Override
            public IResourceStream getResourceStream() {
                return new StringResourceStream(emd.toString(";"), "text/csv");
            }

            @Override
            protected void setHeaders(WebResponse response) {
                super.setHeaders(response);
                response.setAttachmentHeader(emd.getPreferredTitle() + ".csv");
            }
        };
        export.setCacheable(false);
        return export;
    }

}
