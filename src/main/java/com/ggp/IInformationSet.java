package com.ggp;

import java.util.List;

/**
 * Implementing class must be immutable and must suitably override hashCode and equals methods.
 */
public interface IInformationSet {
    /**
     * Generate information set after taking an action.
     * @param a action
     * @return new IS or null if action is not legal.
     */
    IInformationSet next(IAction a);

    /**
     * Generate information set after receiving a percept.
     * @param p percept
     * @return new IS, must return null if given percept cannot occur in this IS
     */
    IInformationSet applyPercept(IPercept p);

    /**
     * Get list of legal actions.
     * @return list or null if there are no legal actions (IS is terminal).
     */
    List<IAction> getLegalActions();

    /**
     * Check whether given action is legal in this IS.
     * @param a
     * @return
     */
    boolean isLegal(IAction a);

    /**
     * Check whether player can receive given percept in this IS.
     * @param p
     * @return
     */
    boolean isValid(IPercept p);

    /**
     * Get ID of player owning this IS
     * @return
     */
    int getOwnerId();
}
