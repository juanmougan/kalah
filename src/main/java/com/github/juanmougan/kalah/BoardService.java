package com.github.juanmougan.kalah;

import static com.github.juanmougan.kalah.PlayerType.NORTH;
import static com.github.juanmougan.kalah.PlayerType.SOUTH;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BoardService {

  private final BoardRepository boardRepository;

  private final PlayerService playerService;

  public Board createInitialBoard(final String southName, final String northName) {
    return this.boardRepository.save(
        newInitialBoard(
            playerService.createPlayer(southName, SOUTH),
            playerService.createPlayer(northName, NORTH)
        )
    );
  }

  public static Board newInitialBoard(Player south, Player north) {
    return Board.builder()
        .id(UUID.randomUUID())
        .south(south)
        .north(north)
        .currentPlayer(south)
        .build();
  }

}
