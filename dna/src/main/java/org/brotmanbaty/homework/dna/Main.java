package org.brotmanbaty.homework.dna;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Problem 1: DNA sequence conversion
 * 
 * DNA is a long molecule that lies inside the nucleus of a cell, and it can be thought of as a very long string
 * consisting of characters in the alphabet {A, C, G, T}. DNA sequencing is the technology that enables reading from DNA
 * molecules and converting them to strings on the output. We are interested in a technology that works in the following
 * way: the DNA molecules in the input are fragmented into pieces of equal length L; each piece is then sequenced by the
 * technology, and its content is encoded in the output. The particular encoding used in the output is the following:
 * 
 * • The file contains multiple consecutive entries, one per piece.<br>
 * • Each piece is represented by L consecutive bytes (1 byte = 8 bits).<br>
 * • The first two (most significant) bits of each byte encode the DNA letter:<br>
 * Encoding Base (DNA letter)<br>
 * 00 A<br>
 * 01 C<br>
 * 10 G<br>
 * 11 T<br>
 * • The last six (least significant) bits of each byte encode the confidence that the readout was correct, also known
 * as the quality score. It is represented as an unsigned 6-bit integer in the range 0 to 63.<br>
 * 
 * Write a program that takes as input an encoded file as well as the number L, and converts it to a text file of the
 * following format (known as the FASTQ format):<br>
 * • Each piece is represented by four lines:<br>
 * o The first line contains the word @READ_ followed by the piece index. The first piece has an index of 1, so its
 * first line would be @READ_1<br>
 * o The second line contains L characters in the {A,C,G,T} alphabet, representing the DNA sequence of the piece.<br>
 * o The third line contains the word +READ_ followed by the piece index (e.g., +READ_1).<br>
 * o The fourth line contains L characters, representing the quality scores of the piece. Each score is represented as
 * an ASCII character in the range 33-96, by adding 33 to the original score. For example, if the original score is 0,
 * it should be represented by the ASCII character 33 (“!”)<br>
 * 
 * ANSWER
 * 
 * See code below that solves the problem. For simplicity, the inputs to the program are defined as constants rather
 * than reading them (from the console, for example). Also for simplicity, the input file has been pregenerated and its
 * location is also hard-coded. This could also have been an input to the application, as well. The application will
 * read the input file and write the results to the file "target/dna-output/output.txt" relative to the working
 * directory.
 */
public class Main {
    private static final Path EXAMPLE_INPUT_PATH = Path.of("src/main/resources/example.binary");
    private static final Path OUTPUT_DIRECTORY = Path.of("target/dna-output");
    private static final Path OUTPUT_PATH = OUTPUT_DIRECTORY.resolve("output.txt");

    public static void main(String args[]) throws IOException {
        // Define what would normally be input to the function
        int numReadingBasesPerFragment = 2;
        Path inputPath = EXAMPLE_INPUT_PATH;

        // Create the output file directory, if it doesn't already exist. Files#newBufferedWriter will not do this, even
        // with StandardOpenOption.CREATE_NEW. That only seems to work at the file level.
        Files.createDirectories(OUTPUT_DIRECTORY);

        // As the input file may be huge (given the size of DNA), instead of reading the entire thing and then writing
        // the entire thing,
        // 1. Create buffered input streams and writers for efficiency (due to the one-by-one logic below)
        // 2. Read fragments one-by-one and write them one-by-one
        try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(inputPath));
                Writer writer = Files.newBufferedWriter(OUTPUT_PATH, StandardOpenOption.CREATE_NEW)) {
            for (int fragmentNumber = 1; transferFragmentNumberWithNumReadingBasesPerFragment(inputStream, writer,
                    fragmentNumber, numReadingBasesPerFragment); ++fragmentNumber) {
                // Do nothing. All work done in transfer function, which returns false when there's nothing left
            }
        }
    }

    private static boolean transferFragmentNumberWithNumReadingBasesPerFragment(InputStream inputStream, Writer writer,
            int fragmentNumber, int numReadingBasesPerFragment) {
        ReadingFragment fragment = BinaryStreamUtils.readReadingFragment(inputStream, numReadingBasesPerFragment);
        if (fragment == null) {
            return false;
        }
        FastqWriterUtils.writeReadingFragment(writer, fragment, fragmentNumber);
        return true;
    }

    /** Convenient function to be able to generate an example file for reading. */
    private static void writeExampleFile() throws IOException {
        byte content[] = new byte[] {(byte) 0b00000000, (byte) 0b11100000, (byte) 0b11000001, (byte) 0b01111111};
        try (OutputStream stream = Files.newOutputStream(EXAMPLE_INPUT_PATH)) {
            stream.write(content);
        }
    }
}
