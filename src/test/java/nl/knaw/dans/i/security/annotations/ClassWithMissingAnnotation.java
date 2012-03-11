package nl.knaw.dans.i.security.annotations;

public class ClassWithMissingAnnotation implements IA, IB
{

    
    @Override
    public void iBSecuredOperation()
    {

    }

    @Override
    public void iBNonSecuredOperation()
    {

    }

    @SecuredOperation(id = "nl.knaw.dans.i.security.annotations.IA.iASecuredOperation")
    @Override
    public void iASecuredOperation()
    {

    }
    
    @SecuredOperation(id = "nl.knaw.dans.i.security.annotations.IA.iASecuredOperation")
    @Override
    public void iASecuredOperation(String s)
    {
        
    }

    @Override
    public void iANonSecuredOperation()
    {

    }

}
