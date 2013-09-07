package net.twisterrob.cinema.gapp.rest.model;

import javax.xml.bind.annotation.*;

import net.twisterrob.cinema.gapp.model.Dateable;

@XmlRootElement
public class RestResponse {
	@XmlElement
	private Status status;
	@XmlElement
	private Dateable payload;

	/**
	 * This method is a gift to JAXB.
	 */
	@SuppressWarnings("unused")
	private RestResponse() {}

	public RestResponse(ServiceStatus status, Dateable payload, String message, Object... messageParams) {
		this.status = new Status(status, String.format(message, messageParams));
		this.payload = payload;
	}
	public Status getStatus() {
		return status;
	}

	public Dateable getPayload() {
		return payload;
	}

	private static class Status {
		private ServiceStatus status;
		private String message;

		/**
		 * This method is a gift to JAXB.
		 */
		@SuppressWarnings("unused")
		private Status() {}

		public Status(ServiceStatus status, String message) {
			this.status = status;
			this.message = message;
		}

		@XmlAttribute
		public ServiceStatus getStatus() {
			return status;
		}

		@XmlElement
		public String getMessage() {
			return message;
		}
	}

	public static enum ServiceStatus {
		Success,
		Failure,
		Error;
	}
}
