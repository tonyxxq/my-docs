package com.ggp.utils;

import com.ggp.IAction;
import com.ggp.ICompleteInformationState;

public class GameUtils {

    public static ICompleteInformationState applyActionSequnce(ICompleteInformationState s, IAction... actions) {
        for (IAction a: actions) {
            s = s.next(a);
        }
        return s;
    }
}
