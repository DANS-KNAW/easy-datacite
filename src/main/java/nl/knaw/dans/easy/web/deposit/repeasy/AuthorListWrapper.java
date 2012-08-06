package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.List;

import nl.knaw.dans.easy.domain.model.emd.types.Author;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractEasyModel;

public class AuthorListWrapper extends AbstractDefaultListWrapper<AuthorListWrapper.AuthorModel, Author>
{
    
    private static final long serialVersionUID = 1733893895631274476L;
    
    public AuthorListWrapper(List<Author> wrappedList)
    {
        super(wrappedList);
    }
    
    public AuthorListWrapper(List<Author> wrappedList, String schemeName, String schemeId)
    {
        super(wrappedList, schemeName, schemeId);
    }

    @Override
    public List<AuthorModel> getInitialItems()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int synchronize(List<AuthorModel> listItems)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public static class AuthorModel extends AbstractEasyModel
    {

        private static final long serialVersionUID = -6272851082229997716L;

        
    }

}
