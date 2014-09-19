package nl.knaw.dans.easy.web.wicket;

import java.io.Serializable;

import org.apache.wicket.model.Model;

public class IdModel extends Model {
    private static final long serialVersionUID = -698492390294313403L;

    private String valueAndId;

    public IdModel() {}

    @Override
    public Serializable getObject() {
        return valueAndId;
    }

    @Override
    public void setObject(Serializable object) {
        this.valueAndId = (String) object;
    }

    protected String getSelectedId() {
        String id = null;
        if (valueAndId != null) {
            int index = valueAndId.indexOf(":");
            if (index > -1) {
                id = valueAndId.substring(index + 2);
            }
        }
        return id;
    }

}
