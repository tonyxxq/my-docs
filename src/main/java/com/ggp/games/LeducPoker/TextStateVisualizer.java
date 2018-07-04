package com.ggp.games.LeducPoker;

import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IStateVisualizer;

public class TextStateVisualizer implements IStateVisualizer {
    @Override
    public void visualize(IInformationSet s, int role) {
    }

    @Override
    public void visualize(ICompleteInformationState s) {
        CompleteInformationState state = (CompleteInformationState) s;
        InformationSet is1 = (InformationSet) s.getInfoSetForPlayer(1), is2 = (InformationSet) s.getInfoSetForPlayer(2);
        System.out.println(String.format("[%s] %d: 1(%s, %d/%d), 2(%s, %d/%d), Public(%s), Pot = %d",
                is2.getRound(), state.getActingPlayerId(), is1.getPrivateCard(), is1.getRemainingMoney(),
                is1.getStartingMoney(), is2.getPrivateCard(), is2.getRemainingMoney(), is2.getStartingMoney(),
                is1.getPublicCard(), is1.getPotSize()));
        if (s.isTerminal()) {
            System.out.println(String.format("Finished: 1(%d), 2(%d)", (int)s.getPayoff(1), (int)s.getPayoff(2)));
        }
    }
}
