[![](https://jitpack.io/v/bscal/MCSeasons.svg)](https://jitpack.io/#bscal/MCSeasons)
### MCSeasons
Minecraft 1.18-1.19 Fabric mod that adds different seasons and seasonal effects to the game. <br>

### Features
* There are 4 seasons per year (Spring, Summer, Autumn, Winter).
* You start on the 0th(?) of Spring, by default each season is 30 Minecraft days.
* Each season brings different effects, both visually and mechanically.
* Different biomes can have different season types. For example:
  * Plains has generic (Spring, Summer, Autumn, Winter)
  * Jungle might have (Wet, Wet, Dry, Dry)
  * Desert might have (Spring, Extreme Summer, Autumn, Warm Winter)
* Time is tracked through in-game time. So `/time set|add` works correctly.
* Leaves fall from leaf blocks, dynamic with seasons and weather
* There is only 1 global clock based off the Overworld.
* `Season Clock` item that can help you keep track of what season it is

## Usage
#### Install

Place `Seasons.jar` into your mods folder.

Seasons are somewhat customizable<br>
There are 2 configs, client-config.conf and server-config.conf

#### Commands

    /seasonsc info                  - Prints season and date in chat
    /seasons debug setlevel [0-2]   - Sets debug info (0 - off, 1 - info, 2 - verbose)
    /seasons set [0-3]              - Sets the season (0 - autumn ... 3 - winter)
     
    You can use minecraft's /time command to adjust time

#### Developer
```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modApi("com.github.bscal:MCSeasons:Tag"))
}
```

#### Useful Classes
* `SeasonApi` - Useful util functions<br><br>
* `SeasonTimer` - Season and time state, can be accessed on both server and client<br><br>
* `SeasonWorld` - World events state<br><br>
* `SeasonClimateManager` - Biome climate info<br><br>
