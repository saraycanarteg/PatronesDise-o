ww

## Flujo de Ejecución Ejemplo (RF-01 - Agregar Estudiante):

1. **Usuario** → Click en botón "Agregar" en `VistaEstudiante`
2. **VistaEstudiante** → `clickAgregar()` → Llama `ServicioCRUDEstudiante.agregarEstudiante()`
3. **ServicioCRUDEstudiante** → `validarDatos()` (Decorator) → Verifica ID, nombre, edad
4. **EstudianteRepositorio** → `guardar(estudiante)` → Persiste en memoria/archivo
5. **ServicioCRUDEstudiante** → `notificarAgregado(estudiante)` → Itera observadores
6. **VistaEstudiante** → `onEstudianteAgregado()` → Muestra mensaje de confirmación
7. **ServicioCRUDEstudiante** → `notificarListaActualizada()` → Itera observadores
8. **VistaEstudiante** → `onListaActualizada(List)` → Actualiza tabla de estudiantes
