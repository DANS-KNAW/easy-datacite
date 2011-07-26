package nl.knaw.dans.easy.domain.deposit.discipline;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.util.TestHelper;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChoiceListTest
{
    private static final Logger logger = LoggerFactory.getLogger(ChoiceListTest.class);
    
    private boolean verbose = Tester.isVerbose();

    @Test
    public void serializeDeserializeEmpty() throws XMLException
    {
        ChoiceList choiceList = new ChoiceList();
        if (verbose)
            logger.debug("\n" + choiceList.asXMLString(4) + "\n");

        ChoiceList choiceList2 = (ChoiceList) JiBXObjectFactory.unmarshal(ChoiceList.class, choiceList.asObjectXML());
        assertEquals(choiceList.asXMLString(), choiceList2.asXMLString());
    }
    
    @Test
    public void serializeDeserializeFull() throws XMLException
    {
        List<KeyValuePair> choices = new ArrayList<KeyValuePair>();
        choices.add(new KeyValuePair("key1", "value1"));
        choices.add(new KeyValuePair("key2", "value2"));
        ChoiceList choiceList = new ChoiceList(choices);
        choiceList.setComment("this is comment");
        if (verbose)
            logger.debug("\n" + choiceList.asXMLString(4) + "\n");

        ChoiceList choiceList2 = (ChoiceList) JiBXObjectFactory.unmarshal(ChoiceList.class, choiceList.asObjectXML());
        assertEquals(choiceList.asXMLString(), choiceList2.asXMLString());
    }
    
    // Will not work when you are offline because of
    // <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
    @Ignore
    @Test
    public void deserializeFromFile() throws XMLException
    {
        File file = TestHelper.getFile(this.getClass(), "spatial.xml");
        ChoiceList choiceList = (ChoiceList) JiBXObjectFactory.unmarshal(ChoiceList.class, file);
        if (verbose)
            logger.debug("\n" + choiceList.asXMLString(4) + "\n");
    }

}
