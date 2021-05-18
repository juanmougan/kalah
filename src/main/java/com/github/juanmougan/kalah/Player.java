package com.github.juanmougan.kalah;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a Player - this WON'T be persisted!
 */
@Data
@Builder
public class Player {

  private String name;
  private PlayerType type;
  // TODO add Pits
  // TODO add Kalah

  public boolean hasLegalMoves() {
    // TODO pits.all.size > 0
    return false;
  }
}
