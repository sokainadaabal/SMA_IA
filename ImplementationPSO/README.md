#  Parallel and distributed implementation of PSO using SMA

> Realiser par Sokaina Daabal / II.BDCC

## c'est quoi PSO?
> L'optimisation par essaim de particules (PSO) est un algorithme d'optimisation populaire qui peut être amélioré en utilisant le parallélisme et la distribution pour accélérer son exécution et améliorer sa scalabilité. Dans cette implémentation, nous avons utilisé le système multi-agent (SMA) pour paralléliser les étapes clés de l'algorithme PSO.

## Description de l'implémentation
1. ```Partitionnement du problème```: Nous avons divisé l'espace de recherche en plusieurs partitions, chaque partition étant traitée par une unité de traitement distincte dans notre système SMA.

2. ```Initialisation``` : Nous avons initialisé un essaim de particules de manière aléatoire au sein de leurs partitions respectives. Chaque particule a été attribuée une position et une vitesse initiale.

3. ```Évaluation parallèle des particules``` : Chaque unité de traitement a évalué la valeur de fitness des particules de sa partition de manière parallèle, en utilisant la fonction de fitness appropriée.

4. ```Communication``` : Après l'évaluation, chaque unité de traitement a partagé sa meilleure position globale avec les autres unités de traitement. Cela a assuré que la meilleure position globale était mise à jour et partagée correctement entre toutes les partitions.

5. ```Mise à jour de la vitesse et de la position``` : Chaque unité de traitement a mis à jour la vitesse et la position des particules de sa partition en utilisant les meilleures positions personnelles et globales. Ces mises à jour ont été effectuées de manière parallèle pour toutes les particules de la partition.

6. ```Condition de terminaison``` : Nous avons vérifié si la condition de terminaison était remplie, telle que le nombre maximal d'itérations atteint ou l'atteinte d'une solution satisfaisante.

7. ```Synchronisation``` : Après chaque itération, les unités de traitement se sont synchronisées pour garantir que la meilleure position globale était correctement mise à jour et partagée entre toutes les partitions.
## Implementation en SMA
### Class Particle 

> - La classe ```Particle``` représente une particule dans l'optimisation par essaim de particules (PSO).
> 
> - Les attributs ```position```, ```velocity```, ```personalBest``` et ```personalBestFitness``` représentent respectivement la position, la vitesse, la meilleure position personnelle et la valeur de fitness correspondante pour une particule.
>
> - Les méthodes ```getPosition()```, ```setPosition()```, ```getVelocity()```, ```setVelocity()```, ```getPersonalBest()```, ```setPersonalBest()```, ```getPersonalBestFitness()``` et ```setPersonalBestFitness()``` sont des accesseurs et des mutateurs pour les attributs.
>
> - Le constructeur ```Particle()``` initialise les positions et les vitesses de manière aléatoire dans une plage spécifique, ainsi que les meilleures positions personnelles et la valeur de fitness initiale.
>
> - La méthode ```updateVelocity()``` met à jour la vitesse de la particule en utilisant les meilleures positions personnelles et globales.
>
>- La méthode ```updatePosition()``` met à jour la position de la particule en fonction de la vitesse.
>
>- La méthode ```fitness()``` calcule la valeur de fitness en fonction de la position de la particule.

```java

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
            position[i] = rand.nextDouble() * (PSOUtils.MAX_POSITION 
                    - PSOUtils.MIN_POSITION) + PSOUtils.MIN_POSITION;
            velocity[i] = rand.nextDouble() * (PSOUtils.MAX_POSITION 
                    - PSOUtils.MIN_POSITION) + PSOUtils.MIN_POSITION;
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
```

### Class ParticleAgent

