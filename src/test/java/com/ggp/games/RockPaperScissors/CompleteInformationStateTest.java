package com.ggp.games.RockPaperScissors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompleteInformationStateTest {

    int getPayoff(int p1, int p2, int player, int size) {
        InformationSet is1 = new InformationSet(1, new ChooseAction(p1), size);
        InformationSet is2 = new InformationSet(2, new ChooseAction(p2), size);
        CompleteInformationState s = new CompleteInformationState(is1, is2);
        return (int) s.getPayoff(player);
    }

    @Test
    void testPayoffFunction() {
        for (int size = 3; size <= 27; size += 2) {
            for (int i = 1; i <= size; ++i) {
                for (int offset = 1; offset <= (size-1)/2; ++offset) {
                    int j = (i + offset) % size;
                    assertEquals(1, getPayoff(i, j, 1, size));
                    assertEquals(-1, getPayoff(i, j, 2, size));
                    assertEquals(1, getPayoff(j, i, 2, size));
                    assertEquals(-1, getPayoff(j, i, 1, size));
                }
            }
        }
    }
}