package br.com.fiap.trip.datastore.config;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBManager {

	private static DynamoDBMapper MAPPER;

	static {

		AmazonDynamoDB ddb = null;
		final String endpoint = System.getenv("ENDPOINT_OVERRIDE");

		if (endpoint != null && !endpoint.isEmpty())
			ddb = AmazonDynamoDBClientBuilder.standard()
					.withEndpointConfiguration(new EndpointConfiguration(endpoint, "us-east-1")).build();
		else
			ddb = AmazonDynamoDBClientBuilder.defaultClient();

		MAPPER = new DynamoDBMapper(ddb);
	}

	public static DynamoDBMapper mapper() {
		return DynamoDBManager.MAPPER;
	}
}
