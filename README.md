# ⚓ Isla Aventura — JavaFX Edition

Juego de aventura de texto con interfaz gráfica JavaFX. Explora una isla 5×5, recoge ítems, resuelve minijuegos y repara tu barco para escapar.

---

## Capturas

> *(Añade screenshots aquí una vez ejecutes el proyecto)*

---

## Tecnologías

- Java 17+
- JavaFX 21
- Maven 3.8+

---

## Cómo ejecutar

### Requisitos previos

- JDK 17 o superior
- Maven 3.8+ ([maven.apache.org](https://maven.apache.org/))

### Ejecutar directamente con Maven

```bash
git clone https://github.com/TU_USUARIO/isla-aventura.git
cd isla-aventura
mvn javafx:run
```

### Generar JAR ejecutable

```bash
mvn package
java -jar target/isla-aventura-1.0.0.jar
```

---

## Estructura del proyecto

```
isla-aventura/
├── src/
│   └── Juego/
│       ├── Jugador.java          # Modelo del jugador (estado + persistencia CSV)
│       ├── GameEngine.java       # Lógica del juego, desacoplada de la UI
│       └── IslaAventuraApp.java  # Interfaz gráfica JavaFX
├── pom.xml
├── .gitignore
└── README.md
```

---

## Cómo se juega

Empiezas en la **Llanura** central de la isla. El objetivo es reparar el **Mono de Mar** (tu barco) y escapar.

### Mapa

```
[ ARR ][ BARCO ][ BARCO ][ BARCO ][ ARR ]
[ MAR ][       ][ CUEVA ][       ][ MAR ]
[ MAR ][ BOSQ  ][ LLAN  ][ GRNJA ][ MAR ]
[ MAR ][       ][ ORCOS ][       ][ MAR ]
[ ARR ][  MAR  ][  MAR  ][  MAR  ][ ARR ]
```

### Ítems y cómo conseguirlos

| Ítem | Dónde |
|---|---|
| 🗝 Llave | Se encuentra en el **Mar** (primera visita) |
| 🪓 Hacha | **Granja** — necesitas la llave y resolver el acertijo del granjero |
| 🪵 Madera | **Bosque** — necesitas el hacha |
| 🔦 Fuente de Luz | **Poblado Orco** — esquiva las flechas |
| 🫙 Resina | **Cueva** — necesitas la fuente de luz |

### Victoria

Llega al **Barco** con Madera + Resina en el inventario.

---

## Decisiones de diseño

- **`GameEngine` desacoplado de la UI**: toda la lógica del juego vive en `GameEngine.java`, sin dependencias de JavaFX. Facilita tests unitarios y cambiar la interfaz sin tocar la lógica.
- **Persistencia en el modelo**: `Jugador` gestiona su propio guardado/carga en CSV. El engine no maneja I/O directamente.
- **Minijuegos integrados en la GUI**: el panel de minijuego aparece y desaparece según contexto, bloqueando los controles de movimiento mientras está activo.

---

## Autor

Néstor Pérez — [LinkedIn](https://linkedin.com/in/TU_PERFIL) · [GitHub](https://github.com/TU_USUARIO)
