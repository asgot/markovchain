package net.asgot.markovchain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A Markov chain implementation.
 * 
 * @author Daniel Martin
 *
 * @param <T>
 *            The type to use as the key for states.
 */
public class MarkovChain<T> {

    private Map<T, State<T>> states;

    private State<T> currentState;

    /**
     * Constructs a {@code MarkovChain} with no states.
     */
    public MarkovChain() {
        this.states = new HashMap<>();
        this.currentState = null;
    }

    /**
     * Sets the current state. The state must have already been defined.
     * 
     * @param key
     *            the key for the state
     * @throws StateNotDefinedException
     *             if the given state has not been defined
     */
    public void setState(T key) {
        if (states.containsKey(key)) {
            this.currentState = states.get(key);
        } else {
            throw new StateNotDefinedException("Cannot set state because it hasn't been defined");
        }
    }

    /**
     * Adds a state with the given key
     * 
     * @param key
     *            the key of the new state
     * @throws StateAlreadyDefinedException
     *             if the state has already been defined
     * @throws IllegalArgumentException
     *             if the key is null
     */
    public void addState(T key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        if (this.states.containsKey(key)) {
            throw new StateAlreadyDefinedException("State already defined");
        }

        this.states.put(key, new State<>(key));
    }

    /**
     * Checks whether the state given with the given key has been defined.
     * 
     * @param key
     *            the key of the state
     * @return true if the state has been defined, false otherwise
     */
    public boolean containsState(T key) {
        return this.states.containsKey(key);
    }

    /**
     * Gets the current state.
     * 
     * @return the current state or {@code null} if no current state exists
     */
    public T getCurrentState() {
        if (this.currentState != null) {
            return this.currentState.getKey();
        }

        return null;
    }

    /**
     * Returns a set of all the defined states
     * 
     * @return the states
     */
    public Set<T> getStates() {
        return this.states.keySet();
    }

    /**
     * Adds a transition from one state to another with the given probability.
     * Both of the given states must have already been defined and the
     * probability must be between 0 and 1 inclusive.
     * 
     * @param from
     *            the from-state for the transition
     * @param to
     *            the to-state for the transition
     * @param probability
     *            the probability that the transition will be taken
     * @throws StateNotDefinedException
     *             if any of the given states are not yet defined
     * @throws IllegalArgumentException
     *             if the probability isn't in the correct range or if the
     *             states given are null
     */
    public void addTransition(T from, T to, double probability) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from/to cannot be null");
        }

        if (!this.states.containsKey(from) || !this.states.containsKey(to)) {
            throw new StateNotDefinedException(
                    "From and to states must be defined already to create a transition");
        }

        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("probability must be between 0 and 1 inclusive");
        }

        State<T> fromState = this.states.get(from);
        State<T> toState = this.states.get(to);

        fromState.addTransition(toState, probability);
    }

    /**
     * Gets all the transition states for the state with the given key
     * 
     * @param key
     *            the key for the state
     * @return a set of states
     */
    public Set<T> getTransitionsForState(T key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        if (!this.states.containsKey(key)) {
            throw new StateNotDefinedException(
                    "Cannot get transitions for state because it's not defined");
        }

        Set<T> transitionStates = new HashSet<>();

        for (State<T> s : states.get(key).getTransitions()) {
            transitionStates.add(s.getKey());
        }

        return transitionStates;
    }

    /**
     * Transitions from the current state to another state based on the
     * probabilities of the defined transitions. If the current state (as given
     * by {@link MarkovChain#getCurrentState()}) is {@code null}, then the new
     * state will also be {@code null}.
     */
    public void transition() {
        if (this.currentState == null) {
            return;
        }

        this.currentState = this.currentState.getNextState();
    }

    /**
     * Creates a {@code MarkovChain} from strings. Each string will have a
     * transition to every string that ever occurs after it, and the
     * probabilities will be based on the number of occurrences. This can be
     * used to create a Markov chain that generates semi-real looking text from
     * a large body of text.
     * 
     * @param iter
     *            an iterator of strings
     * @throws IllegalArgumentException
     *             if {@code iter} is null
     * @return the generated {@code MarkovChain}
     */
    public static MarkovChain<String> fromStrings(Iterator<String> iter) {
        if (iter == null) {
            throw new IllegalArgumentException("iter must not be null");
        }

        Map<String, Map<String, Integer>> occurrenceMap = new HashMap<>();

        String previousWord = null;

        while (iter.hasNext()) {
            String currentWord = iter.next();

            if (!occurrenceMap.containsKey(currentWord)) {
                occurrenceMap.put(currentWord, new HashMap<String, Integer>());
            }

            if (previousWord != null) {
                Map<String, Integer> occurrences = occurrenceMap.get(previousWord);

                if (occurrences.containsKey(currentWord)) {
                    occurrences.put(currentWord, occurrences.get(currentWord) + 1);
                } else {
                    occurrences.put(currentWord, 1);
                }
            }

            previousWord = currentWord;
        }

        MarkovChain<String> mc = new MarkovChain<>();

        for (String word : occurrenceMap.keySet()) {
            if (!mc.containsState(word)) {
                mc.addState(word);
            }

            Map<String, Integer> occurrences = occurrenceMap.get(word);
            int sumOccurrences = occurrences.values().stream().mapToInt((i) -> i.intValue()).sum();

            for (Entry<String, Integer> entry : occurrences.entrySet()) {
                if (!mc.containsState(entry.getKey())) {
                    mc.addState(entry.getKey());
                }

                mc.addTransition(word, entry.getKey(), entry.getValue() / (double) sumOccurrences);
            }
        }

        return mc;
    }
}
