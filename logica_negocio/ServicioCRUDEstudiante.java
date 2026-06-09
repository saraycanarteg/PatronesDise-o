package logica_negocio;

import datos.EstudianteRepositorio;
import presentacion.VistaEstudiante;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase ControlEstudiante - Controlador
 * Maneja la lógica de negocio y la comunicación entre Vista y Repositorio
 * Arquitectura MVC - CONTROLLER
 */
public class ServicioCRUDEstudiante implements IServicioDecorador{
    private EstudianteRepositorio repositorio;
    private VistaEstudiante vista;
    private List<IObservadorCambios> observadores;
    private GestorExportacion gestorExportacion;

    public ServicioCRUDEstudiante(EstudianteRepositorio repositorio, VistaEstudiante vista) {
        this.repositorio = repositorio;
        this.vista = vista;
        this.observadores = new ArrayList<>();
        this.gestorExportacion = new GestorExportacion();
        //this.vista.setControlador(this);
    }

    /**
     * Agrega un observador a la lista de notificaciones
     */
    public void agregarObservador(IObservadorCambios observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    /**
     * Remueve un observador de la lista de notificaciones
     */
    public void removerObservador(IObservadorCambios observador) {
        observadores.remove(observador);
    }

    /**
     * Notifica a todos los observadores que se agregó un estudiante
     */
    private void notificarAgregado(Estudiante estudiante) {
        for (IObservadorCambios obs : observadores) {
            obs.onEstudianteAgregado(estudiante);
        }
    }

    /**
     * Notifica a todos los observadores que se actualizó un estudiante
     */
    private void notificarActualizado(Estudiante estudiante) {
        for (IObservadorCambios obs : observadores) {
            obs.onEstudianteActualizado(estudiante);
        }
    }

    /**
     * Notifica a todos los observadores que se eliminó un estudiante
     */
    private void notificarEliminado(String id) {
        for (IObservadorCambios obs : observadores) {
            obs.onEstudianteEliminado(id);
        }
    }

    /**
     * Notifica a todos los observadores la actualización de la lista
     */
    private void notificarListaActualizada() {
        List<Estudiante> estudiantes = repositorio.listarTodos();
        for (IObservadorCambios obs : observadores) {
            obs.onListaActualizada(estudiantes);
        }
    }

    /**
     * Valida los datos del estudiante
     */
    public String validarDatos(String id, String nombre, String edad) {
        // Validar ID
        if (id == null || id.trim().isEmpty()) {
            return "El ID es obligatorio.";
        }

        // Validar que el ID sea único
        if (repositorio.existeId(id)) {
            return "El ID ya existe.";
        }

        // Validar Nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El Nombre es obligatorio.";
        }

        // Validar Edad
        if (edad == null || edad.trim().isEmpty()) {
            return "La Edad es obligatoria.";
        }

        return null; // Sin errores
    }

    /**
     * Agrega un nuevo estudiante (RF-01)
     */
    public void agregarEstudiante(String id, String nombre, String edad) {
        String error = validarDatos(id, nombre, edad);
        if (error != null) {
            vista.mostrarMensaje(error);
            return;
        }

        Estudiante estudiante = new Estudiante(id, nombre, Integer.parseInt(edad));
        repositorio.guardar(estudiante);
        vista.mostrarMensaje("Estudiante agregado exitosamente.");
        notificarAgregado(estudiante);
        notificarListaActualizada();
    }

    /**
     * Actualiza un estudiante existente (RF-02)
     */
    public void actualizarEstudiante(String id, String nombre, String edad) {
        Estudiante estudiante = repositorio.buscarPorId(id);
        if (estudiante == null) {
            vista.mostrarMensaje("Estudiante no encontrado.");
            return;
        }

        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            vista.mostrarMensaje("El Nombre es obligatorio.");
            return;
        }

        // Validar edad
        if (edad == null || edad.trim().isEmpty()) {
            vista.mostrarMensaje("La Edad es obligatoria.");
            return;
        }

        try {
            int edadInt = Integer.parseInt(edad);
            if (edadInt <= 0 || edadInt > 120) {
                vista.mostrarMensaje("La Edad debe ser numérica y estar en un rango válido (1-120).");
                return;
            }

            Estudiante estudianteActualizado = new Estudiante(id, nombre, edadInt);
            repositorio.actualizar(estudianteActualizado);
            vista.mostrarMensaje("Estudiante actualizado exitosamente.");
            notificarActualizado(estudianteActualizado);
            notificarListaActualizada();
        } catch (NumberFormatException e) {
            vista.mostrarMensaje("La Edad debe ser numérica.");
        }
    }

    /**
     * Elimina un estudiante (RF-03)
     */
    public void eliminarEstudiante(String id) {
        if (id == null || id.trim().isEmpty()) {
            vista.mostrarMensaje("Por favor ingresa un ID para eliminar.");
            return;
        }

        Estudiante estudiante = repositorio.buscarPorId(id);
        if (estudiante == null) {
            vista.mostrarMensaje("Estudiante no encontrado.");
            return;
        }

        repositorio.eliminar(id);
        vista.mostrarMensaje("Estudiante eliminado exitosamente.");
        notificarEliminado(id);
        notificarListaActualizada();
    }

    /**
     * Muestra todos los estudiantes (RF-04)
     */
    public void mostrarTodos() {
        List<Estudiante> estudiantes = repositorio.listarTodos();
        
        if (estudiantes.isEmpty()) {
            vista.mostrarMensaje("No hay estudiantes registrados.");
            vista.mostrarTablaEstudiantes(estudiantes);
        } else {
            vista.mostrarMensaje("Total de estudiantes: " + estudiantes.size());
            vista.mostrarTablaEstudiantes(estudiantes);
        }
        notificarListaActualizada();
    }

    /**
     * Busca un estudiante por ID
     */
    public Estudiante buscarEstudiante(String id) {
        return repositorio.buscarPorId(id);
    }

    /**
     * Exporta todos los estudiantes usando Strategy Pattern (RF-06)
     */
    public void exportarEstudiantes(String rutaArchivo, String formato) {
        List<Estudiante> estudiantes = repositorio.listarTodos();
        if (estudiantes.isEmpty()) {
            vista.mostrarMensaje("No hay estudiantes para exportar.");
            return;
        }
        
        try {
            gestorExportacion.exportar(estudiantes, formato, rutaArchivo);
            vista.mostrarMensaje("✓ Exportación exitosa: " + rutaArchivo + "." + formato);
        } catch (Exception e) {
            vista.mostrarMensaje("✗ Error en exportación: " + e.getMessage());
        }
    }
}
