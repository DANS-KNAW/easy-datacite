package nl.knaw.dans.common.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;

public class ExcludeMessageFilter implements IFeedbackMessageFilter {
    private static final long serialVersionUID = 8558283565551901537L;

    private MarkupContainer excludeContainer;

    public ExcludeMessageFilter(MarkupContainer excludeContainer) {
        this.excludeContainer = excludeContainer;
    }

    @Override
    public boolean accept(FeedbackMessage message) {
        Component reporter = message.getReporter();
        if (reporter == null)
            return true;
        return !excludeContainer.contains(reporter, true);
    }

}
