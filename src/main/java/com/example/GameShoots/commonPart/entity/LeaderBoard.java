package com.example.GameShoots.commonPart.entity;

import com.google.gson.annotations.Expose;

public record  LeaderBoard(@Expose String playerName, @Expose int rank) {
}
