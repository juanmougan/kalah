package com.github.juanmougan.kalah;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameService {

  private final GameRepository gameRepository;

  public Game getById(UUID id) {
    return gameRepository.getOne(id);
  }

  public Game create(GameRequest gameRequest) {
    return gameRepository.save(Game.builder()
        .status(Status.STARTED)
        .build());
  }

  public Game move(UUID gameId, MoveRequest moveRequest) {
    final Game currentGame = this.gameRepository.getOne(gameId);
    final Player currentPlayer = currentGame.nextPlayer();
    // TODO maybe move to the controllers?
    if (!currentGame.isGameInProgress() || !currentPlayer.hasLegalMoves()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Game over!");
    }
    // TODO check valid move, move, switch "next", save
    return currentGame;
  }
}
