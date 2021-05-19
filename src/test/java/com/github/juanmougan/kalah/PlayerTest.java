package com.github.juanmougan.kalah;

import static com.github.juanmougan.kalah.PlayerType.SOUTH;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class PlayerTest {

  @Test
  void givenThereAreLegalMoves_whenHasLegalMoves_thenReturnTrue() {
    // GIVEN
    final Player south = Player.builder()
        .name("South")
        .type(SOUTH)
        .build();
    south.setPits(List.of(
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(0).build(),
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(0).build(),
        Pit.builder().owner(south).ownSeeds(1).rivalSeeds(0).build()
    ));
    // WHEN hasLegalMoves THEN return true
    Assertions.assertThat(south.hasLegalMoves()).isTrue();
  }

  @Test
  void givenThereAreNoLegalMoves_whenHasLegalMoves_thenReturnFalse() {
    // GIVEN
    final Player south = Player.builder()
        .type(SOUTH)
        .build();
    south.setPits(List.of(
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(0).build(),
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(0).build(),
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(1).build()
    ));
    // WHEN hasLegalMoves THEN return false
    Assertions.assertThat(south.hasLegalMoves()).isFalse();
  }
}