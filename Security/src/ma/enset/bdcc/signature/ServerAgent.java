package ma.enset.bdcc.signature;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
