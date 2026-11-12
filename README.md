# AndroidApp

An Android app written in Java. It shows a weather forecast for a city from the OpenWeather forecast API (`https://api.openweathermap.org/data/2.5/forecast`). The app lists one entry for each 3-hour step that the API returns.

The text is in English and German. The app uses the device language and falls back to English for any other language.

## Screens

- `MainActivity` lists the forecast entries in a `RecyclerView`. Each row shows a weather icon, the date and time in UTC, and the temperature.
- `DetailActivity` opens when you tap a row. It shows the condition, date and time, temperature, pressure, humidity, cloud cover, wind speed and direction, and the rain and snow for the last 3 hours.
- `SettingsActivity` has a city field and a checkbox for the metric system. When the checkbox is off, the app requests imperial units.

Changing the city or the unit in the settings loads the forecast again. `MainActivity` writes "Wien" and the metric setting into the saved preferences in `onCreate`, so the app starts with those values each time it launches.

## Stack

- Java with AndroidX AppCompat 1.8.0, Material Components 1.14.0, ConstraintLayout 2.2.2, AndroidX Preference 1.2.1 and Navigation 2.10.0.
- Android Gradle Plugin 9.4.0 and Gradle 9.7.1 through the Gradle wrapper.
- `minSdkVersion` 24, `targetSdkVersion` 30 and `compileSdk` 36, from `BSAWeatherApp/app/build.gradle`.

The OpenWeather API key is a string in `loadWebResult()` in `MainActivity.java`. The app does not read it from a configuration file.

## Build and run

1. Open the `BSAWeatherApp` folder in Android Studio. That folder contains `settings.gradle`, so the repository root is not the project root.
2. Let Gradle sync. If it reports a missing SDK, set `sdk.dir` in `BSAWeatherApp/local.properties` to your Android SDK path. The committed file points to the author's machine.
3. Run the `app` configuration on an emulator or device with Android 7.0 (API 24) or later. The app needs network access.

From the command line, run `./gradlew :app:assembleDebug` in `BSAWeatherApp`.

## Tests

`ExampleUnitTest` asserts `2 + 2`, and `ExampleInstrumentedTest` checks the package name. Both come from the Android Studio project template, and neither tests the app code. Run the unit tests with `./gradlew :app:testDebugUnitTest` in `BSAWeatherApp`.
