"p:\tools\vcs\git\bin\sh.exe" -l -c "eval `ssh-agent` && ssh-add ~/.ssh/heroku_rsa && git add --all && git commit -m 'Deploy' && git push -f heroku master && heroku ps:scale web=1 && sh -l -i"
