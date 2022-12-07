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

Implementation of simulated annealing, following the description from [this](http://katrinaeg.com/simulated-annealing.html) blog post. Basically, to avoid a local maxima and instead find the global one, we calculate an **acceptance probability**. This probability is used, at random points in the process, to choose whether to accept a temporary worse solution since it could lead to a neighboring solution that leads to the gloabl maximum.

1. Generate a random solution -> in sampleAndSwap() call getNeighbors() which finds a randon neighbor partner for the node
2. Calculate its cost using the cost function -> in findPartner() use _old_ as the cost function defined in the paper
3. Generate a random neighboring solution -> use the same random partner
4. Calculate the new solution's cost -> in findPartner() use _new_ as the cost function defined in the paper
5. If the new cost is less than the old cost, move to new solution -> in findPartner() the _if_ condition
6. If new cost is more than old cost, use acceptance probability to choose old solution or not -> accept_prob is computed in findPartner() as suggested in the blog post. Then randommly assign the best partner to be the new solution.

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
