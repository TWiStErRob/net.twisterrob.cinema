## Backup
1. https://app.graphenedb.com/dbs
1. Select database
1. Admin
1. Export database, Export database in dialog
1. Download in dialog, close

## Cleanup
1. Launch Neo4J Browser
1. `match (f:Film) where not (f)--() return f`
1. If the result shows a warning, don't be alarmed, it's just display, the data is returned:  
    > Not all return nodes are being displayed due to Initial Node Display setting. Only 300 of 300 nodes are being displayed
1. Click Export icon and select "Export CSV"
1. Verify downloaded file has rows
1. Save downloaded CSV as `Cineworld Cinema\Database\YYYYMMDD unconnected Films.csv`
1. `match (f:Film) where not (f)--() delete f`
1. `match (f:Film) where not (f)--() return f` to verify empty
1. Favorites > Data Profiling > What kind of nodes exist  
   Remove `WHERE rand() <= 0.1` clause

## Upgrade Neo4J
1. Do a Backup (see above)
1. "Upgrade / Clone"
1. Select AWS Region EU (Ireland)
1. Select Hobby > Sandbox (Free)
1. Name `cinema_prod_ver` (e.g. `cinema_prod_349`)
1. Do a Cleanup (see above)  
   *You have 5 minutes to complete this if database is full.* Otherwise it enters read-only mode:  
   > Your database has exceed the storage limit on the plan and has been switched to read-only mode.
   and you have to do the steps under "DB full"
1. Go to "Connection" tab
1. Update Bookmark with HTTPS url
1. Create `production` user and copy password to Bookmark
1. If it becomes read-only even thought it's < 100% full, do an export/restore cycle in "Admin"
1. Update `tools/data/neo4j` from https://neo4j.com/download-center/#releases (Community Server > Windows > ZIP)

## DB Full
> Your sandbox database `...` has reached its maximum storage capacity.
We have switched your database to read-only mode.

Folders:
 * Backups: `projects/workspace/Cineworld Cinema/Database`
 * Local database: `tools/data/neo4j/data/databases/graph.db`

1. Do a Backup (see above)
1. Extract backup to local database folder
1. Launch `neo4j console` from `tools/data/neo4j/bin`
   Make sure that `JAVA_HOME` is Java 8 for (neo4j 3.x)
   Make sure that `NEO4J_HOME` points to the same distribution from where `neo4j.bat` is executed
1. > Remote interface available at http://localhost:7474/browser/
1. Do a Cleanup (see above)
1. <kbd>CTRL+C</kbd> to stop `neo4j`
1. ZIP contents of local database folder
1. Save backup and ZIP to backups
1. Go to "Admin" tab
1. Restore database
1. Go to the Overview tab of the database in GraphebeDB to verify it's not read-only any more.
1. Run `Cineworld Cinema\Heroku\scripts\update-all.remote.bat`
