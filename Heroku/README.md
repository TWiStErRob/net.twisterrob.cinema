## Development

## Running

`frontend` continuously builds into `deploy` via `npm start`
`backend` serves from `deploy` via `npm start`

## Entry points

 * `backend/src/main/javascript/index.js`: web server; backend for planner, serving deploy folder as content
 * `frontend/src/planner/pages/index.html`: root of planner page
 * `frontend/src/planner/scripts/index.js`: root of planner webapp logic
 * `updateFromCineworldCinemas.js`: scheduled updater for database
 * `updateFromCineworldFilms.js`: scheduled updater for database

## Configuration
 
`NEO4J_URL` and `PORT` can be configured.

## Deployment

Use `scripts/deploy*`

`heroku config:set NPM_CONFIG_PRODUCTION=false` was set in order to install `devDependencies` on Heroku, so `npm build` runs `webpack` correctly.
