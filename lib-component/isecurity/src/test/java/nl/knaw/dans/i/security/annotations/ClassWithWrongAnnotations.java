package nl.knaw.dans.i.security.annotations;

public class ClassWithWrongAnnotations implements IA, IB {

    @SecuredOperation(id = "nl.knaw.dans.i.security.annotations.IA.iASecuredOperation")
    @Override
    public void iBSecuredOperation() {

    }

    @SecuredOperation(id = "nl.knaw.dans.i.security.annotations.IA.iASecuredOperation")
    @Override
    public void iASecuredOperation(String s) {

    }

    @Override
    public void iBNonSecuredOperation() {

    }

    @SecuredOperation(id = "nl.knaw.dans.i.security.annotations.IB.iBSecuredOperation")
    @Override
    public void iASecuredOperation() {

    }

    @Override
    public void iANonSecuredOperation() {

    }

}
