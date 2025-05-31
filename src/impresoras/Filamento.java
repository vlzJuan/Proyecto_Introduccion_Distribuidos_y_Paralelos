package impresoras;

public enum Filamento {
    BLANCO('B',"Blanco"),
    NEGRO('N',"Negro"),
    AZUL('A',"Azul"),
    ROJO('R',"Rojo"),
    VERDE('V',"Verde");

    private static final String identifiers = "BNARV";
    private final char identificador;
    private final String color;


    Filamento(char identifier, String color){
        this.identificador = identifier;
        this.color = color;
    }

    public static boolean isInIdentifiers(char test){

        return identifiers.indexOf(test)>=0;
    }

    public char getId(){
        return this.identificador;
    }

    String getColor(){
        return this.color;
    }


}

