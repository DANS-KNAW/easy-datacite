package nl.knaw.dans.common.lang.search.simple;

import nl.knaw.dans.common.lang.search.Field;

public class SimpleField<T> implements Field<T> {
    private static final long serialVersionUID = -6244061070328879614L;

    private T value;

    private String name;

    public SimpleField(Field<T> copyMe) {
        this.name = copyMe.getName();
        this.value = copyMe.getValue();
    }

    public SimpleField(String name) {
        this.name = name;
    }

    public SimpleField(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + "[name = '" + getName() + "' value = '" + getValue().toString() + "']";
    }
}
