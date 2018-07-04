package com.ggp.games.LeducPoker.percepts;

import com.ggp.IPercept;
import com.ggp.games.LeducPoker.Cards;

import java.util.Objects;

public class CardRevealedPercept implements IPercept {
    private final int owner;
    private final Cards card;
    private final boolean isPublic;

    public CardRevealedPercept(int owner, Cards card, boolean isPublic) {
        this.owner = owner;
        this.card = card;
        this.isPublic = isPublic;
    }

    @Override
    public int getTargetPlayer() {
        return owner;
    }

    public Cards getCard() {
        return card;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardRevealedPercept that = (CardRevealedPercept) o;
        return owner == that.owner &&
                isPublic == that.isPublic &&
                card == that.card;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), owner, card, isPublic);
    }
}
