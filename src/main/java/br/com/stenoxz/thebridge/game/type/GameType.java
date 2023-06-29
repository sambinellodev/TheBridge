package br.com.stenoxz.thebridge.game.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GameType {

    SOLO("Solo"),
    DUO("Dupla");

    @Getter
    private String name;
}
