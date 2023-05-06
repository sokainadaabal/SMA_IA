# Activité pratique : Jeu multi-joueurs avec les SMA

## Objectif de TP 
Ce Tp sert à développer un simple jeu en utilisant les systèmes multi-agent.
Le principe de jeu est de determiner un nombre magique secret entre 0 et 100 par joueurs.
### Pre-condition
Il faut utiliser la bibliothèque JADE, c'est un environnement pour la programmation multi-agent.
## Container
Le container  est un élément clé dans les SMA. C'est un environnement dans lequel les agents sont hébergés et peuvent interagir les uns avec les autres.
- ```MainContainer.java```
  ```` java
  public class MainContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter("gui","true");
        AgentContainer agentContainer=runtime.createMainContainer(profile);
        agentContainer.start();
    }
  }
  ````
## Agents

Dans le tp, nous avons utilisé plusieurs classes telles que : 
  * ````AgentClient```` cette classe permet de lancer le client.
  * ````AgentClientGui```` cette classe permet de lancer l'interface de client.
  * ````AgentServer````  cette classe permet de lancer le serveur.
  * ````AgentServerGUI```` cette classe permet de lancer l'interface de serveur.

Dans le jeu, il faut avoir multijoueurs, pour cela il faut lancer plusieurs instances de classe ```AgentClientGUi``` grace à modifier la configuration dans Intellij. 
   Allez à  ```Edit Configuration``` de classe ```AgentClientGui```  dans ``` Build and run ``` cliquer sur ``` Modify options``` et cocher ```Allow multiple instances```.

## Serveur 
L'agent server va permettre de générer un nombre secret entre 0 et 100, recevoir les reponses de jours et comparer avec le nombre secret si correct va informer puis reinitialise le nombre à nouveau.

- ````Methode de generer le nombre secret ````
````java
// min =0 et max= 100
 protected int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
````
- ````Test si le nombre saisi est egale a le nombre secret ````
````java
protected  String testSiLeNumber(Integer number,String nomClient){
        if(number==nomberChoix){
            nomberChoix=getRandomNumber(0,100);
            envoyerAtous(nomClient);
            return "Bravo, vous avez fait le bon choix. \n \t le number est réinitialisé.";
        }
        else if (number<nomberChoix){
            return  "Plus Grand!";
        }
        else return "Plus Petit";

    }
````
- ```` si le nombre secret est trouve par un client, un message sera envoyer a tous les joueurs et le nombre sera initialiser ````
```java
protected  void envoyerAtous(String client){
        ACLMessage message=new ACLMessage(ACLMessage.INFORM);
        for (String nom:clientAll)
        {
            message.addReceiver(new AID(nom,AID.ISLOCALNAME));
        }
        message.setContent("Félicitations à MR. "+client);
        send(message);
    }
```
## Communication 
Pour que les agents joueurs puissent communiquer avec serveur, nous avons utilisé la bibliothèque jade et grace a l'utilisation d'un protocole de communication est assuré par ``` ACLMessage ``` ce aue permet aux agents de communiquer de manière standardisée et efficace.
```java
ACLMessage reponse= new ACLMessage(ACLMessage.INFORM);
reponse.addReceiver(new AID(nomClient,AID.ISLOCALNAME));
reponse.setContent(bonNumber);
send(reponse);
```
## Implementation

