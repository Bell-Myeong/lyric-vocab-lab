# lyric-vocab-lab

Android app for learning Japanese vocabulary from song lyrics.

## Overview

`lyric-vocab-lab` lets you:

- keep a list of songs (title + artist),
- open a song and fetch lyrics from a public lyrics site,
- tokenize lyrics with Kuromoji,
- review extracted words and check meanings through Jisho API.

The app is implemented with classic Android Views and Java.

## Features

- Song list screen with add-song dialog
- Lyrics lookup from web source (`j-lyric.net`)
- Word extraction from lyrics (Kuromoji tokenizer)
- Word meaning lookup (`jisho.org` API)
- Bottom sheet UI for meaning details

## Tech Stack

- Language: Java 11
- Android: compileSdk 35, minSdk 27, targetSdk 35
- UI: AppCompat, Material Components, RecyclerView, BottomSheetDialog
- Networking/Parsing:
  - OkHttp
  - Gson
  - Jsoup
  - Kuromoji (IPADIC)

## Project Structure

```text
app/src/main/java/com/example/project/
  MainActivity.java
  SongAdapter.java
  Song.java
  LyricsAnalysisActivity.java
  WordLearningActivity.java
```

Main flow:

1. `MainActivity` -> choose/add song
2. `LyricsAnalysisActivity` -> fetch lyrics + extract words
3. `WordLearningActivity` -> tap word to load meaning

## Getting Started

## Prerequisites

- Android Studio (recent version)
- JDK 11
- Internet connection (required for lyrics and dictionary lookup)

## Run in Android Studio

1. Open this folder in Android Studio.
2. Sync Gradle.
3. Run the `app` configuration on an emulator/device.

## Build from terminal

Windows:

```powershell
.\gradlew.bat assembleDebug
```

macOS/Linux:

```bash
./gradlew assembleDebug
```

## Notes

- The app depends on external websites/APIs. If those services change or rate-limit requests, some features may stop working.
- `INTERNET` permission is required and declared in `app/src/main/AndroidManifest.xml`.

## License

No license file is included yet. Add a `LICENSE` file if you plan to share or reuse this project publicly.

