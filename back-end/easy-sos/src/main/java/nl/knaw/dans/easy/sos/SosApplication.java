package nl.knaw.dans.easy.sos;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class SosApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(ObjectsResource.class);
        return s;
    }
}
