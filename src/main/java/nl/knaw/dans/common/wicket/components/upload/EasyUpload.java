package nl.knaw.dans.common.wicket.components.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.command.EasyUploadCancelCommand;
import nl.knaw.dans.common.wicket.components.upload.command.EasyUploadStatusCommand;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;

import org.apache.wicket.AbortException;
import org.apache.wicket.Page;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.IPageLink;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lobo This panel provides an upload component with progressbar, cancel option, maximum file
 *         size detection, error handling, extensible postprocessing on a separate thread, unzipping and
 *         extensible javascript through event handling. To get this baby to work a few shared resources
 *         need to be registered by the wicket application (1) and the webrequest factory method on the
 *         wicket application object needs to be overriden (2). 1. Place this code on your
 *         WebApplication.init() method
 * 
 *         <pre>
 * EasyUploadStatusCommand uploadStatusResource = new EasyUploadStatusCommand();
 * uploadStatusResource.registerAsSharedResource(this);
 * EasyUploadCancelCommand uploadCancelResource = new EasyUploadCancelCommand();
 * uploadCancelResource.registerAsSharedResource(this);
 * </pre>
 * 
 *         2. Place this code on your WebApplication object
 * 
 *         <pre>
 * &#064;Override
 * protected WebRequest newWebRequest(HttpServletRequest servletRequest)
 * {
 *     return new EasyUploadWebRequest(servletRequest);
 * }
 * </pre>
 * 
 *         Override the onReceivedFiles method of this object to handle upload completion events. For
 *         more control create our own postprocessor and register it to this component through the
 *         registerPostProcessor method.
 */
public class EasyUpload extends Panel
{

    private static final long serialVersionUID = -1313908092922713274L;

    private static final Logger LOG = LoggerFactory.getLogger(EasyUpload.class);

    private InlineFrame uploadIFrame = null;

    private EasyUploadConfig config;

    /**
     * This is an event method that may be overwritten.
     * 
     * @param clientParams
     *        a hashmap of parameters received from the client-side
     * @param basePath
     *        the basePath in which the uploaded files are received
     * @param a
     *        list of files received
     */
    public void onReceivedFiles(Map<String, String> clientParams, String basePath, List<File> files)
    {
    }

    /**
     * Creates an EasyUpload wicket component based on a panel. The default tmp directory of the machine
     * will be used for writing the uploads to disk.
     * 
     * @param id
     *        the id of the wicket component.
     */
    public EasyUpload(String id)
    {
        this(id, new EasyUploadConfig());
    }

    /**
     * Creates an EasyUpload wicket component based on a panel.
     * 
     * @param id
     *        the id of the wicket component
     * @param basePath
     *        the path to which all uploads are written (directories will be created for each upload)
     */
    public EasyUpload(String id, String basePath)
    {
        this(id, new EasyUploadConfig(basePath));
    }

    /**
     * Creates an EasyUpload wicket component based on a panel. The default tmp directory of the machine
     * will be used for writing the uploads to disk.
     * 
     * @param id
     *        the id of the wicket component.
     */
    public EasyUpload(String id, EasyUploadConfig config)
    {
        super(id);

        this.setConfig(config);

        setOutputMarkupId(true);

        // add css
        add(CSSPackageResource.getHeaderContribution(new ResourceReference(EasyUpload.class, "EasyUpload.css")));

        // add javascript libraries
        add(JavascriptPackageResource.getHeaderContribution(new ResourceReference(EasyUpload.class, "js/lib/json2.js")));
        add(JavascriptPackageResource.getHeaderContribution(new ResourceReference(EasyUpload.class, "js/lib/jquery-1.3.2.min.js")));

        // add javascript
        add(JavascriptPackageResource.getHeaderContribution(new ResourceReference(EasyUpload.class, "js/EasyUpload.js")));

        // add UploadPanel javascript configuration (server to client)
        IModel variablesModel = new AbstractReadOnlyModel()
        {
            private static final long serialVersionUID = 7602363940615595891L;

            @SuppressWarnings("unchecked")
            public Map getObject()
            {
                Map<String, CharSequence> variables = new HashMap<String, CharSequence>(1);
                ResourceReference uploadStatusRef = new ResourceReference(EasyUploadStatusCommand.RESOURCE_NAME);
                ResourceReference uploadCancelRef = new ResourceReference(EasyUploadCancelCommand.RESOURCE_NAME);

                variables.put("uploadStatusRequestURL", getPage().urlFor(uploadStatusRef));
                variables.put("uploadCancelRequestURL", getPage().urlFor(uploadCancelRef));
                variables.put("autoRemoveMessages", getConfig().autoRemoveMessages() ? "true" : "false");

                return variables;
            }
        };
        add(TextTemplateHeaderContributor.forJavaScript(EasyUpload.class, "js/EasyUploadConfig.js", variablesModel));
    }

