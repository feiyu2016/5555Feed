#Feed

**A beautiful reader for your favorite RSS-feeds.**

##Clone repository and generate local files##

```bash
git clone https://github.com/fjeld/Feed.git   # Clone the repository to your computer
cd Feed                                       # Go to the project directory
android update project -p .                   # Generate local files for the project
```

##Command-line debugging and building##

Clone [this](https://gist.github.com/fjeld/1952fad17ea27cd23db8) gist with `git clone https://gist.github.com/fjeld/1952fad17ea27cd23db8`, 
and follow the instructions in android_debug.py.

##TODO##

- Update the database on new version.
- Implements parsing for atom-feeds
- Fix all imports (remove dotASTERIX's)
- Clean up field names in all .java/.xml
- Clean up / add comments in all .java/.xml
