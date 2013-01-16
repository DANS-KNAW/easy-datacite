package nl.knaw.dans.easy.web.wicketutil;

import java.io.File;

import nl.knaw.dans.common.lang.HomeDirectory;
import nl.knaw.dans.common.wicket.model.TextFileModel;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditableContentHomeBasedTextFileModel extends TextFileModel
{
    private static final Logger log = LoggerFactory.getLogger(EditableContentHomeBasedTextFileModel.class);

    @SpringBean(name = "editableContentHome")
    private HomeDirectory home;

    public EditableContentHomeBasedTextFileModel(String relativeContentPath)
    {
        log.debug("Trying to inject EasyHome bean ...");
        InjectorHolder.getInjector().inject(this);
        log.debug("Injecting EasHome bean success");
        setFile(new File(home.getHomeDirectory(), relativeContentPath));
    }

}
