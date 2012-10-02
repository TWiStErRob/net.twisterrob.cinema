CREATE TABLE IF NOT EXISTS Cinema (
	_id            INTEGER,
	name         NVARCHAR    NOT NULL,
	url          NVARCHAR    NOT NULL,
	address      NVARCHAR,
	postcode     NVARCHAR,
	telephone    NVARCHAR,
	latitude     REAL,
	longitude    REAL,
	PRIMARY KEY(_id AUTOINCREMENT)
);

--	                    CONSTRAINT fk_performance_cinema
--	                    REFERENCES Cinema (_id)
--                            ON DELETE CASCADE
--                            ON UPDATE CASCADE