package br.com.fiap.trip.datastore.exception;

public class TripNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TripNotFoundException() {
		super();
	}

	public TripNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TripNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public TripNotFoundException(String message) {
		super(message);
	}

	public TripNotFoundException(Throwable cause) {
		super(cause);
	}
}
