package nl.knaw.dans.pf.language.emd.binding;

import nl.knaw.dans.pf.language.emd.EmdBean;
import nl.knaw.dans.pf.language.xml.binding.JiBXUnmarshaller;

public class EmdUnmarshaller<T extends EmdBean> extends JiBXUnmarshaller<T>
{
    
    public EmdUnmarshaller(Class<? extends T> beanClass)
    {
        super(EmdMarshaller.BINDING_NAME, beanClass);
    }
    

}
