# HelpDesk IT — Sistema de Gestión de Incidencias

Sistema de ticketing interno inspirado en ITIL, desarrollado con **Java 21 + JavaFX 21 + Gson**.

---

## Estructura del proyecto

```
helpdesk/
├── pom.xml
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── com/helpdesk/
    │       ├── HelpDeskApp.java              ← Punto de entrada
    │       ├── model/
    │       │   ├── Incidencia.java
    │       │   └── Tecnico.java
    │       ├── persistence/
    │       │   └── JsonPersistence.java      ← Carga/guarda JSON en ~/.helpdesk/
    │       ├── service/
    │       │   ├── IncidenciaService.java    ← Lógica de negocio completa
    │       │   └── ExportService.java        ← Exportación CSV
    │       ├── ui/
    │       │   ├── MainWindow.java           ← Shell con sidebar + navegación
    │       │   ├── views/
    │       │   │   ├── PanelResumenView.java       ← Dashboard con tarjetas KPI
    │       │   │   ├── PanelIncidenciasView.java   ← Listado con filtros
    │       │   │   ├── DetalleIncidenciaView.java  ← Vista detalle
    │       │   │   └── GestionTecnicosView.java    ← CRUD técnicos
    │       │   └── components/
    │       │       ├── IncidenciaFormDialog.java   ← Formulario crear/editar
    │       │       ├── CambioEstadoDialog.java     ← Cambio de estado
    │       │       └── TecnicoFormDialog.java      ← Formulario técnico
    │       └── util/
    │           └── UIHelper.java             ← Badges, alerts, formatos
    └── resources/com/helpdesk/css/
        └── styles.css                        ← Tema oscuro completo
```

---

## Requisitos

| Herramienta | Versión mínima |
|-------------|----------------|
| JDK         | 21             |
| Maven       | 3.8+           |

---

## Cómo ejecutar

```bash
# Desde la carpeta helpdesk/
mvn clean javafx:run
```

## Compilar JAR ejecutable

```bash
mvn clean package
# El JAR queda en target/helpdesk-it-1.0.0-shaded.jar
java -jar target/helpdesk-it-1.0.0-shaded.jar
```

> **Nota JavaFX**: si usas el jar directamente sin Maven, necesitas añadir el SDK de JavaFX al módulo path:
> ```bash
> java --module-path /ruta/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml \
>      -jar helpdesk-it-1.0.0-shaded.jar
> ```

---

## Datos persistentes

Los datos se guardan automáticamente en JSON en:
```
~/.helpdesk/incidencias.json
~/.helpdesk/tecnicos.json
```

---

## Funcionalidades implementadas

### Incidencias
- ✅ CRUD completo (crear, ver, editar, eliminar)
- ✅ Asignación a técnico (bloquea técnicos inactivos)
- ✅ Cambio de estado con máquina de estados (ABIERTA → EN_CURSO → RESUELTA → CERRADA)
- ✅ Fecha de resolución solo al pasar a RESUELTA/CERRADA
- ✅ Descripción de solución obligatoria al resolver
- ✅ Cálculo de tiempo de resolución con `java.time.Duration`
- ✅ Alerta visual para incidencias CRÍTICAS abiertas > 4h (fila roja en tabla + banner en detalle)
- ✅ Filtros por categoría, prioridad, estado, técnico y rango de fechas
- ✅ Búsqueda por título/solicitante
- ✅ Doble clic en tabla abre detalle

### Técnicos
- ✅ CRUD completo
- ✅ Activar/desactivar técnico
- ✅ Protección contra eliminar técnico con incidencias activas

### Panel de Resumen
- ✅ Tarjetas con incidencias abiertas por prioridad
- ✅ Media de horas de resolución por categoría
- ✅ Últimas 5 incidencias del mes
- ✅ Listado de alertas críticas

### Exportación
- ✅ Exportar incidencias del mes actual a CSV (UTF-8 con BOM, compatible con Excel)

### Validaciones
- ✅ Campos obligatorios en todos los formularios
- ✅ Formato de email con regex
- ✅ Fecha resolución > fecha apertura
- ✅ Transiciones de estado inválidas bloqueadas
- ✅ Manejo de archivo corrupto

### UI
- ✅ 3 vistas principales: Panel de Incidencias, Detalle de Incidencia, Gestión de Técnicos
- ✅ Panel de Resumen (dashboard)
- ✅ Tema oscuro completo con CSS JavaFX
- ✅ Badges de prioridad y estado codificados por color
- ✅ Diálogos modales estilizados
