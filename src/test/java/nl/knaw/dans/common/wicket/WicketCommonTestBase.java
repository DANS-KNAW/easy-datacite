package nl.knaw.dans.common.wicket;

import java.io.Serializable;

import org.apache.wicket.Page;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WicketCommonTestBase implements Serializable
{
    private static final long serialVersionUID = 7303398653319815055L;

    private static Logger logger = LoggerFactory.getLogger(WicketCommonTestBase.class);

    protected CommonWicketApplication application;

    protected CommonWicketTester tester;

    @Before
    public void before()
    {
        application = new CommonWicketApplication()
        {
            @Override
            public Class<? extends Page> getHomePage()
            {
                return HomeTestPage.class;
            }
        };

        tester = new CommonWicketTester(application);
        tester.startPage(application.getHomePage());
    }

}
