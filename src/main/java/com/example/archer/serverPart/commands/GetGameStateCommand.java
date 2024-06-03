package com.example.archer.serverPart.commands;

import com.example.archer.serverPart.Game;
import com.google.gson.Gson;

import java.io.PrintWriter;

public class GetGameStateCommand {
    public static void getGameState(PrintWriter writer, Gson gson) {
        synchronized (Game.gameLock) {
            if (Game.currentGame == null) {
                writer.println("nogame");
                return;
            }
            writer.println(gson.toJson(Game.currentGame.getState()));
        }
    }
}
