package impresoras;

public enum Filamento {
    BLANCO("White"),
    NEGRO("Black"),
    AZUL("Blue"),
    ROJO("Red"),
    VERDE("Green");

    private final String color;

    Filamento(String color){
        this.color = color;
    }

    String getColor(){
        return this.color;
    }


}

