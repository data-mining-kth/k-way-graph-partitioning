# K-way Graph Partitioning with JaBeJa

Contributors:
<a href="https://github.com/Bralli99">Brando Chiminelli</a>, 
<a href="https://github.com/boyscout99">Tommaso Praturlon</a>

Course: <a href="[https://id2223kth.github.io/](https://www.kth.se/social/course/ID2222/)">Data Mining</a>, at <a href="https://www.kth.se/en">KTH Royal Institute of Technology</a>

## About

This project is about experimenting with the implementation of the **Ja-Be-Ja algorithm**, a K-way partitioning algorithm based on the concept of **simulated annealing**. Ja-Be-Ja algorithm provides a smart and decentralized solution to partition large graphs by randomly assigning classes (colors) to the nodes and computing the local energy between nodes, their connections, and an assigned random subset of nodes. The local energy corresponds to the entropy between the connections of a node and its partners (connected nodes and subset). 

Therefore, the goal of the algorithm is to reduce this entropy (energy) in order to define a balanced partitioning. To reduce this entropy, the nodes are compared to their partners by means of computing their energy with its current color and when switching colors with the presented partner. If the energy between the node and partner reduces when they switch colors, it means that a better balance has been found and the node and partner should swap colors.

In order to not get stuck into a local minimum, Ja-Be-Ja employs the **simulated annealing method** based on the temperature of the systems, which cools down during the process to increse the chances of finding a better local minimum or even the global minimum.

## How to run 
To run the program with the run.sh script
- `./compile.sh`
- `./run.sh -graph {path/to/graph/}`, e.g. `./run.sh -graph ./graphs/3elt.graph`
- `./plot.sh {path/to/output}`, e.g. `./plot.sh output/3elt.graph_NS_HYBRID_GICP_ROUND_ROBIN_T_2.0_D_0.003_RNSS_3_URSS_6_A_2.0_R_1000.txt`

To run the program with the my_plots.sh script, which combines run.sh and plot.sh
- `./compile.sh`
- `./my_plots.sh {**delta** value} {**temp** value} {**alpha** value} {name of the graph}`, e.g. `./my_plots.sh 0.003 2.0 2.0 3elt`

## Task 1 - JaBeJa implementation
Experiments for graphs: 3elt.graph, add20.graph, facebook.graph.
The plots show the performance of JaBeJa with three different graphs, which are the same used in the paper.
The metrics which are taken into consideration are: edge-cut, swaps and data migration.
- **edge-cut** is the number of inter-partition edges, being the energy of the system.
- **swaps** is the number of inter-host swaps, indicating the cost of the algorithm.
- the number of **migrations** that occur when a node has to be moved from its initial partition to the final one.
After R=1000 rounds with hybrid (H) candidate selection policy, and simulated annealing using a linear function for the temperature decrease T = T - delta, the best edge cuts values are:

#### Best edge-cut value for add20 graph (2095)
With `./run.sh -delta 0.003 -alpha 2 -temp 2 -graph graphs/add20.graph`

INFO  Jabeja:327 - round: 999, edge cut:**2095**, swaps: 1090263, migrations: 1751
![add20](plots/graph_add20.png)

#### Best edge-cut value for 3elt graph (2604)
With `./run.sh -delta 0.003 -alpha 2 -temp 2 -graph graphs/3elt.graph`

INFO  Jabeja:327 - round: 999, edge cut:**2604**, swaps: 1580209, migrations: 3328
![3elt](plots/graph_3elt.png)

#### Best edge-cut value for twitter graph (41156)
With `./run.sh -delta 0.003 -alpha 2 -temp 2 -graph graphs/twitter.graph`

INFO  Jabeja:327 - round: 999, edge cut:**41156**, swaps: 899515, migrations: 2049
![twitter](plots/graph_twitter.png)

## Task 2 - JaBeJa with non-linear temperature decrease

In this task, we analyze how the algorithm's performance is affected when different parameters are changed, specifically, the effect of simulated annealing. In the Task 1, Ja-Be-Ja uses a linear function to decrease the temperature and the temperature is multiplied by the cost function. Now, we will analyze how changing the simulated annealing parameters, and the acceptance probability function affects the performance of Ja-Be-Ja.

