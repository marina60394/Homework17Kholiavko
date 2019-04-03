package com.aqacources.project.gmail;

import com.aqacources.project.utils.YamlParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marina on 03.04.2019.
 */
public class CreateNewGmailEmailUsingDots {

    // Path to file with used emails
    private static final String FILE_PATH = "src/main/resources/emails.txt";

    /**
     * Create new email using dots technique
     *
     * @return String that represents new email
     * @throws IOException
     */
    public static String newEmail() throws IOException {

        File fileWithEmail = new File(FILE_PATH);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileWithEmail.getPath()));

        String line;
        List <String> listOfEmails = new ArrayList <>();
        while ((line = bufferedReader.readLine()) != null) {
            listOfEmails.add(line);
        }
        bufferedReader.close();

        String email = YamlParser.getYamlData().getEmail();
        String[] result = new String[262144];
        String[] splitEmail = email.split("[@]");
        String word = splitEmail[0];
        String domain = splitEmail[1];
        String newEmail = null;
        int maskMaxValue = 0;
        for (int j = 0; j < word.length() - 1; ++j) {
            maskMaxValue |= 1 << j;
        }

        for (int mask = 0; mask <= maskMaxValue; ++mask) {
            String dottedWord = word;

            for (int position = word.length() - 1; position >= 1; --position) {
                if ((mask & (1 << position)) > 0) {
                    dottedWord = new StringBuilder(dottedWord).insert(position, ".").toString().replaceAll("\\.\\.\\.|\\.\\.", ".");
                }
            }
            result[mask] = dottedWord + "@" + domain;
        }

        for (int j = 0; j < result.length; j++) {
            if (!listOfEmails.contains(result[j])) {
                newEmail = result[j];
                break;
            }
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileWithEmail, true));
        bufferedWriter.write(newEmail);
        bufferedWriter.newLine();
        bufferedWriter.close();

        return newEmail;
    }
}

