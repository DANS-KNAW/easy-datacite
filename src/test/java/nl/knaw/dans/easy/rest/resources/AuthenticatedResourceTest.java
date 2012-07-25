package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.ws.rs.core.HttpHeaders;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasyUserService;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;

public class AuthenticatedResourceTest {

	private AuthenticatedResource resource;
	private HttpHeaders requestHeadersMock;

	@Before
	public void setUp() {
		resource = new AuthenticatedResource();
		requestHeadersMock = mock(HttpHeaders.class);
		resource.setRequestHeaders(requestHeadersMock);
	}

	@Test
	public void authenticateWithNullHeaders() throws ServiceException {
		when(requestHeadersMock.getRequestHeader(isA(String.class)))
				.thenReturn(null);
		EasyUser user = resource.authenticate();
		assertTrue(user instanceof EasyUserAnonymous);
	}

	@Test
	public void authenticateWithEmptyHeaders() throws ServiceException {
		when(requestHeadersMock.getRequestHeader(isA(String.class)))
				.thenReturn(new ArrayList<String>());
		EasyUser user = resource.authenticate();
		assertTrue(user instanceof EasyUserAnonymous);
	}

	@Test
	public void authenticateWithIncorrectType() throws ServiceException {
		ArrayList<String> authHeader = new ArrayList<String>();
		authHeader.add("Digest dXNlcm5hbWU6cGFzc3dvcmQ="); // username:password
		when(requestHeadersMock.getRequestHeader(isA(String.class)))
				.thenReturn(authHeader);
		EasyUser user = resource.authenticate();
		assertTrue(user instanceof EasyUserAnonymous);
	}

	@Test
	public void authenticateWithIncorrectUsernamePasswordPattern()
			throws ServiceException {
		ArrayList<String> authHeader = new ArrayList<String>();
		authHeader.add("Basic dXNlcm5hbWUgcGFzc3dvcmQ="); // username password
		when(requestHeadersMock.getRequestHeader(isA(String.class)))
				.thenReturn(authHeader);
		EasyUser user = resource.authenticate();
		assertTrue(user instanceof EasyUserAnonymous);
	}

	@Test
	public void authenticateWithNullUserReturned() throws ServiceException {
		setUpUserService(null);
		ArrayList<String> authHeader = new ArrayList<String>();
		authHeader.add("Basic dXNlcm5hbWU6cGFzc3dvcmQ="); // username:password
		when(requestHeadersMock.getRequestHeader(isA(String.class)))
				.thenReturn(authHeader);
		EasyUser user = resource.authenticate();
		assertTrue(user instanceof EasyUserAnonymous);
	}

	private void setUpUserService(final EasyUser user) {
		Services services = new Services();
		EasyUserService userService = new EasyUserService() {
			public void authenticate(Authentication authentication)
					throws ServiceException {
				authentication.setUser(user);
			}
		};
		services.setUserService(userService);
	}

	@Test
	public void authenticateSuccesfull() throws ServiceException {
		EasyUser user = new EasyUserImpl();
		setUpUserService(user);
		ArrayList<String> authHeader = new ArrayList<String>();
		authHeader.add("Basic dXNlcm5hbWU6cGFzc3dvcmQ="); // username:password
		when(requestHeadersMock.getRequestHeader(isA(String.class)))
				.thenReturn(authHeader);
		EasyUser user2 = resource.authenticate();
		assertEquals(user, user2);
	}

}
