package nl.knaw.dans.common.fedora.fox;

import java.net.URI;

public class ContentLocation {
    public enum Type {
        // @formatter:off
        INTERNAL_ID, URL
        // @formatter:on
    }

    private Type type;
    private URI ref;

    protected ContentLocation() {}

    public ContentLocation(Type type, URI ref) {
        this.type = type;
        this.ref = ref;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public URI getRef() {
        return ref;
    }

    public void setRef(URI ref) {
        this.ref = ref;
    }

}
