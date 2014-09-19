package nl.knaw.dans.easy.domain.dataset;

import java.io.Serializable;
import java.util.List;

import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public interface DatasetSubmission extends Serializable {

    void clearAllMessages();

    boolean isCompleted();

    boolean isMailSend();

    Dataset getDataset();

    EasyUser getSessionUser();

    boolean hasMetadataErrors();

    FormPage getFirstErrorPage();

    List<String> getGlobalErrorMessages();

    List<String> getGlobalInfoMessages();

    boolean hasGlobalMessages();

    String getDatasetTitle();

    String getState();

}
