package com.github.juanmougan.kalah;

import static com.github.juanmougan.kalah.Board.INITIAL_SEEDS_PER_PIT;
import static com.github.juanmougan.kalah.Board.NUMBER_OF_PITS;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PitService {

  public final PitRepository pitRepository;

  public List<Pit> createInitialPits(final Player owner) {
    return IntStream.range(0, NUMBER_OF_PITS)
        .mapToObj(i -> this.pitRepository.save(
            Pit.builder()
                .owner(owner)
                .rivalSeeds(0)
                .ownSeeds(INITIAL_SEEDS_PER_PIT)
                .index(i)
                .build()
            )
        )
        .collect(Collectors.toList());
  }
}
