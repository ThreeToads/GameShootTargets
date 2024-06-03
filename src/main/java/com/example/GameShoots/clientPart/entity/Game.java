package com.example.GameShoots.clientPart.entity;

import com.example.GameShoots.commonPart.entity.GameState;
import com.example.GameShoots.commonPart.entity.LeaderBoard;
import com.example.GameShoots.commonPart.entity.Lobby;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.example.GameShoots.commonPart.entity.Messages.*;

public class Game {
    Socket socket; // Сокет для подключения к серверу
    InputStream socketInputStream; // Входящий поток для сокета
    OutputStream socketOutputStream; // Исходящий поток для сокета
    BufferedReader socketReader; // Буферизованный ридер для чтения данных из сокета
    PrintWriter socketWriter; // Писатель для отправки данных через сокет

    public Boolean getConnected() {
        return connected;
    }

    public String getNickname() {
        return nickname;
    }

    public Boolean getGameStarted() {
        return gameStarted;
    }

    private Boolean connected = false; // Флаг, указывающий подключен ли клиент к серверу
    private String nickname; // Никнейм игрока

    private GameState state; // Состояние игры
    private Boolean gameStarted = false; // Флаг, указывающий началась ли игра

    private Lobby lobby; // Лобби игры

    // Метод для подключения к серверу
    public void connect() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), 4000); // Подключение к серверу на локальном хосте и порту 4000
            socketInputStream = socket.getInputStream(); // Получение входящего потока сокета
            socketOutputStream = socket.getOutputStream(); // Получение исходящего потока сокета
            socketReader = new BufferedReader(new InputStreamReader(socketInputStream)); // Инициализация буферизованного ридера
            socketWriter = new PrintWriter(socketOutputStream, true); // Инициализация писателя с автоматическим сбросом буфера
        } catch (IOException e) {
            throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки подключения
        }
    }

    // Метод для присоединения к игре с указанным никнеймом
    public Boolean join(String nickname) {
        this.nickname = nickname; // Сохранение никнейма
        System.out.println("joining"); // Вывод сообщения о начале присоединения
        socketWriter.println(Join); // Отправка команды присоединения на сервер
        socketWriter.println(nickname); // Отправка никнейма на сервер
        System.out.println("joined player"); // Вывод сообщения о завершении отправки данных
        String connectResult;
        try {
            connectResult = socketReader.readLine(); // Чтение ответа от сервера
        } catch (IOException e) {
            throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки чтения
        }
        System.out.println("Received status " + connectResult); // Вывод полученного статуса

        // Обработка ответа от сервера
        switch (connectResult) {
            case "ok":
                connected = true; // Установка флага подключения в true
                return true;
            case "full":
            case "playerexists":
            default:
                return false; // В остальных случаях возвращается false
        }
    }

    // Метод для отключения от сервера
    public void disconnect() {
        if(connected) { // Проверка, подключен ли клиент
            try {
                socketWriter.println(Disconnect); // Отправка команды отключения на сервер
                socketReader.readLine(); // Чтение ответа от сервера
                socketReader = null; // Обнуление ридера
                socketWriter = null; // Обнуление писателя
                socket.close(); // Закрытие сокета
                connected = false; // Установка флага подключения в false
                gameStarted = false; // Установка флага начала игры в false
            } catch (IOException e) {
                throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки отключения
            }
        }
    }

    // Метод для обновления состояния игры
    public void updateState() {
        Gson gson = new Gson(); // Создание объекта Gson для работы с JSON
        socketWriter.println(GetGameState); // Отправка команды получения состояния игры на сервер
        try {
            String response = socketReader.readLine(); // Чтение ответа от сервера
            if("nogame".equals(response)) { // Проверка, не пришел ли ответ "nogame"
                state = null; // Обнуление состояния игры
                return;
            }
            state = gson.fromJson(response, GameState.class); // Парсинг JSON-ответа в объект GameState
        } catch (IOException e) {
            throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки чтения
        }
    }

    // Метод для очистки состояния игры
    public void clearState() {
        state = null; // Обнуление состояния игры
    }

    // Метод для обновления состояния лобби
    public void updateRoom() {
        Gson gson = new Gson(); // Создание объекта Gson для работы с JSON
        socketWriter.println(GetLobbyState); // Отправка команды получения состояния лобби на сервер
        try {
            lobby = gson.fromJson(socketReader.readLine(), Lobby.class); // Парсинг JSON-ответа в объект Lobby
        } catch (IOException e) {
            throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки чтения
        }
    }

    // Метод для получения состояния игры
    public GameState getState() {
        return state;
    }

    // Метод для получения лобби игры
    public Lobby getLobby() {
        return lobby;
    }

    // Метод для отправки готовности игрока
    public void sendReady() {
        socketWriter.println(Ready); // Отправка команды готовности на сервер
        try {
            socketReader.readLine(); // Чтение ответа от сервера
        } catch (IOException e) {
            throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки чтения
        }
    }

    // Метод для отправки команды "не готов"
    public void sendNotReady() {
        socketWriter.println(Unready); // Отправка команды "не готов" на сервер
        try {
            socketReader.readLine(); // Чтение ответа от сервера
        } catch (IOException e) {
            throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки чтения
        }
    }

    // Метод для обновления статуса начала игры
    public void updateGameStartedStatus() {
        socketWriter.println(GameStarted); // Отправка команды проверки начала игры на сервер
        String message;
        try {
            message = socketReader.readLine(); // Чтение ответа от сервера
            gameStarted = message.equals("true"); // Установка флага начала игры в зависимости от ответа
        } catch (IOException e) {
            throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки чтения
        }
    }

    // Метод для отправки команды стрельбы
    public void shoot() {
        if(!gameStarted) { // Проверка, началась ли игра
            return;
        }
        socketWriter.println(Shoot); // Отправка команды стрельбы на сервер
        try {
            socketReader.readLine(); // Чтение ответа от сервера
        } catch (IOException e) {
            throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки чтения
        }
    }

    // Метод для получения таблицы лидеров
    public List<LeaderBoard> getLeaderboard() {
        socketWriter.println("leaderboard"); // Отправка команды получения таблицы лидеров на сервер
        try {
            String message = socketReader.readLine(); // Чтение ответа от сервера
            Type leaderboardType = new TypeToken<ArrayList<LeaderBoard>>() {}.getType(); // Определение типа для парсинга JSON
            return new Gson().fromJson(message, leaderboardType); // Парсинг JSON-ответа в список объектов LeaderBoard
        } catch (IOException e) {
            throw new RuntimeException(e); // Выбрасывание исключения в случае ошибки чтения
        }
    }
}

