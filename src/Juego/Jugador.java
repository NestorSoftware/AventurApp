package Juego;

import java.io.*;

public class Jugador {

    private int fila;
    private int columna;
    private boolean llave;
    private boolean hacha;
    private boolean madera;
    private boolean fuentedeluz;
    private boolean resina;

    public Jugador(int fila, int columna, boolean llave, boolean hacha,
                   boolean madera, boolean fuentedeluz, boolean resina) {
        this.fila = fila;
        this.columna = columna;
        this.llave = llave;
        this.hacha = hacha;
        this.madera = madera;
        this.fuentedeluz = fuentedeluz;
        this.resina = resina;
    }

    /** Resetea el jugador a estado inicial (posición central, sin ítems). */
    public void reset() {
        this.fila = 2;
        this.columna = 2;
        this.llave = false;
        this.hacha = false;
        this.madera = false;
        this.fuentedeluz = false;
        this.resina = false;
    }

    /** Guarda el estado en archivo CSV de una línea. */
    public void guardar(String ruta) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(ruta))) {
            w.write(this.toString());
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    /** Carga el estado desde archivo CSV. Devuelve null si no existe o falla. */
    public static Jugador cargar(String ruta) {
        try (BufferedReader r = new BufferedReader(new FileReader(ruta))) {
            String linea = r.readLine();
            if (linea == null || linea.isBlank()) return null;
            String[] d = linea.split(",");
            return new Jugador(
                Integer.parseInt(d[0]),
                Integer.parseInt(d[1]),
                Boolean.parseBoolean(d[2]),
                Boolean.parseBoolean(d[3]),
                Boolean.parseBoolean(d[4]),
                Boolean.parseBoolean(d[5]),
                Boolean.parseBoolean(d[6])
            );
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return fila + "," + columna + "," + llave + "," + hacha + ","
                + madera + "," + fuentedeluz + "," + resina;
    }

    // ── Getters / Setters ──────────────────────────────────────────────────

    public int getFila()                        { return fila; }
    public void setFila(int fila)               { this.fila = fila; }
    public int getColumna()                     { return columna; }
    public void setColumna(int columna)         { this.columna = columna; }
    public boolean isLlave()                    { return llave; }
    public void setLlave(boolean llave)         { this.llave = llave; }
    public boolean isHacha()                    { return hacha; }
    public void setHacha(boolean hacha)         { this.hacha = hacha; }
    public boolean isMadera()                   { return madera; }
    public void setMadera(boolean madera)       { this.madera = madera; }
    public boolean isFuentedeluz()              { return fuentedeluz; }
    public void setFuentedeluz(boolean f)       { this.fuentedeluz = f; }
    public boolean isResina()                   { return resina; }
    public void setResina(boolean resina)       { this.resina = resina; }

    public void masFila()     { fila++; }
    public void menosFila()   { fila--; }
    public void masColumna()  { columna++; }
    public void menosColumna(){ columna--; }
}
