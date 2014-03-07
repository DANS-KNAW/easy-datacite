package nl.knaw.dans.easy.business.md.amd;

import static org.junit.Assert.*;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataOwner;
import nl.knaw.dans.easy.business.md.amd.ElementnameUpdateStrategy;
import nl.knaw.dans.easy.xml.AdditionalContent;
import nl.knaw.dans.easy.xml.AdditionalMetadata;
import nl.knaw.dans.easy.xml.ResourceMetadata;
import nl.knaw.dans.easy.xml.ResourceMetadataList;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElementnameUpdateStrategyTest
{

    private static final Logger logger = LoggerFactory.getLogger(ElementnameUpdateStrategyTest.class);

    boolean verbose = false;

    @Test
    public void update() throws Exception
    {
        final AdditionalMetadata originalAmd = createAMD(new String[] {"element0", "old value 0"}, new String[] {"element1", "old value 1"}, new String[] {
                "element2", "old value 2"}, new String[] {"element3", "old value 3"});

        if (verbose)
            print(originalAmd);

        AdditionalMetadata newAmd = createAMD(new String[] {"element1", "new value 1"}, new String[] {"element3", "old value 3"}, new String[] {"newElement",
                "i'm new"});

        if (verbose)
            print(newAmd);

        ElementnameUpdateStrategy strategy = new ElementnameUpdateStrategy("myId");
        AdditionalMetadataOwner owner = new AdditionalMetadataOwner()
        {

            @Override
            public void setAdditionalMetadata(AdditionalMetadata addmd)
            {
                fail("Setter-call not expected.");
            }

            @Override
            public AdditionalMetadata getAdditionalMetadata()
            {
                return originalAmd;
            }
        };

        strategy.update(owner, newAmd);

        if (verbose)
            print(originalAmd);

        Element content = originalAmd.getAdditionalContent("myId").getContent();
        assertEquals(5, content.elements().size());
        assertEquals("old value 0", content.element("element0").getText());
        assertEquals("new value 1", content.element("element1").getText());
        assertEquals("old value 2", content.element("element2").getText());
        assertEquals("old value 3", content.element("element3").getText());
        assertEquals("i'm new", content.element("newElement").getText());
    }

    protected AdditionalMetadata createAMD(String[]... values)
    {
        AdditionalMetadata amd = new AdditionalMetadata();
        Element content = new DefaultElement("myContent");

        for (int i = 0; i < values.length; i++)
        {
            Element e = new DefaultElement(values[i][0]);
            e.setText(values[i][1]);
            content.add(e);
        }

        AdditionalContent ac = new AdditionalContent("myId", "this is content", content);
        amd.addAdditionalContent(ac);
        return amd;
    }

    protected void print(AdditionalMetadata amd) throws XMLSerializationException
    {
        ResourceMetadataList rmdl = new ResourceMetadataList();
        ResourceMetadata rmd = new ResourceMetadata("storeId");
        rmd.setAdditionalMetadata(amd);
        rmdl.addResourceMetadata(rmd);
        logger.debug("\n" + rmdl.asXMLString(4) + "\n");
    }

}
