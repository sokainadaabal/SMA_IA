package ma.enset.tp1;

import jade.core.Agent;

public class AgentServer extends Agent {

    @Override
    protected void setup() {System.out.println("*** Server => la methode setup()***");}

    @Override
    protected void afterMove() {
        System.out.println("*** Server => la methode afterMove() ***");
    }

    @Override
    protected void beforeMove() {
        System.out.println("*** Server =>  La methode beforeMove() ***");
    }

    @Override
    protected void takeDown() {
        System.out.println("*** Server =>  La methode takeDown() ***");
    }
}
