package nl.knaw.dans.easy.web.wicket.test;

import nl.knaw.dans.easy.web.SessionScopeService;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.wicketstuff.progressbar.ProgressBar;
import org.wicketstuff.progressbar.spring.ITaskService;
import org.wicketstuff.progressbar.spring.Task;
import org.wicketstuff.progressbar.spring.TaskProgressionModel;

public class TaskServiceProgressExamplePage extends AbstractEasyNavPage
{
    private static class DummyTask extends Task {
        private final int iterations;

        public DummyTask(int iterations) {
            this.iterations = iterations;
        }

        @Override
        protected void run() {
            for(int i = 0; i < iterations; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                System.err.println(this + " is at " + i);
                updateProgress(i, iterations);
                if(isCancelled()) return;
            }
        }
    }

    public TaskServiceProgressExamplePage() {
        final Form form = new Form("form");
        final ProgressBar bar;
        final TaskProgressionModel progressionModel = new TaskProgressionModel() {
            @Override
            protected ITaskService getTaskService() {
                return SessionScopeService.getTaskService();
            }
        };
        form.add(bar = new ProgressBar("bar", progressionModel) {
            @Override
            protected void onFinished(AjaxRequestTarget target) {
                ITaskService taskService = SessionScopeService.getTaskService();
                // finish the task!
                taskService.finish(progressionModel.getTaskId());
                // Hide progress bar after finish
                setVisible(false);
                // Add some JavaScript after finish
                target.appendJavascript("alert('Task done and finished!')");

                // re-enable button
                Component button = form.get("submit");
                button.setEnabled(true);
                target.addComponent(button);
            }
        });
        // Hide progress bar initially
        bar.setVisible(false);

        form.add(new IndicatingAjaxButton("submit", form) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                ITaskService taskService = SessionScopeService.getTaskService();
                // Schedule and start a new task
                Long taskId = taskService.scheduleAndStart(new DummyTask(60));
                // Set taskId for model
                progressionModel.setTaskId(taskId);
                // Start the progress bar, will set visibility to true
                bar.start(target);

                // disable button
                setEnabled(false);
            }
        });
        form.setOutputMarkupId(true);
        add(form);
    }


}
