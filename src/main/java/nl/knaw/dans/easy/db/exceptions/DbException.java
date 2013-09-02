package nl.knaw.dans.easy.db.exceptions;

import org.hibernate.HibernateException;

/**
 * This exception is thrown by the database utility classes. It extends the Hibernate exception, not
 * because that is the right thing to do, but because it makes it easier for catchers of exceptions to
 * catch all exceptions in one catch. Also it should be accepted that these database utilities are made
 * for hibernate and hibernate only. The only reason why they are not called hibernate utilities is
 * because I think it 'database' utilities is more telling.
 * 
 * @author lobo
 */
public class DbException extends HibernateException
{
    private static final long serialVersionUID = -2243461894207732251L;

    public DbException(String message)
    {
        super(message);
    }

    public DbException(Throwable cause)
    {
        super(cause);
    }

    public DbException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
