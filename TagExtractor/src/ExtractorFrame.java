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
    File noiseFile;

    JPanel topPnl;
    JPanel midPnl;
    JPanel botPnl;

    JLabel textLbl;
    JLabel noiseLbl;
    JButton textBtn;
    JButton noiseBtn;
    JButton printBtn;

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
        textLbl = new JLabel();
        noiseLbl = new JLabel();
        textBtn = new JButton("Upload Text File");
        noiseBtn = new JButton("Upload Noise File");
        printBtn = new JButton("Print");

        textBtn.addActionListener((ActionEvent ae) -> textFileHandler());
        noiseBtn.addActionListener((ActionEvent ae) -> noiseFileHandler());
        printBtn.addActionListener((ActionEvent ae) -> printStats());

        topPnl.add(textLbl);
        topPnl.add(noiseLbl);
        topPnl.add(textBtn);
        topPnl.add(noiseBtn);
        topPnl.add(printBtn);
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

        noiseFile = new File("src/EnglishStopWords.txt");
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
        textLbl.setText(textFile.getName());
    }

    private void noiseFileHandler() {
        noiseFile = fileOpener();
        noiseLbl.setText(noiseFile.getName());
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
