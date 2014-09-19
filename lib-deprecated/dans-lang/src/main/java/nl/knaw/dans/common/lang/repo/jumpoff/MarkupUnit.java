package nl.knaw.dans.common.lang.repo.jumpoff;

import java.io.IOException;

import nl.knaw.dans.common.lang.repo.AbstractBinaryUnit;

/**
 * BinaryUnit for storing html content. The content will always be surrounded by
 * 
 * <pre>
 *    &lt;div class="jumpoffpage"&gt;
 *       ...
 *    &lt;/div&gt;
 * </pre>
 * 
 * The content is then stored as managed content in datastream pointing to a file, not as inline xhtml.
 */
public class MarkupUnit extends AbstractBinaryUnit {

    public static final String MIME_TYPE = "text/html";

    public static final String BODY_CLASS = "jumpoffpage";

    public static final String CLASS_START_ELEMENT = "<div class=\"" + BODY_CLASS + "\">";

    public static final String CLASS_END_ELEMENT = "</div>";

    private static final long serialVersionUID = -960182409044708057L;

    private final String unitId;
    private final String unitLabel;

    private boolean contentChanged;
    private String html;

    public MarkupUnit(String unitId, String unitLabel) {
        this.unitId = unitId;
        this.unitLabel = unitLabel;
    }

    @Override
    public String getUnitId() {
        return unitId;
    }

    /**
     * Set the html to store.
     * 
     * @param markup
     *        html to store
     */
    public void setHtml(String markup) {
        contentChanged = true;
        if (markup == null || "".equals(markup)) {
            html = "";
        } else {
            if (markup.trim().startsWith(CLASS_START_ELEMENT)) {
                html = markup;
            } else {
                html = CLASS_START_ELEMENT + "\n" + markup + "\n" + CLASS_END_ELEMENT;
            }
        }
    }

    @Override
    public boolean hasFile() {
        return contentChanged;
    }

    /**
     * Prepares this HtmlMarkup for storage, by writing the content to a temporary file.
     */
    @Override
    public void prepareForStorage() throws IOException {
        setFileContent(getHtml().getBytes(), unitLabel, MIME_TYPE);
    }

    // /**
    // * Use only for deserialization, that is while reading from store.
    // *
    // * @param html
    // * the stored markup stream.
    // */
    // public void setStoredHtml(String html)
    // {
    // this.html = html;
    // }

    /**
     * Get the html content, stored or just set.
     * 
     * @return content of this HtmlMarkupUnit
     */
    public String getHtml() {
        if (html == null) {
            byte[] fileContent = getBinaryContent();
            html = fileContent == null ? "" : new String(fileContent);
        }
        return html;
    }

}
