package ma.enset.bdcc.aes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
