/**
 * The huffman implementation file
 *
 * @author Ahmed Elmi
 * @author Godwin Kangor
 *
 */


import java.io.*;
import java.util.*;
public class Huffmanimplementation implements Huffman {
    public Map<Character,Long> frequencies;
    PriorityQueue<BinaryTree<CodeTreeElement>> priorityQueue;
    Map<Character, String> codes;

    /**
     * Read file provided in pathName and count how many times each character appears
     * @param pathName - path to a file to read
     * @return - Map with a character as a key and the number of times the character appears in the file as value
     * @throws IOException
     */
    @Override
    public Map<Character, Long> countFrequencies(String pathName) throws IOException {
        frequencies = new HashMap<>();
        BufferedReader reader = null;
        // To ensure that certain cleanup actions are performed, whether an exception occurs or not.
        try {
            reader = new BufferedReader(new FileReader(pathName));
            int charasInt;
            while ((charasInt = reader.read()) != -1) { // Read characters until the end of the file
                char character = (char) charasInt;
                frequencies.put(character, frequencies.getOrDefault(character, 0L) + 1);
            }
        } finally {
            // Ensure the reader is closed to free system resources.
            if (reader != null) {
                try {
                    reader.close(); // Try to close the BufferedReader.
                } catch (IOException e) {
                    // If an IOException occurs during closing, it's caught here to avoid crashing the program.
                    System.err.println("Unable to close file. Error: " + e.getMessage());
                }
            }
        }

        return frequencies;
    }

    /**
     * Construct a code tree from a map of frequency counts. Note: this code should handle the special
     * cases of empty files or files with a single character.
     *
     * @param frequencies a map of Characters with their frequency counts from countFrequencies
     * @return the code tree.
     */

    // Method to build a Huffman tree from character frequencies
    @Override
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
        if (frequencies.isEmpty()) return null; // Handling the case of an empty file

        priorityQueue = new PriorityQueue<>(new TreeComparator()); // Priority tree - character, frequency

        for (Character key : frequencies.keySet()) { //
            priorityQueue.add(new BinaryTree<>(new CodeTreeElement(frequencies.get(key), key)));
            // Add initial single character tree to the thePriorityQueue
        }
        if (priorityQueue.size() == 1) {
            // Ensures there are at least two elements in the queue
            priorityQueue.add(new BinaryTree<>(new CodeTreeElement(0l, null)));
        }

        // Combine trees until only one tree is left, which will be the Huffman tree

