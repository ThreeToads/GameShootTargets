package com.example.archer.clientPart.entity;

import com.example.archer.commonPart.entity.GameState;
import com.example.archer.commonPart.entity.LeaderBoard;
import com.example.archer.commonPart.entity.Lobby;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.example.archer.commonPart.entity.Messages.*;

public class Game {
    Socket socket;
    InputStream socketInputStream;
    OutputStream socketOutputStream;
    BufferedReader socketReader;
    PrintWriter socketWriter;

    public Boolean getConnected() {
        return connected;
    }

    public String getNickname() {
        return nickname;
    }

    public Boolean getGameStarted() {
        return gameStarted;
    }

    private Boolean connected = false;
    private String nickname;

    private GameState state;
    private Boolean gameStarted = false;

    private Lobby lobby;

    public void connect() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), 4000);
            socketInputStream = socket.getInputStream();
            socketOutputStream = socket.getOutputStream();
            socketReader = new BufferedReader(new InputStreamReader(socketInputStream));
            socketWriter = new PrintWriter(socketOutputStream, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Boolean join(String nickname) {
        this.nickname = nickname;
        System.out.println("joining");
        socketWriter.println(Join);
        socketWriter.println(nickname);
        System.out.println("joined player");
        String connectResult;
        try {
            connectResult = socketReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Received status " + connectResult);

        switch (connectResult) {
            case "ok":
                connected = true;
                return true;
            case "full":
            case "playerexists":
            default:
                return false;
        }
    }
    public void disconnect() {
        if(connected) {
            try {
                socketWriter.println(Disconnect);
                socketReader.readLine();
                socketReader = null;
                socketWriter = null;
                socket.close();
                connected = false;
                gameStarted = false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void updateState() {
        Gson gson = new Gson();
        socketWriter.println(GetGameState);
        try {
            String response = socketReader.readLine();
            if("nogame".equals(response)) {
                state = null;
                return;
            }
            state = gson.fromJson(response, GameState.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void clearState() {
        state = null;
    }
    public void updateRoom() {
        Gson gson = new Gson();
        socketWriter.println(GetLobbyState);
        try {
            lobby = gson.fromJson(socketReader.readLine(), Lobby.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public GameState getState() {
        return state;
    }
    public Lobby getLobby() {
        return lobby;
    }
    public void sendReady() {
        socketWriter.println(Ready);
        try {
            socketReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendNotReady() {
        socketWriter.println(Unready);
        try {
            socketReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateGameStartedStatus() {
        socketWriter.println(GameStarted);
        String message;
        try {
            message = socketReader.readLine();
            gameStarted = message.equals("true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void shoot() {
        if(!gameStarted) {
            return;
        }
        socketWriter.println(Shoot);
        String message;
        try {
            socketReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<LeaderBoard> getLeaderboard() {
        socketWriter.println("leaderboard");
        try {
            String message = socketReader.readLine();
            Type leaderboardType = new TypeToken<ArrayList<LeaderBoard>>() {}.getType();
            return new Gson().fromJson(message, leaderboardType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
