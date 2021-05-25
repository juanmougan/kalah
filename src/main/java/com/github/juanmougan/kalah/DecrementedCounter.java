package com.github.juanmougan.kalah;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DecrementedCounter {

  private int value;
  private Cell lastVisitedCell;
  private Status status;
  private Board board;

  private DecrementedCounter(Board board, int value) {
    this.board = board;
    this.value = value;
    this.status = Status.ACTIVE;
  }

  public static DecrementedCounter createForBoardAndValue(Board board, int initialValue) {
    return new DecrementedCounter(board, initialValue);
  }

  public void decrementInCell(final Cell currentCell) {
    this.lastVisitedCell = currentCell;
    this.value--;
    if (this.value == 0) {
      this.status = Status.FINISHED;
    }
    System.out.println("Value: " + this.value + " status: " + this.status);
    this.performActionIfFinished();
  }

  public boolean isActive() {
    return this.status.equals(Status.ACTIVE);
  }

  private void performActionIfFinished() {
    if (!this.isActive()) {
      this.lastVisitedCell.performAfterTurnAction(this.board);
    }
  }

  enum Status {
    ACTIVE, FINISHED
  }
}
