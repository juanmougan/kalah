package com.github.juanmougan.kalah;

public enum PlayerType {
  SOUTH {
    @Override
    public Status getWinner() {
      return Status.SOUTH_WINS;
    }
  }, NORTH {
    @Override
    public Status getWinner() {
      return Status.NORTH_WINS;
    }
  };

  public abstract Status getWinner();
}
