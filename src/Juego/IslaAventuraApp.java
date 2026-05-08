package Juego;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * Interfaz gráfica JavaFX para Isla Aventura.
 *
 * Estructura visual:
 *   ┌──────────────────────────────────────────────┐
 *   │  TÍTULO                                      │
 *   ├─────────────────────┬────────────────────────┤
 *   │  Mapa 5×5           │  Panel narrativo        │
 *   │                     ├────────────────────────┤
 *   │                     │  Inventario             │
 *   ├─────────────────────┴────────────────────────┤
 *   │  Controles: N / S / E / O + minijuegos        │
 *   └──────────────────────────────────────────────┘
 */
public class IslaAventuraApp extends Application {

    private GameEngine engine;

    // Mapa visual
    private Label[][] celdas;

    // Panel narrativo
    private TextArea txtNarracion;

    // Inventario
    private Label lblLlave, lblHacha, lblMadera, lblLuz, lblResina;

    // Botones de movimiento
    private Button btnN, btnS, btnE, btnO;

    // Panel de minijuegos (se muestra/oculta según contexto)
    private VBox  panelMinijuego;
    private Label lblMinijuegoTitulo;
    private Label lblAcertijoEnunciado;
    private HBox  pnlBotonesOrco;
    private HBox  pnlAcertijoControles;

    // Colores por tipo de celda
    private static final String CSS_CELDA_BASE = "-fx-border-color: #3a3a3a; -fx-border-width: 1;";

