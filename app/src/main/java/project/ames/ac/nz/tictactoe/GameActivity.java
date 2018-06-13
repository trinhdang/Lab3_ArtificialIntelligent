package project.ames.ac.nz.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    /////////////////////////////////////////////////////////////////////////////////////
    //Declare global variables and objects
    private String receivedGameType, receivedGameLevel;
    private String receivedPlayer1Name, receivedPlayer2Name;

    private TextView gameType, gameLevel;
    private TextView player1Name, player2Name;
    private TextView playerTurn, gameResult;
    private Button btnReplay;

    private Boolean player1 = true, player2 = false;//Player 1 is always playing first
    private int tileStatus[][]; //"0": Empty, available to place a mark
    //"1": Occupied by the mark of player 1: X
    //"2": Occupied by the mark of player 2: O
    private Button btnTile[][];

    //AI Player
    AI_Player smartphone;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);

        ////////////////////////////////////////////////////////////////////////////////////////////
        receivedGameType = "";
        receivedGameLevel = "";
        //Extract data stored in Intent object sent from MainActivity
        receivedGameType = getIntent().getExtras().getString("GameType");
        receivedGameLevel = getIntent().getExtras().getString("GameLevel");
        receivedPlayer1Name = getIntent().getExtras().getString("Player1Name");
        receivedPlayer2Name = getIntent().getExtras().getString("Player2Name");

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Initialize the game board and its values
        btnTile = new Button[3][3];
        tileStatus = new int[3][3];

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Find control elements and do casting
        btnTile[2][2] = (Button) findViewById(R.id.btnTile3);
        btnTile[2][1] = (Button) findViewById(R.id.btnTile2);
        btnTile[2][0] = (Button) findViewById(R.id.btnTile1);
        //
        btnTile[1][2] = (Button) findViewById(R.id.btnTile6);
        btnTile[1][1] = (Button) findViewById(R.id.btnTile5);
        btnTile[1][0] = (Button) findViewById(R.id.btnTile4);
        //
        btnTile[0][2] = (Button) findViewById(R.id.btnTile9);
        btnTile[0][1] = (Button) findViewById(R.id.btnTile8);
        btnTile[0][0] = (Button) findViewById(R.id.btnTile7);
        //
        gameType = (TextView) findViewById(R.id.gameType);
        gameLevel = (TextView) findViewById(R.id.gameLevel);

        player1Name = (TextView) findViewById(R.id.player1);
        player2Name = (TextView) findViewById(R.id.player2);

        gameResult = (TextView) findViewById(R.id.gameResult);
        playerTurn = (TextView) findViewById(R.id.txtPlayerTurn);

        btnReplay = (Button) findViewById(R.id.btnRePlay);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Set listeners for these Widget control elements
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                //Initialize all tiles in the board ready to play
                btnTile[i][j].setOnClickListener(this);
            }
        }
        btnReplay.setOnClickListener(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        gameType.setText(receivedGameType);
        gameLevel.setText(receivedGameLevel);

        //Check which game type has been chosen
        if (receivedGameType.contains("Multilple Players")) {
            gameLevel.setVisibility(View.INVISIBLE);
            player1Name.setText(receivedPlayer1Name + " (X): 0");
            player2Name.setText(receivedPlayer2Name + " (O): 0");

        } else if (receivedGameType.contains("Against Smartphone")) {
            gameLevel.setVisibility(View.VISIBLE);
            player1Name.setText(receivedPlayer1Name + " (X): 0");
            player2Name.setText("Smartphone" + " (O): 0");
        } else {
            gameLevel.setVisibility(View.INVISIBLE);
            player1Name.setText(receivedPlayer1Name + " (X): 0");
            player2Name.setText(receivedPlayer2Name + " (O): 0");
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Call the method to setup board ready to play
        setBoard();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Setup the game board
    private void setBoard() {
        //Initialize the status for all tiles
        //"0": Empty, available to place a mark
        //"1": Occupied by the mark of player 1: X
        //"2": Occupied by the mark of player 2: O
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                //Initialize the status for all tiles to "2", ready to play
                tileStatus[i][j] = 0;//Empty and available to be marked
                btnTile[i][j].setText("");
                btnTile[i][j].setBackgroundColor(Color.WHITE);
                btnTile[i][j].setEnabled(true);
            }
        }
        //Initialize the status of players turn
        player1 = true;
        player2 = false;

        //Initialization an AI
        smartphone = new AI_Player();

        //Set playerTurn TextView visible and set gameResult invisibile
        playerTurn.setVisibility(View.VISIBLE);
        gameResult.setVisibility(View.INVISIBLE);

        /////////////////////////////////////////////////////////////////////////////////////
        //Check which player turn is going and display in TextView
        if (player1) {
            playerTurn.setText("Turn: " + receivedPlayer1Name + " (X)");
        } else if (player2) {
            playerTurn.setText("Turn: " + receivedPlayer2Name + " (O)");
        } else {
            //Errors or Exceptions
            //do nothing
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //markCell method
    public void markCell(String mark, int x, int y) {
        //This method to mark the relevant mark (X or O) and disable the cell
        btnTile[x][y].setText(mark);
        //Disable this cell
        btnTile[x][y].setBackgroundColor(Color.GRAY);
        btnTile[x][y].setEnabled(false);
        if (mark.contains("X")) {
            //Player 1: "X"
            tileStatus[x][y] = 1;
            //Change the turn to Player 2
            player2 = true;
            player1 = false;
        } else if (mark.contains("O")) {
            //Player 2: "O"
            tileStatus[x][y] = 2;
            //Change the turn to Player 1
            player2 = false;
            player1 = true;
        } else {
            //Do nothing
        }

        //Check board to see whether game is over and who is winner
        if (checkBoard()) {
            //Game is Over.
            Toast.makeText(this, "GAME IS OVER!", Toast.LENGTH_SHORT).show();
            //Set playerTurn TextView invisible
            playerTurn.setVisibility(View.INVISIBLE);

            //Disable all the remaining tiles
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    //Disable all tiles
                    btnTile[i][j].setEnabled(false);
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //onClick()
    @Override
    public void onClick(View myView) {
        ////////////////////////////////////////////////////////////////////////////////////////////
        //Check which player turn is going and display in TextView
        if (receivedGameType.contains("Multilple Players")) {
            //Multiple player mode
            if (!player1) {
                //Player 1 is always a human
                playerTurn.setText("Turn: " + receivedPlayer1Name + " (X)");
            } else if (!player2) {
                //Player2 might be a human or AI
                playerTurn.setText("Turn: " + receivedPlayer2Name + " (O)");
            } else {
                //Errors or Exceptions
                //do nothing
            }
        } else {
            //Against Smartphone mode
            //Do nothing
        }


        /////////////////////////////////////////////////////////////////////////////////////
        //Check which tile has been clicked and perform responses
        switch (myView.getId()) {
            case R.id.btnRePlay:
                //Reset the board
                setBoard();
                break;

            case R.id.btnTile1:
                //Check tile status
                if (tileStatus[2][0] == 0) {
                    //Empty tile
                    if (player1) {
                        //Player 1 turn : mark "X"
                        markCell("X", 2, 0);

                        //Check whether it's AI turns right after Player 1's turn
                        if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Easy")) {
                            //Easy_AI takes turn
                            smartphone.EasyAI_takeTurn();
                            Toast.makeText(this, "Easy AI Smartphone", Toast.LENGTH_LONG).show();
                            break;

                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Medium")) {
                            //Medium AI takes turn
                            smartphone.MediumAI_takeTurn();
                            Toast.makeText(this, "Medium AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Hard")) {
                            //Hard AI takes turn
                            smartphone.HardAI_takeTurn();
                            Toast.makeText(this, "Hard AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        }

                    } else if (player2) {
                        //The second player takes turn in Multiple Players mode
                        //Player 2 turn: mark "O"
                    } else {
                        //Errors or Exceptions
                        //Do nothing
                    }
                }
                break;

            case R.id.btnTile2:
                //Check tile status
                if (tileStatus[2][1] == 0) {
                    //Empty tile
                    if (player1) {
                        //Player 1 turn : mark "X"
                        markCell("X", 2, 1);

                        //Check whether it's AI turns right after Player 1's turn
                        if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Easy")) {
                            //Easy_AI takes turn
                            smartphone.EasyAI_takeTurn();
                            Toast.makeText(this, "Easy AI Smartphone", Toast.LENGTH_LONG).show();
                            break;

                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Medium")) {
                            //Medium AI takes turn
                            smartphone.MediumAI_takeTurn();
                            Toast.makeText(this, "Medium AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Hard")) {
                            //Hard AI takes turn
                            smartphone.HardAI_takeTurn();
                            Toast.makeText(this, "Hard AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        }

                    } else if (player2) {
                        //The second player takes turn in Multiple Players mode
                        //Player 2 turn: mark "O"
                        markCell("O", 2, 1);
                    } else {
                        //Errors or Exceptions
                        //Do nothing
                    }
                }
                break;

            case R.id.btnTile3:
                //Check tile status
                if (tileStatus[2][2] == 0) {
                    //Empty tile
                    if (player1) {
                        //Player 1 turn : mark "X"
                        markCell("X", 2, 2);

                        //Check whether it's AI turns right after Player 1's turn
                        if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Easy")) {
                            //Easy_AI takes turn
                            smartphone.EasyAI_takeTurn();
                            Toast.makeText(this, "Easy AI Smartphone", Toast.LENGTH_LONG).show();
                            break;

                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Medium")) {
                            //Medium AI takes turn
                            smartphone.MediumAI_takeTurn();
                            Toast.makeText(this, "Medium AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Hard")) {
                            //Hard AI takes turn
                            smartphone.HardAI_takeTurn();
                            Toast.makeText(this, "Hard AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        }

                    } else if (player2) {
                        //Player 2 turn: mark "O"
                        markCell("O", 2, 2);
                    } else {
                        //Errors or Exceptions
                        //Do nothing
                    }
                }
                break;

            case R.id.btnTile4:
                //Check tile status
                if (tileStatus[1][0] == 0) {
                    //Empty tile
                    if (player1) {
                        //Player 1 turn : mark "X"
                        markCell("X", 1, 0);

                        //Check whether it's AI turns right after Player 1's turn
                        if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Easy")) {
                            //Easy_AI takes turn
                            smartphone.EasyAI_takeTurn();
                            Toast.makeText(this, "Easy AI Smartphone", Toast.LENGTH_LONG).show();
                            break;

                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Medium")) {
                            //Medium AI takes turn
                            smartphone.MediumAI_takeTurn();
                            Toast.makeText(this, "Medium AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Hard")) {
                            //Hard AI takes turn
                            smartphone.HardAI_takeTurn();
                            Toast.makeText(this, "Hard AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        }

                    } else if (player2) {
                        //Player 2 turn: mark "O"
                        markCell("O", 1, 0);
                    } else {
                        //Errors or Exceptions
                        //Do nothing
                    }
                }
                break;

            case R.id.btnTile5:
                //Check tile status
                if (tileStatus[1][1] == 0) {
                    //Empty tile
                    if (player1) {
                        //Player 1 turn : mark "X"
                        markCell("X", 1, 1);

                        //Check whether it's AI turns right after Player 1's turn
                        if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Easy")) {
                            //Easy_AI takes turn
                            smartphone.EasyAI_takeTurn();
                            Toast.makeText(this, "Easy AI Smartphone", Toast.LENGTH_LONG).show();
                            break;

                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Medium")) {
                            //Medium AI takes turn
                            smartphone.MediumAI_takeTurn();
                            Toast.makeText(this, "Medium AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Hard")) {
                            //Hard AI takes turn
                            smartphone.HardAI_takeTurn();
                            Toast.makeText(this, "Hard AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        }
                    } else if (player2) {
                        //Player 2 turn: mark "O"
                        markCell("O", 1, 1);
                    } else {
                        //Errors or Exceptions
                        //Do nothing
                    }
                }
                break;

            case R.id.btnTile6:
                //Check tile status
                if (tileStatus[1][2] == 0) {
                    //Empty tile
                    if (player1) {
                        //Player 1 turn : mark "X"
                        markCell("X", 1, 2);

                        //Check whether it's AI turns right after Player 1's turn
                        if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Easy")) {
                            //Easy_AI takes turn
                            smartphone.EasyAI_takeTurn();
                            Toast.makeText(this, "Easy AI Smartphone", Toast.LENGTH_LONG).show();
                            break;

                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Medium")) {
                            //Medium AI takes turn
                            smartphone.MediumAI_takeTurn();
                            Toast.makeText(this, "Medium AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Hard")) {
                            //Hard AI takes turn
                            smartphone.HardAI_takeTurn();
                            Toast.makeText(this, "Hard AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        }
                    } else if (player2) {
                        //Player 2 turn: mark "O"
                        markCell("O", 1, 2);
                    } else {
                        //Errors or Exceptions
                        //Do nothing
                    }
                }
                break;

            case R.id.btnTile7:
                //Check tile status
                if (tileStatus[0][0] == 0) {
                    //Empty tile
                    if (player1) {
                        //Player 1 turn : mark "X"
                        markCell("X", 0, 0);

                        //Check whether it's AI turns right after Player 1's turn
                        if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Easy")) {
                            //Easy_AI takes turn
                            smartphone.EasyAI_takeTurn();
                            Toast.makeText(this, "Easy AI Smartphone", Toast.LENGTH_LONG).show();
                            break;

                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Medium")) {
                            //Medium AI takes turn
                            smartphone.MediumAI_takeTurn();
                            Toast.makeText(this, "Medium AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Hard")) {
                            //Hard AI takes turn
                            smartphone.HardAI_takeTurn();
                            Toast.makeText(this, "Hard AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        }
                    } else if (player2) {
                        //Player 2 turn: mark "O"
                        markCell("O", 0, 0);
                    } else {
                        //Errors or Exceptions
                        //Do nothing
                    }
                }
                break;

            case R.id.btnTile8:
                //Check tile status
                if (tileStatus[0][1] == 0) {
                    //Empty tile
                    if (player1) {
                        //Player 1 turn : mark "X"
                        markCell("X", 0, 1);

                        //Check whether it's AI turns right after Player 1's turn
                        if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Easy")) {
                            //Easy_AI takes turn
                            smartphone.EasyAI_takeTurn();
                            Toast.makeText(this, "Easy AI Smartphone", Toast.LENGTH_LONG).show();
                            break;

                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Medium")) {
                            //Medium AI takes turn
                            smartphone.MediumAI_takeTurn();
                            Toast.makeText(this, "Medium AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Hard")) {
                            //Hard AI takes turn
                            smartphone.HardAI_takeTurn();
                            Toast.makeText(this, "Hard AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        }
                    } else if (player2) {
                        //Player 2 turn: mark "O"
                        markCell("O", 0, 1);
                    } else {
                        //Errors or Exceptions
                        //Do nothing
                    }
                }
                break;

            case R.id.btnTile9:
                //Check tile status
                if (tileStatus[0][2] == 0) {
                    //Empty tile
                    if (player1) {
                        //Player 1 turn : mark "X"
                        markCell("X", 0, 2);

                        //Check whether it's AI turns right after Player 1's turn
                        if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Easy")) {
                            //Easy_AI takes turn
                            smartphone.EasyAI_takeTurn();
                            Toast.makeText(this, "Easy AI Smartphone", Toast.LENGTH_LONG).show();
                            break;

                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Medium")) {
                            //Medium AI takes turn
                            smartphone.MediumAI_takeTurn();
                            Toast.makeText(this, "Medium AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        } else if (receivedGameType.contains("Against Smartphone") && receivedGameLevel.contains("Hard")) {
                            //Hard AI takes turn
                            smartphone.HardAI_takeTurn();
                            Toast.makeText(this, "Hard AI Smartphone", Toast.LENGTH_LONG).show();
                            break;
                        }
                    } else if (player2) {
                        //Player 2 turn: mark "O"
                        markCell("O", 0, 2);
                    } else {
                        //Errors or Exceptions
                        //Do nothing
                    }
                }
                break;

            default:
                //Errors or Exceptions
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Check the board to see if someone has won
    private boolean checkBoard() {
        boolean gameOver = false;
        //Check whether Player 1 has won
        if ((tileStatus[0][0] == 1 && tileStatus[0][1] == 1 && tileStatus[0][2] == 1)
                || (tileStatus[0][0] == 1 && tileStatus[1][1] == 1 && tileStatus[2][2] == 1)
                || (tileStatus[0][0] == 1 && tileStatus[1][0] == 1 && tileStatus[2][0] == 1)
                || (tileStatus[0][1] == 1 && tileStatus[1][1] == 1 && tileStatus[2][1] == 1)
                || (tileStatus[0][2] == 1 && tileStatus[1][2] == 1 && tileStatus[2][2] == 1)
                || (tileStatus[0][2] == 1 && tileStatus[1][1] == 1 && tileStatus[2][0] == 1)
                || (tileStatus[1][0] == 1 && tileStatus[1][1] == 1 && tileStatus[1][2] == 1)
                || (tileStatus[2][0] == 1 && tileStatus[2][1] == 1 && tileStatus[2][2] == 1)) {

            //Display the text message to gameResult TextView
            gameResult.setVisibility(View.VISIBLE);
            gameResult.setText("GAME OVER! " + receivedPlayer1Name + " won.");
            gameOver = true;

            //Increase the score of winner
            String player1Score = player1Name.getText().toString();
            int Score = Integer.parseInt(player1Score.substring(player1Score.lastIndexOf(" ") + 1));
            player1Name.setText(receivedPlayer1Name + " (X): " + (Score + 1));

            //Check whether Player 2 has won
        } else if ((tileStatus[0][0] == 2 && tileStatus[0][1] == 2 && tileStatus[0][2] == 2)
                || (tileStatus[0][0] == 2 && tileStatus[1][1] == 2 && tileStatus[2][2] == 2)
                || (tileStatus[0][0] == 2 && tileStatus[1][0] == 2 && tileStatus[2][0] == 2)
                || (tileStatus[0][1] == 2 && tileStatus[1][1] == 2 && tileStatus[2][1] == 2)
                || (tileStatus[0][2] == 2 && tileStatus[1][2] == 2 && tileStatus[2][2] == 2)
                || (tileStatus[0][2] == 2 && tileStatus[1][1] == 2 && tileStatus[2][0] == 2)
                || (tileStatus[1][0] == 2 && tileStatus[1][1] == 2 && tileStatus[1][2] == 2)
                || (tileStatus[2][0] == 2 && tileStatus[2][1] == 2 && tileStatus[2][2] == 2)) {

            //Display the text message to gameResult TextView
            gameResult.setVisibility(View.VISIBLE);
            gameResult.setText("GAME OVER! " + receivedPlayer2Name + " won.");
            gameOver = true;

            //Increase the score of winner
            String player2Score = player2Name.getText().toString();
            int Score = Integer.parseInt(player2Score.substring(player2Score.lastIndexOf(" ") + 1));
            player2Name.setText(receivedPlayer2Name + " (O): " + (Score + 1));

        } else {
            //Check whether all cells have been marked up
            boolean boardEmpty = false;
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    if (tileStatus[i][j] == 0) {
                        boardEmpty = true;
                        break;
                    }
                }
            }
            if (!boardEmpty) {
                //No cell is empty
                gameResult.setVisibility(View.VISIBLE);
                gameResult.setText("GAME OVER! It's a draw");
                gameOver = true;
            }
        }
        //Return value
        return gameOver;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //AI_Player
    private class AI_Player {
        ////////////////////////////////////////////////////////////////////////////////////////////
        public void EasyAI_takeTurn() {
            //The easy AI strategy is to place the sign (O) on the first empty cell in this order:
            // the center, one of the four corners, one of the four sides.
            if (tileStatus[1][1] == 0) {
                //The center
                markTile(1, 1);
            } else if (tileStatus[0][0] == 0) {
                //First corners: top, left
                markTile(0, 0);
            } else if (tileStatus[2][2] == 0) {
                //Second corners: bottom right
                markTile(2, 2);
            } else if (tileStatus[0][2] == 0) {
                //Third corners: top, right
                markTile(0, 2);
            } else if (tileStatus[2][0] == 0) {
                //Fourth corners: bottom, left
                markTile(2, 0);
            } else if (tileStatus[0][1] == 0) {
                //First side
                markTile(0, 1);
            } else if (tileStatus[1][0] == 0) {
                //Second side
                markTile(1, 0);
            } else if (tileStatus[1][2] == 0) {
                //Third side
                markTile(1, 2);
            } else if (tileStatus[2][1] == 0) {
                //Fourth side
                markTile(2, 1);
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        public void MediumAI_takeTurn() {
            //The Medium AI strategy  considers to place the sign (O) on the empty cell
            //First step: Search through all the empty cells and block the opponent's win if it exists
            if (tileStatus[0][0] == 0 &&
                    ((tileStatus[0][1] == 1 && tileStatus[0][2] == 1)
                            || (tileStatus[1][1] == 1 && tileStatus[2][2] == 1)
                            || (tileStatus[1][0] == 1 && tileStatus[2][0] == 1))) {
                //Opponent is going to win when placing the next Move (X) in Cell[0][0], so need
                //to block this cell
                markTile(0, 0);
            } else if (tileStatus[0][1] == 0 &&
                    ((tileStatus[0][0] == 1 && tileStatus[0][2] == 1)
                            || (tileStatus[1][1] == 1 && tileStatus[2][1] == 1))) {
                //Opponent is going to win when placing the next Move (X) in Cell[0][1], so need
                //to block this cell
                markTile(0, 1);
            } else if (tileStatus[0][2] == 0 &&
                    ((tileStatus[0][0] == 1 && tileStatus[0][1] == 1)
                            || (tileStatus[1][2] == 1 && tileStatus[2][2] == 1)
                            || (tileStatus[1][1] == 1 && tileStatus[2][0] == 1))) {
                //Opponent is going to win when placing the next Move (X) in Cell[0][2], so need
                //to block this cell
                markTile(0, 2);
            } else if (tileStatus[1][0] == 0 &&
                    ((tileStatus[1][1] == 1 && tileStatus[1][2] == 1)
                            || (tileStatus[0][0] == 1 && tileStatus[2][0] == 1))) {
                //Opponent is going to win when placing the next Move (X) in Cell[1][0], so need
                //to block this cell
                markTile(1, 0);
            } else if (tileStatus[1][1] == 0 &&
                    ((tileStatus[0][0] == 1 && tileStatus[2][2] == 1)
                            || (tileStatus[0][1] == 1 && tileStatus[2][1] == 1)
                            || (tileStatus[0][2] == 1 && tileStatus[2][0] == 1)
                            || (tileStatus[1][0] == 1 && tileStatus[1][2] == 1))) {
                //Opponent is going to win when placing the next Move (X) in Cell[1][1], so need
                //to block this cell
                markTile(1, 1);
            } else if (tileStatus[1][2] == 0 &&
                    ((tileStatus[0][2] == 1 && tileStatus[2][2] == 1)
                            || (tileStatus[1][0] == 1 && tileStatus[1][1] == 1))) {
                //Opponent is going to win when placing the next Move (X) in Cell[1][2], so need
                //to block this cell
                markTile(1, 2);
            } else if (tileStatus[2][0] == 0 &&
                    ((tileStatus[0][0] == 1 && tileStatus[1][0] == 1)
                            || (tileStatus[0][2] == 1 && tileStatus[1][1] == 1)
                            || (tileStatus[2][1] == 1 && tileStatus[2][2] == 1))) {
                //Opponent is going to win when placing the next Move (X) in Cell[2][0], so need
                //to block this cell
                markTile(2, 0);
            } else if (tileStatus[2][1] == 0 &&
                    ((tileStatus[0][1] == 1 && tileStatus[1][1] == 1)
                            || (tileStatus[2][0] == 1 && tileStatus[2][2] == 1))) {
                //Opponent is going to win when placing the next Move (X) in Cell[2][1], so need
                //to block this cell
                markTile(2, 1);
            } else if (tileStatus[2][2] == 0 &&
                    ((tileStatus[0][0] == 1 && tileStatus[1][1] == 1)
                            || (tileStatus[0][2] == 1 && tileStatus[1][2] == 1)
                            || (tileStatus[2][0] == 1 && tileStatus[2][1] == 1))) {
                //Opponent is going to win when placing the next Move (X) in Cell[2][2], so need
                //to block this cell
                markTile(2, 2);
            } else {
                //Second step: If there is no possibility of opponent winning,place the mark (O)
                // as indicated in the Easy AI strategy: The smartphone AI is to place the sign (O)
                // on the first empty cell in this order:
                // the center, one of the four corners, one of the four sides.
                if (tileStatus[1][1] == 0) {
                    //The center
                    markTile(1, 1);
                } else if (tileStatus[0][0] == 0) {
                    //First corners: top, left
                    markTile(0, 0);
                } else if (tileStatus[2][2] == 0) {
                    //Second corners: bottom right
                    markTile(2, 2);
                } else if (tileStatus[0][2] == 0) {
                    //Third corners: top, right
                    markTile(0, 2);
                } else if (tileStatus[2][0] == 0) {
                    //Fourth corners: bottom, left
                    markTile(2, 0);
                } else if (tileStatus[0][1] == 0) {
                    //First side
                    markTile(0, 1);
                } else if (tileStatus[1][0] == 0) {
                    //Second side
                    markTile(1, 0);
                } else if (tileStatus[1][2] == 0) {
                    //Third side
                    markTile(1, 2);
                } else if (tileStatus[2][1] == 0) {
                    //Fourth side
                    markTile(2, 1);
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        public void HardAI_takeTurn() {
            //The Hard AI strategy considers to place the sign (O) on the "best" empty cell

            //First step: take the MOVE to WIN: Search through all empty cells to see any chance of winning for AI.
            // If the winning chance exists, place the mark (O) in this cell;
            if (tileStatus[0][0] == 0 &&
                    ((tileStatus[0][1] == 2 && tileStatus[0][2] == 2)
                            || (tileStatus[1][1] == 2 && tileStatus[2][2] == 2)
                            || (tileStatus[1][0] == 2 && tileStatus[2][0] == 2))) {
                //Opponent is going to win when placing the next Move (X) in Cell[0][0], so need
                //to block this cell
                markTile(0, 0);
            } else if (tileStatus[0][1] == 0 &&
                    ((tileStatus[0][0] == 2 && tileStatus[0][2] == 2)
                            || (tileStatus[1][1] == 2 && tileStatus[2][1] == 2))) {
                //Opponent is going to win when placing the next Move (X) in Cell[0][1], so need
                //to block this cell
                markTile(0, 1);
            } else if (tileStatus[0][2] == 0 &&
                    ((tileStatus[0][0] == 2 && tileStatus[0][1] == 2)
                            || (tileStatus[1][2] == 2 && tileStatus[2][2] == 2)
                            || (tileStatus[1][1] == 2 && tileStatus[2][0] == 2))) {
                //Opponent is going to win when placing the next Move (X) in Cell[0][2], so need
                //to block this cell
                markTile(0, 2);
            } else if (tileStatus[1][0] == 0 &&
                    ((tileStatus[1][1] == 2 && tileStatus[1][2] == 2)
                            || (tileStatus[0][0] == 2 && tileStatus[2][0] == 2))) {
                //Opponent is going to win when placing the next Move (X) in Cell[1][0], so need
                //to block this cell
                markTile(1, 0);
            } else if (tileStatus[1][1] == 0 &&
                    ((tileStatus[0][0] == 2 && tileStatus[2][2] == 2)
                            || (tileStatus[0][1] == 2 && tileStatus[2][1] == 2)
                            || (tileStatus[0][2] == 2 && tileStatus[2][0] == 2)
                            || (tileStatus[1][0] == 2 && tileStatus[1][2] == 2))) {
                //Opponent is going to win when placing the next Move (X) in Cell[1][1], so need
                //to block this cell
                markTile(1, 1);
            } else if (tileStatus[1][2] == 0 &&
                    ((tileStatus[0][2] == 2 && tileStatus[2][2] == 2)
                            || (tileStatus[1][0] == 2 && tileStatus[1][1] == 2))) {
                //Opponent is going to win when placing the next Move (X) in Cell[1][2], so need
                //to block this cell
                markTile(1, 2);
            } else if (tileStatus[2][0] == 0 &&
                    ((tileStatus[0][0] == 2 && tileStatus[1][0] == 2)
                            || (tileStatus[0][2] == 2 && tileStatus[1][1] == 2)
                            || (tileStatus[2][1] == 2 && tileStatus[2][2] == 2))) {
                //Opponent is going to win when placing the next Move (X) in Cell[2][0], so need
                //to block this cell
                markTile(2, 0);
            } else if (tileStatus[2][1] == 0 &&
                    ((tileStatus[0][1] == 2 && tileStatus[1][1] == 2)
                            || (tileStatus[2][0] == 2 && tileStatus[2][2] == 2))) {
                //Opponent is going to win when placing the next Move (X) in Cell[2][1], so need
                //to block this cell
                markTile(2, 1);
            } else if (tileStatus[2][2] == 0 &&
                    ((tileStatus[0][0] == 2 && tileStatus[1][1] == 2)
                            || (tileStatus[0][2] == 2 && tileStatus[1][2] == 2)
                            || (tileStatus[2][0] == 2 && tileStatus[2][1] == 2))) {
                //Opponent is going to win when placing the next Move (X) in Cell[2][2], so need
                //to block this cell
                markTile(2, 2);


            } else {
                //Second step: Defensive – take the MOVE to block the opponent’s win: as seen in Medium AI strategy
                // Search through all the empty cells and block the opponent's win if it exists
                if (tileStatus[0][0] == 0 &&
                        ((tileStatus[0][1] == 1 && tileStatus[0][2] == 1)
                                || (tileStatus[1][1] == 1 && tileStatus[2][2] == 1)
                                || (tileStatus[1][0] == 1 && tileStatus[2][0] == 1))) {
                    //Opponent is going to win when placing the next Move (X) in Cell[0][0], so need
                    //to block this cell
                    markTile(0, 0);
                } else if (tileStatus[0][1] == 0 &&
                        ((tileStatus[0][0] == 1 && tileStatus[0][2] == 1)
                                || (tileStatus[1][1] == 1 && tileStatus[2][1] == 1))) {
                    //Opponent is going to win when placing the next Move (X) in Cell[0][1], so need
                    //to block this cell
                    markTile(0, 1);
                } else if (tileStatus[0][2] == 0 &&
                        ((tileStatus[0][0] == 1 && tileStatus[0][1] == 1)
                                || (tileStatus[1][2] == 1 && tileStatus[2][2] == 1)
                                || (tileStatus[1][1] == 1 && tileStatus[2][0] == 1))) {
                    //Opponent is going to win when placing the next Move (X) in Cell[0][2], so need
                    //to block this cell
                    markTile(0, 2);
                } else if (tileStatus[1][0] == 0 &&
                        ((tileStatus[1][1] == 1 && tileStatus[1][2] == 1)
                                || (tileStatus[0][0] == 1 && tileStatus[2][0] == 1))) {
                    //Opponent is going to win when placing the next Move (X) in Cell[1][0], so need
                    //to block this cell
                    markTile(1, 0);
                } else if (tileStatus[1][1] == 0 &&
                        ((tileStatus[0][0] == 1 && tileStatus[2][2] == 1)
                                || (tileStatus[0][1] == 1 && tileStatus[2][1] == 1)
                                || (tileStatus[0][2] == 1 && tileStatus[2][0] == 1)
                                || (tileStatus[1][0] == 1 && tileStatus[1][2] == 1))) {
                    //Opponent is going to win when placing the next Move (X) in Cell[1][1], so need
                    //to block this cell
                    markTile(1, 1);
                } else if (tileStatus[1][2] == 0 &&
                        ((tileStatus[0][2] == 1 && tileStatus[2][2] == 1)
                                || (tileStatus[1][0] == 1 && tileStatus[1][1] == 1))) {
                    //Opponent is going to win when placing the next Move (X) in Cell[1][2], so need
                    //to block this cell
                    markTile(1, 2);
                } else if (tileStatus[2][0] == 0 &&
                        ((tileStatus[0][0] == 1 && tileStatus[1][0] == 1)
                                || (tileStatus[0][2] == 1 && tileStatus[1][1] == 1)
                                || (tileStatus[2][1] == 1 && tileStatus[2][2] == 1))) {
                    //Opponent is going to win when placing the next Move (X) in Cell[2][0], so need
                    //to block this cell
                    markTile(2, 0);
                } else if (tileStatus[2][1] == 0 &&
                        ((tileStatus[0][1] == 1 && tileStatus[1][1] == 1)
                                || (tileStatus[2][0] == 1 && tileStatus[2][2] == 1))) {
                    //Opponent is going to win when placing the next Move (X) in Cell[2][1], so need
                    //to block this cell
                    markTile(2, 1);
                } else if (tileStatus[2][2] == 0 &&
                        ((tileStatus[0][0] == 1 && tileStatus[1][1] == 1)
                                || (tileStatus[0][2] == 1 && tileStatus[1][2] == 1)
                                || (tileStatus[2][0] == 1 && tileStatus[2][1] == 1))) {
                    //Opponent is going to win when placing the next Move (X) in Cell[2][2], so need
                    //to block this cell
                    markTile(2, 2);
                } else {
                    //Third step: If there is no possibility of opponent winning,place the mark (O)
                    // as indicated in the Easy AI strategy: The smartphone AI is to place the sign (O)
                    // on the first empty cell in this order:
                    // the center, one of the four corners, one of the four sides.
                    if (tileStatus[1][1] == 0) {
                        //The center
                        markTile(1, 1);
                    } else if (tileStatus[0][0] == 0) {
                        //First corners: top, left
                        markTile(0, 0);
                    } else if (tileStatus[2][2] == 0) {
                        //Second corners: bottom right
                        markTile(2, 2);
                    } else if (tileStatus[0][2] == 0) {
                        //Third corners: top, right
                        markTile(0, 2);
                    } else if (tileStatus[2][0] == 0) {
                        //Fourth corners: bottom, left
                        markTile(2, 0);
                    } else if (tileStatus[0][1] == 0) {
                        //First side
                        markTile(0, 1);
                    } else if (tileStatus[1][0] == 0) {
                        //Second side
                        markTile(1, 0);
                    } else if (tileStatus[1][2] == 0) {
                        //Third side
                        markTile(1, 2);
                    } else if (tileStatus[2][1] == 0) {
                        //Fourth side
                        markTile(2, 1);
                    }
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        private void markTile(int x, int y) {
            btnTile[x][y].setText("O");
            btnTile[x][y].setBackgroundColor(Color.GRAY);
            tileStatus[x][y] = 2;
            player2 = false;
            player1 = true;

            //Check board to see whether game is over and who is winner
            if (checkBoard()) {
                //Set playerTurn TextView invisible
                playerTurn.setVisibility(View.INVISIBLE);
                //Disable all the remaining tiles
                for (int i = 0; i <= 2; i++) {
                    for (int j = 0; j <= 2; j++) {
                        //Disable all tiles
                        btnTile[i][j].setEnabled(false);
                    }
                }
            }
        }
    }

}
