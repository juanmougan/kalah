package com.github.juanmougan.kalah;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PitTest {

  @Test
  void givenIsRivalEmptyPit_whenPerformAfterTurnAction_thenNoActionAndSwitchTurn() {
    // GIVEN a Pit for the current Player with no seeds
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

    final Pit pit = Pit.builder()
        .id(UUID.randomUUID())
        .index(3)
        .owner(rivalNorth)
        .ownSeeds(0)
        .rivalSeeds(0)
        .build();

    ownerSouth.setPits(List.of(pit));

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .nextPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    pit.performAfterTurnAction(board);

    // THEN do not capture, switch turn
    assertThat(board.getNextPlayer()).isEqualTo(rivalNorth);
  }

  @Test
  void givenIsRivalNonEmptyPit_whenPerformAfterTurnAction_thenNoActionAndSwitchTurn() {
    // GIVEN a Pit for the rival with both theirs and ours seeds
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

    final Pit pit = Pit.builder()
        .id(UUID.randomUUID())
        .index(3)
        .owner(rivalNorth)
        .ownSeeds(5)
        .rivalSeeds(7)
        .build();

    ownerSouth.setPits(List.of(pit));

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .nextPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    pit.performAfterTurnAction(board);

    // THEN do not capture, switch turn
    assertThat(board.getNextPlayer()).isEqualTo(rivalNorth);
  }

  @Test
  void givenIsNonEmptyOwnPit_whenPerformAfterTurnAction_thenNoActionAndSwitchTurn() {
    // GIVEN a Pit for the rival with both theirs and ours seeds
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

    final Pit pit = Pit.builder()
        .id(UUID.randomUUID())
        .index(3)
        .owner(ownerSouth)
        .ownSeeds(5)
        .rivalSeeds(7)
        .build();

    ownerSouth.setPits(List.of(pit));

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .nextPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    pit.performAfterTurnAction(board);

    // THEN do not capture, switch turn
    assertThat(board.getNextPlayer()).isEqualTo(rivalNorth);
  }

  // TODO add own pit empty, and opposite pit epty
  @Test
  void givenIsEmptyOwnPit_whenPerformAfterTurnAction_thenPerformActionAndSwitchTurn() {
    // GIVEN a Pit for the rival with both theirs and ours seeds
    final int NORTH_SEEDS_IN_OPPOSITE_PIT = 5;
    final int SOUTH_SEEDS_IN_OPPOSITE_PIT = 3;
    final int SEEDS_IN_KALAH = 4;
    final int STARTING_PIT_INDEX = 3;
    final int SEEDS_IN_STARTING_PIT = 2;
    final int endingPitIndex = STARTING_PIT_INDEX + SEEDS_IN_STARTING_PIT;
    final int expectedSeedsInKalah = NORTH_SEEDS_IN_OPPOSITE_PIT + SOUTH_SEEDS_IN_OPPOSITE_PIT + SEEDS_IN_KALAH + 1;
    final int oppositePitIndex = Board.getOppositePitIndex(endingPitIndex);
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

    final Pit startingPit = Pit.builder()
        .id(UUID.randomUUID())
        .index(STARTING_PIT_INDEX)
        .owner(ownerSouth)
        .ownSeeds(SEEDS_IN_STARTING_PIT)
        .rivalSeeds(0)
        .build();
    final Pit endingPit = Pit.builder()
        .id(UUID.randomUUID())
        .index(endingPitIndex)
        .owner(ownerSouth)
        .ownSeeds(0)
        .rivalSeeds(0)
        .build();
    final Pit oppositeEndingPit = Pit.builder()
        .id(UUID.randomUUID())
        .index(oppositePitIndex)
        .owner(rivalNorth)
        .ownSeeds(NORTH_SEEDS_IN_OPPOSITE_PIT)
        .rivalSeeds(0)
        .build();

    ownerSouth.setPits(List.of(startingPit, endingPit));
    rivalNorth.setPits(List.of(oppositeEndingPit));

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .nextPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    startingPit.performAfterTurnAction(board);

    // THEN capture and switch turn
    assertThat(board.getNextPlayer()).isEqualTo(rivalNorth);
    // assert startingPit no longer has own seeds
    Pit actualStartingPit = board.getSouth().getPits().stream().filter(p -> p.getIndex() == STARTING_PIT_INDEX)
        .findFirst().orElseThrow();
    // TODO implement this onwards
    assertThat(actualStartingPit.getOwnSeeds()).isZero();
    // assert endingPit has one seed
    Pit actualEndingPit = board.getSouth().getPits().stream().filter(p -> p.getIndex() == endingPitIndex)
        .findFirst().orElseThrow();
    assertThat(actualEndingPit.getOwnSeeds()).isOne();
    assertThat(actualEndingPit.getRivalSeeds()).isZero();
    // assert opposite is empty
    Pit actualOppositePit = board.getNorth().getPits().stream().filter(p -> p.getIndex() == oppositePitIndex)
        .findFirst().orElseThrow();
    assertThat(actualOppositePit.getOwnSeeds()).isZero();
    assertThat(actualOppositePit.getRivalSeeds()).isZero();
    // assert Kalah has sumed all the seeds
    assertThat(board).extracting(Board::getSouth).extracting(Player::getKalah).extracting(Kalah::getSeeds).isEqualTo(expectedSeedsInKalah);
  }

  @Test
  void givenIsNonEmptyKalah_whenPerformAfterTurnAction_thenNoActionAndSwitchTurn() {
    // GIVEN a Pit for the rival with both theirs and ours seeds
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

    final Kalah kalah = Kalah.builder()
        .id(UUID.randomUUID())
        .seeds(3)
        .build();

    ownerSouth.setKalah(kalah);

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .nextPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    kalah.performAfterTurnAction(board);

    // THEN do not capture, do not switch turn
    assertThat(board.getNextPlayer()).isEqualTo(ownerSouth);
  }

  @Test
  void givenIsEmptyKalah_whenPerformAfterTurnAction_thenNoActionAndSwitchTurn() {
    // GIVEN a Pit for the rival with both theirs and ours seeds
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

    final Kalah kalah = Kalah.builder()
        .id(UUID.randomUUID())
        .seeds(0)
        .build();

    ownerSouth.setKalah(kalah);

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .nextPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    kalah.performAfterTurnAction(board);

    // THEN do not capture, do not switch turn
    assertThat(board.getNextPlayer()).isEqualTo(ownerSouth);
  }

  private static Player mockPlayer(PlayerType playerType) {
    final Player player = mock(Player.class);
    when(player.getName()).thenReturn(playerType.name().toLowerCase());
    when(player.getType()).thenReturn(playerType);
    doCallRealMethod().when(player).setPits(any());
    when(player.getPits()).thenCallRealMethod();
    return player;
  }
}
