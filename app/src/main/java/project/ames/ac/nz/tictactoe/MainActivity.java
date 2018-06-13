package project.ames.ac.nz.tictactoe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /////////////////////////////////////////////////////////////////////////////////////
    //Declare global variables and objects
    private RadioButton multiplePlayer, againstSmartphone;
    private RadioButton easyLevel, mediumLevel, hardLevel;
    private EditText player1Name, player2Name;
    private Button playBtn;
    private String player1NameString, player2NameString;
    private String gameType = "Multilple Players", gameLevel ="Easy Level";

    /////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Find control elements and do casting
        multiplePlayer = (RadioButton) findViewById(R.id.multiplePlayer);
        againstSmartphone = (RadioButton) findViewById(R.id.againstSmartphone);

        player1Name = (EditText) findViewById(R.id.player1Name);
        player2Name = (EditText) findViewById(R.id.player2Name);

        easyLevel = (RadioButton) findViewById(R.id.easyLevel);
        mediumLevel = (RadioButton) findViewById(R.id.mediumLevel);
        hardLevel = (RadioButton) findViewById(R.id.hardLevel);

        playBtn = (Button) findViewById(R.id.playBtn);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Set listeners for these Widget control elements
        playBtn.setOnClickListener(this);

        multiplePlayer.setOnClickListener(this);
        againstSmartphone.setOnClickListener(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Check which game type has been selected
        if (multiplePlayer.isChecked()) {
              //Multiple players types has been chosen
                //Enable users to enter player nicknames
                player1Name.setHint("Enter Player 1 nickname: ");
                player1Name.setEnabled(true);//Enable users to enter player 1 name
                player2Name.setText("");
                player2Name.setHint("Enter Player 2 nickname: ");
                player2Name.setEnabled(true);//Enable users to enter player 2 name
                //player2Name.setFocusable(true);//Enable users to enter player 2 name

                //Disable the Game level options
                easyLevel.setEnabled(false);
                mediumLevel.setEnabled(false);
                hardLevel.setEnabled(false);
        } else {
            //Play against Smartphone has been chosen
              //Play against Smartphone has been chosen
                player1Name.setHint("Enter your nickname: ");
                player1Name.setEnabled(true);//Enable users to enter only Player 1 name
                player2Name.setText("Smartphone");
                player2Name.setEnabled(false);//Disable the player 2 name
                //Enable the Game level options
                easyLevel.setEnabled(true);
                mediumLevel.setEnabled(true);
                hardLevel.setEnabled(true);
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////
    //onClick()
    @Override
    public void onClick(View myView) {
        /////////////////////////////////////////////////////////////////////////////////////
        //Check which Views has been clicked and perform responses
        switch (myView.getId()) {
            case R.id.multiplePlayer:
                //Multiple players types has been chosen
                //Enable users to enter player nicknames
                player1Name.setHint("Enter Player 1 nickname: ");
                player1Name.setEnabled(true);//Enable users to enter player 1 name
                player2Name.setText("");
                player2Name.setHint("Enter Player 2 nickname: ");
                player2Name.setEnabled(true);//Enable users to enter player 2 name
                //player2Name.setFocusable(true);//Enable users to enter player 2 name

                //Disable the Game level options
                easyLevel.setEnabled(false);
                mediumLevel.setEnabled(false);
                hardLevel.setEnabled(false);
                break;

            case R.id.againstSmartphone:
                //Play against Smartphone has been chosen
                player1Name.setHint("Enter your nickname: ");
                player1Name.setEnabled(true);//Enable users to enter only Player 1 name
                player2Name.setText("Smartphone");
                player2Name.setEnabled(false);//Disable the player 2 name
                //Enable the Game level options
                easyLevel.setEnabled(true);
                mediumLevel.setEnabled(true);
                hardLevel.setEnabled(true);
                break;

            case R.id.playBtn:
                //When the Play button has been clicked,
                //Retrieve: game type
                if (multiplePlayer.isChecked()) {
                    //Multiple players types has been chosen
                    gameType = "Multilple Players";
                } else {
                    //Play against Smartphone has been chosen
                    gameType = "Against Smartphone";
                }

                //Retrieve: player names
                player1NameString = player1Name.getText().toString();
                player2NameString = player2Name.getText().toString();

                //Retrieve: game level if any
                if (hardLevel.isChecked()) {
                    //Hard level has been chosen
                    gameLevel = "Hard Level";
                } else if (mediumLevel.isChecked()) {
                    //Medium level has been chosen
                    gameLevel = "Medium Level";
                } else {
                    //Easy level has been chosen
                    gameLevel = "Easy Level";
                }

                //Use EXPLICIT Intent to open GameActivity
                //Add Extra data: (1) GameType, (2) GameLevel, (3) player1Name, (4) Player2Name
                Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                gameIntent.putExtra("GameType", gameType);
                gameIntent.putExtra("GameLevel", gameLevel);
                gameIntent.putExtra("Player1Name", player1NameString);
                gameIntent.putExtra("Player2Name", player2NameString);
                startActivity(gameIntent);
                break;

            default:
                //Errors or Exceptions
                //
                break;
        }
    }
}
