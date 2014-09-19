package nl.knaw.dans.i.security.annotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for evaluating classes with methods annotated with {@link SecuredOperation}.
 * 
 * @author henkb
 */
public class SecuredOperationUtil {
    /**
     * List the securityIds of operations declared in the given interface, that have the annotation {@link SecuredOperation}.
     * 
     * @param _interface
     *        the interface to inspect
     * @return List of securityIds.
     * @throws IllegalArgumentException
     *         if the given <code>_interface</code> is not an interface.
     */
    public static List<String> getDeclaredSecurityIdsOnInterface(Class<?> _interface) {
        if (!_interface.isInterface()) {
            throw new IllegalArgumentException(_interface.getName() + " is not an interface.");
        }
        List<String> securityIds = new ArrayList<String>();
        String iName = _interface.getCanonicalName() + ".";
        for (Method method : _interface.getDeclaredMethods()) {
            if (method.getAnnotation(SecuredOperation.class) != null) {
                securityIds.add(iName + method.getName());
            }
        }
        return securityIds;
    }

    /**
     * List the securityIds of operations in the given <code>implementation</code> class, that are implementations of operations declared in (one of) its
     * interface(s) where both declarations have the annotation {@link SecuredOperation}.
     * <p/>
     * If the given <code>implementation</code> class is itself an interface and the returned list contains items, than this is an indication that an operation
     * is declared more than once in the hierarchy and that both declarations have the annotation {@link SecuredOperation}. This should not happen, as it
     * undermines the uniqueness of the securityId.
     * 
     * @param implementation
     *        an implementing class
     * @return List of securityIds.
     */
    public static List<String> getInterfaceSecurityIds(Class<?> implementation) {
        List<String> securityIds = new ArrayList<String>();
        walkHierarchy(securityIds, implementation, implementation);
        return securityIds;
    }

    private static void walkHierarchy(List<String> securityIds, Class<?> _class, Class<?> implementation) {
        for (Class<?> _interface : _class.getInterfaces()) {
            securityIds.addAll(getSecurityIds(_interface, implementation));
            walkHierarchy(securityIds, _interface, implementation);
        }
    }

    /**
     * List the securityIds of operations in the given <code>implementation</code> class, that are implementations of operations declared in the given
     * <code>_interface</code> class, where both declarations have the annotation {@link SecuredOperation}.
     * 
     * @param _interface
     *        the interface under inspection
     * @param implementation
     *        the implementation class under inspection
     * @return List of securityIds.
     * @throws IllegalArgumentException
     *         if the given <code>_interface</code> is not an interface.
     */
    public static List<String> getSecurityIds(Class<?> _interface, Class<?> implementation) {
        if (!_interface.isInterface()) {
            throw new IllegalArgumentException(_interface.getName() + " is not an interface.");
        }
        List<String> securityIds = new ArrayList<String>();
        String iName = _interface.getCanonicalName() + ".";
        List<String> interfaceMethodNames = new ArrayList<String>();
        for (Method interfaceMethod : _interface.getDeclaredMethods()) {
            if (interfaceMethod.getAnnotation(SecuredOperation.class) != null) {
                interfaceMethodNames.add(interfaceMethod.getName());
            }
        }

        for (Method method : implementation.getDeclaredMethods()) {
            if (method.getAnnotation(SecuredOperation.class) != null && interfaceMethodNames.contains(method.getName())) {
                securityIds.add(iName + method.getName());
            }
        }
        return securityIds;
    }

