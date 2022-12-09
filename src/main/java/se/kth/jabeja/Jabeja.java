package se.kth.jabeja;

import org.apache.log4j.Logger;
import se.kth.jabeja.config.Config;
import se.kth.jabeja.config.NodeSelectionPolicy;
import se.kth.jabeja.io.FileIO;
import se.kth.jabeja.rand.RandNoGenerator;

import java.io.*;
import java.util.*;

// Create public class
public class Jabeja {
  final static Logger logger = Logger.getLogger(Jabeja.class);
  private final Config config;
  private final HashMap<Integer/*id*/, Node/*neighbors*/> entireGraph;
  private final List<Integer> nodeIds;
  private int numberOfSwaps;
  private int round;
  private float T;
  private boolean resultFileCreated = false;

  //-------------------------------------------------------------------
  public Jabeja(HashMap<Integer, Node> graph, Config config) {
    this.entireGraph = graph;
    this.nodeIds = new ArrayList(entireGraph.keySet());
    this.round = 0;
    this.numberOfSwaps = 0;
    this.config = config;
    this.T = config.getTemperature();
  }


  //-------------------------------------------------------------------
  public void startJabeja() throws IOException {
    for (round = 0; round < config.getRounds(); round++) {
      // for every entry in the graph
      for (int id : entireGraph.keySet()) {
        sampleAndSwap(id);
      }

      //one cycle for all nodes have completed.
      //reduce the temperature
      saCoolDown();
      report();
    }
  }

  /**
   * Simulated analealing cooling function
   * The temperature is a function of which iteration you're on
   */
  private void saCoolDown(){
    // TODO for second task -> done
    // --------------------------------------------------------------
    // Uncomment the following section for Task 1, and comment Task 2
    // Uncomment for Task 2 point 2
    // --------------------------------------------------------------
    // /*
    // With restart
    // System.out.printf("T: %f, Round: %d\n", T, round);
    //if (round%(config.getTemperature()/config.getDelta())==0){
      /* 
    if (round == 250)  {
      T = config.getTemperature();
      System.out.printf("Restart occured!\n");
    }
    */
    // /*
    if (T > 1)
      T -= config.getDelta();
    if (T < 1)
      T = 1;
    // */
    
    // --------------------------------------------------------------
    // Uncomment the following section for Task 2, and comment Task 1
    // --------------------------------------------------------------
     /*
    // change temperature decrease to be non-linear
    // Typical choices for alpha are between 0.8 and 0.99
    // System.out.printf("T: %f\n", T);
    T = T*config.getDelta();
    if (T < 0.00001f)
      T = 0.00001f;
     */
  }

  /**
   * Sample and swap algorithm at node p
   * @param nodeId
   * Two nodes exchange their colors if this exchange decreases their energy
   * sampleAndSwap() uses findPartner
   */
  private void sampleAndSwap(int nodeId) {
    // set partner node to null
    Node partner = null; 
    // get node p based on nodeId provided
    Node nodep = entireGraph.get(nodeId);
    // use hybrid heuristic for node selection
    // 1. first try with local policy
    if (config.getNodeSelectionPolicy() == NodeSelectionPolicy.HYBRID
            || config.getNodeSelectionPolicy() == NodeSelectionPolicy.LOCAL) {
      // swap with random neighbors
      // find partner to node p
      partner = findPartner(nodeId, getNeighbors(nodep));
      
    }
    // 2. try with random sample if local policy is not selected
    if (config.getNodeSelectionPolicy() == NodeSelectionPolicy.HYBRID
            || config.getNodeSelectionPolicy() == NodeSelectionPolicy.RANDOM) {
      // if local policy fails then randomly sample the entire graph
      // TODO -> done
      if (partner == null){
        partner = findPartner(nodeId, getSample(nodeId));
      }
    }

    // swap the colors
    // TODO -> done
    if(partner != null){
      int color = partner.getColor();
      partner.setColor(nodep.getColor());
      nodep.setColor(color);
      numberOfSwaps++;
    }
    
  }

  public Node findPartner(int nodeId, Integer[] nodes){

    Node nodep = entireGraph.get(nodeId);

    Node bestPartner = null;
    double highestBenefit = 0;
    
    // alpha is the parameter of the energy function
    double alpha = config.getAlpha();

    // TODO -> done
    for(Integer i : nodes){
      // get node q
      Node nodeq = entireGraph.get(i);
      
      // # of neighbors of node p with color like p
      int d_pp = getDegree(nodep, nodep.getColor());
      
      // # of neighbors of node q with color like q
      int d_qq = getDegree(nodeq, nodeq.getColor());
      
      // old degree -> neighbors with same color
      double old_d = Math.pow(d_pp, alpha) + Math.pow(d_qq, alpha);
      
      // --------------------------------------------------------------
      // Uncomment the following section for Task 1, and comment Task 2
      // --------------------------------------------------------------
      // /*
      // # of neighbors of node p with color like q
      int d_pq = getDegree(nodep, nodeq.getColor());
      
      // # of neighbors of node q with color like p
      int d_qp = getDegree(nodeq, nodep.getColor());
      
      // new degree -> neighbors with different colours
      double new_d = Math.pow(d_pq, alpha) + Math.pow(d_qp, alpha);
      
      // the parameter T is for simulated annealing
      // if there are more colors similar to p in the 
      // neighbourhood of q, then the new best partner is q
      if(new_d*T>old_d && new_d > highestBenefit){
        bestPartner = nodeq;
        highestBenefit = new_d;
      }
      // */
      
      // --------------------------------------------------------------
      // Uncomment the following section for Task 2, and comment Task 1
      // --------------------------------------------------------------
       /*
      // introduce iterations to improve performance
      // int iter = 0;
      // while(iter<100){
      // # of neighbors of node p with color like q
      int d_pq = getDegree(nodep, nodeq.getColor());
      
      // # of neighbors of node q with color like p
      int d_qp = getDegree(nodeq, nodep.getColor());
      
      // new degree -> neighbors with different colours
      double new_d = Math.pow(d_pq, alpha) + Math.pow(d_qp, alpha);
      
      // compute acceptance probability: [0,1]
      double accept_prob = Math.pow(Math.E,(new_d-old_d)/T);
      
      // generate random # to compare with accepance probability
      double rand_num = (double)RandNoGenerator.nextInt(1000)/(double)1000;
      
      // randomly select new_d based on acceptance probability
      if (accept_prob > rand_num && new_d > highestBenefit){
        bestPartner = nodeq;
        highestBenefit = new_d;
      }     
	      // iter++;
      // }
       */
    }
    return bestPartner;
  }

