package nl.knaw.dans.commons.pid;

class PidCaculator {
    // see http://en.wikipedia.org/wiki/Linear_congruential_generator
    // for details of the number generator

    static final long MODULO = (long) Math.pow(2, 31);
    private static final int FACTOR = 3 * 7 * 11 * 13 * 23; // = 69069
    private static final int INCREMENT = 5;

    static long getNext(final long seed) {
        // seed is whatever is in the database
        return ((seed * FACTOR) + INCREMENT) % MODULO;
    }
}
