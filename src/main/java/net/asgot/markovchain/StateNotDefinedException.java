package net.asgot.markovchain;

/**
 * Thrown when an operation is attempted on a state but the state hasn't been
 * defined.
 * 
 * @author Daniel Martins
 *
 */
public class StateNotDefinedException extends RuntimeException {

    private static final long serialVersionUID = 3021718681046505224L;

    /**
     * @param message
     */
    public StateNotDefinedException(String message) {
        super(message);
    }
}
