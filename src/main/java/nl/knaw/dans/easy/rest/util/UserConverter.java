package nl.knaw.dans.easy.rest.util;

import nl.knaw.dans.easy.domain.model.user.EasyUser;

/**
 * A class to convert an EasyUser to a XML String.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public class UserConverter extends SimpleXmlWriter
{

    /**
     * A simple method that returns a XML String containing the information from
     * the given EasyUser.
     * 
     * @param user
     *            The EasyUser from which to extract the information.
     * @return A String containing the user information in XML format.
     */
    public static String convert(EasyUser user)
    {
        String xml = startNode("account");
        xml += addNode("username", user.getCommonName());
        xml += addNode("displayName", user.getDisplayName());
        xml += addNode("email", user.getEmail());
        xml += addNode("title", user.getTitle());
        xml += addNode("initials", user.getInitials());
        xml += addNode("prefix", user.getPrefixes());
        xml += addNode("surname", user.getSurname());
        xml += addNode("function", user.getFunction());
        xml += addNode("primaryDiscipline", user.getDiscipline1());
        xml += addNode("secondaryDiscipline", user.getDiscipline2());
        xml += addNode("tertiaryDiscipline", user.getDiscipline3());
        xml += addNode("telephone", user.getFunction());
        xml += addNode("DAI", user.getDai());
        xml += addNode("organisation", user.getOrganization());
        xml += addNode("departement", user.getDepartment());
        xml += addNode("address", user.getAddress());
        xml += addNode("postal", user.getPostalCode());
        xml += addNode("city", user.getCity());
        xml += addNode("country", user.getCountry());
        xml += endNode("account");
        return xml;
    }

}
