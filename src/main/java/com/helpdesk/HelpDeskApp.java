package com.helpdesk;

import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.*;
import com.helpdesk.ui.MainWindow;
import com.helpdesk.ui.views.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelpDeskApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            JsonPersistence   persistence   = new JsonPersistence();
            IncidenciaService service       = new IncidenciaService(persistence);
            UsuarioService    usuarioService = new UsuarioService(persistence);
            ExportService     exportService  = new ExportService(service);

            primaryStage.setTitle("HelpDesk IT — Sistema de Gestión de Incidencias");
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

            // Primero mostramos el login
            mostrarLogin(primaryStage, usuarioService, service, exportService);

        } catch (Exception e) {
            System.err.println("Error al iniciar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarLogin(Stage stage, UsuarioService us,
                               IncidenciaService service, ExportService es) {
        LoginView login = new LoginView(us, () -> mostrarApp(stage, service, es));
        Scene scene = new Scene(login, 1280, 780);
        scene.getStylesheets().add(
            getClass().getResource("/com/helpdesk/css/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void mostrarApp(Stage stage, IncidenciaService service, ExportService es) {
        MainWindow main  = new MainWindow(service, es);
        Scene scene = new Scene(main, 1280, 780);
        scene.getStylesheets().add(
            getClass().getResource("/com/helpdesk/css/styles.css").toExternalForm());
        stage.setScene(scene);
    }

    public static void main(String[] args) { launch(args); }
}
