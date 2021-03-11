package org.emailvalidator.npr;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class EmailValidatorTest {

    @org.junit.jupiter.api.Test
    void main() throws IOException {
        // Change as needed
        String sourcePath = "I:/Documents/main_test.txt";
        String outputPath = "I:/Documents/main_test_output.txt";

        List<String> testEmails = new ArrayList<String>();
        testEmails.add("jeremy.kim@gmail.com");
        testEmails.add("test.demo@npr.org");
        testEmails.add("demo@yahoo.com");
        testEmails.add("test$10@abc.com");
        testEmails.add("test!blah@demo.com");
        testEmails.add("test.4@@@abc.com");

        List<String> expectedEmails = new ArrayList<String>();
        expectedEmails.add("jeremy.kim@gmail.com");
        expectedEmails.add("test.demo@npr.org");
        expectedEmails.add("demo@yahoo.com");

        File testMainSourceFile = new File (sourcePath);
        testMainSourceFile.createNewFile();
        FileWriter outputFW = new FileWriter(testMainSourceFile.getPath());
        BufferedWriter writer = new BufferedWriter(outputFW);

        for (int i = 0; i < testEmails.size(); i++) {
            writer.write(testEmails.get(i), 0, testEmails.get(i).length());
            writer.newLine();
        }

        writer.close();

        EmailValidator.main(new String[]{sourcePath, outputPath});

        File testOutputFile = new File (outputPath);
        FileReader testFileReader = new FileReader(testOutputFile.getPath());
        BufferedReader testBuffReader = new BufferedReader(testFileReader);

        int index = 0;
        while (testBuffReader.ready()) {
            String email = testBuffReader.readLine();
            assertEquals(email, expectedEmails.get(index));

            index++;
        }

    }

    @org.junit.jupiter.api.Test
    void insertEmail() throws IOException {
        String sourcePath = "I:/Documents/insert_email_test.txt";

        List<String> testEmails = new ArrayList<String>();
        testEmails.add("jeremy.kim@gmail.com");
        testEmails.add("test.demo@npr.org");
        testEmails.add("demo@yahoo.com");

        File testOutputFile = new File(sourcePath);
        testOutputFile.createNewFile();
        FileWriter outputFW = new FileWriter(testOutputFile.getPath());
        BufferedWriter testWriter = new BufferedWriter(outputFW);
        EmailValidator.insertEmail(testEmails, testWriter);

        FileReader testFileReader = new FileReader(testOutputFile.getPath());
        BufferedReader testBuffReader = new BufferedReader(testFileReader);

        int index = 0;
        while (testBuffReader.ready()) {
            String email = testBuffReader.readLine();
            assertEquals(email, testEmails.get(index));

            index++;
        }
    }

    @org.junit.jupiter.api.Test
    void sortEmail() {
        List<String> testEmails = new ArrayList<String>();
        testEmails.add("test.demo@npr.org");
        testEmails.add("jeremy.kim@hotmail.com");
        testEmails.add("john.doe@gmail.com");
        testEmails.add("test@aol.com");
        testEmails.add("demo@yahoo.com");

        List<String> expectedEmails = new ArrayList<String>();
        expectedEmails.add("test@aol.com");
        expectedEmails.add("john.doe@gmail.com");
        expectedEmails.add("jeremy.kim@hotmail.com");
        expectedEmails.add("test.demo@npr.org");
        expectedEmails.add("demo@yahoo.com");

        EmailValidator.sortEmail(testEmails);

        for (int i = 0; i < testEmails.size(); i++) {
            assertEquals(testEmails.get(i), expectedEmails.get(i));
        }
    }
}