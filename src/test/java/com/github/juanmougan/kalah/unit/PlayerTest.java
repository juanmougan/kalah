package com.github.juanmougan.kalah.unit;

import static com.github.juanmougan.kalah.PlayerType.SOUTH;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.juanmougan.kalah.Pit;
import com.github.juanmougan.kalah.Player;
import java.util.List;
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
    assertThat(south.hasLegalMoves()).isTrue();
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
    assertThat(south.hasLegalMoves()).isFalse();
  }

  @Test
  public void givenOwnSeeds_whenHasNoSeedsInOwnPits_thenReturnFalse() {
    // GIVEN
    final Player south = Player.builder()
        .type(SOUTH)
        .build();
    south.setPits(List.of(
        Pit.builder().owner(south).ownSeeds(5).rivalSeeds(0).build(),
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(0).build(),
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(6).build()
    ));
    // WHEN hasSeeds THEN return true
    assertThat(south.hasNoSeedsInOwnPits()).isFalse();
  }

  @Test
  public void givenNoOwnSeeds_whenHasNoSeedsInOwnPits_thenReturnTrue() {
    // GIVEN
    final Player south = Player.builder()
        .type(SOUTH)
        .build();
    south.setPits(List.of(
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(3).build(),
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(0).build(),
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(6).build()
    ));
    // WHEN hasSeeds THEN return true
    assertThat(south.hasNoSeedsInOwnPits()).isTrue();
  }

  @Test
  public void givenRivalSeedsOnly_whenHasNoSeedsInOwnPits_thenReturnTrue() {
    // GIVEN
    final Player south = Player.builder()
        .type(SOUTH)
        .build();
    south.setPits(List.of(
        Pit.builder().owner(south).ownSeeds(0).rivalSeeds(5).build()
    ));
    // WHEN hasSeeds THEN return false
    assertThat(south.hasNoSeedsInOwnPits()).isTrue();
  }

  @Test
  public void givenRivalSeedsInOwnPits_whenCountAllRivalSeedsInOwnPits_thenReturnCount() {
    // GIVEN
    final int ownSeeds = 3;
    final int rivalSeeds = 5;
    final Player south = Player.builder()
        .type(SOUTH)
        .build();
    south.setPits(List.of(
        Pit.builder().owner(south).ownSeeds(ownSeeds).rivalSeeds(rivalSeeds).build()
    ));
    // WHEN
    int allRivalSeedsInOwnPits = south.countAllRivalSeedsInOwnPits();
    // THEN
    assertThat(allRivalSeedsInOwnPits).isEqualTo(rivalSeeds);
  }
}