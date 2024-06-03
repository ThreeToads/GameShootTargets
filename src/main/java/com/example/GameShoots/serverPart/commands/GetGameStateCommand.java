package com.example.GameShoots.serverPart.commands;

import com.example.GameShoots.serverPart.Game;
import com.google.gson.Gson;

import java.io.PrintWriter;

public class GetGameStateCommand {

    // Метод getGameState отвечает за получение текущего состояния игры и его отправку клиенту.
    public static void getGameState(PrintWriter writer, Gson gson) {
        // Синхронизируемся на объекте gameLock, чтобы предотвратить конкурентный доступ к текущей игре
        synchronized (Game.gameLock) {
            // Проверяем, существует ли текущая игра
            if (Game.currentGame == null) {
                // Если игры нет
                writer.println("nogame");
                return;
            }
            // Если игра существует, преобразуем текущее состояние игры в JSON и отправляем его клиенту
            writer.println(gson.toJson(Game.currentGame.getState()));
        }
    }
}
