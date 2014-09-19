package nl.knaw.dans.easy.web.wicket;

import java.io.Serializable;

import nl.knaw.dans.easy.domain.form.FormPage;

public interface WizardNavigationListener extends Serializable {

    void onPageClick(FormPage requestedFormPage);

}
