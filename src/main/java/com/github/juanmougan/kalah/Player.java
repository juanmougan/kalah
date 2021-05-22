package com.github.juanmougan.kalah;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * Represents a Player - this WON'T be persisted!
 */
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Player {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "player_id", updatable = false, nullable = false)
  @ColumnDefault("random_uuid()")
  @Type(type = "uuid-char")
  private UUID id;

  private String name;

  @Enumerated(EnumType.STRING)
  private PlayerType type;

  @OneToMany(mappedBy = "owner")
  @ToString.Exclude
  private List<Pit> pits;

  @OneToOne
  private Kalah kalah;

  public boolean hasLegalMoves() {
    return this.pits.stream()
        .map(Pit::getOwnSeeds)
        .anyMatch(seeds -> seeds != 0);
  }

  public boolean hasNoSeedsInOwnPits() {
    return this.pits.stream()
        .map(Pit::getOwnSeeds)
        .noneMatch(s -> s.equals(0));
  }

  public int countAllRivalSeedsInOwnPits() {
    // It will fit into an int :)
    return this.getPits().stream()
        .map(Pit::getRivalSeeds)
        .mapToInt(Integer::intValue)
        .sum();
  }
}
