package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasyUserService;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.exceptions.AnonymousUserException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class TestResourceTest extends RestTest {
	private final EasyUser user = new EasyUserImpl();
	private final String firstname = "Dennis";

	@Test
	public void helloWorld() {
		WebResource webResource = resource().path("hello/world");
		String responseMsg = webResource.get(String.class);

		assertEquals(200, webResource.head().getStatus());
		assertEquals("hello, world", responseMsg);
	}

	@Test
	public void helloUser() {
		setUpUserService();

		ClientResponse response = resource().path("hello")
				.header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
				.get(ClientResponse.class);

		String entity = response.getEntity(String.class);

		assertEquals(200, response.getStatus());
		assertEquals("hello, " + firstname, entity);
	}

	private void setUpUserService() {
		Services services = new Services();

		UserService userService = new EasyUserService() {
			@Override
			public void authenticate(Authentication authentication)
					throws ServiceException {
				user.setFirstname(firstname);
				authentication.setUser(user);
			}
		};

		services.setUserService(userService);
	}

	@Test
	public void helloAnonymousUser() {
		setUpAnonymousUserException();

		ClientResponse response = resource().path("hello").get(
				ClientResponse.class);

		assertEquals(200, response.getStatus());
	}

	private void setUpAnonymousUserException() {
		Services services = new Services();

		UserService userService = new EasyUserService() {
			@Override
			public void authenticate(Authentication authentication)
					throws ServiceException {
				throw (new AnonymousUserException("WTF"));
			}
		};

		services.setUserService(userService);
	}

	@Test
	public void helloUserServiceException() {
		setUpUserException();

		ClientResponse response = resource().path("hello")
				.header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
				.get(ClientResponse.class);

		assertEquals(500, response.getStatus());
	}

	private void setUpUserException() {
		Services services = new Services();

		UserService userService = new EasyUserService() {
			@Override
			public void authenticate(Authentication authentication)
					throws ServiceException {
				throw (new ServiceException("WTF"));
			}
		};

		services.setUserService(userService);
	}

}
