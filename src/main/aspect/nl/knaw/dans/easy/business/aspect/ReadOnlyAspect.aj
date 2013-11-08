import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.And;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.HasRoleCheck;
import nl.knaw.dans.easy.security.Or;
import nl.knaw.dans.easy.security.SecurityOfficer;
import nl.knaw.dans.easy.security.UpdateEnabledCheck;

public aspect ReadOnlyAspect
{
    pointcut readOnlyRules() :
        execution(protected SecurityOfficer CodedAuthz.getDepositRule())||
        execution(protected SecurityOfficer CodedAuthz.getPermissionRequestRule())||
        execution(protected SecurityOfficer CodedAuthz.getPermissionReplyRule());

    SecurityOfficer around() : readOnlyRules()
    {
        return new And(proceed(), new Or(new UpdateEnabledCheck(), new HasRoleCheck(Role.ARCHIVIST, Role.ADMIN)));
    }
}
