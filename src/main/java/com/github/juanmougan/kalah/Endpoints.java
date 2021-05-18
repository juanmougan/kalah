package com.github.juanmougan.kalah;

public class Endpoints {
  public static final String GAMES = "/games";
  public static final String GAME_BY_ID = GAMES + "/{id}";
  public static final String GAME_PATCH = GAME_BY_ID + "/players/{player}";
}
