package com.ggp.players.deepstack.utils;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IPercept;
import com.ggp.utils.PlayerHelpers;

import java.util.Collections;
import java.util.HashMap;

public class GameTreeTraversalTracker {
    private int myId;
    private PerceptSequence myPerceptSequence;
    private PerceptSequence opponentPerceptSequence;
    private double rndProb;
    private IAction myTopAction;
    private enum TrackingState {
        TOP, WAIT_MY_TURN, END
    }
    private TrackingState trackingState;
    private TrackingState nextTrackingState;
    private ICompleteInformationState state;
    private HashMap<IAction, NextTurnInfoTree> actionToNtit;
    private NextTurnInfoTree ntit;
    private HashMap<IAction, PerceptSequenceMap> actionToPsMap;
    private PerceptSequenceMap psMap;
    private HashMap<IInformationSet, NextRangeTree> myISToNRT;
    private NextRangeTree nrt;

    public GameTreeTraversalTracker(int myId, PerceptSequence myPerceptSequence,
                                    PerceptSequence opponentPerceptSequence, double rndProb, IAction myTopAction,
                                    TrackingState trackingState, ICompleteInformationState state,
                                    HashMap<IAction, NextTurnInfoTree> actionToNtit, NextTurnInfoTree ntit,
                                    HashMap<IAction, PerceptSequenceMap> actionToPsMap, PerceptSequenceMap psMap,
                                    HashMap<IInformationSet, NextRangeTree> myISToNRT, NextRangeTree nrt
    ) {
        this.myId = myId;
        this.myPerceptSequence = myPerceptSequence;
        this.opponentPerceptSequence = opponentPerceptSequence;
        this.rndProb = rndProb;
        this.myTopAction = myTopAction;
        this.trackingState = trackingState;
        this.state = state;
        this.actionToNtit = actionToNtit;
        this.ntit = ntit;
        this.actionToPsMap = actionToPsMap;
        this.psMap = psMap;
        this.myISToNRT = myISToNRT;
        this.nrt = nrt;

        this.nextTrackingState = trackingState;
        if (trackingState == TrackingState.WAIT_MY_TURN && state.getActingPlayerId() == myId) {
            this.nextTrackingState = TrackingState.END;
        } else if (trackingState == TrackingState.TOP) {
            nextTrackingState = TrackingState.WAIT_MY_TURN;
        }
    }

    public static GameTreeTraversalTracker createForAct(int myId) {
        return new GameTreeTraversalTracker(myId, new PerceptSequence(), new PerceptSequence(), 1d,
                null, TrackingState.TOP, null, new HashMap<>(), null,
                new HashMap<>(),null, new HashMap<>(), null);
    }

    public static GameTreeTraversalTracker createForInit(int myId, ICompleteInformationState state) {
        return new GameTreeTraversalTracker(myId, new PerceptSequence(), new PerceptSequence(), 1d,
                null, TrackingState.WAIT_MY_TURN, state, null, new NextTurnInfoTree(),
                null, new PerceptSequenceMap(), null, new NextRangeTree());
    }

    public GameTreeTraversalTracker visit(ICompleteInformationState s) {
        if (state != null) throw new IllegalStateException("Can't visit a state from non-root tracker!");
        return new GameTreeTraversalTracker(myId, myPerceptSequence, opponentPerceptSequence,
                rndProb, myTopAction, trackingState, s, actionToNtit, ntit, actionToPsMap, psMap, myISToNRT, nrt);
    }

    public GameTreeTraversalTracker next(IAction a) {
        ICompleteInformationState nextState = state.next(a);
        double newRndProb = rndProb;
        int opponentId = PlayerHelpers.getOpponentId(myId);
        if (state.isRandomNode()) {
            newRndProb *= 1d/ state.getLegalActions().size();
        }
        if (trackingState == TrackingState.END) {
            return new GameTreeTraversalTracker(myId, null, null, newRndProb, myTopAction, nextTrackingState, nextState, null, null, null, null, null, null);
        }
        IAction newMyTopAction = myTopAction;
        NextTurnInfoTree newNtit = ntit;
        Iterable<IPercept> percepts = state.getPercepts(a);
        PerceptSequenceMap newPsMap = psMap;
        NextRangeTree newNrt = nrt;
        if (trackingState == TrackingState.TOP) {
            if (state.getActingPlayerId() != myId) throw new IllegalStateException("Tracker can't be in TOP state when I'm not acting player.");
            newMyTopAction = a;
            newNtit = actionToNtit.computeIfAbsent(a, k -> new NextTurnInfoTree());
            newPsMap = actionToPsMap.computeIfAbsent(a, k -> new PerceptSequenceMap());
            newNrt = myISToNRT.computeIfAbsent(state.getInfoSetForPlayer(myId), k -> new NextRangeTree());
        }
        newNtit = newNtit.getOrCreatePath(percepts, myId);
        PerceptSequence myNewPs = PerceptSequence.getNext(myPerceptSequence, percepts, myId);
        PerceptSequence oppNewPs = opponentPerceptSequence;
        if (state.getActingPlayerId() == opponentId) {
            // opponent can see his own action and they may be neccessary to obtain correct public state
            // for example (leduc poker):
            // if I obtain private card J and no other percepts I know that the other player obtained J, Q or K and didn't raise
            // if I considered only real percepts I would map my J to opponents {(J), (Q), (K)}
            // I would then map those to my range which would contain 2 IS for each of J, Q and K which is wrong
            // (one for opponent's raise and another one for opponent's call since opponent doesn't receive PotUpdate for his own action)
            oppNewPs = new PerceptSequence(oppNewPs, Collections.singleton(new OwnActionPercept(opponentId, a)), opponentId);
        }
        oppNewPs = PerceptSequence.getNext(oppNewPs, percepts, opponentId);
        return new GameTreeTraversalTracker(myId, myNewPs, oppNewPs, newRndProb, newMyTopAction, nextTrackingState, nextState, null, newNtit, null, newPsMap, null, newNrt);

    }

    public boolean isMyNextTurnReached() {
        return trackingState == TrackingState.WAIT_MY_TURN && nextTrackingState == TrackingState.END;
    }

    public boolean wasMyNextTurnReached() {
        return trackingState == TrackingState.END;
    }

    public ICompleteInformationState getCurrentState() {
        return state;
    }

    public PerceptSequence getMyPerceptSequence() {
        return myPerceptSequence;
    }

    public PerceptSequence getOpponentPerceptSequence() {
        return opponentPerceptSequence;
    }

    public double getRndProb() {
        return rndProb;
    }

    public IAction getMyTopAction() {
        return myTopAction;
    }

    public HashMap<IAction, NextTurnInfoTree> getActionToNtit() {
        return actionToNtit;
    }

    public NextTurnInfoTree getNtit() {
        return ntit;
    }

    public HashMap<IAction, PerceptSequenceMap> getActionToPsMap() {
        return actionToPsMap;
    }

    public PerceptSequenceMap getPsMap() {
        return psMap;
    }

    public HashMap<IInformationSet, NextRangeTree> getMyISToNRT() {
        return myISToNRT;
    }

    public NextRangeTree getNrt() {
        return nrt;
    }
}
