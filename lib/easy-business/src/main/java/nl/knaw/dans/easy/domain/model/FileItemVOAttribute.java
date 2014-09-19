package nl.knaw.dans.easy.domain.model;

/** Names of implementing classes should equal properties of FileItemVO.hbm.xml */
public interface FileItemVOAttribute {
    public static class Attribute<T> implements FileItemVOAttribute {
        private T value;

        public Attribute(T value) {
            this.value = value;
        }

        public String toString() {
            return value.toString();
        }
    }

    public static class MimeType extends Attribute<String> {
        public MimeType(String value) {
            super(value);
        }
    }
}
