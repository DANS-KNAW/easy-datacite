package nl.knaw.dans.easy.util;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class MessengerTest {

    public enum TestState {
        START, MIDDLE, A_BIT_FURTHER, END
    }

    public enum EmptyState {

    }

    @Test
    public void testMessenger() {
        Messenger<TestState> messenger = new Messenger<TestState>(TestState.class);
        assertEquals(TestState.START, messenger.getState());
        assertFalse(messenger.isCompleted());
        messenger.setState(TestState.MIDDLE);
        assertFalse(messenger.isCompleted());
        messenger.setState(TestState.END);
        assertTrue(messenger.isCompleted());
        assertEquals(TestState.class, messenger.getStateType());
    }

    @Test(expected = Exception.class)
    public void testEmptyState() {
        new Messenger<EmptyState>(EmptyState.class);
    }

    @Test
    public void accumulatedStates() {
        Messenger<TestState> messenger = new Messenger<TestState>(TestState.class);
        assertEquals(TestState.START, messenger.getState());
        assertTrue(messenger.getAccumelatedStates().isEmpty());

        messenger.setState(TestState.MIDDLE);
        assertEquals(1, messenger.getAccumelatedStates().size());
        assertTrue(messenger.getAccumelatedStates().contains(TestState.MIDDLE));
        assertEquals(TestState.MIDDLE, messenger.getState());

        messenger.setState(TestState.A_BIT_FURTHER, new IllegalArgumentException("!!"));
        assertEquals(TestState.A_BIT_FURTHER, messenger.getState());
        assertEquals(2, messenger.getAccumelatedStates().size());
        assertTrue(messenger.getAccumelatedStates().contains(TestState.MIDDLE));
        assertTrue(messenger.getAccumelatedStates().contains(TestState.A_BIT_FURTHER));
        assertEquals("Exception.count=1\n\tjava.lang.IllegalArgumentException message=!!", messenger.getExceptionsAsString());
        List<String> accumulatedStateKeys = messenger.getAccumulatedStateKeys();
        assertTrue(accumulatedStateKeys.contains("state.MIDDLE"));
        assertTrue(accumulatedStateKeys.contains("state.A_BIT_FURTHER"));
    }

    @Test
    public void stateKeys() {
        Messenger<TestState> messenger = new Messenger<TestState>(TestState.class);
        List<String> allStateKeys = messenger.getAllStateKeys();
        assertTrue(allStateKeys.contains("state.START"));
        assertTrue(allStateKeys.contains("state.MIDDLE"));
        assertTrue(allStateKeys.contains("state.A_BIT_FURTHER"));
        assertTrue(allStateKeys.contains("state.END"));
        messenger.dumpAllStateKeys();
    }

    @Test
    public void createMailToken() {
        Messenger<TestState> messenger = new Messenger<TestState>(TestState.class);
        String mailToken = messenger.createMailToken(null);
        assertNotNull(mailToken);

        mailToken = messenger.createMailToken("bla bla");
        assertNotNull(mailToken);
    }

}
