package ma.enset.bdcc.sma;

import java.util.Random;

public class Particle {
    private double[] position;
    private double[] velocity;
    private double[] personalBest;
    private double personalBestFitness;

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public double[] getPersonalBest() {
        return personalBest;
    }

    public void setPersonalBest(double[] personalBest) {
        this.personalBest = personalBest;
    }

    public double getPersonalBestFitness() {
        return personalBestFitness;
    }

    public void setPersonalBestFitness(double personalBestFitness) {
        this.personalBestFitness = personalBestFitness;
    }

    Particle(int dimensions) {
        position = new double[dimensions];
        velocity = new double[dimensions];
        personalBest = new double[dimensions];

        Random rand = new Random();
        for (int i = 0; i < dimensions; i++) {
            position[i] = rand.nextDouble() * (PSOUtils.MAX_POSITION - PSOUtils.MIN_POSITION) + PSOUtils.MIN_POSITION;
            velocity[i] = rand.nextDouble() * (PSOUtils.MAX_POSITION - PSOUtils.MIN_POSITION) + PSOUtils.MIN_POSITION;
            personalBest[i] = position[i];
        }

        personalBestFitness = fitness(position);
    }

    void updateVelocity(double[] globalBest) {
        Random rand = new Random();
        for (int i = 0; i < velocity.length; i++) {
            velocity[i] = PSOUtils.INERTIA_WEIGHT * velocity[i]
                    + PSOUtils.C1 * rand.nextDouble() * (personalBest[i] - position[i])
                    + PSOUtils.C2 * rand.nextDouble() * (globalBest[i] - position[i]);
        }
    }

    void updatePosition() {
        for (int i = 0; i < position.length; i++) {
            position[i] += velocity[i];

            if (position[i] < PSOUtils.MIN_POSITION) {
                position[i] = PSOUtils.MIN_POSITION;
            } else if (position[i] > PSOUtils.MAX_POSITION) {
                position[i] = PSOUtils.MAX_POSITION;
            }
        }
    }

    double fitness(double[] position) {
        double sum = 0.0;
        for (double p : position) {
            sum += p * p;
        }
        return sum;
    }
}
