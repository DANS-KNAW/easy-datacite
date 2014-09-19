package nl.knaw.dans.easy.domain.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.junit.Test;

// ecco: CHECKSTYLE: OFF

public class UserImplTest {

    @Test
    public void testEquals() {
        // equal userId, name and email should lead to equals = true and same hashcode
        EasyUser user1 = new EasyUserImpl();
        user1.setId("id");

        user1.setEmail("email");

        EasyUser user2 = new EasyUserImpl();
        user2.setId("id");

        user2.setEmail("email");

        assertTrue(user1.equals(user2));

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    public void synchronizeOn() {
        EasyUser user = new EasyUserImpl();

        // fields to synchronize
        user.setAddress("address 1");
        user.setCity("city 1");
        user.setCountry("country 1");
        user.setDepartment("department 1");
        user.setEmail("email 1");
        user.setFirstname("firstname 1");
        user.setFunction("function 1");
        user.setInitials("initials 1");
        user.setOrganization("organization 1");
        user.setPostalCode("postalCode 1");
        user.setPrefixes("prefixes 1");
        user.setSurname("surname 1");
        user.setTelephone(null);
        user.setTitle("title 1");
        // fields should not be synchronized
        user.setPassword("password 1");
        user.setSHAEncryptedPassword("encrypted Password 1");
        user.setState(User.State.ACTIVE);
        user.setId("userId 1");

        EasyUser otherUser = new EasyUserImpl();
        otherUser.setAddress("address 2");
        otherUser.setCity("city 2");
        otherUser.setCountry("country 2");
        otherUser.setDepartment("department 2");
        otherUser.setEmail("email 2");
        otherUser.setFirstname("firstname 2");
        otherUser.setFunction("function 2");
        otherUser.setInitials("initials 2");
        otherUser.setOrganization("organization 2");
        otherUser.setPostalCode("postalCode 2");
        otherUser.setPrefixes("prefixes 2");
        otherUser.setSurname("surname 2");
        otherUser.setTelephone("22-33");
        otherUser.setTitle("title 2");

        otherUser.setPassword("password 2");
        otherUser.setSHAEncryptedPassword("encrypted Password 2");
        otherUser.setState(User.State.BLOCKED);
        otherUser.setId("userId 2");

        user.synchronizeOn(otherUser);

        // synchronized fields
        assertEquals("address 2", user.getAddress());
        assertEquals("city 2", user.getCity());
        assertEquals("country 2", user.getCountry());
        assertEquals("department 2", user.getDepartment());
        assertEquals("email 2", user.getEmail());
        assertEquals("firstname 2", user.getFirstname());
        assertEquals("function 2", user.getFunction());
        assertEquals("initials 2", user.getInitials());
        assertEquals("organization 2", user.getOrganization());
        assertEquals("postalCode 2", user.getPostalCode());
        assertEquals("prefixes 2", user.getPrefixes());
        assertEquals("surname 2", user.getSurname());
        assertEquals("22-33", user.getTelephone());
        assertEquals("title 2", user.getTitle());

        // non synchronized fields
        assertEquals("password 1", user.getPassword());
        assertEquals(User.State.ACTIVE, user.getState());
        assertEquals("userId 1", user.getId());
        assertEquals("encrypted Password 1", user.getSHAEncryptedPassword());
    }

}
