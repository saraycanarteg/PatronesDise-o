package logica_negocio;
import javax.swing.JOptionPane;
/**
 * Clase ValidacionDecorador - Patrón Decorator
 * Agrega validaciones EXTRAS sin modificar ServicioCRUDEstudiante
 */
public class ValidacionDecorador implements IServicioDecorador {
    private IServicioDecorador servicio;

    public ValidacionDecorador(IServicioDecorador servicio) {
        this.servicio = servicio;
    }
    @Override
    public void mostrarTodos() {
        servicio.mostrarTodos();
    }

    public void agregarEstudiante(String id, String nombre, String edad) {
        if (edad != null && !edad.isEmpty()) {
            try {
                int edadInt = Integer.parseInt(edad);
                if (edadInt <= 0) {
                    JOptionPane.showMessageDialog(
                        null,
                        "La edad debe ser mayor a cero.",
                        "Error de validación",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            } catch (NumberFormatException e) {
                // Dejar que el servicio base maneje este error
            }
        }
        servicio.agregarEstudiante(id, nombre, edad);
    }

    public void actualizarEstudiante(String id, String nombre, String edad) {
        // Validación extra: edad > 0
        if (edad != null && !edad.isEmpty()) {
            try {
                int edadInt = Integer.parseInt(edad);
                if (edadInt <= 0) {
                        JOptionPane.showMessageDialog(
                        null,
                        "La edad debe ser mayor a cero.",
                        "Error de validación",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
            } catch (NumberFormatException e) {
                // Dejar que el servicio base maneje este error
            }
        }
        servicio.actualizarEstudiante(id, nombre, edad);
    }

    public void eliminarEstudiante(String id) {
        servicio.eliminarEstudiante(id);
    }

    public Estudiante buscarEstudiante(String id) {
        return servicio.buscarEstudiante(id);
    }

    @Override
    public void exportarEstudiantes(String rutaArchivo, String formato) {
        servicio.exportarEstudiantes(rutaArchivo, formato);
    }
}
