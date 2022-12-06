# distributed balanced k-way graph partitioning

Authors: Brando Chiminelli, Tommaso Praturlon

To run the program in Task 1 do
- ./compile.sh
- ./run.sh -graph {path/to/graph/}
- ./plot.sh {path/to/output}

## Task 1 
Experiments for graphs: 3elt.graph, add20.graph, facebook.graph.
What does the graph show? Explain.


![3elt](plots/graph_3elt.png)
![add20](plots/graph_add20.png)
![facebook](plots/graph_facebook.png)

## Task 2

In this task, you are to analyze how the algorithm's performance is affected when different parameters are changed, especially the effect of simulated annealing. Currently, Ja-Be-Ja uses a linear function to decrease the temperature (lines 9 - 13 of the Ja-Be-Ja algorithm), and the temperature is multiplied by the cost function (line 26 of the Ja-Be-Ja algorithm). You will now analyze how changing the simulated annealing parameters, and the acceptance probability function affects the performance of Ja-Be-Ja.

### Linear function to decrease the temperature
```
  /**
   * Simulated analealing cooling function
   */
  private void saCoolDown(){
    // TODO for second task
    if (T > 1)
      T -= config.getDelta();
    if (T < 1)
      T = 1;
  }
```
