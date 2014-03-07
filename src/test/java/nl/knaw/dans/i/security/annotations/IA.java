package nl.knaw.dans.i.security.annotations;

public interface IA
{
    @SecuredOperation
    void iASecuredOperation();
    
    @SecuredOperation
    void iASecuredOperation(String s);
    
    void iANonSecuredOperation();
}
