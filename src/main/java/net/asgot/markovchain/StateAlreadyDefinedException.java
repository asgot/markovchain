package net.asgot.markovchain;

/**
 * Thrown when a state is being defined but it has already been defined.
 * 
 * @author Daniel Martin
 *
 */
public class StateAlreadyDefinedException extends RuntimeException {

    private static final long serialVersionUID = -135874834998238657L;

    /**
     * @param message
     */
    public StateAlreadyDefinedException(String message) {
        super(message);
    }
}
