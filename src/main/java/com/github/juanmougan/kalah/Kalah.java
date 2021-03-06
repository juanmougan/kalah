package com.github.juanmougan.kalah;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
  public void performAfterTurnAction(Board board) {
    // No op here
    System.out.println("The player will play again");
  }

  public void addSeeds(int seedsToAdd) {
    this.seeds += seedsToAdd;
  }
}
