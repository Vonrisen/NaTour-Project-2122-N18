package org.natour.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.natour.exceptions.PersistenceException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;

public class NatourS3Bucket {

    private final String access_key = "AKIARJHP7LO3ALA23O45";

    private final String secret_key = "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";

    AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);

    private final AmazonS3 s3_client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.EU_CENTRAL_1)
            .build();

    private final String bucket_name = "natour-android";


    public void putRouteImage(String image_name, byte[] bytes) throws PersistenceException {

        String key = "Routes/Images/" + image_name;

        try {

            s3_client.putObject(bucket_name, key, new ByteArrayInputStream(bytes), null);

        } catch (S3Exception e) {

            throw new PersistenceException(e.getMessage());

        }
    }

    public void putUserProfileImage(String username, byte[] bytes) throws PersistenceException {

        String key = "Users/ProfilePics/" + username;

        try {

            s3_client.putObject(bucket_name, key, new ByteArrayInputStream(bytes), null);

        } catch (S3Exception e) {

            throw new PersistenceException(e.getMessage());

        }
    }

    public byte[] fetchUserProfileImage(String username) throws PersistenceException {

        String key = "Users/ProfilePics/" + username;

         if(!s3_client.doesObjectExist(bucket_name, key))
             throw new PersistenceException("Image does not exist");

        S3Object image = s3_client.getObject(bucket_name, key);

        try {
            return IOUtils.toByteArray(image.getObjectContent());
        } catch (Exception e) {
            return null;
        }
    }


    public byte[] fetchRouteImage(String image_name) throws PersistenceException {

        String key = "Routes/Images/" + image_name;

        if(!s3_client.doesObjectExist(bucket_name, key))
            throw new PersistenceException("Image does not exist");

        S3Object image = s3_client.getObject(bucket_name, key);

        try {
            return IOUtils.toByteArray(image.getObjectContent());
        } catch (Exception e) {
            return null;
        }
    }


}
