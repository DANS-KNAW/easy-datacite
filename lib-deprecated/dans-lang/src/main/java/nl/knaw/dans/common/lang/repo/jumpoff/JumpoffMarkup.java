package nl.knaw.dans.common.lang.repo.jumpoff;

import java.net.URI;

import nl.knaw.dans.common.lang.repo.AbstractTimestampedObject;
import nl.knaw.dans.common.lang.repo.MetadataUnit;

public class JumpoffMarkup extends AbstractTimestampedObject implements MetadataUnit {

    public static final String UNIT_ID = "JOM";

    public static final String UNIT_LABEL = "Markup for jumpoff page";

    public static final String UNIT_FORMAT = "http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd";

    public static final URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);

    public static final String ROOT_START_ELEMENT = "<jumpoff>";

    public static final String ROOT_END_ELEMENT = "</jumpoff>";

    public static final String ROOT_ELEMENT = "<jumpoff/>";

    public static final String BODY_CLASS = "jumpoffpage";

    public static final String CLASS_START_ELEMENT = "<div class=\"" + BODY_CLASS + "\">";

    public static final String CLASS_END_ELEMENT = "</div>";

    private static final long serialVersionUID = 1L;

    private String markup = ROOT_ELEMENT;

    public String getUnitFormat() {
        return UNIT_FORMAT;
    }

    public URI getUnitFormatURI() {
        return UNIT_FORMAT_URI;
    }

    public String getUnitId() {
        return UNIT_ID;
    }

    public String getUnitLabel() {
        return UNIT_LABEL;
    }

    public boolean isVersionable() {
        return false;
    }

    public void setVersionable(boolean versionable) {}

    public byte[] asObjectXML() {
        return markup.getBytes();
    }

    public void setMarkup(String markup) {
        if (markup == null || "".equals(markup)) {
            this.markup = ROOT_ELEMENT;
        } else if (markup.trim().startsWith(ROOT_START_ELEMENT)) {
            this.markup = markup;
        } else {
            if (markup.trim().startsWith(CLASS_START_ELEMENT)) {
                this.markup = ROOT_START_ELEMENT + markup + ROOT_END_ELEMENT;
            } else {
                this.markup = ROOT_START_ELEMENT + CLASS_START_ELEMENT + "\n" + markup + "\n" + CLASS_END_ELEMENT + ROOT_END_ELEMENT;
            }
        }
    }

    public String getMarkup() {
        if (ROOT_ELEMENT.equals(markup)) {
            return "";
        } else {
            return markup.substring(ROOT_START_ELEMENT.length(), markup.length() - ROOT_END_ELEMENT.length());
        }
    }

}
