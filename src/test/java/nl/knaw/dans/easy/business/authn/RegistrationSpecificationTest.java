package nl.knaw.dans.easy.business.authn;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.easy.domain.authn.Registration;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.junit.Test;

public class RegistrationSpecificationTest
{
	@Test
	public void hasAllowedUserIdTest()
	{	
		EasyUserImpl user = new EasyUserImpl("granted");
		Registration reg = new Registration(user);
		assertFalse(RegistrationSpecification.hasAllowedUserID(reg));

		user = new EasyUserImpl("reTurned");
		reg = new Registration(user);
		assertFalse(RegistrationSpecification.hasAllowedUserID(reg));

		user = new EasyUserImpl("deNiED");
		reg = new Registration(user);
		assertFalse(RegistrationSpecification.hasAllowedUserID(reg));

		user = new EasyUserImpl("Submitted");
		reg = new Registration(user);
		assertFalse(RegistrationSpecification.hasAllowedUserID(reg));

		user = new EasyUserImpl("sjakie");
		reg = new Registration(user);
		assertTrue(RegistrationSpecification.hasAllowedUserID(reg));
	}
}
