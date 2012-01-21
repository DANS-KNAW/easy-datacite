package nl.knaw.dans.easy.business.aspect;

//import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
//import nl.knaw.dans.easy.business.services.EasyDatasetService;
//import nl.knaw.dans.easy.servicelayer.services.DatasetService;
//import nl.knaw.dans.easy.business.aspect.ServiceProfiling;
//
//import org.easymock.EasyMock;
//import org.junit.Test;
//import org.slf4j.Logger;
//
//
//public class ServiceProfilingTest
//{
//    
//    @Test
//    public void testAdvice() throws ServiceException
//    {   
//        DatasetService service = new EasyDatasetService();
//        Logger mock = EasyMock.createMock(Logger.class);
//        Logger real = ServiceProfiling.setLogger(mock);
//        
//        mock.info(EasyMock.isA(String.class)); EasyMock.expectLastCall().times(1);
//        
//        EasyMock.replay(mock);
//        service.doBeanPostProcessing();
//        EasyMock.verify(mock);
//        
//        ServiceProfiling.setLogger(real);
//        
//        real.info("Next INFO from " + service.getClass().getSimpleName() + " is the log statement we were testing:");
//        service.doBeanPostProcessing();
//    }
//    
//    
//
//}
