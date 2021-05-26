package com.github.juanmougan.kalah;

import static com.github.juanmougan.kalah.PlayerType.NORTH;
import static com.github.juanmougan.kalah.PlayerType.SOUTH;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
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
  @JoinColumn(name = "current_player")
  private Player currentPlayer;

  public static int getOppositePitIndex(int startingPitIndex) {
    return Board.NUMBER_OF_PITS - 1 - startingPitIndex;
  }

  public void performMovement(Player player, int pitIndex) {
    List<Pit> pits = player.getPits();
    final DecrementedCounter totalSeeds = DecrementedCounter
        .createForBoardAndValue(this, pits.get(pitIndex).getOwnSeeds());
    pits.get(pitIndex).setOwnSeeds(0);
    // TODO do-while this (in case I need to reiteare my own again - say if I have > 13 seeds on my starting pit)
    for (int i = pitIndex + 1; i < pits.size() && totalSeeds.isActive(); i++) {
      Pit currentPit = pits.get(i);
      int currentOwnSeeds = currentPit.getOwnSeeds();
      pits.get(i).setOwnSeeds(currentOwnSeeds + 1);
      totalSeeds.decrementInCell(currentPit);
    }
    // Update Kalah
    if (totalSeeds.isActive()) {
      Kalah kalah = player.getKalah();
      int seeds = kalah.getSeeds();
      kalah.setSeeds(seeds + 1);
      totalSeeds.decrementInCell(kalah);
    }
    // Update rival pits
    List<Pit> rivalPits = this.getRival(player).getPits();
    for (int i = 0; i < rivalPits.size() && totalSeeds.isActive(); i++) {
      Pit currentRivalPit = rivalPits.get(i);
      int currentRivalSeeds = currentRivalPit.getRivalSeeds();// Rival of rival == own
      rivalPits.get(i).setRivalSeeds(currentRivalSeeds + 1);
      totalSeeds.decrementInCell(currentRivalPit);
    }
    // TODO if totalSeeds > 0 handle own Kalah + rival Pits scenario
  }

  private Player getRival(Player player) {
    if (this.south.equals(player)) {
      return this.north;
    }
    return this.south;
  }

  public List<Pit> getRivalPits(Player player) {
    Player rival = this.getRival(player);
    return rival.getPits();
  }

  public void flipTurn() {
    this.setCurrentPlayer(this.getRival(this.getCurrentPlayer()));
  }

  public Status handleGameOver() {
    Player rival = this.getRival(this.currentPlayer);
    rival.getKalah().addSeeds(this.currentPlayer.countAllRivalSeedsInOwnPits());
    // TODO maybe capture them for UI purposes?
    if (rival.getKalah().getSeeds() > this.currentPlayer.getKalah().getSeeds()) {
      return rival.getType().getWinner();
    } else if (rival.getKalah().getSeeds() < this.currentPlayer.getKalah().getSeeds()) {
      return this.currentPlayer.getType().getWinner();
    } else {
      return Status.DRAW;
    }
  }
}
