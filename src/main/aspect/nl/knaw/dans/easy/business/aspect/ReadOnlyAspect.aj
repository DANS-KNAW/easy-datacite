import nl.knaw.dans.easy.security.And;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.SecurityOfficer;
import nl.knaw.dans.easy.security.UpdateEnabledCheck;

public aspect ReadOnlyAspect
{
    pointcut updateRules() :
        execution(protected SecurityOfficer CodedAuthz.getEnableToLoggedInUserRule())||
        execution(protected SecurityOfficer CodedAuthz.*Update*()) ||
        execution(protected SecurityOfficer CodedAuthz.*Edit*()) ||
        execution(public SecurityOfficer CodedAuthz.*Update*()) ||
        execution(public SecurityOfficer CodedAuthz.*Edit*()) 
        ;

    SecurityOfficer around() : updateRules()
    {
        return new And(proceed(), new UpdateEnabledCheck());
    }
}
