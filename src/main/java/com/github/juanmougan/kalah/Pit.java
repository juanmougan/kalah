package com.github.juanmougan.kalah;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
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

  @Override
  public void performAfterTurnAction(Board board) {
    // TODO implement - IF this.isPlayerCell(currentPlayer) THEN capture/not depending ELSE do nothing
    System.out.println("Reached the end of the turn on cell: " + this.toString() + " for player: "
        + board.getNextPlayer() + ":" + board.getNextPlayer().getType());
    board.flipTurn();
  }
}

// TODO the opposite Pit is: N (6) - 1 - myIndex
