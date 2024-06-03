package com.example.archer.serverPart.storageSystem;

import jakarta.persistence.*;


@Entity
@Table(name = "Players")
public class dbPlayer {
    @Id
    @Column(name = "playerId")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "playerName")
    private String nickname;

    @Column(name = "playerRank")
    private int rank;

    public dbPlayer(String nickname, int rank) {
        this.nickname = nickname;
        this.rank = rank;
    }

    public dbPlayer() {
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
