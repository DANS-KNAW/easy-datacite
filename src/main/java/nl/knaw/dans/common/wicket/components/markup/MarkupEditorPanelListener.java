package nl.knaw.dans.common.wicket.components.markup;

public interface MarkupEditorPanelListener
{
    
    void onSubmitWithErrors(String markup);
    
    void onSubmitWithWarnings(String markup);
    
    void onSubmit(String markup);
}
