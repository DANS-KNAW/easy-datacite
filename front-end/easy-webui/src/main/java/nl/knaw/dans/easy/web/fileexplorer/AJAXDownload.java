package nl.knaw.dans.easy.web.fileexplorer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.knaw.dans.easy.domain.download.FileContentWrapper;
import nl.knaw.dans.easy.domain.download.ZipFileContentWrapper;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AJAXDownload extends AbstractAjaxBehavior {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(AJAXDownload.class);

    /**
     * Call this method to initiate the download.
     */
    public void initiate(AjaxRequestTarget target) {
        CharSequence url = getCallbackUrl();

        target.appendJavascript("window.location.href='" + url + "'");
    }

    @Override
    public void onRequest() {
        getComponent().getRequestCycle().setRequestTarget(createTarget());
    }

    private ResourceStreamRequestTarget createTarget() {
        return new ResourceStreamRequestTarget(getResourceStream(), getFileName()) {
            @Override
            public void detach(RequestCycle requestCycle) {
                super.detach(requestCycle);
                detachResourceStream();
            }
        };
    }

    /**
     * Hook method to remove a temporary file created for the download.
     */
    protected void detachResourceStream() {}

    /**
     * @see ResourceStreamRequestTarget#getFileName()
     */
    protected String getFileName() {
        return null;
    }

    /**
     * Hook method providing the actual resource stream.
     */
    protected abstract IResourceStream getResourceStream();

    public static AJAXDownload create(final FileContentWrapper fcw) {
        return new AJAXDownload() {
            private static final long serialVersionUID = 1L;

            @Override
            protected IResourceStream getResourceStream() {
                return new UrlResourceStream(fcw.getURL());
            }

            @Override
            protected String getFileName() {
                return fcw.getFileName();
            }

            @Override
            public void onRequest() {
                super.onRequest();
                try {
                    ((WebResponse) getComponent().getResponse()).setHeader("Link",
                            "<https://easy.dans.knaw.nl/ui/datasets/id/" + URLEncoder.encode(fcw.getDatasetId(), "UTF-8") + "> ; rel = \"collection\"");
                }
                catch (UnsupportedEncodingException e) {
                    logger.error("could not encode the dataset identifier of dataset " + fcw.getDatasetId());
                }
            }
        };
    }

    public static AJAXDownload create(final ZipFileContentWrapper zfcw) {
        return new AJAXDownload() {
            private static final long serialVersionUID = 1L;

            @Override
            protected IResourceStream getResourceStream() {
                return new FileResourceStream(zfcw.getZipFile());
            }

            @Override
            protected String getFileName() {
                return zfcw.getFilename();
            }

            @Override
            protected void detachResourceStream() {
                try {
                    zfcw.deleteZipFile();
                }
                catch (IOException e) {
                    // already logged, no reason to bother the client
                }
            }

            @Override
            public void onRequest() {
                super.onRequest();
                try {
                    ((WebResponse) getComponent().getResponse()).setHeader("Link",
                            "<https://easy.dans.knaw.nl/ui/datasets/id/" + URLEncoder.encode(zfcw.getDatasetId(), "UTF-8") + "> ; rel = \"collection\"");
                }
                catch (UnsupportedEncodingException e) {
                    logger.error("could not encode the dataset identifier of dataset " + zfcw.getDatasetId());
                }
            }
        };
    }
}
