package impresoras;

import Tareas.ListaSimpleSincronizada;
import Tareas.Task;

import java.util.LinkedHashSet;

import static java.lang.String.format;


/**
 * Class: 'Impresora'.
 * Esta clase modela una impresora 3D multicolor como un hilo.
 * Como caracteristica, tendra un conjunto de colores que soporta.
 * Las tareas de impresion que pueda manejar, las 'ejecutara'.
 */
public class Impresora extends Thread{

    private LinkedHashSet<Filamento> colores;
    private final int indice;
    private final String modelo;
    ListaSimpleSincronizada taskQueue;

    final long SLEEP_TIME = 2000;

    // Constructor del hilo.
    public Impresora(int indice, String modelo, ListaSimpleSincronizada taskQueue){
        this.colores = new LinkedHashSet<>();
        this.indice = indice;
        this.modelo = modelo;
        this.taskQueue = taskQueue;
    }


    @Override
    public void run(){
        while(taskQueue.accessAllowed()) {
            Task currentTask = taskQueue.retrieveTask(this);
            if(currentTask!=null){
                // Logica de manejo de las task.
                // Posiblemente, llamar a un metodo 'currentTask.execute()'.
            }
            else{
                System.out.println("No available tasks for this printer. Sleep.");
                try{
                    sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    // La idea es nunca pasar por aca - No deberia mandar señal de interrupción
                    // a un hilo nunca de forma directa. Y si freno el sistema general, estos hilos
                    // deberian terminar sus tareas en ejecucion antes de salir del while.
                }
            }
        }
        System.out.println(this.toString() + ": Ejecucion finalizada."); // Add formteo de cadena.
    }



    public boolean canHandle(Task tarea){
        return false; // IMPLEMENTAR LOGICA DESPUES
    }


    // Metodo para
    public void loadColor(Filamento cargable){
        if(!colores.contains(cargable)){
            colores.add(cargable);
            // Notificar que se cargo correctamente.
        }
        else{
            // Notificar que ya estaba adentro
        }
    }


    // Metodo usado para quitar uno de los colores de la impresora
    public void unloadColor(Filamento retirable){
        // AGREGAR LOGICA ACA.
    }


    // Metodo usado para quitar TODOS los filamentos de una impresora
    public void unloadAll(){
        colores.clear();
    }


    /**
     * Metodo para expresar en una cadena las caracteristicas de esta impresora.
     *
     * @return una cadena descriptiva formateada.
     */
    @Override
    public String toString(){
        return format("Impresora %s (%s)",this.indice, this.modelo);
    }



}
