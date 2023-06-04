package org.natour.tokens;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.RSAKeyProvider;

public class CognitoTokens {

    private static final String POOL_ID = "";
    private  JWTVerifier jwtVerifier;

    public CognitoTokens(){
        String aws_cognito_region = "eu-central-1"; // Replace this with your aws cognito region
        RSAKeyProvider keyProvider = new AwsCognitoRSAKeyProvider(aws_cognito_region, POOL_ID);
        Algorithm algorithm = Algorithm.RSA256(keyProvider);
        jwtVerifier = JWT.require(algorithm).build();
    }
    public void verifyIdToken(String token) {

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
