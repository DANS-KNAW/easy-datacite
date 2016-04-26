package nl.knaw.dans.pf.language.ddm.api;

import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import nl.knaw.dans.pf.language.ddm.handlers.SkippedFieldHandler;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkException;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import org.dom4j.tree.DefaultElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import static java.text.MessageFormat.format;
import static java.util.Collections.sort;

public class Ddm2EmdDoc {

    public static void main(String[] args) throws CrosswalkException, SAXException, XMLSerializationException, FileNotFoundException {

        PrintStream out = new PrintStream(args[0]);
        Ddm2EmdHandlerMap handlerMap = getHandlerMap();
        HashMap<String, NameSpace> nsMap = getNameSpaceMap();
        Attributes attributes = new AttributesImpl();
        ArrayList<String> handledTags = new ArrayList<String>(handlerMap.getKeys());
        sort(handledTags);
        out.println("<article id='mapping'><h1>Mapping</h1><table><tr><th>ddm</th><th>emd</th><th>note</th></tr>");
        for (String s : handledTags) {
            if (s.startsWith("/")) {
                String nsPrefix = s.replaceAll("[^/]*/", "").replaceAll(":.*", "");
                String uri = nsMap.get(nsPrefix).uri;
                String localName = s.replaceAll(".*:", "");
                CrosswalkHandler<EasyMetadata> handler = handlerMap.getHandler(uri, localName, attributes);
                if (handler == null)
                    out.println("<tr><td>" + s + "</td><td> </td><td>ignored</td></tr>");
                else if (handler instanceof SkippedFieldHandler)
                    out.println("<tr><td>" + s + "</td><td> </td><td>ignored with a warning</td></tr>");
                else {
                    EasyMetadata emd = toEmd(nsPrefix, uri, localName);
                    String firstTag = getFirstTag(emd);
                    if (firstTag.trim().isEmpty())
                        throw new CrosswalkException(format("{0}:{2}", nsPrefix, uri, localName), null);
                    out.println("<tr><td>" + s + "</td><td>" + firstTag + "</td><td> </td></tr>");
                }
            }
        }
        out.println("</table></article>");
    }

    private static EasyMetadata toEmd(String nsPrefix, String uri, String localName) throws CrosswalkException {
        String xmlns = nsPrefix.equals("ddm") ? "" : format("xmlns:{0}=''{1}''", nsPrefix, uri);
        String value = localName.equals("spatial") ? "<Point><pos>1.0 2.0</pos></Point>" : "2016";
        String element = format("<{0}:{1}  DAI=''info:eu-repo/dai/nl/1234567897''>{2}</{0}:{1}>", nsPrefix, localName, value);
        return emdFrom(xmlns, element);
    }

    private static String getFirstTag(EasyMetadata emd) throws XMLSerializationException, CrosswalkException {
        DefaultElement firstElement = (DefaultElement) new EmdMarshaller(emd).getXmlElement().elementIterator().next();
        if (firstElement.getQualifiedName().equals("emd:other"))
            return "";
        DefaultElement subElement = (DefaultElement) firstElement.elementIterator().next();
        return format("&lt;{0}&gt&lt;{1}&gt;", firstElement.getQualifiedName(), subElement.getQualifiedName());
    }

    private static EasyMetadata emdFrom(String xmlns, String element) throws CrosswalkException {

        // @formatter:off
        String ddmTemplate = "<?xml version='1.0' encoding='utf-8'?>\n" +
                "<ddm:DDM\n" +
                "  xmlns:ddm='http://easy.dans.knaw.nl/schemas/md/ddm/'\n" +
                "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n" +
                "  xmlns:id-type='http://easy.dans.knaw.nl/schemas/vocab/identifier-type/'\n" +
                "  %s" +
                ">\n" +
                "  %s" +
                "</ddm:DDM>";
        // @formatter:on

        // no validation to allow "one size to fits all"
        Ddm2EmdCrosswalk crosswalk = new Ddm2EmdCrosswalk(null);

        EasyMetadata emd = crosswalk.createFrom(String.format(ddmTemplate, xmlns, element));
        XMLErrorHandler errorHandler = crosswalk.getXmlErrorHandler();
        if (errorHandler.getErrors().size() > 0 || emd == null)
            throw new CrosswalkException(element + "\n" + errorHandler.getMessages().trim(), null);
        return emd;
    }

    private static Ddm2EmdHandlerMap getHandlerMap() throws CrosswalkException {
        // side effect: initialize statics (required for the handlerMap)
        emdFrom("", "");
        return (Ddm2EmdHandlerMap) new Ddm2EmdCrosswalk(null).handlerMap;
    }

    private static HashMap<String, NameSpace> getNameSpaceMap() {
        HashMap<String, NameSpace> nsMap = new HashMap<String, NameSpace>();
        for (NameSpace ns : NameSpace.values())
            nsMap.put(ns.prefix, ns);
        return nsMap;
    }
}
