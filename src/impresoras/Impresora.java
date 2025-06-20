package impresoras;

import logger.Listener;
import tareas.TaskQueueSincronizada;
import tareas.Task;
import logger.Logger;

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

    private final LinkedHashSet<Filamento> colores;
    private final int indice;
    private final String modelo;
    private final Listener logger;
    TaskQueueSincronizada taskQueue;

    final long SLEEP_TIME = 2000;   //  Constante para el tiempo de espera entre intentos.

    /**
     * Constructor de un hilo de impresora.
     *
     * @param indice            , el numero de impresora instanciada
     * @param modelo            , el modelo de la impresora utilizada
     * @param taskQueue         , la lista de tareas sincronizadas
     * @param coloresCargados   , los colores cargados a la impresora.
     */
    public Impresora(int indice, String modelo, TaskQueueSincronizada taskQueue,
                     String coloresCargados, Logger logger){
        this.colores = new LinkedHashSet<>();
        this.indice = indice;
        this.modelo = modelo;
        this.taskQueue = taskQueue;
        this.logger = logger;
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
                logger.log(this + " recibió la tarea " + currentTask);
                // Se elimino la verificacion explicita aqui, ya que de eso se encarga
                // la taskqueue
                currentTask.execute();
                logger.log(this + " finalizo la tarea " + currentTask);
            }
            else{
                try{
                    sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    // La idea es nunca pasar por aca - No deberia mandar señal de interrupción
                    // a un hilo nunca de forma directa. Y si freno el sistema general, estos hilos
                    // deberian terminar sus tareas en ejecucion antes de salir del while.
                }
            }
        }
        logger.log(this + "\t\t: Ejecucion finalizada.");
        //System.out.println(this + "\t\t: Ejecucion finalizada."); // Add formteo de cadena.
    }


    /**
     * Metodo para verificar si la impresora tiene los colores para resolver la tarea.
     *
     * @param tarea , una instancia de Task a resolver.
     * @return      'true', si todos los colores de la task estan en los de impresora,
     *              'false' otherwise.
     */
    public boolean canHandle(Task tarea){
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


    /**
     * Méto-do usado para devolver un string con los colores soportados por esta impresora.
     *
     * @return  Una cadena conjunta con todos los caracteres de los colores de la impresora.
     */
    public String availableColorsIds() {
        StringJoiner joiner = new StringJoiner(", ");
        for (Filamento f : this.colores) {
            joiner.add(Character.toString(f.getId())); // convert char to String
        }
        return joiner.toString();
    }

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