        while (priorityQueue.size() > 1) {
            BinaryTree<CodeTreeElement> tree1 = priorityQueue.poll();
            BinaryTree<CodeTreeElement> tree2 = priorityQueue.poll();

            // Create a new tree with these two trees as children and their frequencies summed

            long freq = tree1.data.getFrequency() + tree2.data.getFrequency();
            BinaryTree<CodeTreeElement> tree = new BinaryTree<>(new CodeTreeElement(freq, null), tree1, tree2);
            // Add the new tree back into the priority queue
            priorityQueue.add(tree);
        }
        return priorityQueue.poll(); // The final tree represents the Huffman coding tree
    }

    /**
     * Computes the code for all characters in the tree and enters them
     * into a map where the key is a character and the value is the code of 1's and 0's representing
     * that character.
     *
     * @param codeTree the tree for encoding characters produced by makeCodeTree
     * @return the map from characters to codes
     */

    // Method to compute Huffman codes for each character based on the Huffman tree
    @Override
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        Map<Character, String> codes = new HashMap<Character,String>();
        if (codeTree !=  null)
            traverse(codeTree, "", codes);;//helper function

        return codes;
    }

    // Recursive method to traverse the Huffman tree and assign codes to characters
    public static void traverse(BinaryTree<CodeTreeElement> node, String code, Map<Character, String> codes) {
        // If the node is a leaf node, add the mapping for the character to the code
        if (node.isLeaf()) {
            // Base case: If the node is a leaf, assign the current code to the character

            codes.put(node.data.getChar(), code);
        }
        else {
            // If the node had children, traverse its left and right subtrees and append "0" or "1"
            traverse(node.getLeft(), code + "0", codes);
            traverse(node.getRight(), code + "1", codes);
        }
    }
    /**
     * Compress the file pathName and store compressed representation in compressedPathName.
     * @param codeMap - Map of characters to codes produced by computeCodes
     * @param pathName - File to compress
     * @param compressedPathName - Store the compressed data in this file
     * @throws IOException
     */

    @Override
    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(pathName));  // FileReader reads from the file
        BufferedBitWriter writer = new BufferedBitWriter(compressedPathName);   // Writes compressed data to the file
        try {
            int characterFile = reader.read(); // Reads the first character from the file
            while (characterFile != -1) {  // Loops to the end of the file (-1)
                char character = (char) characterFile; // Converts int to a character
                String code = codeMap.get(character); // Get the code for the character from the codeMap
                for (int i = 0; i < code.length(); i++) { // Write the code as bits to the output file
                    writer.writeBit(code.charAt(i) == '1');
                }
                characterFile = reader.read(); // Next character in the file
            }
        }
        catch (IOException e) {
            throw new IOException("An error occurred while compressing file", e);
        }
        finally {

            reader.close();
            writer.close();
        }
    }
    /**
     * Decompress file compressedPathName and store plain text in decompressedPathName.
     * @param compressedPathName - file created by compressFile
     * @param decompressedPathName - store the decompressed text in this file, contents should match the original file before compressFile
     * @param codeTree - Tree mapping compressed data to characters
     * @throws IOException
     */

    @Override
    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {
        if (codeTree == null) return;

        BufferedBitReader reader = new BufferedBitReader(compressedPathName);

        BufferedWriter writer = new BufferedWriter(new FileWriter(decompressedPathName));

        BinaryTree<CodeTreeElement> current = codeTree;

        try{
            while (reader.hasNext()) { // Continue reading until all bits are processed
                boolean bit = reader.readBit(); // Read one bit (true for 1, false for 0)
                if (bit) {
                    current = current.getRight();
                } else {
                    current = current.getLeft();
                }
                if (current.isLeaf()) {
                    writer.write(current.data.getChar());
                    current = codeTree;
                }

            }

        }
        finally {
            writer.close(); // Ensure the writer is closed to flush the output
            reader.close(); // Ensure the reader is closed to release resources
        }

        if(current != codeTree){
            throw new EOFException((" File format is not right. \n"));
        }
    }

    public static void main(String[] args) throws Exception{
        Huffmanimplementation huffman = new Huffmanimplementation();
//        String pathName = "PS3/Onechar";//Single character
//        String pathName = "PS3/Emptyfile";//Empty file
//        String pathName = "PS3/WarAndPeace.txt";//multiple words
//        String pathName = "PS3/USConstitution.txt";
//        String pathName = "PS3/TripleA";//repeating character
          String pathName = "PS3/Hello";


        Map<Character, Long> frequencies = huffman.countFrequencies(pathName);
        System.out.println(frequencies); // countFrequencies test pass.

        BinaryTree<CodeTreeElement> codeTree = huffman.makeCodeTree(frequencies);
        System.out.println(codeTree); // codeTree test pass

        Map<Character, String> codeMap = huffman.computeCodes(codeTree);
        System.out.println(codeMap); // codeMap test pass

        String compressedPathname = pathName.substring(0, pathName.length() -4)+ "_compressed.txt";

        try {
            huffman.compressFile(codeMap, pathName, compressedPathname);
        }
        catch(FileNotFoundException ex){
            System.out.println("Failed to compress the file \n");
            return;
        }
        System.out.println(" successfully compressed. \n");

        String decompressedPathname = pathName.substring(0, pathName.length() -4)+ "_decompressed.txt";

        try{
            huffman.decompressFile(compressedPathname,decompressedPathname, codeTree);}
        catch (FileNotFoundException ex){
            System.out.println("Failed to decompress  file \n");
            return;
        }
        System.out.println(" successfully decompressed. \n");
    }
}





