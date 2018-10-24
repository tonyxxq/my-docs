package com.ggp;

import java.io.Serializable;

public interface IPlayerFactory extends Serializable {
    IPlayer create(IGameDescription game, int role);
}
