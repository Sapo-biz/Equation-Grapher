// Jason He
// 2025
// Grapher.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;

public class Grapher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePage());
    }
}

class HomePage extends JFrame {
    public HomePage() {
        setTitle("Function Grapher - Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        JButton openGrapher = new JButton("Open Grapher");
        openGrapher.setFont(new Font("Arial", Font.BOLD, 18));
        openGrapher.addActionListener(e -> {
            new GrapherFrame();
            dispose();
        });
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(openGrapher);
        add(panel);
        setVisible(true);
    }
}

class GrapherFrame extends JFrame {
    private JPanel equationsPanel;
    private java.util.List<EquationRow> equationRows = new ArrayList<>();
    private GraphPanel graphPanel;
    private JButton zoomInButton, zoomOutButton, addEqButton, clearButton;
    public GrapherFrame() {
        setTitle("Function Grapher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        equationsPanel = new JPanel();
        equationsPanel.setLayout(new BoxLayout(equationsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(equationsPanel);
        graphPanel = new GraphPanel();
        zoomInButton = new JButton("Zoom In");
        zoomOutButton = new JButton("Zoom Out");
        addEqButton = new JButton("Add Equation");
        clearButton = new JButton("Clear All");
        zoomInButton.addActionListener(e -> graphPanel.zoomIn());
        zoomOutButton.addActionListener(e -> graphPanel.zoomOut());
        addEqButton.addActionListener(e -> addEquationRow(""));
        clearButton.addActionListener(e -> clearAllEquations());
        JPanel topPanel = new JPanel();
        topPanel.add(addEqButton);
        topPanel.add(clearButton);
        topPanel.add(zoomInButton);
        topPanel.add(zoomOutButton);
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.WEST);
        add(graphPanel, BorderLayout.CENTER);
        addEquationRow(""); // Start with one row
        setVisible(true);
    }
    private void addEquationRow(String eq) {
        EquationRow row = new EquationRow(eq, this);
        equationRows.add(row);
        equationsPanel.add(row);
        equationsPanel.revalidate();
        equationsPanel.repaint();
        updateGraphPanel();
    }
    void removeEquationRow(EquationRow row) {
        equationRows.remove(row);
        equationsPanel.remove(row);
        equationsPanel.revalidate();
        equationsPanel.repaint();
        updateGraphPanel();
    }
    void updateGraphPanel() {
        java.util.List<String> eqs = new ArrayList<>();
        for (EquationRow row : equationRows) {
            eqs.add(row.getEquation());
        }
        graphPanel.setEquations(eqs);
    }
    private void clearAllEquations() {
        equationRows.clear();
        equationsPanel.removeAll();
        addEquationRow("");
        updateGraphPanel();
    }
}

class EquationRow extends JPanel {
    private JTextField eqField;
    private JButton removeButton;
    private GrapherFrame parent;
    public EquationRow(String eq, GrapherFrame parent) {
        this.parent = parent;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        eqField = new JTextField(eq, 20);
        eqField.setFont(new Font("Arial", Font.PLAIN, 16));
        removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> parent.removeEquationRow(this));
        eqField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { parent.updateGraphPanel(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { parent.updateGraphPanel(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { parent.updateGraphPanel(); }
        });
        add(new JLabel("y = "));
        add(eqField);
        add(removeButton);
    }
    public String getEquation() {
        return eqField.getText();
    }
}

class GraphPanel extends JPanel {
    private java.util.List<String> equations = new ArrayList<>();
    private ScriptEngine engine;
    private double scale = 40.0; // pixels per unit
    private double xMin = -10, xMax = 10, yMin = -10, yMax = 10;
    private static final Color[] COLORS = {Color.RED, Color.BLUE, Color.GREEN.darker(), Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.PINK, Color.BLACK};
    private Point mousePoint = null;
    private String errorMessage = null;
    public GraphPanel() {
        setBackground(Color.WHITE);
        engine = new ScriptEngineManager().getEngineByName("JavaScript");
        if (engine == null) {
            System.err.println("JavaScript engine not found. Graphing will not work.");
        }
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                repaint();
            }
        });
    }
    public void setEquations(java.util.List<String> eqs) {
        this.equations = eqs;
        errorMessage = null;
        repaint();
    }
    public void zoomIn() {
        double factor = 0.8;
        double xCenter = (xMin + xMax) / 2;
        double yCenter = (yMin + yMax) / 2;
        double xRange = (xMax - xMin) * factor / 2;
        double yRange = (yMax - yMin) * factor / 2;
        xMin = xCenter - xRange;
        xMax = xCenter + xRange;
        yMin = yCenter - yRange;
        yMax = yCenter + yRange;
        repaint();
    }
    public void zoomOut() {
        double factor = 1.25;
        double xCenter = (xMin + xMax) / 2;
        double yCenter = (yMin + yMax) / 2;
        double xRange = (xMax - xMin) * factor / 2;
        double yRange = (yMax - yMin) * factor / 2;
        xMin = xCenter - xRange;
        xMax = xCenter + xRange;
        yMin = yCenter - yRange;
        yMax = yCenter + yRange;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();
        // Debug output
        System.out.println("paintComponent called, width=" + w + ", height=" + h);
        // Draw grid
        g2.setColor(Color.LIGHT_GRAY);
        double xStep = getNiceStep((xMax - xMin) / 10);
        double yStep = getNiceStep((yMax - yMin) / 10);
        for (double x = Math.ceil(xMin / xStep) * xStep; x <= xMax; x += xStep) {
            int px = worldToScreenX(x, w);
            g2.drawLine(px, 0, px, h);
        }
        for (double y = Math.ceil(yMin / yStep) * yStep; y <= yMax; y += yStep) {
            int py = worldToScreenY(y, h);
            g2.drawLine(0, py, w, py);
        }
        // Draw axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        int xAxis = worldToScreenY(0, h);
        int yAxis = worldToScreenX(0, w);
        g2.drawLine(0, xAxis, w, xAxis); // x-axis
        g2.drawLine(yAxis, 0, yAxis, h); // y-axis
        // Draw equations
        errorMessage = null;
        java.util.List<String> eqsToDraw = (equations == null || equations.isEmpty() || (equations.size() == 1 && equations.get(0).trim().isEmpty()))
            ? java.util.Collections.singletonList("y = x") : equations;
        for (int eqIdx = 0; eqIdx < eqsToDraw.size(); eqIdx++) {
            String eq = eqsToDraw.get(eqIdx);
            if (eq == null || eq.trim().isEmpty()) continue;
            String[] parts = eq.split("=");
            if (parts.length != 2) {
                errorMessage = "Equation must be of the form y = ...";
                continue;
            }
            String left = parts[0].trim();
            String expr = parts[1].trim();
            if (!left.equals("y")) {
                errorMessage = "Only equations of the form y = ... are supported.";
                continue;
            }
            if (engine == null) {
                errorMessage = "JavaScript engine not found. Graphing will not work.";
                break;
            }
            Color color = COLORS[eqIdx % COLORS.length];
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2));
            int prevX = 0, prevY = 0;
            boolean first = true;
            for (int px = 0; px < w; px++) {
                double x = screenToWorldX(px, w);
                try {
                    engine.put("x", x);
                    Object result = engine.eval(expr.replaceAll("\\^", "**"));
                    if (result instanceof Number) {
                        double y = ((Number) result).doubleValue();
                        int py = worldToScreenY(y, h);
                        if (!first && Math.abs(py - prevY) < h) {
                            g2.drawLine(prevX, prevY, px, py);
                        }
                        prevX = px;
                        prevY = py;
                        first = false;
                    }
                } catch (ScriptException | ArithmeticException ex) {
                    first = true;
                }
            }
        }
        // Draw mouse coordinates
        if (mousePoint != null) {
            double x = screenToWorldX(mousePoint.x, w);
            double y = screenToWorldY(mousePoint.y, h);
            String coord = String.format("(%.2f, %.2f)", x, y);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString(coord, mousePoint.x + 10, mousePoint.y - 10);
            g2.fillOval(mousePoint.x - 3, mousePoint.y - 3, 6, 6);
        }
        // Draw error message
        if (errorMessage != null) {
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString(errorMessage, 20, 30);
        }
        // Draw bounds
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString(String.format("x:[%.2f, %.2f]", xMin, xMax), 10, h - 25);
        g2.drawString(String.format("y:[%.2f, %.2f]", yMin, yMax), 10, h - 10);
    }
    private int worldToScreenX(double x, int w) {
        return (int) ((x - xMin) / (xMax - xMin) * w);
    }
    private int worldToScreenY(double y, int h) {
        return (int) ((yMax - y) / (yMax - yMin) * h);
    }
    private double screenToWorldX(int px, int w) {
        return xMin + px * (xMax - xMin) / w;
    }
    private double screenToWorldY(int py, int h) {
        return yMax - py * (yMax - yMin) / h;
    }
    private double getNiceStep(double rawStep) {
        double exp = Math.floor(Math.log10(rawStep));
        double base = rawStep / Math.pow(10, exp);
        double niceBase;
        if (base < 1.5) niceBase = 1;
        else if (base < 3) niceBase = 2;
        else if (base < 7) niceBase = 5;
        else niceBase = 10;
        return niceBase * Math.pow(10, exp);
    }
}
