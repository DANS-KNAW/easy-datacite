package nl.knaw.dans.easy.web.fileexplorer2;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/*
 * Reusable class for displaying a simple popup with a message
 */

public class ModalPopup extends Panel {
	private static final long serialVersionUID = 1L;

	public ModalPopup(final ModalWindow window, String message) {
		super(window.getContentId());
		
		add(new Label("message", message));
		
		add(new IndicatingAjaxLink<Void>("close"){
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				window.close(target);
			}
		});
	}
}
