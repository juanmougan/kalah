package com.github.juanmougan.kalah;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void givenGameId_whenGetById_thenReturnItsStatus() throws Exception {
    // GIVEN an id
    final UUID id = new UUID(0, 0);
    // WHEN getById
    final MvcResult getByIdResult = this.mockMvc.perform(get(Endpoints.GAMES + "/" + id))
        .andExpect(status().isOk())
        .andReturn();
    final String responseAsString = getByIdResult.getResponse().getContentAsString();
    final Game game = this.objectMapper.readValue(responseAsString, Game.class);
    // THEN return the Game data
    assertThat(game).extracting(Game::getId).isEqualTo(id);
    assertThat(game).extracting(Game::getStatus).isEqualTo(Status.STARTED);
    // TODO rest of the assertions
  }
}
