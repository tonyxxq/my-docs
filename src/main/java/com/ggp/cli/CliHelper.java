package com.ggp.cli;

public class CliHelper {
    public static String[] splitArgString(String args) {
        if (args == null || args.isEmpty()) return new String[]{};
        return args.split(" ");
    }
}
