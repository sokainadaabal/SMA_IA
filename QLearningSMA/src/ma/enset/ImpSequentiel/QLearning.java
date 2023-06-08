package ma.enset.ImpSequentiel;

import ma.enset.QLUtils;

public class QLearning {
    private int [][] grid ; //Créer la grille qui va stocker les récompenses
    private double [][] qTable = new double[QLUtils.GRID_SIZE*QLUtils.GRID_SIZE][QLUtils.Action_SIZE]; // declaration de q-table

    private int [][] actions;


}
