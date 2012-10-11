/**
 * 
 */
package nl.knaw.dans.easy.web.authn;

// import nl.knaw.dans.easy.business.UserService;
// import nl.knaw.dans.easy.domain.authn.Authentication;
// import nl.knaw.dans.easy.domain.model.User.Role;
// import nl.knaw.dans.easy.web.common.ApplicationUser;
//
// import org.apache.wicket.security.hive.authentication.DefaultSubject;
// import org.apache.wicket.security.hive.authentication.Subject;
// import org.apache.wicket.security.hive.authentication.UsernamePasswordContext;
// import org.apache.wicket.security.hive.authorization.SimplePrincipal;
// import org.apache.wicket.security.strategies.LoginException;
// import org.apache.wicket.spring.injection.annot.SpringBean;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// /**
// * @author Herman Suijs TODO: use something different than UsernamePasswordContext, because it
// remembers the password.
// */
// public class EasyLoginContext extends UsernamePasswordContext
// {
//
// /**
// * Logger for this class.
// */
// private static final Logger LOGGER = LoggerFactory.getLogger(EasyLoginContext.class);
//
// private Authentication authentication;
//
//
// /**
// * Service to use for authentication.
// */
// @SpringBean(name = "userService")
// private UserService userService;
//
// /**
// * Constructor for logoff.
// */
// public EasyLoginContext()
// {
// // Constructor for Logoff
// }
//
// public EasyLoginContext(final Authentication authentication)
// {
// super(authentication.getUserId(), authentication.getCredentials());
// this.authentication = authentication;
//
// LOGGER.debug("Created logincontext. userId=" + authentication.toString());
// }
//
// /**
// * Method to inject userservice.
// *
// * @param userService
// * to inject
// */
// public final void setUserService(final UserService userService)
// {
// this.userService = userService;
// }
//
// /**
// * Login the user and return a subject with the authorization roles/principals.
// *
// * @see
// org.apache.wicket.security.hive.authentication.UsernamePasswordContext#getSubject(java.lang.String,
// * java.lang.String)
// */
// @Override
// protected Subject getSubject(final String username, final String password) throws LoginException
// {
// DefaultSubject subject = null;
//
// if (!authentication.checkCredentials(username, password))
// {
// return subject;
// }
//
// userService.authenticate(authentication);
//
// if (authentication.isCompleted())
// {
//
// final ApplicationUser applicationUser = new ApplicationUser(authentication.getUser());
//
// subject = new EasySubject(applicationUser);
// String userId = authentication.getUserId();
// for (Role role : authentication.getUser().getRoles())
// {
// subject.addPrincipal(new SimplePrincipal(role.toString()));
// LOGGER.debug("Role added for " + userId + ": " + role);
// }
//
// }
//
// return subject;
// }
//
// /**
// * Make sure the user is logged out.
// *
// * @param subject
// * the subject to logout.
// */
// @Override
// public void notifyLogoff(final Subject subject)
// {
// EasySubject easySubject = (EasySubject) subject;
// this.userService.logout(easySubject.getAppUser().getBusinessUser());
// }
// }
