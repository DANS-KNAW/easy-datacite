package nl.knaw.dans.easy.web.wicket;

import java.util.List;

import nl.knaw.dans.easy.domain.workflow.Remark;

import org.apache.wicket.model.IModel;


public abstract class RemarksModel implements IModel
{

    private static final long serialVersionUID = 1829536760696477410L;
    
    private List<Remark> remarks;
    
    public RemarksModel(List<Remark> remarks)
    {
        this.remarks = remarks;
    }
    
    public Object getObject()
    {
        return remarks;
    }
    
    public void setObject(Object object)
    {
        remarks = (List<Remark>) object;
    }
    
    public void detach()
    {
        
    }
    
    public List<Remark> getRemarks()
    {
        return remarks;
    }

    public abstract void onSubmit();

}
