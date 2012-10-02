DROP TABLE IF EXISTS Cinema;
CREATE TABLE IF NOT EXISTS Cinema (
	_id          INTEGER,
	name         NVARCHAR    NOT NULL
	                         UNIQUE ON CONFLICT FAIL,
	url          NVARCHAR,
	address      NVARCHAR,
	postcode     NVARCHAR,
	telephone    NVARCHAR,
	latitude     REAL,
	longitude    REAL,
	PRIMARY KEY(_id)
);

--	                    CONSTRAINT fk_performance_cinema
--	                    REFERENCES Cinema (_id)
--                            ON DELETE CASCADE
--                            ON UPDATE CASCADE