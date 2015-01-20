package nl.knaw.dans.easy.web.template;

import static org.apache.wicket.markup.html.CSSPackageResource.getHeaderContribution;

import org.apache.wicket.behavior.HeaderContributor;

public class Style {
    // @formatter:off 
    public static final HeaderContributor

            MODAL_HEADER_CONTRIBUTION = getHeaderContribution(Style.class, "css/modal/modal.css"),
            USER_SELECTOR_HEADER_CONTRIBUTION = getHeaderContribution(Style.class, "css/user_selector.css"),
            ADMIN_PANEL_CONTRIBUTION = getHeaderContribution(Style.class, "css/admin_panel.css"),
            EASY_HEADER_CONTRIBUTION = getHeaderContribution(Style.class, "css/easy.css");
    // @formatter:on 
}
