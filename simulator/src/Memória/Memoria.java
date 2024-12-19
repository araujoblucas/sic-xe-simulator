package Memória;

import java.util.ArrayList;

public class Memoria {
    
    public ArrayList<Palavramem> memoria; //Vetor palavramem

    public Memoria(){
        memoria= new ArrayList<>();//Mudei para arraylist pois tem mais flexibilidade

        for(int i=0; i<1000; i++){//1000 posições de memoria 3kb
            memoria.add(new Palavramem());//cada posição 3 bytes
        }
    }
}
