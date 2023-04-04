package cs2212.westernmaps.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class UndoHistoryTest {

    @Test
    public void testInitialState() {
        var history = new UndoHistory(DatabaseState.EMPTY);
        Assertions.assertEquals(DatabaseState.EMPTY, history.getCurrentState());
    }

    @Test
    public void testPushState() {
        var state0 = DatabaseState.EMPTY;
        var history = new UndoHistory(state0);
        Assertions.assertEquals(state0, history.getCurrentState());

        var asdfAccount = new Account("asdf", "", false);
        var state1 = history.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, asdfAccount));
        history.pushState(state1);
        Assertions.assertEquals(state1, history.getCurrentState());

        var hjklAccount = new Account("hjkl", "", true);
        var state2 = history.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, hjklAccount));
        history.pushState(state2);
        Assertions.assertEquals(state2, history.getCurrentState());

        // pushState(...) adds states to the history, so undo is possible.
        Assertions.assertTrue(history.undo());
        Assertions.assertEquals(state1, history.getCurrentState());
        Assertions.assertTrue(history.undo());
        Assertions.assertEquals(state0, history.getCurrentState());

        // We're at the start, so undo is no longer possible.
        Assertions.assertFalse(history.undo());
        Assertions.assertEquals(state0, history.getCurrentState());
    }

    @Test
    public void testPushStateInThePast() {
        var state0 = DatabaseState.EMPTY;
        var history = new UndoHistory(state0);
        Assertions.assertEquals(state0, history.getCurrentState());

        // Start by populating the history with some states.
        var asdfAccount = new Account("asdf", "", false);
        var state1 = history.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, asdfAccount));
        history.pushState(state1);
        Assertions.assertEquals(state1, history.getCurrentState());

        var hjklAccount = new Account("hjkl", "", true);
        var state2 = history.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, hjklAccount));
        history.pushState(state2);
        Assertions.assertEquals(state2, history.getCurrentState());

        // Then, undo once...
        Assertions.assertTrue(history.undo());
        Assertions.assertEquals(state1, history.getCurrentState());

        // ...and push a state while we're in the past.
        var qwerAccount = new Account("qwer", "", true);
        var state3 = history.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, qwerAccount));
        history.pushState(state3);
        Assertions.assertEquals(state3, history.getCurrentState());

        // Now, state2 should be completely gone from the history, since we
        // overwrote it.
        Assertions.assertTrue(history.undo());
        Assertions.assertEquals(state1, history.getCurrentState());
        Assertions.assertTrue(history.undo());
        Assertions.assertEquals(state0, history.getCurrentState());

        // We're at the start, so undo is no longer possible.
        Assertions.assertFalse(history.undo());
        Assertions.assertEquals(state0, history.getCurrentState());
    }

    @Test
    public void testUndoAndRedo() {
        var state0 = DatabaseState.EMPTY;
        var history = new UndoHistory(state0);
        Assertions.assertEquals(state0, history.getCurrentState());

        // Start by populating the history with some states.
        var asdfAccount = new Account("asdf", "", false);
        var state1 = history.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, asdfAccount));
        history.pushState(state1);
        Assertions.assertEquals(state1, history.getCurrentState());

        var hjklAccount = new Account("hjkl", "", true);
        var state2 = history.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, hjklAccount));
        history.pushState(state2);
        Assertions.assertEquals(state2, history.getCurrentState());

        // We haven't undone anything yet, so redo is not possible.
        Assertions.assertFalse(history.redo());
        Assertions.assertEquals(state2, history.getCurrentState());

        // Undo all the way to the start.
        Assertions.assertTrue(history.undo());
        Assertions.assertEquals(state1, history.getCurrentState());
        Assertions.assertTrue(history.undo());
        Assertions.assertEquals(state0, history.getCurrentState());

        // We're at the start, so undo is no longer possible.
        Assertions.assertFalse(history.undo());
        Assertions.assertEquals(state0, history.getCurrentState());

        // Redo everything again.
        Assertions.assertTrue(history.redo());
        Assertions.assertEquals(state1, history.getCurrentState());
        Assertions.assertTrue(history.redo());
        Assertions.assertEquals(state2, history.getCurrentState());

        // We're at the end again, so redo is no longer possible.
        Assertions.assertFalse(history.redo());
        Assertions.assertEquals(state2, history.getCurrentState());
    }

    @Test
    public void testReplaceHistoryWithState() {
        var state0 = DatabaseState.EMPTY;
        var history = new UndoHistory(state0);
        Assertions.assertEquals(state0, history.getCurrentState());

        // Start by populating the history with some states.
        var asdfAccount = new Account("asdf", "", false);
        var state1 = history.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, asdfAccount));
        history.pushState(state1);
        Assertions.assertEquals(state1, history.getCurrentState());

        var hjklAccount = new Account("hjkl", "", true);
        var state2 = history.getCurrentState().modifyAccounts(accounts -> Lists.append(accounts, hjklAccount));
        history.pushState(state2);
        Assertions.assertEquals(state2, history.getCurrentState());

        // Clear the history and make sure the current state is the same.
        history.replaceHistoryWithState(state2);
        Assertions.assertEquals(state2, history.getCurrentState());

        // Now, we shouldn't be able to undo or redo since the history has been
        // cleared.
        Assertions.assertFalse(history.undo());
        Assertions.assertEquals(state2, history.getCurrentState());
        Assertions.assertFalse(history.redo());
        Assertions.assertEquals(state2, history.getCurrentState());
    }
}
