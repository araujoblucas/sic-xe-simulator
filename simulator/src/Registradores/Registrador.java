package Registradores;


public class Registrador {
    private byte[] reg;

    public Registrador(){
        reg=new byte[3];
    }
    public byte[] getReg() {
        return reg;
    }
    
    public String getValueAsString(){ 
        StringBuilder hexValue=new StringBuilder();
        for(byte b:reg){
            hexValue.append(String.format("%02X", b)); 
        } 
        return hexValue.toString(); 
    }
}
