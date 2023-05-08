package ma.enset.bdcc.signature;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
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