    @Override
    public void start(Stage stage) {
        engine = new GameEngine();

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // ── Título ─────────────────────────────────────────────────────
        Label titulo = new Label("⚓  ISLA AVENTURA");
        titulo.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 22px; "
                + "-fx-font-weight: bold; -fx-text-fill: #e2b96f;");
        HBox header = new HBox(titulo);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(14, 0, 10, 0));
        root.setTop(header);

        // ── Centro: mapa + lateral ──────────────────────────────────────
        HBox centro = new HBox(16);
        centro.setPadding(new Insets(0, 16, 0, 16));

        root.setCenter(centro);

        // Mapa
        GridPane gridMapa = buildMapa();
        VBox panelMapa = new VBox(8, new Label("MAPA"), gridMapa);
        panelMapa.getChildren().get(0).setStyle("-fx-font-family: 'Courier New'; "
                + "-fx-font-size: 11px; -fx-text-fill: #888;");
        panelMapa.setAlignment(Pos.TOP_CENTER);
        panelMapa.setMinWidth(270);

        // Lateral derecho
        VBox lateral = new VBox(12);
        lateral.setFillWidth(true);
        HBox.setHgrow(lateral, Priority.ALWAYS);

        txtNarracion = new TextArea();
        txtNarracion.setEditable(false);
        txtNarracion.setWrapText(true);
        txtNarracion.setPrefHeight(200);
        txtNarracion.setStyle(
                "-fx-control-inner-background: #0d0d1a; "
                + "-fx-text-fill: #c9d1d9; "
                + "-fx-font-family: 'Courier New'; "
                + "-fx-font-size: 13px; "
                + "-fx-border-color: #30363d; -fx-border-width: 1;");

        VBox inventario = buildInventario();
        lateral.getChildren().addAll(txtNarracion, inventario);

        centro.getChildren().addAll(panelMapa, lateral);

        // ── Controles inferiores ────────────────────────────────────────
        VBox footer = buildControles();
        root.setBottom(footer);

        // ── Escena ─────────────────────────────────────────────────────
        Scene scene = new Scene(root, 760, 560);
        stage.setTitle("Isla Aventura");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Estado inicial
        refresh(engine.getMensajePendiente());
    }

    // ── Construcción del mapa ─────────────────────────────────────────────

    private GridPane buildMapa() {
        GridPane grid = new GridPane();
        grid.setHgap(3);
        grid.setVgap(3);
        celdas = new Label[5][5];

        for (int f = 0; f < 5; f++) {
            for (int c = 0; c < 5; c++) {
                Label cell = new Label();
                cell.setMinSize(50, 40);
                cell.setMaxSize(50, 40);
                cell.setAlignment(Pos.CENTER);
                cell.setTextAlignment(TextAlignment.CENTER);
                cell.setStyle(CSS_CELDA_BASE + "-fx-background-color: #16213e;");
                cell.setFont(Font.font("Courier New", FontWeight.BOLD, 9));
                celdas[f][c] = cell;
                grid.add(cell, c, f);
            }
        }
        return grid;
    }

    // ── Inventario ────────────────────────────────────────────────────────

    private VBox buildInventario() {
        Label titulo = new Label("INVENTARIO");
        titulo.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-text-fill: #888;");

        lblLlave  = itemLabel("🗝  Llave");
        lblHacha  = itemLabel("🪓  Hacha");
        lblMadera = itemLabel("🪵  Madera");
        lblLuz    = itemLabel("🔦  Fuente de Luz");
        lblResina = itemLabel("🫙  Resina");

        VBox box = new VBox(5, titulo, lblLlave, lblHacha, lblMadera, lblLuz, lblResina);
        box.setStyle("-fx-background-color: #0d0d1a; -fx-padding: 10; "
                + "-fx-border-color: #30363d; -fx-border-width: 1;");
        return box;
    }

    private Label itemLabel(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-text-fill: #444;");
        return l;
    }

    // ── Controles ─────────────────────────────────────────────────────────

    private VBox buildControles() {
        // Brújula de movimiento
        btnN = botonDir("▲ Norte");
        btnS = botonDir("▼ Sur");
        btnE = botonDir("Este ▶");
        btnO = botonDir("◀ Oeste");

        btnN.setOnAction(e -> procesarMovimiento(GameEngine.Dir.NORTE));
        btnS.setOnAction(e -> procesarMovimiento(GameEngine.Dir.SUR));
        btnE.setOnAction(e -> procesarMovimiento(GameEngine.Dir.ESTE));
        btnO.setOnAction(e -> procesarMovimiento(GameEngine.Dir.OESTE));

        GridPane compass = new GridPane();
        compass.setHgap(6);
        compass.setVgap(6);
        compass.setAlignment(Pos.CENTER);
        compass.add(btnN, 1, 0);
        compass.add(btnO, 0, 1);
        compass.add(btnS, 1, 1);
        compass.add(btnE, 2, 1);

        // Botón reiniciar
        Button btnReinicio = new Button("↺ Nueva partida");
        btnReinicio.setStyle("-fx-background-color: #3a1a1a; -fx-text-fill: #e06c75; "
                + "-fx-font-family: 'Courier New'; -fx-font-size: 12px; "
                + "-fx-border-color: #e06c75; -fx-border-width: 1; -fx-cursor: hand;");
        btnReinicio.setOnAction(e -> refresh(engine.reiniciar()));

        // Panel minijuegos (oculto por defecto)
        panelMinijuego = buildPanelMinijuego();
        panelMinijuego.setVisible(false);
        panelMinijuego.setManaged(false);

        HBox fila = new HBox(30, compass, panelMinijuego, btnReinicio);
        fila.setAlignment(Pos.CENTER);
        fila.setPadding(new Insets(10, 16, 14, 16));
        fila.setStyle("-fx-background-color: #0d0d1a; -fx-border-color: #30363d; -fx-border-width: 1 0 0 0;");

        return new VBox(fila);
    }

    private Button botonDir(String texto) {
        Button b = new Button(texto);
        b.setPrefSize(90, 36);
        b.setStyle("-fx-background-color: #16213e; -fx-text-fill: #e2b96f; "
                + "-fx-font-family: 'Courier New'; -fx-font-size: 12px; "
                + "-fx-border-color: #e2b96f; -fx-border-width: 1; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #e2b96f; -fx-text-fill: #1a1a2e; "
                + "-fx-font-family: 'Courier New'; -fx-font-size: 12px; "
                + "-fx-border-color: #e2b96f; -fx-border-width: 1; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: #16213e; -fx-text-fill: #e2b96f; "
                + "-fx-font-family: 'Courier New'; -fx-font-size: 12px; "
                + "-fx-border-color: #e2b96f; -fx-border-width: 1; -fx-cursor: hand;"));
        return b;
    }

    // ── Panel de Minijuegos ───────────────────────────────────────────────

    private VBox buildPanelMinijuego() {
        lblMinijuegoTitulo = new Label("");
        lblMinijuegoTitulo.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; "
                + "-fx-text-fill: #f0a500; -fx-font-weight: bold;");

        // Orcos: dos botones de esquive
        Button btnD = new Button("⬅  Esquivar Izquierda");
        Button btnI = new Button("Esquivar Derecha  ➡");
        estilizarMinijuego(btnD);
        estilizarMinijuego(btnI);
        btnD.setOnAction(e -> procesarOrco());
        btnI.setOnAction(e -> procesarOrco());

        HBox botonesOrco = new HBox(8, btnD, btnI);
        botonesOrco.setId("orco");
        pnlBotonesOrco = botonesOrco;

        // Acertijo del granjero
        Label enunciado = new Label("Soy un número. Si me sumas a 3, el resultado es 5. ¿Qué número soy?");
        enunciado.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-text-fill: #c9d1d9;");
        enunciado.setWrapText(true);
        enunciado.setMaxWidth(220);
        lblAcertijoEnunciado = enunciado;

        Spinner<Integer> spinner = new Spinner<>(0, 20, 0);
        spinner.setStyle("-fx-font-family: 'Courier New';");
        spinner.setPrefWidth(80);

        Button btnResponder = new Button("Responder");
        estilizarMinijuego(btnResponder);
        btnResponder.setOnAction(e -> {
            int val = spinner.getValue();
            refresh(engine.resolverAcertijo(val));
        });

        HBox acertijoControles = new HBox(8, spinner, btnResponder);
        acertijoControles.setId("acertijo");
        pnlAcertijoControles = acertijoControles;

        VBox panel = new VBox(8, lblMinijuegoTitulo, enunciado, botonesOrco, acertijoControles);
        panel.setStyle("-fx-background-color: #1a1200; -fx-padding: 10; "
                + "-fx-border-color: #f0a500; -fx-border-width: 1;");
        panel.setMaxWidth(250);

        // control de visibilidad interno
        botonesOrco.setVisible(false);
        botonesOrco.setManaged(false);
        enunciado.setVisible(false);
        enunciado.setManaged(false);
        acertijoControles.setVisible(false);
        acertijoControles.setManaged(false);

        return panel;
    }

    private void estilizarMinijuego(Button b) {
        b.setStyle("-fx-background-color: #3a2a00; -fx-text-fill: #f0a500; "
                + "-fx-font-family: 'Courier New'; -fx-font-size: 11px; "
                + "-fx-border-color: #f0a500; -fx-border-width: 1; -fx-cursor: hand;");
    }

    // ── Procesado de acciones ──────────────────────────────────────────────

    private void procesarMovimiento(GameEngine.Dir dir) {
        String msg = engine.move(dir);
        refresh(msg);
    }

    private void procesarOrco() {
        String msg = engine.resolverOrco(true);
        refresh(msg);
    }

    // ── Actualización visual ──────────────────────────────────────────────

    /**
     * Refresca toda la UI con el mensaje producido por el engine.
     */
    private void refresh(String mensaje) {
        txtNarracion.setText(mensaje);
        refreshMapa();
        refreshInventario();
        refreshMinijuego();
        refreshEstado();
    }

    private void refreshMapa() {
        String[][] mapa = engine.getMapa();
        int pf = engine.getJugador().getFila();
        int pc = engine.getJugador().getColumna();

        for (int f = 0; f < 5; f++) {
            for (int c = 0; c < 5; c++) {
                Label cell = celdas[f][c];
                boolean esJugador = (f == pf && c == pc);
                String bg = colorCelda(mapa[f][c], esJugador);
                String textColor = esJugador ? "#1a1a2e" : "#c9d1d9";
                cell.setStyle(CSS_CELDA_BASE
                        + "-fx-background-color: " + bg + "; "
                        + "-fx-text-fill: " + textColor + ";");
                cell.setText(esJugador ? "☞" : nombreCorto(mapa[f][c]));
            }
        }
    }

    private String colorCelda(String tipo, boolean jugador) {
        if (jugador) return "#e2b96f";
        return switch (tipo) {
            case "LLANURA"                       -> "#1a3020";
            case "CUEVA", "CUEVA_LUZ"            -> "#2a1a3a";
            case "BOSQUE", "BOSQUE_HACHA"        -> "#0d2010";
            case "GRANJA","GRANJA_LLAVE","GRANJA_HACHA" -> "#2a1a00";
            case "ORCOS"                         -> "#3a0a0a";
            case "MAR", "MAR_LLAVE"              -> "#0a1a3a";
            case "BARCO", "BARCO_LISTO"          -> "#1a2a3a";
            case "ARRECIFE"                      -> "#0a0a1a";
            default                              -> "#111";
        };
    }

    private String nombreCorto(String tipo) {
        return switch (tipo) {
            case "LLANURA"                       -> "LLA-\nNURA";
            case "CUEVA", "CUEVA_LUZ"            -> "CUEVA";
            case "BOSQUE", "BOSQUE_HACHA"        -> "BOS-\nQUE";
            case "GRANJA","GRANJA_LLAVE","GRANJA_HACHA" -> "GRAN-\nJA";
            case "ORCOS"                         -> "ORCOS";
            case "MAR", "MAR_LLAVE"              -> "MAR";
            case "BARCO", "BARCO_LISTO"          -> "BARCO";
            case "ARRECIFE"                      -> "ARR.";
            default                              -> "~";
        };
    }

    private void refreshInventario() {
        Jugador j = engine.getJugador();
        aplicarItem(lblLlave,  j.isLlave());
        aplicarItem(lblHacha,  j.isHacha());
        aplicarItem(lblMadera, j.isMadera());
        aplicarItem(lblLuz,    j.isFuentedeluz());
        aplicarItem(lblResina, j.isResina());
    }

    private void aplicarItem(Label l, boolean activo) {
        String color = activo ? "#98c379" : "#444";
        l.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px; -fx-text-fill: " + color + ";");
    }

    private void refreshMinijuego() {
        boolean orco     = engine.isEsperandoOrco();
        boolean acertijo = engine.isEsperandoAcertijo();
        boolean activo   = orco || acertijo;

        panelMinijuego.setVisible(activo);
        panelMinijuego.setManaged(activo);

        if (!activo) return;

        // Referencias directas — sin acceso por índice
        pnlBotonesOrco.setVisible(orco);
        pnlBotonesOrco.setManaged(orco);
        lblAcertijoEnunciado.setVisible(acertijo);
        lblAcertijoEnunciado.setManaged(acertijo);
        pnlAcertijoControles.setVisible(acertijo);
        pnlAcertijoControles.setManaged(acertijo);

        lblMinijuegoTitulo.setText(orco ? "⚔  ¡ESQUIVA LAS FLECHAS!" : "💬  Acertijo del Granjero");

        setMovementEnabled(false);
    }

    private void refreshEstado() {
        GameEngine.GameState gs = engine.getEstado();
        if (gs == GameEngine.GameState.WIN) {
            txtNarracion.setStyle(txtNarracion.getStyle()
                    + "-fx-text-fill: #98c379;");
            setMovementEnabled(false);
        } else if (gs == GameEngine.GameState.LOSE) {
            setMovementEnabled(false);
        } else {
            setMovementEnabled(!engine.isEsperandoOrco() && !engine.isEsperandoAcertijo());
        }
    }

    private void setMovementEnabled(boolean enabled) {
        btnN.setDisable(!enabled);
        btnS.setDisable(!enabled);
        btnE.setDisable(!enabled);
        btnO.setDisable(!enabled);
    }

    // ── Acceso al mensaje pendiente del engine ────────────────────────────
    // (necesario para el inicio)

    public static void main(String[] args) {
        launch(args);
    }
}
