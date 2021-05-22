package com.github.juanmougan.kalah;

import lombok.Value;

@Value
public class IllegalMovementException extends RuntimeException {

  String message;
}
