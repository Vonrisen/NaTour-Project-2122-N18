package org.natour.idps;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import org.natour.entities.User;
import org.natour.exceptions.CognitoException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Cognito {

    private final String CLIENT_ID = "7krigpkagkcuph3r4li6f8qkk2";
    private final String CLIENT_SECRET = "10lt8rrlbauglu4cuc2magjp4tpe62ufek7m8bkl98pce09ca5dk";
    private final String POOL_ID = "eu-central-1_1QYsCdYWB";

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
            String secret_hash = getSecretHash(user.getUsername());
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
            String secret_hash = getSecretHash(username);
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

            String secret_hash = getSecretHash(username);

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
            String secret_hash = getSecretHash(username);

            ConfirmForgotPasswordRequest confirm_forgotpwd_request = ConfirmForgotPasswordRequest.builder().username(username).clientId(CLIENT_ID).password(new_password).secretHash(secret_hash).confirmationCode(confirmation_code).build();

            cognito_client.confirmForgotPassword(confirm_forgotpwd_request);

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CognitoException("Server Error");
        }
    }

    public String signInUserAndGetTokens(String username, String password) throws CognitoException {


        try {
            Map<String, String> auth_params = new HashMap<>();
            auth_params.put("USERNAME", username);
            auth_params.put("PASSWORD", password);
            auth_params.put("SECRET_HASH", getSecretHash(username));

            InitiateAuthRequest signin_request = InitiateAuthRequest.builder().authParameters(auth_params).
                    clientId(CLIENT_ID).authFlow(AuthFlowType.USER_PASSWORD_AUTH).build();

            InitiateAuthResponse response = cognito_client.initiateAuth(signin_request);

            AuthenticationResultType auth_result = response.authenticationResult();

            String json_tokens = "{\"id_token\":" + auth_result.idToken() + ",\"refresh_token\":" + auth_result.refreshToken() + "\"}";

            return json_tokens;

        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CognitoException("Server Error");
        } catch (Exception e) {
            throw new CognitoException("E");
        }

    }

    public String getNewIdToken(String refresh_token, String username) throws CognitoException {

        try {
            Map<String, String> auth_params = new HashMap<>();
            auth_params.put("SECRET_HASH", getSecretHash(username));
            auth_params.put("REFRESH_TOKEN", refresh_token);

            AdminInitiateAuthRequest signin_request = AdminInitiateAuthRequest.builder().authParameters(auth_params).
                    clientId(CLIENT_ID).authFlow(AuthFlowType.REFRESH_TOKEN_AUTH).userPoolId(POOL_ID).build();

            AdminInitiateAuthResponse response = cognito_client.adminInitiateAuth(signin_request);

            AuthenticationResultType auth_result = response.authenticationResult();

            String new_id_token = auth_result.idToken();

            return new_id_token;
        } catch (CognitoIdentityProviderException e) {
            throw new CognitoException(e.awsErrorDetails().errorMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private String getSecretHash(String username) throws NoSuchAlgorithmException, InvalidKeyException {

        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                CLIENT_SECRET.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(signingKey);
        mac.update(username.getBytes(StandardCharsets.UTF_8));
        byte[] rawHmac = mac.doFinal(CLIENT_ID.getBytes(StandardCharsets.UTF_8));
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

    public boolean userExistsByUsername(String username) throws CognitoException {
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

    public void verifyIdToken(String token) {

        String aws_cognito_region = "eu-central-1"; // Replace this with your aws cognito region
        RSAKeyProvider keyProvider = new AwsCognitoRSAKeyProvider(aws_cognito_region, POOL_ID);
        Algorithm algorithm = Algorithm.RSA256(keyProvider);

        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            //Se il token è scaduto allora uso il refresh token per ottenere un nuovo token, senza disturbare l utente (sloggarlo e chiedergli di ottenere un nuovo idtoken inserendo pwd e username)
            if (e.getMessage().contains("The Token has expired on"))
                throw new RuntimeException(e.getMessage());

            //Se il token è stato manomesso o per qualche motivo non viene validato correttamente, allora lo user dovrà per forza di cose fare il signin nuovamente e ottenere un idtoken da capo
            throw new RuntimeException("Invalid Session, please sign in again");
        }
    }
}
