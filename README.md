# distributed balanced k-way graph partitioning

Authors: Brando Chiminelli, Tommaso Praturlon

To run the program in Task 1 do
- ./compile.sh
- ./run.sh -graph {path/to/graph/}, e.g. `./run.sh -graph ./graphs/3elt.graph`
- ./plot.sh {path/to/output}, e.g. `./plot.sh output/3elt.graph_NS_HYBRID_GICP_ROUND_ROBIN_T_2.0_D_0.003_RNSS_3_URSS_6_A_2.0_R_1000.txt`

## Task 1 
Experiments for graphs: 3elt.graph, add20.graph, facebook.graph.
The plots show the performance of JaBeJa with three different graphs, which are the same used in the paper.
The metrics which are taken into consideration are: edge-cut, swaps and data migration.
- edge-cut is the number of inter-partition edges, being the energy of the system.
- swaps is the number of inter-host swaps, indicating the cost of the algorithm.
- the number of migrations that occur when a node has to be moved from its initial partition to the final one.
After R=1000 rounds with hybrid (H) candidate selection policy, and simulated annealing using a linear function for the temperature decrease T = T - delta, the best cuts are

### add20 graph
With ./run.sh -delta 0.003 -alpha 2 -temp 2 -graph graphs/add20.graph

INFO  Jabeja:327 - round: 999, edge cut:2095, swaps: 1090263, migrations: 1751
![add20](plots/graph_add20.png)

### 3elt graph
With ./run.sh -delta 0.003 -alpha 2 -temp 2 -graph graphs/3elt.graph

INFO  Jabeja:327 - round: 999, edge cut:2604, swaps: 1580209, migrations: 3328
![3elt](plots/graph_3elt.png)

### Twitter graph
With ./run.sh -delta 0.003 -alpha 2 -temp 2 -graph graphs/twitter.graph

INFO  Jabeja:327 - round: 999, edge cut:41156, swaps: 899515, migrations: 2049
![twitter](plots/graph_twitter.png)

## Task 2

In this task, you are to analyze how the algorithm's performance is affected when different parameters are changed, especially the effect of simulated annealing. Currently, Ja-Be-Ja uses a linear function to decrease the temperature (lines 9 - 13 of the Ja-Be-Ja algorithm), and the temperature is multiplied by the cost function (line 26 of the Ja-Be-Ja algorithm). You will now analyze how changing the simulated annealing parameters, and the acceptance probability function affects the performance of Ja-Be-Ja.

Part 1. Implementation of simulated annealing, following the description from [this](http://katrinaeg.com/simulated-annealing.html) blog post. Basically, to avoid a local maxima and instead find the global one, we calculate an **acceptance probability**. This probability is used, at random points in the process, to choose whether to accept a temporary worse solution since it could lead to a neighboring solution that leads to the gloabl maximum.

1. Generate a random solution -> in sampleAndSwap() call getNeighbors() which finds a randon neighbor partner for the node
2. Calculate its cost using the cost function -> in findPartner() use _old_ as the cost function defined in the paper
3. Generate a random neighboring solution -> use the same random partner
4. Calculate the new solution's cost -> in findPartner() use _new_ as the cost function defined in the paper
5. If the new cost is less than the old cost, move to new solution -> in findPartner() the _if_ condition
6. If new cost is more than old cost, use acceptance probability to choose old solution or not -> accept_prob is computed in findPartner() as suggested in the blog post. Then randommly assign the best partner to be the new solution.

Experiment with different values for the temperature T decrease: alpha belongs to [0.8, 0.99].

To experiment change parameters that could be set in run.sh

To see these type ./run.sh -help. Commands are -delta, -alpha, -temp, -rounds.

Using the simulated annealing implementation clearly improves the rate of convergence in add20 and twitter. While in 3elt it does not improve.

# Experiments for task 2 point 1
At round 1000, 
Task 2 point 1 -> implementation of the new simulated annealing. Observe how these changes affect the rate of convergence.
change delta = [0.8, 0.9, 0.99] and T=1. With iteration, and without iteration.

With delta = 0.8, T=1
| graph | edge-cut expected| edge-cut obtained, alpha = 1 | edge-cut obtained, alpha = 2 | edge-cut obtained, alpha = 5 |
| --- | --- | --- | --- | --- |
| add20 | 1206 | 2468 | 2385 | 2512 |
| 3elt	| 390 | 9259 | 3551 | 3691 |
| twitter | 41040 | 40855 | 41201 | 42007 |

Print graphs to see the rate of convergence and comment it.

With delta = 0.9, T=1
| graph | edge-cut expected| edge-cut obtained, alpha = 1 | edge-cut obtained, alpha = 2 | edge-cut obtained, alpha = 5 |
| --- | --- | --- | --- | --- |
| add20 | 1206 | 2447 | **2361** | 2495 |
| 3elt	| 390 | 9203 | 3941 | **3482** |
| twitter | 41040 | **40824** | 41161 | 41946 |

With delta = 0.99, T=1
| graph | edge-cut expected| edge-cut obtained, alpha = 1 | edge-cut obtained, alpha = 2 | edge-cut obtained, alpha = 5 |
| --- | --- | --- | --- | --- |
| add20 | 1206 | 2470 | 2361 | 2479 |
| 3elt	| 390 | 9116 | 3893 | 3829 |
| twitter | 41040 | 40857 | 41187 | 41919 |

### Best edge-cut values add20 graph
![add20](plots/graph_add20_D0.9_T1.0_A2.0.png)

### Best edge-cut values 3elt graph
![add20](plots/graph_3elt_D0.9_T1.0_A5.0.png)

### Best edge-cut values twitter graph
![add20](plots/graph_twitter_D0.9_T1.0_A1.0.png)

# Experiments for task 2 point 2

Change T and delta to find lower cuts. 

Part 2. Restart simulated annealing after a certain number of cycles, depending on the initial T and delta. e.g. for T=2, delta=0.01 after 200 rounds (T/delta). Experiment with different parameters to find lower edge cuts. -> Check on the literature how to reach this and implement.

With delta = 0.003, T=2
| graph | edge-cut expected| edge-cut obtained, alpha = 1 | edge-cut obtained, alpha = 2 | edge-cut obtained, alpha = 5 |
| --- | --- | --- | --- | --- |
| add20 | 1206 | 2057 | **1814** | 2237 |
| 3elt	| 390 | 2329 | 2474 | **2071** |
| twitter | 41040 | 41126 | **40969** | 41523 |
