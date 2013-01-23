package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;
import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;
import nl.knaw.dans.pf.language.emd.types.IsoDate;
import nl.knaw.dans.pf.language.emd.types.Spatial;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchisCollector
{

    public static final String ARCHIS_URI_STRING = "http://archis2.archis.nl";

    public static final URI ARCHIS_URI = URI.create(ARCHIS_URI_STRING);

    public static final String ARCHIS_PDF = "http://archis2.archis.nl/reports/rwservlet?server=rep_owwnlms016&destype=cache&desformat=pdf&userid=DANS/EDNA2593@archis2p&report=OMG_OZK_uitgebreid.rep&p_ids=";

    public static final String BASE_URL = "http://archis2.archis.nl/reports/rwservlet?server=rep_owwnlms016&destype=cache&desformat=xml&userid=DANS/EDNA2593@archis2p&report=OZKMELD_stand.rep&p_ids=";

    public static final String SPATIAL_POINT_SCHEME = "RD";

    private static Logger logger = LoggerFactory.getLogger(ArchisCollector.class);

    private final EasyMetadata easyMetadata;

    private String currentX;
    private String currentPlace;

    private BasicIdentifier currentIdentifier;

    public ArchisCollector(EasyMetadata easyMetadata)
    {
        this.easyMetadata = easyMetadata;
    }

    public void collectInfo(BasicIdentifier archisIdentifier) throws ServiceException
    {
        currentIdentifier = archisIdentifier;
        String archisNumber = getDigits(currentIdentifier.getValue());
        logger.debug("Collecting Archis info. archisNumber=" + archisNumber);
        try
        {
            Document doc = getDocument(archisNumber);
            XPath x = XPath.newInstance("/OZKMELD_STAND/LIST_G_OMG/G_OMG/*");

            List<?> list = x.selectNodes(doc);
            for (Object obj : list)
            {
                if (obj instanceof Element)
                {
                    Element element = (Element) obj;
                    addElement(element);
                }
            }

        }
        catch (IOException e)
        {
            final String msg = "Unable to close Archis InputStream: ";
            logger.error(msg, e);
            throw new ServiceException(msg, e);
        }
        catch (JDOMException e)
        {
            final String msg = "Unable to read Archis document: ";
            logger.error(msg, e);
            throw new ServiceException(msg, e);
        }
    }

    private Document getDocument(String archisNumber) throws ServiceException, IOException
    {
        Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        InputStream inStream = null;
        try
        {
            inStream = getInputStream(archisNumber);
            doc = builder.build(inStream);
        }
        catch (JDOMException e)
        {
            final String msg = "Invalid xml. Unable to read Archis document: ";
            logger.error(msg, e);
            throw new ServiceException(msg, e);
        }
        catch (IOException e)
        {
            final String msg = "Unable to read Archis document: ";
            logger.error(msg, e);
            throw new ServiceException(msg, e);
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
        return doc;
    }

    private InputStream getInputStream(String archisNumber) throws ServiceException
    {
        InputStream inStream = null;
        try
        {
            URL url = new URL(BASE_URL + archisNumber);
            inStream = url.openStream();
        }
        catch (IOException e)
        {
            final String msg = "Unable to connect to Archis service ";
            logger.error(msg, e);
            throw new ServiceException(msg, e);
        }
        return inStream;
    }

    private void addElement(Element element)
    {
        // logger.debug("element.name=" + element.getName() + " element.value=" + element.getTextNormalize());
        String value = element.getTextNormalize();
        if (StringUtils.isBlank(value) || "Onbekend".equalsIgnoreCase(value) || "Niet van toepassing".equalsIgnoreCase(value))
        {
            return;
        }

        try
        {
            Method method = this.getClass().getDeclaredMethod("add" + element.getName(), String.class);
            method.invoke(this, value);
        }
        catch (SecurityException e)
        {
            final String msg = "Unable to write Archis info: ";
            logger.error(msg, e);
        }
        catch (NoSuchMethodException e)
        {
            // ignore this exception
        }
        catch (IllegalArgumentException e)
        {
            final String msg = "Unable to write Archis info: ";
            logger.error(msg, e);
        }
        catch (IllegalAccessException e)
        {
            final String msg = "Unable to write Archis info: ";
            logger.error(msg, e);
        }
        catch (InvocationTargetException e)
        {
            final String msg = "Unable to write Archis info: ";
            logger.error(msg, e);
        }

    }

    @SuppressWarnings("unused")
    private void addOMG_NR(String value)
    {
        currentIdentifier.setScheme(EmdConstants.SCHEME_ARCHIS_ONDERZOEK_M_NR);
        currentIdentifier.setIdentificationSystem(ARCHIS_URI);
        easyMetadata.getEmdIdentifier().add(currentIdentifier);
    }

    @SuppressWarnings("unused")
    private void addUITVOERDER(String value)
    {
        easyMetadata.getEmdCreator().getDcCreator().add(new BasicString(value));
    }

    @SuppressWarnings("unused")
    private void addPROJECTLEIDER(String value)
    {
        easyMetadata.getEmdCreator().getDcCreator().add(new BasicString(value));
    }

    @SuppressWarnings("unused")
    private void addDATUM_AANV(String value)
    {
        DateTime date = convertDate(value);
        if (date != null)
        {
            easyMetadata.getEmdDate().getEasCreated().add(new IsoDate(date));
        }
    }

    private DateTime convertDate(String value)
    {
        DateTime date = null;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        try
        {
            date = formatter.parseDateTime(value);
        }
        catch (IllegalArgumentException e)
        {
            logger.warn("Unexpected date format: " + value);
        }
        return date;
    }

    @SuppressWarnings("unused")
    private void addTOELICHTING(String value)
    {
        easyMetadata.getEmdDescription().getDcDescription().add(new BasicString(value));
    }

    @SuppressWarnings("unused")
    private void addONDERZOEKSTYPE(String value)
    {
        easyMetadata.getEmdSubject().getDcSubject().add(new BasicString(value));
    }

    @SuppressWarnings("unused")
    private void addOMG_KAARTBLAD(String value)
    {
        easyMetadata.getEmdCoverage().getTermsSpatial().add(new BasicString(value));
    }

    @SuppressWarnings("unused")
    private void addTOPONIEM(String value)
    {
        easyMetadata.getEmdCoverage().getTermsSpatial().add(new BasicString(value));
    }

    @SuppressWarnings("unused")
    private void addPLAATS(String value)
    {
        easyMetadata.getEmdCoverage().getTermsSpatial().add(new BasicString(value));
    }

    @SuppressWarnings("unused")
    private void addGEMEENTE(String value)
    {
        easyMetadata.getEmdCoverage().getTermsSpatial().add(new BasicString(value));
        currentPlace = value;
    }

    @SuppressWarnings("unused")
    private void addPROVINCIE(String value)
    {
        easyMetadata.getEmdCoverage().getTermsSpatial().add(new BasicString(value));
    }

    @SuppressWarnings("unused")
    private void addX_COORD(String value)
    {
        //        try
        //        {
        //            currentX = Double.valueOf(value);
        //        }
        //        catch (NumberFormatException e)
        //        {
        //            logger.warn("Unexpected number format for Double: " + value);
        //        }
        currentX = value;
    }

    @SuppressWarnings("unused")
    private void addY_COORD(String value)
    {
        // only works if X_COORD before Y_COORD !!
        try
        {
            if (currentX != null)
            {
                //Double yPoint = Double.valueOf(value);
                String yPoint = value;
                easyMetadata.getEmdCoverage().getEasSpatial().add(new Spatial(currentPlace, new Spatial.Point(SPATIAL_POINT_SCHEME, currentX, yPoint)));
            }
        }
        //        catch (NumberFormatException e)
        //        {
        //            logger.warn("Unexpected number format for Double: " + value);
        //        }
        finally
        {
            currentX = null;
            currentPlace = null;
        }

    }

    public static String getDigits(String s)
    {
        StringBuilder sb = new StringBuilder();
        if (s != null)
        {
            boolean endOfDigits = false;
            for (int i = 0; i < s.length() && !endOfDigits; i++)
            {
                char c = s.charAt(i);
                if (Character.isDigit(c))
                {
                    sb.append(c);
                }
                else
                {
                    endOfDigits = true;
                }
            }
        }
        return sb.toString();
    }

}
