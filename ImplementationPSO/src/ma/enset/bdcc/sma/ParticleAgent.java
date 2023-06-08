package ma.enset.bdcc.sma;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class ParticleAgent extends Agent {
    private Particle particle;
    private double[] globalBest;
    private double globalBestFitness;

    protected void setup() {
        particle = new Particle(PSOUtils.DIMENSIONS);
        globalBest = new double[PSOUtils.DIMENSIONS];
        globalBestFitness = Double.POSITIVE_INFINITY;

        SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                if (particle.getPersonalBestFitness() < globalBestFitness) {
                    globalBestFitness = particle.getPersonalBestFitness();
                    System.arraycopy(particle.getPersonalBest(), 0, globalBest, 0, PSOUtils.DIMENSIONS);
                }
            }
        });

        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            private int iteration = 0;

            @Override
            public void action() {
                particle.updateVelocity(globalBest);
                particle.updatePosition();

                double fitness = particle.fitness(particle.getPosition());
                if (fitness < particle.getPersonalBestFitness()) {
                    particle.setPersonalBestFitness(fitness);
                    System.arraycopy(particle.getPosition(), 0, particle.getPersonalBest(), 0, PSOUtils.DIMENSIONS);
                }

                if (fitness < globalBestFitness) {
                    globalBestFitness = fitness;
                    System.arraycopy(particle.getPosition(), 0, globalBest, 0, PSOUtils.DIMENSIONS);
                }

                System.out.println("N° Iteration: " + (iteration + 1) + ", Agent: " + getLocalName() + ", Meilleur Fitness: " + globalBestFitness);

                iteration++;

                if (iteration >= PSOUtils.MAX_ITERATIONS) {
                    doDelete();
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });

        addBehaviour(sequentialBehaviour);
    }

    protected void takeDown() {
        System.out.println("Agent " + getLocalName() + " est terminer.");
    }
}
