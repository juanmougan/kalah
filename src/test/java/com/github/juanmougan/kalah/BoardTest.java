package com.github.juanmougan.kalah;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BoardTest {

  @Test
  void givenMovementOnlyBetweenSelfPits_whenPerformMovement_thenUpdateBoardStatus() {
    // GIVEN a Board
    Player south = createPlayerWithSeeds(PlayerType.SOUTH, List.of(0, 3, 0, 0, 0, 0),
        List.of(0, 0, 0, 0, 0, 0));
    Player north = createPlayerWithSeeds(PlayerType.NORTH, List.of(0, 3, 0, 0, 0, 0),
        List.of(0, 0, 0, 0, 0, 0));
    Board board = Board.builder()
        .south(south)
        .north(north)
        .nextPlayer(south)
        .build();
    // WHEN a move between our pits is performed
    board.performMovement(south, 1);
    // THEN update the Board status
    assertThat(board.getSouth().getPits()).extracting(Pit::getOwnSeeds)
        .containsExactly(0, 0, 1, 1, 1, 0);
  }

  @Test
  void givenMovementBetweenSelfPitsKalahAndRivalSeeds_whenPerformMovement_thenUpdateBoardStatus() {
    // GIVEN a Board
    Player south = createPlayerWithSeeds(PlayerType.SOUTH, List.of(0, 0, 0, 0, 4, 0),
        List.of(3, 3, 0, 0, 0, 0));
    Player north = createPlayerWithSeeds(PlayerType.NORTH, List.of(2, 2, 0, 0, 0, 0),
        List.of(0, 0, 0, 0, 0, 0));
    Board board = Board.builder()
        .south(south)
        .north(north)
        .nextPlayer(south)
        .build();
    // WHEN a move between our pits is performed
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

  // TODO South.own should be == North.rival, THIS IS WRONG!

  private Player createPlayerWithSeeds(PlayerType playerType, List<Integer> ownSeeds,
      List<Integer> rivalSeeds) {
    Player player = mock(Player.class);
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