import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class ScriptUpdaterUI {
    private JFrame frame;
    private JLabel scriptFileLabel;
    private JTextField scriptFileField;
    private JButton browseButton, convertButton;
    private JFileChooser fileChooser;

    public ScriptUpdaterUI() {
        frame = new JFrame("Script Updater");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        scriptFileLabel = new JLabel("Select script file: ");
        scriptFileField = new JTextField();
        scriptFileField.setEditable(false);

        browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    scriptFileField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        convertButton = new JButton("Convert");
        convertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String scriptFileName = scriptFileField.getText();
                if (!scriptFileName.isEmpty()) {
                    scriptUpdater(scriptFileName);
                    JOptionPane.showMessageDialog(frame, "File updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a file!");
                }
            }
        });

        c.gridx = 0;
        c.gridy = 0;
        frame.add(scriptFileLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        frame.add(scriptFileField, c);

        c.gridx = 2;
        c.gridy = 0;
        frame.add(browseButton, c);

        c.gridx = 1;
        c.gridy = 1;
        frame.add(convertButton, c);

        frame.setVisible(true);
    }

    public static void scriptUpdater(String scriptFileName) {
        String transactionName = "";
        String newValue1 = ",\"{VuserID} | Iteration : {iterationID} | TimeStamp : {Timestamp} | Username : {Username} | Error at step : ";
        String line;
        StringBuilder updatedScript = new StringBuilder();

        try {
            File scriptFile = new File(scriptFileName);
            BufferedReader br = new BufferedReader(new FileReader(scriptFile));

            while ((line = br.readLine()) != null) {
                if (line.contains("web_reg_find")) {
                    if (line.contains("Text=")) {
                        String[] parts = line.split("Text=");
                        String textCheck = parts[1].substring(0, parts[1].indexOf(","));
                        line = "web_reg_find(\"Text=" + textCheck + newValue1 + transactionName + "\"" + ",LAST);";
                    }
                }
                if (line.contains("lr_start_transaction")) {
                    String[] parts = line.split("\"");
                    transactionName = parts[1];
                }
                if (line.contains("lr_end_transaction")) {
                    transactionName = "";
                }
                updatedScript.append(line).append("\n");
            }

            br.close();

            FileWriter fw = new FileWriter(scriptFile);
            fw.write(updatedScript.toString());
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ScriptUpdaterUI();
            }
        });
    }
}