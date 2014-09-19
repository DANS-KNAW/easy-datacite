package nl.knaw.dans.pf.language.ddm.handlertypes;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.types.InvalidDateStringException;
import nl.knaw.dans.pf.language.emd.types.IsoDate;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkHandler;

import org.joda.time.DateTime;
import org.xml.sax.SAXException;

public abstract class IsoDateHandler extends CrosswalkHandler<EasyMetadata> {
    /** @return now with just day precision */
    protected static DateTime getToday() {
        return new DateTime(new DateTime().toString("YYYY-MM-dd"));
    }

    protected void validateRange(final IsoDate isoDate, final DateTime min, final DateTime max) throws SAXException {
        if (isoDate.getValue().isBefore(min) || isoDate.getValue().isAfter(max))
            error("value out of range (" + min + ", " + max + ")");
    }

    protected IsoDate createDate(final String uri, final String localName) throws SAXException {
        final String value = getCharsSinceStart().trim();
        if (value.length() == 0)
            return null;
        final IsoDate isoDate = new IsoDate();
        if (value != null && value.trim().length() > 0) {
            try {
                isoDate.setValueAsString(value.trim());
            }
            catch (final InvalidDateStringException e) {
                error(e.getMessage());
            }
        }
        return isoDate;
    }
}
