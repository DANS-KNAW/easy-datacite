package nl.knaw.dans.common.lang.search.bean.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.knaw.dans.common.lang.search.Index;

/**
 * A search bean is a POJO that has the @SearchBean annotation (this class) and one or more properties
 * annotated with the @SearchField annotation. A search bean is a mapping between a Java object and a
 * indexed search document. Each search bean should be uniquely identified by its type identifier. The
 * type identifier is a string that is stored in the search engine alongside the other properties
 * (@SearchField). By the type identifier the search engine can know which search bean class it should
 * needs to convert the document into.
 * 
 * @author lobo
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SearchBean
{
    /**
     * The default index that the search engine should use if no index parameter has been passed to the
     * search engine.
     */
    Class<? extends Index> defaultIndex();

    /**
     * The type identifier string by which the search engine can know from which kind of search bean the
     * document came. If the search bean inherits from another search bean the type identifier of the
     * super search bean is also stored in the document. This allows for polymorphism at the index level.
     */
    String typeIdentifier();
}
