package nl.knaw.dans.i.security.annotations;

public abstract class MixedInterfacesClass implements IA {

    @Override
    public void iASecuredOperation() {

    }

    @Override
    public void iASecuredOperation(String s) {

    }

    @Override
    public void iANonSecuredOperation() {

    }

}
