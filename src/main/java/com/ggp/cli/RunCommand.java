package com.ggp.cli;

import com.ggp.*;
import picocli.CommandLine;

@CommandLine.Command(name = "run",
        mixinStandardHelpOptions = true,
        description = "Runs given game with given players",
        optionListHeading = "%nOptions:%n",
        sortOptions = false
)
public class RunCommand implements Runnable {
    @CommandLine.ParentCommand
    private MainCommand mainCommand;

    @CommandLine.Option(names={"-g", "--game"}, description="game to be played", required=true)
    private String game;

    @CommandLine.Option(names={"--game-args"}, description="additional arguments for selected game")
    private String gameArgs;

    @CommandLine.Option(names={"--player1"}, description="player 1", required=true)
    private String player1;

    @CommandLine.Option(names={"--player1-args"}, description="additional arguments for player 1")
    private String player1Args;

    @CommandLine.Option(names={"--player2"}, description="player 2", required=true)
    private String player2;

    @CommandLine.Option(names={"--player2-args"}, description="additional arguments for player 2")
    private String player2Args;

    private IPlayerFactoryCommand getPlayerFactoryCommand(String player, String args) {
        IPlayerFactoryCommand plCmd = mainCommand.getPlayerFactoryRegistry().getCommand(player);
        if (plCmd == null) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Unknown player '" + player + "'.", null, player);
        }
        CommandLine.populateCommand(plCmd, CliHelper.splitArgString(args));
        return plCmd;
    }

    private IPlayerFactory getPlayerFactory(IPlayerFactoryCommand plCmd, String player) {
        IPlayerFactory pl =  plCmd.getPlayerFactory();
        if (pl == null) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Failed to setup player '" + player + "'.", null, player);
        }
        return pl;
    }

    @Override
    public void run() {
        IGameCommand gameCommand = mainCommand.getGameRegistry().getCommand(game);
        if (gameCommand == null) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Unknown game '" + game + "'.", null, game);
        }
        CommandLine.populateCommand(gameCommand, CliHelper.splitArgString(gameArgs));
        IGameDescription gameDesc = gameCommand.getGameDescription();
        if (gameDesc == null) {
            throw new CommandLine.ParameterException(new CommandLine(this), "Failed to setup game '" + game + "'.", null, game);
        }
        IStateVisualizer visualizer = gameCommand.getStateVisualizer();
        IPlayerFactoryCommand plCmd1 = getPlayerFactoryCommand(player1, player1Args);
        IPlayerFactoryCommand plCmd2 = getPlayerFactoryCommand(player2, player2Args);

        IPlayerFactory pl1 = getPlayerFactory(plCmd1, player1);
        IPlayerFactory pl2 = getPlayerFactory(plCmd2, player2);

        GameManager manager = new GameManager(pl1, pl2, gameDesc);
        if (visualizer != null) {
            manager.registerGameListener(new GamePlayVisualizer(visualizer));
        }
        manager.registerGameListener(plCmd1.getGameListener());
        manager.registerGameListener(plCmd2.getGameListener());

        manager.run(1000, 1000);
        System.out.println("Result 1:" + manager.getPayoff(1) + ", 2:" + manager.getPayoff(2));

    }
}
