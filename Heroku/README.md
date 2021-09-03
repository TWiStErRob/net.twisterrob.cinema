## Development

### Setup
 * git
    * `git init .`
    * `git remote add heroku https://git.heroku.com/twisterrob-cinema.git`
    * `git fetch -a`
 * node (`nvm use <version from package.json>`)
 	* npm (`npm install --global npm@<version from package.json>`)
 * heroku (`npm install --global heroku`)
    * `heroku keys:add ~/.ssh/id_rsa.pub` (see https://stackoverflow.com/a/6059231)
 * `scripts/env.bat` (or copy contents to global vars)

### Running

`frontend` continuously builds into `deploy` via `npm start`
`backend` serves from `deploy` via `npm start`
`test` contains UI tests, [README.md](test/README.md) for more.

### Entry points

 * `backend/src/main/javascript/index.js`: web server; backend for planner, serving deploy folder as content
 * `frontend/src/planner/pages/index.html`: root of planner page
 * `frontend/src/planner/scripts/index.js`: root of planner webapp logic
 * `updateFromCineworldCinemas.js`: scheduled updater for database
 * `updateFromCineworldFilms.js`: scheduled updater for database

## Configuration
 
`NEO4J_URL` and `PORT` can be configured, see [env.bat](scripts/env.bat) for example.

## Deployment

Use `scripts/deploy*`

`heroku config:set NPM_CONFIG_PRODUCTION=false` was set in order to install `devDependencies` on Heroku, so `npm build` runs `webpack` correctly.
`heroku config:set NODE_ENV=production` was set automatically by Heroku
`heroku config:set NEO4J_URL=https://production:password@....dbs.graphenedb.com:24780` was set to use the right database

see also https://dashboard.heroku.com/apps/twisterrob-cinema/settings

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
