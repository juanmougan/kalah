package com.github.juanmougan.kalah.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.juanmougan.kalah.Board;
import com.github.juanmougan.kalah.Endpoints;
import com.github.juanmougan.kalah.Game;
import com.github.juanmougan.kalah.GameRequest;
import com.github.juanmougan.kalah.MoveRequest;
import com.github.juanmougan.kalah.Pit;
import com.github.juanmougan.kalah.Player;
import com.github.juanmougan.kalah.PlayerType;
import com.github.juanmougan.kalah.Status;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerIT {

  public static final String STARTED_GAME_ID = "00000000-0000-0000-0000-000000000000";
  public static final String DRAWN_GAME_ID = "00000000-0000-0000-0000-000000000001";
  public static final String LAST_MOVE_GAME_ID = "00000000-0000-0000-0000-000000000020";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Disabled("Will delete this endpoint")
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
    final String SOUTH_NAME = "Julio";
    final String NORTH_NAME = "Manuel";
    final GameRequest gameRequest = GameRequest.builder()
        .playerSouth(SOUTH_NAME)
        .playerNorth(NORTH_NAME)
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
    assertThat(createdGame).extracting(Game::currentPlayer).extracting(Player::getName).isEqualTo(SOUTH_NAME);
    assertThat(createdGame).extracting(Game::getBoard).extracting(Board::getSouth).extracting(Player::getName).isEqualTo(SOUTH_NAME);
    assertThat(createdGame).extracting(Game::getBoard).extracting(Board::getNorth).extracting(Player::getName).isEqualTo(NORTH_NAME);
  }

  // TODO add test that if move ends on Kalah, don't flip turns
  @Test
  public void givenPlayer_andPit_whenMove_thenReturnNewBoard() throws Exception {
    // GIVEN a Player and a move (a Pit number)
    final int startingIndex = 3;
    final UUID gameId = UUID.fromString(STARTED_GAME_ID);
    final MoveRequest moveRequest = MoveRequest.builder()
        .pit(startingIndex)
        .playerType(PlayerType.SOUTH)
        .build();
    // WHEN move
    final String requestContent = this.objectMapper.writeValueAsString(moveRequest);
    final MvcResult moveMadeResult = this.mockMvc.perform(
        patch(Endpoints.GAMES + "/" + gameId)
            .contentType(APPLICATION_JSON).content(requestContent))
        .andExpect(status().isOk())
        .andReturn();
    // THEN get the new Board for the Game
    final Game updatedGame = deserializeResponse(moveMadeResult);
    assertThat(updatedGame).extracting(Game::getId).isEqualTo(gameId);
    assertThat(updatedGame).extracting(Game::getStatus).isEqualTo(Status.STARTED);
    final List<Pit> southPits = updatedGame.getBoard().getSouth().getPits();
    assertThat(southPits.get(startingIndex)).extracting(Pit::getOwnSeeds).isEqualTo(0);
    assertThat(southPits.get(startingIndex + 1)).extracting(Pit::getOwnSeeds).isEqualTo(1);
    assertThat(southPits.get(startingIndex + 2)).extracting(Pit::getOwnSeeds).isEqualTo(1);
    assertThat(updatedGame).extracting(Game::currentPlayer).extracting(Player::getType)
        .isEqualTo(PlayerType.NORTH);
  }

  @Test
  public void givenPlayer_andPit_andEndedGame_whenMove_thenReturnBadRequest() throws Exception {
    // GIVEN a Player and a move (a Pit number)
    final UUID gameId = UUID.fromString(DRAWN_GAME_ID);
    final MoveRequest moveRequest = MoveRequest.builder()
        .pit(0)
        .playerType(PlayerType.SOUTH)
        .build();
    // WHEN move THEN 400 is returned
    final String requestContent = this.objectMapper.writeValueAsString(moveRequest);
    this.mockMvc.perform(
        patch(Endpoints.GAMES + "/" + gameId)
            .contentType(APPLICATION_JSON).content(requestContent))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void givenPlayer_andPit_andNotMyTurn_whenMove_thenReturnBadRequest() throws Exception {
    // GIVEN a Player and a move (a Pit number)
    final UUID gameId = UUID.fromString(STARTED_GAME_ID);
    final MoveRequest moveRequest = MoveRequest.builder()
        .pit(0)
        .playerType(PlayerType.NORTH)
        .build();
    // WHEN move THEN 400 is returned
    final String requestContent = this.objectMapper.writeValueAsString(moveRequest);
    this.mockMvc.perform(
        patch(Endpoints.GAMES + "/" + gameId)
            .contentType(APPLICATION_JSON).content(requestContent))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void givenNorthLastMove_whenMove_thenGameOver_andSouthWins() throws Exception {
    // GIVEN a game about to end
    final UUID lastMoveGameId = UUID.fromString(LAST_MOVE_GAME_ID);
    final MoveRequest moveRequest = MoveRequest.builder()
        .pit(5)
        .playerType(PlayerType.NORTH)
        .build();
    final String requestContent = this.objectMapper.writeValueAsString(moveRequest);
    // WHEN north moves
    final MvcResult moveMadeResult = this.mockMvc.perform(
        patch(Endpoints.GAMES + "/" + lastMoveGameId)
            .contentType(APPLICATION_JSON).content(requestContent))
        .andExpect(status().isOk())
        .andReturn();
    // THEN
    final Game gameOver = deserializeResponse(moveMadeResult);
    assertThat(gameOver).extracting(Game::getId).isEqualTo(lastMoveGameId);
    assertThat(gameOver).extracting(Game::getStatus).isEqualTo(Status.SOUTH_WINS);
  }

  private Game deserializeResponse(MvcResult createdGameResult)
      throws UnsupportedEncodingException, JsonProcessingException {
    final String responseAsString = createdGameResult.getResponse().getContentAsString();
    return this.objectMapper.readValue(responseAsString, Game.class);
  }
}
