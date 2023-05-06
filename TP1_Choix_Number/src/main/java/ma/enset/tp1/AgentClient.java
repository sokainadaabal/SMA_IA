package ma.enset.tp1;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import  jade.core.behaviours.Behaviour;

public class AgentClient extends GuiAgent {

    private  AgentClientGui agentClientGui;
    @Override
    protected void setup() {
        System.out.println("Client => La methode setup() est Encore d'executer ");
        // add behaviour
        addBehaviour(new CyclicBehaviour() {
            // sera execute de la manière infini
            // jade recommande cette methode
            @Override
            public void action() {
                System.out.println("La methode CyclicBehaviour est vient d'appeler ");
                ACLMessage message = receive();
                if(message!=null){
                    System.out.println("Reception du message : " + message.getContent());
                }
                else {
                    block();
                }
            }
        });
    }

    @Override
    protected void afterMove() {
        System.out.println("*** Client => la methode afterMove() ***");
    }

    @Override
    protected void beforeMove() {
        System.out.println("*** Client =>  La methode beforeMove() ***");
    }

    @Override
    protected void takeDown() {
        System.out.println("*** Client =>  La methode takeDown() ***");
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
