package impresoras;

import Tareas.ListaSimpleSincronizada;
import Tareas.Task;

import java.util.LinkedHashSet;
import java.util.StringJoiner;

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
    public Impresora(int indice, String modelo, ListaSimpleSincronizada taskQueue,
                     String coloresCargados){
        this.colores = new LinkedHashSet<>();
        this.indice = indice;
        this.modelo = modelo;
        this.taskQueue = taskQueue;
        this.setName(format("[Impresora %d]", indice)); //  Asigno nombre al hilo
        for(char c : coloresCargados.toCharArray()){
            Filamento f = Filamento.filamentoFromId(c);
            if(f !=null){
                colores.add(f);
            }
        }

    }

    @Override
    public void run(){
        while(taskQueue.accessAllowed()) {
            Task currentTask = taskQueue.retrieveTask(this);
            if(currentTask!=null){
                if(canHandle(currentTask)){
                    // Logica de manejo de las task.
                    // Posiblemente, llamar a un metodo 'currentTask.execute()'.
                }
                else{
                    // Informar que aca la impresora no pudo manejarlo.
                }
            }
            else{
                System.out.println(this.toString() +"\t\t:No available tasks. Sleep " + SLEEP_TIME);
                try{
                    sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    // La idea es nunca pasar por aca - No deberia mandar señal de interrupción
                    // a un hilo nunca de forma directa. Y si freno el sistema general, estos hilos
                    // deberian terminar sus tareas en ejecucion antes de salir del while.
                }
            }
        }
        System.out.println(this.toString() + "\t\t: Ejecucion finalizada."); // Add formteo de cadena.
    }


    /**
     * Metodo para verificar si la impresora tiene los colores para resolver la tarea.
     *
     * @param tarea , una instancia de Task a resolver.
     * @return      'true', si todos los colores de la task estan en los de impresora,
     *              'false' otherwise.
     */
    private boolean canHandle(Task tarea){
        return this.colores.containsAll(tarea.coloresDeImpresion());
    }


    /**
     * Méto-do usado para devolver un string con los colores soportados por esta impresora.
     *
     * @return  Una cadena conjunta con todos los colores descriptos de la impresora.
     */
    public String availableColorsDescriptive(){
        StringJoiner joiner = new StringJoiner(", ");
        for(Filamento f:this.colores){
            joiner.add(f.getColor());
        }
        return joiner.toString();
    }


    /*

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

    */
    /**
     * Metodo para expresar en una cadena las caracteristicas de esta impresora.
     *
     * @return una cadena descriptiva formateada.
     */
    @Override
    public String toString(){
        return format("Impresora %s (%s)",
                this.indice, this.modelo);
    }



}
