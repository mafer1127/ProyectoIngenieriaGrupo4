package com.helpdesk.ui;

import java.io.File;
import java.time.LocalDate;

import com.helpdesk.model.Usuario;
import com.helpdesk.service.ExportService;
import com.helpdesk.service.IncidenciaService;
import com.helpdesk.service.SessionManager;
import com.helpdesk.ui.views.DetalleIncidenciaView;
import com.helpdesk.ui.views.EstadisticasSLAView;
import com.helpdesk.ui.views.GestionTecnicosView;
import com.helpdesk.ui.views.PanelIncidenciasView;
import com.helpdesk.ui.views.PanelResumenView;
import com.helpdesk.util.UIHelper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class MainWindow extends BorderPane {

    private final IncidenciaService service;
    private final ExportService     exportService;

    private PanelResumenView      vistaResumen;
    private PanelIncidenciasView  vistaIncidencias;
    private DetalleIncidenciaView vistaDetalle;
    private GestionTecnicosView   vistaTecnicos;
    private EstadisticasSLAView   vistaSLA;

    private final StackPane contentArea = new StackPane();
    private Button          btnActivo;
    private Label           lblTopTitle;

    public MainWindow(IncidenciaService service, ExportService exportService) {
        this.service       = service;
        this.exportService = exportService;
        construirLayout();
    }

    private void construirLayout() {
        vistaResumen     = new PanelResumenView(service);
        vistaDetalle     = new DetalleIncidenciaView(service, () -> navegarA("incidencias"));
        vistaIncidencias = new PanelIncidenciasView(service, exportService, () -> navegarA("detalle"));
        vistaIncidencias.setDetalleView(vistaDetalle);
        vistaTecnicos    = new GestionTecnicosView(service);
        vistaSLA         = new EstadisticasSLAView(service);


        setLeft(construirSidebar());
        setTop(construirTopbar());

        contentArea.getChildren().addAll(
            vistaResumen, vistaIncidencias, vistaDetalle,
            vistaTecnicos, vistaSLA
        );
        setCenter(contentArea);

        // Vista inicial
        Button btnHome = (Button) ((VBox) getLeft()).lookup(".nav-btn");
        activarVista(btnHome, vistaResumen, "DASHBOARD");
    }

    // ─── Sidebar ──────────────────────────────────────────────────────────────
    private VBox construirSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");

        // Logo
        VBox logoBox = new VBox(2);
        logoBox.getStyleClass().add("sidebar-logo-box");
        logoBox.setPadding(new Insets(22, 20, 18, 20));
        HBox logoRow = new HBox(10);
        logoRow.setAlignment(Pos.CENTER_LEFT);
        Label icoLogo = new Label("🖥");
        icoLogo.setStyle("-fx-font-size: 20px;");
        Label lblLogo = new Label("Help Desk");
        lblLogo.getStyleClass().add("sidebar-logo");
        logoRow.getChildren().addAll(icoLogo, lblLogo);
        logoBox.getChildren().add(logoRow);

        // ─ Sección HOME ─
        Label secMain = new Label("MENÚ PRINCIPAL");
        secMain.getStyleClass().add("nav-section-label");

        Button btnResumen     = navBtn("🏠  HOME",                "resumen",     "Panel de Resumen");
        Button btnIncidencias = navBtn("🎫  INCIDENCIAS",         "incidencias", "Incidencias");
        Button btnSLA         = navBtn("📈  ESTADÍSTICAS",        "sla",         "Estadísticas SLA");
        Button btnGrafica     = navBtn("📤  EXPORTAR",            "exportar",     "Exportar CSV");

        // ─ Sección CONFIGURACIÓN ─
        Label secConf = new Label("CONFIGURACIÓN");
        secConf.getStyleClass().add("nav-section-label");

        Button btnTecnicos = navBtn("👷  GESTIÓN TÉCNICOS",      "tecnicos",    "Gestión de Técnicos");
        

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ─ Usuario abajo ─
        VBox userBox = construirUserBox();

        Button btnLogout = new Button("🚪  Cerrar sesión");
        btnLogout.getStyleClass().add("btn-logout");
        btnLogout.setOnAction(e -> {
            SessionManager.getInstance().logout();
            UIHelper.alertInfo("Sesión cerrada", "Has cerrado sesión correctamente.\nReinicia la aplicación para volver a entrar.");
        });

        boolean esTecnico = SessionManager.getInstance().esTecnicoOAdmin();

        sidebar.getChildren().addAll(logoBox, secMain, btnResumen);
        if (esTecnico) sidebar.getChildren().addAll(btnIncidencias, btnSLA, btnGrafica);
        else sidebar.getChildren().add(btnIncidencias);

        sidebar.getChildren().addAll(secConf);
        if (esTecnico) sidebar.getChildren().add(btnTecnicos);
        

        return sidebar;
    }

    private VBox construirUserBox() {
        VBox box = new VBox(2);
        box.getStyleClass().add("sidebar-user-box");
        box.setPadding(new Insets(12, 16, 12, 16));

        if (SessionManager.getInstance().isLoggedIn()) {
            Usuario u = SessionManager.getInstance().getUsuario();
            Label name = new Label(u.getNombre());
            name.getStyleClass().add("sidebar-user-name");
            Label role = new Label("Rol: " + u.getRol().name().replace("_", " "));
            role.getStyleClass().add("sidebar-user-role");
            box.getChildren().addAll(name, role);
        }
        VBox.setMargin(box, new Insets(0, 12, 8, 12));
        return box;
    }

    // ─── Topbar ───────────────────────────────────────────────────────────────
    private HBox construirTopbar() {
        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(Pos.CENTER_LEFT);

        lblTopTitle = new Label("DASHBOARD");
        lblTopTitle.getStyleClass().add("topbar-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Notificación
        Button btnBell = new Button("🔔");
        btnBell.getStyleClass().add("btn-icon");
        btnBell.setStyle("-fx-font-size: 16px;");

        // Avatar + nombre usuario
        HBox userRow = new HBox(8);
        userRow.setAlignment(Pos.CENTER);

        Label avatar = new Label();
        avatar.getStyleClass().add("topbar-avatar");
        Label userName = new Label("Usuario");
        userName.getStyleClass().add("topbar-user-name");

        if (SessionManager.getInstance().isLoggedIn()) {
            Usuario u = SessionManager.getInstance().getUsuario();
            String initials = u.getNombre().isEmpty() ? "U"
                : String.valueOf(u.getNombre().charAt(0)).toUpperCase();
            avatar.setText(initials);
            userName.setText(u.getNombre());
        }

        Label arrow = new Label("⌄");
        arrow.setStyle("-fx-text-fill: #7A8BB5; -fx-font-size: 12px;");
        userRow.getChildren().addAll(avatar, userName, arrow);

        topbar.getChildren().addAll(lblTopTitle, spacer, btnBell, userRow);
        topbar.setSpacing(12);
        return topbar;
    }

    // ─── Navegación ──────────────────────────────────────────────────────────
    private Button navBtn(String texto, String vistaId, String titulo) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("nav-btn");
        btn.setOnAction(e -> {
            switch (vistaId) {
                case "resumen"     -> { vistaResumen.cargar();          activarVista(btn, vistaResumen, "DASHBOARD"); }
                case "incidencias" -> { vistaIncidencias.cargarDatos(); activarVista(btn, vistaIncidencias, "DASHBOARD"); }
                case "tecnicos"    -> { vistaTecnicos.cargarDatos();    activarVista(btn, vistaTecnicos, "DASHBOARD"); }
                case "sla"         -> { vistaSLA.cargar();              activarVista(btn, vistaSLA, "DASHBOARD"); }
                case "exportar"    -> exportarMesActual();
            }
        });
        return btn;
    }

    private void navegarA(String vista) {
        switch (vista) {
            case "detalle"     -> mostrarVista(vistaDetalle);
            case "incidencias" -> { vistaIncidencias.cargarDatos(); mostrarVista(vistaIncidencias); }
        }
    }

    private void activarVista(Button btn, Node vista, String titulo) {
        if (btnActivo != null) btnActivo.getStyleClass().remove("nav-btn-active");
        if (btn != null) {
            btn.getStyleClass().add("nav-btn-active");
            btnActivo = btn;
        }
        if (lblTopTitle != null) lblTopTitle.setText(titulo);
        mostrarVista(vista);
    }

    private void mostrarVista(Node vista) {
        contentArea.getChildren().forEach(n -> n.setVisible(false));
        vista.setVisible(true);
    }

    private void exportarMesActual() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar CSV del mes actual");

        LocalDate hoy = LocalDate.now();
        String nombreArchivo = String.format("incidencias_%d_%02d.csv", hoy.getYear(), hoy.getMonthValue());
        fc.setInitialFileName(nombreArchivo);

        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File f = fc.showSaveDialog(getScene().getWindow());
        if (f == null) return;

        try {
            exportService.exportarMesActualCSV(f.getAbsolutePath());
            UIHelper.alertInfo(
                "Exportación completada",
                "Se ha generado el CSV con las incidencias del mes actual."
            );
        } catch (Exception ex) {
            UIHelper.alertError("Error al exportar", ex.getMessage());
        }
    }


}
