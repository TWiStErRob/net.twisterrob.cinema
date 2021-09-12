## Development

### Setup
* node (`nvm use <version from package.json>`)
   * npm (`npm install --global npm@<version from package.json>`)
* heroku (`npm install --global heroku`) or [any other way](https://devcenter.heroku.com/articles/heroku-cli#download-and-install)
 * git
    * `git --work-tree=Heroku remote add heroku https://git.heroku.com/twisterrob-cinema.git`
    * `heroku login` (otherwise fetch and push fails)
    * `git fetch -a`
 * `scripts/env.bat` (or copy contents to global vars)

### Running

`frontend` continuously builds into `deploy` via `npm start`
`backend` serves from `deploy` via `npm start`
`test` contains UI tests, [README.md](test/README.md) for more.

### Entry points

 * [:backend:endpoint Main.kt](backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/Main.kt): web server; backend for planner, serving deploy folder as content
 * `frontend/src/planner/pages/index.html`: root of planner page
 * `frontend/src/planner/scripts/index.js`: root of planner webapp logic
 * [:backend:sync Main.kt](backend/sync/src/main/kotlin/net/twisterrob/cinema/cineworld/sync/Main.kt): scheduled updater for database

## Configuration
 
`NEO4J_URL` and `PORT` must be configured, see [env.bat](scripts/env.bat) for example.

## Deployment

See [Heroku App Manifest](app.json) ([docs](https://devcenter.heroku.com/articles/app-json-schema)) for more info on what environment is running the app.

Use `scripts/deploy*`.

See also https://dashboard.heroku.com/apps/twisterrob-cinema/settings.

## Debug Production

1. Redirect localhost:
   1. Disable Antivirus
   2. edit system32/drivers/etc/hosts to have `127.0.0.1	cinema.twisterrob.net`
   3. Enable Antivirus
2. Set env:
   * NODE_ENV: production
   * PORT: 80
   * NEO4J_URL: ...
3. run `npm start` in `frontend` (in the background)
4. Debug `src\main\javascript\index.js` in `backend` folder
