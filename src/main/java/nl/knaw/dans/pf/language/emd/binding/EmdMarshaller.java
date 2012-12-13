package nl.knaw.dans.pf.language.emd.binding;

import nl.knaw.dans.l.xml.binding.JiBXMarshaller;
import nl.knaw.dans.pf.language.emd.EmdBean;

public class EmdMarshaller extends JiBXMarshaller
{
    public static final String BINDING_NAME = "easymetadata_binding";
    
    public EmdMarshaller(EmdBean bean)
    {
        super(BINDING_NAME, bean);
    }
    

}
