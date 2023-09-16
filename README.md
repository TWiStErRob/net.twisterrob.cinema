
# net.twisterrob.cinema
Mostly Cineworld related hacks. Below is a historical-order description of the evolution.

## Motivation
If you're wondering why I would go through so many iterations of hacking around Cineworld,
it's because I like watching movies and I had a Cineworld Unlimited card (watch as many movies as you want for about 2 ticket prices per month).
I was hand-planning double-bills initially, but it got tedious and wanted to have some fun coding and learning, so I tried and tried.

## android
First try from way back, before my occupation was Android Engineer. It was just a basic ~C~**R**~UD~ app, with a funny map.

## AppEngine + Models/Cineworld
Round two, a year later, a DAO layer in Java with direct Quickbook JSON calls + a web UI rendering a table with JQuery, never really worked out.

## Heroku (Node)
Started out as a backend migration from AppEngine (Java) to Heroku (Node.js) wrapping a Neo4J graph database.
Later evolved into a scheduled synchronization from Cineworld Quickbook into the Neo4J database and a middleware BFF to save favourites.

## Heroku (planner)
Building on the previous migration, I rewrote the frontend from JQuery to Angular JS (the v1 and only at the time).
Additionally added the capability to select multiple films and cinemas and get a plan to watch as many movies as possible in a row in one cinema.

## Heroku (OGM)
Having an unmaintained ORM library in NodeJS and the lack of strong typing prompted me to migrate the Neo4J DAO layer from Node.js to Neo4J Java ORM in a Kotlin ktor app keeping the same JSON contract as before.
Also, the synchronization mechanism of the Cineworld API was broken, so I rewrote the sync to work on the Cineworld Syndication Feed (XML).

## Current state
Sadly the Syndication feed of Cineworld is as good as dead since their recent redesign of the website and mobile app.
The Quickbook API with access key is also not available anymore. At this point this app is pretty much defunct unless I find a data source for it.
At this point (2022 November), even [Google](https://www.google.com/search?q=cineworld+wood+green) struggles with the show times showing "No showings" for most days.
The database is still populated with random data, just for demonstration purposes.

**Visit [The planner](https://cinema.twisterrob.net/planner) and click "All" above "New Films" to see what this is.**

## map
A few hour-long hack in Kotlin Scripting to consume the cinema listing of Cineworld and generate a .kml file from it, so it can be imported in Google Maps.