    /**
     * Enforce a one-to-one relation of secured operations declared in an implementing class and in its interfaces. Typical usage in a unit test:
     * 
     * <pre>
     *    {@literal @}Test
     *    public void checkSecuredOperationIds()
     *    {
     *        SecuredOperationUtil.checkSecurityIds(MyImplementing.class);
     *    }
     * </pre>
     * 
     * This will throw a RuntimeException if {@link SecuredOperation} annotations are not properly used.
     * <p/>
     * Check validity and consistency of an implementing class against it's interface hierarchy.
     * <p/>
     * Checks whether
     * <ul>
     * <li>id of {@link SecuredOperation} annotation of implementing class method is not blank;</li>
     * <li>id of {@link SecuredOperation} annotation of implementing class method is consistent with interface method;</li>
     * <li>{@link SecuredOperation} annotation on implementing class method is absent in implemented interface(s);</li>
     * <li>all {@link SecuredOperation} annotations on interface methods are present on implementing class methods.</li>
     * </ul>
     * 
     * @param _class
     *        implementing class under inspection
     * @throws RuntimeException
     *         if {@link SecuredOperation} annotations are not valid, or not consistent with interface hierarchy of the given implementing class.
     */
    public static void checkSecurityIds(Class<?> _class) {
        if (_class.isInterface()) {
            throw new IllegalArgumentException(_class.getName() + " is an interface");
        }
        List<String> securityIds = getInterfaceSecurityIds(_class);
        List<String> declaredIds = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (Method method : _class.getMethods()) {
            SecuredOperation securityAnn = method.getAnnotation(SecuredOperation.class);
            if (securityAnn != null) {
                checkSecurityId(sb, securityAnn, _class, method, securityIds);
                declaredIds.add(securityAnn.id());
            }
        }

        checkMissingAnnotations(sb, _class);

        String foundErrors = sb.toString();
        if (!"".equals(foundErrors)) {
            System.err.println(foundErrors);
            throw new RuntimeException(_class.getName() + " has illegal or missing @SecuredOperation annotations." + foundErrors);
        }
    }

    protected static void checkMissingAnnotations(StringBuilder sb, Class<?> implementation) {
        List<String> secIds = new ArrayList<String>();
        for (Class<?> i : implementation.getInterfaces()) {
            listAllInterfaceAnnotations(i, secIds);
        }
        for (Method method : implementation.getDeclaredMethods()) {
            SecuredOperation securedOperation = method.getAnnotation(SecuredOperation.class);
            if (securedOperation != null) {
                secIds.remove(securedOperation.id());
            }
        }
        for (String securityId : secIds) {
            sb.append("\n\tMissing @SecuredOperation(id = \"" + securityId + "\")" + "' in class " + implementation.getName());
            sb.append(constructLink(implementation));
        }
    }

    private static void listAllInterfaceAnnotations(Class<?> _interface, List<String> secIds) {
        secIds.addAll(getDeclaredSecurityIdsOnInterface(_interface));
        for (Class<?> i : _interface.getInterfaces()) {
            listAllInterfaceAnnotations(i, secIds);
        }
    }

    private static void checkSecurityId(StringBuilder sb, SecuredOperation securityAnn, Class<?> _class, Method method, List<String> securityIds) {
        // checks blank ids, inappropriate ids and superfluous annotations.
        String id = securityAnn.id();
        if (StringUtils.isBlank(id)) {
            sb.append("\n\tId of @SecuredOperation on operation '" + method.getName() + "' in class " + _class.getName() + " cannot be blank.");
            sb.append(constructLink(_class));
        } else if (!id.endsWith(method.getName())) {
            sb.append("\n\tId '" + securityAnn.id() + "' of @SecuredOperation on operation '" + method.getName() + "' in class " + _class.getName()
                    + " is not in applicable for the annotated method. End of id must equal methodname.");
            sb.append(constructLink(_class));
        } else if (!securityIds.contains(id)) {
            sb.append("\n\t@SecuredOperation with id '" + id + "' on '" + method.getName() + "' in class " + _class.getName()
                    + " is not declared in the interface hierarchy.");
            sb.append(constructLink(_class));
        }

    }

    private static String constructLink(Class<?> _class) {
        StringBuilder sb = new StringBuilder().append(" (").append(_class.getSimpleName()).append(".java:1)");
        return sb.toString();
    }

    public static String getSecurityId(Class<?> _class, String methodName) {
        String securityId = null;
        for (Method method : _class.getMethods()) {
            SecuredOperation securedOperation = method.getAnnotation(SecuredOperation.class);
            if (securedOperation != null && method.getName().equals(methodName)) {
                securityId = securedOperation.id();
                break;
            }

        }
        return securityId;
    }

}
