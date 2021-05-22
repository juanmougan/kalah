package com.github.juanmougan.kalah.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.juanmougan.kalah.Endpoints;
import com.github.juanmougan.kalah.Game;
import com.github.juanmougan.kalah.GameRequest;
import com.github.juanmougan.kalah.MoveRequest;
import com.github.juanmougan.kalah.PlayerType;
import com.github.juanmougan.kalah.Status;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerIT {

  public static final String STARTED_GAME_ID = "00000000-0000-0000-0000-000000000000";
  public static final String DRAWN_GAME_ID = "00000000-0000-0000-0000-000000000001";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void givenGameId_whenGetById_thenReturnItsStatus() throws Exception {
    // GIVEN an id
    final UUID id = UUID.fromString(STARTED_GAME_ID);
    // WHEN getById
    final MvcResult getByIdResult = this.mockMvc.perform(get(Endpoints.GAMES + "/" + id))
        .andExpect(status().isOk())
        .andReturn();
    final Game game = deserializeResponse(getByIdResult);
    // THEN return the Game data
    assertThat(game).extracting(Game::getId).isEqualTo(id);
    assertThat(game).extracting(Game::getStatus).isEqualTo(Status.STARTED);
    // TODO rest of the assertions
  }

  @Test
  public void givenGameRequestData_whenCreateGame_thenCreateIt_andReturnItsData()
      throws Exception {
    // GIVEN some Game data
    final GameRequest gameRequest = GameRequest.builder()
        .playerSouth("Julio")
        .playerNorth("Manuel")
        .build();
    // WHEN create Game
    final String requestContent = this.objectMapper.writeValueAsString(gameRequest);
    MvcResult createdGameResult = this.mockMvc
        .perform(post(Endpoints.GAMES).contentType(APPLICATION_JSON).content(requestContent))
        .andExpect(status().is(201))
        .andReturn();
    final Game createdGame = deserializeResponse(createdGameResult);
    // THEN verify the Game data
    assertThat(createdGame).extracting(Game::getId).isNotNull();
    assertThat(createdGame).extracting(Game::getStatus).isEqualTo(Status.STARTED);
  }

  @Test
  public void givenPlayer_andPit_whenMove_thenReturnNewBoard() throws Exception {
    // GIVEN a Player and a move (a Pit number)
    final UUID gameId = UUID.fromString(STARTED_GAME_ID);
    final MoveRequest moveRequest = MoveRequest.builder()
        .pit(4)
        .build();
    // WHEN move
    final String requestContent = this.objectMapper.writeValueAsString(moveRequest);
    final MvcResult moveMadeResult = this.mockMvc.perform(
        patch(Endpoints.GAMES + "/" + gameId + "/players/" + PlayerType.SOUTH.name())
            .contentType(APPLICATION_JSON).content(requestContent))
        .andExpect(status().isOk())
        .andReturn();
    // THEN get the new Board for the Game
    final Game updatedGame = deserializeResponse(moveMadeResult);
    assertThat(updatedGame).extracting(Game::getId).isEqualTo(gameId);
    assertThat(updatedGame).extracting(Game::getStatus).isEqualTo(Status.STARTED);
  }

  @Test
  public void givenPlayer_andPit_andEndedGame_whenMove_thenReturnBadRequest() throws Exception {
    // GIVEN a Player and a move (a Pit number)
    final UUID gameId = UUID.fromString(DRAWN_GAME_ID);
    final MoveRequest moveRequest = MoveRequest.builder()
        .pit(0)
        .build();
    // WHEN move THEN 400 is returned
    final String requestContent = this.objectMapper.writeValueAsString(moveRequest);
    this.mockMvc.perform(
        patch(Endpoints.GAMES + "/" + gameId + "/players/" + PlayerType.SOUTH.name())
            .contentType(APPLICATION_JSON).content(requestContent))
        .andExpect(status().isBadRequest());
  }

  private Game deserializeResponse(MvcResult createdGameResult)
      throws UnsupportedEncodingException, JsonProcessingException {
    final String responseAsString = createdGameResult.getResponse().getContentAsString();
    return this.objectMapper.readValue(responseAsString, Game.class);
  }
}
