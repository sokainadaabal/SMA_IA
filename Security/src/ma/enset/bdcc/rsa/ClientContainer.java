package ma.enset.bdcc.rsa;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class ClientContainer {
    public static void main(String[] args) throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer container=runtime.createAgentContainer(profile);
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANLRe3hnpJ2oUcOKsV8KS6ciGKE8FFEAFlHrWIGF7G_4-GtJKd6jZxPpir6BB29BpBuChhxwGvBedYmFvCfHzb8CAwEAAQ==";
        AgentController agentController=container.createNewAgent("client", ClientAgent.class.getName(),new Object[]{publicKey});
        agentController.start();
    }
}
