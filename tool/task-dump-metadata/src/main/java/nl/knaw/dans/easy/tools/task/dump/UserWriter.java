package nl.knaw.dans.easy.tools.task.dump;

import java.io.PrintWriter;

import javax.naming.Binding;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserWriter {
    private static final Logger logger = LoggerFactory.getLogger(UserWriter.class);

    private Binding user;
    private PrintWriter writer;
    private Attributes attributes;
    private String userName;

    UserWriter user(Binding user) {
        this.user = user;
        return this;
    }

    UserWriter writer(PrintWriter writer) {
        this.writer = writer;
        return this;
    }

    void write() {
        try {
            DirContext context = (DirContext) user.getObject();
            attributes = context.getAttributes("");
            userName = asString("uid");
            logger.info(String.format("Writing user '%s'", userName));

            writeAttribute("username", asString("uid"));
            writeAttribute("address", asString("postalAddress"));
            writeAttribute("postalcode", asString("postalCode"));
            writeAttribute("city", asString("l"));
            writeAttribute("country", asString("st"));
            writeAttribute("email", asString("mail"));
            writeAttribute("newsletter", booleanAsString("dansNewsletter"));
            writeAttribute("telephone", asString("telephoneNumber"));
            writeAttribute("title", asString("title"));
            writeAttribute("displayname", asString("displayName"));
            writeAttribute("initials", asString("initials"));
            writeAttribute("prefix", asString("dansPrefixes"));
            writeAttribute("surname", asString("sn"));
            writeAttribute("hashedPassword", byteArrayAsString("userPassword"));
            writeAttribute("inactive", isInactiveAsString("dansState"));
            writeAttribute("organisation", asString("o"));
            writeAttribute("department", asString("ou"));
            writeAttribute("function", asString("employeeType"));

        }
        catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeAttribute(String name, String value) throws NamingException {
        writer.println(String.format("USER[%s]:%s=%s", userName, name, value));
    }

    private String asString(String name) throws NamingException {
        Attribute a = attributes.get(name);
        return a == null || a.get() == null ? "" : a.get().toString();
    }

    private String booleanAsString(String name) throws NamingException {
        return asString(name).toLowerCase();
    }

    private String byteArrayAsString(String name) throws NamingException {
        Attribute a = attributes.get(name);
        return a == null || a.get() == null ? "" : new String((byte[]) a.get());
    }

    private String isInactiveAsString(String name) throws NamingException {
        Attribute a = attributes.get(name);
        return Boolean.toString(!"ACTIVE".equals(a.get().toString()));
    }
}
