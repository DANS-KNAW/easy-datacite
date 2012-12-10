package nl.knaw.dans.l.xml.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLTransformerTest
{

    private static String HONK;

    private static final Logger logger = LoggerFactory.getLogger(XMLTransformerTest.class);

    private static final String FILE_XML = "src/test/resources/test-files/transform/index.xml";
    private static final String FILE_XSL = "src/test/resources/test-files/transform/index.xsl";
    private static final String FILE_OUT = "target/transformer-output.html";

    private static final String ROOT_XML = "src/test/resources/test-files/transform/root.xml";
    private static final String XALAN_TOOT_XSL = "src/test/resources/test-files/transform/xalan-toot.xsl";
    private static final String SAXON_TOOT_XSL = "src/test/resources/test-files/transform/saxon-toot.xsl";    

    /**
     * Test involves xalan-style java call. With Saxon the namesapce declaration in the stylesheet
     * is different. Stylesheet contains
     * <pre>
     *  xmlns:test="xalan://nl.knaw.dans.l.xml.transform.XMLTransformerTest"
     * </pre>
     * 
     * @see http://support.sms-fed.com/support/docs/DOC-1260
     * 
     * @throws Exception
     */
    @Test
    public void xalanHonkTest() throws Exception
    {
        XMLTransformer transformer = new XMLTransformer(XALAN_TOOT_XSL, XMLTransformer.TF_XALAN);
        assertTrue(transformer.getTransformerFactoryName().contains("xalan"));
        performHonkTest(transformer, new File(ROOT_XML));
    }
    
    /**
     * Test involves saxon-style java call. Stylesheet contains
     * <pre>
     *  xmlns:test="java:nl.knaw.dans.l.xml.transform.XMLTransformerTest" 
     * </pre>
     * 
     * @throws Exception
     */
    @Test
    public void saxonHonkTest() throws Exception
    {
        XMLTransformer transformer = new XMLTransformer(SAXON_TOOT_XSL, XMLTransformer.TF_SAXON);
        assertTrue(transformer.getTransformerFactoryName().contains("saxon"));
        performHonkTest(transformer, new File(ROOT_XML));
    }

    private void performHonkTest(XMLTransformer transformer, File rootXml) throws TransformerException
    {
        HONK = "yes";
        StringWriter out = new StringWriter();
        transformer.transform(rootXml, out);
        assertEquals("toet toeoet ", out.toString());
        assertEquals(0, transformer.getErrorCount());

        HONK = "no";
        out = new StringWriter();
        transformer.transform(rootXml, out);
        assertEquals("Ok, I'll keep quiet. ", out.toString());
        assertEquals(0, transformer.getErrorCount());
    }

    /**
     * Called from xslt processor.
     * 
     * @see #xalanHonkTest()
     * @see #saxonHonkTest()
     * @return the honk-state, either 'yes' or 'no'.
     */
    public static String getHonk()
    {
        return HONK;
    }
    
    @Test
    public void cacheTest() throws Exception
    {
        XMLTransformer.clearCache();
        assertEquals(0, XMLTransformer.getCacheSize());
        int times = 2;
        
        saxonDurationTest(times);
        xalanDurationTest(times);
        saxonDurationTest(times);
        xalanDurationTest(times);
        
        assertEquals(2, XMLTransformer.getCacheSize());
        assertEquals(1, XMLTransformer.getCacheSize(XMLTransformer.TF_SAXON));
        assertEquals(1, XMLTransformer.getCacheSize(XMLTransformer.TF_XALAN));
        
        XMLTransformer.clearCache(XMLTransformer.TF_SAXON);
        assertEquals(1, XMLTransformer.getCacheSize());
        assertEquals(0, XMLTransformer.getCacheSize(XMLTransformer.TF_SAXON));
        assertEquals(1, XMLTransformer.getCacheSize(XMLTransformer.TF_XALAN));
    }
    
    @Ignore("Performance test")
    @Test
    public void durationTest() throws Exception
    {
        int times = 10000;
        boolean saxonFirst = true;
        if (saxonFirst)
        {
            saxonDurationTest(times);
            xalanDurationTest(times);
            saxonDurationTest(times);
            xalanDurationTest(times);
        }
        else
        {
            xalanDurationTest(times);
            saxonDurationTest(times);
            xalanDurationTest(times);
            saxonDurationTest(times);
        }
    }
    
    private void saxonDurationTest(int times) throws TransformerException
    {
        XMLTransformer transformer = new XMLTransformer(SAXON_TOOT_XSL, XMLTransformer.TF_SAXON);
        File rootXml = new File(ROOT_XML);
        long start = System.currentTimeMillis();
        
        for (int i = 0; i <= times; i++)
        {     
            performHonkTest(transformer, rootXml);
        }
        long duration = System.currentTimeMillis() - start;
        double average = (double)duration/(double)times;
        logger.debug("SAXON: times={}; duration={}; average=" + average, times, duration);
    }
    
    private void xalanDurationTest(int times) throws TransformerException
    {
        XMLTransformer transformer = new XMLTransformer(XALAN_TOOT_XSL);
        File rootXml = new File(ROOT_XML);
        long start = System.currentTimeMillis();
        
        for (int i = 0; i <= times; i++)
        {
            performHonkTest(transformer, rootXml);
        }
        long duration = System.currentTimeMillis() - start;
        double average = (double)duration/(double)times;
        logger.debug("XALAN: times={}; duration={}; average=" + average, times, duration);
    }
   

    @Ignore("Stylesheet gets documents online.")
    @Test
    public void transform() throws Exception
    {
        XMLTransformer transformer = new XMLTransformer(FILE_XSL);
        transformer.transform(FILE_XML, FILE_OUT);

        if (transformer.getErrorCount() > 0)
        {
            logger.error(transformer.getErrors());
        }
        assertEquals(0, transformer.getErrorCount());

        transformer.transform(new File(FILE_XML), System.out);
        assertEquals(0, transformer.getErrorCount());
    }

}
