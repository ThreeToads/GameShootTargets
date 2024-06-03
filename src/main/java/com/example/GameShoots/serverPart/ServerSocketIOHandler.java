package com.example.GameShoots.serverPart;

import com.example.GameShoots.commonPart.entity.Lobby;
import com.example.GameShoots.commonPart.entity.Player;
import com.example.GameShoots.serverPart.commands.*;
import com.example.GameShoots.serverPart.storageSystem.Players;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.example.GameShoots.commonPart.entity.Messages.*;

public class ServerSocketIOHandler extends Thread {
    private final Socket clientSocket; // Сокет для подключения клиента
    private final Lobby lobby; // Лобби игры

    public ServerSocketIOHandler(Socket clientSocket, Lobby room) {
        this.clientSocket = clientSocket; // Инициализация сокета клиента
        this.lobby = room; // Инициализация лобби
    }

    @Override
    public void run() {
        BufferedReader br; // Буферизованный ридер для чтения данных от клиента
        PrintWriter writer; // Писатель для отправки данных клиенту
        Player associatedPlayer = null; // Игрок, связанный с текущим клиентом
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(); // Создание объекта Gson с настройками
        Players players = new Players(); // Объект, содержащий список игроков
        System.out.println("Accepted new connection"); // Вывод сообщения о новом подключении
        try {
            writer = new PrintWriter(clientSocket.getOutputStream(), true); // Инициализация писателя с авто-сбросом буфера
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Инициализация буферизованного ридера
            String message = ""; // Переменная для хранения входящего сообщения
            boolean newMes = true; // Флаг для отслеживания нового сообщения
            while (true) {
                if (newMes) {
                    message = br.readLine(); // Чтение нового сообщения от клиента
                }
                newMes = true;
                if (message.equals("leaderboard")) {
                    writer.println(gson.toJson(players.getTopPlayers())); // Отправка клиенту списка топ игроков в формате JSON
                }
                else if (message.equals(GameStarted)) {
                    GameStartedCommand.startGame(writer); // Запуск игры
                } else if (message.equals(Join)) {
                    Pair<Boolean, Player> p = JoinCommand.join(lobby, writer, br, associatedPlayer, players); // Присоединение к игре
                    newMes = p.getKey(); // Обновление флага нового сообщения
                    associatedPlayer = p.getValue(); // Обновление связанного игрока
                } else if (message.equals(Disconnect)) {
                    disconnectPlayer(associatedPlayer); // Отключение игрока
                    break;
                } else if (message.equals(GetGameState)) {
                    GetGameStateCommand.getGameState(writer, gson); // Получение состояния игры
                } else if (message.equals(GetLobbyState)) {
                    synchronized (lobby) {
                        writer.println(gson.toJson(lobby)); // Отправка состояния лобби клиенту в формате JSON
                    }
                } else if (message.equals(Ready)) {
                    ReadyCommand.ready(lobby, writer, associatedPlayer); // Обработка команды "готов"
                } else if (message.equals(Unready)) {
                    UnreadyCommand.unready(lobby, writer, associatedPlayer); // Обработка команды "не готов"
                } else if (message.equals(Shoot)) {
                    ShootCommand.shoot(writer, associatedPlayer); // Обработка команды "выстрел"
                }
            }
            clientSocket.close(); // Закрытие сокета клиента
        } catch (IOException | NullPointerException e) {
            disconnectPlayer(associatedPlayer); // Отключение игрока в случае ошибки
        }
    }

    private void disconnectPlayer(Player associatedPlayer) {
        if (associatedPlayer != null) {
            synchronized (Game.gameLock) {
                Game.currentGame = null; // Обнуление текущей игры
            }
            synchronized (lobby) {
                lobby.removePlayer(associatedPlayer); // Удаление игрока из лобби
                lobby.notify(); // Уведомление всех потоков, ожидающих этого монитора
            }
            System.out.println(associatedPlayer.getNickname() + " disconnected"); // Вывод сообщения об отключении игрока
        }
    }
}

