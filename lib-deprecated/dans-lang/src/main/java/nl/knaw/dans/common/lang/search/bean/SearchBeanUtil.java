package nl.knaw.dans.common.lang.search.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.ClassUtil;
import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.Index;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchBean;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchField;
import nl.knaw.dans.common.lang.search.exceptions.CannotInstantiateIndexException;
import nl.knaw.dans.common.lang.search.exceptions.ObjectIsNotASearchBeanException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanException;
import nl.knaw.dans.common.lang.search.simple.SimpleField;

import org.apache.commons.lang.StringUtils;

/**
 * Class with helper functions for working with search beans
 * 
 * @author lobo
 */
public class SearchBeanUtil
{
    private static void checkSearchBean(Class<?> sbClass) throws ObjectIsNotASearchBeanException
    {
        if (!sbClass.isAnnotationPresent(SearchBean.class))
            throw new ObjectIsNotASearchBeanException(sbClass.toString());
    }

    public static String getTypeIdentifier(Class<?> sbClass) throws SearchBeanException
    {
        checkSearchBean(sbClass);
        return sbClass.getAnnotation(SearchBean.class).typeIdentifier();
    }

    public static List<String> getTypeHierarchy(Class<?> sbClass) throws SearchBeanException
    {
        checkSearchBean(sbClass);

        List<String> result = new ArrayList<String>();
        result.add(sbClass.getAnnotation(SearchBean.class).typeIdentifier());
        if (sbClass.getSuperclass().isAnnotationPresent(SearchBean.class))
        {
            result.addAll(getTypeHierarchy(sbClass.getSuperclass()));
        }
        return result;
    }

    public static Field getPrimaryKey(Object searchBean) throws SearchBeanException
    {
        Class<?> sbClass = searchBean.getClass();
        checkSearchBean(sbClass);

        String primaryKeyName = getDefaultIndex(sbClass).getPrimaryKey();

        for (java.lang.reflect.Field classField : ClassUtil.getAllFields(sbClass).values())
        {
            if (classField.isAnnotationPresent(SearchField.class))
            {
                SearchField sbField = classField.getAnnotation(SearchField.class);
                if (sbField.name().equals(primaryKeyName))
                {
                    Object fieldValue;
                    try
                    {
                        fieldValue = getFieldValue(searchBean, classField.getName());
                    }
                    catch (Exception e)
                    {
                        throw new SearchBeanException("Error while trying to get the primary key of SearchBean " + searchBean.getClass().toString(), e);
                    }

                    return new SimpleField(sbField.name(), fieldValue);
                }
            }
        }

        return null;
    }

    public static Index getDefaultIndex(Class<?> sbClass) throws SearchBeanException
    {
        checkSearchBean(sbClass);

        try
        {
            return sbClass.getAnnotation(SearchBean.class).defaultIndex().newInstance();
        }
        catch (InstantiationException e)
        {
            throw new CannotInstantiateIndexException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new CannotInstantiateIndexException(e);
        }
    }

    public static String getFieldName(Object searchBean, String propertyName) throws NoSuchFieldException
    {
        Map<String, java.lang.reflect.Field> fields = ClassUtil.getAllFields(searchBean.getClass());
        java.lang.reflect.Field classField = fields.get(propertyName);
        if (classField == null)
            throw new NoSuchFieldException(propertyName);
        if (classField.isAnnotationPresent(SearchField.class))
            return classField.getAnnotation(SearchField.class).name();
        else
            return null;
    }

    public static Object getFieldValue(Object searchBean, String propertyName) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException
    {
        String getMethodName = "get" + StringUtils.capitalize(propertyName);
        Method getMethod = searchBean.getClass().getMethod(getMethodName, new Class[] {});
        return getMethod.invoke(searchBean);
    }

}
