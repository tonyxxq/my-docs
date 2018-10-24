package com.ggp.games.LeducPoker.actions;

import com.ggp.IAction;
import com.ggp.games.LeducPoker.Cards;

import java.util.Objects;

public class DealCardAction implements IAction {
    private static final long serialVersionUID = 1L;
    private final Cards card;
    private final int player; // 0 -> public

    public DealCardAction(Cards card) {
        this.card = card;
        this.player = 0;
    }

    public DealCardAction(Cards card, int player) {
        this.card = card;
        this.player = player;
    }

    public Cards getCard() {
        return card;
    }

    public int getPlayer() {
        return player;
    }

    public boolean isPublic() {
        return player == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DealCardAction that = (DealCardAction) o;
        return player == that.player &&
                card == that.card;
    }

    @Override
    public int hashCode() {
        return Objects.hash(card, player);
    }

    @Override
    public String toString() {
        if (isPublic()) return "DealCardAction{" + card + "}";
        return "DealCardAction{" + card + " -> " + player + "}";
    }
}
