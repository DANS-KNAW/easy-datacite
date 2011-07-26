package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.DownloadFilter;

import org.junit.Test;

public class FileItemCensorTest
{

    @Test
    public void censor1() throws DomainException
    {
        List<FileItemVO> items = new ArrayList<FileItemVO>();
        FileItemVO fivo = new FileItemVO();
        fivo.setAccessibleTo(AccessibleTo.ANONYMOUS);
        items.add(fivo);

        Dataset dataset = new DatasetImpl("dummy-dataset:1");
        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.PUBLISHED);
        
        EasyUser sessionUser = null;
        
        DownloadFilter filter = new DownloadFilter(sessionUser, dataset);
        List<? extends ItemVO> filteredItems = filter.apply(items);
        // dataset.storeId and fivo.datasetId are not equal
        assertEquals(0, filteredItems.size());
    }
    
    @Test
    public void censor2() throws DomainException
    {
        List<FileItemVO> items = new ArrayList<FileItemVO>();
        FileItemVO fivo = new FileItemVO();
        fivo.setAccessibleTo(AccessibleTo.ANONYMOUS);
        fivo.setDatasetSid("pipo");
        items.add(fivo);

        Dataset dataset = new DatasetImpl("pipo");
        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.PUBLISHED);

        EasyUser sessionUser = null;
        
        DownloadFilter filter = new DownloadFilter(sessionUser, dataset);
        List<? extends ItemVO> filteredItems = filter.apply(items);
        assertEquals(1, filteredItems.size());
    }
    
    @Test
    public void censor3() throws DomainException
    {
        List<FileItemVO> items = new ArrayList<FileItemVO>();
        FileItemVO fivo = new FileItemVO();
        fivo.setAccessibleTo(AccessibleTo.KNOWN);
        fivo.setDatasetSid("pipo");
        items.add(fivo);

        Dataset dataset = new DatasetImpl("pipo");
        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.PUBLISHED);

        EasyUser sessionUser = null;
        
        DownloadFilter filter = new DownloadFilter(sessionUser, dataset);
        List<? extends ItemVO> filteredItems = filter.apply(items);

        assertEquals(0, filteredItems.size());
    }
    
    @Test
    public void censor4() throws DomainException
    {
        List<FileItemVO> items = new ArrayList<FileItemVO>();
        FileItemVO fivo = new FileItemVO();
        fivo.setAccessibleTo(AccessibleTo.ANONYMOUS);
        fivo.setDatasetSid("pipo");
        items.add(fivo);

        Dataset dataset = new DatasetImpl("pipo");
        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.PUBLISHED);
        
        EasyUser sessionUser = new EasyUserImpl();

        DownloadFilter filter = new DownloadFilter(sessionUser, dataset);
        List<? extends ItemVO> filteredItems = filter.apply(items);

        assertEquals(1, filteredItems.size());
    }
    
    @Test
    public void censor5() throws DomainException
    {
        List<FileItemVO> items = new ArrayList<FileItemVO>();
        FileItemVO fivo = new FileItemVO();
        fivo.setAccessibleTo(AccessibleTo.KNOWN);
        fivo.setDatasetSid("pipo");
        items.add(fivo);

        Dataset dataset = new DatasetImpl("pipo");
        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.PUBLISHED);
        
        EasyUser sessionUser = new EasyUserImpl();

        DownloadFilter filter = new DownloadFilter(sessionUser, dataset);
        List<? extends ItemVO> filteredItems = filter.apply(items);

        assertEquals(0, filteredItems.size());
    }
    
    @Test
    public void censor6() throws DomainException
    {
        List<FileItemVO> items = new ArrayList<FileItemVO>();
        FileItemVO fivo = new FileItemVO();
        fivo.setAccessibleTo(AccessibleTo.KNOWN);
        fivo.setDatasetSid("pipo");
        items.add(fivo);

        Dataset dataset = new DatasetImpl("pipo");
        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.PUBLISHED);
        
        EasyUser sessionUser = new EasyUserImpl();
        sessionUser.setState(State.ACTIVE);

        DownloadFilter filter = new DownloadFilter(sessionUser, dataset);
        List<? extends ItemVO> filteredItems = filter.apply(items);

        assertEquals(1, filteredItems.size());
    }

}
