package code.imogen.upload;

import java.util.Calendar;
import java.util.Date;

import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class S3Uploader {

	private final S3Service s3Service;
	
	public S3Uploader() {
		AWSCredentials awsCredentials = 
			    new AWSCredentials(ACCESS_KEY, SECRET_ACCESS_KEY);
		s3Service = new RestS3Service(awsCredentials);
	}
	
	public String upload(String filename, String content) {
		try {
			S3Bucket[] myBuckets = s3Service.listAllBuckets();
			
			S3Object stringObject = new S3Object(filename, content);
			stringObject.addMetadata("content-type", "text/html; charset=utf-8");
			
			s3Service.putObject(myBuckets[0], stringObject);
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, 24);
			Date expiryDate = cal.getTime();
			String signedUrl = s3Service.createSignedGetUrl(
				    myBuckets[0].getName(), stringObject.getKey(), expiryDate, false);

			return signedUrl;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		S3Uploader uploader = new S3Uploader();
		uploader.upload("test4", "<!doctype html><html><body><h1>Hello2!</h1><p>content</p></body></html>");
	}
}
