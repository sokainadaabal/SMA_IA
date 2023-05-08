package ma.enset.bdcc.rsa;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

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
