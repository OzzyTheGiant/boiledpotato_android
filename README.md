![](https://github.com/OzzyTheGiant/boiledpotato_android/blob/master/app/src/main/res/mipmap-xxhdpi/ic_launcher.png?raw=true) 
# Boiled Potato
A sample recipe search app using Spoonacular REST API and built natively for Android

Boiled Potato is an app built with Kotlin and Android Architecture Components. 
This app is meant to showcase my Android development skills and you may use this as
a reference when building your own apps.

> **Note**: If you pull this repository, you will need an API key from Spoonacular API,
> which you will then copy and paste into your **local.properties** file as:
> `webApiKey=yourPersonalApiKey`
>
> You can get access to this API at [rapidapi.com]

### Android Compatibility

**Minimum SDK: 21** - Android 5.0 Lollipop

**Target SDK: 28** - Android 9.0 Pie


### Features
The main activity will show a simple search box at the top of the page and
at some buttons that you can toggle at the bottom for filtering search results 
by cuisine type. Additionally, there is a Favorites button at the top right corner.

Once you hit the search button, the search results activity will begin by displaying
recipes, which will be retrieved from a local SQLite database using Room
or from the web API if no results found in database. If you clicked the Favorites
button in the previous activity, a list of favorite recipes will show up.

Upon clicking a recipe, you will view the recipes details such as name, servings
and preparation time. It will load up ingredients and instructions from API
if viewing recipe for the first time. You can mark this recipe as a favorite.