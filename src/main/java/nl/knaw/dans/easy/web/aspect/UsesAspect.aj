package nl.knaw.dans.easy.web.aspect;


public aspect UsesAspect
{
    
    pointcut data() : call(* nl.knaw.dans.easy.data..*.*(..))
        && !call(* nl.knaw.dans.easy.data.search.*.*(..));
    
    pointcut business() : call(* nl.knaw.dans.easy.business..*.*(..));
    
    pointcut ldap() : call(* nl.knaw.dans.easy.ldap..*.*(..)) ||
                    call(* nl.knaw.dans.common.ldap..*.*(..));
    
    pointcut fedora() : call(* nl.knaw.dans.easy.fedora..*.*(..)) ||
                      call(* nl.knaw.dans.common.fedora..*.*(..));         
    
    pointcut canDo() : withincode(* nl.knaw.dans.easy.web..*Test.*(..));
    
    declare error : data() && !canDo()
        : "Illegal direct call to data layer. Use nl.dans.knaw.easy.servicelayer.Services";
    
    declare warning : data() && canDo()
        : "Illegal direct call to data layer. Use nl.dans.knaw.easy.servicelayer.Services";
    
    declare error : business()
        : "Illegal direct call to business layer. Use nl.dans.knaw.easy.servicelayer";
    
    declare error : ldap()
        : "Illegal direct call to ldap plugins";
    
    declare error : fedora()
        : "Illegal direct call to fedora plugins";
}
