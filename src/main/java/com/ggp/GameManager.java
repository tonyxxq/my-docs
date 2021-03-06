package com.ggp;

import com.ggp.utils.PlayerHelpers;
import com.ggp.utils.random.RandomSampler;

import java.util.ArrayList;
import java.util.Iterator;

public class GameManager {
    private IPlayer player1;
    private IPlayer player2;
    private ICompleteInformationState state;
    private RandomSampler randomActionSelector = new RandomSampler();
    private ArrayList<IGameListener> gameListeners = new ArrayList<>();

    public GameManager(IPlayerFactory playerFactory1, IPlayerFactory playerFactory2, IGameDescription game) {
        this.player1 = playerFactory1.create(game, 1);
        this.player2 = playerFactory2.create(game, 2);
        this.state = game.getInitialState();
    }

    public void run(long initTimeoutMillis, long actTimeoutMillis) {
        run(initTimeoutMillis, actTimeoutMillis, null);
    }

    public void run(long initTimeoutMillis, long actTimeoutMillis, Iterator<IAction> forcedActions) {
        gameListeners.forEach((listener) -> listener.gameStart());
        player1.init(initTimeoutMillis);
        player2.init(initTimeoutMillis);

        while(!playOneTurn(actTimeoutMillis, forcedActions)) {}
        gameListeners.forEach((listener) -> listener.gameEnd(getPayoff(1), getPayoff(2)));
    }

    private boolean playOneTurn(long actTimeoutMillis, Iterator<IAction> currentForcedAction) {
        if (player1 == null || player2 == null) return true;
        gameListeners.forEach((listener) -> listener.stateReached(state));
        if (state.isTerminal()) return true;
        IAction a;
        int turn = state.getActingPlayerId();
        if (currentForcedAction == null) {
            if (state.isRandomNode()) {
                IRandomNode rndNode = state.getRandomNode();
                a = randomActionSelector.select(state.getLegalActions(), action -> rndNode.getActionProb(action)).getResult();
            } else {
                a = PlayerHelpers.callWithSelectedParam(turn, player1, player2, p -> p.act(actTimeoutMillis));
                if (!state.isLegal(a)) {
                    throw new IllegalStateException(String.format("Player %d chose illegal action %s", turn, a));
                }
            }
        } else {
            a = currentForcedAction.next();
            if (!state.isLegal(a)) {
                throw new IllegalStateException(String.format("Illegal forced action %s", a));
            }
            if (!state.isRandomNode()) {
                PlayerHelpers.callWithSelectedParamVoid(turn, player1, player2, p -> p.forceAction(a, actTimeoutMillis));
            }
        }

        gameListeners.forEach((listener) -> listener.actionSelected(state, a));
        Iterable<IPercept> percepts = state.getPercepts(a);
        state = state.next(a);
        for (IPercept p: percepts) {
            PlayerHelpers.callWithSelectedParamVoid(p.getTargetPlayer(), player1, player2, player -> player.receivePercepts(p));
        }
        return false;
    }

    public int getPayoff(int role) {
        return (int) state.getPayoff(role);
    }

    public void registerGameListener(IGameListener listener) {
        if (listener != null) gameListeners.add(listener);
    }
}
