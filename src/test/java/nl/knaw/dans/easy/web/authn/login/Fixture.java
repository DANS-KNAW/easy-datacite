package nl.knaw.dans.easy.web.authn.login;

import java.io.File;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.HomeDirectory;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.powermock.api.easymock.PowerMock;

public class Fixture
{
    protected static ApplicationContextMock applicationContext;
    private static UserService userService;

    @BeforeClass
    public static void mockApplicationContext() throws Exception
    {
        PowerMock.resetAll();

        userService = PowerMock.createMock(UserService.class);
        EasyMock.expect(userService.newUsernamePasswordAuthentication()).andStubReturn(new UsernamePasswordAuthentication());

        final HomeDirectory homeDir = new FileSystemHomeDirectory(new File("src/main/assembly/dist/res/example/editable/"));
        final SystemReadOnlyStatus systemReadOnlyStatus = PowerMock.createMock(SystemReadOnlyStatus.class);
        final CodedAuthz codedAuthz = new CodedAuthz();
        codedAuthz.setSystemReadOnlyStatus(systemReadOnlyStatus);

        applicationContext = new ApplicationContextMock();
        applicationContext.putBean("systemReadOnlyStatus", systemReadOnlyStatus);
        applicationContext.putBean("authz", codedAuthz);
        applicationContext.putBean("security", new Security(codedAuthz));
        applicationContext.putBean("editableContentHome", homeDir);
        applicationContext.putBean("userService", userService);
    }

    @AfterClass
    public static void cleanup() throws Exception
    {
        // until someone sees any use for these files:
        final File folder = new File("target/work");
        if (folder.exists())
            FileUtils.forceDelete(folder);
    }
}
