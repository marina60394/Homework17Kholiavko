package com.aqacources.project.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marina on 03.04.2019.
 */
public class GetEmailsFromGmail {

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME =
            "MyApplication";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials_qa/gmail-api-quickstart");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     */
    private static final List <String> SCOPES =
            Arrays.asList(GmailScopes.GMAIL_MODIFY, GmailScopes.GMAIL_LABELS);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                GetEmailsFromGmail.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Method that gets list of unread messages
     *
     * @param wait
     * @return
     * @throws IOException
     */
    public static List <Message> getListOfEmail(boolean wait) throws IOException {
        // Build a new authorized API client service.
        Gmail service = getGmailService();

        // Print the labels in the user's account.
        String user = "me";
        String query = "is:unread";
        ListMessagesResponse response = service.users().messages().list(user).setQ(query).execute();
        int attempt = 10;
        if (wait == true) {
            if (response.getMessages() == null) {
                for (int i = 0; i <= attempt; i++) {
                    response = service.users().messages().list(user).setQ(query).execute();
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (response.getMessages() == null) {
                        continue;
                    } else {
                        break;
                    }
                }
            }
        }

        List <Message> messages = new ArrayList <>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(user).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        return messages;
    }

    /**
     * Method that gets message by id
     *
     * @return
     * @throws IOException
     */
    public static Message getMessage() throws IOException {
        String messageId = null;
        Gmail service = getGmailService();

        // Print the labels in the user's account.
        String user = "me";

        List <Message> messages = getListOfEmail(true);

        for (Message message : messages) {
            messageId = message.getId();
        }

        Message message = service.users().messages().get(user, messageId).execute();

        return message;
    }

    /**
     * Method that marks message as read
     *
     * @throws IOException
     */
    public static void makeAsRead() throws IOException {
        String messageId;
        Gmail service = getGmailService();

        Message message = getMessage();
        messageId = message.getId();
        String user = "me";

        List <String> labelsToRemove = Arrays.asList("UNREAD");

        ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(labelsToRemove);
        service.users().messages().modify(user, messageId, mods).execute();
    }

    /**
     * Method that marks all messages as read
     *
     * @throws IOException
     */
    public static void makeAllAsRead() throws IOException {
        String messageId;
        Gmail service = getGmailService();
        List <Message> messages = getListOfEmail(false);
        for (Message message : messages) {
            messageId = message.getId();
            String user = "me";

            List <String> labelsToRemove = Arrays.asList("UNREAD");

            ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(labelsToRemove);
            service.users().messages().modify(user, messageId, mods).execute();
        }
    }

    /**
     * Method to get exact mime message by id and get URL to confirm registration
     */
    public static String getInviteUrl() throws IOException {
        Gmail service = getGmailService();
        String userId = "me";
        String messageId = null;
        String inviteToken = null;
        List <Message> messages = getListOfEmail(true);

        for (Message message : messages) {
            messageId = message.getId();
        }

        Message message = service.users().messages().get(userId, messageId).execute();

        List <MessagePart> parts = message.getPayload().getParts();

        for (MessagePart part : parts) {
            MessagePartBody body = part.getBody();
            byte[] fileByteArray = Base64.decodeBase64(body.getData());
            String p = ".*(http.*)\\s+Please.*";
            Pattern pattern = Pattern.compile(p);
            String str = new String(fileByteArray);
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                inviteToken = matcher.group(1);
                break;
            }
        }
        return inviteToken;
    }

}
