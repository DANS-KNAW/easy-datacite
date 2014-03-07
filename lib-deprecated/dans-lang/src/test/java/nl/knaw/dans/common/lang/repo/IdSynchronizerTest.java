package nl.knaw.dans.common.lang.repo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.exception.LockAcquireTimeoutException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdSynchronizerTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IdSynchronizerTest.class);

    class Obj
    {
        public String id;
        public String label;

        public Obj(String id)
        {
            this.id = id;
        }
    }

    @Test
    public void testSync() throws Exception
    {
        final IdSynchronizer<String> sync = new IdSynchronizer<String>();

        final Obj A = new Obj("A");
        final Obj B = new Obj("B");
        final Obj C = new Obj("C");
        final Obj D = new Obj("D");
        final int sleepMultiplier = 2;

        Thread t1 = new Thread()
        {
            @Override
            public void run()
            {
                List<String> idList = getIdList(A, B, C);
                try
                {
                    LOGGER.debug("t1 awaiting lock");
                    sync.acquireLock(idList);
                    LOGGER.debug("t1 locked");
                    Thread.sleep(125 * sleepMultiplier);
                    A.label = "hello";
                    C.label = "foo";
                }
                catch (InterruptedException e)
                {
                    assertEquals(true, false);
                }
                catch (LockAcquireTimeoutException e)
                {
                    assertEquals(true, false);
                }
                finally
                {
                    sync.releaseLock(idList);
                }
            }
        };

        Thread t2 = new Thread()
        {
            @Override
            public void run()
            {
                List<String> idList = getIdList(B, C, D);
                try
                {
                    LOGGER.debug("t2 awaiting lock");
                    sync.acquireLock(idList);
                    LOGGER.debug("t2 locked");
                    Thread.sleep(125 * sleepMultiplier);
                    B.label = "world";
                    C.label = C.label + "bar";
                    D.label = "hello ";
                }
                catch (InterruptedException e)
                {
                    assertEquals(true, false);
                }
                catch (LockAcquireTimeoutException e)
                {
                    assertEquals(true, false);
                }
                finally
                {
                    sync.releaseLock(idList);
                }
            }
        };

        Thread t3 = new Thread()
        {
            @Override
            public void run()
            {
                List<String> idList = getIdList(D);
                try
                {
                    LOGGER.debug("t3 awaiting lock");
                    sync.acquireLock(idList);
                    LOGGER.debug("t3 locked");
                    D.label = A.label + " " + B.label + " " + C.label;
                }
                catch (InterruptedException e)
                {
                    assertEquals(true, false);
                }
                catch (LockAcquireTimeoutException e)
                {
                    assertEquals(true, false);
                }
                finally
                {
                    sync.releaseLock(idList);
                }
            }
        };

        t1.start();
        Thread.sleep(10 * sleepMultiplier);
        t2.start();
        Thread.sleep(10 * sleepMultiplier);
        t3.start();
        Thread.sleep(10 * sleepMultiplier);

        assertEquals(1, sync.getLockThreadCount(A.id));
        assertEquals(2, sync.getLockThreadCount(B.id));
        assertEquals(2, sync.getLockThreadCount(C.id));
        assertEquals(2, sync.getLockThreadCount(D.id));

        assertEquals(4, sync.getLockIdCount());

        t1.join();
        LOGGER.debug("t1 finished");
        t2.join();
        LOGGER.debug("t2 finished");
        t3.join();
        LOGGER.debug("t3 finished");

        assertEquals("foobar", C.label);
        assertEquals("hello world foobar", D.label);

        assertEquals(0, sync.getLockThreadCount(A.id));
        assertEquals(0, sync.getLockThreadCount(B.id));
        assertEquals(0, sync.getLockThreadCount(C.id));
        assertEquals(0, sync.getLockThreadCount(D.id));
        assertEquals(0, sync.getLockIdCount());
    }

    @Test
    public void testNestedLocks() throws Exception
    {
        final IdSynchronizer<String> sync = new IdSynchronizer<String>();

        sync.acquireLock("A");
        sync.acquireLock("A");
        sync.acquireLock("A");
        sync.acquireLock("A");
        sync.acquireLock("B");

        assertEquals(1, sync.getLockThreadCount("A"));
        assertEquals(1, sync.getLockThreadCount("B"));
        assertEquals(2, sync.getLockIdCount());
        assertEquals(4, sync.getLockThreadUseCount("A"));
        assertEquals(1, sync.getLockThreadUseCount("B"));

        sync.releaseLock("B");
        sync.releaseLock("A");
        sync.releaseLock("A");
        sync.releaseLock("A");
        sync.releaseLock("A");

        assertEquals(0, sync.getLockThreadCount("A"));
        assertEquals(0, sync.getLockThreadCount("B"));
        assertEquals(0, sync.getLockIdCount());
        assertEquals(0, sync.getLockThreadUseCount("A"));
        assertEquals(0, sync.getLockThreadUseCount("B"));
    }

    @Test(expected = LockAcquireTimeoutException.class)
    public void testTimeout() throws InterruptedException, LockAcquireTimeoutException
    {
        final IdSynchronizer<String> sync = new IdSynchronizer<String>(10, true);
        Thread t1 = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sync.acquireLock("A");
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    assertEquals(true, false);
                }
                catch (LockAcquireTimeoutException e)
                {
                    assertEquals(true, false);
                }
                finally
                {
                    sync.releaseLock("A");
                }
            }
        };
        t1.start();

        Thread.sleep(20);
        sync.acquireLock("A");
        sync.releaseLock("A");
    }

    class StateThread extends Thread
    {
        public int state = 0;

        public void waitFor(int waitForState) throws InterruptedException
        {
            while (this.state != waitForState)
            {
                Thread.sleep(5);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testInterrupt() throws InterruptedException, LockAcquireTimeoutException
    {
        final IdSynchronizer<String> sync = new IdSynchronizer<String>();

        final Obj A = new Obj("A");
        final Obj B = new Obj("B");
        final Obj C = new Obj("C");

        List<String> idList = getIdList(A, B);
        sync.acquireLock(idList);

        StateThread t1 = new StateThread()
        {
            @Override
            public void run()
            {
                List<String> idList = getIdList(B, C);
                try
                {
                    LOGGER.debug("t1 trying to acquire lock on B and C");
                    state = 1;
                    sync.acquireLock(idList);
                    state = 2;
                }
                catch (InterruptedException e)
                {
                    state = 3;
                }
                catch (LockAcquireTimeoutException e)
                {
                    state = 4;
                }
                finally
                {
                    //	sync.releaseLock(idList);
                }
            }
        };

        assertEquals(1, sync.getLockThreadCount("A"));
        assertEquals(1, sync.getLockThreadCount("B"));
        assertEquals(0, sync.getLockThreadCount("C"));

        t1.start();
        t1.waitFor(1);
        Thread.sleep(10);

        assertEquals(1, sync.getLockThreadCount("A"));
        assertEquals(2, sync.getLockThreadCount("B"));
        assertEquals(1, sync.getLockThreadCount("C"));

        t1.interrupt();
        t1.stop();
        t1.join(1000);

        assertEquals(3, t1.state);
        assertEquals(1, sync.getLockThreadCount("A"));
        assertEquals(1, sync.getLockThreadCount("B"));
        assertEquals(0, sync.getLockThreadCount("C"));

        sync.releaseLock(idList);

        assertEquals(0, sync.getLockThreadCount("A"));
        assertEquals(0, sync.getLockThreadCount("B"));
        assertEquals(0, sync.getLockThreadCount("C"));
    }

    private List<String> getIdList(Obj... dmos)
    {
        ArrayList<String> result = new ArrayList<String>(dmos.length);
        for (int i = 0; i < dmos.length; i++)
        {
            result.add(dmos[i].id);
        }
        return result;
    }
}
