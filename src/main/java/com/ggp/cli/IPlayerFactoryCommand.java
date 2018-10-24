package com.ggp.cli;

import com.ggp.IGameListener;
import com.ggp.IPlayerFactory;

public interface IPlayerFactoryCommand {
    IPlayerFactory getPlayerFactory();
    default IGameListener getGameListener() {
        return null;
    }
}
