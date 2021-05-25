package com.github.juanmougan.kalah;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlayerService {

  private final PlayerRepository playerRepository;

  private final PitService pitService;

  private final KalahService kalahService;

  public Player createPlayer(String name, PlayerType type) {
    Player player = Player.builder()
        .name(name)
        .type(type)
        .kalah(kalahService.createEmpty())
        .build();
    player = this.playerRepository.save(player);
    player.setPits(this.pitService.createInitialPits(player));
    return player;
  }

}
