package com.example.GameShoots.serverPart.storageSystem;

import com.example.GameShoots.commonPart.entity.LeaderBoard;
import com.example.GameShoots.commonPart.entity.Player;
import com.example.GameShoots.serverPart.entity.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class Players {

    // Метод для получения игрока по никнейму
    public Player getPlayer(String nickname) {
        // Создание экземпляров dbLoader и dbSaver для работы с базой данных
        dbLoader dbLoader = new dbLoader();
        dbSaver dbSaver = new dbSaver();

        // Поиск игрока в базе данных по никнейму
        dbPlayer data = dbLoader.findByNickname(nickname);

        // Если игрок не найден, создаем нового игрока с начальным рангом 0 и сохраняем его в базу данных
        if (data == null) {
            data = new dbPlayer(nickname, 0);
            dbSaver.save(data);
        }

        // Возвращаем объект ServerPlayer, связанный с данными игрока
        return new ServerPlayer(data, this);
    }

    // Метод для получения списка топ-игроков
    public List<LeaderBoard> getTopPlayers() {
        // Создание экземпляра dbLoader для работы с базой данных
        dbLoader dbLoader = new dbLoader();

        // Получение списка топ-игроков из базы данных (ограничено 10 игроками)
        List<dbPlayer> data = dbLoader.getTopPlayers(10);

        // Создание списка LeaderBoard для хранения результатов
        List<LeaderBoard> leaderboard = new ArrayList<>();

        // Преобразование данных игроков в объекты LeaderBoard и добавление их в список
        for (final var player : data) {
            leaderboard.add(new LeaderBoard(player.getNickname(), player.getRank()));
        }

        // Возвращение списка топ-игроков
        return leaderboard;
    }

    // Метод для обновления данных игрока в базе данных
    public void updatePlayer(ServerPlayer player) {
        // Создание экземпляра dbSaver для работы с базой данных
        dbSaver dbSaver = new dbSaver();

        // Обновление данных игрока в базе данных
        dbSaver.update(player.getPersistentData());
    }
}

