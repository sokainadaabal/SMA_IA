package ma.enset.bddc.sequenciel;

import jade.wrapper.AgentContainer;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import ma.enset.bddc.gasma.GAUtils;

public class SimpleContainer {
    public static void main(String[] args)  throws Exception{
        Runtime runtime= Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(profile.MAIN_HOST,"localhost");
        AgentContainer agentContainer =runtime.createAgentContainer(profile);
        AgentController masterAgent=agentContainer.createNewAgent("master",MasterAgent.class.getName(),new Object[]{});
        masterAgent.start();

        for (int i=0;i< GAUtils.ISLAND_NUMBER;i++){
            AgentController islandAgent = agentContainer.createNewAgent("IslandAgent"+i, IslandAgent.class.getName(),new Object[]{});
            islandAgent.start();
        }
    }
}
