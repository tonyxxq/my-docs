package com.ggp.players.deepstack;

import com.ggp.IGameListener;
import com.ggp.IPlayerFactory;
import com.ggp.cli.IPlayerFactoryCommand;
import com.ggp.players.deepstack.debug.ResolvingListener;
import com.ggp.players.deepstack.resolvers.CFRResolver;
import com.ggp.players.deepstack.resolvers.MCCFRResolver;
import picocli.CommandLine;

@CommandLine.Command(
        name = "deepstack",
        description = "Deepstack player"
)
public class DeepstackPlayerCommand implements IPlayerFactoryCommand {
    private ResolvingListener listener = new ResolvingListener();

    @Override
    public IPlayerFactory getPlayerFactory() {
        return new DeepstackPlayer.Factory(new CFRResolver.Factory(null, 2), listener);
        //return new DeepstackPlayer.Factory(new MCCFRResolver.Factory(), listener);
    }

    @Override
    public IGameListener getGameListener() {
        return listener;
    }
}
