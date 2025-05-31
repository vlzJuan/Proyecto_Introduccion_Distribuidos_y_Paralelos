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

    // Constructor para los filamento constantes.
    Filamento(char identifier, String color){
        this.identificador = identifier;
        this.color = color;
    }

    /**
     * Metodo para retornar un filamento, en función de su identificador.
     *
     * @param id    , un caracter que identifica al color buscado.
     * @return      el Filamento apropiado al identificador si es valido, o 'null' otherwise.
     */
    public static Filamento filamentoFromId(char id) {
        Filamento ret = null;
        for (Filamento f : values()) {
            if (f.identificador == id) {
                ret = f;
            }
        }
        return ret;
    }

    // Getters de identificador y color.
    public char getId(){
        return this.identificador;
    }
    String getColor(){
        return this.color;
    }
    
}
