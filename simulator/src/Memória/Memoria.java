package Memória;
public class Memoria {
    
    public Palavramem[] memoria; //Vetor palavramem

    public Memoria(){
        memoria= new Palavramem[1000];//1000 posições
        for(int i=0; i<1000; i++){
            memoria[i]= new Palavramem();//cada posição 3 bytes
        }
    }
}
