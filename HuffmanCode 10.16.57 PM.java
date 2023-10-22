import java.util.*;
import java.io.*;

// Uyen Tran
// CSE143 AU21 - TA: Joe Spaniac
// 12/10/2021
// Final Project: Huffman Coding

// This class applies the Huffman algorithm to compress a text file,
// where each letter/character will have its own string representation of "0s"
// and "1s" instead of the original 8-bit "0s" and "1s" representation
// Letters that have a smaller count will have a shorter representation and vice versa
public class HuffmanCode {

    private Queue<HuffmanNode> lettersTree;
    private HuffmanNode overallRoot;
    
    // Constructs a new HuffmanCode which the computer will create its own characters/letters structure.
    // The structure will be written based on the count of each character, where any 
    // character that has the same count will have the same length of string representation of "0s" and "1"
    // Characters that have a smaller count will have a shorter representation and vice versa
    public HuffmanCode(int[] frequencies) {
        lettersTree = new PriorityQueue<>();
        
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] != 0) {
                HuffmanNode curr = new HuffmanNode((char) i, frequencies[i]);
                lettersTree.add(curr);
            }
        }
        
        while (lettersTree.size() > 1) {
            HuffmanNode first = lettersTree.remove();
            HuffmanNode second = lettersTree.remove();
            HuffmanNode total = new HuffmanNode('\0', first.frequency + second.frequency, first, second);
            lettersTree.add(total);
        }
        
        overallRoot = lettersTree.peek();
    }
    
    // Creates the computer's own characters structure from a file
    // The file is legal, not null and in standard format, which contains 
    // pairs of lines of a character's ascii value and its "0s" and "1s" string representation 
    // Parameters:
    //            Scanner input: the scanner that is linked to a file and 
    //                           reads the file
    public HuffmanCode(Scanner input) {
        overallRoot = new HuffmanNode();
        
        while (input.hasNextLine()) {
            HuffmanNode curr = overallRoot;
            int asciiValue = Integer.parseInt(input.nextLine());
            String code = input.nextLine();
             
            for (int i = 0; i < code.length() - 1; i++) {
                if (code.charAt(i) == '0') {
                    if (curr.left == null) {
                        curr.left = new HuffmanNode();
                    }
                    curr = curr.left;
                } else {
                    if (curr.right == null) {
                        curr.right = new HuffmanNode();
                    }
                    curr = curr.right;
                }
            }
             
            if (code.charAt(code.length() - 1) == '0') {
                curr.left = new HuffmanNode((char) asciiValue);
            } else {
                curr.right = new HuffmanNode((char) asciiValue);
            }
        }
    }
    
    // Prints out the characters of a text file and its own "0s" and "1s" string 
    // representations used by the computer to another file
    // Each character will come in pairs of lines with its string representation respectively
    // Parameters:
    //            PrintStream output: the printstream that prints out to a file
    public void save(PrintStream output) {
        save(output, overallRoot, "");
    }

    // Keeps going through the left and rigth branches of the characters tree until 
    // it hits a leaf node, then prints out pairs of lines of the ascii value of that character and 
    // its own string representation of "0s" and "1s" starting from the beginning of the tree.
    // Prints out "0" if going left, "1" if going right
    // Parameters:
    //            PrintStream output: the printstream that prints out to a file
    //            HuffmanNode curr: the current node in the whole tree
    //            String codeRep: the string representation of each character, 
    //                            will be updated as we go left or right of the tree
    private void save(PrintStream output, HuffmanNode curr, String codeRep) {
        if (curr != null) {
            if (curr.left == null && curr.right == null) {
                output.println((int) (curr.data));
                output.println(codeRep);
            } else {
                save(output, curr.left, codeRep + '0');
                save(output, curr.right, codeRep + '1');
            }
        }
    }
    
    // Writes out the actual text message from a file of strings of "0s" and "1s" 
    // based on the computer's characters structure
    // Parameters:
    //            BitInputStream input: links to a legal encoded file of "0s" and "1s" strings
    //            PrintStream output: the printstream that prints out to a file
    public void translate(BitInputStream input, PrintStream output) {
        HuffmanNode curr = overallRoot;
        
        while (input.hasNextBit()) {
            int oneBit = input.nextBit();

            if (oneBit == 0) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }

            if (curr.left == null && curr.right == null) {
                output.write((int) (curr.data));
                curr = overallRoot;
            }
        }
    }
    
    // The HuffmanNode is a class that is used to store a character and its count/frequency by the computer.
    // Initially left and right branch is set to null but it will be updated 
    // as the computer creates the tree based on the given information from a 
    // file of ascii values and its "0s" and "1s" string or an array of counts of each character
    // If "0", the tree travels left; right if it's "1"
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        public char data;            // the given character
        public HuffmanNode left;     // the left branch; or the "0" branch
        public HuffmanNode right;    // the right branch; or the "1" branch
        public int frequency;        // the frequency, or the count of a character
        
        // Constructs a HuffmanNode with a given character, count/frequency, 
        // left/"0", and right/"1" branch
        // Parameters:
        //            char data: the given character
        //            int frequency: the given count/frequency
        //            HuffmanNode left: the given "0"/left branch
        //            HuffmanNode right: the given "1"/right branch
        public HuffmanNode(char data, int frequency, HuffmanNode left, HuffmanNode right) {
            this.data = data;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        // Counstruct a node with a default "null" chracter, a frequency of 0 and "null" left and right branches
        public HuffmanNode() {
            this('\0', 0, null, null);
        }   

        // Constructs a node with the given character and a default frequency of 0, "null" left and right branches
        public HuffmanNode(char data) {
            this(data, 0, null, null);
        }

        // Counstruct a node with a given data, a given frequecy/count, and "null" left & right branches
        public HuffmanNode(char data, int frequency) {
            this(data, frequency, null, null);
        }     

        // Returns -1 if this HuffmanNode has a smaller count/frequency compared to the other node
        // Returns 0 if this HuffmanNode has the same count/frequency compared to the other node
        // Returns 1 if this HuffmanNode has a larger count/frequency compared to the other node
        public int compareTo(HuffmanNode other) {
            return this.frequency - other.frequency;
        }   
    }
}
