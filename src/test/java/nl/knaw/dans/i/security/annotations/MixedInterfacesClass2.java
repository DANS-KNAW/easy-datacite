package nl.knaw.dans.i.security.annotations;

public class MixedInterfacesClass2 extends MixedInterfacesClass implements IB
{

    @Override
    public void iBSecuredOperation()
    {

    }

    @Override
    public void iBNonSecuredOperation()
    {

    }

}
