package com.github.juanmougan.kalah;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KalahService {

  private final KalahRepository kalahRepository;

  public Kalah createEmpty() {
    return this.kalahRepository.save(Kalah.builder().seeds(0).build());
  }

}
