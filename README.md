markovchain
===========

A Markov chain implementation in Java.

##Example Usage 
This code snippet defines the Markov chain seen here http://en.wikipedia.org/wiki/Examples_of_Markov_chains#A_very_simple_weather_model):

```java
MarkovChain<String> mc = new MarkovChain<>();
        
mc.addState("S");
mc.addState("R");

mc.addTransition("S", "R", 0.1);
mc.addTransition("S", "S", 0.9);
mc.addTransition("R", "S", 0.5);
mc.addTransition("R", "R", 0.5);

mc.setState("S");
        
for (int i = 0; i < 20; i++) {
  System.out.print(mc.getCurrentState());
  mc.transition();
}
```

Sample output: SSSSRRSSSSSSSSSSSSRR
