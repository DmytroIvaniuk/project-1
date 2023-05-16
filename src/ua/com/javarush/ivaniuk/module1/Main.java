package ua.com.javarush.ivaniuk.module1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final List<Character> ALPHABET = Arrays.asList('а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж',
            'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с',
            'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь',
            'э', 'ю', 'я', '.', ',', '"', ':', '-', '!', '?', ' ');

    public static List<Character> cipherAlphabet = Arrays.asList('а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж',
            'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с',
            'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь',
            'э', 'ю', 'я', '.', ',', '"', ':', '-', '!', '?', ' ');

    private static final int ENCRYPT = 1;
    private static final int DECRYPT_WITH_KEY = 2;
    private static final int DECRYPT_BRUTE_FORCE = 3;
    private static final int NUMBER_OF_ELEMENTS_IN_ALPHABET = ALPHABET.size();
    private static final int ZERO_DISPLACE = 0;
    private static Path sourceFilePath;
    private static Path destFilePath;
    private static int key;
    private static int action;
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        actionMenu();
        if (action == ENCRYPT) {
            writeToFile(destFilePath, cipherCaesar(sourceFilePath, key));
        }
        if (action == DECRYPT_WITH_KEY) {
            writeToFile(destFilePath, cipherCaesar(sourceFilePath, -key));
        }
        if (action == DECRYPT_BRUTE_FORCE) {
            writeToFile(destFilePath, bruteForce(sourceFilePath));
        }
    }

    public static void actionMenu() {
        System.out.println("Choose an action:\n" +
                "1 - Text encryption;\n" +
                "2 - Decrypt text with key;\n" +
                "3 - Brute force text decryption;");
        action = scanner.nextInt();
        scanner.nextLine();
        if(action<ENCRYPT||action>DECRYPT_BRUTE_FORCE){
            System.err.println("Choose one of the three menu items");
            System.exit(1);
        }
        if (action == ENCRYPT || action == DECRYPT_WITH_KEY) {
            sourceFilePath = fileAddressToRead();
            destFilePath = fileAddressToWrite();
            cryptoKey();
        }
        if (action == DECRYPT_BRUTE_FORCE) {
            sourceFilePath = fileAddressToRead();
            destFilePath = fileAddressToWrite();
        }


    }

    private static Path fileAddressToRead() {
        System.out.println("Enter file address to read:");
        String sourceFileName = scanner.nextLine();
        Path sourceFilePath = null;
        try {
            sourceFilePath = Path.of(sourceFileName);
        } catch (InvalidPathException e) {
            System.err.println("Path is invalid.");
            System.exit(2);
        }

        if (!Files.isRegularFile(sourceFilePath)) {
            System.err.println(sourceFilePath.getFileName() + " is not a file.");
            System.exit(3);
        }

        if (!sourceFilePath.toString().endsWith(".txt")) {
            System.err.println(sourceFilePath.getFileName() + " is not .txt");
            System.exit(4);
        }

        if (!Files.isReadable(sourceFilePath)) {
            System.err.println(sourceFilePath.getFileName() + " is not readable.");
            System.exit(5);
        }

        try {
            if (Files.size(sourceFilePath) == 0) {
                throw new IOException();
            }
        } catch (IOException e) {
            System.err.println(sourceFilePath.getFileName() + " is empty.");
            System.exit(6);
        }
        return sourceFilePath;
    }

    private static Path fileAddressToWrite() {
        System.out.println("Enter file address to write:");
        String destFileName = scanner.nextLine();
        Path destFilePath = null;
        try {
            destFilePath = Path.of(destFileName);
        } catch (InvalidPathException e) {
            System.err.println("Path is invalid.");
            System.exit(7);
        }

        if (Files.exists(destFilePath)) {
            if (!Files.isRegularFile(destFilePath)) {
                System.err.println(destFilePath.getFileName() + " is not a file.");
                System.exit(8);
            }

            if (!destFilePath.toString().endsWith(".txt")) {
                System.err.println(destFilePath.getFileName() + " is not .txt");
                System.exit(9);
            }

            if (!Files.isWritable(destFilePath)) {
                System.err.println(destFilePath.getFileName() + " is not writable.");
                System.exit(10);
            }
        } else {
            try {
                Files.createFile(destFilePath);
            } catch (IOException e) {
                System.err.println("Something went wrong while creating the file");
                System.exit(11);
            }
        }
        return destFilePath;
    }

    private static void cryptoKey() {
        System.out.println("Enter the key: ");
        key = scanner.nextInt();
        scanner.nextLine();
        try {
            if (key <= ZERO_DISPLACE || key > NUMBER_OF_ELEMENTS_IN_ALPHABET - 1)
                throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            System.err.println("Key must be a number, number must be greater than zero and less than "
                    + NUMBER_OF_ELEMENTS_IN_ALPHABET);
            System.exit(12);
        }
    }

    public static String cipherCaesar(Path sourceFilePath, int key) {
        String text = getStringFromFile(sourceFilePath);
        Collections.rotate(cipherAlphabet, key);
        char[] textChar = text.toLowerCase().toCharArray();
        StringBuilder cipherText = new StringBuilder();
        for (char c : textChar) {
            if (ALPHABET.contains(c)) {
                int temp = cipherAlphabet.indexOf(c);
                cipherText.append(ALPHABET.get(temp));
            } else {
                cipherText.append(c);
            }
        }
        return cipherText.toString();
    }

    private static void writeToFile(Path destFilePath, String cipherText) {
        try {
            Files.writeString(destFilePath, cipherText);
        } catch (IOException e) {
            System.err.println("Something went wrong while writing to the file");
            System.exit(13);
        }
    }

    private static String bruteForce(Path sourceFilePath) {
        String text = "";
        for (int i = ZERO_DISPLACE + 1; i < NUMBER_OF_ELEMENTS_IN_ALPHABET; i++) {
            text = cipherCaesar(sourceFilePath, ZERO_DISPLACE - 1).toLowerCase();
            if (text.contains(" на ") || text.contains(" или ") || text.contains(" но ")
                    || text.contains(" не ") || text.contains(" по ") || text.contains(" из ")
                    || text.contains(" в ")|| text.contains(" с ")) {
                return text;
            }
        }
        return text;
    }

    private static String getStringFromFile(Path filePath) {
        String text = "";
        try {
            text = Files.readString(filePath);
        } catch (IOException e) {
            System.err.println("Can't read the file " + filePath.getFileName());
            System.exit(14);
        }
        return text;
    }
}
