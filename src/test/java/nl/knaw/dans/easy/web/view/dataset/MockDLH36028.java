package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;

import java.io.File;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.easymock.EasyMock;

/**
 * Mock http://easy.dans.knaw.nl:8080/fedora/objects/easy-dlh:36028/datastreams/DLHL/content <br>
 * to replicate https://drivenbydata.atlassian.net/browse/EASY-560 <br>
 * <br>
 * User IDs are altered, but the LogMyActions flags match. Files are visible for anonymous anyway so no
 * further need for scrambling the data
 */
public class MockDLH36028
{

    static final Integer[] NR_OF_FILE_PER_ROW = {1, 1, 1, 1, 1, 1, 1, 4, 1, 3, 1, 1};
    static final String EXPECTED_DOWNLOAD = "2013-02-05T14:40:06.700+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
            + "2013-02-05T14:47:22.715+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + "2013-02-05T18:44:51.846+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
            + "2013-02-11T10:55:19.434+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + "2013-02-11T10:55:31.976+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + "2013-02-11T11:01:16.151+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
            + "2013-02-12T13:51:29.008+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
            + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
            + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
            + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
            + "2013-02-21T14:34:03.198+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
            + "2013-02-22T11:36:28.861+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
            + "2013-02-22T11:36:28.861+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
            + "2013-02-22T11:36:28.861+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + "2013-02-23T11:29:40.653+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + "2013-02-23T11:33:00.059+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n";

    static DownloadList getList(UserService userService) throws Exception
    {
        final byte[] data = FileUtil.readFile(new File("src/test/resources/mock-xml/issue560-dlh36028.xml"));
        final EasyUserImpl user = new EasyUserImpl(Role.USER);
        final EasyUserImpl userNoLog = new EasyUserImpl(Role.USER);
        userNoLog.setLogMyActions(false);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("s1234567"))).andStubReturn(user);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("laalbers"))).andStubReturn(userNoLog);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("pschilder"))).andStubReturn(user);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("juulesengel"))).andStubReturn(userNoLog);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("warejacob"))).andStubReturn(user);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("hvanleeuwen"))).andStubReturn(userNoLog);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("theodorus"))).andStubReturn(userNoLog);
        return (DownloadList) JiBXObjectFactory.unmarshal(DownloadList.class, data);
    }
}
