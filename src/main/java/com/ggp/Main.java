package com.ggp;

import com.ggp.cli.MainCommand;
import com.ggp.games.LeducPoker.LeducPokerCommand;
import com.ggp.games.RockPaperScissors.RPSCommand;
import com.ggp.games.TicTacToe.TTTCommand;
import com.ggp.players.deepstack.DeepstackPlayerCommand;
import com.ggp.players.random.RandomPlayerCommand;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        MainCommand main = new MainCommand();
        main.registerGameCommand(new RPSCommand());
        main.registerGameCommand(new LeducPokerCommand());
        main.registerGameCommand(new TTTCommand());

        main.registerPlayerFactoryCommand(new RandomPlayerCommand());
        main.registerPlayerFactoryCommand(new DeepstackPlayerCommand());
        CommandLine cli = new CommandLine(main);
        cli.run(main, args);

    }
}
