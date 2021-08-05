import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

import java.util.*;

public class PercentCalculator {

    private static double cutoff;
    private static int totalTokens;
    private static boolean isToken;

    private static File older;
    private static File newer;

    private static ArrayList<String> excluded;
    private static ArrayList<String> allAddresses;

    private static HashMap<String,Double> olderMap;
    private static HashMap<String,Double> newerMap;

    public PercentCalculator(ArrayList<String> excluded, String old, String newN, double cutoff
    ,int totalTokens, boolean isToken) throws IOException{
        this.excluded = excluded;
        allAddresses = new ArrayList<>(); //KEEP
        older = new File(old); //PASSED NAME
        newer = new File(newN); //PASSED NAME
        this.isToken = isToken;
        this.cutoff = cutoff;//PASSED VALUE
        this.totalTokens = totalTokens;//passed value


        if(isToken) {
            olderMap = getMaps(older);
            newerMap = getMaps(newer);
        } else{
            olderMap = getMapsNFT(older);
            newerMap = getMapsNFT(newer);
        }
    }

    public static HashMap<String,Double> getMaps(File current) throws IOException {
        Scanner scan = new Scanner(current);
        HashMap<String,Double> currentMap = new HashMap<>();
        scan.nextLine();
        while(scan.hasNextLine()){
            String currentLine = scan.nextLine();
            Scanner lineScan = new Scanner(currentLine);
            lineScan.useDelimiter(",");
            String address = lineScan.next();
            if(!allAddresses.contains(address)) {
                allAddresses.add(address.substring(1,address.length()-1));
            }
            address = address.substring(1,address.length()-1);
            double value = getValue(lineScan.next());
            if(!excluded.contains(address)){
                currentMap.put(address,value);
            }
        }
        return currentMap;
    }

    public static HashMap<String,Double> getMapsNFT(File current) throws IOException {
        Scanner scan = new Scanner(current);
        HashMap<String,Double> currentMap = new HashMap<>();
        while(scan.hasNextLine()){
            String currentLine = scan.nextLine();
            Scanner lineScan = new Scanner(currentLine);
            lineScan.useDelimiter(",");
                lineScan.next();
                lineScan.next();
                lineScan.next();
                double value = Double.valueOf(lineScan.next());
                String address = lineScan.next();
                if (!allAddresses.contains(address)) {
                    allAddresses.add(address);
                }
                if (!excluded.contains(address)) {
                    currentMap.put(address, value);
                }
        }
        return currentMap;
    }

    private static double getValue (String toDouble){
        return Double.valueOf(toDouble.substring(1,toDouble.length()-1));
    }

    private static double totalHeld (){
        double total = 0;
        Set<String> adresses = olderMap.keySet();
        Iterator<String> adIt = adresses.iterator();
        for(int i = 0; i < adresses.size(); i++){
            String address = adIt.next();
            if(newerMap.containsKey(address) && olderMap.get(address) >= cutoff){
                if(olderMap.get(address) <= newerMap.get(address)) {
                    total += olderMap.get(address);
                }
            }
        }
        if(total == 0){
            throw new IllegalStateException("map of older spreadsheet has no valid values");
        }
        return total;
    }

    public static HashMap<String,Double> findValidHolders(){
        double numTokensHeld = totalHeld();
        HashMap<String,Double> tempFinalValues = new HashMap<>();
        for(String address: olderMap.keySet()){
            if(newerMap.containsKey(address) && olderMap.get(address) >= cutoff){
                if(olderMap.get(address) <= newerMap.get(address)){
                    double valueToGive = ((olderMap.get(address) / numTokensHeld) * totalTokens);
                    tempFinalValues.put(address,valueToGive);
                }
            }
        }
        return tempFinalValues;
    }

    public static void allValues(HashMap<String, Double> rewarded) throws IOException{
        PrintStream allOutput = new PrintStream(new File("all_output.csv"));
        System.setOut(allOutput);
        System.out.print("\"Holder Address\",\"Old Balance\",\"New Balance\",\"Status\"\n");
        for(String address : allAddresses){
            double oldVal;
            double newVal;
            if(olderMap.containsKey(address)){
                oldVal = olderMap.get(address);
            } else {oldVal = 0;}
            if(newerMap.containsKey(address)) {
                newVal = newerMap.get(address);
            } else{newVal = 0;}
            System.out.print(addQuotes(address));
            System.out.print(",");
            System.out.print(addQuotes(""+oldVal));
            System.out.print(",");
            System.out.print(addQuotes(""+newVal));
            System.out.print(",");
            if (excluded.contains(address)) {
                System.out.println(addQuotes("Not Rewarded: On excluded list"));
            } else if(rewarded.containsKey(address)){
                System.out.println("\"Rewarded\"");
            } else if(oldVal < cutoff){
                System.out.println(addQuotes("Not Rewarded: Did not meet reward cutoff"));
            } else if(oldVal > newVal){
                System.out.println(addQuotes("Not Rewarded: Fewer tokens in wallet"));
            } else if(newVal == 0){
                System.out.println(addQuotes("Not Rewarded: Address not found in new file"));
            } else if(oldVal == 0){
                System.out.println(addQuotes("Not Rewarded: Address not found in old file"));
            }
        }
    }


    private static String addQuotes (String s){
        s = "\"" + s + "\"";
        return s;
    }

    public static void main (String[] Args) throws IOException {
        GUI test1 = new GUI();
    }


}

class GUI implements ActionListener, MouseListener {

    String oldFileName;
    String newFileName;
    double cutoff;
    int tokens;
    boolean isToken;
    ArrayList<String> excluded = new ArrayList<>();

