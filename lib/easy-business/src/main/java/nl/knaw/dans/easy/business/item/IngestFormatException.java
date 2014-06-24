package nl.knaw.dans.easy.business.item;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;

public class IngestFormatException extends ServiceException
{
    private static final long serialVersionUID = 1L;

    public IngestFormatException(String string)
    {
        super(string);
    }
}
