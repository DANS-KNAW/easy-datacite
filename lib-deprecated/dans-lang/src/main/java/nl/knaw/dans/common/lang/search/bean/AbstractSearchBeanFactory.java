package nl.knaw.dans.common.lang.search.bean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.lang.ClassUtil;
import nl.knaw.dans.common.lang.search.Document;
import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.Index;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchBean;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchField;
import nl.knaw.dans.common.lang.search.exceptions.DocumentReturnedInvalidTypeException;
import nl.knaw.dans.common.lang.search.exceptions.MissingRequiredFieldException;
import nl.knaw.dans.common.lang.search.exceptions.PrimaryKeyMissingException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanFactoryException;
import nl.knaw.dans.common.lang.search.exceptions.UnknownSearchBeanTypeException;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public abstract class AbstractSearchBeanFactory implements SearchBeanFactory {
    /**
     * A map that contains for each type a corresponding search bean class
     */
    private Map<String, Class<?>> typeMap;

    public abstract Class<?>[] getSearchBeanClasses();

    protected synchronized void createTypeMap() {
        typeMap = new HashMap<String, Class<?>>();
        for (Class<?> sbClass : getSearchBeanClasses()) {
            SearchBean sbAnnot = sbClass.getAnnotation(SearchBean.class);
            typeMap.put(sbAnnot.typeIdentifier(), sbClass);
        }
    }

    protected synchronized Map<String, Class<?>> getTypeMap() {
        if (typeMap == null)
            createTypeMap();
        return typeMap;
    }

    protected Map<String, java.lang.reflect.Field> getFieldsMap(Class<?> sbClass) {
        Map<String, java.lang.reflect.Field> results = new HashMap<String, java.lang.reflect.Field>();
        for (java.lang.reflect.Field classField : ClassUtil.getAllFields(sbClass).values()) {
            if (classField.isAnnotationPresent(SearchField.class)) {
                SearchField sbField = classField.getAnnotation(SearchField.class);
                results.put(sbField.name(), classField);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public Object createSearchBean(String type, Document document) throws SearchBeanFactoryException, SearchBeanException {
        Class<?> sbClass = null;
        Object searchBean = null;
        sbClass = getTypeMap().get(type);
        if (sbClass == null)
            throw new UnknownSearchBeanTypeException(type);

        Map<String, java.lang.reflect.Field> fieldsMap = null;
        try {
            fieldsMap = getFieldsMap(sbClass);
            searchBean = sbClass.newInstance();
        }
        catch (Exception e) {
            throw new SearchBeanException(e);
        }

        // check if the primary key is in the document
        Index index = SearchBeanUtil.getDefaultIndex(sbClass);
        if (index != null) {
            if (document.getFieldByName(index.getPrimaryKey()) == null)
                throw new PrimaryKeyMissingException("Primary key not found in document. " + document.toString());
        }

        // convert fields
        for (Map.Entry<String, java.lang.reflect.Field> searchField : fieldsMap.entrySet()) {
            Field docField = document.getFieldByName(searchField.getKey());
            java.lang.reflect.Field classField = searchField.getValue();
            if (docField != null) {
                String propName = classField.getName();
                String setMethodName = "set" + StringUtils.capitalize(propName);
                try {
                    Method setMethod = sbClass.getMethod(setMethodName, new Class[] {classField.getType()});
                    Class fieldType = classField.getType();
                    Object value = docField.getValue();

                    SearchField sbField = classField.getAnnotation(SearchField.class);
                    Class<? extends SearchFieldConverter<?>> converterClass = sbField.converter();

                    // convert to basic types
                    boolean basicConversionFailed = false;
                    if (!ClassUtil.instanceOf(value, fieldType)) {
                        // Don't change the type of value if we have a non-default converter
                        // instead, let the converter do it's work
                        // if (ClassUtil.classImplements(fieldType, Collection.class) )
                        if (ClassUtil.classImplements(fieldType, Collection.class) && converterClass.equals(DefaultSearchFieldConverter.class)) {
                            ArrayList listValue = new ArrayList(1);
                            listValue.add(value);
                            value = listValue;
                        } else if (fieldType.equals(String.class)) {
                            value = value.toString();
                        } else if (fieldType.equals(DateTime.class) && value.getClass().equals(Date.class)) {
                            value = new DateTime((Date) value);
                        } else if (fieldType.equals(DateTime.class) && value.getClass().equals(String.class)) {
                            value = new DateTime(value.toString());
                        } else if (ClassUtil.instanceOf(fieldType, Enum.class)) {
                            value = Enum.valueOf(fieldType, value.toString());
                        } else if (fieldType.equals(int.class) && value.getClass().equals(Integer.class)) {
                            value = ((Integer) value).intValue();
                        } else
                            // if basic conversion fails, the converter might still
                            // save the day
                            basicConversionFailed = true;
                    }

                    if (!converterClass.equals(DefaultSearchFieldConverter.class)) {
                        SearchFieldConverter<?> converter = converterClass.newInstance();
                        value = converter.fromFieldValue(value);
                    } else {
                        if (basicConversionFailed)
                            // converter did not save the day
                            throw new DocumentReturnedInvalidTypeException("expected " + fieldType.toString() + " but got " + value.getClass().toString());
                    }

                    setMethod.invoke(searchBean, value);
                }
                catch (Exception e) {
                    throw new SearchBeanException(e);
                }
            } else {
                SearchField sbField = classField.getAnnotation(SearchField.class);
                if (sbField.required())
                    throw new MissingRequiredFieldException(sbField.name());
            }
        }

        return searchBean;
    }

    public SearchBeanConverter<?> getSearchBeanConverter(Class<?> sbClass) throws SearchBeanFactoryException, SearchBeanException {
        return (SearchBeanConverter<?>) new GenericSearchBeanConverter();
    }
}
