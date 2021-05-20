INSERT INTO player (player_id, name, type) VALUES ('00000000-0000-0000-0000-000000000000', 'south', 'SOUTH');                                                                     -- TODO insert missing data
INSERT INTO player (player_id, name, type) VALUES ('00000000-0000-0000-0000-000000000001', 'north', 'NORTH');                                                                     -- TODO insert missing data
INSERT INTO pit (pit_id, index, owner, own_seeds, rival_seeds) VALUES ('00000000-0000-0000-0000-000000000004', 4, '00000000-0000-0000-0000-000000000000', 3, 1);
INSERT INTO pit (pit_id, index, owner, own_seeds, rival_seeds) VALUES ('00000000-0000-0000-0000-000000000003', 5, '00000000-0000-0000-0000-000000000000', 0, 0);
INSERT INTO kalah (kalah_id, seeds) VALUES ('00000000-0000-0000-0000-000000000000', 0);
INSERT INTO pit (pit_id, index, owner, own_seeds, rival_seeds) VALUES ('00000000-0000-0000-0000-000000000010', 0, '00000000-0000-0000-0000-000000000001', 0, 0);
INSERT INTO board (board_id, south, next_player) VALUES ('00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000000', '00000000-0000-0000-0000-000000000000'); -- TODO insert missing data
INSERT INTO game (game_id, status, board) VALUES ('00000000-0000-0000-0000-000000000000', 'STARTED', '00000000-0000-0000-0000-000000000000');
INSERT INTO game (game_id, status, board) VALUES ('00000000-0000-0000-0000-000000000001', 'DRAW', '00000000-0000-0000-0000-000000000000');
