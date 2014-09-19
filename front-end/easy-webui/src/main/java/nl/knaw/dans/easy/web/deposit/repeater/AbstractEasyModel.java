package nl.knaw.dans.easy.web.deposit.repeater;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.InvalidDateStringException;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

public abstract class AbstractEasyModel implements Serializable {

    private static final long serialVersionUID = -4270645863951808118L;

    private List<String> errors;

    public void addErrorMessage(String message) {
        if (errors == null) {
            errors = new ArrayList<String>();
        }
        errors.add(message);
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void clearErrors() {
        if (errors != null) {
            errors.clear();
        }
    }

    public String convertToString(Double d) {
        String value = null;
        if (d != null) {
            value = d.toString();
        }
        return value;
    }

    public Double convertToDouble(String value, String messagePart) {
        Double d = null;
        if (value != null) {
            try {
                d = Double.parseDouble(value);
            }
            catch (NumberFormatException e) {
                addErrorMessage("The value at " + messagePart + " is not a number: " + value);
            }
        }
        return d;
    }

    public IsoDate convertToDateTime(String value) {
        IsoDate isoDate = null;
        try {
            isoDate = new IsoDate(value);
        }
        catch (InvalidDateStringException e) {
            addErrorMessage(e.getMessage());
        }
        return isoDate;
    }

}
