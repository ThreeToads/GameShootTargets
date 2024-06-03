package com.example.archer.serverPart;

import com.example.archer.commonPart.entity.Lobby;
import com.example.archer.commonPart.entity.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private final AtomicBoolean running = new AtomicBoolean(false);

    public void startServer(int port) {
        if (running.getAndSet(true)) {
            System.out.println("Can't start second server in same instance");
            return;
        }

        Lobby lobby = new Lobby();
        Thread gameloopThread = new Thread(() -> {
            System.out.println("Started gameloop thread");
            try {
                while (true) {
                    synchronized (lobby) {
                        while (!lobby.isReady()) {
                            lobby.wait();
                        }
                    }
                    System.out.println("Starting new game");
                    synchronized (lobby) {
                        synchronized (Game.gameLock) {
                            for (Player player : lobby.getPlayerList()) {
                                player.reset();
                            }
                        }
                        Game.currentGame = new Game(640, 380, lobby.getPlayerList().toArray(new Player[0]));
                    }
                    while (true) {
                        synchronized (Game.gameLock) {
                            if (Game.currentGame == null) {
                                System.out.println("Game was destroyed");
                                break;
                            }

                            Game.currentGame.step();
                            if (Game.currentGame.getState().getIsFinished()) {
                                break;
                            }
                        }
                        synchronized (lobby) {
                            while (!lobby.isReady()) {
                                synchronized (Game.gameLock) {
                                    Game.currentGame.getState().setIsPaused(true);
                                }
                                lobby.wait();
                                synchronized (Game.gameLock) {
                                    if (Game.currentGame == null) {
                                        break;
                                    }
                                }
                            }
                            synchronized (Game.gameLock) {
                                if (Game.currentGame != null) {
                                    Game.currentGame.getState().setIsPaused(false);
                                }
                            }
                        }
                        Thread.sleep(10);
                    }
                    synchronized (lobby) {
                        for (Player p : lobby.getPlayerList()) {
                            p.setReady(false);
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        gameloopThread.start();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Started server on port " + port);
            while (running.get()) {
                new ServerSocketIOHandler(serverSocket.accept(), lobby).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameloopThread.interrupt();
    }
}
