package Juego;

import java.util.HashMap;

/**
 * GameEngine contiene toda la lógica del juego desacoplada de la interfaz.
 * La GUI sólo llama a move(Dir) y consulta el estado resultante.
 */
public class GameEngine {

    public enum Dir { NORTE, SUR, ESTE, OESTE }

    public enum GameState { PLAYING, WIN, LOSE }

    private static final String SAVE_FILE = "GuardarPartida.txt";

    private Jugador jugador;
    private final String[][] mapa = new String[5][5];
    private final HashMap<String, String> textos = new HashMap<>();

    // Estado intermedio para minijuegos
    private boolean esperandoOrcoDodge  = false;  // turno de esquivar flechas
    private boolean esperandoAcertijo   = false;  // turno del acertijo
    private GameState estado = GameState.PLAYING;
    private String mensajePendiente = "";          // texto a mostrar en la UI

    public GameEngine() {
        inicializarTextos();
        inicializarMapa();
        cargarOIniciar();
    }

    // ── Inicialización ────────────────────────────────────────────────────

    private void inicializarTextos() {
        textos.put("LLANURA",         "El centro de la isla es apacible, pero aún faltan OBJETOS por encontrar.");
        textos.put("CUEVA",           "Una cueva oscura a los pies de la cordillera. Necesitas una FUENTE DE LUZ para entrar.");
        textos.put("CUEVA_LUZ",       "Con tu fuente de luz exploras la cueva. Encuentras barriles de RESINA. Tomas uno.");
        textos.put("BOSQUE",          "Un bosque tétrico. Necesitas un HACHA para obtener madera.");
        textos.put("BOSQUE_HACHA",    "Talas un árbol y obtienes la MADERA que necesitabas.");
        textos.put("GRANJA",          "Una granja tranquila. Junto a un tocón hay un HACHA con un CANDADO CERRADO.");
        textos.put("GRANJA_LLAVE",    "La llave encaja en el candado. El granjero te propone un acertijo para llevarte el hacha...");
        textos.put("GRANJA_HACHA",    "¡Ya tienes el HACHA! Poco más hay por aquí.");
        textos.put("ORCOS",           "Un claro con un Poblado Orco fortificado. ¡Te disparan flechas en llamas!");
        textos.put("MAR",             "La orilla del mar. Entre la arena encuentras una LLAVE. La coges.");
        textos.put("MAR_LLAVE",       "El mar… qué hermoso mar. Deseas zarpar cuanto antes.");
        textos.put("BARCO",           "El viejo Mono de Mar. Con esa grieta en el casco no puedes zarpar. Necesitas MADERA y RESINA.");
        textos.put("BARCO_LISTO",     "Con la MADERA y la RESINA tapas el agujero. ¡Una aventura termina, otra comienza!\n\n  |------   -----    |\\\\\n  |           |      | \\\\ \n  |------     |      |  \\\\\n  |           |      |   \\\\\n  |         -----    |    \\\\");
        textos.put("VACIO",           "Aquí no hay nada. Mejor ir a otra dirección.");
        textos.put("ARRECIFE",        "El arrecife. Alejarse más sería peligroso.");
    }

    private void inicializarMapa() {
        mapa[0][0] = "ARRECIFE";
        mapa[0][4] = "ARRECIFE";
        mapa[4][0] = "ARRECIFE";
        mapa[4][4] = "ARRECIFE";
        mapa[0][1] = "BARCO";
        mapa[0][2] = "BARCO";
        mapa[0][3] = "BARCO";
        mapa[1][0] = "MAR";
        mapa[2][0] = "MAR";
        mapa[3][0] = "MAR";
        mapa[1][4] = "MAR";
        mapa[2][4] = "MAR";
        mapa[3][4] = "MAR";
        mapa[4][1] = "MAR";
        mapa[4][2] = "MAR";
        mapa[4][3] = "MAR";
        mapa[1][1] = "VACIO";
        mapa[1][3] = "VACIO";
        mapa[3][1] = "VACIO";
        mapa[3][3] = "VACIO";
        mapa[1][2] = "CUEVA";
        mapa[2][1] = "BOSQUE";
        mapa[2][2] = "LLANURA";
        mapa[2][3] = "GRANJA";
        mapa[3][2] = "ORCOS";
    }

    private void cargarOIniciar() {
        Jugador cargado = Jugador.cargar(SAVE_FILE);
        if (cargado != null) {
            jugador = cargado;
            mensajePendiente = "Partida cargada. " + textoActual();
        } else {
            jugador = new Jugador(2, 2, false, false, false, false, false);
            mensajePendiente = textoActual();
        }
        actualizarMapa();
    }

    // ── API pública para la GUI ───────────────────────────────────────────

    /** Mueve al jugador en la dirección indicada y devuelve el texto resultante. */
    public String move(Dir dir) {
        if (estado != GameState.PLAYING) return mensajePendiente;
        if (esperandoOrcoDodge || esperandoAcertijo) return mensajePendiente;

        int f = jugador.getFila();
        int c = jugador.getColumna();

        switch (dir) {
            case NORTE -> f--;
            case SUR   -> f++;
            case ESTE  -> c++;
            case OESTE -> c--;
        }

        // Límites del mapa
        if (f < 0 || f > 4 || c < 0 || c > 4) {
            return "No puedes avanzar más en esa dirección, podría ser peligroso.";
        }

        jugador.setFila(f);
        jugador.setColumna(c);
        actualizarEstado();
        jugador.guardar(SAVE_FILE);
        return mensajePendiente;
    }

