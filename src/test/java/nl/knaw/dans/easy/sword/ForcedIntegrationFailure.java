package nl.knaw.dans.easy.sword;

import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.sword.util.SubmitFixture;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.easymock.EasyMock;

import static org.easymock.EasyMock.*;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class ForcedIntegrationFailure extends IntegrationFixture
{
    @Test
    public void mockedAddDirectoryContents() throws Exception
    {
        ItemService saved = Services.getItemService();
        ItemService mocked = PowerMock.createMock(ItemService.class);
        new Services().setItemService(mocked);

        mocked.addDirectoryContents(isA(EasyUserImpl.class), isA(DatasetImpl.class), isA(DmoStoreId.class), isA(File.class), isA(ItemIngester.class),
                isA(IngestReporter.class));
        expectLastCall().andThrow(new ServiceException("mocked exception"));
        PowerMock.replayAll();

        PostMethod run = run();
        new Services().setItemService(saved);

        assertResponseCode(run, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        PowerMock.verifyAll();
    }

    @Test
    public void depositWithDDM() throws Exception
    {
        DatasetService saved = Services.getDatasetService();
        DatasetService mocked = PowerMock.createMock(DatasetService.class);
        new Services().setDatasetService(mocked);

        expect(mocked.newDataset(isA(MetadataFormat.class))).andStubDelegateTo(saved);
        mocked.submitDataset(isA(DatasetSubmission.class));
        expectLastCall().andThrow(new ServiceException("mocked exception"));
        PowerMock.replayAll();

        PostMethod run = run();
        new Services().setDatasetService(saved);

        assertResponseCode(run, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        PowerMock.verifyAll();
    }

    private PostMethod run() throws FileNotFoundException, IOException, HttpException
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("data-plus-ddm.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        return method;
    }
}
