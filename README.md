# Soccer Penalties Game ⚽

## Overview
Soccer Penalties Game is an interactive Java Swing game that simulates a real penalty shootout.  
You take up to 5 shots against a moving goalie. Your goal is to score as many times as possible, watch the animations, and try to beat the high score list.

This project uses multiple custom panels (Home, Game, Game Over, High Scores) all connected in one main `JFrame`. The game includes animation, input handling, score tracking, and file-based high score saving.

- **Language:** Java
- **Built with:** NetBeans
- **GUI library:** Java Swing
- **Status:** Complete

---

## Gameplay / Features
- 🎯 **Penalty shootout gameplay**  
  - Player takes up to 5 shots.
  - Goalie moves side to side to try to block.
  - Ball “kicks” are animated.

- 🧠 **Randomized goalie movement**  
  - The goalie direction is randomized using `java.util.Random`.
  - A `javax.swing.Timer` is used in `GamePanel` to animate movement.

- 📊 **Score tracking**  
  - Shows current shot number out of 5.
  - Shows how many goals you scored.
  - Displays the result for each shot in a scoreboard UI (visual labels / boxes).

- 🏆 **Game Over screen**  
  - After all shots are taken, `GameOverPanel` displays your final score (ex: `Final Score: 4 / 5`).
  - You can enter your name and save your score.

- 📈 **High Scores screen**  
  - `HighScoresPanel` reads and displays data from `highscores.txt`.
  - Scores are stored in the format `"Name - Score"`.
  - Supports basic searching and sorting (for example, view all scores or look up a specific player).

- 🖼 Polished UI  
  - Uses custom images and GIFs:
    - goal background
    - moving goalie sprite
    - soccer ball
    - ball kick animation
    - trophy / celebration GIF on win
    - home screen background

- 🔁 **Replay loop**  
  - At the end, you can play again without restarting the entire program.
  - Panels switch dynamically inside the same main frame.

---

## How it’s structured
This is a Swing, panel-based game. The main frame is `PenaltiesGame`:

- `PenaltiesGame`  
  - Creates the window (`JFrame`)
  - Sets title, size, look & feel, default close behavior
  - Holds references to all panels:
    - `HomePanel`
    - `GamePanel`
    - `GameOverPanel`
    - `HighScoresPanel`
  - Switches between panels so the whole app feels like one game

Each screen in the game is its own panel:
- `HomePanel` – main menu (Play / High Scores / Exit)
- `GamePanel` – actual gameplay logic, animations, scoring
- `GameOverPanel` – final score, name entry, save score button
- `HighScoresPanel` – leaderboard view / search

Animation + logic highlights from `GamePanel`:
- Uses `Timer` to animate the goalie.
- Uses randomness to decide goalie movement direction.
- Tracks:
  - `totalShots`
  - `maxShots` (5)
  - `goalsScored`
- Updates visual labels for each shot so the user sees “goal” vs “save”.

---

## How to run it
### Option 1: Run in NetBeans (easiest)
1. Open NetBeans.
2. Go to `File > Open Project...`
3. Select the `SoccerPenaltiesGame` folder.
4. Run the project (the main class is `penaltiesgame.PenaltiesGame`).

### Option 2: Run with `javac` / `java`
If you want to run it manually:

```bash
# 1. clone the repo
git clone https://github.com/siddhipatel911/SoccerPenaltiesGame.git
cd SoccerPenaltiesGame/src

# 2. compile
javac penaltiesgame/*.java

# 3. run
java penaltiesgame.PenaltiesGame
```
### Note:
- You must run it from a directory where it can read/write highscores.txt, because the game saves player scores to that file after each round.

- Make sure the images/ folder with all the .png/.jpg/.gif files is available in the same classpath so the GUI can load the graphics.

---

## What I Learned: 
- Building a full GUI in Java Swing (panels, labels, buttons, images, layout).
  
- Using javax.swing.Timer for animation instead of blocking loops.

- Switching views inside one JFrame instead of opening/closing new windows.
  
- Storing and reading persistent player data from a text file using java.io.
  
- Handling basic game state (max shots, goals scored, score display, reset on replay).

---

## Future improvements:
- Difficulty levels (faster goalie, fewer shot attempts).

- Sound effects on goal / save.
  
- More advanced high score sorting (e.g. auto-rank top 10).
  
- Export scores to CSV or database instead of a text file.
