package com.example.besednik;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String targetWord = "APPLE"; // Initial target word
    private final String[] wordList = {"APPLE", "BEACH", "BRAIN", "BREAD", "BRUSH", "CHAIR", "CHEST", "CHORD", "CLICK", "CLOCK", "CLOUD", "DANCE", "DIARY", "DRINK", "DRIVE", "EARTH", "FEAST", "FIELD", "FLAME", "FLOUR", "FRUIT", "GLASS", "GRAPE", "GREEN", "GRIND", "HEART", "HOUSE", "JUICE", "LIGHT", "LEMON", "MELON", "MONEY", "MUSIC", "NIGHT", "OCEAN", "PARTY", "PIANO", "PILOT", "PLANE", "PHONE", "PIZZA", "PLANT", "RADIO", "RIVER", "ROBOT", "SHIRT", "SHOES", "SMILE", "SNAKE", "SPACE", "SPOON", "STORM", "TABLE", "TIGER", "TOAST", "TOUCH", "TRAIN", "TRUCK", "VOICE", "WATER", "WATCH", "WHALE", "WORLD", "WRITE", "YOUTH", "ZEBRA"};

    private int currentRow = 0;
    private int currentCol = 0;
    private final TextView[][] grid = new TextView[6][5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initGrid();
        initKeyboard();
        pickRandomWord();
    }

    private void pickRandomWord() {
        Random random = new Random();
        targetWord = wordList[random.nextInt(wordList.length)];
    }

    private void initGrid() {
        for (int r = 0; r < 6; r++) {
            int rowId = getResources().getIdentifier("row_" + r, "id", getPackageName());
            View rowView = findViewById(rowId);
            for (int c = 0; c < 5; c++) {
                int cellId = getResources().getIdentifier("cell_" + c, "id", getPackageName());
                grid[r][c] = rowView.findViewById(cellId);
            }
        }
    }

    private void initKeyboard() {
        String keys = "QWERTYUIOPASDFGHJKLZXCVBNM";
        for (char c : keys.toCharArray()) {
            int keyId = getResources().getIdentifier("key_" + String.valueOf(c).toLowerCase(), "id", getPackageName());
            Button btn = findViewById(keyId);
            if (btn != null) {
                btn.setOnClickListener(v -> onKeyPress(c));
            }
        }

        findViewById(R.id.key_enter).setOnClickListener(v -> onEnterPress());
        findViewById(R.id.key_del).setOnClickListener(v -> onDeletePress());
    }

    private void onKeyPress(char letter) {
        if (currentCol < 5 && currentRow < 6) {
            TextView cell = grid[currentRow][currentCol];
            cell.setText(String.valueOf(letter));

            // --- POP ANIMATION ---
            cell.animate().scaleX(1.15f).scaleY(1.15f).setDuration(100)
                    .withEndAction(() -> cell.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start())
                    .start();
            // ---------------------

            currentCol++;
        }
    }

    private void onDeletePress() {
        if (currentCol > 0) {
            currentCol--;
            grid[currentRow][currentCol].setText("");
        }
    }

    private void onEnterPress() {
        if (currentCol < 5) {
            Toast.makeText(this, "Not enough letters", Toast.LENGTH_SHORT).show();
            return;
        }

        String guess = "";
        for (int c = 0; c < 5; c++) {
            guess += grid[currentRow][c].getText().toString();
        }

        checkGuess(guess);

        if (guess.equals(targetWord)) {
            showGameOverDialog(true);
        } else if (currentRow == 5) {
            showGameOverDialog(false);
        } else {
            currentRow++;
            currentCol = 0;
        }
    }

    private void checkGuess(String guess) {
        boolean[] targetMatched = new boolean[5];
        boolean[] guessMatched = new boolean[5];

        // First pass: Find correct letters (Green)
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == targetWord.charAt(i)) {
                grid[currentRow][i].setBackgroundResource(R.drawable.cell_background_correct);
                targetMatched[i] = true;
                guessMatched[i] = true;
            }
        }

        // Second pass: Find present letters (Yellow)
        for (int i = 0; i < 5; i++) {
            if (!guessMatched[i]) {
                for (int j = 0; j < 5; j++) {
                    if (!targetMatched[j] && guess.charAt(i) == targetWord.charAt(j)) {
                        grid[currentRow][i].setBackgroundResource(R.drawable.cell_background_present);
                        targetMatched[j] = true;
                        guessMatched[i] = true;
                        break;
                    }
                }
            }
        }

        // Third pass: Mark remaining as absent (Gray)
        for (int i = 0; i < 5; i++) {
            if (!guessMatched[i]) {
                grid[currentRow][i].setBackgroundResource(R.drawable.cell_background_absent);
            }
        }
    }

    private void showGameOverDialog(boolean won) {
        String title = won ? "Splendid!" : "Game Over";
        String message = won ? "You guessed the word: " + targetWord : "The word was: " + targetWord;

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message + "\n\nDo you want to play again?")
                .setPositiveButton("Play Again", (dialog, which) -> resetGame())
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .setCancelable(false)
                .show();

        disableKeyboard();
    }

    private void resetGame() {
        currentRow = 0;
        currentCol = 0;
        pickRandomWord();

        // Reset all grid cells
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
                grid[r][c].setText("");
                grid[r][c].setBackgroundResource(R.drawable.cell_background);
            }
        }

        // Re-enable the custom keyboard
        enableViewHierarchy(findViewById(R.id.keyboard_container));
    }

    private void disableKeyboard() {
        disableViewHierarchy(findViewById(R.id.keyboard_container));
    }

    private void disableViewHierarchy(View v) {
        v.setEnabled(false);
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                disableViewHierarchy(vg.getChildAt(i));
            }
        }
    }

    private void enableViewHierarchy(View v) {
        v.setEnabled(true);
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                enableViewHierarchy(vg.getChildAt(i));
            }
        }
    }
}