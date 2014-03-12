package nl.knaw.dans.platform.language;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPHeaderElement;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlRequest;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlResponse;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.Validation;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidationMessage;
import org.tempuri.BasicHttpBinding_ISikb0102ServiceStub;
import org.tempuri.Sikb0102ServiceLocator;

public class SOAPClient
{
    private static final String WSSE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static String username = "bergh";
    private static String password = "cC!XzlKK";

    public static void main(String[] args) throws Exception
    {
        Sikb0102ServiceLocator service = new Sikb0102ServiceLocator();

        BasicHttpBinding_ISikb0102ServiceStub stub = (BasicHttpBinding_ISikb0102ServiceStub) service.getBasicHttpBinding_ISikb0102Service();

        QName securityHeader = new QName(WSSE, "Security", "wsse");
        SOAPHeaderElement secu = new SOAPHeaderElement(securityHeader);// new SOAPHeaderElement(WSSE,
                                                                       // "Security");
        // secu.addNamespaceDeclaration("wsse", WSSE);
        // secu.setMustUnderstand(true);
        // secu.setActor(null);

        MessageElement unt = new MessageElement(WSSE, "UsernameToken");
        secu.addChildElement(unt);

        MessageElement usern = new MessageElement(WSSE, "Username");
        usern.addTextNode(username);
        unt.addChildElement(usern);

        MessageElement passw = new MessageElement(WSSE, "Password");
        passw.addTextNode(password);
        unt.addChildElement(passw);

        stub.setHeader(secu);

        String xml = getXml();
        ValidateXmlRequest request = new ValidateXmlRequest(xml);

        ValidateXmlResponse response = null;

        try
        {
            response = stub.validate(request);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.err.println("endpoint=" + stub._getCall().getTargetEndpointAddress());
        // String req = stub._getCall().getUsername();
        // System.err.println(req);
        stub._getCall().getMessageContext().getRequestMessage().writeTo(System.err);

        System.err.println();
        System.err.println("version=" + response.getVersion());
        System.err.println("success=" + response.getSuccess());
        if (response.getErrorMessages() != null)
        {
            for (String error : response.getErrorMessages())
            {
                System.err.println("error=" + error);
            }
        }
        Validation validation = response.getValidation();
        System.err.println(validation);
        System.err.println("getErrorCount=" + validation.getErrorCount());
        System.err.println("getWarningCount=" + validation.getWarningCount());
        System.err.println("getValidXml=" + validation.getValidXml());
        if (validation.getMessages() != null)
        {
            for (ValidationMessage vm : validation.getMessages())
            {
                System.err.println();
                System.err.println("getGroupName=" + vm.getGroupName());
                System.err.println("getMessage=" + vm.getMessage());
                System.err.println("getValidationRuleId=" + vm.getValidationRuleId());
                System.err.println("getMessageType=" + vm.getMessageType());
            }
        }
    }

    private static String getXml() throws FileNotFoundException, IOException
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        // InputStream in = new FileInputStream("src/test/resources/test-files/pakbon_valid.xml");
        // String xml = IOUtils.toString(in);
        // in.close();
        // return xml;
    }

}
