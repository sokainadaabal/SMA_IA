package ma.enset.ImpSma;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ma.enset.QLUtils;



import java.util.Random;

public class QLAgent extends Agent {
    private double [][] qTable = new double[QLUtils.GRID_SIZE*QLUtils.GRID_SIZE][QLUtils.Action_SIZE]; // declaration de q-table
    // definir les etats
    private int stateX,stateY;

    @Override
    public void setup(){
        System.out.println("Bonjour Agent :"+getAID().getName() +" est pres pour trouver le but final");
        SequentialBehaviour sb= new SequentialBehaviour();
        sb.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                resetState();
            }
        });
         sb.addSubBehaviour(new Behaviour() {
            int itera;
            @Override
            public void action() {
                runQLearning();
            }
            @Override
            public boolean done() {
                return finished() || itera>=QLUtils.MAX_EPOCH ;
            }
        });
        sb.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                //printQTable();
                EnvoyerQTable();
            }
        });
        addBehaviour(sb);
    }
    // pour le choix de l'action
    private int chooseAction(double eps){
        Random rn = new Random();
        double meilleurQ=Double.MIN_VALUE;
        int actionIndex=0;
        if( rn.nextDouble()<eps){
            //Exploration
            actionIndex = rn.nextInt(QLUtils.Action_SIZE);
        }else {
            //Exploitation
            int st=stateX*QLUtils.GRID_SIZE+stateY;
            //choisir la meilleur action à partir d'état actuel
            for(int i=0;i<QLUtils.Action_SIZE;i++){
                if(qTable[st][i]>meilleurQ){
                    meilleurQ=qTable[st][i];
                    actionIndex=i;
                }
            }
        }
        return actionIndex;
    }
    private int  executeAction(int act){
        stateX=Math.max(0, Math.min(QLUtils.actions[act][0]+stateX,QLUtils.GRID_SIZE-1));
        stateY=Math.max(0, Math.min(QLUtils.actions[act][1]+stateY,QLUtils.GRID_SIZE-1));
        return  stateX*QLUtils.GRID_SIZE+stateY;
    }
    private void resetState(){
        // l'etat initiale de l'agent
        this.stateX=(Integer) getArguments()[0]; // l'axe X
        this.stateY=(Integer) getArguments()[1]; // l'axe Y
    }
    public void runQLearning(){
        int iterartion=0;
        int currentState;
        int nextState;
        int act,act1;
        resetState();
        while(iterartion < QLUtils.MAX_EPOCH) {
            resetState();
            while (!finished()) {
                currentState = stateX * QLUtils.GRID_SIZE + stateY;
                //Choisir action à faire
                act = chooseAction(QLUtils.EPS);
                //Effectuer l'action
                nextState = executeAction(act);
                act1 = chooseAction(0);
                //Calculer les Qvalues
                qTable[currentState][act] = qTable[currentState][act] + QLUtils.ALPHA * ( QLUtils.GRID[stateX][stateY] + QLUtils.GAMMA * qTable[nextState][act1] - qTable[currentState][act]);
            }
            iterartion++;
        }
        showResult();
    }

    private void showResult() {
        System.out.println("***** Qtable *****");
        for (double [] line:
                qTable) {
            System.out.printf("[");
            for (double qValue:
                    line) {
                System.out.print(qValue+" , ");
            }
            System.out.println("]");
        }
        System.out.println("");
        resetState();
        while(!finished()){
            int act = chooseAction(0);
            System.out.println("state " +(stateX*QLUtils.GRID_SIZE+stateY) +" action :"+act);
            executeAction(act);
        }
        System.out.println("Final State: "+(stateX*QLUtils.GRID_SIZE+stateY));
    }

    // Algorithme
    private boolean finished() {
        return  QLUtils.GRID[stateX][stateY]==1;
    }

    public void EnvoyerQTable(){
        DFAgentDescription dfd= new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();

        sd.setType("QLearningAgent");
        dfd.addServices(sd);

        DFAgentDescription[] result= null;
        try{
              result= DFService.search(this,dfd);
        }catch(FIPAException e) {
              throw  new RuntimeException(e);
        }
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(result[0].getName());
        for (double[] doubles : qTable){
            for (double adouble:doubles){
                msg.setContent(msg.getContent()+ adouble + ",");
            }
            msg.setContent(msg.getContent() +"\n");
        }
        send(msg);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e){
            throw new RuntimeException(e);
        }
    }
}