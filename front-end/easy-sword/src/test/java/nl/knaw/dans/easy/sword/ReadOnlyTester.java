package nl.knaw.dans.easy.sword;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.knaw.dans.common.lang.exception.ReadOnlyException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.solr.SolrSearchEngine;
import nl.knaw.dans.easy.business.services.EasySearchService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.search.DatasetSearchImpl;
import nl.knaw.dans.easy.data.search.EasySearchBeanFactory;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.sword.util.SubmitFixture;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class ReadOnlyTester extends IntegrationFixture {
    // static mocking of SystemStatus seems to conflict
    // with AspectJ annotations in the business layer (or something else)
    // as a workaround we test methods guarded by SystemStatus.getReadOnly

    private static EasySearchService searchService;
    private static EasyUserImpl user;

    @BeforeClass
    public static void prepareCountDatasets() throws Exception {
        SolrSearchEngine searchEngine = new SolrSearchEngine("http://evm:8080/solr", new EasySearchBeanFactory());
        new Data().setSearchEngine(searchEngine);
        new Data().setDatasetSearch(new DatasetSearchImpl(searchEngine));
        searchService = new EasySearchService();

        user = new EasyUserImpl("user") {
            private static final long serialVersionUID = 1L;

            public boolean isActive() {
                return true;
            }
        };
        user.addRole(Role.USER);
    }

    @Test
    public void noDraftCausedByReadOnlyExceptionOnIngest() throws Exception {
        // temporarily logging stack traces by SystemStatus.getReadOnly proved
        // that currently the method is only called at the start of addDirectoryContents
        // and not for individual items
        int oldNumberOfDatasets = searchService.getNumberOfDatasets(user);

        ItemService saved = Services.getItemService();
        ItemService mocked = PowerMock.createMock(ItemService.class);
        new Services().setItemService(mocked);
        try {
            mocked.addDirectoryContents(//
                    isA(EasyUserImpl.class), //
                    isA(DatasetImpl.class), //
                    isA(DmoStoreId.class), //
                    isA(File.class), //
                    isA(ItemIngester.class),//
                    isA(IngestReporter.class));
            expectLastCall().andThrow(new ServiceException(new ReadOnlyException()));
            execute();
        }
        finally {
            new Services().setItemService(saved);
        }
        assertThat(oldNumberOfDatasets, is(searchService.getNumberOfDatasets(user)));
    }

    @Test
    public void readOnlyBeforeSubmitLeavesDraft() throws Exception {
        int oldNumberOfDatasets = searchService.getNumberOfDatasets(user);

        DatasetService saved = Services.getDatasetService();
        DatasetService mocked = PowerMock.createMock(DatasetService.class);
        new Services().setDatasetService(mocked);
        try {
            expect(mocked.newDataset(//
                    isA(MetadataFormat.class)//
                    )).andStubDelegateTo(saved);
            mocked.submitDataset(isA(DatasetSubmission.class));
            expectLastCall().andThrow(new ServiceException(new ReadOnlyException()));
            execute();
        }
        finally {
            new Services().setDatasetService(saved);
        }
        assertThat(oldNumberOfDatasets + 1, is(searchService.getNumberOfDatasets(user)));
    }

    private void execute() throws FileNotFoundException, IOException, HttpException {
        PowerMock.replayAll();
        final RequestEntity request = createRequest(SubmitFixture.getFile("data-plus-ddm.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        PowerMock.verifyAll();
    }
}
