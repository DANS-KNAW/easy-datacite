package nl.knaw.dans.i.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes the intention that the annotated operation is secured.
 * <p/>
 * The implementation of promises given by Annotations cannot be enforced. Check the validity and
 * consistency of {@link SecuredOperation} annotations on an implementing class against
 * {@link SecuredOperation} annotations on its interfaces with
 * {@link SecuredOperationUtil#checkSecurityIds(Class)}.
 * 
 * @see SecuredOperationUtil
 * @author henkb
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SecuredOperation
{
    String id() default "";
}
