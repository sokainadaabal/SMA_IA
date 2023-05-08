package ma.enset.bdcc.signature;

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
        String privateKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAjTwrXDH6xinWIvZctDUp8t6jAn92GrH4s3ufqy+A6/NPhMaCiEtdXK8CifEOucqHdSI5a4LDgjOyM2r9a5CInQIDAQABAkAIItE0ocRv1J/QqnSIr7oCPa+88xlwoZP8HZZYcYdxusn7pLzoDKq+ZojTwa0i9p+ShdkjZ77oE6tQw5jBTr+ZAiEAlzUQ1lJuhbV64Plgrmokt97Y49g8Ti1GVMSSm9Lb7KsCIQDvHdgVCMa6bE3n0TgeieOSHrYfSeRAF2iL65isfHxP1wIgMwo8lrvYhtXNlqdXFUjLAC3+9FoHcTQjK3X3LxYsGmUCIBL8t6/T5pPfDcCeqGokYOG1dpi9cVZ1hWO27YSkzeo5AiAUXcMT3VWlU26I5VNAFwNaqda5bCNFwwbq/Qy77d4INQ==";
        AgentController agentController=container.createNewAgent("client", ClientAgent.class.getName(),new Object[]{privateKey});
        agentController.start();
    }
}
