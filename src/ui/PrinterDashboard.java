package ui;

import impresoras.Impresora;
import logger.Logger;
import tareas.Task;
import tareas.TaskQueueSincronizada;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PrinterDashboard extends JFrame {

    private final TaskQueueSincronizada taskQueue = new TaskQueueSincronizada();
    private final List<Impresora> hilos = new ArrayList<>();

    // UI Components
    private final DefaultListModel<String> taskListModel = new DefaultListModel<>();
    private final JList<String> taskList = new JList<>(taskListModel);

    private final JTextArea logArea = new JTextArea(15, 40);
    private final JTextField taskNameField = new JTextField(15);
    private final JTextField taskTimeField = new JTextField(6);
    private final JTextField taskColorsField = new JTextField(6);

    private final JButton addTaskButton = new JButton("Agregar tarea");
    private final JButton stopButton = new JButton("Detener ejecución");

    private final Logger uiLogger;

    public PrinterDashboard() {
        super("Valenzuela - Granja de Impresoras 3D simuladas con hilos (Proyecto final)");

        // Setup UI Logger that writes to both console and UI
        uiLogger = new Logger() {
            @Override
            public void log(String msj) {
                super.log(msj);
                SwingUtilities.invokeLater(() -> {
                    logArea.append(msj + "\n");
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                });
            }
        };

        // Layout setup
        setLayout(new BorderLayout(5, 5));

        // Left panel - Printers info (just labels, threads run silently)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new TitledBorder("Impresoras"));

        // ACA INSTANCIO MANUALMENTE LAS IMPRESORAS A USAR.

        Impresora imp1 = new Impresora(1, "Anycubic Kobre 3", taskQueue,
                "BN", uiLogger);
        Impresora imp2 = new Impresora(2, "Anycubic Kobra 3", taskQueue,
                "RVA", uiLogger);
        Impresora imp3 = new Impresora(3, "FLSUN V400", taskQueue,
                "A", uiLogger);
        Impresora imp4 = new Impresora(4, "Creality Hi-Combo", taskQueue,
                "BN", uiLogger);
        hilos.add(imp1);
        hilos.add(imp2);
        hilos.add(imp3);
        hilos.add(imp4);
        for(Impresora imp:hilos){
            JLabel label = new JLabel(imp + " - Colores: " + imp.availableColorsIds());
            leftPanel.add(label);
        }
        /// FIN DE AGREGADO MANUAL

        add(leftPanel, BorderLayout.WEST);

        // Right panel - Task list
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new TitledBorder("Tareas en cola"));

        taskList.setVisibleRowCount(15);
        JScrollPane taskScrollPane = new JScrollPane(taskList);
        rightPanel.add(taskScrollPane, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);

        // Center panel - Log area
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new TitledBorder("Registro de eventos"));

        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        centerPanel.add(logScrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Task input and control buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        bottomPanel.setBorder(new TitledBorder("Crear nueva tarea"));

        bottomPanel.add(new JLabel("Nombre:"));
        bottomPanel.add(taskNameField);

        bottomPanel.add(new JLabel("Duración (ms):"));
        bottomPanel.add(taskTimeField);

        bottomPanel.add(new JLabel("Colores (ej: BNARV):"));
        bottomPanel.add(taskColorsField);

        bottomPanel.add(addTaskButton);

        stopButton.setBackground(Color.RED);
        stopButton.setForeground(Color.WHITE);
        bottomPanel.add(stopButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Add listeners
        addTaskButton.addActionListener(e -> addTask());
        stopButton.addActionListener(e -> stopExecution());

        // Window settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        // Start printers (threads)
        hilos.forEach(Thread::start);

        // Periodic UI updater for tasks list
        Timer timer = new Timer(1000, e -> updateTaskList());
        timer.start();
    }

    private void addTask() {
        String name = taskNameField.getText().trim();
        String timeStr = taskTimeField.getText().trim();
        String colors = taskColorsField.getText().trim().toUpperCase();

        if (name.isEmpty() || timeStr.isEmpty() || colors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long time;
        try {
            time = Long.parseLong(timeStr);
            if (time <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La duración debe ser un número entero positivo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate colors (only allowed letters from Filamento identifiers)
        if (!colors.matches("[BNARV]+")) {
            JOptionPane.showMessageDialog(this, "Los colores sólo pueden contener los caracteres B, N, A, R, V", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task newTask = new Task(name, time, colors);
        taskQueue.addTask(newTask);
        uiLogger.log("Nueva tarea agregada: " + newTask);

        // Aca se pueden borrar los campos usados. Los dejo asi nomas para facilidad de demostracion.
        //taskNameField.setText("");
        //taskTimeField.setText("");
        //taskColorsField.setText("");

        updateTaskList();
    }

    private void updateTaskList() {
        SwingUtilities.invokeLater(() -> {
            taskListModel.clear();
            taskQueue.tareasEnLista().forEach(t -> taskListModel.addElement(t.toString()));
        });
    }

    private void stopExecution() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea detener la ejecución?",
                "Confirmar detención", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            taskQueue.endProcessing();
            uiLogger.log("Se ha detenido la ejecución. Las impresoras finalizarán sus tareas actuales.");
            stopButton.setEnabled(false);
            addTaskButton.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PrinterDashboard dashboard = new PrinterDashboard();
            dashboard.setVisible(true);
        });
    }
}
