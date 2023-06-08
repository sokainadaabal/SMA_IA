package ma.enset.bdcc.sma;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.List;

public class MainContainer {
    public static void main(String[] args) {
        Runtime runtime = Runtime.instance();
        runtime.setCloseVM(true);
        ProfileImpl profile = new ProfileImpl();
        AgentContainer mainContainer = runtime.createMainContainer(profile);

        List<AgentController> agentControllers = new ArrayList<>();

        for (int i = 0; i < PSOUtils.SWARM_SIZE; i++) {
            try {
                Object[] agentArgs = {i};
                AgentController agentController = mainContainer.createNewAgent("particle." + i,
                        ParticleAgent.class.getName(), agentArgs);
                agentControllers.add(agentController);
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }

        for (AgentController agentController : agentControllers) {
            try {
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }
}
