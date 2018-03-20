# SimpleRSS
SimpleRSS - small and simple example app for reading RSS from different sources. I spent ~ 7 evenings to make it.
It`s just an example and "training field" for testing some technologies. If something did not work - create issue request.

![screenshoot](https://github.com/GreyLabsDev/SimpleRSS/blob/master/scr.jpg)

### Features
* Adding multiple RSS sources for reading
* Storing downloaded news items todatabase
* Searching in downloaded news by keyword in title,description or source name
* Reading full versions of downloaded news (link to browser)
* Cleaning all downloaded news and sources

### Used technologies
* [OkHttp3](https://square.github.io/okhttp/) - for network use
* [Room ORM](https://developer.android.com/training/data-storage/room/index.html) - for acces to database, reading, writing and searching
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata.html) - for reactive UI (updating RecyclerView items after geting full response)
