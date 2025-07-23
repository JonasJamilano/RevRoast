import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Represents a single item in the cart
class CartItem {
    String name;
    double price;
    int quantity;

    CartItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}

// Shared cart logic and data
class CartData {
    static List<CartItem> items = new ArrayList<>();

    // Adds a product to the cart
    static void addItem(String name, double price) {
        for (CartItem item : items) {
            if (item.name.equals(name)) {
                item.quantity++;
                return;
            }
        }
        items.add(new CartItem(name, price, 1));
    }

    // Clears the cart
    static void clear() {
        items.clear();
    }
}

// The Cart GUI window
public class cart extends JFrame {
    private JTextArea cartContents;

    public cart() {
        setTitle("🛒 Your Pit Stop Cart");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cartContents = new JTextArea();
        cartContents.setEditable(false);
        cartContents.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(cartContents);

        JButton clearButton = new JButton("Clear Cart");
        clearButton.addActionListener(e -> {
            CartData.clear();
            updateCartDisplay();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateCartDisplay();
        setVisible(true);
    }

    private void updateCartDisplay() {
        StringBuilder sb = new StringBuilder();

        if (CartData.items.isEmpty()) {
            sb.append("🏎️ Your cart is currently empty.\n");
        } else {
            double total = 0;
            for (CartItem item : CartData.items) {
                double subtotal = item.price * item.quantity;
                sb.append(item.quantity).append("x ").append(item.name)
                        .append(" - ₱").append(String.format("%.2f", subtotal))
                        .append("\n");
                total += subtotal;
            }
            sb.append("\n🏁 Total: ₱").append(String.format("%.2f", total));
        }

        cartContents.setText(sb.toString());
    }

    // Example usage (main method for testing)
    public static void main(String[] args) {
        // Simulate adding items from a menu
        CartData.addItem("Rev Brew", 150.0);
        CartData.addItem("Turbo Espresso", 120.0);
        CartData.addItem("Turbo Espresso", 120.0); // add again for quantity demo
        CartData.addItem("Pit Stop Muffin", 90.0);

        new cart(); // show cart
    }
}
