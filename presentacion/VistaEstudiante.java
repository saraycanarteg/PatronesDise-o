package presentacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;
import logica_negocio.AdaptadorEntradaEstudiante;
import logica_negocio.Estudiante;
import logica_negocio.IServicioDecorador;
import logica_negocio.IObservadorCambios;

import java.util.List;

/**
 * Clase FormularioCrudEstudiante - Vista (Boundary)
 * Interfaz gráfica del CRUD de estudiantes
 * 3 Atributos: txtId, txtNombre, txtEdad
 * 6 Métodos: clickAgregar, clickActualizar, clickEliminar, clickMostrarTodo, mostrarMensaje, mostrarTablaEstudiantes
 */
public class VistaEstudiante extends JFrame implements IObservadorCambios {
    private IServicioDecorador controlador;
    private AdaptadorEntradaEstudiante adaptadorEntrada;

    // 3 Atributos especificados en el diagrama
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtEdad;

    // Componentes adicionales para la UI
    private JButton btnAgregar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnMostrarTodo;
    private JButton btnImportar;
    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;


    public VistaEstudiante() {
        inicializarGUI();
    }

    public void setControlador(IServicioDecorador controlador) {
        this.controlador = controlador;
        this.adaptadorEntrada = new AdaptadorEntradaEstudiante(controlador);
    }

    private void inicializarGUI() {
        setTitle("CRUD de Estudiantes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelEntrada = new JPanel(new BorderLayout(10, 10));
        panelEntrada.setBorder(BorderFactory.createTitledBorder("Datos del Estudiante"));

        // Panel de campos
        JPanel panelCampos = new JPanel(new GridLayout(1, 6, 10, 10));
        panelCampos.add(new JLabel("ID:"));
        txtId = new JTextField();
        panelCampos.add(txtId);

        panelCampos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelCampos.add(txtNombre);

        panelCampos.add(new JLabel("Edad:"));
        txtEdad = new JTextField();
        panelCampos.add(txtEdad);

        panelEntrada.add(panelCampos, BorderLayout.NORTH);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 3, 10, 10));
        
        btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> clickAgregar());
        panelBotones.add(btnAgregar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> clickActualizar());
        panelBotones.add(btnActualizar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> clickEliminar());
        panelBotones.add(btnEliminar);

        btnMostrarTodo = new JButton("Mostrar Todos");
        btnMostrarTodo.addActionListener(e -> clickMostrarTodo());
        panelBotones.add(btnMostrarTodo);

        btnImportar = new JButton("Importar");
        btnImportar.addActionListener(e -> clickImportar());
        panelBotones.add(btnImportar);

        JButton btnExportar = new JButton("Exportar");
        btnExportar.addActionListener(e -> clickExportar());
        panelBotones.add(btnExportar);

        panelEntrada.add(panelBotones, BorderLayout.SOUTH);

        panelPrincipal.add(panelEntrada, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Edad"}, 0);
        tablaEstudiantes = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaEstudiantes);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Lista de Estudiantes"));
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        add(panelPrincipal);
        setVisible(true);
    }

    /**
     * clickAgregar - Maneja el evento del botón Agregar
     */
    public void clickAgregar() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String edad = txtEdad.getText().trim();
        controlador.agregarEstudiante(id, nombre, edad);
        limpiarCampos();
    }

    /**
     * clickActualizar - Maneja el evento del botón Actualizar
     */
    public void clickActualizar() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String edad = txtEdad.getText().trim();
        controlador.actualizarEstudiante(id, nombre, edad);
        limpiarCampos();
    }

    /**
     * clickEliminar - Maneja el evento del botón Eliminar
     */
    public void clickEliminar() {
        String id = txtId.getText().trim();
        controlador.eliminarEstudiante(id);
        limpiarCampos();
    }

    /**
     * clickMostrarTodo - Maneja el evento del botón Mostrar Todos
     */
    public void clickMostrarTodo() {
        controlador.mostrarTodos();
    }

    /**
     * clickImportar - Importa estudiantes desde archivo CSV/JSON
     */
    public void clickImportar() {
        if (adaptadorEntrada == null) {
            mostrarMensaje("El controlador no esta inicializado.");
            return;
        }

        String[] opciones = {"Seleccionar archivo", "Ingresar ruta"};
        int opcion = JOptionPane.showOptionDialog(
            this,
            "Selecciona como deseas importar:",
            "Importar Estudiante",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (opcion == JOptionPane.CLOSED_OPTION) {
            return;
        }

        if (opcion == 0) {
            importarConSelector();
        } else {
            importarConRutaManual();
        }
    }

    private void importarConSelector() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Selecciona archivo CSV o JSON");
        selector.setFileFilter(new FileNameExtensionFilter("Archivos CSV y JSON", "csv", "json"));

        File carpetaDatos = new File("datos");
        if (carpetaDatos.exists() && carpetaDatos.isDirectory()) {
            selector.setCurrentDirectory(carpetaDatos);
        }

        int resultado = selector.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();
        procesarImportacionArchivo(archivo.getAbsolutePath());
    }

    private void importarConRutaManual() {
        String ruta = JOptionPane.showInputDialog(
            this,
            "Ingresa la ruta del archivo CSV o JSON:",
            "datos/Estudiantes1.csv"
        );
        if (ruta == null || ruta.trim().isEmpty()) {
            return;
        }

        procesarImportacionArchivo(ruta);
    }

    private void procesarImportacionArchivo(String rutaArchivo) {
        try {
            int totalImportados = adaptadorEntrada.importarDesdeArchivo(rutaArchivo);
            JOptionPane.showMessageDialog(
                this,
                "Importacion completada. Registros importados: " + totalImportados,
                "Importacion exitosa",
                JOptionPane.INFORMATION_MESSAGE
            );

            controlador.mostrarTodos();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de importacion", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * clickExportar - Maneja el evento del botón Exportar con diálogos interactivos
     */
    private void clickExportar() {
        // Paso 1: Elegir formato (CSV o JSON)
        String[] formatosDisponibles = {"CSV", "JSON"};
        int opcionFormato = JOptionPane.showOptionDialog(
            this,
            "Selecciona el formato de exportación:",
            "Elegir Formato",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            formatosDisponibles,
            formatosDisponibles[0]
        );

        if (opcionFormato == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String formato = formatosDisponibles[opcionFormato].toLowerCase();

        // Paso 2: Elegir cómo guardar (selector de carpeta o ruta manual)
        String[] opciones = {"Seleccionar carpeta", "Ingresar ruta"};
        int opcionGuardado = JOptionPane.showOptionDialog(
            this,
            "¿Cómo deseas guardar el archivo?",
            "Guardar Exportación",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (opcionGuardado == JOptionPane.CLOSED_OPTION) {
            return;
        }

        if (opcionGuardado == 0) {
            exportarConSelectorCarpeta(formato);
        } else {
            exportarConRutaManual(formato);
        }
    }

    /**
     * exportarConSelectorCarpeta - Abre un selector de carpeta para guardar la exportación
     */
    private void exportarConSelectorCarpeta(String formato) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Selecciona dónde guardar la exportación");
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        File carpetaDatos = new File("datos");
        if (carpetaDatos.exists() && carpetaDatos.isDirectory()) {
            selector.setCurrentDirectory(carpetaDatos);
        }

        int resultado = selector.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File carpetaSeleccionada = selector.getSelectedFile();
        String nombreArchivo = pedirNombreArchivo();
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return;
        }

        String rutaCompleta = carpetaSeleccionada.getAbsolutePath() + File.separator + nombreArchivo;
        procesarExportacion(rutaCompleta, formato);
    }

    /**
     * exportarConRutaManual - Permite ingresar la ruta manualmente
     */
    private void exportarConRutaManual(String formato) {
        String rutaBase = JOptionPane.showInputDialog(
            this,
            "Ingresa la ruta base donde guardar el archivo:",
            "datos"
        );
        if (rutaBase == null || rutaBase.trim().isEmpty()) {
            return;
        }

        String nombreArchivo = pedirNombreArchivo();
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return;
        }

        String rutaCompleta = rutaBase + File.separator + nombreArchivo;
        procesarExportacion(rutaCompleta, formato);
    }

    /**
     * pedirNombreArchivo - Solicita al usuario el nombre del archivo a exportar
     */
    private String pedirNombreArchivo() {
        return JOptionPane.showInputDialog(
            this,
            "Ingresa el nombre del archivo (sin extensión):",
            "estudiantes_exportados"
        );
    }

    /**
     * procesarExportacion - Ejecuta la exportación con el formato y ruta especificados
     */
    private void procesarExportacion(String rutaArchivo, String formato) {
        try {
            controlador.exportarEstudiantes(rutaArchivo, formato);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en exportación", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * mostrarMensaje - Muestra un mensaje en el área de resultados
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    /**
     * mostrarTablaEstudiantes - Actualiza la tabla con la lista de estudiantes
     */
    public void mostrarTablaEstudiantes(List<Estudiante> estudiantes) {
        modeloTabla.setRowCount(0);
        for (Estudiante est : estudiantes) {
            modeloTabla.addRow(new Object[]{est.getId(), est.getNombre(), est.getEdad()});
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
        txtId.requestFocus();
    }

    /**
     * onEstudianteAgregado - Método del Observer, notificado cuando se agrega un estudiante
     */
    @Override
    public void onEstudianteAgregado(Estudiante estudiante) {
        mostrarMensaje("✓ Estudiante agregado: " + estudiante.getNombre());
    }

    /**
     * onEstudianteActualizado - Método del Observer, notificado cuando se actualiza un estudiante
     */
    @Override
    public void onEstudianteActualizado(Estudiante estudiante) {
        mostrarMensaje("✓ Estudiante actualizado: " + estudiante.getNombre());
    }

    /**
     * onEstudianteEliminado - Método del Observer, notificado cuando se elimina un estudiante
     */
    @Override
    public void onEstudianteEliminado(String id) {
        mostrarMensaje("✓ Estudiante eliminado (ID: " + id + ")");
    }

    /**
     * onListaActualizada - Método del Observer, notificado cuando se actualiza la lista de estudiantes
     */
    @Override
    public void onListaActualizada(List<Estudiante> estudiantes) {
        mostrarTablaEstudiantes(estudiantes);
    }
}
