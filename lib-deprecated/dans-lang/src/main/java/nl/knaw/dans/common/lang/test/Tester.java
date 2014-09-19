package nl.knaw.dans.common.lang.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;

/**
 * A light weight test helper class. If picking up properties, a file <code>test.properties</code> should be on the classPath, typically in the folder
 * <code>src/test/resources</code>.
 * 
 * @author ecco Sep 24, 2009
 */
public final class Tester {
    /**
     * Key for the property {@value} .
     */
    public static final String KEY_TEST = "tester.test";

    /**
     * Key for the property {@value} .
     */
    public static final String KEY_VERBOSE = "tester.verbose";

    /**
     * The bundle name for properties.
     */
    public static final String BUNDLE_NAME = "test";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Tester() {
        // static class
    }

    /**
     * Get the property associated with the given key.
     * 
     * @param key
     *        key in the file <code>test.properties</code>, somewhere on the classPath
     * @return value of given key
     */
    public static String getString(final String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

    /**
     * Get the property {@link #KEY_VERBOSE} translated to a boolean.
     * 
     * @return <code>true</code> if the value of {@link #KEY_VERBOSE} is 'true', <code>false</code> otherwise
     */
    public static boolean isVerbose() {
        return "true".equals(getString(KEY_VERBOSE));
    }

    /**
     * Get the URL for the given location, or <code>null</code> if no resource exists on given location.
     * 
     * @param location
     *        a relative path on the class path, separated with "/"
     * @return URL of the resource or <code>null</code>
     */
    public static URL getResource(final String location) {
        return ResourceLocator.getURL(location);
    }

    /**
     * Get the file on the given location.
     * 
     * @param location
     *        a relative path on the class path, separated with "/"
     * @return the file at the given location
     * @throws NullPointerException
     *         if the file was not found
     */
    public static File getFile(final String location) throws ResourceNotFoundException {
        return ResourceLocator.getFile(location);
    }

    /**
     * Get InputStream from given location. The caller is responsible for proper closing of the InputStream.
     * 
     * @param location
     *        a relative path on the class path, separated with "/"
     * @return InputStream from given location
     * @throws IOException
     *         if such mishap occurs
     * @throws ResourceNotFoundException
     *         if no file exists on given location
     */
    public static InputStream getInputStream(final String location) throws IOException, ResourceNotFoundException {
        return ResourceLocator.getInputStream(location);
    }

    public static void printClassAndFieldHierarchy(Class<?> clazz) {
        while (clazz != null) {
            System.out.println(clazz.getName());
            for (Field field : clazz.getDeclaredFields()) {
                System.out.println("\t" + field.getName() + ":" + field.getType().getName());
            }
            clazz = clazz.getSuperclass();
        }
    }

}
