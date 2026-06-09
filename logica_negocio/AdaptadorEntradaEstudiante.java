package logica_negocio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adaptador que transforma una fuente externa (CSV/JSON) con el formato:
 * codigo, nombreCompleto, anios
 * al modelo interno Estudiante: id, nombre, edad.
 */
public class AdaptadorEntradaEstudiante {
    private static final Pattern CAMPO_JSON = Pattern.compile(
        "\"(codigo|nombreCompleto|anios)\"\\s*:\\s*(\"([^\"]*)\"|[0-9]+)"
    );
    private static final Pattern OBJETO_JSON = Pattern.compile("\\{[^{}]*\\}", Pattern.DOTALL);

    private final IServicioDecorador servicioDestino;

    public AdaptadorEntradaEstudiante(IServicioDecorador servicioDestino) {
        this.servicioDestino = servicioDestino;
    }

    /**
     * Recibe una linea CSV en el orden: codigo,nombreCompleto,anios
     */
    public void agregarDesdeCSV(String lineaCsv) {
        Estudiante estudiante = adaptarDesdeCSV(lineaCsv);
        servicioDestino.agregarEstudiante(
            estudiante.getId(),
            estudiante.getNombre(),
            String.valueOf(estudiante.getEdad())
        );
    }

    /**
     * Recibe un JSON plano con claves: codigo, nombreCompleto, anios.
     * Ejemplo: {"codigo":"A1","nombreCompleto":"Ana Perez","anios":20}
     */
    public void agregarDesdeJSON(String json) {
        Estudiante estudiante = adaptarDesdeJSON(json);
        servicioDestino.agregarEstudiante(
            estudiante.getId(),
            estudiante.getNombre(),
            String.valueOf(estudiante.getEdad())
        );
    }

    /**
     * Importa estudiantes desde un archivo CSV o JSON.
     * Retorna la cantidad de registros importados.
     */
    public int importarDesdeArchivo(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria.");
        }

        Path ruta = Paths.get(rutaArchivo.trim());
        String nombre = ruta.getFileName().toString().toLowerCase();

        try {
            if (nombre.endsWith(".csv")) {
                return importarArchivoCsv(ruta);
            }
            if (nombre.endsWith(".json")) {
                return importarArchivoJson(ruta);
            }
            throw new IllegalArgumentException("Formato no soportado. Usa .csv o .json");
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo: " + rutaArchivo);
        }
    }

    public Estudiante adaptarDesdeCSV(String lineaCsv) {
        if (lineaCsv == null || lineaCsv.trim().isEmpty()) {
            throw new IllegalArgumentException("La linea CSV no puede ser vacia.");
        }

        String[] partes = lineaCsv.split(",");
        if (partes.length != 3) {
            throw new IllegalArgumentException(
                "CSV invalido. Formato esperado: codigo,nombreCompleto,anios"
            );
        }

        String codigo = partes[0].trim();
        String nombreCompleto = partes[1].trim();
        String aniosTexto = partes[2].trim();

        int anios = parsearAnios(aniosTexto);
        return new Estudiante(codigo, nombreCompleto, anios);
    }

    public Estudiante adaptarDesdeJSON(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("El JSON no puede ser vacio.");
        }

        Map<String, String> campos = extraerCamposJson(json);

        String codigo = campos.get("codigo");
        String nombreCompleto = campos.get("nombreCompleto");
        String aniosTexto = campos.get("anios");

        if (codigo == null || nombreCompleto == null || aniosTexto == null) {
            throw new IllegalArgumentException(
                "JSON invalido. Debe incluir: codigo, nombreCompleto, anios"
            );
        }

        int anios = parsearAnios(aniosTexto);
        return new Estudiante(codigo.trim(), nombreCompleto.trim(), anios);
    }

    private int parsearAnios(String aniosTexto) {
        try {
            return Integer.parseInt(aniosTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo anios debe ser numerico.");
        }
    }

    private Map<String, String> extraerCamposJson(String json) {
        Map<String, String> campos = new HashMap<>();
        Matcher matcher = CAMPO_JSON.matcher(json);

        while (matcher.find()) {
            String clave = matcher.group(1);
            String valorConComillas = matcher.group(2);
            String valorSinComillas = matcher.group(3);

            String valor = valorSinComillas != null ? valorSinComillas : valorConComillas;
            campos.put(clave, valor);
        }

        return campos;
    }

    private int importarArchivoCsv(Path ruta) throws IOException {
        List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        int importados = 0;

        for (String linea : lineas) {
            if (linea == null || linea.trim().isEmpty()) {
                continue;
            }

            if (linea.toLowerCase().startsWith("codigo,")) {
                continue;
            }

            agregarDesdeCSV(linea);
            importados++;
        }

        if (importados == 0) {
            throw new IllegalArgumentException("El archivo CSV no contiene registros validos.");
        }

        return importados;
    }

    private int importarArchivoJson(Path ruta) throws IOException {
        String contenido = Files.readString(ruta, StandardCharsets.UTF_8).trim();
        if (contenido.isEmpty()) {
            throw new IllegalArgumentException("El archivo JSON esta vacio.");
        }

        int importados = 0;
        if (contenido.startsWith("[")) {
            Matcher objetos = OBJETO_JSON.matcher(contenido);
            while (objetos.find()) {
                agregarDesdeJSON(objetos.group());
                importados++;
            }
        } else {
            agregarDesdeJSON(contenido);
            importados = 1;
        }

        if (importados == 0) {
            throw new IllegalArgumentException("No se encontraron objetos validos en el JSON.");
        }

        return importados;
    }
}