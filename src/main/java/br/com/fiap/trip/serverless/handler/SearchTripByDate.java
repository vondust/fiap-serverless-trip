package br.com.fiap.trip.serverless.handler;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import br.com.fiap.trip.datastore.TripDatastore;
import br.com.fiap.trip.datastore.entity.Trip;
import br.com.fiap.trip.serverless.model.HandlerRequest;
import br.com.fiap.trip.serverless.model.HandlerResponse;

public class SearchTripByDate implements RequestHandler<HandlerRequest, HandlerResponse> {

	private TripDatastore repository = new TripDatastore();

	@Override
	public HandlerResponse handleRequest(final HandlerRequest request, final Context context) {
		final String starts = request.getQueryStringParameters().get("starts");
		final String ends = request.getQueryStringParameters().get("ends");

		context.getLogger().log("Searching for registered trips between " + starts + " and " + ends);

		final List<Trip> trips = this.repository.findByPeriod(starts, ends);

		if (trips == null || trips.isEmpty())
			return HandlerResponse.builder().setStatusCode(404).build();

		return HandlerResponse.builder().setStatusCode(200).setObjectBody(trips).build();
	}
}
