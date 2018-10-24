package com.ggp;

public interface IPlayer {
    /**
     * Perform initial setup, can be called only once.
     *
     * @param timeoutMillis maximum time the computation can take in milliseconds
     */
    void init(long timeoutMillis);

    /**
     * Select action in the current state of the game.
     *
     * @param timeoutMillis maximum time the computation can take in milliseconds
     * @return selected action
     */
    IAction act(long timeoutMillis);

    /**
     * Force player to take given action.
     *
     * Player should perform the same strategy computation as with act and just override action sampling at the end.
     * @param a
     * @param timeoutMillis maximum time the computation can take in milliseconds
     */
    void forceAction(IAction a, long timeoutMillis);

    /**
     * Return assigned player ID.
     * @return
     */
    int getRole();

    /**
     * Update your information set using the given percept.
     *
     * No explicit timeout is given here, but the method is only meant to be a quick update and therefore shouldn't do any extra computation.
     * @param percept
     */
    void receivePercepts(IPercept percept);
}