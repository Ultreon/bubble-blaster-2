package dev.ultreon.bubbles.premain;

import org.lwjgl.system.Platform;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorWindow extends JFrame {

    public ErrorWindow(Throwable e) {
        super("Knot Launch Error");

        if (Platform.get() == Platform.LINUX) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } catch (Exception ex) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                         InstantiationException exc) {
                    throw new RuntimeException(exc);
                }
            }
        } else try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                 InstantiationException exc) {
            throw new RuntimeException(exc);
        }
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setLayout(new java.awt.GridBagLayout());
        var gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        var labelPanel = new JPanel();
        labelPanel.setLayout(new java.awt.FlowLayout(FlowLayout.CENTER, 10, 10));
        labelPanel.add(new JLabel(e.getClass().getSimpleName() + ": " + e.getMessage(), UIManager.getIcon("OptionPane.errorIcon"), JLabel.CENTER));
        this.add(labelPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane();
        var comp = new JTextArea();
        var sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        comp.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        comp.setAutoscrolls(true);
        comp.setText(sw.toString().replace("\t", "    "));
        comp.setEditable(false);
        comp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        scrollPane.setViewportView(comp);
        this.add(scrollPane, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.ipadx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        var separator = new JSeparator(JSeparator.HORIZONTAL);
        this.add(separator, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        var buttonPanel = new JPanel();
        buttonPanel.setLayout(new java.awt.FlowLayout(FlowLayout.RIGHT, 5, 5));
        var button = new JButton("OK");
        button.setPreferredSize(new Dimension(80, 30));
        button.addActionListener(e1 -> this.dispose());
        buttonPanel.add(button);
        this.add(buttonPanel, gbc);

        this.setLocationRelativeTo(null);

        this.setVisible(true);
        this.setSize(800, 450);
        this.setMaximumSize(new Dimension(800, 450));

        this.setResizable(false);
        this.setAlwaysOnTop(true);
    }
}
