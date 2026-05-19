# Besednik

Besednik is a Slovenian version of the popular word-guessing game Wordle, built specifically for the Android platform. It challenges players to find a hidden five-letter Slovenian word within six attempts.

## Features

- Extensive Slovenian Dictionary: The game validates every guess against a dictionary of over 100,000 five-letter Slovenian words stored in the assets folder.
- Custom Keyboard: An integrated in-game keyboard that includes Slovenian characters (Č, Š, Ž) for a seamless native experience.
- Fluid Animations: 
    - Letter Reveal: Each letter flips 180 degrees to reveal its status (correct, present, or absent).
    - Win Animation: A vertical wave bounce effect triggers when the correct word is guessed.
    - Shake Effect: The row shakes horizontally if an invalid word is entered.
- Visual Feedback: High-contrast colors based on the original Wordle color palette for accessibility and clarity.
- Robust Game Logic: Handles edge cases like duplicate letters and validates word existence before consuming a turn.

## How to Play

1. Enter any valid five-letter Slovenian word using the on-screen keyboard.
2. Press POTRDI to submit your guess.
3. The tiles will change color to provide feedback:
    - Green: The letter is correct and in the right position.
    - Yellow: The letter is in the word but in a different position.
    - Gray: The letter is not part of the target word.
4. If the guess is not in the dictionary, the row will shake and no attempt is used.
5. Guess the word within six tries to win.

## Technical Requirements

- Minimum SDK: 33 (Android 13)
- Target SDK: 36
- Development Environment: Android Studio
- Language: Java

## Installation

1. Clone the repository to your local machine.
2. Open the project in Android Studio.
3. Ensure the words5.txt file is present in the app/src/main/assets/ directory.
4. Build and run the application on an emulator or a physical device running Android 13 or higher.
