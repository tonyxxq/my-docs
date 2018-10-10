package com.ggp;

import java.io.Serializable;

public interface ICompleteInformationStateFactory extends Serializable {
    /**
     * Make complete information state give both player's information sets
     * @param player1
     * @param player2
     * @param actingPlayer
     * @return resulting CIS or null if given IS are not compatible
     */
    ICompleteInformationState make(IInformationSet player1, IInformationSet player2, int actingPlayer);
}
