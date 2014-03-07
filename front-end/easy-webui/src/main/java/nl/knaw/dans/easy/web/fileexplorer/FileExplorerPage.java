package nl.knaw.dans.easy.web.fileexplorer;

import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.https.RequireHttps;

/**
 * FileExplorePage displays FileExplorer Panel.
 */
@RequireHttps
public class FileExplorerPage extends AbstractEasyPage
{
    public FileExplorerPage(DatasetModel model)
    {
        super(model);
        add(new Label("message", ""));
        addContent(model);
    }

    private void addContent(DatasetModel model)
    {
        add(new FileExplorer("fe", model));
    }
}
