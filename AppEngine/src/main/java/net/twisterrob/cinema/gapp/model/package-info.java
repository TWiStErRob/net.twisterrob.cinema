@XmlJavaTypeAdapters({
/*	*/@XmlJavaTypeAdapter(type = Key.class, value = KeyAdapter.class),
/*	*/@XmlJavaTypeAdapter(type = Map.class, value = MapAdapter.class),
/*	*/@XmlJavaTypeAdapter(type = DateTime.class, value = DateTimeAdapter.class),
/*	*/@XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class),
/*	*/@XmlJavaTypeAdapter(type = LocalTime.class, value = LocalTimeAdapter.class),
/*	*/@XmlJavaTypeAdapter(type = LocalDateTime.class, value = LocalDateTimeAdapter.class)})
package net.twisterrob.cinema.gapp.model;

import java.util.Map;

import javax.xml.bind.annotation.adapters.*;

import net.twisterrob.utils.datastore.KeyAdapter;
import net.twisterrob.utils.jaxb.MapAdapter;
import net.twisterrob.utils.joda.*;

import org.joda.time.*;

import com.google.appengine.api.datastore.Key;
