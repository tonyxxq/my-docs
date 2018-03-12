package com.ggp.games.TicTacToe;

import com.ggp.IPercept;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CompleteInformationStateTest {

    private CompleteInformationState initialState;

    @Before
    public void setUp() throws Exception {
        GameDescription gd = new GameDescription();
        this.initialState = (CompleteInformationState) gd.getInitialState();
    }

    @After
    public void tearDown() throws Exception {
    }

    private void assertPercept(Iterable<IPercept> percepts, boolean expected, int player) {
        int i = 0;
        for(IPercept _p: percepts) {
            ActionSuccessPercept p = (ActionSuccessPercept) _p;
            assertSame(expected, p.isSuccessful());
            assertSame(player, p.getTargetPlayer());
            i++;
        }
        assertSame(1, i);
    }

    @Test
    public void testGameplay_xWin_col() {
        CompleteInformationState s = initialState;
        assertSame(25, s.getLegalActions().size());
        s = (CompleteInformationState) s.next(MarkFieldAction.getAction(1, 0, 0));
        assertSame(2, s.getActingPlayerId());
        assertSame(25, s.getLegalActions().size());
        // test percepts
        assertPercept(s.getPercepts(MarkFieldAction.getAction(2, 0, 0)), false, 2);
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                if (x == 0 && y == 0) continue;
                assertPercept(s.getPercepts(MarkFieldAction.getAction(2, x, y)), true, 2);
            }
        }

        s = (CompleteInformationState) s.next(MarkFieldAction.getAction(2, 0, 0));
        assertFalse(s.isLegal(MarkFieldAction.getAction(2, 0, 0))); // wrong player
        assertFalse(s.isLegal(MarkFieldAction.getAction(1, 0, 0))); // already marked
        assertFalse(s.isTerminal());
        CompleteInformationState lastRound = null;
        for (int x = 1; x < 5; ++x) {
            assertFalse(s.isTerminal());
            if (x == 4) {
                lastRound = s;
            }
            s = (CompleteInformationState) s.next(MarkFieldAction.getAction(1, x, 0));
            assertFalse(s.isTerminal());
            s = (CompleteInformationState) s.next(MarkFieldAction.getAction(2, x, 1));
        }
        assertTrue(s.isTerminal());
        // player 1 wins
        assertSame(1, (int) s.getPayoff(1));
        assertSame(-1, (int) s.getPayoff(2));

        // make player 2 win starting from lastRound
        s = (CompleteInformationState) lastRound.next(MarkFieldAction.getAction(1, 1, 1));
        s = (CompleteInformationState) s.next(MarkFieldAction.getAction(2, 4, 1));
        s = (CompleteInformationState) s.next(MarkFieldAction.getAction(1, 2, 1));
        s = (CompleteInformationState) s.next(MarkFieldAction.getAction(2, 0, 1));
        assertTrue(s.isTerminal());
        // player 2 wins
        assertSame(-1, (int) s.getPayoff(1));
        assertSame(1, (int) s.getPayoff(2));
    }

    @Test
    public void testGameplay_xWin_row() {
        CompleteInformationState s = initialState;
        for (int y = 0; y < 5; ++y) {
            s = (CompleteInformationState) s.next(MarkFieldAction.getAction(1, 2, y));
            s = (CompleteInformationState) s.next(MarkFieldAction.getAction(2, y, 0));
        }

        assertTrue(s.isTerminal());
        // player 1 wins
        assertSame(1, (int) s.getPayoff(1));
        assertSame(-1, (int) s.getPayoff(2));
    }

    @Test
    public void testGameplay_oWin_diag() {
        CompleteInformationState s = initialState;
        for (int y = 0; y < 5; ++y) {
            s = (CompleteInformationState) s.next(MarkFieldAction.getAction(1, 0, 4-y));
            s = (CompleteInformationState) s.next(MarkFieldAction.getAction(2, y, y)); // player 2 will reach [0,0] first
        }

        assertTrue(s.isTerminal());
        // player 2 wins
        assertSame(-1, (int) s.getPayoff(1));
        assertSame(1, (int) s.getPayoff(2));
    }

    @Test
    public void testGameplay_draw() {
        CompleteInformationState s = initialState;
        for (int y = 0; y < 5; ++y) {
            s = (CompleteInformationState) s.next(MarkFieldAction.getAction(1, 0, y));
            assertFalse(s.isTerminal());
            s = (CompleteInformationState) s.next(MarkFieldAction.getAction(2, 1, y));
        }

        assertTrue(s.isTerminal());
        // draw
        assertSame(0, (int) s.getPayoff(1));
        assertSame(0, (int) s.getPayoff(2));
    }
}