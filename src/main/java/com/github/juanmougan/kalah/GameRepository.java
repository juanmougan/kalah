package com.github.juanmougan.kalah;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, UUID> {

}
