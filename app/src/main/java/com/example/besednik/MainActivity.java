package com.example.besednik;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String targetWord = "POTOK"; // Initial target word in Slovenian
    private final String[] wordList = {
            // originalne
            "BREZA", "CESTA", "KROVI", "MATER", "OTROK",
            "SONCE", "TRAVA", "VETER", "ZAKON", "POTOK",
            "HRANA", "MESTO", "SREČA", "NAROD", "OBRAZ",
            "POLJE", "STENA", "KOTEL", "IGRAČ",
            // narava
            "OBLAK", "VIHAR", "MEGLA", "SNEŽAK", "MORJE",
            "GOZDA", "SKALA", "ZEMLJA", "REKA", "DOLINA",
            "DREVO", "HRIBI", "PLAŽA", "SOLZA", "JESEN",
            // živali
            "KRAVA", "MAČKA", "MIŠKA", "MAČEK", "ŽIVAL",
            "SRAKA", "KOMAR", "OPICA", "LOVEC", "RIBIČ",
            "VOLKI",
            // dom & predmeti
            "TORBA", "METLA", "VRATA", "MREŽA", "KOCKA",
            "KLIMA", "PAPIR", "PUŠKA", "ŽLICA", "VEDRO",
            "VESLO", "MASKA", "TABLA", "PALMA", "SVEČK",
            // hrana & pijača
            "MLEKO", "SLIVA", "POPER", "PASTA", "KRONA",
            "SIRUP", "ČESEN", "SLAMA", "ZELJE", "SADJE",
            // ljudje & družba
            "SOSED", "VOJAK", "KUHAR", "ŠOLAR", "ŠOFER",
            "VARUH", "STRIC", "NEČAK", "REVEŽ", "NOREC",
            // dejanja & stanja
            "STRAH", "TREMA", "SANJE", "SMOLA", "GROZA",
            "ZMAGA", "POGUM", "TREBA", "SLAST", "NERED",
            // ostalo
            "VLADA", "VLOGA", "RAČUN", "URNIK", "USODA",
            "TOŽBA", "SPLET", "SEJEM", "POSEL", "OBROK",
            "OBISK", "KORAK", "VIDEZ", "ZGLED", "ZLATO",
            "ŽAREK", "ŽIVAL", "ŠPORT", "TEČAJ", "TEKAČ",
            "VIHAR", "ZALIV", "ZAVOJ", "ZANKA", "VENEC",
            "MOTIV", "RADIO", "ROBOT", "TUNEL", "ULICA",
    };
    private int currentRow = 0;
    private int currentCol = 0;
    private final TextView[][] grid = new TextView[6][5];
    private final Map<Character, Button> keyboardButtons = new HashMap<>();
    private final Map<Character, Integer> letterStatuses = new HashMap<>(); // 1: absent, 2: present, 3: correct
    private final Set<String> dictionary = new HashSet<>();

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

        loadDictionary();
        initGrid();
        initKeyboard();
        pickRandomWord();
    }

    private void loadDictionary() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("words5.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.trim().toUpperCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Napaka pri nalaganju slovarja", Toast.LENGTH_LONG).show();
        }
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
        String keys = "QWERTZUIOPŠASDFGHJKLČŽYXCVBNM";
        for (char c : keys.toCharArray()) {
            int keyId = getResources().getIdentifier("key_" + String.valueOf(c).toLowerCase(), "id", getPackageName());
            Button btn = findViewById(keyId);
            if (btn != null) {
                keyboardButtons.put(c, btn);
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
            Toast.makeText(this, getString(R.string.not_enough_letters), Toast.LENGTH_SHORT).show();
            return;
        }

        String guess = "";
        for (int c = 0; c < 5; c++) {
            guess += grid[currentRow][c].getText().toString();
        }

        if (!dictionary.isEmpty() && !dictionary.contains(guess.toUpperCase())) {
            shakeRow();
            Toast.makeText(this, "Beseda ne obstaja", Toast.LENGTH_SHORT).show();
            return;
        }

        disableKeyboard();

        int[] statuses = calculateGuessStatuses(guess);
        animateGuessReveal(statuses, guess);
    }

    private void shakeRow() {
        int rowId = getResources().getIdentifier("row_" + currentRow, "id", getPackageName());
        View rowView = findViewById(rowId);
        if (rowView != null) {
            rowView.animate()
                    .translationX(20).setDuration(50)
                    .withEndAction(() -> rowView.animate().translationX(-20).setDuration(50)
                            .withEndAction(() -> rowView.animate().translationX(0).setDuration(50).start())
                            .start())
                    .start();
        }
    }

    private int[] calculateGuessStatuses(String guess) {
        int[] statuses = new int[5]; // 1: absent, 2: present, 3: correct
        boolean[] targetMatched = new boolean[5];
        boolean[] guessMatched = new boolean[5];

        // First pass: Find correct letters (Green)
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == targetWord.charAt(i)) {
                statuses[i] = 3;
                targetMatched[i] = true;
                guessMatched[i] = true;
            }
        }

        // Second pass: Find present letters (Yellow)
        for (int i = 0; i < 5; i++) {
            if (!guessMatched[i]) {
                char c = guess.charAt(i);
                for (int j = 0; j < 5; j++) {
                    if (!targetMatched[j] && c == targetWord.charAt(j)) {
                        statuses[i] = 2;
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
                statuses[i] = 1;
            }
        }
        return statuses;
    }

    private void animateGuessReveal(int[] statuses, String guess) {
        long delay = 250;
        for (int i = 0; i < 5; i++) {
            final int index = i;
            TextView cell = grid[currentRow][index];

            cell.animate()
                    .rotationX(90)
                    .setDuration(250)
                    .setStartDelay(i * delay)
                    .withEndAction(() -> {
                        int status = statuses[index];
                        if (status == 3) cell.setBackgroundResource(R.drawable.cell_background_correct);
                        else if (status == 2) cell.setBackgroundResource(R.drawable.cell_background_present);
                        else if (status == 1) cell.setBackgroundResource(R.drawable.cell_background_absent);

                        updateKeyboardStatus(guess.charAt(index), status);

                        cell.setRotationX(-90);
                        cell.animate()
                                .rotationX(0)
                                .setDuration(250)
                                .setStartDelay(0)
                                .withEndAction(() -> {
                                    if (index == 4) {
                                        finalizeGuess(guess);
                                    }
                                })
                                .start();
                    })
                    .start();
        }
    }

    private void finalizeGuess(String guess) {
        if (guess.equalsIgnoreCase(targetWord)) {
            animateWin();
        } else if (currentRow == 5) {
            showGameOverDialog(false);
        } else {
            currentRow++;
            currentCol = 0;
            enableKeyboard();
        }
    }

    private void animateWin() {
        for (int i = 0; i < 5; i++) {
            final int index = i;
            TextView cell = grid[currentRow][i];
            cell.animate()
                    .translationY(-50)
                    .setDuration(200)
                    .setStartDelay(i * 100)
                    .withEndAction(() -> {
                        cell.animate()
                                .translationY(0)
                                .setDuration(200)
                                .withEndAction(() -> {
                                    if (index == 4) {
                                        showGameOverDialog(true);
                                    }
                                })
                                .start();
                    })
                    .start();
        }
    }

    private void enableKeyboard() {
        enableViewHierarchy(findViewById(R.id.keyboard_container));
    }

    private void updateKeyboardStatus(char letter, int status) {
        Integer currentStatus = letterStatuses.get(letter);
        if (currentStatus == null || status > currentStatus) {
            letterStatuses.put(letter, status);
            Button btn = keyboardButtons.get(letter);
            if (btn != null) {
                if (status == 3) btn.setBackgroundResource(R.drawable.cell_background_correct);
                else if (status == 2) btn.setBackgroundResource(R.drawable.cell_background_present);
                else if (status == 1) btn.setBackgroundResource(R.drawable.cell_background_absent);
            }
        }
    }

    private void showGameOverDialog(boolean won) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_game_over, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Nastavitev prozornega ozadja, da se vidijo zaobljeni robovi CardView-a
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        TextView tvTitleClone = dialogView.findViewById(R.id.tv_dialog_title_clone);
        TextView tvMessage = dialogView.findViewById(R.id.tv_dialog_message);
        TextView tvSubMessage = dialogView.findViewById(R.id.tv_dialog_submessage);
        Button btnRetry = dialogView.findViewById(R.id.btn_dialog_retry);
        Button btnExit = dialogView.findViewById(R.id.btn_dialog_exit);

        String title = won ? getString(R.string.win_title) : getString(R.string.loss_title);
        tvTitle.setText(title);
        tvTitleClone.setText(title);
        
        int colorRes = won ? R.color.colorCorrect : R.color.colorPresent;
        tvTitleClone.setTextColor(ContextCompat.getColor(this, colorRes));
        btnRetry.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, colorRes)));

        String statusMessage = won ? getString(R.string.win_message, targetWord) : getString(R.string.loss_message, targetWord);
        tvMessage.setText(statusMessage);
        tvSubMessage.setText(getString(R.string.dialog_message_format, "").replace("\n\n", ""));

        btnRetry.setOnClickListener(v -> {
            resetGame();
            dialog.dismiss();
        });

        btnExit.setOnClickListener(v -> finish());

        dialog.show();
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
                grid[r][c].setTranslationY(0);
                grid[r][c].setRotationX(0);
            }
        }

        // Reset keyboard statuses and backgrounds
        letterStatuses.clear();
        for (Button btn : keyboardButtons.values()) {
            btn.setBackgroundResource(R.drawable.cell_background);
        }

        // Re-enable the custom keyboard
        enableKeyboard();
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