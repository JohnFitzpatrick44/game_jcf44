// Corresponding interface to Power.java. I think this shows good design, as an interface is an effective way
// to ensure a new Power subclass will behave with Breakout accordingly. 

/**
 * To make a new power type, follow this interface. This will allow it to interact with Breakout without errors. 
 * 
 * @author Jack Fitzpatrick
 * @version %G%
 */
public interface IPower {
	
    /**
     * Method run when power hits paddle, initiate effects
     */
	public void trigger();
	
	/**
	 * Update position of the power on screen
	 * @param elapsedTime Amount of time passed in one step
	 */
	public void updatePos(double elapsedTime);
	
	/**
	 * Method run when owner brick is broken
	 */
	public void startFalling();
	
	/**
	 * Gets whether power is falling or not
	 * @return falling boolean, if power is free from brick
	 */
	public boolean getFalling();
}
