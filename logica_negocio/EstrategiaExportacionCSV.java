package logica_negocio;

import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase EstrategiaExportacionCSV - Estrategia Concreta
 * Implementa la exportación de estudiantes a formato CSV
 */
public class EstrategiaExportacionCSV implements IEstrategiaExportacion {
    @Override
    public void exportar(List<Estudiante> estudiantes, String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            // Escribir encabezado
            
            // Escribir datos
            for (Estudiante est : estudiantes) {
                writer.write(String.format("%s,%s,%d\n", 
                    est.getId(), 
                    est.getNombre(), 
                    est.getEdad()
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al exportar a CSV: " + e.getMessage());
        }
    }

    @Override
    public String obtenerExtension() {
        return "csv";
    }
}
