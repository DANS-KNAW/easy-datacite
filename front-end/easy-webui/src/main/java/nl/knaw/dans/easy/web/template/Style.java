package nl.knaw.dans.easy.web.template;

import static org.apache.wicket.markup.html.CSSPackageResource.getHeaderContribution;

import org.apache.wicket.behavior.HeaderContributor;

public class Style {
    // @formatter:off 
    public static final HeaderContributor

            USER_SELECTOR_HEADER_CONTRIBUTION = getHeaderContribution(Style.class, "css/user_selector.css"),
            EASY_HEADER_CONTRIBUTION = getHeaderContribution(Style.class, "css/easy.css");
    // @formatter:on 
}