    JFrame frame;
    JPanel panel;
    JLabel oldFile;
    JLabel newFile;
    JLabel cutoffL;
    JLabel excludeL;
    JLabel added;
    JLabel tokensL;
    JLabel safteyTokens;
    JLabel status;
    JLabel version;
    JLabel format;
    JTextField formatTF;
    JTextField tokensTF;
    JTextField oldText;
    JTextField newText;
    JTextField cutoffTF;
    JTextField excludeTF;
    JButton submit;
    JButton exclude;


    public GUI() throws IOException {

        frame = new JFrame();


        panel = new JPanel();
        frame.setSize(500,350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setTitle("Airdrop Calculator");

        panel.setLayout(null);

        oldFile = new JLabel("Name of Older File");
        oldFile.setBounds(10,20,120,25);
        panel.add(oldFile);

        oldText = new JTextField(20);
        oldText.setBounds(150,20,165,25);
        panel.add(oldText);

        newFile = new JLabel("Name of Newer File");
        newFile.setBounds(10,50,120,25);
        panel.add(newFile);

        newText = new JTextField(20);
        newText.setBounds(150,50,165,25);
        panel.add(newText);

        cutoffL = new JLabel("Min Tokens for Consideration");
        cutoffL.setBounds(10,80,180,25);
        panel.add(cutoffL);

        cutoffTF = new JTextField(15);
        cutoffTF.setBounds(210,80,105,25);
        panel.add(cutoffTF);

        tokensL = new JLabel("Number of Tokens to Drop");
        tokensL.setBounds(10,110,150,25);
        panel.add(tokensL);

        tokensTF = new JTextField(20);
        tokensTF.setBounds(210,110,105,25);
        panel.add(tokensTF);

        format = new JLabel("Format (Token or NFT)");
        format.setBounds(10,140,150,25);
        panel.add(format);

        formatTF = new JTextField(20);
        formatTF.setBounds(210,140,105,25);
        panel.add(formatTF);

        excludeL = new JLabel("Wallet to Exclude");
        excludeL.setBounds(10,230,120,25);
        panel.add(excludeL);

        excludeTF = new JTextField(60);
        excludeTF.setBounds(130,230,320,25);
        panel.add(excludeTF);

        exclude = new JButton("Exclude");
        exclude.setBounds(10,260,80,25);
        exclude.addMouseListener(this);
        panel.add(exclude);

        added = new JLabel("");
        added.setBounds(110,230,250,25);
        panel.add(added);

        version = new JLabel("Version 1.2.1");
        version.setBounds(400,280,250,25);
        panel.add(version);

        safteyTokens = new JLabel("");
        safteyTokens.setBounds(110,170,220,25);
        panel.add(safteyTokens);

        submit = new JButton("Submit");
        submit.setBounds(10,170,80,25);
        submit.addActionListener(this);
        submit.addMouseListener(this);
        panel.add(submit);

        status = new JLabel("");
        status.setBounds(10,200,300,25);
        panel.add(status);


        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        oldFileName = oldText.getText();
        newFileName = newText.getText();
        String forma = formatTF.getText();
        cutoff = Double.valueOf(cutoffTF.getText());
        tokens = Integer.valueOf(tokensTF.getText());
        if(cutoff < 0){
            status.setText("Min Tokens Must Be >= 0");
        } else if(tokens < 0){
            status.setText("Must Drop at Least 1 Token");
        } else if(!forma.toLowerCase().equals("token") && !forma.toLowerCase().equals("nft")){
            status.setText("Please Select a Valid Format");
        }
        else {
            if(forma.toLowerCase().equals("token")){
                isToken = true;
            } else{
                isToken = false;
            }
            try {
                status.setText("File(s) not Found or Improperly Formatted");
                pc();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }


    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String temp = excludeTF.getText();
        if(excluded.contains(temp)){
            added.setText("Address Already Excluded");
        }else if(temp.length() == 42 && temp.startsWith("0x")){
            excluded.add(temp);
            added.setText("Added to Excluded List Successfully");
        }else if(temp.length() == 0) {
        }else{
            added.setText("Invalid Entry, not Added");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(tokensTF.getText().length() > 0) {
            safteyTokens.setText("Approx " + numToWords(tokensTF.getText()) + " Tokens.");
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        safteyTokens.setText("");
    }

    public void pc() throws IOException {
        PercentCalculator test = new PercentCalculator(excluded,oldFileName,newFileName,cutoff,tokens,isToken);
        HashMap<String,Double> testMap = test.findValidHolders();

        PrintStream testPrint = new PrintStream(new File("Rewards.txt"));
        System.setOut(testPrint);

        for(String temp : testMap.keySet()){
            System.out.print(temp);
            System.out.print(",");
            System.out.printf("%.5f",testMap.get(temp));
            System.out.println();
        }
        test.allValues(testMap);
        status.setText("Rewards Spreadsheet Generated Successfully");
    }

    public String numToWords(String input){
        if(input.length() == 0){
            return "";
        }
        StringBuilder wordNum = new StringBuilder();
        int firstToAdd = (input.length() % 3);
        if(firstToAdd == 0){
            firstToAdd += 3;
        }
        wordNum.append(input, 0, firstToAdd);
        int l = input.length();
        if(l <= 3){
        } else if(l <= 6){
            wordNum.append(" Thousand");
        } else if(l <= 9){
            wordNum.append(" Million");
        } else if(l <= 12){
            wordNum.append(" Billion");
        } else if(l <= 15){
            wordNum.append(" Trillion");
        } else if(l <= 18){
            wordNum.append(" Quadrillion");
        } else if(l <= 21){
            wordNum.append(" Quintillion");
        } else {
            wordNum.append(" Above Quintillions");
        }
        return wordNum.toString();
    }
}
