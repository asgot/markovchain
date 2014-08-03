package net.asgot.markovchain;

/**
 * Thrown when a transition is being defined but was already defined.
 * 
 * @author Daniel Martin
 *
 */
public class TransitionAlreadyDefinedException extends RuntimeException {

    private static final long serialVersionUID = -5349895367381780806L;

    /**
     * @param message
     */
    public TransitionAlreadyDefinedException(String message) {
        super(message);
    }
}
