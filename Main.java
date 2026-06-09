import datos.EstudianteRepositorio;
import presentacion.VistaEstudiante;

import javax.swing.SwingUtilities;

import logica_negocio.IServicioDecorador;
import logica_negocio.ServicioCRUDEstudiante;
import logica_negocio.ValidacionDecorador;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            EstudianteRepositorio repositorio = new EstudianteRepositorio();

            VistaEstudiante vista = new VistaEstudiante();

            ServicioCRUDEstudiante servicioBase =
                new ServicioCRUDEstudiante(repositorio, vista);

            IServicioDecorador servicioDecorado =
                new ValidacionDecorador(servicioBase);

        
            vista.setControlador(servicioDecorado);
            
            // Registrar la vista como observadora del servicio
            servicioBase.agregarObservador(vista);

        });
    }
}