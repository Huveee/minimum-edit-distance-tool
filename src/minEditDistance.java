import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class minEditDistance {

	public static void main(String[] args) throws IOException {
		
		SwingUtilities.invokeLater(() -> createAndShowGUI());
		

	}
	
	public static void createAndShowGUI() {  //The function for uı design
        // Main frame
        JFrame frame = new JFrame("Minimum Edit Distance Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        //Main pannel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("Minimum Edit Distance Tool", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Enterence pannel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel partLabel = new JLabel("Select Part:");
        JComboBox<String> partSelector = new JComboBox<>(new String[]{"Part 1", "Part 2"});
        inputPanel.add(partLabel);
        inputPanel.add(partSelector);

        JLabel word1Label = new JLabel("Enter Word 1:");
        JTextField word1Field = new JTextField();
        inputPanel.add(word1Label);
        inputPanel.add(word1Field);

        JLabel word2Label = new JLabel("Enter Word 2 (Part 2 only):");
        JTextField word2Field = new JTextField();
        word2Field.disable();
        inputPanel.add(word2Label);
        inputPanel.add(word2Field);

        JButton calculateButton = new JButton("Calculate");
        inputPanel.add(calculateButton);

        // The result area to show the result
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
       

        // The place for word2
        partSelector.addActionListener(e -> {
        	word2Field.setEnabled(partSelector.getSelectedItem().equals("Part 2"));
        	frame.revalidate();
        });
        
        

        // Calculate button 
        calculateButton.addActionListener(e -> {
        	
        	long startTime, endTime, totalTime;
        	
            String part = (String) partSelector.getSelectedItem();
            String word1 = word1Field.getText().trim();
            String word2 = word2Field.getText().trim();

            if ("Part 1".equals(part)) {
                if (word1.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a word for Part 1.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    List<String> vocabulary = readFile("vocabulary_tr.txt");
                    StringBuilder result = new StringBuilder("Closest words:\n");
                    
                    startTime = System.nanoTime();
                    
                    findFiveClosest(word1, vocabulary,result);
                    
                    endTime = System.nanoTime();
                    
                    totalTime = endTime - startTime; 
                    
                    result.append("\nExecution Time for Part 1: ").append(totalTime).append(" ns");
                    
                    resultArea.setText(result.toString());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading vocabulary file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if ("Part 2".equals(part)) {
                if (word1.isEmpty() || word2.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both words for Part 2.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                StringBuilder result = new StringBuilder("Transformation Steps:\n");
                
                startTime = System.nanoTime();
                
                int operations = minDistance(word1, word2, 2,result);
                
                endTime = System.nanoTime();
                
                totalTime = endTime - startTime;           
                
                result.append("\nMinimum Edit Distance: ").append(operations);
                
                result.append("\nExecution Time for Part 2: ").append(totalTime).append(" ns");
                resultArea.setText(result.toString());
            }
            
            frame.revalidate();
        });

        // Adding the pannels to the frame
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }
	
	
	public static int minDistance(String word1,String word2,int opNumber,StringBuilder steps) {  //The function for the MED algorithm
		
		final int length1= word1.length(); //The length of the word1
		final int length2= word2.length(); //The length of the word2
		
		int [][] minOp=new int[length2+1][length1+1];  //Initializing the cost matrix
		
		
		minOp[0][0]=0; //Filling the left top corner of minOp matrix with 0
		
		for(int i=1;i<=length1;i++) { //Filling the first row 
			minOp[0][i]=i;
		}
		
		for(int j=1;j<=length2;j++) { //Filling the first column 
			minOp[j][0]=j;
		}
		

		
		for(int i=1; i<=length2; i++) {  //Filling the other rows and columns with cost values
			for(int j=1; j<=length1; j++) {
				if(word1.charAt(j-1)==word2.charAt(i-1)) {
					minOp[i][j]=minOp[i-1][j-1];
				}
				else {
					int topLeft=minOp[i-1][j-1]; //Getting the topLeft value
					int top=minOp[i-1][j];     //Getting the top value
					int left=minOp[i][j-1];    //Getting the left value
					
					//Initializing the cost with minimum of topLeft, top and left values.
					minOp[i][j]=Math.min(topLeft, Math.min(top, left))+1;  		
				}
			}
		}
		
		

		if(opNumber==2) { //Finding the operations needed to transform
			
			boolean isEntered=false; //To control if the value of minOp[i][j] coming from topleft or top

			int holder=0; //The holder for the current value of the j
			
			for(int i=length2; i>=1; i--) {  //Filling the other rows and columns with cost values
				for(int j=length1; j>=1; j--) {
					if (isEntered ) {
						j=holder;
						isEntered=false;
					}
					
					int topLeft=minOp[i-1][j-1]; //Getting the topLeft value
					int top=minOp[i-1][j];     //Getting the top value
					int left=minOp[i][j-1];    //Getting the left value
					
					if(minOp[i][j]==topLeft && word1.charAt(j-1)== word2.charAt(i-1)) { // The values of i and j are going to be decreased
						if(j>=1) {
							isEntered=true;
							holder=j-1;					
						}
						if(i==1) {
							while(holder!=0) {
								steps.append("Delete ").append(word1.charAt(holder - 1)).append("\n");
								holder--;
							}
						}
						else if(j==1 && i>1) {
							while(i!=1) {
								steps.append("Insert ").append(word2.charAt(i - 2)).append("\n");
								i--;
							}
						}
						break;
					}	
					else if(minOp[i][j]== (topLeft+1)) {  // The values of i and j are going to be decreased
						if(j>=1) {
							isEntered=true;
							holder=j-1;					
						}
						steps.append("Substitution ").append(word1.charAt(j - 1)).append(" with ").append(word2.charAt(i - 1)).append("\n");
						if(i==1) {
							while(holder!=0) {
								steps.append("Delete ").append(word1.charAt(holder - 1)).append("\n");
								holder--;
							}
						}
						else if(j==1 && i>1) {
							while(i!=1) {
								steps.append("Insert ").append(word2.charAt(i - 1)).append("\n");
								i--;
							}
						}
						break;
					}
					else if(minOp[i][j]== (top+1)) { // The value of i will be decreased and j remains same
						isEntered=true;
						holder=j;
						steps.append("Insert ").append(word2.charAt(i - 1)).append("\n");
						if(i==1) {
							while(holder!=0) {
								steps.append("Delete ").append(word1.charAt(holder - 1)).append("\n");
								holder--;
							}
						}
						else if(j==1 && i>1) {
							while(i!=1) {
								steps.append("Insert ").append(word2.charAt(i - 1)).append("\n");
								i--;
							}
						}
						break;
					}
					else if(minOp[i][j]== (left+1)) { // The value of j will be decreased
						steps.append("Delete ").append(word1.charAt(j - 1)).append("\n");
						if(i==1) {
							while(holder!=0) {
								steps.append("Delete ").append(word1.charAt(holder - 1)).append("\n");
								holder--;
							}
						}
						else if(j==1 && i>1) {
							while(i!=1) {
								steps.append("Insert ").append(word2.charAt(i - 1)).append("\n");
								i--;
							}
						}
					}

				}
			}
			


		}
		
		return minOp[length2][length1]; //Returning the final cost value from the matrix
		
	}
	
	
	
	public static List<String> readFile(String word) throws IOException {  //The function to read the file
		FileReader fReader= new FileReader("vocabulary_tr.txt");
		String line;
		BufferedReader brReader= new BufferedReader(fReader);
			
		List<String> vocabulary = new ArrayList<>(); //The list for words in file
		
		while((line=brReader.readLine()) != null ) { //Getting the rows of file 
			vocabulary.add(line.trim());	//Add the words to the list					
		}
		
		return vocabulary;
	}
	
	
	//The function to apply MED and find the five closest words
	public static void findFiveClosest(String word, List <String> vocabulary,StringBuilder result){ 
		
		HashMap<String,Integer> minDistanceMap= new HashMap<String,Integer>();
		
		for(String vocabWord:vocabulary) {		
			int numOfOp= minDistance(word, vocabWord, 1,result); //Calculating the number of operations with the minDistance 		
			minDistanceMap.put(vocabWord,numOfOp);  //Putting the words and its number of operations to the map
		}
						
		List<Map.Entry<String, Integer>> entryList = new ArrayList<>(minDistanceMap.entrySet());

        //Sorting according to the values
        entryList.sort(Map.Entry.comparingByValue());
        
        int count = 0; //Counter to print the 5 closest words

        //Print the sorted entries
        for (Map.Entry<String, Integer> entry : entryList) {
        	
        	result.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            count++;
            
            if (count == 5) { // Break the loop after printing 5 element
                break;
            }
            
        }
	}
	
	

}
