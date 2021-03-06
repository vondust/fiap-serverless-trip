package br.com.fiap.trip.serverless.handler;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.trip.datastore.TripDatastore;
import br.com.fiap.trip.datastore.entity.Trip;
import br.com.fiap.trip.serverless.model.HandlerRequest;
import br.com.fiap.trip.serverless.model.HandlerResponse;

public class CreateTrip implements RequestHandler<HandlerRequest, HandlerResponse> {

	private TripDatastore repository = new TripDatastore();

	@Override
	public HandlerResponse handleRequest(final HandlerRequest request, final Context context) {

		Trip trip = null;
		try {
			trip = new ObjectMapper().readValue(request.getBody(), Trip.class);
		} catch (IOException e) {
			return HandlerResponse.builder()
					.setStatusCode(400)
					.setRawBody("There is a error in your TripEntity!")
					.build();
		}

		trip.setUrlRepository(createBucket(trip));
		context.getLogger().log("Creating a new trip: " + trip);
		final Trip tripRecorded = repository.save(trip);
		return HandlerResponse.builder().setStatusCode(201).setObjectBody(tripRecorded).build();
	}

	private String createBucket(final Trip trip) {
        String bucketName = new StringBuilder()
        		.append(trip.getCountry().toLowerCase()).append("-")
        		.append(trip.getCity().toLowerCase()).append("-")
        		.append(trip.getDateTrip())
        		.append(ThreadLocalRandom.current().nextLong(100000, 1000000))
        		.toString();

		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

			if (!s3Client.doesBucketExistV2(bucketName)) {
				s3Client.createBucket(new CreateBucketRequest(bucketName));
				return s3Client.getUrl(bucketName, null).toString();
			}
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (SdkClientException e) {
			e.printStackTrace();
		}

        return bucketName;
	}
}
