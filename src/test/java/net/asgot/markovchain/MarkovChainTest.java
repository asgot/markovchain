package net.asgot.markovchain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.asgot.markovchain.MarkovChain;
import net.asgot.markovchain.StateAlreadyDefinedException;
import net.asgot.markovchain.StateNotDefinedException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link MarkovChain}
 * 
 * @author Daniel Martin
 *
 */
public class MarkovChainTest {

    private MarkovChain<String> markovChain;

    /**
     * Setup the test object
     */
    @Before
    public void setUp() {
        this.markovChain = new MarkovChain<String>();
    }

    /**
     * Make sure adding, setting, getting, and checking for a state work
     */
    @Test
    public void addGetSetContainsState() {
        markovChain.addState("test");
        markovChain.setState("test");
        assertTrue(markovChain.containsState("test"));
        assertEquals("test", markovChain.getCurrentState());
    }

    /**
     * Setting state that hasn't been defined should throw exception
     */
    @Test(expected = StateNotDefinedException.class)
    public void stateNotDefined() {
        markovChain.setState("not defined");
    }

    /**
     * Adding two states with same key should throw exception
     */
    @Test(expected = StateAlreadyDefinedException.class)
    public void stateAlreadyDefined() {
        markovChain.addState("already defined");
        markovChain.addState("already defined");
    }

    /**
     * Make sure defining a transition and calling transition() works as
     * expected
     */
    @Test
    public void testTransition() {
        markovChain.addState("s1");
        markovChain.addState("s2");
        markovChain.addTransition("s1", "s2", 1.0);
        markovChain.setState("s1");
        markovChain.transition();
        assertEquals("s2", markovChain.getCurrentState());

        Set<String> transitionStates = markovChain.getTransitionsForState("s1");
        assertEquals(1, transitionStates.size());
        assertTrue(transitionStates.contains("s2"));
    }

    /**
     * Defining a transition where the "from" state hasn't been defined should
     * throw an exception
     */
    @Test(expected = StateNotDefinedException.class)
    public void fromStateNotDefinedTransition() {
        markovChain.addState("s1");
        markovChain.addTransition("s2", "s1", 0.5);
    }

    /**
     * Defining a transition where the "to" state hasn't been defined should
     * throw an exception
     */
    @Test(expected = StateNotDefinedException.class)
    public void toStateNotDefinedTransition() {
        markovChain.addState("s1");
        markovChain.addTransition("s1", "s2", 0.5);
    }

    /**
     * Shouldn't accept a negative probability
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeProbability() {
        markovChain.addState("s1");
        markovChain.addTransition("s1", "s1", -1);
    }

    /**
     * Shouldn't accept a probability > 1
     */
    @Test(expected = IllegalArgumentException.class)
    public void greaterThanOneProbability() {
        markovChain.addState("s1");
        markovChain.addTransition("s1", "s1", 1.1);
    }

    /**
     * null should be returned if no states are defined as well as after
     * transition() is called
     */
    @Test
    public void noStatesDefinedTransition() {
        assertEquals(null, markovChain.getCurrentState());
        markovChain.transition();
        assertEquals(null, markovChain.getCurrentState());
    }

    /**
     * Exception should be thrown if from-state for transition is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullFromTransition() {
        markovChain.addState("s1");
        markovChain.addTransition(null, "s1", 0.5);
    }

    /**
     * Exception should be thrown if to-state for transition is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullToTransition() {
        markovChain.addState("s1");
        markovChain.addTransition("s1", null, 0.5);
    }

    /**
     * Null shouldn't be a state
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullState() {
        assertFalse(markovChain.containsState(null));
        markovChain.addState(null);
    }

    /**
     * Test getStates()
     */
    @Test
    public void getStates() {
        assertEquals(0, markovChain.getStates().size());
        markovChain.addState("state");
        assertEquals(1, markovChain.getStates().size());
    }

    /**
     * Should throw IllegalArgumentException when getting transitions for null
     */
    @Test(expected = IllegalArgumentException.class)
    public void getTransitionsForNull() {
        markovChain.getTransitionsForState(null);
    }

    /**
     * Should throw IllegalArgumentException when getting transitions for
     * undefined state
     */
    @Test(expected = StateNotDefinedException.class)
    public void getTransitionsForNotDefined() {
        markovChain.getTransitionsForState("state");
    }

    /**
     * Tests the fromStrings factory method
     */
    @Test
    public void fromStrings() {
        List<String> strings = new ArrayList<>();
        strings.add("the");
        strings.add("man");
        strings.add("and");
        strings.add("the");
        strings.add("man");

        MarkovChain<String> markovChain = MarkovChain.fromStrings(strings.iterator());

        assertTrue(markovChain.containsState("the"));
        assertTrue(markovChain.containsState("man"));
        assertTrue(markovChain.containsState("and"));

        Set<String> theTransitions = markovChain.getTransitionsForState("the");
        assertEquals(1, theTransitions.size());
        assertTrue(theTransitions.contains("man"));

        Set<String> manTransitions = markovChain.getTransitionsForState("man");
        assertEquals(1, manTransitions.size());
        assertTrue(manTransitions.contains("and"));

        Set<String> andTransitions = markovChain.getTransitionsForState("and");
        assertEquals(1, andTransitions.size());
        assertTrue(andTransitions.contains("the"));
    }

    /**
     * giving fromStrings a null argument should throw IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void fromStringNullIterator() {
        MarkovChain.fromStrings(null);
    }
}
