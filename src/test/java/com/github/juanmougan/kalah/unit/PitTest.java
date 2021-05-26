package com.github.juanmougan.kalah.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.juanmougan.kalah.Board;
import com.github.juanmougan.kalah.Kalah;
import com.github.juanmougan.kalah.Pit;
import com.github.juanmougan.kalah.Player;
import com.github.juanmougan.kalah.PlayerType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PitTest {

  private static Player mockPlayer(PlayerType playerType) {
    final Player player = mock(Player.class);
    when(player.getName()).thenReturn(playerType.name().toLowerCase());
    when(player.getType()).thenReturn(playerType);
    doCallRealMethod().when(player).setPits(any());
    when(player.getPits()).thenCallRealMethod();
    return player;
  }

  private static Player mockPlayer(PlayerType playerType, Kalah kalah) {
    Player player = mockPlayer(playerType);
    when(player.getKalah()).thenReturn(kalah);
    return player;
  }

  @Test
  void givenIsRivalEmptyPit_whenPerformAfterTurnAction_thenNoActionAndSwitchTurn() {
    // GIVEN a Pit for the current Player with no seeds
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

    final Pit rivalPit = Pit.builder()
        .id(UUID.randomUUID())
        .index(3)
        .owner(rivalNorth)
        .ownSeeds(0)
        .rivalSeeds(0)
        .build();

    rivalNorth.setPits(List.of(rivalPit));

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .currentPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    rivalPit.performAfterTurnAction(board);

    // THEN do not capture, switch turn
    assertThat(board.getCurrentPlayer()).isEqualTo(rivalNorth);
  }

  @Test
  void givenIsRivalNonEmptyPit_whenPerformAfterTurnAction_thenNoActionAndSwitchTurn() {
    // GIVEN a Pit for the rival with both theirs and ours seeds
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

    final Pit rivalPit = Pit.builder()
        .id(UUID.randomUUID())
        .index(3)
        .owner(rivalNorth)
        .ownSeeds(5)
        .rivalSeeds(7)
        .build();

    rivalNorth.setPits(List.of(rivalPit));

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .currentPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    rivalPit.performAfterTurnAction(board);

    // THEN do not capture, switch turn
    assertThat(board.getCurrentPlayer()).isEqualTo(rivalNorth);
  }

  @Test
  void givenIsNonEmptyOwnPit_whenPerformAfterTurnAction_thenNoActionAndSwitchTurn() {
    // GIVEN a Pit for the rival with both theirs and ours seeds
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

    final Pit ownPit = Pit.builder()
        .id(UUID.randomUUID())
        .index(3)
        .owner(ownerSouth)
        .ownSeeds(5)
        .rivalSeeds(7)
        .build();

    ownerSouth.setPits(List.of(ownPit));

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .currentPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    ownPit.performAfterTurnAction(board);

    // THEN do not capture, switch turn
    assertThat(board.getCurrentPlayer()).isEqualTo(rivalNorth);
  }

  @Test
  @Disabled("Review")
  void givenIsEmptyOwnPit_whenPerformAfterTurnAction_thenPerformActionAndSwitchTurn() {
    // GIVEN a Pit for the rival with both theirs and ours seeds
    final int NORTH_SEEDS_IN_OPPOSITE_PIT = 3;
    final int SEEDS_IN_KALAH = 4;
    final int STARTING_PIT_INDEX = 3;
    final int SEEDS_IN_STARTING_PIT = 2;
    final int endingPitIndex = STARTING_PIT_INDEX + SEEDS_IN_STARTING_PIT;
    final int expectedSeedsInKalah = NORTH_SEEDS_IN_OPPOSITE_PIT + SEEDS_IN_KALAH + 1;
    final int oppositePitIndex = Board.getOppositePitIndex(endingPitIndex);

    final Kalah southKalah = Kalah.builder()
        .id(UUID.randomUUID())
        .seeds(SEEDS_IN_KALAH)
        .build();
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH, southKalah);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

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

    ownerSouth.setPits(List.of(endingPit));
    rivalNorth.setPits(List.of(oppositeEndingPit));

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .currentPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    endingPit.performAfterTurnAction(board);

    // THEN capture and switch turn
    assertThat(board.getCurrentPlayer()).isEqualTo(rivalNorth);
    assertThat(endingPit.getOwnSeeds()).isZero();
    Pit actualOppositePit = board.getNorth().getPits().stream()
        .filter(p -> p.getIndex() == oppositePitIndex)
        .findFirst().orElseThrow();
    assertThat(actualOppositePit.getOwnSeeds()).isZero();
    assertThat(actualOppositePit.getRivalSeeds()).isZero();
    assertThat(board).extracting(Board::getSouth).extracting(Player::getKalah)
        .extracting(Kalah::getSeeds).isEqualTo(expectedSeedsInKalah);
  }

  @Test
  void givenIsEmptyOwnPit_andEmptyOppositePit_whenPerformAfterTurnAction_thenPerformActionAndDoNotCaptureAndSwitchTurn() {
    // GIVEN a Pit for the rival with both theirs and ours seeds
    final int NORTH_SEEDS_IN_OPPOSITE_PIT = 3;
    final int SEEDS_IN_KALAH = 4;
    final int STARTING_PIT_INDEX = 3;
    final int SEEDS_IN_STARTING_PIT = 2;
    final int endingPitIndex = STARTING_PIT_INDEX + SEEDS_IN_STARTING_PIT;
    final int expectedSeedsInKalah = NORTH_SEEDS_IN_OPPOSITE_PIT + SEEDS_IN_KALAH + 1;
    final int oppositePitIndex = Board.getOppositePitIndex(endingPitIndex);

    final Kalah southKalah = Kalah.builder()
        .id(UUID.randomUUID())
        .seeds(SEEDS_IN_KALAH)
        .build();
    final Player ownerSouth = mockPlayer(PlayerType.SOUTH, southKalah);
    final Player rivalNorth = mockPlayer(PlayerType.NORTH);

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
        .ownSeeds(0)
        .rivalSeeds(0)
        .build();

    ownerSouth.setPits(List.of(endingPit));
    rivalNorth.setPits(List.of(oppositeEndingPit));

    final Board board = Board.builder()
        .id(UUID.randomUUID())
        .currentPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    endingPit.performAfterTurnAction(board);

    // THEN do not capture, switch turn
    assertThat(board.getCurrentPlayer()).isEqualTo(rivalNorth);
    assertThat(endingPit.getOwnSeeds()).isZero();
    Pit actualOppositePit = board.getNorth().getPits().stream()
        .filter(p -> p.getIndex() == oppositePitIndex)
        .findFirst().orElseThrow();
    assertThat(actualOppositePit.getRivalSeeds()).isZero();
    assertThat(board).extracting(Board::getSouth).extracting(Player::getKalah)
        .extracting(Kalah::getSeeds).isEqualTo(SEEDS_IN_KALAH);
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
        .currentPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    kalah.performAfterTurnAction(board);

    // THEN do not capture, do not switch turn
    assertThat(board.getCurrentPlayer()).isEqualTo(ownerSouth);
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
        .currentPlayer(ownerSouth)
        .south(ownerSouth)
        .north(rivalNorth)
        .build();

    // WHEN perform action
    kalah.performAfterTurnAction(board);

    // THEN do not capture, do not switch turn
    assertThat(board.getCurrentPlayer()).isEqualTo(ownerSouth);
  }
}
