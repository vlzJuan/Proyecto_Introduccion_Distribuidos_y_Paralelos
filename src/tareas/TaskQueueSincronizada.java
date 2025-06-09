package tareas;

import impresoras.Impresora;

import java.util.Iterator;
import java.util.LinkedHashSet;


public class TaskQueueSincronizada {

    private final LinkedHashSet<Task> tareas;
    private boolean allowReentry;

    // Debatir si hacer una lista de Threads, y que cada impresora instanciada
    // deba estar ahi. Posiblemente para el ultimo print me sirva, si hago un join con todos
    // los hilos en ejecucion despues de bloquear el re-acceso.

    public TaskQueueSincronizada(){
        tareas = new LinkedHashSet<>();
        allowReentry = true;
    }


    /**
     * Simple metodo getter del allowReentry.
     *
     * @return  allowReentry
     */
    public boolean accessAllowed(){
        return this.allowReentry;
    }

    /**
     * Metodo usado para agregar una tarea de impresion al recurso compartido de trabajos pendientes.
     * Nota: No existe metodo para pop-earlas manualmente. De eso se encargan los hilos Impresora.
     *
     * @param task  , la tarea de impresion a añadir.
     */
    public void addTask(Task task){
        this.tareas.add(task);
    }


    public LinkedHashSet<Task> tareasEnLista(){
        return this.tareas;
    }

    /**
     * Metodo sincronizado (Se usa a si mismo de lock) para devolver una tarea a una impresora.
     *
     * @param impresora , el hilo que esta usando el recurso.
     * @return          'null', cuando la impresora no soporta los colores de la tarea,
     *                  o la primera tarea que la impresora pueda manejar, elseway.
     */
    public synchronized Task retrieveTask(Impresora impresora) {
        Iterator<Task> iterator = tareas.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (impresora.canHandle(task)) {
                System.out.printf("[Control thread]: %s tomo la tarea '%s'\n",
                        impresora, task);
                iterator.remove();
                return task;
            }
        }
        return null; // Si no hay tareas coincidentes, se retorna null.
    }

    /**
     * Metodo hecho para finalizar la busqueda de tareas.
     * Por la ejecucion concurrente, los hilos van a completar sus tareas en ejecucion
     * antes de finalizar el programa.
     */
    public void endProcessing(){
        this.allowReentry = false;
    }

}
