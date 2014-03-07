package nl.knaw.dans.common.wicket.components.editablepanel;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CompositeProcessor implements EditablePanel.Processor, Serializable
{
    private final EditablePanel.Processor[] processors;

    public CompositeProcessor(final EditablePanel.Processor... processors)
    {
        this.processors = processors;
    }

    @Override
    public String process(final String content)
    {
        String result = content;

        for (final EditablePanel.Processor p : processors)
        {
            result = p.process(result);
        }

        return result;
    }

}
