package nl.knaw.dans.common.fedora.fox;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.joda.time.DateTime;

public class DatastreamVersion extends AbstractTimestampedJiBXObject<DatastreamVersion> {

    public static String MIMETYPE_XML = "text/xml";
    public static ContentDigestType CONTENT_DIGEST_TYPE = ContentDigestType.SHA_1;
    private static final long serialVersionUID = 2904449323405243287L;

    private static final int MAX_ID_LENGTH = 64;

    private String versionId;
    private String label;
    private DateTime created;
    private String mimeType;
    private Set<URI> altIds = new LinkedHashSet<URI>();
    private URI formatURI;
    private long size;
    private ContentDigest contentDigest = new ContentDigest(CONTENT_DIGEST_TYPE.code, null);
    private ContentLocation contentLocation;
    private XMLContent xmlContent;
    private byte[] binaryContent;

    /**
     * Used by JiBX serialization.
     */
    protected DatastreamVersion() {

    }

    /**
     * Constructs a new DatastreamVersion. A DatastreamVersion can also be obtained by method {@link Datastream#addDatastreamVersion(String, String)}.
     * <p/>
     * If a DatastreamVersion is not ingested as part of a DigitalObject, but is used stand-alone-wise to modify an existing Datastream, then the versionId will
     * not be used entirely, only the streamId part of the versionId will be used, i.e the part of the versionId before the first period ('.').
     * 
     * @see Datastream#addDatastreamVersion(String, String)
     * @param versionId
     *        the versionId
     * @param mimeType
     *        the mimeType, can be null
     */
    public DatastreamVersion(final String versionId, final String mimeType) {
        setVersionId(versionId);
        this.mimeType = mimeType;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        if (versionId == null || versionId.length() <= MAX_ID_LENGTH) {
            this.versionId = versionId;
        } else {
            throw new IllegalArgumentException("The string '" + versionId + "' is not allowed as Fedora Datastream id.");
        }
    }

    public String getStreamId() {
        String streamId = null;
        if (versionId != null) {
            streamId = versionId.split("\\.")[0];
        }
        return streamId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DateTime getCreated() {
        return created;
    }

    // /**
    // * On Fedora the foxml datastreamVersion CREATED attribute is read only.
    // *
    // * @param created
    // * some date
    // */
    // protected void setCreated(DateTime created)
    // {
    // this.created = created;
    // }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Set<URI> getAltIds() {
        return altIds;
    }

    void setAltIdString(String list) {
        altIds.clear();
        if (list != null) {
            String[] ids = list.split(" ");
            for (String id : ids) {
                try {
                    URI uri = new URI(id);
                    altIds.add(uri);
                }
                catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    String getAltIdString() {
        if (altIds.isEmpty()) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            int count = 0;
            for (URI uri : altIds) {
                builder.append(uri.toString());
                if (++count < altIds.size()) {
                    builder.append(" ");
                }
            }
            return builder.toString();
        }
    }

    public List<String> getAltIdList() {
        List<String> idList = new ArrayList<String>();
        for (URI uri : altIds) {
            idList.add(uri.toString());
        }
        return idList;
    }

    public String[] getAltIdArray() {
        return getAltIdList().toArray(new String[altIds.size()]);
    }

    public URI getFormatURI() {
        return formatURI;
    }

    public void setFormatURI(URI formatURI) {
        this.formatURI = formatURI;
    }

    public long getSize() {
        return size;
    }

    public String getChecksumType() {
        return contentDigest == null ? null : contentDigest.typeCode;
    }

    public ContentDigestType getContentDigestType() {
        return contentDigest == null ? null : ContentDigestType.forCode(contentDigest.typeCode);
    }

    public String getContentDigest() {
        return contentDigest == null ? null : contentDigest.digest;
    }

    public void setContentDigest(final ContentDigestType type, final String digest) {
        if (type == null || digest == null) {
            contentDigest = null;
        } else {
            contentDigest = new ContentDigest(type.code, digest);
        }
    }

    public XMLContent getXmlContent() {
        return xmlContent;
    }

    // TODO: here unnecessary of xml writing (and parsing) is taking place
    // this should be replaced for performance reasons. This code is still
    // here, because everything was based on JiBX in the beginning
    public String getXmlContentString() {
        Element xmlEl = xmlContent.getElement();
        Document doc = xmlEl.getDocument();
        String encoding = "UTF-8";
        if (doc != null)
            doc.getXMLEncoding();

        Writer osw = new StringWriter();
        OutputFormat opf = new OutputFormat("  ", true, encoding);
        XMLWriter writer = new XMLWriter(osw, opf);
        try {
            writer.write(xmlEl);
            writer.close();
        }
        catch (IOException e) {
            return "";
        }
        return osw.toString();
    }

    public void setXmlContent(XMLContent xmlContent) {
        this.xmlContent = xmlContent;
    }

    // TODO: the parsing here is completely unnecessary and should be replaced
    // for performance reasons. This code is still
    // here, because everything was based on JiBX in the beginning
    public void setXmlContent(String xmlString) throws DocumentException {
        Document document = DocumentHelper.parseText(xmlString);
        this.xmlContent = new XMLContent(document.getRootElement());
    }

    // TODO: the parsing here is completely unnecessary and should be replaced
    // for performance reasons. This code is still
    // here, because everything was based on JiBX in the beginning
    public void setXmlContent(byte[] xmlContent) throws DocumentException {
        setXmlContent(new String(xmlContent));
    }

    public void setXmlContent(Element element) {
        setXmlContent(new XMLContent(element));
    }

    public String getDsLocation() {
        return contentLocation == null ? null : contentLocation.getRef().toString();
    }

    public ContentLocation getContentLocation() {
        return contentLocation;
    }

    public void setContentLocation(ContentLocation contentLocation) {
        this.contentLocation = contentLocation;
    }

    public void setContentLocation(ContentLocation.Type type, URI ref) {
        setContentLocation(new ContentLocation(type, ref));
    }

    public byte[] getBinaryContent() {
        return binaryContent;
    }

    public void setBinaryContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }

    public Element getXmlContentElement() {
        return xmlContent == null ? null : xmlContent.getElement();
    }

    // ecco: CHECKSTYLE: OFF

    /**
     * Holds type and checksum of a content digest.
     * 
     * @author ecco
     */
    public static class ContentDigest {

        String typeCode;
        String digest;

        ContentDigest() {

        }

        ContentDigest(String typeCode, String digest) {
            this.typeCode = typeCode;
            this.digest = digest;
        }

    }

}
