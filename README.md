To run: 

### Set up the auth tokens: 
- Get a Google Places API auth token
- Create a .env file in ~/src/main/resources and add ```GOOGLE_MAPS_API_KEY=[YOUR API KEY]```

- Get a SlackAPI webhook
- Create a config.properties in ~/src/main/resources and add:
```
SLACK_WEBHOOK_URL=YOUR_SLACK_WEBHOOK_URL
SLACK_BOT_TOKEN=YOUR_SLACK_BOT_TOKEN
```

### Run the main class:
```
$ mvn exec:java -Dexec.mainClass="Main"
```
Note what port it's running on, ex.: ```[Thread-2] INFO spark.embeddedserver.jetty.EmbeddedJettyServer - >> Listening on 0.0.0.0:4567```

### Download ngrok: (https://dashboard.ngrok.com/get-started/setup/macos) 

```
$ ngrok http http://localhost:[YOUR PORT] 
```
Note the forwarding address, ex: ```Forwarding https://8145-2601-647-4402-af20-5cd3-563b-d910-1668.ngrok-free.app -> http://localhost:4567```   

### Set up Slack
- Go to your Slack API app.
- Copy and paste the forwarding link (the left link from above, where left link --> rightlink)
- Put it in the slack as the return link for the /route slash command and the interactivity request URL
- Add /route at the end of the link for the command
- Add /slack/interactions at the end of the link for the interactivity

ex: ```https://3d13-76-14-50-136.ngrok-free.app/route```


### Use the app on Slack
Reinstall the app on Slack (refresh as necessary), and run /route 
Type in and use the autofill to find suggestions along your route. 