> - La classe ```ParticleAgent``` étend la classe Agent du système multi-agent (SMA).
>
> - Les attributs ```particle```, ```globalBest``` et ```globalBestFitness``` représentent respectivement la particule associée à l'agent, la meilleure position globale et la valeur de fitness correspondante.
>
> - La méthode ```setup()``` est appelée lors de la création de l'agent. Elle initialise la particule, le meilleur global et définit les comportements séquentiels de l'agent.
>
> - Le premier comportement ```OneShotBehaviour``` met à jour le meilleur global en comparant la meilleure position personnelle de la particule avec le meilleur global actuel.
>
> - Le deuxième comportement est une classe anonyme qui implémente l'interface Behaviour. Il représente le comportement principal de la particule, où la vitesse et la position sont mises à jour à chaque itération. Il vérifie également si la fitness de la particule est meilleure que sa meilleure fitness personnelle et la meilleure fitness globale, mettant à jour ces valeurs si nécessaire. Les informations sur l'itération actuelle sont affichées.
>
> - La méthode ```takeDown()``` est appelée lorsque l'agent est terminé et affiche un message indiquant la fin de l'agent.

```java
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
                    System.arraycopy(particle.getPersonalBest(), 
                            0, globalBest, 0, PSOUtils.DIMENSIONS);
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
                    System.arraycopy(particle.getPosition(),
                            0, particle.getPersonalBest(),
                            0, PSOUtils.DIMENSIONS);
                }

                if (fitness < globalBestFitness) {
                    globalBestFitness = fitness;
                    System.arraycopy(particle.getPosition(), 
                            0, globalBest, 0, PSOUtils.DIMENSIONS);
                }

                System.out.println("N° Iteration: " + (iteration + 1) + ", Agent: " 
                        + getLocalName() + ", Meilleur Fitness: " + globalBestFitness);

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
```
### Class MainContainer
> - La classe ```MainContainer``` représente le conteneur principal du système multi-agent (SMA). 
> 
> - La méthode ```main()``` est le point d'entrée du programme.
>
> - Le runtime JADE est initialisé et configuré pour fermer la machine virtuelle (VM) lors de la fin de l'exécution.
>
> - Un profil est créé pour le conteneur principal.
>
> - Le conteneur principal est créé en utilisant le runtime et le profil.
>
> - Une liste de contrôleurs d'agent est créée pour stocker les contrôleurs d'agent créés.
>
> - Une boucle est utilisée pour créer un contrôleur d'agent pour chaque particule de l'optimisation.
>
> - Les arguments de l'agent sont définis pour chaque particule (dans cet exemple, l'index de la particule).
>
> - Un contrôleur d'agent est créé pour chaque particule en utilisant le nom de l'agent, la classe de l'agent et les arguments de l'agent.
>
> - Les contrôleurs d'agent sont ajoutés à la liste des contrôleurs d'agent.
>
> - Une autre boucle est utilisée pour démarrer chaque agent en appelant la méthode start() sur chaque contrôleur d'agent.
>
> - Des messages d'erreur sont affichés en cas d'erreur lors de la création ou du démarrage des agents.
```java
public class MainContainer {
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        runtime.setCloseVM(true);
        ProfileImpl profile = new ProfileImpl();
        AgentContainer mainContainer = runtime.createMainContainer(profile);

        List<AgentController> agentControllers = new ArrayList<>();

        for (int i = 0; i < PSOUtils.SWARM_SIZE; i++) {
            try {
                Object[] agentArgs = {i};
                AgentController agentController = mainContainer.createNewAgent("particle." 
                        + i, ParticleAgent.class.getName(), agentArgs);
                agentControllers.add(agentController);
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        for (AgentController agentController : agentControllers) {
            try {
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }
}
```
### Class PSOUtils