    protected void onBeforeRender()
    {
        super.onBeforeRender();
        if (uploadIFrame == null)
        {
            // the iframe should be attached to a page to be able to get its pagemap,
            // that's why i'm adding it in onBeforRender
            addUploadIFrame();

            // use a 'component id'
            Label progress = new Label("uploadProgress", "");
            progress.setOutputMarkupId(true);
            progress.setMarkupId("uploadProgress" + "_" + getMarkupId());
            add(progress);
        }
    }

    private void addUploadIFrame()
    {
        IPageLink iFrameLink = new IPageLink()
        {
            private static final long serialVersionUID = -6200934928206624082L;

            public Page getPage()
            {
                // use the markup id to set the component id of the new frame
                EasyUploadIFrame uiframe = new EasyUploadIFrame(getMarkupId());
                uiframe.setEasyUpload(EasyUpload.this);
                return uiframe;
            }

            public Class<? extends WebPage> getPageIdentity()
            {
                return EasyUploadIFrame.class;
            }
        };
        uploadIFrame = new InlineFrame("uploadIframe", getPage().getPageMap(), iFrameLink);
        add(uploadIFrame);
    }

    /**
     * Convenience method. Same as calling easyUpload.getConfig().getBasePath().
     * 
     * @return the path where files are upload to
     */
    public String getBasePath()
    {
        return config.getBasePath();
    }

    public void cancel()
    {
        EasyUploadProcesses.getInstance().cancelUploadsByEasyUpload(this);
    }

    /*------------------------------------------------
     * List holding code for the post-processes
     *------------------------------------------------*/

    private ArrayList<Class<? extends IUploadPostProcess>> postProcesses = new ArrayList<Class<? extends IUploadPostProcess>>();

    public void registerPostProcess(Class<? extends IUploadPostProcess> postProcessClass)
    {
        postProcesses.add(postProcessClass);
    }

    public void unregisterPostProcess(Class<? extends IUploadPostProcess> postProcessClass)
    {
        postProcesses.remove(postProcessClass);
    }

    public List<IUploadPostProcess> getPostProcesses(String filename)
    {
        List<File> files = new ArrayList<File>(1);
        files.add(new File(filename));
        return getPostProcesses(files);
    }

    public List<IUploadPostProcess> getPostProcesses(List<File> files)
    {
        List<IUploadPostProcess> rtn = new ArrayList<IUploadPostProcess>();
        for (int i = 0; i < postProcesses.size(); i++)
        {
            IUploadPostProcess postProcess = createPostProcess(postProcesses.get(i));
            if (postProcess.needsProcessing(files))
                rtn.add(postProcess);
        }
        return rtn;
    }

    public IUploadPostProcess createPostProcess(Class<? extends IUploadPostProcess> pclass)
    {
        try
        {
            return pclass.newInstance();
        }
        catch (InstantiationException e)
        {
            LOG.error("cannot instantiate postProcess");
            error("cannot instantiate postProcess");
            throw new AbortException();
        }
        catch (IllegalAccessException e)
        {
            LOG.error("cannot instantiate postProcess through constructor");
            error("cannot instantiate postProcess through constructor");
            throw new AbortException();
        }
    }

    public void setConfig(EasyUploadConfig config)
    {
        this.config = config;
    }

    public EasyUploadConfig getConfig()
    {
        return config;
    }

}
