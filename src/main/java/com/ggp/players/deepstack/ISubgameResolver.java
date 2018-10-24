package com.ggp.players.deepstack;

import com.ggp.*;
import com.ggp.players.deepstack.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface ISubgameResolver {
    interface Factory {
        ISubgameResolver create(int myId, IInformationSet hiddenInfo, InformationSetRange myRange, HashMap<IInformationSet, Double> opponentCFV,
                                ICompleteInformationStateFactory cisFactory, ArrayList<IResolvingListener> resolvingListeners);
        String getConfigString();
    }

    class ActResult {
        public IStrategy cumulativeStrategy;
        public Map<IAction, NextTurnInfoTree> actionToNTIT;
        public Map<IAction, PerceptSequenceMap> actionToPsMap;
        public Map<IInformationSet, NextRangeTree> myISToNRT;
        public double opponentCFVNorm;

        public ActResult(IStrategy cumulativeStrategy, Map<IAction, NextTurnInfoTree> actionToNTIT,
                         Map<IAction, PerceptSequenceMap> actionToPsMap, Map<IInformationSet, NextRangeTree> myISToNRT,
                         double opponentCFVNorm) {
            this.cumulativeStrategy = cumulativeStrategy;
            this.actionToNTIT = actionToNTIT;
            this.actionToPsMap = actionToPsMap;
            this.myISToNRT = myISToNRT;
            this.opponentCFVNorm = opponentCFVNorm;
        }
    }

    class InitResult {
        public NextTurnInfoTree ntit;
        public NextRangeTree nrt;
        public PerceptSequenceMap psMap;
        public double opponentCFVNorm;

        public InitResult(NextTurnInfoTree ntit, NextRangeTree nrt, PerceptSequenceMap psMap, double opponentCFVNorm) {
            this.ntit = ntit;
            this.nrt = nrt;
            this.psMap = psMap;
            this.opponentCFVNorm = opponentCFVNorm;
        }
    }

    ActResult act(IterationTimer timeout);
    InitResult init(ICompleteInformationState initialState, IterationTimer timeout);
}
