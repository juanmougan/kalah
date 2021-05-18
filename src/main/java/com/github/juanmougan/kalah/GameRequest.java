package com.github.juanmougan.kalah;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameRequest {
  String playerSouth;
  String playerNorth;
}
