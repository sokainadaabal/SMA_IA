package ma.enset.bdcc.signature;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class ServerContainer {
    public static void main(String[] args) throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer container=runtime.createAgentContainer(profile);
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAI08K1wx+sYp1iL2XLQ1KfLeowJ/dhqx+LN7n6svgOvzT4TGgohLXVyvAonxDrnKh3UiOWuCw4IzsjNq/WuQiJ0CAwEAAQ==";
        AgentController agentController=container.createNewAgent("server", ServerAgent.class.getName(),new Object[]{publicKey});
        agentController.start();
    }
}
