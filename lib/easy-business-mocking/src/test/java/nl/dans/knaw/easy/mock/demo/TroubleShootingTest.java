package nl.dans.knaw.easy.mock.demo;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.mock.BusinessMocker;
import nl.knaw.dans.easy.mock.FileMocker;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Guide to interpret messages of exceptions thrown by {@link PowerMock}. The class provides some examples of erroneous usage or unimplemented expectations of
 * the the {@link BusinessMocker} library.
 */
@RunWith(PowerMockRunner.class)
public class TroubleShootingTest {
    final static int EQUALS = 1;
    final static int CONTAINS = 2;

    /**
     * The test methods using an instance explain possible causes of a problem.
     */
    static enum Message {
        NON_FIXED_COUNT_SET(EQUALS, "last method called on mock already has a non-fixed count set."), //
        UNEXPECTED_METHOD(CONTAINS, "Unexpected method call"), //
        RECORD_STATE(EQUALS, "calling verify is not allowed in record state"), //
        UNEXPECTED(CONTAINS, "Unexpected method call "), //
        //
        ;
        private final String value;
        private int type;

        Message(final int type, final String value) {
            this.type = type;
            this.value = value;
        }

        public void verify(final Throwable throwable) {
            switch (type) {
            case EQUALS:
                assertThat(throwable.getMessage(), equalTo(value));
                break;

            case CONTAINS:
                assertThat(throwable.getMessage(), containsString(value));
                break;
            }
        }
    }

    private BusinessMocker mock;

    @Before
    public void setUp() throws Exception {
        mock = new BusinessMocker();
    }

    /**
     * Not sure if the first stub or a random stub of the duplicates is used.
     */
    @Test
    public void duplicateStubs() throws Exception {
        mock.dataset(mock.nextDmoStoreId(Dataset.NAMESPACE)).with(DatasetState.DRAFT).with(DatasetState.DELETED);
        PowerMock.replayAll();
        assertThat(mock.getDatasets().get(0).getAdministrativeState(), equalTo(DatasetState.DRAFT));
        PowerMock.verifyAll();
    }

    private class InitialApproachMocker {
        final Dataset dataset = PowerMock.createMock(Dataset.class);

        InitialApproachMocker with(DatasetState state) {
            // the new approach is andStubReturn
            expect(dataset.getAdministrativeState()).andReturn(DatasetState.DELETED).anyTimes();
            return this;
        }
    }

    /** The initial approach would reveal mistakes. */
    @Test
    public void duplicateExpectations() throws Exception {
        try {
            new InitialApproachMocker().with(DatasetState.DRAFT).with(DatasetState.DELETED);
        }
        catch (final IllegalStateException e) {
            Message.NON_FIXED_COUNT_SET.verify(e);
            return;
        }
        fail();
    }

    /**
     * For the sake of the example {@link FileMocker#with(AccessibleTo)} is omitted. However, the mockers are written on a n as-needed basis. So In practice you
     * might have to write or extend a method for some of the {@link AbstractMocker} implementations, or extend a constructor. Typically you need a call to some
     * variant of {@link EasyMock#expect(Object)} or {@link PowerMock}. Naming conventions:
     * <ul>
     * <li>plain "with" for non ambiguous signatures and {@link IExpectationSetters#andStubReturn(Object)} which sets a default return value which is used as a
     * fallback only when regular .andReturn() have been used up</li>
     * <li>plain "withXxxx" to disambiguate a signatures and {@link IExpectationSetters#andStubReturn(Object)}</li>
     * <li>plain "expectYyyy" for a more specific number of times</li>
     * </ul>
     * 
     * @throws Exception
     */
    @Test
    public void missingExpectations() throws Exception {
        String fileStoreId = mock.nextDmoStoreId(FileItem.NAMESPACE);
        mock.file("test.txt", fileStoreId);
        PowerMock.replayAll();
        try {
            final FileItem fileItem = (FileItem) Data.getEasyStore().retrieve(new DmoStoreId(fileStoreId));
            fileItem.getAccessibleTo();
        }
        catch (final AssertionError e) {
            Message.UNEXPECTED_METHOD.verify(e);
            return;
        }
        fail();
    }

    @Test
    public void replayMissing() throws Exception {
        mock.file("test.txt").with(AccessibleTo.RESTRICTED_REQUEST, VisibleTo.ANONYMOUS);
        // replay should belong here
        try {
            Data.getEasyStore().retrieve(new DmoStoreId("easyfile:1"));
            PowerMock.verifyAll();
        }
        catch (final IllegalStateException e) {
            Message.RECORD_STATE.verify(e);
            return;
        }
        fail();
    }

    @Test
    public void noInline() throws Exception {
        final FileStoreAccess fsa = Data.getFileStoreAccess();
        final DmoStoreId dmoStoreId = new DmoStoreId(mock.nextDmoStoreId(Dataset.NAMESPACE));

        // Only inline expressions work, so don't do this
        final ItemFilters noItermFilters = eq((ItemFilters) null);
        final ItemOrder noItemOrder = eq((ItemOrder) null);

        expect(fsa.getFilesAndFolders(eq(dmoStoreId), eq((Integer) 0), eq((Integer) 0), noItemOrder, noItermFilters))//
                .andStubReturn(null);
        PowerMock.replayAll();
        try {
            fsa.getFilesAndFolders(dmoStoreId, 0, 0, null, null);
        }
        catch (final AssertionError e) {
            Message.UNEXPECTED.verify(e);
            return;
        }
        fail();
    }

    /** Proves the explaining comment in noInline() */
    @Test
    public void inline() throws Exception {
        final FileStoreAccess fsa = Data.getFileStoreAccess();
        final DmoStoreId dmoStoreId = new DmoStoreId(mock.nextDmoStoreId(Dataset.NAMESPACE));
        expect(fsa.getFilesAndFolders(eq(dmoStoreId), eq((Integer) 0), eq((Integer) 0), eq((ItemOrder) null), eq((ItemFilters) null)))//
                .andStubReturn(null);
        PowerMock.replayAll();
        fsa.getFilesAndFolders(dmoStoreId, 0, 0, null, null);
        PowerMock.verifyAll();
    }

    /** Proper solutions: {@link ExampleTest#emptyFolder()} and {@link ExampleTest#justAnEmptyFolder()} */
    @Test
    public void incompleteEmptyFolder() throws Exception {
        final String storeId = mock.nextDmoStoreId(Dataset.NAMESPACE);
        mock.dataset(storeId).with(mock.folder("a"));

        PowerMock.replayAll();

        try {
            ClassUnderTest.getNrOfFilesAndFolders(storeId);
        }
        catch (final AssertionError e) {
            Message.UNEXPECTED.verify(e);
            return;
        }
        fail();
    }
}
