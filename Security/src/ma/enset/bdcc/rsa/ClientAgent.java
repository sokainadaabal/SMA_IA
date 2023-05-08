package ma.enset.bdcc.rsa;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

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
