package com.github.juanmougan.kalah;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class Pit implements Cell {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "pit_id", updatable = false, nullable = false)
  @ColumnDefault("random_uuid()")
  @Type(type = "uuid-char")
  private UUID id;

  @Column
  private int index;

  @ManyToOne
  @JoinColumn(name = "owner")
  @JsonIgnore
  // TODO add ModelMapper and create DTOs
  private Player owner;

  @Column(name = "own_seeds")
  private int ownSeeds;

  @Column(name = "rival_seeds")
  private int rivalSeeds;

  private boolean isPlayerPit(Player player) {
    return this.owner.equals(player);
  }

  private boolean wasEmptyBeforeMovement() {
    return ownSeeds == 1 && rivalSeeds == 0;
  }

  public int acceptCapture() {
    int originalOwnSeeds = this.ownSeeds;
    int originalRivalSeeds = this.rivalSeeds;
    this.setOwnSeeds(0);
    this.setRivalSeeds(0);
    return originalOwnSeeds + originalRivalSeeds;
  }

  @Override
  public void performAfterTurnAction(Board board) {
    Player currentPlayer = board.getCurrentPlayer();
    System.out.println("Reached the end of the turn on cell: " + this.toString() + " for player: "
        + currentPlayer + ":" + currentPlayer.getType());
    if (this.isPlayerPit(currentPlayer) && this.wasEmptyBeforeMovement()) {
      int oppositePitIndex = Board.getOppositePitIndex(this.getIndex());
      Pit rivalPit = board.getRivalPits(currentPlayer).stream()
          .filter(p -> p.getIndex() == oppositePitIndex)
          .findFirst()
          .orElseThrow(() -> new IllegalMovementException(
              String.format("Pit with index %d not found", oppositePitIndex)));
      int capturedSeeds = rivalPit.acceptCapture();
      if (capturedSeeds > 0) {
        this.setOwnSeeds(0);
        currentPlayer.getKalah().addSeeds(capturedSeeds + 1);
      }
    }
    board.flipTurn();
  }
}
