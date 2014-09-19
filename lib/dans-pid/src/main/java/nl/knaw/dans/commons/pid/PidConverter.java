package nl.knaw.dans.commons.pid;

class PidConverter {
    static final int ID_LENGTH = 6;
    static final int RADIX = 36;
    static final int SEPARATOR_POSITION = 4; // Note that it was 3 in EASY1
    static final String SEPARATOR = "-";

    private PidConverter() {
        // only static methods so no constructor needed
    }

    public static class InvalidUrn extends RuntimeException {
        // private constructors so anyone can catch, only owner can throw

        private static final long serialVersionUID = 1L;

        private InvalidUrn(final String explanation, final Throwable cause) {
            super(explanation, cause);
        }

        private InvalidUrn(final String cause) {
            super(cause);
        }
    };

    static String encode(final long id) {
        String result = Long.toString(id, RADIX).toLowerCase();

        while (result.length() < ID_LENGTH)
            result = "0" + result;
        return result.substring(0, SEPARATOR_POSITION) + SEPARATOR + result.substring(SEPARATOR_POSITION);
    }

    protected static String toUrn(final String prefix, final Long id) {
        return prefix + encode(id);
    }

}
