package com.ggp.players.deepstack.trackers;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;
import com.ggp.IInformationSet;
import com.ggp.IPercept;
import com.ggp.players.deepstack.utils.NextRangeTree;
import com.ggp.players.deepstack.utils.NextTurnInfoTree;
import com.ggp.players.deepstack.utils.PerceptSequence;
import com.ggp.players.deepstack.utils.PerceptSequenceMap;
import com.ggp.utils.OwnActionPercept;
import com.ggp.utils.PlayerHelpers;

import java.util.Collections;
import java.util.HashMap;

public class CFRDTracker implements IGameTraversalTracker {
    private int myId;
    private PerceptSequence myPerceptSequence;
    private PerceptSequence opponentPerceptSequence;
    private double rndProb;
    private IAction myTopAction;
    private enum TrackingState {
        ROOT, MY_FIRST_TURN, WAIT_MY_TURN, END
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

    public CFRDTracker(int myId, PerceptSequence myPerceptSequence,
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
        } else if (trackingState == TrackingState.MY_FIRST_TURN) {
            nextTrackingState = TrackingState.WAIT_MY_TURN;
        } else if (trackingState == TrackingState.ROOT && state.getActingPlayerId() == PlayerHelpers.getOpponentId(myId)) {
            nextTrackingState = TrackingState.MY_FIRST_TURN;
        }
    }

    public static CFRDTracker createForAct(int myId, double rangeNorm, ICompleteInformationState cfrdRoot) {
        return new CFRDTracker(myId, new PerceptSequence(), new PerceptSequence(), rangeNorm,
                null, TrackingState.ROOT, cfrdRoot, new HashMap<>(), null,
                new HashMap<>(),null, new HashMap<>(), null);
    }

    public static CFRDTracker createForInit(int myId, ICompleteInformationState state) {
        return new CFRDTracker(myId, new PerceptSequence(), new PerceptSequence(), 1d,
                null, TrackingState.WAIT_MY_TURN, state, null, new NextTurnInfoTree(),
                null, new PerceptSequenceMap(), null, new NextRangeTree());
    }

    public CFRDTracker visit(ICompleteInformationState s) {
        if (state != null) throw new IllegalStateException("Can't visit a state from non-root tracker!");
        return new CFRDTracker(myId, myPerceptSequence, opponentPerceptSequence,
                rndProb, myTopAction, trackingState, s, actionToNtit, ntit, actionToPsMap, psMap, myISToNRT, nrt);
    }

    public CFRDTracker visitRandom(ICompleteInformationState s, double visitProb) {
        if (state != null) throw new IllegalStateException("Can't visit a state from non-root tracker!");
        return new CFRDTracker(myId, myPerceptSequence, opponentPerceptSequence,
                rndProb * visitProb, myTopAction, trackingState, s, actionToNtit, ntit, actionToPsMap, psMap, myISToNRT, nrt);
    }

    @Override
    public CFRDTracker next(IAction a) {
        ICompleteInformationState nextState = state.next(a);
        double newRndProb = rndProb;
        int opponentId = PlayerHelpers.getOpponentId(myId);
        if (state.isRandomNode()) {
            newRndProb *= state.getRandomNode().getActionProb(a);
        }
        if (trackingState == TrackingState.END) {
            return new CFRDTracker(myId, null, null, newRndProb, myTopAction, nextTrackingState, nextState, null, null, null, null, null, null);
        }
        IAction newMyTopAction = myTopAction;
        NextTurnInfoTree newNtit = ntit;
        Iterable<IPercept> percepts = state.getPercepts(a);
        PerceptSequenceMap newPsMap = psMap;
        NextRangeTree newNrt = nrt;
        PerceptSequence myNewPs = myPerceptSequence;
        PerceptSequence oppNewPs = opponentPerceptSequence;
        if (trackingState == TrackingState.MY_FIRST_TURN) {
            if (state.getActingPlayerId() != myId) throw new IllegalStateException("Tracker can't be in MY_FIRST_TURN state when I'm not acting player.");
            newMyTopAction = a;
            newNtit = actionToNtit.computeIfAbsent(a, k -> new NextTurnInfoTree());
            newPsMap = actionToPsMap.computeIfAbsent(a, k -> new PerceptSequenceMap());
            newNrt = myISToNRT.computeIfAbsent(state.getInfoSetForPlayer(myId), k -> new NextRangeTree());
        }
        if (trackingState != TrackingState.ROOT) {
            newNtit = newNtit.getOrCreatePath(percepts, myId);
            myNewPs = PerceptSequence.getNext(myPerceptSequence, percepts, myId);
            oppNewPs = opponentPerceptSequence;
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
        }

        return new CFRDTracker(myId, myNewPs, oppNewPs, newRndProb, newMyTopAction, nextTrackingState, nextState, actionToNtit, newNtit, actionToPsMap, newPsMap, myISToNRT, newNrt);

    }

    public boolean isMyNextTurnReached() {
        return trackingState == TrackingState.WAIT_MY_TURN && nextTrackingState == TrackingState.END;
    }

    public boolean wasMyNextTurnReached() {
        return trackingState == TrackingState.END;
    }

    @Override
    public ICompleteInformationState getCurrentState() {
        return state;
    }

    public PerceptSequence getMyPerceptSequence() {
        return myPerceptSequence;
    }

    public PerceptSequence getOpponentPerceptSequence() {
        return opponentPerceptSequence;
    }

    @Override
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
