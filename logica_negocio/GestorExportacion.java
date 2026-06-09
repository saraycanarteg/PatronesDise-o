package logica_negocio;

import java.util.*;

/**
 * Clase GestorExportacion - Context
 * Gestiona las diferentes estrategias de exportación y delega el trabajo
 */
public class GestorExportacion {
    private Map<String, IEstrategiaExportacion> estrategias;

    public GestorExportacion() {
        this.estrategias = new HashMap<>();
        // Registrar estrategias disponibles
        estrategias.put("csv", new EstrategiaExportacionCSV());
        estrategias.put("json", new EstrategiaExportacionJSON());
    }

    public void exportar(List<Estudiante> estudiantes, String formato, String rutaArchivo) {
        IEstrategiaExportacion estrategia = estrategias.get(formato.toLowerCase());
        
        if (estrategia == null) {
            throw new IllegalArgumentException("Formato no soportado: " + formato);
        }
        
        // Si no especificó extensión, la agregamos
        if (!rutaArchivo.endsWith("." + formato.toLowerCase())) {
            rutaArchivo = rutaArchivo + "." + formato.toLowerCase();
        }
        
        estrategia.exportar(estudiantes, rutaArchivo);
    }

    public void agregarEstrategia(String formato, IEstrategiaExportacion estrategia) {
        estrategias.put(formato.toLowerCase(), estrategia);
    }
}
