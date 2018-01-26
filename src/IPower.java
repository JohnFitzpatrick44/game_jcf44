/**
 * To make a new power type, follow this interface. 
 * 
 * @author Jack
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
