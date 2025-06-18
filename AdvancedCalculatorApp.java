import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class AdvancedCalculatorApp extends JFrame implements ActionListener {
    private JTextField display;
    private StringBuilder expression = new StringBuilder();

    private final String[] buttonLabels = {
        "CE", "+/-", "%", "/",
        "7", "8", "9", "*",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", ".", "=",
    };

    public AdvancedCalculatorApp() {
        setTitle("Advanced Calculator");
        setSize(350, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 20, 30));

        display = new JTextField("0");
        display.setFont(new Font("SansSerif", Font.BOLD, 32));
        display.setEditable(false);
        display.setBackground(new Color(20, 20, 30));
        display.setForeground(Color.WHITE);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBackground(new Color(20, 20, 30));
        for (String label : buttonLabels) {
            JButton btn = createButton(label);
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createButton(String label) {
        JButton btn = new JButton(label);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 20));
        btn.setForeground(Color.BLACK);

        if ("CE +/- % / * - + =".contains(label)) {
            btn.setBackground(new Color(135, 206, 235)); // light blue
        } else {
            btn.setBackground(new Color(40, 40, 50)); // dark buttons
            btn.setForeground(Color.WHITE);
        }

        btn.addActionListener(this);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "CE":
                expression.setLength(0);
                display.setText("0");
                break;
            case "+/-":
                try {
                    double val = Double.parseDouble(display.getText());
                    val *= -1;
                    display.setText(formatResult(val));
                    expression.setLength(0);
                    expression.append(display.getText());
                } catch (NumberFormatException ignored) {}
                break;
            case "%":
                try {
                    double val = Double.parseDouble(display.getText());
                    val /= 100;
                    display.setText(formatResult(val));
                    expression.setLength(0);
                    expression.append(display.getText());
                } catch (NumberFormatException ignored) {}
                break;
            case "=":
                try {
                    if (expression.length() == 0) break;

                    double result = evaluate(expression.toString());
                    display.setText(formatResult(result));
                    expression.setLength(0);
                    expression.append(display.getText());
                } catch (Exception ex) {
                    display.setText("Error");
                    expression.setLength(0);
                }
                break;
            default:
                if (!expression.isEmpty() && isOperator(cmd.charAt(0)) && isOperator(expression.charAt(expression.length() - 1))) {
                    // Replace last operator with new one
                    expression.setCharAt(expression.length() - 1, cmd.charAt(0));
                } else {
                    expression.append(cmd);
                }
                display.setText(expression.toString());
        }
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.valueOf(result);
        }
    }

    private double evaluate(String expr) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operations = new Stack<>();
        int i = 0;
        while (i < expr.length()) {
            char ch = expr.charAt(i);
            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    num.append(expr.charAt(i++));
                }
                numbers.push(Double.parseDouble(num.toString()));
                continue;
            }
            while (!operations.isEmpty() && precedence(ch) <= precedence(operations.peek())) {
                double b = numbers.pop();
                double a = numbers.pop();
                char op = operations.pop();
                numbers.push(apply(a, b, op));
            }
            operations.push(ch);
            i++;
        }
        while (!operations.isEmpty()) {
            double b = numbers.pop();
            double a = numbers.pop();
            char op = operations.pop();
            numbers.push(apply(a, b, op));
        }
        return numbers.pop();
    }

    private int precedence(char op) {
        return (op == '+' || op == '-') ? 1 : 2;
    }

    private double apply(double a, double b, char op) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> b != 0 ? a / b : Double.NaN;
            default -> 0;
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdvancedCalculatorApp::new);
    }
}
