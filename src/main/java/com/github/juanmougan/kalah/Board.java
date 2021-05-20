package com.github.juanmougan.kalah;

import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Data
@Builder
@Entity
public class Board {

  public static final int NUMBER_OF_PITS = 6;
  public static final int INITIAL_SEEDS_PER_PIT = 4;

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "board_id", updatable = false, nullable = false)
  @ColumnDefault("random_uuid()")
  @Type(type = "uuid-char")
  private UUID id;

  @OneToOne
  @JoinColumn(name = "south")
  private Player south;

  @OneToOne
  @JoinColumn(name = "north")
  private Player north;

  @OneToOne
  @JoinColumn(name = "next_player")
  private Player nextPlayer;

  public static Board newInitialBoard(String southPlayerName, String northPlayerName) {
    final Player newSouthPlayer = Player.builder().build();   // TODO complete this
    return Board.builder()
        .id(UUID.randomUUID())
        .south(newSouthPlayer)
        .north(Player.builder().build()) // TODO complete this
        .nextPlayer(newSouthPlayer)
        .build();
  }

  public void performMovement(Player player, int pitIndex) {
    List<Pit> pits = player.getPits();
    final DecrementedCounter totalSeeds = DecrementedCounter
        .createForPlayerAndValue(player, pits.get(pitIndex).getOwnSeeds());
    pits.get(pitIndex).setOwnSeeds(0);
    // TODO do-while this (in case I need to reiteare my own again - say if I have > 13 seeds on my starting pit)
    for (int i = pitIndex + 1; i < pits.size() && totalSeeds.isActive(); i++) {
      Pit currentPit = pits.get(i);
      int currentOwnSeeds = currentPit.getOwnSeeds();
      totalSeeds.decrementInCell(currentPit);
      pits.get(i).setOwnSeeds(currentOwnSeeds + 1);
    }
    // Update Kalah
    if (totalSeeds.isActive()) {
      Kalah kalah = player.getKalah();
      int seeds = kalah.getSeeds();
      totalSeeds.decrementInCell(kalah);
      kalah.setSeeds(seeds + 1);
    }
    // Update rival pits
    List<Pit> rivalPits = this.getRival(player).getPits();
    for (int i = 0; i < rivalPits.size() && totalSeeds.isActive(); i++) {
      Pit currentRivalPit = rivalPits.get(i);
      int currentRivalSeeds = currentRivalPit.getRivalSeeds();// Rival of rival == own
      totalSeeds.decrementInCell(currentRivalPit);
      rivalPits.get(i).setRivalSeeds(currentRivalSeeds + 1);
    }
    // TODO if totalSeeds > 0 handle own Kalah + rival Pits scenario
  }

  private Player getRival(Player player) {
    if (this.south.equals(player)) {
      return this.north;
    }
    return this.south;
  }
}
