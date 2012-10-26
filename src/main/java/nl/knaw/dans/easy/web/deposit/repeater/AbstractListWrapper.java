package nl.knaw.dans.easy.web.deposit.repeater;

import java.util.List;

import org.apache.wicket.Component;

public abstract class AbstractListWrapper<T> implements ListWrapper<T>
{

    private static final long serialVersionUID = 7473810515414444306L;

    private Component component;

    public void setComponent(Component component)
    {
        this.component = component;
    }

    public Component getComponent()
    {
        return component;
    }

    @Override
    public List<T> getInitialEditableItems()
    {
        return getInitialItems();
    }

    protected void handleErrors(List<String> messages, int index)
    {
        Component component = getComponent();
        if (component instanceof AbstractRepeaterPanel)
        {
            AbstractRepeaterPanel<?> repeaterPanel = (AbstractRepeaterPanel<?>) component;
            for (String message : messages)
            {
                repeaterPanel.error(index, message);
            }
        }
        else
        {
            for (String message : messages)
            {
                component.error(message);
            }
        }
    }

}
