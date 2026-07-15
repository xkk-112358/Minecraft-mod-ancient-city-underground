<div align="center">

# Ancient City Underground 🏛️

**让远古城市填满地下世界**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-brightgreen?style=flat-square)](https://minecraft.net)
[![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-dbd0b4?style=flat-square)](https://fabricmc.net)
[![License](https://img.shields.io/badge/License-CC0--1.0-blue?style=flat-square)](LICENSE)

</div>

---

## 介绍

**Ancient City Underground** 是一个 Fabric 模组，它彻底改变了远古城市的生成机制：

- **全域生成** — 远古城市不再局限于深暗之域，而是在**所有主世界群系**地下生成
- **高密度覆盖** — 生成间距为 10 区块，每座城市半径 128 格，**每个区块都在古城覆盖范围内**
- **适中重叠** — 城市之间重叠可控，既不太拥挤也不太稀疏
- **安全出生** — 玩家首次进入世界时，会安全地出生在**最近古城的随机安全位置**（走道、庭院等），每次位置不同
- **完全兼容** — 不影响末地传送门、要塞等其他结构

![Ancient City Underground](screenshots/ancient-city.png)

---

## 特性

| 特性 | 说明 |
|------|------|
| 🗺️ **全群系生成** | 平原、沙漠、森林、海洋、高山……所有主世界群系地下都有古城 |
| 📐 **密度优化** | `spacing=10, separation=2`，平均间距 ~160 格，覆盖半径 128 格 |
| 🎲 **随机出生点** | 每次重开世界出生位置不同，可能是庭院、走道或城门前 |
| 🛡️ **安全检测** | 自动检查出生点是否安全（地面实心、身体和头部是空气） |
| ⚡ **轻量无侵入** | 纯数据包覆盖 + Mixin，不修改原版结构定义 |

---

## 环境要求

| 组件 | 版本 |
|------|------|
| Minecraft | **1.21.11** |
| Fabric Loader | ≥ 0.19.3 |
| Fabric API | ≥ 0.141.4+1.21.11 |
| Java | ≥ 21 |

## 安装步骤

1. 安装 [Fabric Loader](https://fabricmc.net/use/)
2. 下载 [Fabric API](https://modrinth.com/mod/fabric-api)
3. 下载本模组的 JAR（从 Releases 或自行构建）
4. 将 JAR 放入 `.minecraft/mods/` 文件夹
5. 启动游戏，**新建世界**即可体验

---

## 技术细节

### 实现方式

| 机制 | 方法 |
|------|------|
| **全群系生成** | 覆盖 `data/minecraft/tags/worldgen/biome/has_structure/ancient_city.json`，引用 `#minecraft:is_overworld` |
| **生成密度** | 覆盖 `data/minecraft/worldgen/structure_set/ancient_city.json`，`spacing=10, separation=2` |
| **玩家出生点** | Mixin 注入 `PlayerList#placeNewPlayer`，查找最近古城并寻找随机安全位置 |

### 结构集参数

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

| 参数 | 值 | 说明 |
|------|----|------|
| `spacing` | 10 | 网格大小（区块），每 10×10 区块有一个生成尝试 |
| `separation` | 2 | 最小间距（区块），城市中心距网格边界至少 2 区块 |
| 最小中心距 | 4 区块 (64 格) | 两座古城能靠得最近的距离 |
| 最大中心距 | 16 区块 (256 格) | 两座古城能隔得最远的距离（刚好相接） |
| 覆盖半径 | 8 区块 (128 格) | 原版 `max_distance_from_center`，未修改 |

---

## 自行构建

```bash
git clone https://github.com/xkk-112358/Minecraft-mod-ancient-city-underground.git
cd Minecraft-mod-ancient-city-underground

# Windows
gradlew build

# Linux / macOS
./gradlew build
```

构建产物在 `build/libs/` 目录下：
- `ancient-city-underground-<version>.jar` — 可运行的模组文件
- `ancient-city-underground-<version>-sources.jar` — 源码包

---

## 项目结构

```
src/
├── main/
│   ├── java/com/example/
│   │   ├── TemplateMod.java                    # 模组主入口 + SERVER_STARTED 事件
│   │   └── mixin/
│   │       ├── ExampleMixin.java               # 示例 Mixin
│   │       └── PlayerListMixin.java            # 玩家出生点注入
│   └── resources/
│       ├── fabric.mod.json                     # 模组元数据
│       ├── anciencity.mixins.json              # Mixin 配置
│       ├── assets/anciencity/image.png          # 模组图标
│       └── data/minecraft/
│           ├── tags/worldgen/biome/has_structure/ancient_city.json
│           └── worldgen/structure_set/ancient_city.json
└── client/
    └── java/com/example/client/
        ├── TemplateModClient.java              # 客户端入口
        └── mixin/
            └── ExampleClientMixin.java         # 客户端示例 Mixin
```

---

## 许可证

本项目基于 [CC0 1.0 通用](LICENSE) 协议开源。您可以自由使用、修改和分享。

---

<div align="center">

**Made with ❤️ by [xiaomaiya](https://github.com/xkk-112358)**

[![GitHub](https://img.shields.io/badge/GitHub-Repository-181717?style=for-the-badge&logo=github)](https://github.com/xkk-112358/Minecraft-mod-ancient-city-underground)

</div>
