package com.github.juanmougan.kalah;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameService {

  private final GameRepository gameRepository;

  private final BoardService boardService;

  private static void failIfGameOver(Game currentGame, Player currentPlayer) {
    if (!currentGame.isGameInProgress() || !currentPlayer.hasLegalMoves()) {
      throw new IllegalMovementException("Game over!");
    }
  }

  private static void failIfNotPlayersTurn(Player currentPlayer, PlayerType playerType) {
    if (!currentPlayer.getType().equals(playerType)) {
      throw new IllegalMovementException(String.format("It's %s turn!", currentPlayer.getName()));
    }
  }

  public Game getById(UUID id) {
    return gameRepository.getOne(id);
  }

  public Game create(GameRequest gameRequest) {
    return gameRepository.save(Game.builder()
        .status(Status.STARTED)
        .board(this.boardService.createInitialBoard(
            gameRequest.getPlayerSouth(),
            gameRequest.getPlayerNorth()))
        .build());
  }

  public Game move(UUID gameId, MoveRequest moveRequest) {
    final Game currentGame = this.gameRepository.getOne(gameId);
    final Player currentPlayer = currentGame.currentPlayer();
    failIfGameOver(currentGame, currentPlayer);
    failIfNotPlayersTurn(currentPlayer, moveRequest.getPlayerType());
    // TODO check valid move (array out of bounds only?)
    currentGame.getBoard().performMovement(currentPlayer, moveRequest.getPit());
    currentGame.verifyGameOver();
    return this.gameRepository.save(currentGame);
  }
}
