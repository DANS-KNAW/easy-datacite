package nl.knaw.dans.platform.language.pakbon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;

import nl.knaw.dans.pf.language.xml.exc.ValidatorException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.commons.io.IOUtils;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlRequest;
import org.datacontract.schemas._2004._07.Sikb0102_WebService.ValidateXmlResponse;
import org.tempuri.BasicHttpBinding_ISikb0102ServiceStub;
import org.tempuri.Sikb0102ServiceLocator;

public class PakbonValidator
{
    private static final String WSSE_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String WSSE_PREFIX = "wsse";
    private static final String WSSE_SECURITY = "Security";
    private static final String WSSE_USERNAMETOKEN = "UsernameToken";
    private static final String WSSE_USERNAME = "Username";
    private static final String WSSE_PASSWORD = "Password";
    
    private static final Sikb0102ServiceLocator SERVICE = new Sikb0102ServiceLocator();    
    
    
    public String getUsername()
    {
        return PakbonCredentials.instance().getUsername();
    }

    public String getPassword()
    {
        return PakbonCredentials.instance().getPassword();
    }
    
    public ValidateXmlResponse validateXml(File file) throws ValidatorException, SOAPException, IOException
    {
        try
        {
            return validateXml(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            throw new ValidatorException(e);
        }
    }

    public ValidateXmlResponse validateXml(InputStream in) throws ValidatorException, SOAPException, IOException
    {
        String xml;
        try
        {
            xml = IOUtils.toString(in);
        }
        catch (IOException e)
        {
            throw new ValidatorException(e);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    throw new ValidatorException(e);
                }
            }
        }
        return validateXml(xml);
    }
    
    public ValidateXmlResponse validateXml(String xml) throws ValidatorException, SOAPException, IOException
    {
        BasicHttpBinding_ISikb0102ServiceStub stub = null;
        try
        {
            stub = (BasicHttpBinding_ISikb0102ServiceStub) SERVICE.getBasicHttpBinding_ISikb0102Service();
            
            // build up security header
            SOAPHeaderElement security = new SOAPHeaderElement(new QName(WSSE_NS, WSSE_SECURITY, WSSE_PREFIX));
            MessageElement usernameToken = new MessageElement(WSSE_NS, WSSE_USERNAMETOKEN);
            security.addChildElement(usernameToken);
            
            MessageElement uname = new MessageElement(WSSE_NS, WSSE_USERNAME);
            uname.addTextNode(getUsername());
            usernameToken.addChildElement(uname);
            
            MessageElement passw = new MessageElement(WSSE_NS, WSSE_PASSWORD);
            passw.addTextNode(getPassword());
            usernameToken.addChildElement(passw);
            
            stub.setHeader(security);
            
            // build request
            ValidateXmlRequest request = new ValidateXmlRequest(xml);
            
            // validate
            ValidateXmlResponse response = stub.validate(request);
            
            return response;
        }
        catch (ServiceException e)
        {
            throw new ValidatorException(e);
        }
        catch (RemoteException e)
        {
            throw new ValidatorException(e);
        }
        finally
        {
            if (stub != null && stub._getCall() != null && stub._getCall().getMessageContext() != null)
            {
                //stub._getCall().getMessageContext().getRequestMessage().writeTo(System.err);
                stub._getCall().getMessageContext().dispose();
            }
        }
    }

}
