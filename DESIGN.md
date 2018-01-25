design
======

# Design Process and Goals
Before refactoring for analysis, all subclasses were in one file (Breakout.java). This was re-done to simplify the project, and separate code for the code masterpiece submission. I did my best to allow for easy expansion of the code. 

# Adding New Features
Many of the classes are easily expandable. For example:
 * Adding new bricks, balls, and paddles simply requires you to call their respective constructors.
 * Making new types of bricks means picking an arbitrary brick code, and adding the new brick's behavior based on its type to the Brick class.
 * Adding new power up or down effects requires you to expand the random number generator limits by one, and adding a new case in the trigger() method.
 * Adding new levels, or making another level file, involves following the instructions in the ReadMe.md and changing the file constant in Breakout.java.
 * Changing window size does get buggy, but most components will scale with the change. Change the X and YSIZE constants.
 * Adding cheats requires adding a KeyCode case to the handleKeyInput method.
 * Generally, any other constants can be changed.
 	* INIT_BALL_SPEED, PADDLE_ACCELERATION, PADDLE_DRAG, FALL_SPEED, and BRICK_DRAG change various speeds and accelerations.
 	* COLOR_PALETTE, BRICK_CURVE, and BACKGROUND change aesthetics.
 	* The image file names can be changed to put in custom images for powers and hearts.
 	
# Design Decisions
 * Before I separated classes into different files, they were all in Breakout.java.
 	* This simplified the code writing for me, but quickly became large and hard to navigate (> 1000 lines in one file is not sustainable).
 * Early on, I designed the balls and bricks classes and arrays to be very expandable, allowing for multiple balls and custom levels to be easy to implement.
 * All subclasses extend a javaFX class. 
 	* Allows for easy manipulation of their position, appearance, etc.
 	* I did have some issues with inheritance, and learned a lot about it.
 * I had a lot of private state variables, both in Breakout.java as a whole, and each subclass.
 	* I'm not sure if this was good or bad design, but it was very cluttered.
 	* Caused some issues when I separated out subclasses, but I was able to work around it.

# Other Assumptions
 * There is only one possible level file format, which is quite strict. I am not very good at file handling, so I didn't add error checking. 
 	* This part of the program seems the most volatile, so be careful when adding levels.
 * I noticed some inconsistent bugs pertaining to skipping a level, jumping to a level, and ending the game at the same time. This would cause bricks to appear over the end game UI.
 	* As long as the player does not mash cheat keys as fast as possible, this problem shouldn't occur.	
 	