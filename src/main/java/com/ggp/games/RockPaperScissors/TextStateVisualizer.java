package com.ggp.games.RockPaperScissors;

import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IStateVisualizer;

public class TextStateVisualizer implements IStateVisualizer {
    @Override
    public void visualize(IInformationSet s, int role) {
        System.out.println(((InformationSet)s).getChosenAction().toString());
    }

    @Override
    public void visualize(ICompleteInformationState s) {
        CompleteInformationState state = (CompleteInformationState) s;
        InformationSet is1 = (InformationSet) state.getInfoSetForPlayer(1),
                is2 = (InformationSet) state.getInfoSetForPlayer(2);
        System.out.println("1: " + is1.getChosenAction() + ", 2:" + is2.getChosenAction());
    }
}
