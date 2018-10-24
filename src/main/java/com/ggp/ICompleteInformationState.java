package com.ggp;

import java.io.Serializable;
import java.util.List;

/**
 * Implementing class must be immutable and must suitably override hashCode and equals methods.
 */
public interface ICompleteInformationState extends Serializable {
    /**
     * Checks whether state is terminal
     * @return
     */
    boolean isTerminal();

    /**
     * @return 1,2 or 0 for random node
     */
    int getActingPlayerId();

    /**
     * Get payoff, only valid in terminal node
     * @param player
     * @return payoff
     */
    double getPayoff(int player);

    /**
     * Get legal actions
     * @return list of legal actions, must not be null or empty in non-terminal nodes
     */
    List<IAction> getLegalActions();

    /**
     * Get information set for given player at this state.
     *
     * For non-acting player this corresponds to IS after taking his last action and receiving all percepts prior to this state.
     * @param player
     * @return information set
     */
    IInformationSet getInfoSetForPlayer(int player);

    /**
     * Get CIS resulting from taking an action
     * @param a action
     * @return next CIS or null if action is not valid, or current state is terminal
     */
    ICompleteInformationState next(IAction a);

    /**
     * Get percepts resulting from taking given action at this state
     * @param a
     * @return percepts or null if action is not valid, or state is terminal
     */
    Iterable<IPercept> getPercepts(IAction a);

    /**
     * Get random node
     * @return random node or null if this node is not random
     */
    IRandomNode getRandomNode();

    /**
     * Checks whether given action is legal
     * @param a
     * @return
     */
    default boolean isLegal(IAction a) {
        if (isTerminal() || a == null) return false;
        if (isRandomNode()) return getLegalActions().contains(a);
        return getInfoSetForActingPlayer().isLegal(a);
    }

    default IInformationSet getInfoSetForActingPlayer() {
        return getInfoSetForPlayer(getActingPlayerId());
    }

    default boolean isRandomNode() {
        return getActingPlayerId() == 0;
    }

}
