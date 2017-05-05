# WeatherWidget
A Weather Widget for Android using OpenWeatherMap and TimeZoneDB APIs
![WeatherWidget](https://github.com/AKouki/WeatherWidget/blob/master/weather_widget.png)

## Features
* 5 day weather forecast
* Current local time for selected cities (GMT Offset is saved locally)
* Ability to select temperature unit, Celsius or Fahrenheit
* Automatic weather update interval: 30 minutes
* Automatic clock update interval: 1 minute
* Different icons/background for Day/Night

## Getting Started
In order to use this code, you need to obtain an API KEY from [OpenWeatherMap](http://openweathermap.org/) and [TimeZoneDB](https://timezonedb.com)

### First Build
1. Grab the source code
2. Open Android Studio and load the project
3. Open `WeatherFetchTask.java`, `TimeZoneFetchTask.java` and `PlaceApiTask.java` <br> and replace the `API_KEY` String variable with your own Key
4. That's all... Build the Project!
