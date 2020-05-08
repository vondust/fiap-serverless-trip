package br.com.fiap.trip.datastore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import br.com.fiap.trip.datastore.config.DynamoDBManager;
import br.com.fiap.trip.datastore.entity.Trip;

public class TripDatastore {

	private static final DynamoDBMapper MAPPER = DynamoDBManager.mapper();

	public Trip save(final Trip trip) {
		MAPPER.save(trip);
		return trip;
	}

	public Optional<Trip> search(final String id) {
		final Trip trip = MAPPER.load(Trip.class, id);
		return Optional.ofNullable(trip);
	}

	public List<Trip> findByPeriod(final String starts, final String ends) {
		final Map<String, AttributeValue> params = new HashMap<String, AttributeValue>();
		params.put(":val1", new AttributeValue().withS(starts));
		params.put(":val2", new AttributeValue().withS(ends));

		final DynamoDBQueryExpression<Trip> queryExpression = new DynamoDBQueryExpression<Trip>()
				.withKeyConditionExpression("dateTrip between :val1 and :val2")
				.withExpressionAttributeValues(params);

		final List<Trip> studies = MAPPER.query(Trip.class, queryExpression);

		return studies;
	}

	public void delete(Trip input) {
		MAPPER.delete(input);
	}
}
