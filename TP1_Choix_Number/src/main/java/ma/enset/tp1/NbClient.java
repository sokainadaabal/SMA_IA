package ma.enset.tp1;

public class NbClient {
    public static  int cont=0;
    String nom;
    public NbClient(){
        System.out.println("*** Un client vient de se connecter ***");
        System.out.println(" Client de numero : " + cont);
        cont++;
        nom= " Client_"+cont;
    }
}
