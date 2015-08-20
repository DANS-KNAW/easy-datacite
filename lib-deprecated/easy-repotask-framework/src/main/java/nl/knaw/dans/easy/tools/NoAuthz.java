package nl.knaw.dans.easy.tools;

import java.util.List;

import nl.knaw.dans.easy.security.Authz;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.SecurityOfficer;

public class NoAuthz implements Authz {

    @Override
    public SecurityOfficer getSecurityOfficer(String item) {
        return new SecurityOfficer() {

            @Override
            public boolean isEnableAllowed(ContextParameters ctxParameters) {
                return true;
            }

            @Override
            public boolean isComponentVisible(ContextParameters ctxParameters) {
                return true;
            }

            @Override
            public String getProposition() {
                return "No authorization";
            }

            @Override
            public boolean getHints(ContextParameters ctxParameters, List<Object> hints) {
                return false;
            }

            @Override
            public String explainEnableAllowed(ContextParameters ctxParameters) {
                return "No authorization";
            }

            @Override
            public String explainComponentVisible(ContextParameters ctxParameters) {
                return "No authorization";
            }
        };
    }

    @Override
    public boolean hasSecurityOfficer(String item) {
        return false;
    }

    @Override
    public boolean isProtectedPage(String pageName) {
        return false;
    }

}
