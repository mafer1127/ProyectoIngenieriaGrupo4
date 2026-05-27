package com.helpdesk.ui.views;

import com.helpdesk.model.Usuario;
import com.helpdesk.service.SessionManager;
import com.helpdesk.service.UsuarioService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class LoginView extends BorderPane {

    private final UsuarioService usuarioService;
    private final Runnable       onLoginOk;

    // ── Campos login ──────────────────────────────────────────────────────────
    private final TextField     tfUser  = new TextField();
    private final PasswordField pfPass  = new PasswordField();
    private final Label         lblErr  = new Label();

    // ── Campos registro ───────────────────────────────────────────────────────
    private final TextField     tfRegNombre    = new TextField();
    private final TextField     tfRegApellido  = new TextField();
    private final TextField     tfRegEmail     = new TextField();
    private final PasswordField pfRegPass      = new PasswordField();
    private final TextField     tfRegUsername  = new TextField();
    private final ComboBox<Usuario.Rol> cbRegRol = new ComboBox<>();
    private final Label         lblRegErr      = new Label();

    // ── Estado ────────────────────────────────────────────────────────────────
    private boolean modoRegistro = false;

    // ── Contenedores intercambiables ──────────────────────────────────────────
    private VBox formLogin;
    private VBox formRegistro;
    private VBox formCard;
    private VBox logoCard;

    public LoginView(UsuarioService usuarioService, Runnable onLoginOk) {
        this.usuarioService = usuarioService;
        this.onLoginOk      = onLoginOk;
        construirUI();
    }

    private void construirUI() {
        getStyleClass().add("login-root");

        // ── Card izquierda: Logo ──────────────────────────────────────────────
        logoCard = new VBox(16);
        logoCard.getStyleClass().add("login-logo-card");
        logoCard.setAlignment(Pos.CENTER);
        logoCard.setMinWidth(380);
        logoCard.setMaxWidth(380);
        logoCard.setSpacing(20);

        Label icoLogo  = new Label("🖥");
        icoLogo.setStyle("-fx-font-size: 90px;");
        Label logoTitle = new Label("HelpDesk");
        logoTitle.getStyleClass().add("login-logo-title");
        Label logoSub = new Label("Sistema de Gestión\nde Incidencias");
        logoSub.getStyleClass().add("login-logo-sub");
        logoSub.setTextAlignment(TextAlignment.CENTER);
        logoCard.getChildren().addAll(icoLogo, logoTitle, logoSub);

        // ── Card derecha: formulario ──────────────────────────────────────────
        formCard = new VBox(0);
        formCard.getStyleClass().add("login-form-card");
        formCard.setMinWidth(320);
        formCard.setMaxWidth(340);

        formLogin    = construirFormLogin();
        formRegistro = construirFormRegistro();

        formCard.getChildren().add(formLogin);

        // ── Layout central ────────────────────────────────────────────────────
        HBox center = new HBox(0, logoCard, formCard);
        center.setAlignment(Pos.CENTER);
        center.setPrefWidth(Double.MAX_VALUE);

        VBox wrapper = new VBox(center);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setFillWidth(true);
        wrapper.setPrefWidth(Double.MAX_VALUE);
        wrapper.setPrefHeight(Double.MAX_VALUE);

        //limitar altura para que el padding funcione
        wrapper.setMaxHeight(500); 
        setCenter(wrapper);
    }

    // ── Formulario de LOGIN ───────────────────────────────────────────────────
    private VBox construirFormLogin() {
        VBox box = new VBox(0);

        Label titulo = new Label("Login");
        titulo.getStyleClass().add("login-form-title");
        titulo.setPadding(new Insets(0, 0, 22, 0));

        Label lUser = label("Email");
        tfUser.setPromptText("usuario o email");
        tfUser.getStyleClass().add("login-field");
        tfUser.setMaxWidth(Double.MAX_VALUE);

        Label lPass = label("Contraseña");
        lPass.setPadding(new Insets(12, 0, 4, 0));
        pfPass.setPromptText("••••••••");
        pfPass.getStyleClass().add("login-field");
        pfPass.setMaxWidth(Double.MAX_VALUE);
        pfPass.setOnAction(e -> intentarLogin());

        lblErr.getStyleClass().add("form-error");
        lblErr.setWrapText(true);
        lblErr.setPadding(new Insets(6, 0, 0, 0));

        Button btnLogin = new Button("Login");
        btnLogin.getStyleClass().add("btn-primary");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(btnLogin, new Insets(18, 0, 0, 0));
        btnLogin.setOnAction(e -> intentarLogin());

        // Link "Crea una cuenta"
        HBox linksRow = new HBox();
        linksRow.setAlignment(Pos.CENTER);
        linksRow.setPadding(new Insets(14, 0, 0, 0));
        Label linkCrear = new Label("Crea una cuenta");
        linkCrear.getStyleClass().add("login-link");
        linkCrear.setOnMouseClicked(e -> cambiarModo(true));
        linksRow.getChildren().add(linkCrear);

        Label hint = new Label("");
        hint.getStyleClass().add("login-hint");
        hint.setWrapText(true);
        hint.setTextAlignment(TextAlignment.CENTER);
        hint.setMaxWidth(Double.MAX_VALUE);
        hint.setAlignment(Pos.CENTER);
        VBox.setMargin(hint, new Insets(10, 0, 0, 0));

        box.getChildren().addAll(titulo, lUser, tfUser, lPass, pfPass, lblErr, btnLogin, linksRow, hint);
        return box;
    }

    // ── Formulario de REGISTRO ────────────────────────────────────────────────
    private VBox construirFormRegistro() {
        VBox box = new VBox(0);
        

        Label titulo = new Label("Sign Up");
        titulo.getStyleClass().add("login-form-title");
        titulo.setPadding(new Insets(0, 0, 18, 0));

        // Nombre + Apellido en fila
        HBox nombreRow = new HBox(10);
        VBox boxNombre   = campoConLabel("Nombre",   tfRegNombre,   "Nombre");
        VBox boxApellido = campoConLabel("Apellido", tfRegApellido, "Apellido");
        HBox.setHgrow(boxNombre,   Priority.ALWAYS);
        HBox.setHgrow(boxApellido, Priority.ALWAYS);
        nombreRow.getChildren().addAll(boxNombre, boxApellido);

        VBox boxEmail    = campoConLabel("Email",      tfRegEmail,    "correo@ejemplo.com");
        VBox.setMargin(boxEmail, new Insets(10, 0, 0, 0));

        VBox boxPass = new VBox(4);
        Label lPass = label("Contraseña");
        pfRegPass.setPromptText("mín. 8 chars, 1 mayúscula, 1 número");
        pfRegPass.getStyleClass().add("login-field");
        pfRegPass.setMaxWidth(Double.MAX_VALUE);
        boxPass.getChildren().addAll(lPass, pfRegPass);
        VBox.setMargin(boxPass, new Insets(10, 0, 0, 0));

        VBox boxUser = campoConLabel("Usuario", tfRegUsername, "nombre de usuario único");
        VBox.setMargin(boxUser, new Insets(10, 0, 0, 0));

        // Campo ROL
        cbRegRol.getItems().addAll(Usuario.Rol.USUARIO_FINAL, Usuario.Rol.TECNICO);
        cbRegRol.setValue(Usuario.Rol.USUARIO_FINAL);
        VBox boxRol = new VBox(4);
        boxRol.getChildren().addAll(label("Rol"), cbRegRol);
        cbRegRol.getStyleClass().add("login-field");
        cbRegRol.setMaxWidth(Double.MAX_VALUE);
        
        VBox.setMargin(boxRol, new Insets(10, 0, 0, 0));

        lblRegErr.getStyleClass().add("form-error");
        lblRegErr.setWrapText(true);
        lblRegErr.setPadding(new Insets(6, 0, 0, 0));

        Button btnRegistrar = new Button("Registrarse");
        btnRegistrar.getStyleClass().add("btn-primary");
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(btnRegistrar, new Insets(16, 0, 0, 0));
        btnRegistrar.setOnAction(e -> intentarRegistro());

        HBox linkRow = new HBox();
        linkRow.setAlignment(Pos.CENTER);
        linkRow.setPadding(new Insets(12, 0, 0, 0));
        Label linkVolver = new Label("¿Ya tienes una cuenta?");
        linkVolver.getStyleClass().add("login-link");
        linkVolver.setOnMouseClicked(e -> cambiarModo(false));
        linkRow.getChildren().add(linkVolver);

        box.getChildren().addAll(titulo, nombreRow, boxEmail, boxPass, boxUser, boxRol, lblRegErr, btnRegistrar, linkRow);
        return box;
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────
    private Label label(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("login-label");
        l.setPadding(new Insets(0, 0, 4, 0));
        return l;
    }

    private VBox campoConLabel(String labelTxt, TextField campo, String prompt) {
        VBox box = new VBox(4);
        box.getChildren().add(label(labelTxt));
        campo.setPromptText(prompt);
        campo.getStyleClass().add("login-field");
        campo.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().add(campo);
        return box;
    }

    // ── Cambio Login ↔ Registro ───────────────────────────────────────────────
    private void cambiarModo(boolean registro) {
        modoRegistro = registro;
        formCard.getChildren().clear();
        lblErr.setText("");
        lblRegErr.setText("");
        formCard.getChildren().add(registro ? formRegistro : formLogin);
    }

    // ── Lógica login ──────────────────────────────────────────────────────────
    private void intentarLogin() {
        lblErr.setText("");
        String u = tfUser.getText().trim();
        String p = pfPass.getText();
        if (u.isBlank() || p.isBlank()) { lblErr.setText("Introduce usuario y contraseña."); return; }
        usuarioService.login(u, p).ifPresentOrElse(usuario -> {
            SessionManager.getInstance().login(usuario);
            onLoginOk.run();
        }, () -> lblErr.setText("Usuario o contraseña incorrectos."));
    }

    // ── Lógica registro ───────────────────────────────────────────────────────
    private void intentarRegistro() {
        lblRegErr.setText("");
        String nombre   = tfRegNombre.getText().trim();
        String apellido = tfRegApellido.getText().trim();
        String email    = tfRegEmail.getText().trim();
        String pass     = pfRegPass.getText();
        String username = tfRegUsername.getText().trim();

        if (nombre.isBlank() || apellido.isBlank() || email.isBlank()
                || pass.isBlank() || username.isBlank()) {
            lblRegErr.setText("Completa todos los campos.");
            return;
        }
        if (pass.length() < 8) {
            lblRegErr.setText("La contraseña debe tener al menos 8 caracteres.");
            return;
        }
        if (!pass.matches(".*[A-Z].*")) {
            lblRegErr.setText("La contraseña debe contener al menos una mayúscula.");
            return;
        }
        if (!pass.matches(".*[0-9].*")) {
            lblRegErr.setText("La contraseña debe contener al menos un número.");
            return;
        }
        if (!email.contains("@")) {
            lblRegErr.setText("Introduce un email válido.");
            return;
        }

        try {
            Usuario nuevo = new Usuario(
                username,
                UsuarioService.hash(pass),
                nombre + " " + apellido,
                email,
                cbRegRol.getValue()
            );
            usuarioService.crearUsuario(nuevo);
            // Auto-login tras registro
            SessionManager.getInstance().login(nuevo);
            onLoginOk.run();
        } catch (IllegalArgumentException ex) {
            lblRegErr.setText(ex.getMessage());
        }
    }
}
