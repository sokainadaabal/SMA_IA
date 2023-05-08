package ma.enset.bdcc.aes;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import ma.enset.bdcc.aes.ServerAgent;

public class ServerContainer {
    public static void main(String[] args) throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer container=runtime.createAgentContainer(profile);
        String password = "ABCDEFGHABCDEFGH";//128(16caractères)  192 ou 256 bits
        AgentController agentController=container.createNewAgent("server", ServerAgent.class.getName(),new Object[]{password});
        agentController.start();
    }
}
