package com.github.juanmougan.kalah;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Data
@Builder
@Entity
public class Kalah implements Cell {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "kalah_id", updatable = false, nullable = false)
  @ColumnDefault("random_uuid()")
  @Type(type = "uuid-char")
  private UUID id;

  // TODO maybe I need to differentiate own and rival seeds - so the UI can show them with different colors?
  private int seeds;

  @Override
  public void performAfterTurnAction(Player currentPlayer) {
    // TODO implement - board.setNextPlayer(currentPlayer)
    System.out.println("Reached the end of the turn on cell: " + this.toString() + " for player: "
        + currentPlayer.getName() + ":" + currentPlayer.getType());
  }
}
