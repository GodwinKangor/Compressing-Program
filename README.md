# Compressor-Program

## 📖 Overview
This project is a **Huffman Compression Program** implemented in **Java** as part of a CS10 assignment.  
It demonstrates how **Huffman coding** can be used to compress and decompress files efficiently by assigning shorter codes to frequently occurring characters and longer codes to less frequent ones.

---

## 🛠️ Project Structure
```
Compressor-Program/
│
├── Compressed File/ # Stores compressed output files
├── Decompressed file/ # Stores decompressed output files
├── Test Files/ # Input files for testing compression
│
├── BinaryTree.java # Data structure for building Huffman tree
├── BufferedBitReader.java # Utility class for reading bits
├── BufferedBitWriter.java # Utility class for writing bits
├── CodeTreeElement.java # Represents elements in the Huffman code tree
├── Huffman.java # Main Huffman coding algorithm
├── HuffmanImplementation.java# Driver program (main execution)
├── TreeComparator.java # Helper for comparing nodes in priority queue
└── README.md
```
##  How It Works

### 1. Compression
- Reads an input file and counts character frequencies.
- Builds a **Huffman tree** using a priority queue.
- Generates variable-length prefix codes for each character.
- Writes a **compressed bitstream** to the `Compressed File/` directory.

### 2. Decompression
- Reads the compressed bitstream and reconstructs the Huffman tree.
- Traverses the tree to decode each sequence of bits into characters.
- Produces the original file in the `Decompressed file/` directory.


## 💻 Usage

### Compile
```bash
javac *.java
