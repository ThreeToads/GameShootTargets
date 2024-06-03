package com.example.GameShoots.serverPart.commands;

import com.example.GameShoots.commonPart.entity.Lobby;
import com.example.GameShoots.commonPart.entity.Player;

import java.io.PrintWriter;

public class UnreadyCommand {
    public static void unready(Lobby lobby, PrintWriter writer, Player associatedPlayer) {
        if (associatedPlayer == null) {
            writer.println("noplayer");
            return;
        }
        synchronized (lobby) {
            associatedPlayer.setReady(false);
            writer.println("ok");
            System.out.println(associatedPlayer.getNickname() + " is not ready");
        }
    }
}
