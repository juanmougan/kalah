package com.github.juanmougan.kalah;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GameService {

  private final GameRepository gameRepository;

  public Game getById(UUID id) {
    return gameRepository.getOne(id);
  }
}
