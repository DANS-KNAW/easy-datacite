package nl.knaw.dans.easy.business.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

public class AspectUtil
{
    
    private AspectUtil()
    {
        // static class
    }
    
    public static String printJoinPoint(JoinPoint jp)
    {
        return new StringBuilder(printSignatureAndSource(jp))//
            .append("\n")//
            .append(printParameters(jp))//
            .toString();
    }
    
    public static String printSignatureAndSource(JoinPoint jp)
    {
        return new StringBuilder(jp.getSignature().toString())//
            .append(" (")//
            .append(jp.getSourceLocation().getFileName())//
            .append(":")//
            .append(jp.getSourceLocation().getLine())//
            .append(")")//
            .toString();
    }
    
    public static String printSource(JoinPoint jp)
    {
        return new StringBuilder()//
            .append(" (")//
            .append(jp.getSourceLocation().getFileName())//
            .append(":")//
            .append(jp.getSourceLocation().getLine())//
            .append(")")//
            .toString();
    }
    
    public static String printSourceAndSignature(JoinPoint jp)
    {
        return new StringBuilder()//
            .append(" (")//
            .append(jp.getSourceLocation().getFileName())//
            .append(":")//
            .append(jp.getSourceLocation().getLine())//
            .append(")\n\t===> ")//
            .append(jp.getSignature().toString()) //
            .toString();
    }
    
    public static String printParameters(JoinPoint jp)
    {
        StringBuilder sb = new StringBuilder("Arguments: ");
        Object[] args = jp.getArgs();
        String[] names = ((CodeSignature) jp.getSignature()).getParameterNames();
        Class<?>[] types = ((CodeSignature) jp.getSignature()).getParameterTypes();
        for (int i = 0; i < args.length; i++)
        {
            sb.append("\n\t" + i + ". " + names[i] + " : " + types[i].getName() + " = " + args[i]);
        }
        return sb.toString();
    }

}
