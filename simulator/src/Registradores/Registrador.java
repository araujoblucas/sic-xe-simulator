package Registradores;

public class Registrador {
    private byte[] reg;

    public Registrador() {
        reg = new byte[3];
    }

    public byte[] getReg() {
        return reg;
    }

    public void setReg(byte b1, byte b2, byte b3) {
        reg[0] = b1;
        reg[1] = b2;
        reg[2] = b3;
    }

    public String getValor(){
        StringBuilder hexValue = new StringBuilder();
        for (int i=0; i<3; i++) {
            hexValue.append(String.format("%02X",  reg[i]));
        }
        return hexValue.toString();
    }
}
