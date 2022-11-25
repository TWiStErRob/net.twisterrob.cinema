## Development

### Setup
* node (`nvm use <version from package.json>`)
   * npm (`npm install --global npm@<version from package.json>`)
* heroku (`npm install --global heroku`) or [any other way](https://devcenter.heroku.com/articles/heroku-cli#download-and-install)
 * git
    * `git --work-tree=Heroku remote add heroku https://git.heroku.com/twisterrob-cinema.git`
    * `heroku login` (otherwise fetch and push fails)
    * `git fetch -a`
 * `git clone https://github.com/TWiStErRob/net.twisterrob.cinema.history.git history`
 * `scripts/env.bat` (or copy contents to global vars)

### Running

`frontend` continuously builds into `deploy/frontend` via `npm start`
`backend` serves from `deploy/frontend` via `npm start`
`test` contains UI tests, [README.md](test/README.md) for more.

### Entry points

 * [:backend:endpoint Main.kt](backend/endpoint/src/main/kotlin/net/twisterrob/cinema/cineworld/backend/Main.kt): web server; `backend` for planner, serving `deploy/frontend` folder as content
 * `frontend/src/planner/pages/index.html`: root of planner page
 * `frontend/src/planner/scripts/index.js`: root of planner webapp logic
 * [:backend:sync Main.kt](backend/sync/src/main/kotlin/net/twisterrob/cinema/cineworld/sync/Main.kt): scheduled updater for database

## Configuration
 
`NEO4J_URL` and `PORT` must be configured, see [env.bat](scripts/env.bat) for example.

## Deployment

Deployment is done via `:deploy:appengine` Gradle module.
 * `gradlew :deploy:appengine:appengineStage` will build everything into `deploy/appengine/build/staged-app` folder.
 * `gradlew :deploy:appengine:appengineDeploy` will upload the staged files to Google App Engine.

On GitHub Actions there are multiple triggers for `Deploy to Google App Engine` workflow:
 * Every branch is deployable with [manual dispatch](https://github.com/TWiStErRob/net.twisterrob.cinema/actions/workflows/Deploy%20to%20Google%20App%20Engine.yml).
 * Every PR is automatically deployed to a unique URL.
 * Every push to `release` branch is automatically deployed to production.

Push code to deploy to `origin/release` branch, or execute "Release from master" workflow from GitHub Actions.

All deployments are visible at [Google Cloud Console - Versions](https://console.cloud.google.com/appengine/versions?project=twisterrob-cinema).

Manage the app at [Google Cloud Dashboard](https://console.cloud.google.com/appengine?project=twisterrob-cinema).

In domain: `cinema CNAME ghs.googlehosted.com.`

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
