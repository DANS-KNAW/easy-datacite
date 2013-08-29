package nl.knaw.dans.pf.language.xml.transform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for applying stylesheet transformations (xslt) to xml. XMLTransformer caches stylesheet
 * templates for better performance. Cache can be cleared by calling {@link #clearCache()} or
 * {@link #clearCache(String)}. The latter call clears the cache on a per TransformerFactory basis.
 * 
 * @author ecco
 */
public class XMLTransformer implements ErrorListener
{

    /**
     * The system property key for {@link TransformerFactory}.
     */
    public static final String PROP_TRANSFORMERFACTORY = "javax.xml.transform.TransformerFactory";

    /**
     * TransformerFactory name for Xalan.
     */
    public static final String TF_XALAN = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";

    /**
     * TransformerFactory name for Saxon.
     */
    public static final String TF_SAXON = "net.sf.saxon.TransformerFactoryImpl";

    /**
     * The default maximum for {@link Transformer} reuse. Saxon has a known problem with heap memory
     * usage if the same Transformer is used over and over again.
     */
    public static final int DEFAULT_MAX_TRANSFORMER_REUSE = 100;

    private static Map<String, Map<String, Templates>> TEMPLATES_CACHE = Collections.synchronizedMap(new HashMap<String, Map<String, Templates>>());

    private static Logger logger = LoggerFactory.getLogger(XMLTransformer.class);

    private int cycleCount;
    private int maxTransformerReuse = DEFAULT_MAX_TRANSFORMER_REUSE;

    private final TransformerFactory transformerFactory;
    private final String stylesheetName;
    private final List<TransformerException> errorList = new ArrayList<TransformerException>();

    private Transformer transformer;

    /**
     * Constructor that takes the filename of the stylesheet as parameter, using the default
     * {@link TransformerFactory}, from xalan.
     * 
     * @param xsltFileName
     *        filename of the stylesheet
     * @throws TransformerException
     *         in case of exceptional conditions
     */
    public XMLTransformer(String xsltFileName) throws TransformerException
    {
        this(xsltFileName, TF_XALAN);
    }

    /**
     * Constructor that takes the stylesheet file as parameter, using the default
     * {@link TransformerFactory} from xalan.
     * 
     * @param xsltFile
     *        stylesheet file
     * @throws TransformerException
     *         in case of exceptional conditions
     */
    public XMLTransformer(File xsltFile) throws TransformerException
    {
        this(xsltFile, TF_XALAN);
    }

    /**
     * Constructor that takes the filename of the stylesheet and the fully qualified classname of the
     * transformerFactory as parameters.
     * 
     * @param xsltFileName
     *        filename of the stylesheet
     * @param transformerFactoryName
     *        fully qualified classname of the transformerFactory
     * @throws TransformerException
     *         in case of exceptional conditions
     */
    public XMLTransformer(String xsltFileName, String transformerFactoryName) throws TransformerException
    {
        this(new File(xsltFileName), transformerFactoryName);
    }

    /**
     * Constructor that takes the stylesheet file and the fully qualified classname of the
     * transformerFactory as parameters.
     * 
     * @param xsltFile
     *        filename of the stylesheet
     * @param transformerFactoryName
     *        fully qualified classname of the transformerFactory
     * @throws TransformerException
     *         in case of exceptional conditions
     */
    public XMLTransformer(File xsltFile, String transformerFactoryName) throws TransformerException
    {
        if (transformerFactoryName == null)
        {
            transformerFactoryName = TF_XALAN;
            logger.warn("Parameter 'transformerFactoryName' is null. Using default TransformerFactory.");
        }
        System.setProperty(XMLTransformer.PROP_TRANSFORMERFACTORY, transformerFactoryName);
        transformerFactory = TransformerFactory.newInstance();
        stylesheetName = xsltFile.getPath();
        logger.info("Using TransformerFactory: {}", this.transformerFactory.getClass().getName());
    }
    
    public XMLTransformer(URL url) throws TransformerException
    {
        this(url, TF_XALAN);
    }
    
    public XMLTransformer(URL url, String transformerFactoryName) throws TransformerException
    {
        if (transformerFactoryName == null)
        {
            transformerFactoryName = TF_XALAN;
            logger.warn("Parameter 'transformerFactoryName' is null. Using default TransformerFactory.");
        }
        System.setProperty(XMLTransformer.PROP_TRANSFORMERFACTORY, transformerFactoryName);
        transformerFactory = TransformerFactory.newInstance();
        stylesheetName = url.toString();
        logger.info("Using TransformerFactory: {}", this.transformerFactory.getClass().getName());
    }

    /**
     * Constructor that takes the filename of the stylesheet and the transformerFactory as parameters.
     * 
     * @param xsltFileName
     *        filename of the stylesheet
     * @param transformerFactory
     *        transformerFactory to use
     * @throws TransformerException
     *         in case of exceptional conditions
     */
    public XMLTransformer(String xsltFileName, TransformerFactory transformerFactory) throws TransformerException
    {
        this(new File(xsltFileName), transformerFactory);
    }

    /**
     * Constructor that takes the stylesheet file and the transformerFactory as parameters.
     * 
     * @param xsltFile
     *        stylesheet file
     * @param transformerFactory
     *        transformerFactory to use
     * @throws TransformerException
     *         in case of exceptional conditions
     */
    public XMLTransformer(File xsltFile, TransformerFactory transformerFactory) throws TransformerException
    {
        if (transformerFactory == null)
        {
            logger.warn("Parameter 'transformerFactory' is null. Using default TransformerFactory.");
            System.setProperty(XMLTransformer.PROP_TRANSFORMERFACTORY, TF_XALAN);
            this.transformerFactory = TransformerFactory.newInstance();
        }
        else
        {
            this.transformerFactory = transformerFactory;
        }
        stylesheetName = xsltFile.getPath();
        logger.info("Using TransformerFactory: {}", this.transformerFactory.getClass().getName());
    }

    /**
     * Get the name of the TransformerFactory in use.
     * 
     * @return name of the TransformerFactory in use
     */
    public String getTransformerFactoryName()
    {
        return transformerFactory.getClass().getName();
    }

    public int getMaxTransformerReuse()
    {
        return maxTransformerReuse;
    }

    public void setMaxTransformerReuse(int maxTransformerReuse)
    {
        if (maxTransformerReuse < 1)
        {
            throw new IllegalArgumentException("maxTransformerReuse should be set to a possitive integer. " + "A value of " + maxTransformerReuse
                    + " is invalid.");
        }
        this.maxTransformerReuse = maxTransformerReuse;
    }

    public void transform(String filenameIn, String filenameOut) throws TransformerException
    {
        transform(new StreamSource(filenameIn), new StreamResult(filenameOut));
    }

    public void transform(File fileIn, File fileOut) throws TransformerException
    {
        transform(new StreamSource(fileIn), new StreamResult(fileOut));
    }

    public void transform(Source xmlSource, File fileOut) throws TransformerException
    {
        transform(xmlSource, new StreamResult(fileOut));
    }

    public void transform(File fileIn, OutputStream out) throws TransformerException
    {
        transform(new StreamSource(fileIn), new StreamResult(out));
    }

    public void transform(File fileIn, Writer out) throws TransformerException
    {
        transform(new StreamSource(fileIn), new StreamResult(out));
    }

    public void transform(URL urlIn, OutputStream out) throws TransformerException
    {
        try
        {
            transformURL(urlIn, out);
        }
        catch (IOException e)
        {
            logger.error("Could not close inputstream [{}]", urlIn);
            throw new TransformerException(e);
        }
    }

    private void transformURL(URL urlIn, OutputStream out) throws TransformerException, IOException
    {
        InputStream inStream = null;
        try
        {
            inStream = urlIn.openStream();
            transform(new StreamSource(inStream), new StreamResult(out));
        }
        catch (IOException e)
        {
            logger.error("Could not open or read inputstream [{}].", urlIn);
            throw new TransformerException(e);
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
    }
    
    public void transform(InputStream in, OutputStream out) throws TransformerException
    {
        transform(new StreamSource(in), new StreamResult(out));
    }

    /**
     * Transform the given <code>xmlSource</code> to the given <code>result</code>, using this
     * XMLTransformer' stylesheet. During transformation this XMLTransformer listens for errors. After
     * transformation errors and warnings can be obtained.
     * 
     * @see #getErrorCount()
     * @see #getErrors()
     * @param xmlSource
     *        source of xml
     * @param result
     *        transformation result
     * @throws TransformerException
     *         in case of exceptional conditions
     */
    public void transform(Source xmlSource, Result result) throws TransformerException
    {
        errorList.clear();
        getTransformer().transform(xmlSource, result);
    }

    /**
     * Get the Transformer in use.
     * 
     * @return the Transformer in use
     * @throws TransformerException
     *         in case of exceptional conditions
     */
    public Transformer getTransformer() throws TransformerException
    {
        // known problems with reuse transformer. out of memory error after transforming {amount}
        // documents.
        if (cycleCount++ % maxTransformerReuse == 0)
        {
            transformer = null;
        }
        if (transformer == null)
        {
            transformer = getTemplates(transformerFactory, stylesheetName).newTransformer();
            transformer.setErrorListener(this);
        }
        else
        {
            transformer.reset();
        }
        return transformer;
    }

    public void error(TransformerException exception) throws TransformerException
    {
        errorList.add(exception);
        logger.warn(exception.getMessageAndLocation());
    }

    public void fatalError(TransformerException exception) throws TransformerException
    {
        errorList.add(exception);
        logger.warn(exception.getMessageAndLocation());
    }

    public void warning(TransformerException exception) throws TransformerException
    {
        errorList.add(exception);
        logger.warn(exception.getMessageAndLocation());
    }

    public int getErrorCount()
    {
        return errorList.size();
    }

    public String getErrors()
    {
        final StringBuilder builder = new StringBuilder();
        for (TransformerException tex : errorList)
        {
            builder.append(tex.getClass().getName());
            builder.append("\n\t");
            builder.append(tex.getMessage());
            builder.append("\n\t");
            builder.append("line=");
            builder.append(tex.getLocator().getLineNumber());
            builder.append(", column=");
            builder.append(tex.getLocator().getColumnNumber());
            builder.append("\n\t");
            builder.append(tex.getLocator().getSystemId());
            builder.append("\n");
        }
        return builder.toString();
    }

    private static Templates getTemplates(TransformerFactory factory, String stylesheetName) throws TransformerException
    {
        Templates templates;
        String factoryName = factory.getClass().getName();
        synchronized (TEMPLATES_CACHE)
        {
            Map<String, Templates> factoryTemplates = TEMPLATES_CACHE.get(factoryName);
            if (factoryTemplates == null)
            {
                factoryTemplates = new HashMap<String, Templates>();
                TEMPLATES_CACHE.put(factoryName, factoryTemplates);
                logger.trace("New factory templates map for [{}]", factoryName);
            }
            templates = factoryTemplates.get(stylesheetName);
            if (templates == null)
            {
                File stylesheetFile = new File(stylesheetName);
                if (stylesheetFile.exists())
                {
                    templates = createTemplatesFromFile(factory, stylesheetFile);
                }
                else
                {
                    templates = createTemplatesFromURL(factory, stylesheetName);
                }
                factoryTemplates.put(stylesheetName, templates);
                logger.info("Cache, size={} [{}]", factoryTemplates.size(), factoryName + ":" + stylesheetName);
            }
            return templates;
        }
    }

    private static Templates createTemplatesFromURL(TransformerFactory factory, String stylesheetName) throws TransformerException
    {
        Templates templates;
        InputStream inStream = null;
        try
        {
            URL url = new URL(stylesheetName);
            inStream = url.openStream();
            templates = factory.newTemplates(new StreamSource(inStream));
        }
        catch (MalformedURLException e)
        {
            throw new TransformerException(e);
        }
        catch (IOException e)
        {
            throw new TransformerException(e);
        }
        finally
        {
            IOUtils.closeQuietly(inStream);
        }
        return templates;
    }

    private static Templates createTemplatesFromFile(TransformerFactory factory, File stylesheetFile) throws TransformerConfigurationException
    {
        Templates templates;
        templates = factory.newTemplates(new StreamSource(stylesheetFile));
        return templates;
    }

    /**
     * Clear all cached templates.
     */
    public static void clearCache()
    {
        synchronized (TEMPLATES_CACHE)
        {
            TEMPLATES_CACHE.clear();
            logger.info("Cleared cache.");
        }
    }

    /**
     * Clear cached templates for the given <code>transformerFactoryName</code>.
     * 
     * @param transformerFactoryName
     *        fully qualified classname of a TransformerFactory
     */
    public static void clearCache(String transformerFactoryName)
    {
        synchronized (TEMPLATES_CACHE)
        {
            Map<String, Templates> factoryTemplates = TEMPLATES_CACHE.get(transformerFactoryName);
            if (factoryTemplates != null)
            {
                factoryTemplates.clear();
                logger.info("Cleared cache [{}].", transformerFactoryName);
            }
        }
    }

    public static int getCacheSize()
    {
        int size = 0;
        synchronized (TEMPLATES_CACHE)
        {
            for (Map<String, Templates> factoryTemplates : TEMPLATES_CACHE.values())
            {
                size += factoryTemplates.size();
            }
        }
        return size;
    }

    public static int getCacheSize(String transformerFactoryName)
    {
        int size = -1;
        synchronized (TEMPLATES_CACHE)
        {
            Map<String, Templates> factoryTemplates = TEMPLATES_CACHE.get(transformerFactoryName);
            if (factoryTemplates != null)
            {
                size = factoryTemplates.size();
            }
        }
        return size;
    }

}
