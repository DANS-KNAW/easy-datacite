package nl.knaw.dans.easy.web.wicket.test;

import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.wicketstuff.progressbar.ProgressBar;
import org.wicketstuff.progressbar.Progression;
import org.wicketstuff.progressbar.ProgressionModel;

public class TestProgressPage extends AbstractEasyNavPage
{

    private int         progress = 0;
    private ProgressBar bar;

    public TestProgressPage()
    {
        init();
    }

    private void init()
    {
        setOutputMarkupId(true);
        add(bar = new ProgressBar("bar", new ProgressionModel()
        {
            private static final long serialVersionUID = 1L;

            protected Progression getProgression()
            {
                System.err.println("getProgression " +progress);
                return new Progression(progress);
            }
        })
        {
            private static final long serialVersionUID = 1L;

            protected void onFinished(AjaxRequestTarget target)
            {
                //setVisible(false);
                target.appendJavascript("alert('Task done!')");
            }
        });
        
        Form<String> form = new Form<String>("form");
        add(form);
        
        form.add(new IndicatingAjaxButton("submit", form) {

            private static final long serialVersionUID = 1L;

            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                bar.start(target);
                new Thread() {
                    public void run() {
                        for(int i = 0; i <= 100; i++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) { }
                                progress = i;
                                System.err.println("next=" + i);
                            }
                    }
                }.start();
            }
        });
    }

}
