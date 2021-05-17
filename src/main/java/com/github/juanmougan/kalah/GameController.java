package com.github.juanmougan.kalah;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameController {

  private final GameService gameService;

  @GetMapping(Endpoints.GAME_BY_ID)
  public Game getById(@PathVariable UUID id) {
    return gameService.getById(id);
  }
}
