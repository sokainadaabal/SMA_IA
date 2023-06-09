package ma.enset.ImpSma;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ma.enset.QLUtils;

public class MasterAgent extends Agent {
    @Override
    protected void setup() {
        DFAgentDescription dfd=new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("QLearningAgent");
        sd.setName("masterAgent");
        dfd.addServices(sd);
        try{
            DFService.register(this,dfd);
        }catch(FIPAException e){
                throw new RuntimeException(e);
        }
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage recivedMessage = receive();
                double[][] qTable= new double[QLUtils.GRID_SIZE*QLUtils.GRID_SIZE][QLUtils.Action_SIZE];
                if(recivedMessage!=null){
                    // recoit le table Q-table de l'apart de agent
                    System.out.println(recivedMessage.getSender().getName());
                    String qTString= recivedMessage.getContent();
                    String[] rows= qTString.split("\n");
                    // supprimer la ligne null de table
                    rows[0]=rows[0].substring(4);
                    for (int i=0;i<rows.length;i++){
                        String[] columns=rows[i].split(",");
                        for (int j=0;j<columns.length;j++){
                            qTable[i][j]=Double.parseDouble(columns[j]);
                        }
                    }
                    // Afficher Q-Table
                    for (int i=0;i<QLUtils.GRID_SIZE*QLUtils.GRID_SIZE;i++) {
                        for (int j = 0; j < QLUtils.Action_SIZE; j++) {
                            System.out.print(qTable[i][j] + " ");
                        }
                        System.out.println();
                    }

                }
                else {
                    block();
                }
            }
        });
    }
}
