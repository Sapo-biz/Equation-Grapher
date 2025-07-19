# Function Grapher

A simple Java application still in its BETA Stage to graph mathematical functions instantly. 

## Features
- **Home Page:** Landing page with a button to launch the grapher.
- **Live Graphing:** Enter any equation in terms of `x` (e.g., `sin(x)`, `x^2 + 3*x - 5`). The graph updates instantly as you type.
- **Clear Button:** Instantly clear the equation and the graph.
- **Professional UI:** Built with Java Swing for cross-platform compatibility.

## Usage
1. **Compile:**
   ```sh
   javac Grapher.java
   ```
2. **Run:**
   ```sh
   java Grapher
   ```
3. **How to Use:**
   - On the home page, click "Open Grapher".
   - Enter your equation in the input field (e.g., `x^2`, `sin(x)`, `x^3 - 2*x`).
   - The graph will update immediately.
   - Click "Clear" to reset the input and graph.

## Supported Syntax
- Use `x` as the variable.
- Basic operations: `+`, `-`, `*`, `/`, `^` (for powers)
- Functions: `sin(x)`, `cos(x)`, `tan(x)`, `log(x)`, etc. (JavaScript Math functions)
- Example: `x^2 + 2*x + 1`, `sin(x)`, `x^3 - 4*x`

## Requirements
- Java 8 or higher
- **If you see "JavaScript engine not found. Graphing will not work." in your terminal, your Java version does not support the Nashorn engine by default (Java 15+). In that case, you must use Java 8–14 or add a JavaScript engine dependency.**

## License
Program still in basic form. Feel free to use with permission.