import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;

public class ExtractorFrame extends JFrame {
    Map<String, Integer> printMap;
    ArrayList<String> noiseWords;
    ArrayList<String> filteredTextWords;
    File textFile;
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files (.txt)", "txt");

    JPanel topPnl;
    JPanel midPnl;
    JPanel botPnl;

    JButton textBtn;

    JLabel fileLbl;
    JTextArea displayArea;
    JScrollPane displayScroll;

    JButton saveBtn;

    public ExtractorFrame() {
        createTopPanel();
        createMiddlePanel();
        createBottomPanel();

        Toolkit tk=Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();

        setTitle("Extractor");
        setSize(480, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createTopPanel() {
        topPnl = new JPanel();
        textBtn = new JButton("Upload Text File");

        textBtn.addActionListener((ActionEvent ae) -> textFileHandler());

        topPnl.add(textBtn);
        add(topPnl, BorderLayout.NORTH);
    }

    private void createMiddlePanel() {
        midPnl = new JPanel();
        fileLbl = new JLabel();
        displayArea = new JTextArea(30, 40);
        displayScroll = new JScrollPane(displayArea);

        midPnl.add(fileLbl);
        midPnl.add(displayScroll);
        add(midPnl, BorderLayout.CENTER);
    }

    private void createBottomPanel() {
        botPnl = new JPanel();
        saveBtn = new JButton("Save Results");

        saveBtn.addActionListener((ActionEvent ae) -> saveStats());

        botPnl.add(saveBtn);
        add(botPnl, BorderLayout.SOUTH);
    }

    private void printStats() {
        if (textFile == null) {
            return;
        }

        File noiseFile = new File("src/EnglishStopWords.txt");
        noiseWords = new ArrayList<String>();
        try {
            Scanner myReader = new Scanner(noiseFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                noiseWords.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        filteredTextWords = new ArrayList<String>();
        try {
            Scanner myReader = new Scanner(textFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (!data.isEmpty()) {
                    String[] lineWords = data.replaceAll(
                            "[^a-zA-Z\\s]", "").toLowerCase().split(" ");
                    for (String word : lineWords) {
                        System.out.println(word);
                        if (!noiseWords.contains(word)) {
                            filteredTextWords.add(word);
                        }
                    }
                }
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        printMap = filteredTextWords.stream()
                .collect( Collectors.groupingBy( Function.identity(), Collectors.summingInt(e -> 1) ));
        printMap.forEach((key, value) -> displayArea.append(key + ": " + value + "\n"));

    }

    private void textFileHandler() {
        textFile = fileOpener();
        fileLbl.setText(textFile.getName());
        printStats();
    }

    private File fileOpener()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());
            return chooser.getSelectedFile();
        }
        return null;
    }

    private void saveStats() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(filter);
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.toString().endsWith(".txt")) {
                file = new File(file.toString() + ".txt");
            }

            String content = displayArea.getText();
            Scanner in = new Scanner(System.in);
            Path filePath = Paths.get(file.getPath());

            try
            {
                OutputStream out =
                        new BufferedOutputStream(Files.newOutputStream(filePath, CREATE));
                BufferedWriter writer =
                        new BufferedWriter(new OutputStreamWriter(out));
                writer.write(content, 0, content.length());
                writer.close(); // must close the file to seal it and flush buffer
                System.out.println("Data file written!");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
