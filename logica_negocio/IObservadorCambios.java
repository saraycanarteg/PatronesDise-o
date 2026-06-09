package logica_negocio;

import java.util.List;

/**
 * Interfaz IObservadorCambios - Observer
 * Define los métodos que deben implementar los observadores para ser notificados
 * de cambios en los datos de estudiantes
 */
public interface IObservadorCambios {
    void onEstudianteAgregado(Estudiante estudiante);
    void onEstudianteActualizado(Estudiante estudiante);
    void onEstudianteEliminado(String id);
    void onListaActualizada(List<Estudiante> estudiantes);
}
