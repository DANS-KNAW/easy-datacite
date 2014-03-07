package nl.knaw.dans.easy.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import nl.knaw.dans.easy.rest.resources.AccountResource;
import nl.knaw.dans.easy.rest.resources.AdvancedSearchResource;
import nl.knaw.dans.easy.rest.resources.CmdiResource;
import nl.knaw.dans.easy.rest.resources.DatasetResource;
import nl.knaw.dans.easy.rest.resources.DisciplineResource;
import nl.knaw.dans.easy.rest.resources.SearchResource;
import nl.knaw.dans.easy.rest.resources.TestResource;

/**
 * The Application class that locates all the resource classes to initialize the REST service. This can
 * be achieved without such a class but the other methods are not compatible with Spring injection.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public class RestApplication extends Application
{

    /**
     * Returns the Resource classes that will be used to map the REST service.
     * 
     * @return A Set containing the Resource classes.
     */
    @Override
    public Set<Class<?>> getClasses()
    {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(TestResource.class);
        s.add(DatasetResource.class);
        s.add(SearchResource.class);
        s.add(AdvancedSearchResource.class);
        s.add(AccountResource.class);
        s.add(DisciplineResource.class);
        s.add(CmdiResource.class);
        return s;
    }

}
