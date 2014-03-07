package nl.knaw.dans.common.fedora.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import nl.knaw.dans.common.fedora.fox.DigitalObject;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmoContainer;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmoContainerItem;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmoRecursiveItem;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDobConverterTest
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractDobConverterTest.class);

    @Test
    public void dummyContainerTest() throws RepositoryException, XMLSerializationException
    {
        DummyConverter<DummyDmoContainerItem> itemConverter = new DummyConverter<DummyDmoContainerItem>(DummyDmoContainerItem.class);
        DummyConverter<DummyDmoContainer> containerConverter = new DummyConverter<DummyDmoContainer>(DummyDmoContainer.class);
        DummyConverter<DummyDmoRecursiveItem> ritemConverter = new DummyConverter<DummyDmoRecursiveItem>(DummyDmoRecursiveItem.class);

        DummyDmoContainerItem i1 = new DummyDmoContainerItem("dummy-item:1");
        DummyDmoContainerItem i2 = new DummyDmoContainerItem("dummy-item:2");
        DummyDmoContainerItem i3 = new DummyDmoContainerItem("dummy-item:3");
        DummyDmoContainerItem i4 = new DummyDmoContainerItem("dummy-item:4");

        DummyDmoRecursiveItem ir1 = new DummyDmoRecursiveItem("dummy-ritem:1");
        DummyDmoRecursiveItem ir2 = new DummyDmoRecursiveItem("dummy-ritem:2");

        DummyDmoContainer c1 = new DummyDmoContainer("dummy-container:1");

        c1.addChild(ir1);
        c1.addChild(i1);
        i1.addParent(ir2);
        ir1.addChild(i3);
        ir1.addChild(ir2);
        ir2.addChild(i3);

        DigitalObject dob_i1 = itemConverter.serialize(i1);
        logger.debug(dob_i1.asXMLString());
        DigitalObject dob_i2 = itemConverter.serialize(i2);
        DigitalObject dob_i3 = itemConverter.serialize(i3);
        DigitalObject dob_i4 = itemConverter.serialize(i4);

        DigitalObject dob_ir1 = ritemConverter.serialize(ir1);
        DigitalObject dob_ir2 = ritemConverter.serialize(ir2);

        DigitalObject dob_c1 = containerConverter.serialize(c1);

        DummyDmoContainerItem c_i1 = new DummyDmoContainerItem("dummy-item:1");
        itemConverter.deserialize(dob_i1, c_i1);
        DummyDmoContainerItem c_i2 = new DummyDmoContainerItem("dummy-item:2");
        itemConverter.deserialize(dob_i2, c_i2);
        DummyDmoContainerItem c_i3 = new DummyDmoContainerItem("dummy-item:3");
        itemConverter.deserialize(dob_i3, c_i3);
        DummyDmoContainerItem c_i4 = new DummyDmoContainerItem("dummy-item:4");
        itemConverter.deserialize(dob_i4, c_i4);

        DummyDmoRecursiveItem c_ir1 = new DummyDmoRecursiveItem("dummy-ritem:1");
        ritemConverter.deserialize(dob_ir1, c_ir1);
        DummyDmoRecursiveItem c_ir2 = new DummyDmoRecursiveItem("dummy-ritem:1");
        ritemConverter.deserialize(dob_ir2, c_ir2);

        DummyDmoContainer c_c1 = new DummyDmoContainer("dummy-container:1");
        containerConverter.deserialize(dob_c1, c_c1);

        compareRelationships(c1.getRelations(), c_c1.getRelations());

        compareRelationships(ir1.getRelations(), c_ir1.getRelations());
        compareRelationships(ir2.getRelations(), c_ir2.getRelations());

        compareRelationships(i1.getRelations(), c_i1.getRelations());
        compareRelationships(i2.getRelations(), c_i2.getRelations());
        compareRelationships(i3.getRelations(), c_i3.getRelations());
        compareRelationships(i4.getRelations(), c_i4.getRelations());

        compareStringSets(ir2.getParentSids(), c_ir2.getParentSids());
        compareStringSets(ir1.getParentSids(), c_ir1.getParentSids());

        compareStringSets(i1.getParentSids(), c_i1.getParentSids());
        compareStringSets(i2.getParentSids(), c_i2.getParentSids());
        compareStringSets(i3.getParentSids(), c_i3.getParentSids());
        compareStringSets(i4.getParentSids(), c_i4.getParentSids());
    }

    private void compareStringSets(Set<DmoStoreId> set1, Set<DmoStoreId> set2)
    {
        assertEquals(set1.size(), set2.size());
        for (DmoStoreId str1 : set1)
        {
            assertTrue(set2.contains(str1));
        }
    }

    private void compareRelationships(Relations rs, Relations rs2)
    {
        if (rs == null || rs2 == null)
        {
            assertTrue(rs == null && rs2 == null);
            return;
        }

        Set<Relation> r = rs.getRelation(null, null);
        Set<Relation> r2 = rs2.getRelation(null, null);
        assertEquals(r.size(), r2.size());

        for (Relation t1 : r)
        {
            boolean found = false;
            for (Relation t2 : r2)
            {
                if (t1.subject.equals(t2.subject) && t1.predicate.equals(t2.predicate) && t1.object.equals(t1.object) && t1.isLiteral == t2.isLiteral)
                {
                    if (t1.datatype == null)
                        assertTrue(t2.datatype == null);
                    else
                        assertEquals(t1.datatype, t2.datatype);
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }
}
