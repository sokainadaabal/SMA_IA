# AES | RSA | SHA256withRSA | cryptage, chiffrement et la sécurite de donnée
## Le cryptage de donnée 
Il s’agit d’un procédé dont l’objectif est de sécuriser vos informations en les «brouillant» afin de les rendre incompréhensibles aux yeux des individus non concernés.
## Vocabulaire 
Des notions à connaitre :
- ``` La clé de chiffrement ``` 👉  c’est le système utilisé pour décrypter le message.
 Si pendant longtemps il s’agissait de codes, de phrases ou encore de machines de chiffrement, la cryptologie moderne utilise des données codées sous forme binaire.
- ``` Le chiffrement par bloc ```  👉  ce mode opératoire consiste à fragmenter les informations à chiffrer par blocs d’une même taille (64 ou 128 bits par exemple). Le cryptage s’effectue ensuite bloc par bloc.
- ``` Le chiffrement par flux ``` 👉  cette technologie traite des données de longueurs différentes, sans les découper.
- ``` La cryptographie symétrique ``` 👉  elle implique que l’information soit cryptée et décryptée à l’aide d’une même clé de chiffrement. Ce système est rapide, car il utilise peu de ressources et de calculs par rapport à l’asymétrique.
- ``` La cryptographie asymétrique ``` 👉  dans ce cas de figure, deux clés différentes sont nécessaires :
  - la clé publique, pour chiffrer la donnée.
  - la clé secrète privée équivalente, pour déchiffrer cette même donnée.

## Confidentialité des données ( Utilisation de l'algorithme AES)
### L'algorithme AES
L'algorithme AES (Advanced Encryption Standard) est une norme de chiffrement symétrique utilisée pour sécuriser les communications et protéger les données sensibles.
L'AES utilise la même clé pour chiffrer et déchiffrer un texte.
Le cryptage AES supporte différentes tailles de clé : 128, 192 ou 256 bits. En fonction de ces tailles de clé, le nombre d’opérations nécessaires par séquence pour le cryptage des blocs diffère :
  - 10 pour 128 bits,
  - 12 pour 192 bits,
  - 14 pour 256 bits.
### Application de l'algorithme AES
Dans le package ```ma.enset.bdcc.aes```, nous avons faire le code de l'implementation de aes :
- ```ClientAgent.java```
``` java
public class ClientAgent extends Agent {
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                String messageClair = "HELLO SERVER";
                String passwordSentFromServer = (String) getArguments()[0];
                //créer la clé secréte avec un mot de passe
                SecretKey secretKey = new SecretKeySpec(passwordSentFromServer.getBytes() , "AES");

                try {
                    //créer l'objet cipher
                    Cipher cipher = Cipher.getInstance("AES");
                    //initialiser l'objet cipher
                    cipher.init(Cipher.ENCRYPT_MODE , secretKey);
                    //message à envoyé au serveur
                    byte[] encryptMsg = cipher.doFinal(messageClair.getBytes());
                    String encryptEncoded = Base64.getEncoder().encodeToString(encryptMsg);
                    //System.out.println(encryptEncoded);
                    ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                    aclMessage.setContent(encryptEncoded);
                    aclMessage.addReceiver(new AID("server",AID.ISLOCALNAME));
                    send(aclMessage);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
```
- ```ClientContainer.java```
``` java
public class ClientContainer {
    public static void main(String[] args) throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer container=runtime.createAgentContainer(profile);
        String password = "ABCDEFGHABCDEFGH";//128(16caractères)  192 ou 256 bits
        AgentController agentController=container.createNewAgent("client", ClientAgent.class.getName(),new Object[]{password});
        agentController.start();
    }
}
```
- ```ServerAgent.java```
``` java
public class ServerAgent extends Agent {
    @Override
    protected void setup() {
        String password = (String) getArguments()[0];
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage recieve = receive();
                if(recieve!=null){
                    String cryptedEncodedMsg = recieve.getContent();
                    System.out.println("cryptedEncodedMsg = "+cryptedEncodedMsg);
                    byte[] encryptMsg = Base64.getDecoder().decode(cryptedEncodedMsg);
                    SecretKey secretKey = new SecretKeySpec(password.getBytes() , "AES");
                    try {
                        Cipher cipher = Cipher.getInstance("AES");
                        cipher.init(Cipher.DECRYPT_MODE , secretKey);
                        byte[] decryptedMsg = cipher.doFinal(encryptMsg);
                        System.out.println("Message = "+new String(decryptedMsg));
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchPaddingException | InvalidKeyException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalBlockSizeException e) {
                        throw new RuntimeException(e);
                    } catch (BadPaddingException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    block();
                }
            }
        });
    }
}

```
- ```ServerContainer.java```
``` java
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
```
- ```MainContainer.java```
``` java 
public class MainContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter("gui","true");
        AgentContainer agentContainer=runtime.createMainContainer(profile);
        agentContainer.start();
    }
}
```
### Implementation
- ```MainContainer```

