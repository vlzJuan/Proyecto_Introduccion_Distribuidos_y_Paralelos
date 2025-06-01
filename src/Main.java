import Tareas.ListaSimpleSincronizada;
import Tareas.Task;
import impresoras.Impresora;

import java.util.ArrayList;

public class Main {


    public static ArrayList<Impresora> hilos;
    public static ListaSimpleSincronizada tareas;


    public static void main(String[] args) {

        // Declaro un array para seguir los hilos, y las tareas
        hilos = new ArrayList<>();
        tareas = new ListaSimpleSincronizada();


        // Declaro impresoras
        for(int i=0; i<3; i++){
            hilos.add(new Impresora(i, "Anycubic Kobra 3", tareas, "BN"));
        }
        hilos.add(new Impresora(3, "Creality Hi Combo", tareas, "RVA"));

        // Inicio los hilos
        for(Impresora imp : hilos){
            imp.start();
        }


        // Testeo
        try{
            Thread.sleep(4000);
            tareas.addTask(new Task("Gatito negro1.gcode", 4000, "N"));
            tareas.addTask(new Task("Gatito negro2.gcode", 4000, "N"));
            tareas.addTask(new Task("Gatito blanco.gcode", 4000, "B"));
            tareas.addTask(new Task("Gatito Bicolor.gcode", 7000, "RA"));
            tareas.addTask(new Task("Gatito noimprimible.gcode", 6000, "NAV"));
            Thread.sleep(10000);
            tareas.endProcessing();
            // Espero a que todos los hilos mueran para seguir.
            for(Impresora hilo:hilos){
                hilo.join();
            }
        }
        catch (InterruptedException e) {
            System.out.println("Proceso del main interrumpido");
        }
        finally{
            System.out.println("Tareas sin agarrar:");
            for(Task t:tareas.tareasEnLista()){
                System.out.println(t);
            }
            System.out.println("Test finalizado");
        }


    }

}
