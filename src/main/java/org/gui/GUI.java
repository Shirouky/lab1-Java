package org.gui;

import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.main.Controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import javax.swing.*;

public class GUI extends JFrame {

    JLabel label = new JLabel("Terver calculator");

    JButton buttonImport = new JButton("Import");
    JButton buttonCalc = new JButton("Calculate");
    JButton buttonExport = new JButton("Export");
    JButton buttonExit = new JButton("Exit");
    JTextField pathField = new JTextField("Path", 20);
    JTextField sheetField = new JTextField("Sheet", 20);
    //    JLabel results = new JLabel("");
    Controller controller;
//    JPanel container = new JPanel();


    public GUI(Controller controller) {
        this.controller = controller;

        GroupLayout layout = new GroupLayout(this.getContentPane());
        setLayout(layout);
        float t = 30;
        label.setFont(label.getFont().deriveFont(t));
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(label)
                                .addComponent(buttonImport)
                                .addComponent(buttonCalc)
                                .addComponent(buttonExport)
                                .addComponent(buttonExit))
                        .addComponent(pathField)
                        .addComponent(sheetField));


        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(label)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(buttonImport)
                                .addComponent(pathField)
                                .addComponent(sheetField))
                        .addComponent(buttonCalc)
                        .addComponent(buttonExport)
                        .addComponent(buttonExit));


        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buttonImport.addActionListener(new ImportEvent());
        buttonCalc.addActionListener(new CalcEvent());
        buttonExport.addActionListener(new ExportEvent());
        buttonExit.addActionListener(new ExitEvent());
        buttonCalc.setEnabled(false);
        buttonExport.setEnabled(false);
    }

    private class ImportEvent implements ActionListener {
        final GUI gui = GUI.this;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                var path = this.gui.pathField.getText();
                var sheet = this.gui.sheetField.getText();
                path = "D:\\МИФИ\\4 семестр\\Теория и технология программирования\\lab1\\Data Samples.xlsx";
                sheet = "Вариант 3";
                this.gui.controller.importData(path, sheet);
                this.gui.buttonCalc.setEnabled(true);
            } catch (ParseException | IOException ex) {
                JOptionPane.showMessageDialog(this.gui, "Something wrong with the file");
            } catch (NullPointerException ex) {
                JOptionPane.showMessageDialog(this.gui, "Please select different sheet");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this.gui, "Wrong number format in file");
            }  catch (OLE2NotOfficeXmlFileException ex) {
                JOptionPane.showMessageDialog(this.gui, "Error");
            }  catch (NotOfficeXmlFileException ex) {
                JOptionPane.showMessageDialog(this.gui, "Error xlm file");
            }  catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this.gui, "Error");
            }
        }
    }

    private class CalcEvent implements ActionListener {
        final GUI gui = GUI.this;

        @Override
        public void actionPerformed(ActionEvent e) {
            var results = this.gui.controller.calculate();
            this.gui.buttonExport.setEnabled(true);
//            var container = new JPanel();
//            this.gui.add(container);
//
//            for (String elem : this.gui.controller.getColumns()) {
//                container.add(new JLabel(elem));
//            }
//
//
//            for (String row : results.keySet()) {
//                container.add(new JLabel(row));
//                for (String key : results.get(row).keySet()) {
//                    container.add(new JLabel(results.get(row).get(key).toString()));
//                }
//            }
        }
    }

    private class ExportEvent implements ActionListener {
        final GUI gui = GUI.this;

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                this.gui.controller.export();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this.gui, "Something wrong with the file");
            }
        }
    }

    private class ExitEvent implements ActionListener {
        final GUI gui = GUI.this;

        @Override
        public void actionPerformed(ActionEvent e) {
            this.gui.dispose();
        }
    }
}
