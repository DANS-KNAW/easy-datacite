package nl.knaw.dans.easy.domain.workflow;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.RepoAccess;

import org.joda.time.DateTime;

public class Remark extends AbstractJiBXObject<Remark>
{
    
    private static final long serialVersionUID = 1L;
    
    private String text;
    private String remarkerId;
    private EasyUser remarker;
    private DateTime remarkDate;
    
    public Remark()
    {
        
    }
    
    public Remark(String text, String remarkerId)
    {
        this.text = text;
        this.remarkerId = remarkerId;
        this.remarkDate = new DateTime();
    }

    public String getText()
    {
        return text;
    }
    
    public void setText(String text)
    {
        this.text = text;
        remarkDate = new DateTime();
    }

    public String getRemarkerId()
    {
        return remarkerId;
    }
    
    public void setRemarkerId(String remarkerId)
    {
        this.remarkerId = remarkerId;
        if (remarker != null && !remarker.getId().equals(remarkerId))
        {
            remarker = null;
        }
    }

    public DateTime getRemarkDate()
    {
        return remarkDate;
    }
    
    public EasyUser getRemarker() //throws UnknownIdentifierException, DataLayerInAccessableException
    {
        if (remarker == null && remarkerId != null)
        {
            remarker = RepoAccess.getDelegator().getUser(remarkerId);
        }
        return remarker;
    }
    
    public void setRemarker(EasyUser remarker)
    {
        this.remarker = remarker;
        setRemarkerId(remarker == null ? null : remarker.getId());
    }
    
    public Remark clone()
    {
        Remark clone = new Remark();
        clone.text = text;
        clone.remarkerId = remarkerId;
        clone.remarkDate = remarkDate;
        return clone;
    }
}