  /**
   * The the degreee on the node based on 
   * @param node
   * @param colorId
   * @return how many neighbors of the node have color == colorId
   */
  private int getDegree(Node node, int colorId){
    int degree = 0;
    for(int neighborId : node.getNeighbours()){
      Node neighbor = entireGraph.get(neighborId);
      if(neighbor.getColor() == colorId){
        degree++;
      }
    }
    return degree;
  }

  /**
   * Returns a uniformly random sample of the graph
   * @param currentNodeId
   * @return Returns a uniformly random sample of the graph
   */
  private Integer[] getSample(int currentNodeId) {
    int count = config.getUniformRandomSampleSize();
    int rndId;
    int size = entireGraph.size();
    ArrayList<Integer> rndIds = new ArrayList<Integer>();

    while (true) {
      rndId = nodeIds.get(RandNoGenerator.nextInt(size));
      if (rndId != currentNodeId && !rndIds.contains(rndId)) {
        rndIds.add(rndId);
        count--;
      }

      if (count == 0)
        break;
    }

    Integer[] ids = new Integer[rndIds.size()];
    return rndIds.toArray(ids);
  }

  /**
   * Get random neighbors. The number of random neighbors is controlled using
   * -closeByNeighbors command line argument which can be obtained from the config
   * using {@link Config#getRandomNeighborSampleSize()}
   * @param node
   * @return
   */
  private Integer[] getNeighbors(Node node) {
    ArrayList<Integer> list = node.getNeighbours();
    int count = config.getRandomNeighborSampleSize();
    int rndId;
    int index;
    int size = list.size();
    ArrayList<Integer> rndIds = new ArrayList<Integer>();

    if (size <= count)
      rndIds.addAll(list);
    else {
      while (true) {
        index = RandNoGenerator.nextInt(size);
        rndId = list.get(index);
        if (!rndIds.contains(rndId)) {
          rndIds.add(rndId);
          count--;
        }

        if (count == 0)
          break;
      }
    }

    Integer[] arr = new Integer[rndIds.size()];
    return rndIds.toArray(arr);
  }


  /**
   * Generate a report which is stored in a file in the output dir.
   *
   * @throws IOException
   */
  private void report() throws IOException {
    int grayLinks = 0;
    int migrations = 0; // number of nodes that have changed the initial color
    int size = entireGraph.size();

    for (int i : entireGraph.keySet()) {
      Node node = entireGraph.get(i);
      int nodeColor = node.getColor();
      ArrayList<Integer> nodeNeighbours = node.getNeighbours();

      if (nodeColor != node.getInitColor()) {
        migrations++;
      }

      if (nodeNeighbours != null) {
        for (int n : nodeNeighbours) {
          Node p = entireGraph.get(n);
          int pColor = p.getColor();

          if (nodeColor != pColor)
            grayLinks++;
        }
      }
    }

    int edgeCut = grayLinks / 2;
    // suppress logger except last round
    if (round == config.getRounds()-1){
    logger.info("round: " + round +
            ", edge cut:" + edgeCut +
            ", swaps: " + numberOfSwaps +
            ", migrations: " + migrations);
    }
    saveToFile(edgeCut, migrations);
  }

  private void saveToFile(int edgeCuts, int migrations) throws IOException {
    String delimiter = "\t\t";
    String outputFilePath;

    //output file name
    File inputFile = new File(config.getGraphFilePath());
    outputFilePath = config.getOutputDir() +
            File.separator +
            inputFile.getName() + "_" +
            "NS" + "_" + config.getNodeSelectionPolicy() + "_" +
            "GICP" + "_" + config.getGraphInitialColorPolicy() + "_" +
            "T" + "_" + config.getTemperature() + "_" +
            "D" + "_" + config.getDelta() + "_" +
            "RNSS" + "_" + config.getRandomNeighborSampleSize() + "_" +
            "URSS" + "_" + config.getUniformRandomSampleSize() + "_" +
            "A" + "_" + config.getAlpha() + "_" +
            "R" + "_" + config.getRounds() + ".txt";

    if (!resultFileCreated) {
      File outputDir = new File(config.getOutputDir());
      if (!outputDir.exists()) {
        if (!outputDir.mkdir()) {
          throw new IOException("Unable to create the output directory");
        }
      }
      // create folder and result file with header
      String header = "# Migration is number of nodes that have changed color.";
      header += "\n\nRound" + delimiter + "Edge-Cut" + delimiter + "Swaps" + delimiter + "Migrations" + delimiter + "Skipped" + "\n";
      FileIO.write(header, outputFilePath);
      resultFileCreated = true;
    }

    FileIO.append(round + delimiter + (edgeCuts) + delimiter + numberOfSwaps + delimiter + migrations + "\n", outputFilePath);
  }
}
