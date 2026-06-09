package logica_negocio;

import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase EstrategiaExportacionJSON - Estrategia Concreta
 * Implementa la exportación de estudiantes a formato JSON
 */
public class EstrategiaExportacionJSON implements IEstrategiaExportacion {
    @Override
    public void exportar(List<Estudiante> estudiantes, String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write("[\n");
            for (int i = 0; i < estudiantes.size(); i++) {
                Estudiante est = estudiantes.get(i);
                writer.write(String.format("  {\"codigo\":\"%s\",\"nombreCompleto\":\"%s\",\"aniosww\":%d}",
                    est.getId(),
                    est.getNombre(),
                    est.getEdad()
                ));
                
                if (i < estudiantes.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            writer.write("]");
        } catch (IOException e) {
            throw new RuntimeException("Error al exportar a JSON: " + e.getMessage());
        }
    }

    @Override
    public String obtenerExtension() {
        return "json";
    }
}
