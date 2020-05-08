package br.com.fiap.trip.serverless.handler;

import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import br.com.fiap.trip.datastore.TripDatastore;
import br.com.fiap.trip.datastore.entity.Trip;
import br.com.fiap.trip.serverless.model.HandlerRequest;
import br.com.fiap.trip.serverless.model.HandlerResponse;

public class SearchTripById implements RequestHandler<HandlerRequest, HandlerResponse> {

	private TripDatastore datastore = new TripDatastore();

	@Override
	public HandlerResponse handleRequest(final HandlerRequest request, final Context context) {

		final String id = request.getPathParameters().get("id");
		context.getLogger().log("[#] - Searching Trip by Id: " + id);
		final Optional<Trip> tripRetrieved = datastore.search(id);

		if (tripRetrieved.isPresent()) {
			context.getLogger().log("[#] - Trip found " + tripRetrieved.get());
			return HandlerResponse.builder().setObjectBody(tripRetrieved.get()).build();
		}

		return HandlerResponse.builder().setStatusCode(404).build();
	}
}
