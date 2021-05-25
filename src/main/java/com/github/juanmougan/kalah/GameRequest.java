package com.github.juanmougan.kalah;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRequest {

  private String playerSouth;
  private String playerNorth;
}
