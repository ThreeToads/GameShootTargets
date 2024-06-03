package com.example.archer.serverPart;

import com.example.archer.commonPart.entity.Lobby;
import com.example.archer.commonPart.entity.Player;
import com.example.archer.serverPart.commands.*;
import com.example.archer.serverPart.storageSystem.Players;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.example.archer.commonPart.entity.Messages.*;

public class ServerSocketIOHandler extends Thread {
    private final Socket clientSocket;
    private final Lobby lobby;

    public ServerSocketIOHandler(Socket clientSocket, Lobby room) {
        this.clientSocket = clientSocket;
        this.lobby = room;
    }

    @Override
    public void run() {
        BufferedReader br;
        PrintWriter writer;
        Player associatedPlayer = null;
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Players players = new Players();
        System.out.println("Accepted new connection");
        try {
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String message = "";
            boolean newMes = true;
            while (true) {
                if (newMes) {
                    message = br.readLine();
                }
                newMes = true;
                if (message.equals("leaderboard")) {
                    writer.println(gson.toJson(players.getTopPlayers()));
                }
                else if (message.equals(GameStarted)) {
                    GameStartedCommand.startGame(writer);
                } else if (message.equals(Join)) {
                    Pair<Boolean, Player> p = JoinCommand.join(lobby, writer, br, associatedPlayer, players); // join(writer, br, associatedPlayer);
                    newMes = p.getKey();
                    associatedPlayer = p.getValue();
                } else if (message.equals(Disconnect)) {
                    disconnectPlayer(associatedPlayer);
                    break;
                } else if (message.equals(GetGameState)) {
                    GetGameStateCommand.getGameState(writer, gson);
                } else if (message.equals(GetLobbyState)) {
                    synchronized (lobby) {
                        writer.println(gson.toJson(lobby));
                    }
                } else if (message.equals(Ready)) {
                    ReadyCommand.ready(lobby, writer, associatedPlayer);
                } else if (message.equals(Unready)) {
                    UnreadyCommand.unready(lobby, writer, associatedPlayer);
                } else if (message.equals(Shoot)) {
                    ShootCommand.shoot(writer, associatedPlayer);
                }
            }
            clientSocket.close();
        } catch (IOException | NullPointerException e) {
            disconnectPlayer(associatedPlayer);
        }
    }

    private void disconnectPlayer(Player associatedPlayer) {
        if (associatedPlayer != null) {
            synchronized (Game.gameLock) {
                Game.currentGame = null;
            }
            synchronized (lobby) {
                lobby.removePlayer(associatedPlayer);
                lobby.notify();
            }
            System.out.println(associatedPlayer.getNickname() + " disconnected");
        }
    }
}
