package nl.knaw.dans.easy.domain.worker;

import nl.knaw.dans.common.lang.repo.UnitOfWorkListener;

public interface WorkListener extends UnitOfWorkListener
{
    
    boolean onWorkStart();
    
    void onWorkEnd();

}
