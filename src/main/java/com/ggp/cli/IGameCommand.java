package com.ggp.cli;

import com.ggp.IGameDescription;
import com.ggp.IStateVisualizer;

public interface IGameCommand {
    IGameDescription getGameDescription();
    IStateVisualizer getStateVisualizer();
}
