package ma.enset;

public class QLUtils {
   public static final double ALPHA=0.1;
    public static final double GAMMA = 0.9;
    public static final double EPS = 0.3; // epsilon
    public static final int MAX_EPOCH=2000;
    public static final int GRID_SIZE=6;

    public static final int Action_SIZE=4;


    public  static final int[][]  actions=new int [][]{
            {0,-1}, // gauche
            {0,1}, // droite
            {1,0}, // bas
            {-1,0}  // haut
    };

   public static final int AGENTS_NUMBER = 3;

    public static final int[][] GRID={
            //0=rien, -1 obstacte, 1=arrivée
            {0, 0, 0, 0, 0,-1},
            {0, 0, 0, 0,-1, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0,-1,-1, 0, 0},
            {0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1}
    }; // presente les recompense
}

