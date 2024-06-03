package com.example.GameShoots.serverPart;

import com.example.GameShoots.commonPart.entity.Lobby;
import com.example.GameShoots.commonPart.entity.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private final AtomicBoolean running = new AtomicBoolean(false); // Переменная для контроля состояния работы сервера

    public void startServer(int port) {
        if (running.getAndSet(true)) { // Проверка, запущен ли сервер уже, и установка состояния в "запущен"
            System.out.println("Can't start second server in same instance");
            return; // Если сервер уже запущен, выводится сообщение и метод завершает выполнение
        }

        Lobby lobby = new Lobby(); // Создание лобби для игры
        Thread gameloopThread = new Thread(() -> { // Создание потока для игрового цикла
            System.out.println("Started gameloop thread"); // Сообщение о запуске потока игрового цикла
            try {
                while (true) { // Бесконечный цикл игрового потока
                    synchronized (lobby) {
                        while (!lobby.isReady()) { // Ожидание готовности лобби
                            lobby.wait(); // Поток ожидает, пока лобби не будет готово
                        }
                    }
                    System.out.println("Starting new game"); // Сообщение о начале новой игры
                    synchronized (lobby) {
                        synchronized (Game.gameLock) {
                            for (Player player : lobby.getPlayerList()) { // Сброс состояния всех игроков в лобби
                                player.reset();
                            }
                        }
                        Game.currentGame = new Game(640, 380, lobby.getPlayerList().toArray(new Player[0])); // Создание новой игры с параметрами
                    }
                    while (true) { // Цикл выполнения игры
                        synchronized (Game.gameLock) {
                            if (Game.currentGame == null) { // Проверка, не завершилась ли игра
                                System.out.println("Game was destroyed");
                                break; // Если игра завершена, выход из цикла
                            }

                            Game.currentGame.step(); // Выполнение шага игры
                            if (Game.currentGame.getState().getIsFinished()) { // Проверка, не завершена ли игра
                                break; // Если игра завершена, выход из цикла
                            }
                        }
                        synchronized (lobby) {
                            while (!lobby.isReady()) { // Ожидание, пока все игроки будут готовы
                                synchronized (Game.gameLock) {
                                    Game.currentGame.getState().setIsPaused(true); // Приостановка игры
                                }
                                lobby.wait(); // Ожидание готовности лобби
                                synchronized (Game.gameLock) {
                                    if (Game.currentGame == null) { // Проверка, не завершилась ли игра
                                        break;
                                    }
                                }
                            }
                            synchronized (Game.gameLock) {
                                if (Game.currentGame != null) {
                                    Game.currentGame.getState().setIsPaused(false); // Продолжение игры
                                }
                            }
                        }
                        Thread.sleep(10); // Задержка на 10 миллисекунд перед следующим шагом
                    }
                    synchronized (lobby) {
                        for (Player p : lobby.getPlayerList()) { // Сброс состояния готовности всех игроков
                            p.setReady(false);
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // Обработка исключения
            }
        });
        gameloopThread.start(); // Запуск потока игрового цикла
        try (ServerSocket serverSocket = new ServerSocket(port)) { // Создание серверного сокета
            System.out.println("Started server on port " + port); // Сообщение о запуске сервера
            while (running.get()) { // Пока сервер запущен
                new ServerSocketIOHandler(serverSocket.accept(), lobby).start(); // Ожидание подключения клиентов и запуск их обработчиков
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // Обработка исключения
        }
        gameloopThread.interrupt(); // Прерывание потока игрового цикла при остановке сервера
    }
}

