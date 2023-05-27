package ma.enset.bddc.gasma;

import java.util.Arrays;

public class GAApplication {


    public static void main(String[] args) {
      GenticAlgorithm ga=new GenticAlgorithm();
      ga.initialize();
      ga.sortPopulation();
      ga.showPopulation();
      int iter=0;
      while (GAUtils.MAX_ITERATIONS>iter &&  ga.getBestFintness()< GAUtils.CHROMOSOME_SIZE){
          System.out.println("Iteration : "+iter);
          ga.crossover();
          ga.mutation();
          ga.sortPopulation();
          ga.showPopulation();
          System.out.println("iteration :"+iter+" -> "+Arrays.toString(ga.getPopulation()[0].getChromosome())+" : "+ga.getBestFintness());
          iter++;
      }

    }
}
