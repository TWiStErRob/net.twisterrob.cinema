package net.twisterrob.cinema.gapp;

import java.util.List;

import org.slf4j.*;

import com.google.gson.*;
import com.twister.cineworld.exception.*;
import com.twister.cineworld.model.json.*;
import com.twister.cineworld.model.json.data.*;
import com.twister.cineworld.model.json.request.*;
import com.twister.cineworld.model.json.response.BaseListResponse;

public class CineworldAccessor {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(CineworldAccessor.class);

	private final JsonClient m_jsonClient;

	public CineworldAccessor() {
		Gson gson = new GsonBuilder() //
				.registerTypeAdapter(CineworldDate.class, new CineworldDateTypeConverter()) //
				.create();
		m_jsonClient = new JavaNetURLJsonClient(gson);
	}

	public List<CineworldFilm> getAllFilms() throws ApplicationException {
		FilmsRequest request = new FilmsRequest();
		request.setFull(true);
		List<CineworldFilm> list = getList(request);
		return list;
	}
	public List<CineworldCinema> getAllCinemas() throws ApplicationException {
		CinemasRequest request = new CinemasRequest();
		request.setFull(true);
		List<CineworldCinema> list = getList(request);
		return list;
	}

	private <T extends CineworldBase> List<T> getList(final BaseListRequest<T> request) throws ApplicationException {
		BaseListResponse<T> response = this.m_jsonClient.get(request.getURL(), request.getResponseClass());
		if (response.getErrors() != null && !response.getErrors().isEmpty()) {
			throw new InternalException("Errors in JSON response: %s", response.getErrors());
		}
		return response.getList();
	}

	@SuppressWarnings("unused")
	private <T extends CineworldBase> T getSingular(final BaseListRequest<T> request, final Object parameter)
			throws ApplicationException {
		List<T> list = getList(request);
		if (list.isEmpty()) {
			throw new InternalException("No results for request");
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new InternalException("Multiple %s returned for parameter=%s", request.getRequestType(), parameter);
		}
	}

}