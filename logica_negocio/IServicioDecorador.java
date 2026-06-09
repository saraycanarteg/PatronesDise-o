package logica_negocio;

public interface IServicioDecorador {
    void agregarEstudiante(String id, String nombre, String edad);
    void actualizarEstudiante(String id, String nombre, String edad);
    void eliminarEstudiante(String id);
    void mostrarTodos();
    Estudiante buscarEstudiante(String id);
    void exportarEstudiantes(String rutaArchivo, String formato);
}
