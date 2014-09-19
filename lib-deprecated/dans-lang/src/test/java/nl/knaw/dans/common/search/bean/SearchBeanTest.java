package nl.knaw.dans.common.search.bean;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.knaw.dans.common.lang.search.IndexDocument;
import nl.knaw.dans.common.lang.search.bean.SearchBeanConverter;
import nl.knaw.dans.common.lang.search.exceptions.MissingRequiredFieldException;
import nl.knaw.dans.common.lang.search.exceptions.PrimaryKeyMissingException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanFactoryException;
import nl.knaw.dans.common.lang.search.simple.SimpleDocument;
import nl.knaw.dans.common.lang.search.simple.SimpleField;

import org.joda.time.DateTime;
import org.junit.Test;

public class SearchBeanTest {

    private Date curDate;
    private DateTime d1;
    private DateTime d2;

    @SuppressWarnings("unchecked")
    @Test
    public void testBeanCreationAndConversion() throws SearchBeanFactoryException, SearchBeanException, SearchBeanConverterException {
        SimpleDocument d = new SimpleDocument();
        d.addField(new SimpleField<Integer>(DummySB.ID_NAME, 5));
        d.addField(new SimpleField<String>(DummySB.NAME_NAME, "hello world"));
        ArrayList<DateTime> dates = new ArrayList<DateTime>();
        d1 = new DateTime(2010, 03, 04, 18, 8, 10, 100);
        d2 = new DateTime(1999, 12, 31, 23, 59, 59, 999);
        dates.add(d1);
        dates.add(d2);
        d.addField(new SimpleField<List<DateTime>>(DummySB.DATES_NAME, dates));
        List<String> addresses = new ArrayList<String>();
        addresses.add("dummy 1");
        addresses.add("dummy 2");
        d.addField(new SimpleField<List<String>>(DummySB.ADDRESSES_NAME, addresses));
        curDate = new Date();
        d.addField(new SimpleField<Date>(DummySB.CURDATE_NAME, curDate));

        // convert document to search bean
        DummySB dummy = (DummySB) DummySearchBeanFactory.getInstance().createSearchBean("dummy", d);

        // check if dummy has all properties set right
        checkAllProperties(dummy);

        // convert the dummy back to document d2
        SearchBeanConverter converter = DummySearchBeanFactory.getInstance().getSearchBeanConverter(dummy.getClass());
        IndexDocument d2 = converter.toIndexDocument(dummy);

        // convert d2 back to a dummy object
        DummySB dummy2 = (DummySB) DummySearchBeanFactory.getInstance().createSearchBean("dummy", d2);

        // check if dummy2 has all properties set right
        checkAllProperties(dummy2);
    }

    private void checkAllProperties(DummySB dummy) {
        assertEquals(new Integer(5), dummy.getId());
        assertEquals("hello world", dummy.getName());
        assertEquals(d1, dummy.getDates().get(0));
        assertEquals(d2, dummy.getDates().get(1));
        assertEquals("dummy 1", dummy.getAddresses().get(0));
        assertEquals("dummy 2", dummy.getAddresses().get(1));
        assertEquals(curDate, dummy.getCurrentDate());
    }

    @Test(expected = MissingRequiredFieldException.class)
    public void testRequiredFieldMissing() throws SearchBeanFactoryException, SearchBeanException {
        SimpleDocument d = new SimpleDocument();
        d.addField(new SimpleField<Integer>(DummySB.ID_NAME, 5));

        DummySearchBeanFactory.getInstance().createSearchBean("dummy", d);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = MissingRequiredFieldException.class)
    public void testRequiredFieldMissing2() throws SearchBeanFactoryException, SearchBeanException, SearchBeanConverterException {
        DummySB dummy = new DummySB();
        dummy.setId(2);
        dummy.setName(null);

        // convert the dummy back to document d2
        SearchBeanConverter converter = DummySearchBeanFactory.getInstance().getSearchBeanConverter(dummy.getClass());
        converter.toIndexDocument(dummy);
    }

    @Test(expected = PrimaryKeyMissingException.class)
    public void testMissingPrimaryKey() throws SearchBeanFactoryException, SearchBeanException {
        SimpleDocument d = new SimpleDocument();
        d.addField(new SimpleField<String>(DummySB.NAME_NAME, "hello world"));

        DummySearchBeanFactory.getInstance().createSearchBean("dummy", d);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = PrimaryKeyMissingException.class)
    public void testMissingPrimaryKey2() throws SearchBeanFactoryException, SearchBeanException, SearchBeanConverterException {
        DummySB dummy = new DummySB();
        dummy.setName("hello");

        // convert the dummy back to document d2
        SearchBeanConverter converter = DummySearchBeanFactory.getInstance().getSearchBeanConverter(dummy.getClass());
        converter.toIndexDocument(dummy);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCopyField() throws SearchBeanFactoryException, SearchBeanException, SearchBeanConverterException {
        DummySB dummy = new DummySB();
        dummy.setId(2);
        dummy.setName("hello");

        // convert the dummy back to document d2
        SearchBeanConverter converter = DummySearchBeanFactory.getInstance().getSearchBeanConverter(dummy.getClass());
        IndexDocument d = converter.toIndexDocument(dummy);

        assertEquals("hello_copied", d.getFields().getByFieldName(DummySB.COPYNAME_NAME).getValue());
    }
}
