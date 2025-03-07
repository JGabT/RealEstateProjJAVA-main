import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class RealEstateFrame extends JFrame {
    private List<LotComponent> lots = new ArrayList<>();
    private JTextArea displayArea;
    private JPanel controlPanel;
    
    public RealEstateFrame() {
        super("Real Estate Management System");
        setLayout(new BorderLayout());
        
        // Initialize the display area
        displayArea = new JTextArea(20, 50);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(0, 1, 5, 5));
        
        // Add lot button
        JButton addLotButton = new JButton("Add New Lot");
        addLotButton.addActionListener(e -> addNewLot());
        
        // View lots button
        JButton viewLotsButton = new JButton("View All Lots");
        viewLotsButton.addActionListener(e -> displayLots());
        
        // Add decorations button
        JButton addDecorationsButton = new JButton("Add Decorations to Lot");
        addDecorationsButton.addActionListener(e -> addDecorations());
        
        controlPanel.add(addLotButton);
        controlPanel.add(viewLotsButton);
        controlPanel.add(addDecorationsButton);
        
        add(controlPanel, BorderLayout.WEST);
    }
    
    private void addNewLot() {
        try {
            String blockStr = JOptionPane.showInputDialog("Enter Block Number:");
            String lotStr = JOptionPane.showInputDialog("Enter Lot Number:");
            String sizeStr = JOptionPane.showInputDialog("Enter Size (sqm):");
            String priceStr = JOptionPane.showInputDialog("Enter Base Price ($):");
            
            int block = Integer.parseInt(blockStr);
            int lotNumber = Integer.parseInt(lotStr);
            double size = Double.parseDouble(sizeStr);
            double price = Double.parseDouble(priceStr);
            
            Lot newLot = new Lot(block, lotNumber, size, price);
            lots.add(newLot);
            
            displayArea.setText("New lot added successfully!\n" + newLot.getDescription());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayLots() {
        if (lots.isEmpty()) {
            displayArea.setText("No lots available.");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Available Lots:\n\n");
        
        for (int i = 0; i < lots.size(); i++) {
            sb.append(i + 1).append(". ").append(lots.get(i).getDescription()).append("\n");
        }
        
        displayArea.setText(sb.toString());
    }
    
    private void addDecorations() {
        if (lots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No lots available to decorate", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] lotOptions = new String[lots.size()];
        for (int i = 0; i < lots.size(); i++) {
            LotComponent lot = lots.get(i);
            lotOptions[i] = (i + 1) + ". " + getLotBaseDescription(lot);
        }
        
        String selectedLot = (String) JOptionPane.showInputDialog(
            this, "Select a lot to decorate:", "Add Decorations",
            JOptionPane.QUESTION_MESSAGE, null, lotOptions, lotOptions[0]);
        
        if (selectedLot == null) return;
        
        int index = Integer.parseInt(selectedLot.split("\\.")[0]) - 1;
        
        String[] options = {"Landscaping", "Fencing", "Both", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            this, "Select decorations to add:", "Add Decorations",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        
        LotComponent decoratedLot = lots.get(index);
        
        switch (choice) {
            case 0: // Landscaping
                decoratedLot = new LandscapingDecorator(decoratedLot);
                break;
            case 1: // Fencing
                decoratedLot = new FencingDecorator(decoratedLot);
                break;
            case 2: // Both
                decoratedLot = new LandscapingDecorator(new FencingDecorator(decoratedLot));
                break;
            default:
                return;
        }
        
        lots.set(index, decoratedLot);
        displayArea.setText("Decorations added successfully!\n" + decoratedLot.getDescription());
    }
    
    private String getLotBaseDescription(LotComponent lot) {
        // Get the base lot description without decorations
        if (lot instanceof LotDecorator) {
            return getLotBaseDescription(((LotDecorator) lot).getDecoratedLot());
        } else {
            return ((Lot) lot).getId(); 
        }
    }
}