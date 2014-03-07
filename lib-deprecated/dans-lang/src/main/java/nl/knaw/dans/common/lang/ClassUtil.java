package nl.knaw.dans.common.lang;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ClassUtil
{

    /**
     * Returns a list of all fields on a class including the fields of the super classes.
     * 
     * @param clazz
     *        the class to scan for fields
     * @return a list of all fields declared on clazz
     */
    public static Map<String, Field> getAllFields(Class<?> clazz)
    {
        return doGetAllFields(new HashMap<String, Field>(), clazz);
    }

    protected static Map<String, Field> doGetAllFields(Map<String, Field> fields, Class<?> clazz)
    {
        for (Field field : clazz.getDeclaredFields())
        {
            fields.put(field.getName(), field);
        }

        if (clazz.getSuperclass() != null)
        {
            fields = doGetAllFields(fields, clazz.getSuperclass());
        }

        return fields;
    }

    /**
     * A runtime version of the instanceof keyword. Instanceof cannot check the right hand 
     * for run time classes. This function can. 
     * @param object the left hand object (as in the instanceof keyword)
     * @param clazz the right hand class (as in the instanceof keyword) 
     * @return true if object is an instance of the clazz 
     */
    public static boolean instanceOf(Object object, Class<?> clazz)
    {
        return instanceOf(object.getClass(), clazz);
    }

    public static boolean instanceOf(Class<?> lclass, Class<?> clazz)
    {
        if (lclass.equals(clazz))
            return true;
        if (clazz.isInterface())
            return classImplements(lclass, clazz);
        else
            return classExtends(lclass, clazz);
    }

    /**
     * Checks if a class or one of its super classes implements a certain interface. 
     * @param clazz the class to check 
     * @param interfaces the interfaces to search for
     * @return true if clazz implements one of the interfaces
     */
    public static boolean classImplements(Class<?> clazz, Class<?>... interfaces)
    {
        if (clazz.isInterface())
        {
            for (Class<?> checkImpl : interfaces)
            {
                if (checkImpl.equals(clazz))
                    return true;
            }
        }

        Class<?>[] implementations = clazz.getInterfaces();
        for (Class<?> intf : implementations)
        {
            for (Class<?> checkImpl : interfaces)
            {
                if (intf.equals(checkImpl))
                    return true;
            }
        }

        return clazz.getSuperclass() == null ? false : classImplements(clazz.getSuperclass(), interfaces);
    }

    /**
     * Checks if a class extends another class
     * 
     * @param clazz
     *        the class to check
     * @param superClasses
     *        the superClasses to check for
     * @return true if clazz extends one of the superClasses
     */
    public static boolean classExtends(Class<?> clazz, Class<?>... superClasses)
    {
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz == null)
            return false;
        for (Class<?> superClass : superClasses)
        {
            if (superClass.equals(superClazz))
                return true;
        }
        return classExtends(superClazz, superClasses);
    }
}
