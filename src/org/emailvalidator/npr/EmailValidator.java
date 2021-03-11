package org.emailvalidator.npr;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

    static File outputFile;
    static Logger logger = Logger.getLogger(EmailValidator.class.getName());
    static List<String> validEmails = new ArrayList<String>();

    // expects args[0] to be source path, args[1] to be output path
    public static void main(String[] args) {
        String sourcePath;
        String outputPath;

        // defaults both paths to hardcoded path if args don't exist
        if (args.length >= 2) {
            sourcePath = args[0];
            outputPath = args[1];
        } else {
            sourcePath = "I:/Documents/email_address.txt";
            outputPath = "I:/Documents/email_address_output.txt";
        }

        outputFile = new File(outputPath);
        logger.log(Level.INFO, "Starting email conversion.");

        try (FileReader fileReader = new FileReader(sourcePath);
             BufferedReader buffReader = new BufferedReader(fileReader);){
            logger.log(Level.INFO, "Found file to be analyzed.");
            if(outputFile.createNewFile()) {
                logger.log(Level.INFO, "New output was created in: " + outputFile.getPath());
            } else {
                PrintWriter pw = new PrintWriter(outputFile.getPath());
                pw.close();
                logger.log(Level.INFO, "Existing Output file found and cleared for new email validation and sorting. File path: " + outputFile.getPath());
            }

            FileWriter outputFW = new FileWriter(outputFile.getPath());
            BufferedWriter outputBuffWriter = new BufferedWriter(outputFW);
            // As a quick explanation of how this regex works,
            // ^[a-zA-Z0-9_.-] this portion means
            //          1.) the string can only contain lowercase and uppercase a-z,
            //          2.) numbers 0-9 and
            //          3.) only , . _ (comma period underscore characters)
            // the + (plus symbol) indicates that there needs to be at least one character that fits this criteria above
            // (?:\.[a-zA-Z0-9_.-]+)
            //          the (?: indicates that the following will group but not match/capture the expression
            //          the \\. indicates that we are checking if an expression matches the "." (period)
            //          the [a-zA-Z0-9_.-] indicates that we are checking for the same criteria as above (letters, numbers, and , . _)
            //          while the + (plus) indicates that there needs to be at least character that matches any of these criteria,
            //          the * (asterisk) preceding that means 0 or more needs to match
            // The @ (at symbol) indicates that there needs to be an @ symbol after the criteria above

            String regex = "^[a-zA-Z0-9_{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(regex);

            while (buffReader.ready()) {
                String email = buffReader.readLine();
                Matcher matcher = pattern.matcher(email);
                System.out.println("original: " + email);
                if(matcher.matches()) {
                    System.out.println("validated: " + email);
                    validEmails.add(email);
                }
            }

            sortEmail(validEmails);
            insertEmail(validEmails, outputBuffWriter);

            logger.log(Level.INFO, "Finished validating and sorting emails in: " + outputFile.getPath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "File was not found in specified path. Error: " + e.getMessage());
                e.printStackTrace();
            }

    }


    public static void insertEmail (List<String> validEmails, BufferedWriter writer) throws IOException {
        for (int i = 0; i < validEmails.size(); i++) {
            writer.write(validEmails.get(i), 0, validEmails.get(i).length());
            writer.newLine();
        }

        writer.close();
    }

    public static void sortEmail (List<String> validEmails) {
        Collections.sort(validEmails, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.split("@")[1].compareTo(b.split("@")[1]);
            }
        });
    }
}
