package com.github.juanmougan.kalah.unit;

import static com.github.juanmougan.kalah.PlayerType.NORTH;
import static com.github.juanmougan.kalah.PlayerType.SOUTH;
import static com.github.juanmougan.kalah.Status.NORTH_WINS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import com.github.juanmougan.kalah.Board;
import com.github.juanmougan.kalah.Kalah;
import com.github.juanmougan.kalah.Pit;
import com.github.juanmougan.kalah.Player;
import com.github.juanmougan.kalah.PlayerType;
import com.github.juanmougan.kalah.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BoardTest {

  @Test
  void givenMovementOnlyBetweenSelfPits_whenPerformMovement_thenUpdateBoardStatus() {
    // GIVEN a Board
    Player south = createPlayerWithSeeds(SOUTH, List.of(0, 3, 0, 0, 1, 0),
        List.of(0, 0, 0, 0, 0, 0));
    Player north = createPlayerWithSeeds(PlayerType.NORTH, List.of(0, 3, 0, 0, 0, 0),
        List.of(0, 0, 0, 0, 0, 0));
    Board board = Board.builder()
        .south(south)
        .north(north)
        .currentPlayer(south)
        .build();
    // WHEN a move between our pits is performed
    board.performMovement(south, 1);
    // THEN update the Board status
    assertThat(board.getSouth().getPits()).extracting(Pit::getOwnSeeds)
        .containsExactly(0, 0, 1, 1, 2, 0);
  }

  @Test
  void givenMovementBetweenSelfPitsKalahAndRivalSeeds_whenPerformMovement_thenUpdateBoardStatus() {
    // GIVEN a Board
    Player south = createPlayerWithSeeds(SOUTH, List.of(0, 0, 0, 0, 4, 0),
        List.of(3, 3, 0, 0, 0, 0));
    Player north = createPlayerWithSeeds(PlayerType.NORTH, List.of(2, 2, 0, 0, 0, 0),
        List.of(0, 0, 0, 0, 0, 0));
    Board board = Board.builder()
        .south(south)
        .north(north)
        .currentPlayer(south)
        .build();
    // WHEN a move is performed
    board.performMovement(south, 4);
    // THEN update the Board status
    assertThat(board.getSouth().getPits()).extracting(Pit::getOwnSeeds)
        .containsExactly(0, 0, 0, 0, 0, 1);
    assertThat(board.getSouth())
        .extracting(Player::getKalah)
        .extracting(Kalah::getSeeds)
        .isEqualTo(1);
    assertThat(board.getNorth().getPits()).extracting(Pit::getRivalSeeds)
        .containsExactly(1, 1, 0, 0, 0, 0);
  }

  @Test
  void givenStartingBoard_whenPerformTwoMovements_thenUpdateBoardStatus() {
    // GIVEN a starting Board
    Player south = createPlayerWithSeeds(SOUTH, List.of(4, 4, 4, 4, 4, 4),
        List.of(0, 0, 0, 0, 0, 0));
    Player north = createPlayerWithSeeds(PlayerType.NORTH, List.of(4, 4, 4, 4, 4, 4),
        List.of(0, 0, 0, 0, 0, 0));
    Board board = Board.builder()
        .south(south)
        .north(north)
        .currentPlayer(south)
        .build();
    // WHEN South moves
    board.performMovement(south, 5);
    // THEN update the Board status
    assertThat(board.getSouth().getPits()).extracting(Pit::getOwnSeeds)
        .containsExactly(4, 4, 4, 4, 4, 0);
    assertThat(board.getSouth())
        .extracting(Player::getKalah)
        .extracting(Kalah::getSeeds)
        .isEqualTo(1);
    assertThat(board.getNorth().getPits()).extracting(Pit::getRivalSeeds)
        .containsExactly(1, 1, 1, 0, 0, 0);
    // AND WHEN North moves
    board.performMovement(north, 3);
    // THEN verify the board status
    assertThat(board.getNorth().getPits()).extracting(Pit::getOwnSeeds)
        .containsExactly(4, 4, 4, 0, 5, 5);
    assertThat(board.getNorth())
        .extracting(Player::getKalah)
        .extracting(Kalah::getSeeds)
        .isEqualTo(1);
    assertThat(board.getSouth().getPits()).extracting(Pit::getRivalSeeds)
        .containsExactly(1, 0, 0, 0, 0, 0);
  }

  @Test
  public void givenSouthHasNoMoves_whenGameOver_thenNorthGetsTheSeedsAndWins() {
    // GIVEN
    Player south = createPlayerWithSeedsAndKalah(
        SOUTH,
        List.of(0, 0, 0, 0, 0, 0),
        List.of(0, 0, 1, 0, 1, 0),
        Kalah.builder().id(UUID.randomUUID()).seeds(5).build()
    );
    Player north = createPlayerWithSeedsAndKalah(
        NORTH,
        List.of(0, 1, 1, 0, 0, 0),
        List.of(0, 1, 1, 0, 0, 0),
        Kalah.builder().id(UUID.randomUUID()).seeds(5).build()
    );
    Board board = Board.builder()
        .south(south)
        .north(north)
        .currentPlayer(south)
        .build();
    // WHEN
    Status status = board.handleGameOver();
    // THEN
    assertThat(status).isEqualTo(NORTH_WINS);
    assertThat(north).extracting(Player::getKalah).extracting(Kalah::getSeeds).isEqualTo(7);
  }

  private Player createPlayerWithSeedsAndKalah(PlayerType playerType, List<Integer> ownSeeds,
      List<Integer> rivalSeeds, Kalah kalah) {
    Player player = mock(Player.class);
    when(player.getName()).thenReturn(playerType.name().toLowerCase());
    when(player.getType()).thenReturn(playerType);
    List<Pit> pits = createPits(player, ownSeeds, rivalSeeds);
    when(player.getPits()).thenReturn(pits);
    when(player.getKalah()).thenReturn(kalah);
    when(player.countAllRivalSeedsInOwnPits()).thenCallRealMethod();
    return player;
  }

  private Player createPlayerWithSeeds(PlayerType playerType, List<Integer> ownSeeds,
      List<Integer> rivalSeeds) {
    Player player = mock(Player.class);
    when(player.getName()).thenReturn(playerType.name().toLowerCase());
    when(player.getType()).thenReturn(playerType);
    List<Pit> pits = createPits(player, ownSeeds, rivalSeeds);
    when(player.getPits()).thenReturn(pits);
    Kalah kalah = Kalah.builder().id(UUID.randomUUID()).seeds(0).build();
    when(player.getKalah()).thenReturn(kalah);
    return player;
  }

  private List<Pit> createPits(Player player, List<Integer> ownSeeds, List<Integer> rivalSeeds) {
    final List<Pit> pits = new ArrayList<>();
    for (int i = 0; i < ownSeeds.size(); i++) {
      final Pit pit = Pit.builder()
          .id(UUID.randomUUID())
          .ownSeeds(ownSeeds.get(i))
          .rivalSeeds(rivalSeeds.get(i))
          .index(i)
          .owner(player)
          .build();
      pits.add(pit);
    }
    return pits;
  }
}