package nl.knaw.dans.easy.tools.dataset;

import static org.junit.Assert.assertFalse;

import java.util.NoSuchElementException;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.ApplicationOnlineTest;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.easy.tools.task.am.dataset.DatasetStateFilter;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.junit.Test;

public class DatasetIteratorOnlineTest extends ApplicationOnlineTest {

    @Test
    public void iterator() throws Exception {
        DatasetIterator diter = new DatasetIterator();
        while (diter.hasNext()) {
            System.err.println(diter.next().getLabel());
        }
        assertFalse(diter.hasNext());
        assertFalse(diter.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void iterator2() throws Exception {
        DatasetIterator diter = new DatasetIterator();
        while (diter.hasNext()) {
            System.err.println(diter.next().getLabel());
        }
        diter.next();
    }

    @Test
    public void iteratorWithFilter() throws Exception {
        DatasetIterator diter = new DatasetIterator(new DmoFilter<Dataset>() {

            @Override
            public boolean accept(Dataset dmo) {
                return dmo.getLabel().contains("test");
            }
        });
        while (diter.hasNext()) {
            System.err.println(diter.next().getLabel());
        }
        assertFalse(diter.hasNext());
        assertFalse(diter.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorWithFilter2() throws Exception {
        DatasetIterator diter = new DatasetIterator(new DmoFilter<Dataset>() {

            @Override
            public boolean accept(Dataset dmo) {
                return dmo.getLabel().contains("test");
            }
        });
        while (diter.hasNext()) {
            System.err.println(diter.next().getLabel() + " (" + this.getClass().getName() + ".metadataFormatFilterIterator)");
        }
        diter.next();
    }

    @Test
    public void iteratorWithFilter3() throws Exception {
        DatasetIterator diter = new DatasetIterator(new DmoFilter<Dataset>() {

            @Override
            public boolean accept(Dataset dmo) {
                return false;
            }
        });

        assertFalse(diter.hasNext());
        assertFalse(diter.hasNext());
    }

    @Test
    public void metadataFormatFilterIterator() throws Exception {
        DatasetIterator diter = new DatasetIterator(new MetadataFormatFilter(MetadataFormat.ARCHAEOLOGY));
        while (diter.hasNext()) {
            System.err.println(diter.next().getLabel() + " (" + this.getClass().getName() + ".metadataFormatFilterIterator)");
        }
    }

    @Test
    public void iteratorWith2Filters() throws Exception {
        DatasetIterator diter = new DatasetIterator(new MetadataFormatFilter(MetadataFormat.SOCIOLOGY), new DatasetStateFilter(DatasetState.DRAFT.toString()));
        while (diter.hasNext()) {
            System.err.println(diter.next().getLabel() + " (" + this.getClass().getName() + ".iteratorWith2Filters)");
        }
    }

}
