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
            // As a quick explanation of how this regex checks for:
            // 1. There must be at least one character that matches the criteria before the @ (at symbol): lowercase/uppercase a-z, numbers, comma, period, underscore
            // 2. There cannot be any other character than letters, numbers, periods, commas, and underscores before the @ (at symbol)
            // 3. There must be a @ (at symbol)
            // 4. There cannot be leading, trailing, or consecutive periods
            // 5. There cannot be consecutive @ symbols
            // 6. After the @ symbol, there must be at least one character, must have a period, and must have at least 2 characters following the period

            String regex = "^[a-zA-Z0-9_{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(regex);

            while (buffReader.ready()) {
                String email = buffReader.readLine();
                Matcher matcher = pattern.matcher(email);
                if(matcher.matches()) {
                    validEmails.add(email);
                }
            }

            sortEmail(validEmails);
            insertEmail(validEmails, outputBuffWriter);

            logger.log(Level.INFO, "Finished validating and sorting emails in: " + outputFile.getPath());
            } catch (FileNotFoundException e) {
                logger.log(Level.SEVERE, "File was not found in specified path. Error: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error: " + e.getMessage());
                e.printStackTrace();
            }

    }


    public static void insertEmail (List<String> validEmails, BufferedWriter writer) throws IOException {
        // Now that the emails are both valid and sorted, we can go ahead and
        //      1. Write the emails in order of the list
        //      2. Create a new line after (writer.write does not automatically write on the next line)
        for (int i = 0; i < validEmails.size(); i++) {
            writer.write(validEmails.get(i), 0, validEmails.get(i).length());
            writer.newLine();
        }

        writer.close();
    }

    public static void sortEmail (List<String> validEmails) {
        // Creating a custom comparator so that we can do two things:
        //         1. Sort by the string AFTER the @ (at symbol)
        //         2. Compare the two strings after the @
        Collections.sort(validEmails, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.split("@")[1].compareTo(b.split("@")[1]);
            }
        });
    }
}
