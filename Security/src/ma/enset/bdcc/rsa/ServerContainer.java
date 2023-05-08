package ma.enset.bdcc.rsa;

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
        String privateKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEA0tF7eGeknahRw4qxXwpLpyIYoTwUUQAWUetYgYXsb_j4a0kp3qNnE-mKvoEHb0GkG4KGHHAa8F51iYW8J8fNvwIDAQABAkAIVNsKUR5CVMqWbb7AwMlom4JZrOQop1Y6epCO3doQOrZJivVtkaHqSlY_Ma81f6341T3dujWZaE3mw0NS_RHBAiEA6xfntsjL5e27QSUfSXYRAzS9imiI5tSs9B5fwUHwHq8CIQDlkO9KT3XmXuQaZjBICYKFkgp_qNNN2Er55mNA2laF8QIgcuMExquMETpDR0u35XOATtvIQMpjFMMcHlR1oQDzMlsCICV5NTaCJhLG5qFQkQ0RUFcRcdlI68VHS2Xjr8wEWB9hAiBVwQh4xcYBfVoN4UKFXLk2ya6u-jsE5vee-J3RhXeEhg==";
        AgentController agentController=container.createNewAgent("server", ServerAgent.class.getName(),new Object[]{privateKey});
        agentController.start();
    }
}