    /**
     * Resuelve el minijuego del Poblado Orco.
     * @param esquiva true = jugador esquivó (cualquier dirección cuenta)
     */
    public String resolverOrco(boolean esquiva) {
        esperandoOrcoDodge = false;
        int dado = (int)(Math.random() * 5 + 1);
        if (dado == 3) {
            estado = GameState.LOSE;
            jugador.reset();
            jugador.guardar(SAVE_FILE);
            mensajePendiente = "¡Te han alcanzado las flechas! Has muerto. La partida se reinicia.";
        } else {
            jugador.setFuentedeluz(true);
            mapa[1][2] = "CUEVA_LUZ";
            mensajePendiente = "¡Esquivaste! Tomas una flecha encendida: tienes FUENTE DE LUZ. ¡Huye de aquí!";
        }
        jugador.guardar(SAVE_FILE);
        return mensajePendiente;
    }

    /**
     * Resuelve el acertijo del granjero.
     * @param respuesta número introducido por el jugador
     */
    public String resolverAcertijo(int respuesta) {
        esperandoAcertijo = false;
        if (respuesta == 2) {
            jugador.setHacha(true);
            mapa[2][3] = "GRANJA_HACHA";
            mensajePendiente = "¡Correcto! El granjero te entrega el HACHA.";
        } else {
            mensajePendiente = "Respuesta incorrecta. El granjero se niega. Vuelve cuando quieras intentarlo.";
        }
        jugador.guardar(SAVE_FILE);
        return mensajePendiente;
    }

    /** Reinicia la partida completamente. */
    public String reiniciar() {
        jugador.reset();
        estado = GameState.PLAYING;
        esperandoOrcoDodge = false;
        esperandoAcertijo = false;
        inicializarMapa();
        jugador.guardar(SAVE_FILE);
        mensajePendiente = textoActual();
        return mensajePendiente;
    }

    // ── Lógica interna ────────────────────────────────────────────────────

    private void actualizarEstado() {
        int f = jugador.getFila();
        int c = jugador.getColumna();

        // Recoger llave en el mar (primera visita sin llave)
        if (esMar(f, c) && !jugador.isLlave()) {
            jugador.setLlave(true);
            actualizarMapa();
            mensajePendiente = "Entre la arena del mar encuentras una LLAVE. ¡La coges!\n\n" + textoActual();
            return;
        }

        // Cueva con luz → resina
        if (f == 1 && c == 2 && jugador.isFuentedeluz() && !jugador.isResina()) {
            jugador.setResina(true);
            mapa[1][2] = "CUEVA_LUZ";
            mensajePendiente = textos.get("CUEVA_LUZ") + " Has obtenido RESINA.";
            return;
        }

        // Bosque con hacha → madera
        if (f == 2 && c == 1 && jugador.isHacha() && !jugador.isMadera()) {
            jugador.setMadera(true);
            mapa[2][1] = "BOSQUE_HACHA";
            mensajePendiente = textos.get("BOSQUE_HACHA");
            return;
        }

        // Granja con llave pero sin hacha → acertijo
        if (f == 2 && c == 3 && jugador.isLlave() && !jugador.isHacha()) {
            esperandoAcertijo = true;
            mapa[2][3] = "GRANJA_LLAVE";
            mensajePendiente = textos.get("GRANJA_LLAVE");
            return;
        }

        // Poblado Orco
        if (f == 3 && c == 2) {
            esperandoOrcoDodge = true;
            mensajePendiente = textos.get("ORCOS") + "\n¡Elige dirección para esquivar!";
            return;
        }

        // Victoria: llegar al barco con madera + resina
        if ((f == 0 && (c == 1 || c == 2 || c == 3)) && jugador.isMadera() && jugador.isResina()) {
            estado = GameState.WIN;
            mensajePendiente = textos.get("BARCO_LISTO");
            jugador.reset();
            jugador.guardar(SAVE_FILE);
            return;
        }

        mensajePendiente = textoActual();
    }

    private void actualizarMapa() {
        if (jugador.isLlave()) {
            for (int[] pos : new int[][]{{1,0},{2,0},{3,0},{4,1},{4,2},{4,3},{3,4},{2,4},{1,4}}) {
                mapa[pos[0]][pos[1]] = "MAR_LLAVE";
            }
        }
    }

    private boolean esMar(int f, int c) {
        return (c == 0 && f >= 1 && f <= 3)
            || (f == 4 && c >= 1 && c <= 3)
            || (c == 4 && f >= 1 && f <= 3);
    }

    private String textoActual() {
        String clave = mapa[jugador.getFila()][jugador.getColumna()];
        return textos.getOrDefault(clave, "Un lugar misterioso...");
    }

    // ── Consultas de estado para la GUI ──────────────────────────────────

    public Jugador getJugador()              { return jugador; }
    public String[][] getMapa()              { return mapa; }
    public GameState getEstado()             { return estado; }
    public boolean isEsperandoOrco()         { return esperandoOrcoDodge; }
    public boolean isEsperandoAcertijo()     { return esperandoAcertijo; }
    public String getMensajePendiente()      { return mensajePendiente; }
    public String getNombreCelda(int f, int c) {
        String k = mapa[f][c];
        return switch (k) {
            case "LLANURA"     -> "Llanura";
            case "CUEVA","CUEVA_LUZ" -> "Cueva";
            case "BOSQUE","BOSQUE_HACHA" -> "Bosque";
            case "GRANJA","GRANJA_LLAVE","GRANJA_HACHA" -> "Granja";
            case "ORCOS"       -> "Poblado Orco";
            case "MAR","MAR_LLAVE" -> "Mar";
            case "BARCO","BARCO_LISTO" -> "Barco";
            case "ARRECIFE"    -> "Arrecife";
            default            -> "~";
        };
    }
}
