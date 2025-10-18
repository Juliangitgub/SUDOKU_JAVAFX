# 🎮 Sudoku 6x6

Proyecto del curso **Fundamentos de Programación Orientada a Eventos (POE) - 2025-2**  
**Universidad del Valle**

---

## 📘 Descripción
Aplicación del juego **Sudoku 6x6**, desarrollada en **Java** usando **JavaFX**.  
El objetivo es completar una cuadrícula de 6x6 con números del 1 al 6,  
sin repetir en filas, columnas ni bloques de 2x3.

El proyecto implementa la arquitectura **Modelo - Vista - Controlador (MVC)**  
y cumple con los requisitos del **Miniproyecto #2**.

---

## ⚙️ Funcionalidades

- 🖥️ Interfaz gráfica en JavaFX (FXML + Scene Builder)  
- 🎹 Ingreso de números con teclado (1–6)  
- ✅ Validación en tiempo real de filas, columnas y bloques  
- 🔁 Botón **Restart** para generar un nuevo tablero  
- 💡 Botón **Help** que usa `HintSolver` para sugerir un número válido  
- 🧩 Tableros generados automáticamente con solución válida  

---

## 🧩 Estructura del proyecto
src/com/example/sudoku_express/
├── Controllers/
│ ├── MenuController.java
│ └── SudokuController.java
├── Models/
│ ├── Board.java
│ ├── PuzzleGenerator.java
│ ├── Validator.java
│ ├── HintSolver.java
│ ├── Hint.java
│ └── AlertBox.java
├── Views/
│ ├── MainView.java
│ └── SudokuView.java
└── resources/com/example/sudoku_express/
├── MenuView.fxml
└── SudokuView.fxml


---

## ▶️ Ejecución

1. Abre el proyecto en **IntelliJ IDEA**, **Eclipse** o **VS Code**.  
2. Configura **JavaFX SDK** (versión 21 o superior).  
3. Ejecuta la clase principal (`HelloAplication.java`).  
4. El juego se abrirá en **pantalla completa**.  

---

## 🎯 Controles

| Acción | Descripción |
|--------|--------------|
| 🖱️ Click en celda | Selecciona la celda |
| 🔢 Teclas 1–6 | Ingresa número |
| ⌫ Backspace/Delete | Borra número |
| 🔁 Botón Restart | Nuevo tablero |
| 💡 Botón Help | Muestra una pista válida |

---

## 👥 Autores

- **Julian Meneses**  
- *RAMIREZ ARRIAGA JUAN DAVID*  

**Curso:**- Fundamentos de Programación Orientada a Eventos  
**Semestre:** 2025-2  

---

## 🛠️ Tecnologías

- ☕ Java 17  
- 🎨 JavaFX 21  
- 🧰 Scene Builder  
- 🗂️ Git / GitHub  

---

## 📄 Licencia

Proyecto académico — uso educativo únicamente.


