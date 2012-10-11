/**
 * 
 */
package nl.knaw.dans.easy.web.authn;

// import nl.knaw.dans.easy.web.common.ApplicationUser;
//
// import org.apache.commons.lang.builder.EqualsBuilder;
// import org.apache.commons.lang.builder.HashCodeBuilder;
// import org.apache.wicket.security.hive.authentication.DefaultSubject;
//
// /**
// * Easy logged in subject.
// *
// * @author Herman Suijs
// */
// public class EasySubject extends DefaultSubject
// {
//
// /**
// * Serial version UID.
// */
// private static final long serialVersionUID = 8885784128197180239L;
//
// /**
// * ApplicationUser represented by this subject. TODO: maybe integrate ApplicationUser and EasySubject.
// */
// private final ApplicationUser appUser;
//
// /**
// * Default constructor with represented user.
// *
// * @param applicationUser represented user
// */
// public EasySubject(final ApplicationUser applicationUser)
// {
// this.appUser = applicationUser;
// }
//
// /**
// * Return details of the current user.
// *
// * @return current user.
// */
// public ApplicationUser getAppUser()
// {
// return this.appUser;
// }
//
// /**
// * Return string representation.
// *
// * @return string representation
// */
// @Override
// public String toString()
// {
// return "Easy subject with name: " + appUser.getCommonName() + " and id: " + appUser.getUserId();
// }
//
// /**
// * Return hashcode.
// *
// * @return hashcode
// */
// @Override
// public int hashCode()
// {
// return new HashCodeBuilder(255, 3).append(appUser.getUserId()).toHashCode();
// }
//
// /**
// * Test for equality.
// *
// * @param obj object to check
// * @return true if object is equal
// */
// @Override
// public boolean equals(final Object obj)
// {
// boolean equals = false;
// if (obj == this)
// {
// equals = true;
// }
// else if (obj.getClass().equals(this.getClass()))
// {
// EasySubject anotherSubject = (EasySubject) obj;
// equals = new EqualsBuilder().append(this.getAppUser().getBusinessUser().getUserId(),
// anotherSubject.getAppUser().getBusinessUser().getUserId())
// .append(this.getAppUser().getUserId(), anotherSubject.getAppUser().getUserId()).isEquals();
// }
// return equals;
// }
// }
