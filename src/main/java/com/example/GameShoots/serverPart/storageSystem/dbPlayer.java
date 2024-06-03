package com.example.GameShoots.serverPart.storageSystem;

import jakarta.persistence.*;


@Entity // Указывает, что этот класс является сущностью JPA
@Table(name = "Players") // Указывает, что таблица в базе данных называется "Players"
public class dbPlayer {

    @Id // Указывает, что это поле является первичным ключом
    @Column(name = "playerId") // Связывает это поле с колонкой "playerId" в таблице
    @GeneratedValue(strategy = GenerationType.SEQUENCE) // Указывает стратегию генерации значений для первичного ключа
    private int id;

    @Column(name = "playerName") // Связывает это поле с колонкой "playerName" в таблице
    private String nickname;

    @Column(name = "playerRank") // Связывает это поле с колонкой "playerRank" в таблице
    private int rank;

    // Конструктор с параметрами
    public dbPlayer(String nickname, int rank) {
        this.nickname = nickname;
        this.rank = rank;
    }

    // Пустой конструктор (необходим для JPA)
    public dbPlayer() {
    }

    // Геттер для поля id
    public int getId() {
        return id;
    }

    // Геттер для поля nickname
    public String getNickname() {
        return nickname;
    }

    // Геттер для поля rank
    public int getRank() {
        return rank;
    }

    // Сеттер для поля rank
    public void setRank(int rank) {
        this.rank = rank;
    }
}
