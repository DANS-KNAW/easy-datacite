package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;

public class KeyValuePair implements Serializable {

    public static final String PROP_VALUE = "value";
    public static final String PROP_KEY = "key";

    private static final long serialVersionUID = 4159707050416742894L;
    private String key;
    private String value;
    private int indent = 0;

    public KeyValuePair() {

    }

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " key=" + key + " value=" + value;
    }

    public void setIndent(int indent) {
        this.indent = indent;
    }

    public int getIndent() {
        return indent;
    }

    @Override
    public boolean equals(Object obj) {
        boolean eq = false;
        if (obj == null) {
            eq = false;
        } else if (obj instanceof KeyValuePair) {
            KeyValuePair kvp = (KeyValuePair) obj;
            eq = new EqualsBuilder().append(this.key, kvp.key).isEquals();
        }
        return eq;
    }

    @Override
    public int hashCode() {
        return key == null ? 0 : key.hashCode();
    }

}
