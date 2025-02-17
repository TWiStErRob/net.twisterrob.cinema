# Release process

1. Verify [Website][website] works.
2. Run [Release script][release].
3. Smoke test [Website][website].
4. Check [Cloud services][cloud].

[cloud]: https://console.cloud.google.com/appengine/versions?project=twisterrob-cinema&serviceId=default
[release]: https://github.com/TWiStErRob/net.twisterrob.cinema/actions/workflows/planner-release.yml
[website]: https://cinema.twisterrob.net/planner
