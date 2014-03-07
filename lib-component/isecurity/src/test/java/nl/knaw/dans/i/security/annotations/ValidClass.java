package nl.knaw.dans.i.security.annotations;

public class ValidClass implements IB
{

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

    @SecuredOperation(id = "nl.knaw.dans.i.security.annotations.IB.iBSecuredOperation")
    @Override
    public void iBSecuredOperation()
    {

    }

    @Override
    public void iBNonSecuredOperation()
    {

    }

}
