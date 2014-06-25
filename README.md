# Feed #
## A beautiful reader for your favorite RSS-feeds. ##

-----------------------------------------------------

**Clone repository and generate local files**

```bash
git clone https://github.com/fjeld/Feed.git   # Clone the repository to your computer
cd Feed                                       # Go to the project directory
android update project -p .                   # Generate local files for the project
```

**Command-line debugging and building**

Clone [this](https://gist.github.com/fjeld/9989959) gist with `git clone https://gist.github.com/fjeld/9989959`, and  
follow the instructions in android_debug.sh.

**Todo**

- Replace ArrayList for "Read items" with HashSet
- Implement image caching
- Implements parsing for atom-feeds
- Setting for automatic 'Mark all as read'
- Fix all imports (remove dotASTERIX's)
- Clean up field names in all .java/.xml
- Clean up / add comments in all .java/.xml
