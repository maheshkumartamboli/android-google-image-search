Android Google Image Search
===

A bare-bones Android application showcasing the [Google Image Search API](https://developers.google.com/image-search/).

This is an Android equivalent of a [web app I built](https://github.com/Trindaz/google-image-search) using the same Google Image Search API.

Screenshots
---

![Android Google Image Search Screenshot](https://raw.githubusercontent.com/Trindaz/android-google-image-search/master/docs/android-google-image-search-screenshot-1.png "Android Google Image Search Screenshot")

Installation
---

```bash
git clone https://github.com/Trindaz/android-google-image-search.git
```

Implementation Notes
---
- [Android Query](https://code.google.com/p/android-query/) used for HTTP requests
- Android caching used instead of Android Query caching for images
- No OrderingBuffer needed as with [web app equivalent](https://github.com/Trindaz/google-image-search); In the case of Android only an [integer representing total number of initial contiguous results](https://github.com/Trindaz/android-google-image-search/blob/6413471b9cf0d2a25ebac706f9d37df029613d07/src/com/hellotext/googleimagesearch/ImageAdapter.java#L24) was needed. The difference in design was due to the fact that in a backbone application you cannot store HTTP results in the same list used to store search results for display (neatly) without them also being rendered immediately, as you can thanks to ImageAdapter in this Android app.
