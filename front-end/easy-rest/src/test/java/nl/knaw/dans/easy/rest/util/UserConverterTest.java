package nl.knaw.dans.easy.rest.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.junit.Before;
import org.junit.Test;

public class UserConverterTest {
    EasyUser user;

    @Before
    public void setUp() {
        user = mock(EasyUser.class);
        when(user.getCommonName()).thenReturn("username");
        when(user.getDisplayName()).thenReturn("displayname");
        when(user.getEmail()).thenReturn("email");
        when(user.getTitle()).thenReturn("title");
        when(user.getInitials()).thenReturn("initials");
        when(user.getPrefixes()).thenReturn("prefix");
        when(user.getSurname()).thenReturn("surname");
        when(user.getFunction()).thenReturn("function");
        when(user.getDiscipline1()).thenReturn("primaryDiscipline");
        when(user.getDiscipline2()).thenReturn("secondaryDiscipline");
        when(user.getDiscipline3()).thenReturn("tertiaryDiscipline");
        when(user.getTelephone()).thenReturn("telephone");
        when(user.getDai()).thenReturn("DAI");
        when(user.getOrganization()).thenReturn("organisation");
        when(user.getDepartment()).thenReturn("department");
        when(user.getAddress()).thenReturn("address");
        when(user.getPostalCode()).thenReturn("postal");
        when(user.getCity()).thenReturn("city");
        when(user.getCountry()).thenReturn("country");
    }

    @Test(expected = AssertionError.class)
    public void notInstantiable() {
        new UserConverter();
    }

    @Test
    public void conversion() {
        String xml = UserConverter.convert(user);
        String expectedXml = "<account>" + "<username>username</username>" + "<displayName>displayname</displayName>" + "<email>email</email>"
                + "<title>title</title>" + "<initials>initials</initials>" + "<prefix>prefix</prefix>" + "<surname>surname</surname>"
                + "<function>function</function>" + "<primaryDiscipline>primaryDiscipline</primaryDiscipline>"
                + "<secondaryDiscipline>secondaryDiscipline</secondaryDiscipline>" + "<tertiaryDiscipline>tertiaryDiscipline</tertiaryDiscipline>"
                + "<telephone>function</telephone>" + "<DAI>DAI</DAI>" + "<organisation>organisation</organisation>" + "<departement>department</departement>"
                + "<address>address</address>" + "<postal>postal</postal>" + "<city>city</city>" + "<country>country</country>" + "</account>";
        assertEquals(expectedXml, xml);
    }

}
