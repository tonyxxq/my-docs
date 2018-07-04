package com.ggp.games.LeducPoker;

public enum Rounds {
    Start, PrivateCard, Bet1, PublicCard, Bet2, End;
    public Rounds next() {
        switch (this) {
            case End:
                return End;
            case Bet2:
                return End;
            case PublicCard:
                return Bet2;
            case Bet1:
                return PublicCard;
            case PrivateCard:
                return Bet1;
            case Start:
                return PrivateCard;
        }
        return Start;
    }
}
