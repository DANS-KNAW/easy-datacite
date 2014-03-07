package nl.knaw.dans.common.lang.progress;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgressAggregatorTest
{
    private static final Logger logger = LoggerFactory.getLogger(ProgressAggregatorTest.class);

    private static boolean verbose = Tester.isVerbose();

    @Test
    public void aggregateProgress()
    {
        ProgressAggregator cpl = new ProgressAggregator();
        TestProgressListener tpl = new TestProgressListener();
        cpl.addListeners(tpl);

        List<ProgressSubject> progressSubjects = createProgressSubjects(4);
        for (ProgressSubject ps : progressSubjects)
        {
            cpl.registerSubject(ps);
        }

        for (ProgressSubject ps : progressSubjects)
        {
            playSubject(ps, false, false);
        }

        assertEquals(tpl.map.get("process.id.1"), new Integer(10));
        assertEquals(tpl.map.get("process.id.2"), new Integer(30));
        assertEquals(tpl.map.get("process.id.3"), new Integer(60));
        assertEquals(tpl.map.get("process.id.4"), new Integer(100));
    }

    @Test(expected = IllegalStateException.class)
    public void aggregateProgressSkippingOnStart()
    {
        ProgressAggregator cpl = new ProgressAggregator();

        List<ProgressSubject> progressSubjects = createProgressSubjects(4);
        for (ProgressSubject ps : progressSubjects)
        {
            cpl.registerSubject(ps);
        }

        try
        {
            for (int i = 0; i < progressSubjects.size(); i++)
            {
                playSubject(progressSubjects.get(i), i == 2, false);
            }
        }
        catch (IllegalStateException e)
        {
            if (verbose)
                logger.error("Expected error:\n", e);
            throw e;
        }

    }

    @Test(expected = IllegalStateException.class)
    public void aggregateProgressSkippingOnEnd()
    {
        ProgressAggregator cpl = new ProgressAggregator();

        List<ProgressSubject> progressSubjects = createProgressSubjects(4);
        for (ProgressSubject ps : progressSubjects)
        {
            cpl.registerSubject(ps);
        }

        try
        {
            for (int i = 0; i < progressSubjects.size(); i++)
            {
                playSubject(progressSubjects.get(i), false, i == 1);
            }
        }
        catch (IllegalStateException e)
        {
            if (verbose)
                logger.error("Expected error:\n", e);
            throw e;
        }

    }

    private void playSubject(ProgressSubject ps, boolean skipStart, boolean skipEnd)
    {
        int total = 2000;
        if (!skipStart)
            ps.onStartProcess();
        for (int i = 0; i < total; i++)
        {
            ps.onProgress(total, i);
        }
        if (!skipEnd)
            ps.onEndProcess();
    }

    private List<ProgressSubject> createProgressSubjects(int amount)
    {
        List<ProgressSubject> subjects = new ArrayList<ProgressSubject>();
        for (int i = 0; i < amount; i++)
        {
            ProgressSubject ps = new ProgressSubject("" + (i + 1), i + 1);
            subjects.add(ps);
        }
        return subjects;
    }

    private static class TestProgressListener implements ProgressListener
    {

        int currentProgress;
        private Map<String, Integer> map = new HashMap<String, Integer>();

        @Override
        public void onStartProcess(String processId)
        {
            if (verbose)
                System.err.println("onStart " + processId);
        }

        @Override
        public void updateProgress(int percentage)
        {
            currentProgress = percentage;
            if (verbose)
                System.err.println("update " + percentage);
        }

        @Override
        public void onEndProcess(String processId)
        {
            map.put(processId, currentProgress);
            if (verbose)
                System.err.println("onEnd " + processId);
        }

    }

}
