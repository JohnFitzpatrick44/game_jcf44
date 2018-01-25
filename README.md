game
====

# First project for CompSci 308 Fall 2017

John Fitzpatrick
Started 1/14/18, Submitted 1/21/18, Updated 1/24/18
jcf44

## Breakout.java
Basic javafx application skeleton code taken from lab_bounce.
Start the program with Breakout.java (use main.java when playing the refactored game for the analysis).
Test level files included, change constant value in Breakout.java
 * Program will handle IOException
 * Test files are LevelFile.txt, LevelFile1.txt, LevelFile2.txt.
 * Use arrow keys to move paddle, hold/release space to stick/unstick ball.
 * Press up arrow key to make paddle "jump".
 * The ball will bounce at different angles depending on where it hits the paddle.
 * There are multiple block types: neutral, blocks that must be hit from the top, and power up/down blocks.
 * All blocks can have durabilities up to 3.
 * Permanent blocks are also implemented, and they cannot be moved in movable mode.
 * Power ups include decreased ball speed, increased paddle length, and extra balls.
 * Power downs include increased ball speed, decreased paddle length, and reversed controls.
 * Press button on side bar to turn on moving block mode (extra feature).

## Cheat codes:
 * 'O' - adds a new ball
 * 'P' - finishes current level
 * 'L' - refills lives
 * 1-5 - jumps to the respective level
 * '=' - increases ball speed
 * '-' - decreases ball speed
 * ']' - increases paddle length
 * '[' - decreases paddle length

## Level file format:
 * Blocks will be placed in the level from left to right, starting at the top left of the screen, and fitting 10 blocks per row.
 * Blocks are signified by one letter and one number, representing block type and durability (permanent blocks have a durability of 4).
 * 'n' is a neutral block, 'p' is power up, 'd' is power down, 't' is top-only.
 * 'n0' will make a blank space, so patters of blocks can be made.
 * Separate brick codes by whitespace, separate levels by one '-' in a new line by itself.
Example file:
```
n1 n0 n0 p1 n0 n0 d1 n0 n0 n1
-
```
 * Will create one row of four spaced out blocks, with one power up and one power down.
 * Level file format is very restrictive.
