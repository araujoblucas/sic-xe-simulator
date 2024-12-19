package Registradores;

public class Registradores {

    public byte[][] regs24bits;
    public byte[] regs48bits;

    public Registradores(int numero){
    
        regs24bits=new byte[9][3]; // 9 registradores, cada um com 3 bytes

        regs48bits=new byte[6];

        
    }
}
