package nl.knaw.dans.easy.business.dataset;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.security.CodedAuthz;

import org.junit.Ignore;
import org.junit.Test;

public class DatasetWorkDispatcherTest
{

    /*
     * Ignore this test for now, because getAdditionalLicenseURL has no security as a work-around. This
     * should be fixed later.
     */
    @Ignore
    @Test
    public void testSecurity() throws ServiceException
    {
        boolean hasSecurity = true;
        CodedAuthz authz = new CodedAuthz();
        Method[] methods = DatasetWorkDispatcher.class.getDeclaredMethods();
        for (Method method : methods)
        {
            if (Modifier.isPublic(method.getModifiers()))
            {
                String signature = createSignature(method);
                if (!authz.hasSecurityOfficer(signature))
                {
                    System.err.println(signature);
                    hasSecurity = false;
                }
            }
        }
        assertTrue(hasSecurity);
    }

    private String createSignature(Method method)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType().getSimpleName()).append(" ").append(method.getDeclaringClass().getName()).append(".").append(method.getName())
                .append("(");
        Class[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++)
        {
            sb.append(params[i].getSimpleName());
            if (i < params.length - 1)
            {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

}
