package com.ggp;

public interface IPlayer {
    /**
     * Perform initial setup, can be called only once.
     */
    void init();

    /**
     * Select action in the current state of the game.
     * @return selected action
     */
    IAction act();

    /**
     * Force player to take given action.
     *
     * Player should perform the same strategy computation as with act and just override action sampling at the end.
     * @param a
     */
    void forceAction(IAction a);

    /**
     * Return assigned player ID.
     * @return
     */
    int getRole();

    /**
     * Update your information set using the given percept.
     * @param percept
     */
    void receivePercepts(IPercept percept);
}