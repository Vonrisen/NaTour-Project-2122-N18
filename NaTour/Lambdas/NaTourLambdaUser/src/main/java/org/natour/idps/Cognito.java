package org.natour.idps;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.google.gson.Gson;
import org.natour.entities.User;
import org.natour.exceptions.CognitoException;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Cognito {

    private final String CLIENT_ID = "";
    private final String CLIENT_SECRET = "";
    private final String POOL_ID = "";


    private CognitoIdentityProviderClient cognito_client;

    public Cognito() {
        //Creating cognito provider client
        cognito_client = CognitoIdentityProviderClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
    }

    public void signUpUser(User user) throws CognitoException {

        try {
            if (userExistsByEmail(user.getEmail()))
                throw new CognitoException("A user with this email already exists");

            //Setting up user attributes(in our case, only email is needed)
            AttributeType user_attributes = AttributeType.builder()
                    .name("email")
                    .value(user.getEmail())
                    .build();

            //Safe way to encode client id and secret id
            String secret_hash = getSecretHash(user.getUsername(), CLIENT_SECRET, CLIENT_ID);
            //Setting up user request, specifying user pool id, username, password and attributes
            SignUpRequest signup_request = SignUpRequest.builder().username(user.getUsername()).password(user.getPassword()).userAttributes(user_attributes).clientId(CLIENT_ID).secretHash(secret_hash).build();

            //Creating user
            cognito_client.signUp(signup_request);

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CognitoException("Server Error");
        }
    }

    public void confirmUser(String username, String confirmation_code) throws CognitoException {

        try {
            //Safe way to encode client id and secret id
            String secret_hash = getSecretHash(username, CLIENT_SECRET, CLIENT_ID);
            //Setting up confirm request
            ConfirmSignUpRequest c = ConfirmSignUpRequest.builder().secretHash(secret_hash).confirmationCode(confirmation_code).username(username).clientId(CLIENT_ID).build();
            //Confirming user by confirmation_code(Sent via email after signup)
            cognito_client.confirmSignUp(c);
        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CognitoException("Server Error");
        }
    }

    public void initiateForgotPassword(String username) throws CognitoException {
        try {

            if (!userExistsByUsername(username))
                throw new CognitoException("User does not exist");

            String secret_hash = getSecretHash(username, CLIENT_SECRET, CLIENT_ID);

            ForgotPasswordRequest forgotpwd_request = ForgotPasswordRequest.builder().clientId(CLIENT_ID).secretHash(secret_hash).username(username).build();

            cognito_client.forgotPassword(forgotpwd_request);

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CognitoException("Server Error");
        }
    }

    public void resetPassword(String username, String new_password, String confirmation_code) throws CognitoException {

        try {
            String secret_hash = getSecretHash(username, CLIENT_SECRET, CLIENT_ID);

            ConfirmForgotPasswordRequest confirm_forgotpwd_request = ConfirmForgotPasswordRequest.builder().username(username).clientId(CLIENT_ID).password(new_password).secretHash(secret_hash).confirmationCode(confirmation_code).build();

            cognito_client.confirmForgotPassword(confirm_forgotpwd_request);

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CognitoException("Server Error");
        }
    }

    public void changePassword(String old_password, String new_password, String access_token) throws CognitoException {

        try {
            ChangePasswordRequest change_password_request = ChangePasswordRequest.builder().accessToken(access_token).previousPassword(old_password).proposedPassword(new_password).build();

            cognito_client.changePassword(change_password_request);

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        }

    }

    public String signInAndGetTokens(String username, String password) throws CognitoException {


        try {
            Map<String, String> auth_params = new HashMap<>();
            auth_params.put("USERNAME", username);
            auth_params.put("PASSWORD", password);
            auth_params.put("SECRET_HASH", getSecretHash(username, CLIENT_SECRET, CLIENT_ID));

            InitiateAuthRequest signin_request = InitiateAuthRequest.builder().authParameters(auth_params).
                    clientId(CLIENT_ID).authFlow(AuthFlowType.USER_PASSWORD_AUTH).build();

            InitiateAuthResponse response = cognito_client.initiateAuth(signin_request);

            AuthenticationResultType auth_result = response.authenticationResult();

            Tokens tokens = new Tokens(auth_result.idToken(), auth_result.refreshToken(), auth_result.accessToken());

            Gson gson = new Gson();

            String json_tokens = gson.toJson(tokens);

            return json_tokens;

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CognitoException("Server Error");
        }

    }

    public String getNewIdAndAccessTokens(String refresh_token, String username) throws CognitoException {

        try {
            Map<String, String> auth_params = new HashMap<>();
            auth_params.put("SECRET_HASH", getSecretHash(username, CLIENT_SECRET, CLIENT_ID));
            auth_params.put("REFRESH_TOKEN", refresh_token);

            AdminInitiateAuthRequest signin_request = AdminInitiateAuthRequest.builder().authParameters(auth_params).
                    clientId(CLIENT_ID).authFlow(AuthFlowType.REFRESH_TOKEN_AUTH).userPoolId(POOL_ID).build();

            AdminInitiateAuthResponse response = cognito_client.adminInitiateAuth(signin_request);

            AuthenticationResultType auth_result = response.authenticationResult();

            Tokens tokens = new Tokens(auth_result.idToken(), null, auth_result.accessToken());

            Gson gson = new Gson();

            String id_token = gson.toJson(tokens, Tokens.class);

            return id_token;

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean createGoogleUser(User user, String id_token) throws CognitoException, GeneralSecurityException, IOException {


        int action = getGoogleSignInAction(user.getEmail());
        //Action = 1 : If user had already registered with google, he is free to sign in if token is valid, otherwise an exception is thrown
        if (action == 1) {
            if (GoogleAuth.isIdTokenValid(id_token))
                return false;
            throw new RuntimeException("User not authorized, please sign in again "+ id_token);
        }
        //Action = 2 : A user (signed up regularly) is already registered with that email, so an exception is thrown
        else if (action == 2)
            throw new CognitoException("Email already used, please sign in regularly");

        //Action = 0 : User with the given email does not exist, so a new user is registered in the userpool

        CharacterRule alphabets = new CharacterRule(EnglishCharacterData.Alphabetical);
        CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit);
        CharacterRule special = new CharacterRule(EnglishCharacterData.Special);

        PasswordGenerator passwordGenerator = new PasswordGenerator();
        String password = passwordGenerator.generatePassword(10, alphabets, digits, special) +";2M";


        AttributeType user_attributes = AttributeType.builder()
                .name("email")
                .value(user.getEmail())
                .build();

        try {

            AdminCreateUserRequest create_user_request = AdminCreateUserRequest.builder().userPoolId(POOL_ID).username(user.getUsername()).messageAction("SUPPRESS").userAttributes(user_attributes).temporaryPassword(password).build();

            cognito_client.adminCreateUser(create_user_request);

            AdminSetUserPasswordRequest user_password_request = AdminSetUserPasswordRequest.builder().userPoolId(POOL_ID).password(password).permanent(true).username(user.getUsername()).build();

            cognito_client.adminSetUserPassword(user_password_request);

            return true;

        } catch (CognitoIdentityProviderException e) {

            throw new CognitoException(e.awsErrorDetails().errorMessage());

        }
    }


    private String getSecretHash(String username, String client_secret, String client_id) throws NoSuchAlgorithmException, InvalidKeyException {

        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                client_secret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(signingKey);
        mac.update(username.getBytes(StandardCharsets.UTF_8));
        byte[] rawHmac = mac.doFinal(client_id.getBytes(StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(rawHmac);
    }

    public boolean userExistsByEmail(String email) throws CognitoException {
        try {

            String filter = "email = \"" + email + "\"";

            ListUsersRequest usersRequest = ListUsersRequest.builder()
                    .userPoolId(POOL_ID).limit(1)
                    .filter(filter)
                    .build();

            ListUsersResponse response = cognito_client.listUsers(usersRequest);

            return !response.users().isEmpty();

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.getMessage());
        }
    }


    public int getGoogleSignInAction(String email) throws CognitoException {
        try {

            String filter = "email = \"" + email + "\"";

            ListUsersRequest usersRequest = ListUsersRequest.builder()
                    .userPoolId(POOL_ID).limit(1)
                    .filter(filter)
                    .build();

            ListUsersResponse response = cognito_client.listUsers(usersRequest);

            if (response.users().isEmpty())
                return 0;
            else if (response.users().get(0).attributes().get(1).value().equals("true"))
                return 2;
            else
                return 1;

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.getMessage());
        }
    }


    public HashMap<String, Object> getCognitoUsers(String pagination_token) throws CognitoException {


        HashMap<String, Object> ret = new HashMap<>();

        ListUsersRequest user_request;
        try {
            if (pagination_token == null)
                user_request = ListUsersRequest.builder()
                        .userPoolId(POOL_ID).attributesToGet("email")
                        .build();
            else
                user_request = ListUsersRequest.builder()
                        .userPoolId(POOL_ID).paginationToken(pagination_token).attributesToGet("email")
                        .build();
        }
         catch(CognitoIdentityProviderException e){
                throw new CognitoException(e.getMessage());
            }
            ListUsersResponse response = cognito_client.listUsers(user_request);

            ret.put("users", response.users());
            ret.put("pagination_token", response.paginationToken());

            return ret;
        }


        public boolean userExistsByUsername (String username) throws CognitoException {
            try {

                String filter = "username = \"" + username + "\"";


                ListUsersRequest usersRequest = ListUsersRequest.builder().limit(1)
                        .userPoolId(POOL_ID)
                        .filter(filter)
                        .build();

                ListUsersResponse response = cognito_client.listUsers(usersRequest);

                return !response.users().isEmpty();

            } catch (CognitoIdentityProviderException e) {
                throw new CognitoException(e.getMessage());
            }
        }

        public void verifyIdToken (String token){

            String aws_cognito_region = "eu-central-1"; // Replace this with your aws cognito region
            RSAKeyProvider keyProvider = new AwsCognitoRSAKeyProvider(aws_cognito_region, POOL_ID);
            Algorithm algorithm = Algorithm.RSA256(keyProvider);

            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            try {
                jwtVerifier.verify(token);
            } catch (JWTVerificationException e) {
                //Se il token è scaduto allora uso il refresh token per ottenere un nuovo token, senza disturbare l utente (sloggarlo e chiedergli di ottenere un nuovo idtoken inserendo pwd e username)
                if (e.getMessage().contains("expired"))
                    throw new RuntimeException(e.getMessage());

                //Se il token è stato manomesso o per qualche motivo non viene validato correttamente, allora lo user dovrà per forza di cose fare il signin nuovamente e ottenere un idtoken da capo
                throw new RuntimeException("Invalid Session, please sign in again: " + e.getMessage());

            }
        }
    }
