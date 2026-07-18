<div align="center">

# Ancient City Underground 🏛️

**Let Ancient Cities Fill the Underground**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-brightgreen?style=flat-square)](https://minecraft.net)
[![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-dbd0b4?style=flat-square)](https://fabricmc.net)
[![License](https://img.shields.io/badge/License-CC0--1.0-blue?style=flat-square)](LICENSE)

</div>

---

## Introduction

**Ancient City Underground** is a Fabric mod that completely reworks Ancient City generation:

- **All Biomes** — Ancient Cities are no longer restricted to the Deep Dark. They generate in **all Overworld biomes** underground.
- **High Density** — Generation spacing of 10 chunks. With a 128-block city radius, **every chunk is covered** by city structures.
- **Moderate Overlap** — Cities overlap just enough for full coverage without excessive clustering.
- **Safe Spawning** — On first join, players spawn at a **random safe spot inside the nearest city** — a walkway, courtyard, or other open area.
- **Bedrock Roof** — A bedrock layer at Y=60 seals the underground world. No blocks or mobs generate above it.
- **Fully Compatible** — Does not affect End Portals, Strongholds, or any other vanilla structures.

![Ancient City Underground](screenshots/ancient-city.png)

---

## Features

| Feature | Description |
|---------|-------------|
| 🗺️ **All Biomes** | Generate under plains, deserts, forests, oceans, mountains — every Overworld biome |
| 📐 **Optimized Density** | `spacing=10, separation=2`, ~160 block average spacing, 128-block coverage radius |
| 🎲 **Random Spawn** | Each new world spawns you at a different spot — courtyard, walkway, or city gate |
| 🛡️ **Safety Check** | Automatically verifies ground is solid, body and head are in open air |
| 🧱 **Bedrock Roof** | Bedrock layer at Y=60 with no blocks or mobs above — a sealed underground world |
| ⚡ **Lightweight** | Pure datapack overrides + Mixin, no vanilla structure definitions modified |

---

## Requirements

| Component | Version |
|-----------|---------|
| Minecraft | **1.21.11** |
| Fabric Loader | ≥ 0.19.3 |
| Fabric API | ≥ 0.141.4+1.21.11 |
| Java | ≥ 21 |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the mod JAR (from Releases or build it yourself)
4. Place the JAR into `.minecraft/mods/`
5. Launch the game and **create a new world**

---

## Technical Details

### Implementation

| Mechanism | Method |
|-----------|--------|
| **All-biome generation** | Override `data/minecraft/tags/worldgen/biome/has_structure/ancient_city.json` referencing `#minecraft:is_overworld` |
| **Generation density** | Override `data/minecraft/worldgen/structure_set/ancient_city.json` with `spacing=10, separation=2` |
| **Player spawn** | Mixin into `PlayerList#placeNewPlayer` to find the nearest city and pick a random safe position |

### Structure Set Parameters

```json
{
    "placement": {
        "type": "minecraft:random_spread",
        "spacing": 10,
        "separation": 2,
        "salt": 191119
    }
}
```

| Parameter | Value | Description |
|-----------|-------|-------------|
| `spacing` | 10 | Grid size in chunks. One city attempt per 10×10 chunk cell |
| `separation` | 2 | Minimum distance from cell boundary in chunks |
| Min center distance | 4 chunks (64 blocks) | Closest two cities can get |
| Max center distance | 16 chunks (256 blocks) | Farthest two cities can be (edges just touch) |
| Coverage radius | 8 chunks (128 blocks) | Vanilla `max_distance_from_center`, unchanged |

---

## Build from Source

```bash
git clone https://github.com/xkk-112358/Minecraft-mod-ancient-city-underground.git
cd Minecraft-mod-ancient-city-underground

# Windows
gradlew build

# Linux / macOS
./gradlew build
```

Output in `build/libs/`:
- `ancient-city-underground-<version>.jar` — runnable mod
- `ancient-city-underground-<version>-sources.jar` — source code

---

## Project Structure

```
src/
├── main/
│   ├── java/com/example/
│   │   ├── TemplateMod.java                    # Mod initializer + SERVER_STARTED event
│   │   └── mixin/
│   │       ├── ExampleMixin.java               # Example Mixin
│   │       └── PlayerListMixin.java            # Player spawn injection
│   └── resources/
│       ├── fabric.mod.json                     # Mod metadata
│       ├── anciencity.mixins.json              # Mixin config
│       ├── assets/anciencity/image.png          # Mod icon
│       └── data/minecraft/
│           ├── tags/worldgen/biome/has_structure/ancient_city.json
│           └── worldgen/structure_set/ancient_city.json
└── client/
    └── java/com/example/client/
        ├── TemplateModClient.java              # Client entry point
        └── mixin/
            └── ExampleClientMixin.java         # Client example Mixin
```

---

## License

This project is available under the [CC0 1.0 Universal](LICENSE) license. Feel free to use, modify, and share.

---

<div align="center">

**Made with ❤️ by [xiaomaiya](https://github.com/xkk-112358)**

[![GitHub](https://img.shields.io/badge/GitHub-Repository-181717?style=for-the-badge&logo=github)](https://github.com/xkk-112358/Minecraft-mod-ancient-city-underground)

</div>
