package com.example.GameShoots.serverPart.commands;

import com.example.GameShoots.serverPart.Game;

import java.io.PrintWriter;

public class GameStartedCommand {
    public static void startGame(PrintWriter writer) {
        synchronized (Game.gameLock) {
            writer.println(!(Game.currentGame == null || Game.currentGame.getState().getIsFinished()));
        }
    }
}
