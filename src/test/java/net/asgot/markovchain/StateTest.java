package net.asgot.markovchain;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import net.asgot.markovchain.State;
import net.asgot.markovchain.TransitionAlreadyDefinedException;

import org.junit.Test;

/**
 * Tests {@link State}
 * 
 * @author Daniel Martin
 *
 */
public class StateTest {

    /**
     * If a state has no transitions then {@link State#getNextState()} should
     * return itself.
     */
    @Test
    public void getNextStateWithNoTransitions() {
        Object key = new Object();
        State<Object> state = new State<>(key);
        State<Object> newState = state.getNextState();
        assertEquals(state, newState);
    }

    /**
     * If there is one transition with probability 1.0 then that transition
     * should be taken.
     */
    @Test
    public void getNextStateWithOneTransition() {
        State<Object> state1 = new State<>(new Object());
        State<Object> state2 = new State<>(new Object());

        state1.addTransition(state2, 1);

        State<Object> nextState = state1.getNextState();

        assertEquals(state2, nextState);
    }

    /**
     * Test transition to self
     */
    @Test
    public void selfTransition() {
        State<Object> state = new State<>(new Object());
        state.addTransition(state, 1.0);

        State<Object> nextState = state.getNextState();

        assertEquals(state, nextState);
    }

    /**
     * If a transition with 0 probability is defined, then it should not be
     * taken
     */
    @Test
    public void zeroProbabilityTransition() {
        State<Object> state1 = new State<>(new Object());
        State<Object> state2 = new State<>(new Object());

        state1.addTransition(state2, 0);

        State<Object> nextState = state1.getNextState();

        assertEquals(state1, nextState);
    }

    /**
     * If a transition to the same state is defined twice then
     * IllegalArgumentException should be thrown
     */
    @Test(expected = TransitionAlreadyDefinedException.class)
    public void addTransitionToSameStateTwice() {
        State<Object> state1 = new State<>(new Object());
        State<Object> state2 = new State<>(new Object());

        state1.addTransition(state2, 0);
        state1.addTransition(state2, 0);
    }

    /**
     * Passing in a negative probability should result in an
     * IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeProbability() {
        State<Object> state = new State<>(new Object());
        state.addTransition(state, -1);
    }

    /**
     * Passing in a probability > 1 should result in an IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void greaterThanOneProbability() {
        State<Object> state = new State<>(new Object());
        state.addTransition(state, 1.1);
    }

    /**
     * If the sum of probabilities over all transitions would exceed 1, then
     * IllegalArgumentException should be thrown
     */
    @Test(expected = IllegalArgumentException.class)
    public void sumOfProbabilitiesExceedsOne() {
        State<Object> state1 = new State<>(new Object());
        State<Object> state2 = new State<>(new Object());

        state1.addTransition(state1, 0.9);
        state1.addTransition(state2, 0.2);
    }

    /**
     * Define a bunch of transitions and make sure the right one is taken
     */
    @Test
    public void getRightStateFromABunchOfTransitions() {
        Random rand = mock(Random.class);
        when(rand.nextDouble()).thenReturn(.51);

        State<Object> state = new State<>(new Object(), rand);
        State<Object> rightState = new State<>(new Object());

        for (int i = 0; i < 50; i++) {
            state.addTransition(new State<>(new Object()), 0.01);
        }

        state.addTransition(rightState, 0.01);

        for (int i = 0; i < 49; i++) {
            state.addTransition(new State<>(new Object()), 0.01);
        }

        State<Object> newState = state.getNextState();

        assertEquals(rightState, newState);
    }

    /**
     * getKey() should return the correct key
     */
    @Test
    public void testGetKey() {
        Object key = new Object();
        State<Object> state = new State<>(key);
        assertEquals(key, state.getKey());
    }

    /**
     * toString() should return toString() on the key
     */
    @Test
    public void testToString() {
        String s = "test";
        State<String> state = new State<>(s);
        assertEquals(s, state.toString());
    }

    /**
     * Should throw an exception if key is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullKey() {
        new State<Object>(null);
    }

    /**
     * Should throw an exception if random is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullRandom() {
        new State<Object>("state", null);
    }
}
