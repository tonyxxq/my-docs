package com.ggp;

public interface IPlayerFactory {
    IPlayer create(IGameDescription game, int role);
}
