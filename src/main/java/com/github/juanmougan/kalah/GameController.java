package com.github.juanmougan.kalah;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameController {

  private final GameService gameService;

  @GetMapping(Endpoints.GAME_BY_ID)
  public Game getById(@PathVariable UUID id) {
    return gameService.getById(id);
  }

  @PostMapping(Endpoints.GAMES)
  @ResponseStatus(HttpStatus.CREATED)
  public Game create(@RequestBody GameRequest request) {
    return gameService.create(request);
  }

  @PatchMapping(Endpoints.GAME_PATCH)
  public Game move(@PathVariable UUID id, @PathVariable PlayerType player, @RequestBody MoveRequest request) {
    // TODO create service, etc
    return null;
  }
}