### Part 1 - Implementation of Simulated Annealing
Following the description from [this](http://katrinaeg.com/simulated-annealing.html) blog post we implemented a different simulated annealing. Basically, to avoid a local maxima and instead find the global one, we calculate an **acceptance probability**. This probability is used, at random points in the process, to choose whether to accept a temporary worse solution since it could lead to a neighboring solution that leads to the gloabl maximum.

1. Generate a random solution -> in sampleAndSwap() call getNeighbors() which finds a randon neighbor partner for the node
2. Calculate its cost using the cost function -> in findPartner() use _old_ as the cost function defined in the paper
3. Generate a random neighboring solution -> use the same random partner
4. Calculate the new solution's cost -> in findPartner() use _new_ as the cost function defined in the paper
5. If the new cost is less than the old cost, move to new solution -> in findPartner() the _if_ condition
6. If new cost is more than old cost, use acceptance probability to choose old solution or not -> accept_prob is computed in findPartner() as suggested in the blog post. Then randommly assign the best partner to be the new solution.

Using the simulated annealing implementation we see that for add20 and twitter graphs the value of edge-cuts converges more rapidly, but the performance is actually slightly improved only for twitter.

### Experiments for Task 2 - Part 1
Implementation of the new simulated annealing. Observe how these changes affect the rate of convergence.
change delta = [0.8, 0.9, 0.99] and T=1.

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

#### Best edge-cut value for add20 graph (2361)
![add20](plots/graph_add20_D0.9_T1.0_A2.0.png)

#### Best edge-cut value for 3elt graph (3482)
![3elt](plots/graph_3elt_D0.9_T1.0_A5.0.png)

#### Best edge-cut value for twitter graph (40824)
![twitter](plots/graph_twitter_D0.9_T1.0_A1.0.png)

### Part 2 - Implementation of Restart
Here we investigate how the Ja-Be-Ja algorithm behaves when the simulated annealing is restarted after Ja-Be-Ja has converged. For example, if T is 2 and delta is 0.01, then after 400 rounds, the temperature will cool down to 1, and no more bad-swaps will be accepted. Ja-Be-Ja will converge soon after that. We implement the simulated-annealing restart after 400 rounds. We also experiment with different parameters and configurations to find lower edge cuts.

What we see is that with all the three graphs we experience an improvement in the performance thanks to the restart.

### Experiments for Task 2 point 2
With delta = 0.003, T=2
| graph | edge-cut expected| edge-cut obtained, alpha = 1 | edge-cut obtained, alpha = 2 | edge-cut obtained, alpha = 5 |
| --- | --- | --- | --- | --- |
| add20 | 1206 | **1814** | 2057 | 2237 |
| 3elt	| 390 | 2474 | 2329 | **2071** |
| twitter | 41040 | **40969** | 41126 | 41523 |

#### Best edge-cut value for add20 graph (1814)
![add20](plots/graph_add20_D0.003_T2.0_A1.0.png)

#### Best edge-cut value for 3elt graph (2071)
![add20](plots/graph_3elt_D0.003_T2.0_A5.0.png)

#### Best edge-cut value for twitter graph (40969)
![add20](plots/graph_twitter_D0.003_T2.0_A1.0.png)

## Task 3 - Custom Acceptance Probability Function

With the default acceptance probability, the one used in Task 2 point 1, we introduced a function that made the rejection of a swap with lower cost less certain. However, the acceptance probability defined in that way, makes the choice of a temporary non-optimal solution impossible after some rounds. This is because as the temperature decreases very fast, the acceptance probabilty becomes very close to zero every time the non-optimal solution is found. 

For this reason, we wanted to experiment with a different custom function which could slightly inhibit this behaviour. We decided to use a Gaussian function of the form exp(-(new\_d-old\_d)^2\*sqrt(T)) to dumpen the effect of decreasing T when there are non-optimal solutions, i.e. when old\_d is greater than new\_d as defined in our code.

Even though this change does not improve much with graphs add20 or twitter, it does show a better cut for 3elt. The reason may lie in the fact that the previous solution converged very rapidly to a local minima which was anyway too high. While with this new proposed solution, the increased probability of acceping a temporary sub-optimal solution, actually allowed the algorithm to find a better cut, as shown in the table and picture below.

| graph | parameters | edge-cut default function | edge-cut custom function |
| --- | --- | --- | --- |
| 3elt | delta=0.9, alpha=2.0, T=1.0 | 3941 | **2324** |

![3elt_custom](plots/graph_3elt_D0.9_T1.0_A2.0_custom.png)
