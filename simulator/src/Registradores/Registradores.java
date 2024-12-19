package Registradores;
public class Registradores {

    private Registrador[] registradores;

    public Registradores() {
        registradores = new Registrador[8]; 
        for(int i=0; i<8; i++){
            registradores[i]=new Registrador();
        }
    }
    public Registrador getRegistradores(int reg) {
        return registradores[reg];
    }

    
}
