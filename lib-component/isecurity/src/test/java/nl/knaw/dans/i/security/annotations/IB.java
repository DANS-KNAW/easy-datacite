package nl.knaw.dans.i.security.annotations;

public interface IB extends IA
{

    @SecuredOperation
    void iBSecuredOperation();

    void iBNonSecuredOperation();

}
