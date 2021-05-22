package com.github.juanmougan.kalah;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Game {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "game_id", updatable = false, nullable = false)
  @ColumnDefault("random_uuid()")
  @Type(type = "uuid-char")
  private UUID id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @OneToOne
  @JoinColumn(name = "board")
  private Board board;

  public Player nextPlayer() {
    return this.board.getCurrentPlayer();
  }

  public boolean isGameInProgress() {
    return Status.STARTED.equals(this.status);
  }

  public void verifyGameOver() {
    if (this.getBoard().getCurrentPlayer().hasNoSeedsInOwnPits()) {
      this.setStatus(this.getBoard().handleGameOver());
    }
  }
}
