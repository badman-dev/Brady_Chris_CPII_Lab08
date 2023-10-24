import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExtractorFrame extends JFrame {
    Map<String, Integer> printMap;
    ArrayList<String> noiseWords;
    ArrayList<String> filteredTextWords;
    File textFile;

    JPanel topPnl;
    JPanel midPnl;
    JPanel botPnl;

    JButton textBtn;

    JLabel fileLbl;
    JTextArea displayArea;
    JScrollPane displayScroll;

    public ExtractorFrame() {
        createTopPanel();
        createMiddlePanel();
        createBottomPanel();

        Toolkit tk=Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();

        setTitle("Extractor");
        setSize(800, 800);
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
                String[] lineWords = data.replaceAll(
                        "[^a-zA-Z\\s]", "").toLowerCase().split(" ");
                for (String word : lineWords) {
                    System.out.println(word);
                    if (!noiseWords.contains(word)) {
                        filteredTextWords.add(word);
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
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files (.txt)", "txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " +
                    chooser.getSelectedFile().getName());
            return chooser.getSelectedFile();
        }
        return null;
    }
}