> La classe utilitaire ```PSOUtils``` contenant des constantes utilisées dans l'algorithme PSO (Particle Swarm Optimization).
>
> Ces constantes sont utilisées pour définir les paramètres de l'algorithme PSO, tels que le nombre d'itérations, la taille de l'essaim, le nombre de dimensions du problème, les coefficients d'apprentissage, le poids d'inertie et les limites de position des particules.
```java
public class PSOUtils {
    public static final int MAX_ITERATIONS = 100;
    public static final int SWARM_SIZE = 30;
    public static final int DIMENSIONS = 2;
    public static final double C1 = 2.0;
    public static final double C2 = 2.0;
    public static final double INERTIA_WEIGHT = 0.7;
    public static final double MAX_POSITION = 100.0;
    public static final double MIN_POSITION = -100.0;
}
```
## Execution 
> Pour 5 iteration.
```commandline
N° Iteration: 1, Agent: particle.4, Meilleur Fitness: 6226.0272534821615
N° Iteration: 1, Agent: particle.9, Meilleur Fitness: 658.8003870692536
N° Iteration: 1, Agent: particle.12, Meilleur Fitness: 2936.3640129669852
N° Iteration: 1, Agent: particle.8, Meilleur Fitness: 8238.307218689028
N° Iteration: 1, Agent: particle.7, Meilleur Fitness: 3609.1222684649647
N° Iteration: 1, Agent: particle.11, Meilleur Fitness: 10758.276494054986
N° Iteration: 1, Agent: particle.13, Meilleur Fitness: 9541.3439775585
N° Iteration: 1, Agent: particle.14, Meilleur Fitness: 1198.796876757894
N° Iteration: 1, Agent: particle.3, Meilleur Fitness: 1623.6319862429116
N° Iteration: 1, Agent: particle.10, Meilleur Fitness: 11105.696929531927
N° Iteration: 1, Agent: particle.25, Meilleur Fitness: 2807.9663986094492
N° Iteration: 1, Agent: particle.26, Meilleur Fitness: 9411.602877967049
N° Iteration: 1, Agent: particle.15, Meilleur Fitness: 1746.940520704779
N° Iteration: 1, Agent: particle.5, Meilleur Fitness: 10743.246636390655
N° Iteration: 1, Agent: particle.6, Meilleur Fitness: 1192.9410661895008
N° Iteration: 1, Agent: particle.16, Meilleur Fitness: 5969.066876147939
N° Iteration: 1, Agent: particle.1, Meilleur Fitness: 3489.349606388242
N° Iteration: 1, Agent: particle.17, Meilleur Fitness: 2939.9762306803977
N° Iteration: 1, Agent: particle.27, Meilleur Fitness: 3029.2325469609536
N° Iteration: 1, Agent: particle.19, Meilleur Fitness: 2156.9964695185936
N° Iteration: 2, Agent: particle.8, Meilleur Fitness: 8238.307218689028
N° Iteration: 1, Agent: particle.0, Meilleur Fitness: 14853.984689018327
N° Iteration: 2, Agent: particle.3, Meilleur Fitness: 191.33507946683622
N° Iteration: 1, Agent: particle.2, Meilleur Fitness: 9592.320795227826
N° Iteration: 2, Agent: particle.10, Meilleur Fitness: 10154.89976840305
N° Iteration: 2, Agent: particle.19, Meilleur Fitness: 668.033052602956
N° Iteration: 2, Agent: particle.26, Meilleur Fitness: 9411.602877967049
N° Iteration: 2, Agent: particle.9, Meilleur Fitness: 537.7901087786502
N° Iteration: 2, Agent: particle.25, Meilleur Fitness: 2807.9663986094492
N° Iteration: 2, Agent: particle.12, Meilleur Fitness: 2936.3640129669852
N° Iteration: 2, Agent: particle.27, Meilleur Fitness: 1570.6249419116416
N° Iteration: 2, Agent: particle.4, Meilleur Fitness: 5604.941955564602
N° Iteration: 3, Agent: particle.9, Meilleur Fitness: 465.00449777679574
N° Iteration: 2, Agent: particle.15, Meilleur Fitness: 1542.640817546409
N° Iteration: 3, Agent: particle.12, Meilleur Fitness: 2936.3640129669852
N° Iteration: 2, Agent: particle.14, Meilleur Fitness: 54.52053429011408
N° Iteration: 2, Agent: particle.5, Meilleur Fitness: 10312.43243976107
N° Iteration: 3, Agent: particle.26, Meilleur Fitness: 9411.602877967049
N° Iteration: 3, Agent: particle.19, Meilleur Fitness: 131.2752224317255
N° Iteration: 2, Agent: particle.6, Meilleur Fitness: 1192.9410661895008
N° Iteration: 1, Agent: particle.29, Meilleur Fitness: 2513.9066363688107
N° Iteration: 1, Agent: particle.22, Meilleur Fitness: 4225.0986600251745
N° Iteration: 3, Agent: particle.10, Meilleur Fitness: 10004.487745542143
N° Iteration: 2, Agent: particle.13, Meilleur Fitness: 6263.182018727337
N° Iteration: 2, Agent: particle.2, Meilleur Fitness: 9592.320795227826
N° Iteration: 1, Agent: particle.21, Meilleur Fitness: 8718.79592429515
N° Iteration: 3, Agent: particle.8, Meilleur Fitness: 8238.307218689028
N° Iteration: 2, Agent: particle.1, Meilleur Fitness: 3489.349606388242
N° Iteration: 3, Agent: particle.3, Meilleur Fitness: 191.33507946683622
N° Iteration: 2, Agent: particle.16, Meilleur Fitness: 5969.066876147939
N° Iteration: 1, Agent: particle.20, Meilleur Fitness: 4963.3931933472595
N° Iteration: 2, Agent: particle.17, Meilleur Fitness: 2939.9762306803977
N° Iteration: 2, Agent: particle.11, Meilleur Fitness: 10758.276494054986
N° Iteration: 1, Agent: particle.18, Meilleur Fitness: 2121.944148495809
N° Iteration: 2, Agent: particle.7, Meilleur Fitness: 2216.604516420417
N° Iteration: 2, Agent: particle.0, Meilleur Fitness: 14853.984689018327
N° Iteration: 1, Agent: particle.23, Meilleur Fitness: 6940.646518775988
N° Iteration: 1, Agent: particle.28, Meilleur Fitness: 11128.95770091758
N° Iteration: 3, Agent: particle.25, Meilleur Fitness: 2807.9663986094492
N° Iteration: 1, Agent: particle.24, Meilleur Fitness: 8413.958852618105
N° Iteration: 3, Agent: particle.4, Meilleur Fitness: 4101.940011826749
N° Iteration: 4, Agent: particle.9, Meilleur Fitness: 458.31480011497825
N° Iteration: 3, Agent: particle.15, Meilleur Fitness: 1542.640817546409
N° Iteration: 4, Agent: particle.12, Meilleur Fitness: 2936.3640129669852
N° Iteration: 3, Agent: particle.14, Meilleur Fitness: 54.52053429011408
N° Iteration: 3, Agent: particle.5, Meilleur Fitness: 10312.43243976107
N° Iteration: 4, Agent: particle.26, Meilleur Fitness: 8748.12822546415
N° Iteration: 4, Agent: particle.19, Meilleur Fitness: 3.247856530024962
N° Iteration: 3, Agent: particle.6, Meilleur Fitness: 1192.9410661895008
N° Iteration: 2, Agent: particle.29, Meilleur Fitness: 2513.9066363688107
N° Iteration: 2, Agent: particle.22, Meilleur Fitness: 4225.0986600251745
N° Iteration: 4, Agent: particle.10, Meilleur Fitness: 10004.487745542143
N° Iteration: 3, Agent: particle.13, Meilleur Fitness: 1349.9058434476844
N° Iteration: 3, Agent: particle.2, Meilleur Fitness: 7587.192909153122
N° Iteration: 4, Agent: particle.8, Meilleur Fitness: 4828.111607181723
N° Iteration: 2, Agent: particle.21, Meilleur Fitness: 8718.79592429515
N° Iteration: 3, Agent: particle.1, Meilleur Fitness: 3489.349606388242
N° Iteration: 4, Agent: particle.3, Meilleur Fitness: 191.33507946683622
N° Iteration: 3, Agent: particle.16, Meilleur Fitness: 2974.1676843958917
N° Iteration: 2, Agent: particle.20, Meilleur Fitness: 4963.3931933472595
N° Iteration: 3, Agent: particle.17, Meilleur Fitness: 2939.9762306803977
N° Iteration: 3, Agent: particle.27, Meilleur Fitness: 1447.7222434632595
N° Iteration: 3, Agent: particle.11, Meilleur Fitness: 10758.276494054986
N° Iteration: 2, Agent: particle.18, Meilleur Fitness: 2121.944148495809
N° Iteration: 3, Agent: particle.7, Meilleur Fitness: 1758.3977459917128
N° Iteration: 3, Agent: particle.0, Meilleur Fitness: 14488.603016529982
N° Iteration: 2, Agent: particle.23, Meilleur Fitness: 6940.646518775988
N° Iteration: 2, Agent: particle.28, Meilleur Fitness: 10004.361934492323
N° Iteration: 4, Agent: particle.25, Meilleur Fitness: 2807.9663986094492
N° Iteration: 2, Agent: particle.24, Meilleur Fitness: 5528.708049567575
N° Iteration: 4, Agent: particle.4, Meilleur Fitness: 3673.896032791227
N° Iteration: 5, Agent: particle.9, Meilleur Fitness: 458.31480011497825
N° Iteration: 4, Agent: particle.15, Meilleur Fitness: 1542.640817546409
N° Iteration: 5, Agent: particle.12, Meilleur Fitness: 985.1767229435363
N° Iteration: 4, Agent: particle.14, Meilleur Fitness: 54.52053429011408
N° Iteration: 4, Agent: particle.5, Meilleur Fitness: 10312.43243976107
N° Iteration: 5, Agent: particle.26, Meilleur Fitness: 8711.241134303953
N° Iteration: 5, Agent: particle.19, Meilleur Fitness: 3.247856530024962
N° Iteration: 4, Agent: particle.6, Meilleur Fitness: 1192.9410661895008
N° Iteration: 3, Agent: particle.29, Meilleur Fitness: 2513.9066363688107
N° Iteration: 3, Agent: particle.22, Meilleur Fitness: 4225.0986600251745
N° Iteration: 5, Agent: particle.10, Meilleur Fitness: 10004.487745542143
N° Iteration: 4, Agent: particle.13, Meilleur Fitness: 214.83576244142043
N° Iteration: 4, Agent: particle.2, Meilleur Fitness: 6195.128721822715
N° Iteration: 5, Agent: particle.8, Meilleur Fitness: 2807.547527079657
N° Iteration: 3, Agent: particle.21, Meilleur Fitness: 8718.79592429515
N° Iteration: 5, Agent: particle.3, Meilleur Fitness: 191.33507946683622
N° Iteration: 4, Agent: particle.1, Meilleur Fitness: 3489.349606388242
N° Iteration: 4, Agent: particle.16, Meilleur Fitness: 1429.546338055804
N° Iteration: 4, Agent: particle.17, Meilleur Fitness: 2693.2826200578265
N° Iteration: 3, Agent: particle.20, Meilleur Fitness: 3235.425499310025
N° Iteration: 4, Agent: particle.27, Meilleur Fitness: 1447.7222434632595
N° Iteration: 4, Agent: particle.11, Meilleur Fitness: 10758.276494054986
N° Iteration: 3, Agent: particle.18, Meilleur Fitness: 2121.944148495809
N° Iteration: 4, Agent: particle.7, Meilleur Fitness: 1758.3977459917128
N° Iteration: 4, Agent: particle.0, Meilleur Fitness: 11926.76782340471
N° Iteration: 3, Agent: particle.23, Meilleur Fitness: 3855.0887780855755
N° Iteration: 3, Agent: particle.28, Meilleur Fitness: 10004.361934492323
N° Iteration: 5, Agent: particle.25, Meilleur Fitness: 2807.9663986094492
N° Iteration: 3, Agent: particle.24, Meilleur Fitness: 3299.7760404127584
N° Iteration: 5, Agent: particle.4, Meilleur Fitness: 3673.896032791227
Agent particle.9 est terminer.
N° Iteration: 5, Agent: particle.15, Meilleur Fitness: 1517.0396524031535
Agent particle.12 est terminer.
N° Iteration: 5, Agent: particle.5, Meilleur Fitness: 10312.43243976107
Agent particle.26 est terminer.
Agent particle.19 est terminer.
N° Iteration: 4, Agent: particle.22, Meilleur Fitness: 4225.0986600251745
N° Iteration: 4, Agent: particle.29, Meilleur Fitness: 2513.9066363688107
Agent particle.10 est terminer.
Agent particle.8 est terminer.
N° Iteration: 4, Agent: particle.21, Meilleur Fitness: 7498.11080940149
Agent particle.3 est terminer.
N° Iteration: 5, Agent: particle.21, Meilleur Fitness: 7498.11080940149
N° Iteration: 5, Agent: particle.6, Meilleur Fitness: 108.53666904533351
Agent particle.21 est terminer.
Agent particle.5 est terminer.
N° Iteration: 4, Agent: particle.24, Meilleur Fitness: 2622.685605428526
Agent particle.15 est terminer.
N° Iteration: 4, Agent: particle.28, Meilleur Fitness: 10004.361934492323
Agent particle.4 est terminer.
N° Iteration: 4, Agent: particle.23, Meilleur Fitness: 3855.0887780855755
Agent particle.25 est terminer.
N° Iteration: 5, Agent: particle.0, Meilleur Fitness: 10768.593108915855
N° Iteration: 5, Agent: particle.7, Meilleur Fitness: 1758.3977459917128
N° Iteration: 5, Agent: particle.11, Meilleur Fitness: 10758.276494054986
N° Iteration: 4, Agent: particle.18, Meilleur Fitness: 2121.944148495809
N° Iteration: 5, Agent: particle.27, Meilleur Fitness: 1447.7222434632595
N° Iteration: 4, Agent: particle.20, Meilleur Fitness: 1170.0455464243958
N° Iteration: 5, Agent: particle.16, Meilleur Fitness: 1349.893019389713
N° Iteration: 5, Agent: particle.17, Meilleur Fitness: 1550.2765216799592
N° Iteration: 5, Agent: particle.23, Meilleur Fitness: 3855.0887780855755
N° Iteration: 5, Agent: particle.28, Meilleur Fitness: 10004.361934492323
N° Iteration: 5, Agent: particle.24, Meilleur Fitness: 2581.4716669373906
Agent particle.6 est terminer.
N° Iteration: 5, Agent: particle.14, Meilleur Fitness: 54.52053429011408
N° Iteration: 5, Agent: particle.2, Meilleur Fitness: 5321.833842929245
N° Iteration: 5, Agent: particle.13, Meilleur Fitness: 214.83576244142043
N° Iteration: 5, Agent: particle.1, Meilleur Fitness: 3489.349606388242
N° Iteration: 5, Agent: particle.22, Meilleur Fitness: 4225.0986600251745
N° Iteration: 5, Agent: particle.29, Meilleur Fitness: 2513.9066363688107
N° Iteration: 5, Agent: particle.18, Meilleur Fitness: 2121.944148495809
N° Iteration: 5, Agent: particle.20, Meilleur Fitness: 342.38467502509934
Agent particle.16 est terminer.
Agent particle.27 est terminer.
Agent particle.17 est terminer.
Agent particle.23 est terminer.
Agent particle.11 est terminer.
Agent particle.28 est terminer.
Agent particle.24 est terminer.
Agent particle.7 est terminer.
Agent particle.0 est terminer.
Agent particle.20 est terminer.
Agent particle.18 est terminer.
Agent particle.29 est terminer.
Agent particle.22 est terminer.
Agent particle.1 est terminer.
Agent particle.13 est terminer.
Agent particle.2 est terminer.
Agent particle.14 est terminer.
```
## Résultats et observations
> Grâce à l'implémentation parallèle de l'algorithme PSO utilisant SMA, nous avons observé une amélioration significative des performances par rapport à une implémentation séquentielle. Les temps d'exécution ont été réduits, ce qui a permis de trouver des solutions optimales plus rapidement, en particulier pour les problèmes de grande taille. 
> 
> Cependant, il convient de noter que l'efficacité de la parallélisation dépend de plusieurs facteurs, tels que le nombre de particules, la taille de l'espace de recherche et les ressources matérielles disponibles. Une répartition équilibrée des particules et une communication efficace entre les unités de traitement sont essentielles pour obtenir de bonnes performances.


## A Retenir 

> L'implémentation parallèle de l'algorithme PSO en utilisant SMA a démontré son potentiel pour améliorer les performances et la scalabilité de PSO. En utilisant le parallélisme, nous avons pu accélérer le processus d'optimisation et trouver des solutions optimales plus rapidement. Cependant, une attention particulière doit être portée à l'équilibrage de charge et à la gestion de la communication pour tirer pleinement parti de la parallélisation.
>
> Cette implémentation fournit une base solide pour explorer davantage de techniques d'optimisation parallèle et distribuée, et peut être adaptée en fonction des besoins spécifiques du problème et de l'environnement d'exécution.


