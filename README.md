game
====

First project for CompSci 308 Fall 2017

John Fitzpatrick
1/21/18
jcf44

Breakout.java
Basic javafx code taken from lab_bounce.

Use arrow keys to move paddle, hold/release space to stick/unstick ball
Press up arrow key to make paddle "jump"
Press button on side bar to turn on moving block mode

Cheat codes:
'o' - adds a new ball
'p' - finishes current level
'l' - refills lives
1-5 - jumps to the respective level

Level file format:
Blocks will be placed in the level from left to right, starting at the top left of the screen, and fitting 10 blocks per row.
Blocks are signified by one letter and one number, representing block type and durability (if applicable, number does not matter for permanent blocks, so just put '1')
'n' is a neutral block, 'p' is power up, 'd' is power down
'n0' will make a blank space, so patters of blocks can be made
Separate brick codes by whitespace, separate levels by one '-' in a new line by itself.
Example file:
n1 n0 n0 p1 n0 n0 d1 n0 n0 n1
-

Will create one row of four spaced out blocks, with one power up and one power down.
