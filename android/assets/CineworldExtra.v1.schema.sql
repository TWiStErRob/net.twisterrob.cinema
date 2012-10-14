BEGIN TRANSACTION;

CREATE TABLE IF NOT EXISTS CinemaCompany (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER,
	name                NVARCHAR           NOT NULL UNIQUE,
	website_url         NVARCHAR           NULL,	
	facebook_profile    NVARCHAR           NULL,
	twitter_name        NVARCHAR           NULL,
	youtube_user        NVARCHAR           NULL,
	PRIMARY KEY(_id)
	--SECONDARY KEY(name)
);

CREATE TABLE IF NOT EXISTS Cinema (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_company            INTEGER            NOT NULL DEFAULT 1
	                                       CONSTRAINT "fk-Cinema-CinemaCompany" REFERENCES CinemaCompany(_id)
	,
	_id                 INTEGER            NOT NULL,
	name                NVARCHAR           NOT NULL UNIQUE,
	details_url         NVARCHAR           NULL,
	territory           CHAR(2)            NOT NULL,
	address             NVARCHAR           NULL,
	postcode            CHAR(7)            NOT NULL,
	telephone           NVARCHAR           NULL,
	latitude            REAL               NULL,
	longitude           REAL               NULL,
	PRIMARY KEY(_company, _id)
	--SECONDARY KEY(name)
);

-- TODO Film can be in multiple categories (Lion King "edi":42157: family, junior)
CREATE TABLE IF NOT EXISTS FilmCategory (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER,
	code                NVARCHAR           NOT NULL UNIQUE,
	name                NVARCHAR           NOT NULL,
	PRIMARY KEY(_id)
	--SECONDARY KEY(code)
);

CREATE TABLE IF NOT EXISTS FilmClassification (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER,
	code                NVARCHAR           NOT NULL UNIQUE,
	name                NVARCHAR           NOT NULL,
	PRIMARY KEY(_id)
	--SECONDARY KEY(code)
);

CREATE TABLE IF NOT EXISTS FilmAdvisory (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER,
	code                NVARCHAR           NOT NULL UNIQUE,
	name                NVARCHAR           NOT NULL,
	PRIMARY KEY(_id)
	--SECONDARY KEY(code)
);

CREATE TABLE IF NOT EXISTS FilmDistributor (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER,
	name                NVARCHAR           NOT NULL UNIQUE,
	PRIMARY KEY(_id)
	--SECONDARY KEY(name)
);

CREATE TABLE IF NOT EXISTS FilmType (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER,
	code                NVARCHAR           NOT NULL UNIQUE,
	name                NVARCHAR           NOT NULL,
	PRIMARY KEY(_id)
	--SECONDARY KEY(code)
);

CREATE TABLE IF NOT EXISTS Film (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER,
	edi                 INTEGER            NULL UNIQUE,
	title               NVARCHAR           NOT NULL,
	details_url         NVARCHAR           NULL,
	poster_url          NVARCHAR           NULL,
	still_url           NVARCHAR           NULL,
	thumbnail_url       NVARCHAR           NULL,
	classification      INTEGER
	                                       CONSTRAINT "fk-Film-FilmClassification" REFERENCES Film_Classification(_id)
	,
	advisory            INTEGER
	                                       CONSTRAINT "fk-Film-FilmAdvisory" REFERENCES Film_Advisory(_id)
	,
	type                INTEGER
	                                       CONSTRAINT "fk-Film-FilmType" REFERENCES Film_Type(_id)
	,
	PRIMARY KEY(_id)
	--SECONDARY KEY(edi)
);

CREATE TABLE IF NOT EXISTS Event (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER,
	code                NVARCHAR           NOT NULL UNIQUE,
	name                NVARCHAR           NOT NULL,
	PRIMARY KEY(_id)
	--SECONDARY KEY(code)
);

CREATE TABLE IF NOT EXISTS PerformanceType (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_id                 INTEGER,
	code                NVARCHAR           NOT NULL UNIQUE,
	name                NVARCHAR           NOT NULL,
	PRIMARY KEY(_id)
	--SECONDARY KEY(code)
);

CREATE TABLE IF NOT EXISTS Performance (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_cinema             INTEGER
	                                       CONSTRAINT "fk-Peformance-Cinema" REFERENCES Cinema(_id)
	,
	_film               INTEGER
	                                       CONSTRAINT "fk-Peformance-Film" REFERENCES Film(_id)
	,
	_date               DATE               NOT NULL,
	_time               TIME               NOT NULL,
	subtitle_lang       CHAR(2)            NULL,
	audio_description   CHAR(2)            NULL, -- NULL=no, ??=yes, en=English, ...
	type                INTEGER
	                                       CONSTRAINT "fk-Peformance-PerformanceType" REFERENCES PerformanceType(_id)
	,
	detailsUrl          NVARCHAR           NULL,
	bookingUrl          NVARCHAR           NULL,
	
	PRIMARY KEY(_cinema, _film, _date, _time)
);

-- Many-to-many tables

CREATE TABLE IF NOT EXISTS Event_Film (
	_event              INTEGER
	                                       CONSTRAINT "fk-Event_Film-Event" REFERENCES Event(_id)
	,
	_film               INTEGER
	                                       CONSTRAINT "fk-Event_Film-Film" REFERENCES Film(_id)
	,
	PRIMARY KEY(_event, _film)
);

-- Helper tables
CREATE TABLE IF NOT EXISTS "Helper:GeoCache" (
	__last_update       DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP,
	_postcode           CHAR(7)            NOT NULL,
	latitude            REAL               NOT NULL,
	longitude           REAL               NOT NULL,
	PRIMARY KEY(_postcode)
);

END TRANSACTION;