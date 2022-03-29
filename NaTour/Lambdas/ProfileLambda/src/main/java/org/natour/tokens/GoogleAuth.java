package org.natour.tokens;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;

public class GoogleAuth {



    public static boolean isIdTokenValid(String id_token) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("556927589955-pahgt8na4l8de0985mvlc9gugfltbkef.apps.googleusercontent.com"))

                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken token = verifier.verify(id_token);

        return token != null ? true : false;

    }

    public static HashMap<String, String> getAccountParams(String id_token) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("556927589955-pahgt8na4l8de0985mvlc9gugfltbkef.apps.googleusercontent.com"))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken token = verifier.verify(id_token);

        if (token != null) {

            HashMap<String, String> account_params = new HashMap<>();

            Payload payload = token.getPayload();

            // Get profile information from payload
            account_params.put("email",payload.getEmail());
            account_params.put("username", (String)payload.get("name"));

            return account_params;

        }

        throw new RuntimeException("Invalid Google id token");
    }
}
