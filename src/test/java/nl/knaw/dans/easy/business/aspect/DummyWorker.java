package nl.knaw.dans.easy.business.aspect;

import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.worker.AbstractWorker;

public class DummyWorker extends AbstractWorker
{

    public DummyWorker(UnitOfWork uow)
    {
        super(uow);
    }

    protected void workFoo() throws ServiceException
    {

    }

    protected void workAndThrowExceoption() throws Exception
    {
        throw new Exception();
    }

    protected void workWithoutExceptions()
    {

    }

    protected void workAndThrowRuntimeException()
    {
        throw new RuntimeException();
    }

    protected void workNotBecauseCanceled()
    {
        throw new RuntimeException("I'm not working because I'm supposed to be canceled.");
    }

}
