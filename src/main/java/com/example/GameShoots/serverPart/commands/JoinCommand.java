package com.example.GameShoots.serverPart.commands;

import com.example.GameShoots.commonPart.entity.Lobby;
import com.example.GameShoots.commonPart.entity.Player;
import com.example.GameShoots.serverPart.Game;
import com.example.GameShoots.serverPart.storageSystem.Players;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class JoinCommand {

    // Метод join отвечает за обработку запроса на присоединение игрока к лобби.
    public static Pair<Boolean, Player> join(Lobby lobby, PrintWriter writer, BufferedReader br, Player associatedPlayer, Players players) throws IOException {
        // Синхронизируемся на объекте lobby, чтобы предотвратить конкурентный доступ к лобби
        synchronized (lobby) {
            // Читаем никнейм из входного потока
            String nickname = br.readLine();

            // Синхронизируемся на объекте gameLock, чтобы предотвратить конкурентный доступ к текущей игре
            synchronized (Game.gameLock) {
                // Проверяем, существует ли текущая игра и не завершена ли она
                if (Game.currentGame != null && !Game.currentGame.getState().getIsFinished()) {
                    // Если игра уже начата, отправляем клиенту сообщение "gamestarted"
                    writer.println("gamestarted");
                    return new Pair<>(true, associatedPlayer);
                }
            }

            // Проверяем, не является ли игрок уже залогиненным
            if (associatedPlayer != null) {
                writer.println("alreadyloggedin");
                return new Pair<>(true, associatedPlayer);
            }

            // Проверяем, не превышено ли максимальное число участников в лобби (4 игрока)
            if (lobby.getPlayerList().size() >= 4) {
                writer.println("full");
                return new Pair<>(true, associatedPlayer);
            }

            // Проверяем, существует ли уже игрок с таким никнеймом в лобби
            boolean flag = true;
            for (Player p : lobby.getPlayerList()) {
                if (p.getNickname().equals(nickname)) {
                    writer.println("playerexists");
                    flag = false;
                    break;
                }
            }
            if (!flag) return new Pair<>(false, associatedPlayer);

            // Логируем сообщение о успешной авторизации игрока
            System.out.println(nickname + " logged in");

            // Получаем объект Player по никнейму
            associatedPlayer = players.getPlayer(nickname);

            // Добавляем игрока в лобби
            double y = 105.0;
            lobby.addPlayer(associatedPlayer);

            // Устанавливаем координату Y для всех игроков в лобби
            for (Player p : lobby.getPlayerList()) {
                p.setY(y);
                y += 90.0;
            }
        }
        // Отправляем клиенту сообщение "ok"
        writer.println("ok");
        return new Pair<>(true, associatedPlayer);
    }
}
