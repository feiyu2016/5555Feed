Feed<br>A beautiful reader for your favorite RSS-feeds.
-------------------------------------------------------------------------

For command-line debugging, check out <a href="https://gist.github.com/fjeld/9989959" target="_blank">this</a> bash script.

**Installation via command-line**
```sh
git clone https://github.com/fjeld/Feed.git   # Clone the repository to your computer
cd Feed                                       # Go to the project directory
android update project -p .                   # Generate local files for the project
./android_debug.sh FeedActivity               # Build and execute the app on your device
```

**TODO**
- Implement image caching
- Fix all imports (remove dotASTERIX's)
- Clean up field names in all .java
- Clean up / add comments in all .java/.xml
- Pass activity to classes instead of application
- Parser should handle atom-feeds
