package com.github.juanmougan.kalah;

import java.util.UUID;
import lombok.Data;

@Data
public class GameResponse {
  private UUID id;
  private Status status;
  private Player next;
}
