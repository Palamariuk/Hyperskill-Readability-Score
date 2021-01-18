package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    final static Scanner scanner = new Scanner(System.in);
    static final int[] grades = {6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 24};

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static String[] splitIntoWords(String text) {
        return text.split("\\s+");
    }

    public static int calculateCharacters(String text) {
        return text.replaceAll("[ \n\t]", "").length();
    }

    public static int calculateWords(String text) {
        return splitIntoWords(text).length;
    }

    public static int calculateSentences(String text) {
        return text.split("[?!]|\\.{1,3}").length;
    }

    public static int calculateSyllables(String word) {
        word = word.toLowerCase();
        int result = word.replaceAll("e\\b", "").
                replaceAll("[aeiouy]+", "a").
                replaceAll("[^a]", "").
                length();
        return Math.max(1, result);
    }

    public static double calculateAri(int chars, int words, int sentences) {
        return 4.71 * (chars * 1.0 / words) + 0.5 * (words * 1.0 / sentences) - 21.43;
    }

    public static double calculateFk(int words, int sentences, int syllables) {
        return 0.39 * (words * 1.0 / sentences) + 11.8 * (syllables * 1.0 / words) - 15.59;
    }

    public static double calculateSmog(int words, int sentences, int polysyllables) {
        return 1.043 * Math.sqrt(polysyllables * 30.0 / sentences) + 3.1291;
    }

    public static double calculateCl(int chars, int words, int sentences) {
        double s = sentences * 1.0 / words * 100;
        double l = chars * 1.0 / words * 100;
        return 0.0588 * l - 0.296 * s - 15.8;
    }

    public static void main(String[] args) {

        try {
            String path = Paths.get(args[0]).toAbsolutePath().toString();
            String text = readFileAsString(path);

            int numberOfCharacters = calculateCharacters(text);
            int numberOfWords = calculateWords(text);
            int numberOfSentences = calculateSentences(text);

            int numberOfSyllables = Arrays.stream(text.split("\\s+")).
                    mapToInt(Main::calculateSyllables).
                    sum();
            int numberOfPolysyllables = (int) Arrays.stream(text.split("\\s+")).
                    mapToInt(Main::calculateSyllables).
                    filter(a -> a > 2).
                    count();


            System.out.printf("The text is:\n%s\n\n", text);
            System.out.println("Words: " + numberOfWords);
            System.out.println("Sentences: " + numberOfSentences);
            System.out.println("Characters: " + numberOfCharacters);
            System.out.println("Syllables: " + numberOfSyllables);
            System.out.println("Polysyllables: " + numberOfPolysyllables);
            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            String typeOfScore = scanner.nextLine();
            System.out.println();

            double averageAge = 0;

            if (typeOfScore.equals("ARI") || typeOfScore.equals("all")) {
                double score = calculateAri(numberOfCharacters, numberOfWords, numberOfSentences);
                int age = score < grades.length ? grades[(int) score - 1] : grades[12];
                System.out.printf("Automated Readability Index: %.2f (about %d year olds).\n", score, age);
                averageAge += age;
            }

            if (typeOfScore.equals("FK") || typeOfScore.equals("all")) {
                double score = calculateFk(numberOfWords, numberOfSentences, numberOfSyllables);
                int age = score < grades.length ? grades[(int) score - 1] : grades[12];
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d year olds).\n", score, age);
                averageAge += age;
            }

            if (typeOfScore.equals("SMOG") || typeOfScore.equals("all")) {
                double score = calculateSmog(numberOfWords, numberOfSentences, numberOfPolysyllables);
                int age = score < grades.length ? grades[(int) score - 1] : grades[12];
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d year olds).\n", score, age);
                averageAge += age;
            }

            if (typeOfScore.equals("CL") || typeOfScore.equals("all")) {
                double score = calculateCl(numberOfCharacters, numberOfWords, numberOfSentences);
                int age = score < grades.length ? grades[(int) score - 1] : grades[12];
                System.out.printf("Coleman–Liau index: %.2f (about %d year olds).\n", score, age);
                averageAge += age;
            }

            averageAge /= 4.0;
            if (typeOfScore.equals("all")) {
                System.out.printf("This text should be understood in average by %.2f year olds.", averageAge);
            }

        } catch (ArrayIndexOutOfBoundsException | IOException error) {
            System.out.println("Cannot read file: " + error.getMessage());
        }

    }
}
