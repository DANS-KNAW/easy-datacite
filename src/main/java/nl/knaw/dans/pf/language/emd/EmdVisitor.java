package nl.knaw.dans.pf.language.emd;

import nl.knaw.dans.pf.language.emd.bean.EmdContainer;

public interface EmdVisitor
{

    Object container(EmdContainer container);

}
