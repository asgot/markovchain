package net.asgot.markovchain;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

/**
 * Represents a state in a {@link MarkovChain}
 * 
 * @author Daniel Martin
 *
 * @param <T>
 *            The type to use a key for the state
 */
public class State<T> {

    private T key;

    private Random random;

    private TreeMap<Double, State<T>> transitions;

    private double sumOfProbabilities = 0.0;

    /**
     * Constructs a {@code State}. Uses {@code new Random()} as the RNG for
     * choosing transitions to take.
     * 
     * @param key
     *            the key for the state
     * @throws IllegalArgumentException
     *             if key is null
     */
    public State(T key) {
        this(key, new Random());
    }

    /**
     * Constructs a {@code State}.
     * 
     * @param key
     *            the key for the state
     * @param random
     *            source of random numbers for calculating the next state
     * @throws IllegalArgumentException
     *             if key or random is null
     */
    public State(T key, Random random) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        if (random == null) {
            throw new IllegalArgumentException("random must not be null");
        }

        this.key = key;
        this.random = random;
        this.transitions = new TreeMap<>();
    }

    /**
     * Adds a transition to another state with the given probability.
     * 
     * @param otherState
     *            the to-state for the transition
     * @param probability
     *            the probability that the transition will occur
     * @throws TransitionAlreadyDefinedException
     *             if a transition has already been defined to the given state
     * @throws IllegalArgumentException
     *             if the probability is not between 0 and 1 inclusive or the
     *             sum of probabilities for all transitions exceeds 1
     */
    public void addTransition(State<T> otherState, double probability) {
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("probability must be between 0 and 1 inclusive");
        }

        if (transitions.containsValue(otherState)) {
            throw new TransitionAlreadyDefinedException(String.format(
                    "Transition already defined from %s to %s", this.key.toString(),
                    otherState.toString()));
        }

        if (sumOfProbabilities + probability > 1.01) {
            throw new IllegalArgumentException(
                    "The sum of probabilities after adding this new transition must not exceed 1.0");
        }

        this.transitions.put(probability + sumOfProbabilities, otherState);
        this.sumOfProbabilities += probability;
    }

    /**
     * Gets all the states this state has a transition to
     * 
     * @return collection of transition states
     */
    public Collection<State<T>> getTransitions() {
        return this.transitions.values();
    }

    /**
     * Returns one of the states in the defined transitions based on the
     * probabilities. If the sum of probabilities for all transitions is less
     * than 1, then the rest of the probability space will represent a
     * transition back onto the same state.
     * 
     * @return a state from the transitions
     */
    public State<T> getNextState() {
        double rand = this.random.nextDouble();

        Entry<Double, State<T>> e = this.transitions.higherEntry(rand);

        return e != null ? e.getValue() : this;
    }

    /**
     * Gets the key for this state.
     * 
     * @return the key
     */
    public T getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return this.key.toString();
    }
}
