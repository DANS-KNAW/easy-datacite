package nl.knaw.dans.easy.rest;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import nl.knaw.dans.easy.rest.resources.AbstractResource;

import org.junit.Test;

public class RestApplicationTest {

    @Test
    public void testApplication() {
        RestApplication app = new RestApplication();
        Set<Class<?>> resources = app.getClasses();
        for (Class<?> resource : resources) {
            assertTrue(AbstractResource.class.isAssignableFrom(resource));
        }
    }
}
