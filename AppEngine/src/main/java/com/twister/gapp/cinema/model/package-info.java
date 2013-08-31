@XmlJavaTypeAdapters({
/*	*/@XmlJavaTypeAdapter(type = DateTime.class, value = DateTimeAdapter.class),
/*	*/@XmlJavaTypeAdapter(type = DateMidnight.class, value = DateMidnightAdapter.class),
/*	*/@XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class),
/*	*/@XmlJavaTypeAdapter(type = LocalTime.class, value = LocalTimeAdapter.class),
/*	*/@XmlJavaTypeAdapter(type = LocalDateTime.class, value = LocalDateTimeAdapter.class)})
package com.twister.gapp.cinema.model;

import javax.xml.bind.annotation.adapters.*;

import org.joda.time.*;

import com.twister.utils.joda.*;
