package ma.enset.sma;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class AgentServer extends GuiAgent {
    private AgentServerGui agentServerGui;
    private int nomberChoix;
    List<String> clientAll = new ArrayList<>();
    @Override
    protected void setup() {
        nomberChoix= getRandomNumber(0,100);
        System.out.println("***  la méthode setup *****");
        agentServerGui=(AgentServerGui)getArguments()[0];
        agentServerGui.setAgentServer(this);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // tester si le client est connecter
                ACLMessage receivedMSG = receive();
                if (receivedMSG!=null){
                    agentServerGui.showMessage(receivedMSG.getContent());
                    // convertire le message a un integer
                    Integer numero = Integer.parseInt(receivedMSG.getContent());
                    String nomClient= receivedMSG.getSender().getLocalName();

                    if(!clientAll.contains(nomClient))
                    {
                        clientAll.add(nomClient);
                    }
                    // assure que vous avez recu le bon nomber
                    String bonNumber=testSiLeNumber(numero,nomClient);
                    ACLMessage reponse= new ACLMessage(ACLMessage.INFORM);
                    reponse.addReceiver(new AID(nomClient,AID.ISLOCALNAME));
                    reponse.setContent(bonNumber);
                    send(reponse);
                }else {
                    block();
                }

            }}
        );

    }

    protected int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    @Override
    protected void beforeMove() {
        System.out.println("***  la méthode beforeMove *****");
    }

    @Override
    protected void afterMove() {
        System.out.println("***  la méthode afterMove *****");
    }

    @Override
    protected void takeDown() {
        System.out.println("***  la méthode takeDown *****");
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
        String parameter =(String) guiEvent.getParameter(0);
        ACLMessage message=new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(new AID("Joueur",AID.ISLOCALNAME));
        message.setContent(parameter);
        send(message);
    }
    protected  void envoyerAtous(String client){
        ACLMessage message=new ACLMessage(ACLMessage.INFORM);
        for (String nom:clientAll)
        {
            message.addReceiver(new AID(nom,AID.ISLOCALNAME));
        }
        message.setContent("Félicitations à MR. "+client);
        send(message);
    }
    protected  String testSiLeNumber(Integer number,String nomClient){
        if(number==nomberChoix){
            nomberChoix=getRandomNumber(0,100);
            envoyerAtous(nomClient);
            return "Bravo, vous avez fait le bon choix. \n \t le number est réinitialisé.";
        }
        else if (number<nomberChoix){
            return  "Plus Grand";
        }
        else return "Plus Petit";

    }
}
