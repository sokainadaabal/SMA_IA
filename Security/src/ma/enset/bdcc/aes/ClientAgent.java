package ma.enset.bdcc.aes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
