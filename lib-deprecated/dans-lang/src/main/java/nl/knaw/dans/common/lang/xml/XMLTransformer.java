package nl.knaw.dans.common.lang.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLTransformer implements ErrorListener {

    public static final String PROP_TRANSFORMERFACTORY = "javax.xml.transform.TransformerFactory";

    private static Logger logger = LoggerFactory.getLogger(XMLTransformer.class);

    private final TransformerFactory transformerFactory;
    private final Source xsltSource;
    private final List<TransformerException> errorList = new ArrayList<TransformerException>();

    public XMLTransformer(String xsltFileName) throws TransformerConfigurationException, TransformerFactoryConfigurationError {
        this(xsltFileName, TransformerFactory.newInstance());
    }

    public XMLTransformer(File xsltFile) throws TransformerConfigurationException, TransformerFactoryConfigurationError {
        this(xsltFile, TransformerFactory.newInstance());
    }

    public XMLTransformer(String xsltFileName, String transformerFactoryName) throws TransformerConfigurationException, TransformerFactoryConfigurationError {
        if (transformerFactoryName != null) {
            System.setProperty(XMLTransformer.PROP_TRANSFORMERFACTORY, transformerFactoryName);
        }
        transformerFactory = TransformerFactory.newInstance();

        xsltSource = new StreamSource(new File(xsltFileName));

        logger.info("Using TransformerFactory: " + this.transformerFactory.getClass().getName());
    }

    public XMLTransformer(String xsltFileName, TransformerFactory transformerFactory) throws TransformerConfigurationException,
            TransformerFactoryConfigurationError
    {
        this(new File(xsltFileName), transformerFactory);
    }

    public XMLTransformer(File xsltFile, TransformerFactory transformerFactory) throws TransformerConfigurationException, TransformerFactoryConfigurationError {
        if (transformerFactory == null) {
            // get a default one
            this.transformerFactory = TransformerFactory.newInstance();
        } else {
            this.transformerFactory = transformerFactory;
        }
        xsltSource = new StreamSource(xsltFile);

        logger.info("Using TransformerFactory: " + this.transformerFactory.getClass().getName());
    }

    public void transform(String xmlIn, String xmlOut) throws TransformerException, IOException {
        Source xmlSource = new StreamSource(xmlIn);
        transform(xmlSource, new File(xmlOut));
    }

    public void transform(File xmlIn, File xmlOut) throws TransformerException, IOException {
        Source xmlSource = new StreamSource(xmlIn);
        transform(xmlSource, xmlOut);
    }

    public void transform(Source xmlSource, File xmlOut) throws TransformerException, IOException {
        errorList.clear();
        // cannot reuse transformer. out of memory error after transforming 5286 documents.
        Transformer transformer = transformerFactory.newTransformer(xsltSource);
        Result result = new StreamResult(xmlOut);
        transformer.setErrorListener(this);
        transformer.transform(xmlSource, result);
    }

    public void transform(File xmlIn, java.io.OutputStream out) throws TransformerException, IOException {
        Source xmlSource = new StreamSource(xmlIn);
        errorList.clear();
        // cannot reuse transformer. out of memory error after transforming 5286 documents.
        Transformer transformer = transformerFactory.newTransformer(xsltSource);
        Result result = new StreamResult(out);
        transformer.setErrorListener(this);
        transformer.transform(xmlSource, result);
    }

    public void error(TransformerException exception) throws TransformerException {
        errorList.add(exception);
        logger.warn(exception.getMessageAndLocation());
    }

    public void fatalError(TransformerException exception) throws TransformerException {
        errorList.add(exception);
        logger.warn(exception.getMessageAndLocation());
    }

    public void warning(TransformerException exception) throws TransformerException {
        errorList.add(exception);
        logger.warn(exception.getMessageAndLocation());
    }

    public String getErrors() {
        final StringBuilder builder = new StringBuilder();
        for (TransformerException tex : errorList) {
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

}
