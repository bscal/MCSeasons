[![](https://jitpack.io/v/bscal/MCSeasons.svg)](https://jitpack.io/#bscal/MCSeasons)
### MCSeasons
Minecraft 1.18-1.18.2 Fabric mod that adds different seasons to the game.
<br><br>

### Gameplay
Default there are 4 seasons per year (Spring, Summer, Autumn, Winter).<br>
However seasons can be customized and each biome can have a different group of seasons.
For example: (Wet, Wet, Dry, Dry) for a jungle biome.

Time is tracked through in-game time. So `/time set|add` works correctly.

Seasons how their own effects and colors.
* Spring is brighter
* Summer is default colors
* Autumn is browner and leaves are multicolored
* Winter is a gray washed greed

Leaves fall from leaves, dynamic with seasons and weather

Contains a small API and config files to edit and hook into the mod.

Seasons are tied to minecraft time. Different seasons types per biome.

There is only 1 global clock based off the Overworld.

## Usage
#### Install

Place `Seasons.jar` into your mods folder.

####Commands

    /seasonsc info                  - Prints season and date in chat
    /seasons debug setlevel [0-2]   - Sets debug info (0 - off, 1 - info, 2 - verbose)
    /seasons set [0-3]              - Sets the season (0 - autumn ... 3 - winter)
     
    You can use minecraft's /time command to adjust time

## Developer
#### Install
To include the API use:
```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    api(include("com.github.bscal.MCSeasons:api:Tag"))
}
```

If you'd you like to extend Seasons:
```
dependencies {
    modImplementation("com.github.bscal:MCSeasons:Tag")
}
```

### Notes
* Looking at the `SeasonAPI` file show most commonly needed functions.
* Seasons are tracked by an internal id.
* Seasons and SeasonalType are enums containing info on what season it is.
* Time is tracked server side and synced to clients
* Time is based off the Overworld only
* Seasons is a bit unfinished, so it could change, but SeasonAPI should make it more reliable to use.
