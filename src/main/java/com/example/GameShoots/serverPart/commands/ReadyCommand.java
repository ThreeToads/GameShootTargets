package com.example.GameShoots.serverPart.commands;

import com.example.GameShoots.commonPart.entity.Lobby;
import com.example.GameShoots.commonPart.entity.Player;

import java.io.PrintWriter;

public class ReadyCommand {
    public static void ready(Lobby lobby, PrintWriter writer, Player associatedPlayer) {
        if (associatedPlayer == null) {
            writer.println("noplayer");
            return;
        }
        synchronized (lobby) {
            associatedPlayer.setReady(true);
            System.out.println(associatedPlayer.getNickname() + "is ready");
            lobby.notify();
        }
        writer.println("ok");
    }
}
