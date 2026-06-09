package logica_negocio;

import java.util.List;

/**
 * Interfaz IEstrategiaExportacion - Strategy
 * Define los métodos que deben implementar las estrategias de exportación
 */
public interface IEstrategiaExportacion {
    void exportar(List<Estudiante> estudiantes, String rutaArchivo);
    String obtenerExtension();
}
