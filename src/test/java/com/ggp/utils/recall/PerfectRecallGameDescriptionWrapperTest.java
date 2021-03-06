package com.ggp.utils.recall;

import com.ggp.ICompleteInformationState;
import com.ggp.games.LeducPoker.Cards;
import com.ggp.games.LeducPoker.GameDescription;
import com.ggp.games.LeducPoker.actions.CallAction;
import com.ggp.games.LeducPoker.actions.DealCardAction;
import com.ggp.games.LeducPoker.actions.RaiseAction;
import com.ggp.utils.GameUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PerfectRecallGameDescriptionWrapperTest {

    @Test
    void testLeducPoker() {
        ICompleteInformationState initialState = (new PerfectRecallGameDescriptionWrapper(new GameDescription(7,7))).getInitialState();
        ICompleteInformationState s1, s2;
        s1 = GameUtils.applyActionSequnce(initialState,
                new DealCardAction(Cards.J, 1),
                new DealCardAction(Cards.Q, 2),
                CallAction.instance,
                RaiseAction.instance,
                CallAction.instance,
                new DealCardAction(Cards.K));
        s2 = GameUtils.applyActionSequnce(initialState,
                new DealCardAction(Cards.J, 1),
                new DealCardAction(Cards.Q, 2),
                RaiseAction.instance,
                CallAction.instance,
                new DealCardAction(Cards.K));

        // both action arrive to same CIS in original game, but they have different action sequence
        // and therefore should be different in perfect-recall game.
        assertNotEquals(s1.getInfoSetForPlayer(1), s2.getInfoSetForPlayer(1));
        assertNotEquals(s1.getInfoSetForPlayer(2), s2.getInfoSetForPlayer(2));
        assertNotEquals(s1, s2);

        ICompleteInformationState os1 = ((PerfectRecallCIS)s1).getOrigState();
        ICompleteInformationState os2 = ((PerfectRecallCIS)s2).getOrigState();
        assertEquals(os1.getInfoSetForPlayer(1), os2.getInfoSetForPlayer(1));
        assertEquals(os1.getInfoSetForPlayer(2), os2.getInfoSetForPlayer(2));
        assertEquals(os1, os2);

        assertEquals(s1.getInfoSetForPlayer(1), s1.getInfoSetForPlayer(1));
        assertEquals(s1.getInfoSetForPlayer(2), s1.getInfoSetForPlayer(2));
    }

    @Test
    void testRPS() {
        ICompleteInformationState initialState = (new PerfectRecallGameDescriptionWrapper(new com.ggp.games.RockPaperScissors.GameDescription(3))).getInitialState();
        assertNotEquals(initialState.getInfoSetForPlayer(1), initialState.getInfoSetForPlayer(2));
    }
}