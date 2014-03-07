package nl.knaw.dans.easy.domain.form;

import nl.knaw.dans.easy.domain.deposit.discipline.DepositDiscipline;

/**
 * Signals an exception in the configuration of {@link DepositDiscipline}s.
 * 
 * @author ecco Apr 8, 2009
 */
public class FormConfigurationException extends Exception
{

    private static final long serialVersionUID = -2886161910444752248L;

    public FormConfigurationException()
    {
    }

    public FormConfigurationException(String message)
    {
        super(message);
    }

    public FormConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public FormConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
