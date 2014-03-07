package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.eq;

import java.io.File;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.easymock.EasyMock;

/**
 * Mocks http://easy.dans.knaw.nl:8080/fedora/objects/easy-dlh:36028/datastreams/DLHL/content <br>
 * to replicate https://drivenbydata.atlassian.net/browse/EASY-560 <br>
 * <br>
 * User IDs are altered, but the LogMyActions flags match. Files are visible for anonymous anyway, so no
 * further need for scrambling the data.
 */
public class MockedDLHL36028
{

    private static final Integer[] NR_OF_FILES_PER_ROW = {1, 1, 1, 1, 1, 1, 1, 4, 1, 3, 1, 1};
    private static final String ARCHIVIST_EXPECTATION = ""
            + /* 1 */"2013-02-05T14:40:06.700+01:00;s1234567;n erd;n.erd@x.y;;;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
            + /* 2 */"2013-02-05T14:47:22.715+01:00;s1234567;n erd;n.erd@x.y;;;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 3 */"2013-02-05T18:44:51.846+01:00;laalbers;l aalbers;l.aalbers@y.x;;;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
            + /* 4 */"2013-02-11T10:55:19.434+01:00;pschilder;p schilder;p.schilder@eu.x;een universiteit;ass. professor;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 5 */"2013-02-11T10:55:31.976+01:00;pschilder;p schilder;p.schilder@eu.x;een universiteit;ass. professor;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 6 */"2013-02-11T11:01:16.151+01:00;pschilder;p schilder;p.schilder@eu.x;een universiteit;ass. professor;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
            + /* 7 */"2013-02-12T13:51:29.008+01:00;juulesengel;j engel;j.engel@u.x;universiteit;;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
            + /* 8 */"2013-02-21T14:31:01.962+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
            + /* 8 */"2013-02-21T14:31:01.962+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
            + /* 8 */"2013-02-21T14:31:01.962+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 8 */"2013-02-21T14:31:01.962+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
            + /* 9 */"2013-02-21T14:34:03.198+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
            + /* 10 */"2013-02-22T11:36:28.861+01:00;hvanleeuwen;h.van leewen;h.van.leewen@au.x;andere universiteit;;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
            + /* 10 */"2013-02-22T11:36:28.861+01:00;hvanleeuwen;h.van leewen;h.van.leewen@au.x;andere universiteit;;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
            + /* 10 */"2013-02-22T11:36:28.861+01:00;hvanleeuwen;h.van leewen;h.van.leewen@au.x;andere universiteit;;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 11 */"2013-02-23T11:29:40.653+01:00;theodorus;theo dorus;theo.dorus@eu.x;een universitet;student;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 12 */"2013-02-23T11:33:00.059+01:00;theodorus;theo dorus;theo.dorus@eu.x;een universitet;student;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n";
    private static final String DEPOSITOR_EXPECTATION = ""
            + /* 1 */"2013-02-05T14:40:06.700+01:00;s1234567;n erd;n. erd@x.y;;;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
            + /* 2 */"2013-02-05T14:47:22.715+01:00;s1234567;n erd;n. erd@x.y;;;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 3 */"2013-02-05T18:44:51.846+01:00;Anonymous;;;;;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
            + /* 4 */"2013-02-11T10:55:19.434+01:00;pschilder;p schilder;p.schilder@eu.x;een universiteit;ass. professor;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 5 */"2013-02-11T10:55:31.976+01:00;pschilder;p schilder;p.schilder@eu.x;een universiteit;ass. professor;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 6 */"2013-02-11T11:01:16.151+01:00;pschilder;p schilder;p.schilder@eu.x;een universiteit;ass. professor;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
            + /* 7 */"2013-02-12T13:51:29.008+01:00;Anonymous;;;;;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
            + /* 8 */"2013-02-21T14:31:01.962+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
            + /* 8 */"2013-02-21T14:31:01.962+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
            + /* 8 */"2013-02-21T14:31:01.962+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 8 */"2013-02-21T14:31:01.962+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
            + /* 9 */"2013-02-21T14:34:03.198+01:00;warejacob;w jacob;w.jacob@eu.x;een universiteit;promovendus;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
            + /* 10 */"2013-02-22T11:36:28.861+01:00;Anonymous;;;;;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
            + /* 10 */"2013-02-22T11:36:28.861+01:00;Anonymous;;;;;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
            + /* 10 */"2013-02-22T11:36:28.861+01:00;Anonymous;;;;;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 11 */"2013-02-23T11:29:40.653+01:00;Anonymous;;;;;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
            + /* 12 */"2013-02-23T11:33:00.059+01:00;Anonymous;;;;;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n";

    private final UserService userService;
    private EasyUser sessionUser;

    public MockedDLHL36028(final UserService userService, final EasyUser sessionUser)
    {
        this.userService = userService;
        this.sessionUser = sessionUser;
    }

    public DownloadList getList() throws Exception
    {
        final byte[] data = FileUtil.readFile(new File("src/test/resources/mock-xml/issue560-dlh36028.xml"));

        // assertions deduce the display name from the email, so exactly one dot in the name portion
        mockUser("s1234567", "n.erd@x.y", "", "", true); // line 1+2
        mockUser("laalbers", "l.aalbers@y.x", "", "", false); // line 3
        mockUser("pschilder", "p.schilder@eu.x", "een universiteit", "ass. professor", true); // line 4-6
        mockUser("juulesengel", "j.engel@u.x", "universiteit", "", false); // line 7
        mockUser("warejacob", "w.jacob@eu.x", "een universiteit", "promovendus", true);// line 8-9
        mockUser("hvanleeuwen", "h.van.leewen@au.x", "andere universiteit", "", false); // line 10
        mockUser("theodorus", "theo.dorus@eu.x", "een universitet", "student", false); // line 11-12
        return (DownloadList) JiBXObjectFactory.unmarshal(DownloadList.class, data);
    }

    private void mockUser(final String id, final String email, final String organization, final String function, final boolean logMyActions)
            throws ServiceException
    {
        final String displayName = email.split("@")[0];

        final EasyUserImpl user = new EasyUserImpl(id, email);
        user.addRole(Role.USER);
        user.setState(State.ACTIVE);
        user.setOrganization(organization);
        user.setInitials(displayName.replaceAll("\\.[^.]*$", ""));
        user.setSurname(displayName.split("^.*\\.")[1]);
        user.setFunction(function);
        user.setLogMyActions(logMyActions);
        EasyMock.expect(userService.getUserById(eq(sessionUser), eq(id))).andStubReturn(user);
    }

    public static Integer[] getNrOfFilesPerRow()
    {
        return NR_OF_FILES_PER_ROW;
    }

    public static String getArchivistExpectation()
    {
        // a getter prevents the need for recompiling (clear project) when the value changes
        return ARCHIVIST_EXPECTATION;
    }

    public static String getDepositorExpectation()
    {
        // expectations are formatted as download but is also used to test the panel content
        return DEPOSITOR_EXPECTATION;
    }
}
