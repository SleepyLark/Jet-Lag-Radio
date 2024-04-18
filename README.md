# Jet Lag Radio
Whether you're on the air or offline, it will get to you eventually

# Overview
This is a collection of whatever material I can get about [Jet Set Radio Live](https://jetsetradio.live) with the main goal being preservation and helping people find songs.

Currently, here's the resources I have:
- **Dump of each stations** (https://archive.org/details/JSRL-station-08312020)
  - For offline listening.  Because of how slow Archive.org is for uploading it's hard to constantly maintain.
- **M3U files for online streaming**
  - Streams the mp3 file directly from the site for each station. Currently doesn't have song length information.
- **Direct Download Links for each station** (.csv and .txt)
  - For people who only want to download certain songs or in bulk using a downloader app.
- **Custom cover art for each station**
  - Most of the MP3 files don't have any metadata, so I made some album covers using the wallpaper background for each station.
- **Plain text and CSV listing of each song and artist**
- **Support for [jetsetradiofuture.live](https://jetsetradiofuture.live/)**
  

# Issues
I don't know how often the site gets updated so some songs may be added, removed, or changed later on in the future.  It's also dependent on the site staying online and that nothing changes with the page's source code that would break how files are accessed.

The program I've been using to quickly get all this information was something I slapped together in Java using my limited programming knowledge, it's currently a mess and probably won't be useful as there are far better alternatives.

Also, I should mention that "Jet Mash Radio" is whack due to using Russian characters in its file name which is then used as the song's URL so it ends up not encoding well.  

# Credits
* The mysterious **Professor K** for collecting and hosting these songs on his website.
* **[Mcdonoughd](https://github.com/Mcdonoughd) for hosting [jetsetradiofuture.live](https://jetsetradiofuture.live/) and helping to *SAVE THE FUTURE*.
* **[sks316](https://github.com/sks316/JSRLoader) for making a really useful downloader script.
* And listeners like **YOU**, thank you.

## Other repos to check out
* [RudiesGarage](https://github.com/RudiesGarage) - has a scraper tool for JSRL and a copy of the source code for jetsetradiofuture.live
* [JSRLoader](https://github.com/sks316/JSRLoader) - a python script that can download songs from the site directly and quickly