![image](https://github.com/sokainadaabal/SMA_IA/assets/48890714/dac9ad2e-20d1-49b7-8d72-8e7eda992d22)

- ```ServerContainer```

![image](https://github.com/sokainadaabal/SMA_IA/assets/48890714/83f49dfd-287b-4c2c-bef4-566c80704d21)

- ```ClientContainer```

![image](https://github.com/sokainadaabal/SMA_IA/assets/48890714/2ff54408-34d5-4aba-b744-329c2d1ef58a)

- ``` Apres lancement de client ```

![image](https://github.com/sokainadaabal/SMA_IA/assets/48890714/7e747425-f89d-479b-8b07-bc833a1fda5c)

## Sécurité des communications ( Utilisation de l'algorithme RSA)
### L'algorithme RSA
L'algorithme RSA (Rivest, Shamir, Adleman) est un algorithme de cryptographie asymétrique largement utilisé pour le chiffrement et la signature numérique.
L'algorithme RSA repose sur le concept de la cryptographie à clé publique, où une paire de clés distinctes est utilisée : une clé publique et une clé privée. La clé publique est partagée avec les autres utilisateurs, tandis que la clé privée est gardée secrète par le propriétaire.
### Application de l'algorithme RSA
Dans le package ``` ma.enset.bdcc.rsa ``` , nous avons faire le code de l'implementation de rsa:
- ```GenereteRSAKeys.java```
``` java 
public class GenereteRSAKeys {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        String encodedPRK = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String encodedPbK= Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("************** Public key ****************");
        System.out.println(encodedPbK);
        System.out.println("************** Private key ****************");
        System.out.println(encodedPRK);
    }
}
```
> Ce classe permet de generer des cles RSA.
- ```ClientAgent.java```
``` java
public class ClientAgent extends Agent {
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                String messageClair = "HELLO SERVER";
                String encodedPublicKey = (String) getArguments()[0];
                byte[] decodedPbk = Base64.getUrlDecoder().decode(encodedPublicKey);
                try {
                    //Créer une clé publique à partir de celle déja génerée
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PublicKey publicKey =  keyFactory.generatePublic(new X509EncodedKeySpec(decodedPbk));
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE , publicKey);
                    byte[] encryptMsg = cipher.doFinal(messageClair.getBytes());
                    String encryptEncoded = Base64.getUrlEncoder().encodeToString(encryptMsg);
                    ACLMessage aclMessage = new ACLMessage();
                    aclMessage.setContent(encryptEncoded);
                    aclMessage.addReceiver(new AID("server",AID.ISLOCALNAME));
                    send(aclMessage);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
```
- ```ClientContainer.java```
``` java
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
```
- ```ServerAgent.java```
``` java
public class ServerAgent extends Agent {
    @Override
    protected void setup() {
        String encoderPrk = (String) getArguments()[0];
        byte[] decodedPrk = Base64.getUrlDecoder().decode(encoderPrk);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage recieve = receive();
                if(recieve!=null){
                    String cryptedEncodedMsg = recieve.getContent();
                    System.out.println("cryptedEncodedMsg = "+cryptedEncodedMsg);
                    byte[] encryptMsg = Base64.getUrlDecoder().decode(cryptedEncodedMsg);
                    try {
                        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decodedPrk));
                        Cipher cipher = Cipher.getInstance("RSA");
                        cipher.init(Cipher.DECRYPT_MODE , privateKey);
                        byte[] decryptedMsg = cipher.doFinal(encryptMsg);
                        System.out.println("Message = "+new String(decryptedMsg));

                    } catch (InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchPaddingException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalBlockSizeException e) {
                        throw new RuntimeException(e);
                    } catch (BadPaddingException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    block();
                }


            }
        });
    }
}
```
- ```ServerContainer.java```
``` java
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
```

> meme code utiliser dans AES, il y a un changement dans les classes ClientAgent et ServerAgent. 
> Dans le classe ServerContainer et ClientConatiner on utilise le cle privee et public generer par le class GenereteRSAKeys.
### Implementation
- ```GenereteRSAKeys```

![image](https://github.com/sokainadaabal/SMA_IA/assets/48890714/83ca8971-2963-4976-9905-4dec5a5446aa)

- ```ServerContainer```

![image](https://github.com/sokainadaabal/SMA_IA/assets/48890714/8ba9bafb-fd3a-48ba-9622-66b32f1a0347)

- ```ClientContainer```

![image](https://github.com/sokainadaabal/SMA_IA/assets/48890714/2b65e854-ddb9-4a70-a29b-9e6759450c56)

## Intégrité des données (L'algorithme SHA256withRSA)
### L'algorithme SHA256withRSA
L'algorithme "SHA256withRSA" est une combinaison de deux algorithmes, à savoir SHA-256 (Secure Hash Algorithm 256 bits) et RSA (Rivest, Shamir, Adleman). Il est souvent utilisé en cryptographie pour la signature numérique des données.
### Application de l'algorithme SHA256withRSA
Dans le package ``` ma.enset.bdcc.signature ```, nous avons faire le code de l'implementation de SHA256withRSA:
> nous avons refaire le meme code de AES selement un changement au niveau de class ClientAgent et ServerAgent 
- ``` ClientAgent.java ```
``` java 
public class ClientAgent extends Agent {
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                String messageClair = "HELLO SERVER";
                String encodedPublicKey = (String) getArguments()[0];
                byte[] decodedPbk = Base64.getDecoder().decode(encodedPublicKey);
                try {
                    //Créer une clé publique à partir de celle déja génerée
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PrivateKey privateKey =  keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedPbk));
                    Signature signature = Signature.getInstance("SHA256withRSA");
                    signature.initSign(privateKey);
                    signature.update(messageClair.getBytes());
                    byte[] sign=signature.sign();
                    String encryptSign = Base64.getEncoder().encodeToString(sign);
                    String encryptDocument = Base64.getEncoder().encodeToString(messageClair.getBytes());
                    String documendSign=encryptDocument+"__.__"+encryptSign;
                    ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                    aclMessage.setContent(documendSign);
                    aclMessage.addReceiver(new AID("server",AID.ISLOCALNAME));
                    send(aclMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
```
- ``` ServerAgent.java ```
``` java 
public class ServerAgent extends Agent {
    @Override
    protected void setup() {
        String encoderPrk = (String) getArguments()[0];
        byte[] decodedPrk = Base64.getDecoder().decode(encoderPrk);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage recieve = receive();
                if(recieve!=null){
                    String documentSign = recieve.getContent();
                    String[] documentSplit = documentSign.split("__.__");
                    byte[] documedSign= Base64.getDecoder().decode(documentSplit[0]);
                    byte[] sign = Base64.getDecoder().decode(documentSplit[1]);
                    try {
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodedPrk));
                        Signature signature= Signature.getInstance("SHA256withRSA");
                        signature.initVerify(publicKey);
                        signature.update(documedSign);
                        boolean verify = signature.verify(sign);
                        System.out.println(verify?"Signature ok ":"signature non ok ");

                    } catch (InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }  catch (InvalidKeyException e) {
                        throw new RuntimeException(e);
                    } catch (SignatureException e) {
                        throw new RuntimeException(e);
                    }

                }else{
                    block();
                }


            }
        });
    }
}
```
### Implementation
- ``` ClientContainer ```

![image](https://github.com/sokainadaabal/SMA_IA/assets/48890714/e4083484-7aaf-402f-b1d6-ba6badd2aec9)

- ``` ServerContainer ```

![image](https://github.com/sokainadaabal/SMA_IA/assets/48890714/268fcbd4-21c5-4461-a59d-5f01a8f5d1e3)
