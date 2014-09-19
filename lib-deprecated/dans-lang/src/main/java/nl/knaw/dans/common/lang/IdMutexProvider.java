package nl.knaw.dans.common.lang;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/** @author McDowell */
// gotten from
// http://illegalargumentexception.blogspot.com/2008/04/java-synchronizing-on-transient-id.html
@SuppressWarnings("unchecked")
public class IdMutexProvider {

    private final Map mutexMap = new WeakHashMap();

    /** Get a mutex object for the given (non-null) id. */
    public Mutex getMutex(String id) {
        if (id == null) {
            throw new NullPointerException();
        }

        Mutex key = new MutexImpl(id);
        synchronized (mutexMap) {
            WeakReference ref = (WeakReference) mutexMap.get(key);
            if (ref == null) {
                mutexMap.put(key, new WeakReference(key));
                return key;
            }
            Mutex mutex = (Mutex) ref.get();
            if (mutex == null) {
                mutexMap.put(key, new WeakReference(key));
                return key;
            }
            return mutex;
        }
    }

    /** Get the number of mutex objects being held */
    public int getMutexCount() {
        synchronized (mutexMap) {
            return mutexMap.size();
        }
    }

    public static interface Mutex {}

    private static class MutexImpl implements Mutex {
        private final String id;

        protected MutexImpl(String id) {
            this.id = id;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (this.getClass() == o.getClass()) {
                return this.id.equals(o.toString());
            }
            return false;
        }

        public int hashCode() {
            return id.hashCode();
        }

        public String toString() {
            return id;
        }
    }

}
