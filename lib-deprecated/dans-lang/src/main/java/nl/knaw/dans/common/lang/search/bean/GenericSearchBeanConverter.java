package nl.knaw.dans.common.lang.search.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import nl.knaw.dans.common.lang.ClassUtil;
import nl.knaw.dans.common.lang.search.IndexDocument;
import nl.knaw.dans.common.lang.search.bean.annotation.CopyField;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchBean;
import nl.knaw.dans.common.lang.search.bean.annotation.SearchField;
import nl.knaw.dans.common.lang.search.exceptions.MissingRequiredFieldException;
import nl.knaw.dans.common.lang.search.exceptions.ObjectIsNotASearchBeanException;
import nl.knaw.dans.common.lang.search.exceptions.PrimaryKeyMissingException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanException;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleIndexDocument;

import org.apache.commons.lang.StringUtils;

public class GenericSearchBeanConverter implements SearchBeanConverter<Object> {

    @SuppressWarnings("unchecked")
    public IndexDocument toIndexDocument(Object searchBean) throws SearchBeanConverterException, SearchBeanException {
        Class sbClass = searchBean.getClass();
        if (!sbClass.isAnnotationPresent(SearchBean.class))
            throw new ObjectIsNotASearchBeanException(sbClass.toString());

        SimpleIndexDocument indexDocument = new SimpleIndexDocument(SearchBeanUtil.getDefaultIndex(sbClass));

        for (java.lang.reflect.Field classField : ClassUtil.getAllFields(sbClass).values()) {
            if (classField.isAnnotationPresent(SearchField.class)) {
                SearchField sbField = classField.getAnnotation(SearchField.class);

                String fieldName = sbField.name();
                boolean isRequired = sbField.required();
                Class<? extends SearchFieldConverter<?>> converter = sbField.converter();
                String propName = classField.getName();
                String getMethodName = "get" + StringUtils.capitalize(propName);
                addFieldToDocument(searchBean, indexDocument, getMethodName, fieldName, isRequired, converter);

                if (classField.isAnnotationPresent(CopyField.class)) {
                    for (Annotation annot : classField.getAnnotations()) {
                        if (annot instanceof CopyField) {
                            CopyField sbCopyField = (CopyField) annot;
                            fieldName = sbCopyField.name();
                            isRequired = sbCopyField.required();
                            getMethodName = "get" + StringUtils.capitalize(propName) + StringUtils.capitalize(sbCopyField.getterPostfix());
                            converter = sbCopyField.converter();
                            addFieldToDocument(searchBean, indexDocument, getMethodName, fieldName, isRequired, converter);

                        }
                    }
                }
            }
        }

        if (indexDocument.getIndex() != null) {
            if (indexDocument.getFields().getByFieldName(indexDocument.getIndex().getPrimaryKey()) == null)
                throw new PrimaryKeyMissingException("Primary key not set to search bean object.");
        }

        return indexDocument;
    }

    @SuppressWarnings("unchecked")
    private void addFieldToDocument(Object searchBean, SimpleIndexDocument indexDocument, String getMethodName, String fieldName, boolean isRequired,
            Class<? extends SearchFieldConverter<?>> converterClass) throws SearchBeanException, MissingRequiredFieldException, SearchBeanConverterException
    {
        Method getMethod = null;
        Object fieldValue = null;
        try {
            getMethod = searchBean.getClass().getMethod(getMethodName, new Class[] {});
            fieldValue = getMethod.invoke(searchBean);
        }
        catch (Exception e) {
            throw new SearchBeanException(e);
        }

        if (fieldValue != null) {
            if (!converterClass.equals(DefaultSearchFieldConverter.class)) {
                // run the SearchFieldConverter
                try {
                    SearchFieldConverter<Object> converter = (SearchFieldConverter<Object>) converterClass.newInstance();
                    fieldValue = converter.toFieldValue(fieldValue);
                }
                catch (InstantiationException e) {
                    throw new SearchBeanConverterException(e);
                }
                catch (IllegalAccessException e) {
                    throw new SearchBeanConverterException(e);
                }
            }

            SimpleField newField = new SimpleField(fieldName, fieldValue);
            indexDocument.addField(newField);
        } else {
            if (isRequired)
                throw new MissingRequiredFieldException(fieldName);
        }
    }

}
