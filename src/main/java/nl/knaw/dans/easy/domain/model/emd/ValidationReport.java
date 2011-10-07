package nl.knaw.dans.easy.domain.model.emd;

import java.io.Serializable;


public class ValidationReport implements Serializable
{
    private static final long serialVersionUID = -6892602243185901783L;
    
    private String message;
    private String messageCode;
    private String xpathExpression;
    private String panelId;
    private Validator source;
    private Caller caller;
    
    public ValidationReport(String message, Validator source)
    {
        this.message = message;
        this.source = source;
        this.caller = getCaller();
    }
    
    public ValidationReport(String message, String xpathExpression, Validator source)
    {
        this.message = message;
        this.xpathExpression = xpathExpression;
        this.source = source;
        this.caller = getCaller();
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public String getMessageCode()
    {
        return messageCode;
    }
    
    public void setMessageCode(String messageCode)
    {
        this.messageCode = messageCode;
    }
    
    public String getXpathExpression()
    {
        return xpathExpression;
    }
    
    public void setXpathExpression(String xpathExpression)
    {
        this.xpathExpression = xpathExpression;
    }
    
    public String getPanelId()
    {
        return panelId;
    }
    
    public void setPanelId(String panelId)
    {
        this.panelId = panelId;
    }
    
    public Validator getSource()
    {
        return source;
    }
    
    public void setSource(Validator source)
    {
        this.source = source;
    }
    
    public String getSourceName()
    {
        return source == null ? "null" : source.getClass().getName();
    }
    
    public String getSourceLink()
    {
        return caller.getSourceLink();
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n")
            .append("message=").append(message).append("\n")
            .append("messageCode=").append(messageCode).append("\n")
            .append("xpathExpression=").append(xpathExpression).append("\n")
            .append("panelId=").append(panelId).append("\n")
            .append("sourceLink=").append(getSourceLink()).append("\n");
        return sb.toString();
    }
    
    private Caller getCaller()
    {
        String className = "";
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
        {
            String cn = ste.getClassName();
            if (!cn.equals(Thread.class.getName()) 
                    && !cn.equals(ValidationReport.class.getName()) 
                    && !cn.equals(this.getClass().getName()))
            {
                className = ste.getClassName();
                sb.append(className)
                .append(".")
                .append(ste.getMethodName())
                .append(" (")
                .append(ste.getFileName())
                .append(":")
                .append(ste.getLineNumber())
                .append(")");
                break;
            }
        }
        return new Caller(className, sb.toString());
    }
    
    public static class Caller
    {
        private final String className;
        private final String sourceLink;
        
        public Caller(String className, String sourceLink)
        {
            this.className = className;
            this.sourceLink = sourceLink;
        }

        public String getClassName()
        {
            return className;
        }

        public String getSourceLink()
        {
            return sourceLink;
        }
    }
}
