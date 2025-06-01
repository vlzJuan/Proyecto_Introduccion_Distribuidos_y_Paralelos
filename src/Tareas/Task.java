package Tareas;

import impresoras.Filamento;

import java.util.LinkedHashSet;

public class Task {

    private final String taskName;
    private final long timeToCompletion;
    private final LinkedHashSet<Filamento> colores;

    @SuppressWarnings("FieldCanBeLocal")
    private final long PRINT_INTERVAL = 1000;

    public Task(String nombre, long taskTime, String colorString){
        this.taskName = nombre;
        this.timeToCompletion = taskTime;
        this.colores = new LinkedHashSet<>();
        for(char c : colorString.toCharArray()){
            Filamento f = Filamento.filamentoFromId(c);
            if(f != null){
                colores.add(f);
            }
        }
    }



    public void execute(){

        String threadName = Thread.currentThread().getName();


        //  Sector de simulacion de comportamiento de la tarea
        long timeElapsedInSeconds   = timeToCompletion/PRINT_INTERVAL;  // Cantidad de segundos que tarda
        long restoDeDivision        = timeToCompletion%PRINT_INTERVAL;  // Cantidad de milisegundos restante
        try{
            for(int i=0; i<timeElapsedInSeconds; i++){
                Thread.sleep(PRINT_INTERVAL);
                System.out.printf("%s: Procesando %s\tTime elapsed: %d segundos.\n",
                        threadName, this, i);
            }
            Thread.sleep(restoDeDivision);
            System.out.printf("%s: Tarea finalizada (%s)!\n",
                    threadName, this);
        }
        catch(InterruptedException e){
            System.out.printf("%s:Ejecucion interrumpida.\n", threadName);
        }
        // Fin de simulacion de impresion de tarea.


    }




    public LinkedHashSet<Filamento> coloresDeImpresion(){
        return this.colores;
    }


    private String formattedTime(){

        long resto = timeToCompletion;
        StringBuilder str = new StringBuilder();
        if(resto>=60000){
            str.append((resto / 60000)).append("m ");
            resto = resto%60000;
        }
        str.append(resto).append("s");
        return str.toString();
    }

    private String formattedColors(){
        StringBuilder ret = new StringBuilder();
        for(Filamento f:colores){
            ret.append(f.getId());
        }
        return ret.toString();
    }

    @Override
    public String toString(){
        return this.taskName + " [" + this.formattedColors() + "], time: " + this.formattedTime();
    }

}
