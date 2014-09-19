package nl.knaw.dans.common.lang;

import java.io.Serializable;

public class PooledObject<T, I> implements Serializable {
    private static final long serialVersionUID = -1764960178220911377L;

    private T object;
    private I info;

    public PooledObject() {

    }

    public PooledObject(T object, I info) {
        this.setObject(object);
        this.setInfo(info);
    }

    public void setObject(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void setInfo(I info) {
        this.info = info;
    }

    public I getInfo() {
        return info;
    }
}
