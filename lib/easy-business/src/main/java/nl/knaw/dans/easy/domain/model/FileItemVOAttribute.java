package nl.knaw.dans.easy.domain.model;

/** Names of implementing classes should equal properties of FileItemVO.hbm.xml */
public interface FileItemVOAttribute {
    public static abstract class Attribute<T> implements FileItemVOAttribute {
        private T value;

        public Attribute(T value) {
            this.value = value;
        }

        public String toString() {
            return value.toString();
        }
    }
}
