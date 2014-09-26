package nl.knaw.dans.easy.web.authn;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.resetAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.junit.After;
import org.junit.Before;

public class UserInfoFixture {

    protected EasyApplicationContextMock applicationContext;
    protected EasyUserImpl shownUser;
    protected List<FederativeUserIdMap> federationUsers;
    protected FederativeUserRepo mockdFederativeUserRepo;

    @Before
    public void mockApplicationContext() throws Exception {
        final String shownUserId = "shownUserId";
        shownUser = new EasyUserImpl(shownUserId);
        shownUser.setInitials("s.");
        shownUser.setSurname("Hown");

        final UserService userService = createMock(UserService.class);
        expect(userService.isUserWithStoredPassword(eq(shownUser))).andReturn(true).anyTimes();
        expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andReturn(shownUser).anyTimes();

        federationUsers = new ArrayList<FederativeUserIdMap>();
        mockdFederativeUserRepo = createMock(FederativeUserRepo.class);
        expect(mockdFederativeUserRepo.findByDansUserId(shownUserId)).andStubReturn(federationUsers);

        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.setDepositService(mockDespositChoices());
        applicationContext.setUserService(userService);
        applicationContext.putBean("federativeUserRepo", mockdFederativeUserRepo);
    }

    /** Mock drop-down list for a discipline. */
    private DepositService mockDespositChoices() throws ServiceException {
        final ArrayList<KeyValuePair> choices = new ArrayList<KeyValuePair>();
        choices.add(new KeyValuePair("custom.Disciplines", "mockedDisciplines"));

        final DepositService depositService = createMock(DepositService.class);
        expect(depositService.getChoices(isA(String.class), (Locale) isNull())).andStubReturn(new ChoiceList(choices));
        return depositService;
    }

    @After
    public void verify() {
        verifyAll();
        resetAll();
    }

}
