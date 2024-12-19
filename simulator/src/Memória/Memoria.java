package Memória;
public class Memoria {
    
    public Palavramem[] memoria;

    public Memoria(){
        memoria= new Palavramem[1000];
        for(int i=0; i<1000; i++){
            memoria[i]= new Palavramem();
        }
    }
}
