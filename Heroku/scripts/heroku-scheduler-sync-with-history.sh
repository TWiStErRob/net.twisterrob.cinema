#!/usr/bin/env bash
set -e
SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &> /dev/null && pwd)

# Set up GitHub authentication.
eval "$(ssh-agent -s)"
# Need to make sure no-one else can access this file, otherwise the following error comes from `ssh-add`:
# > @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# > @         WARNING: UNPROTECTED PRIVATE KEY FILE!          @
# > @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# > Permissions 0777 for 'sync-key.private' are too open.
# > It is required that your private key files are NOT accessible by others.
# > This private key will be ignored.
# Note running this script on WSL might require additional setup:  https://stackoverflow.com/a/50856772/253468
# `[automount]\noptions="metadata"` in /etc/wsl.conf, and then `wsl --shutdown` from cmd.
if [ "$HEROKU_ENV" == "1" ]; then
  echo "$SYNC_KEY" > sync-key.private
fi
chmod --verbose 600 sync-key.private
ssh-add sync-key.private
# Need to make sure SSH can connect to host, otherwise it fails with:
# > The authenticity of host 'github.com (140.82.121.4)' can't be established.
# > ECDSA key fingerprint is SHA256:p2QAMXNIC1TJYWeIOttrVc98/R1BUFWu3/LiyKgUfQM.
# Workaround: https://superuser.com/a/1111974/145861
ssh-keyscan -t rsa -H github.com >> ~/.ssh/known_hosts

# Get a clean clone.
rm --force --recursive build/sync
git clone --depth 1 git@github.com:TWiStErRob/net.twisterrob.cinema.history.git build/sync

# Prepare clone for execution.
rm --verbose --force build/sync/weekly_film_times.xml
rm --verbose --force build/sync/weekly_film_times_ie.xml
rm --verbose --force build/sync/weekly_film_times.log

# Execute sync.
("${SCRIPT_DIR}/heroku-scheduler-sync.sh" || true) | tee build/sync/weekly_film_times.log

# Save and publish execution output.
cd build/sync
git add .
git config user.email "heroku-scheduler+sync@twisterrob.net"
git config user.name "Heroku Scheduler"
git commit -m "Heroku Scheduler: Sync"
git push
