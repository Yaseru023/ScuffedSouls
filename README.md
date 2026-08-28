# ScuffedSouls

A Minecraft Forge 1.20.1 mod focused on custom player progression, buildup mechanics, classes, and Souls-inspired death mechanics.

> **Status:** 🚧 In Development

## ✨ Features

### ⚔️ Player Classes

* Custom player class system
* Class selection when joining for the first time
* Persistent class data across player deaths/respawns
* Designed to support multiple unique playstyles

### ☠️ Death & Soul Anchors

* Players lose a portion of item durability when they die
* Player XP is stored at the death location
* A **Soul Anchor** is created to hold the player's lost XP
* Soul Anchors expire after a period of time
* Prevents lost XP from simply disappearing on death

### 🩸 Buildup System

* Custom buildup mechanics tied to status effects
* Configurable maximum buildup
* Configurable buildup decay
* Configurable buildup application amount
* Custom effect duration and amplifier values
* Client/server buildup synchronization

### 🌎 Dimension Effects

* Special effects are applied when entering specific dimensions
* Currently includes support for:

  * `the_faint_radiance:paradise`

### 🌐 Networking

* Custom Forge networking system
* Synchronization of buildup definitions between server and client
* Synchronization of individual buildup values
* Client-side class selection screen communication

## 🛠️ Technology

* **Java**
* **Minecraft 1.20.1**
* **Minecraft Forge 47.4.0**
* **Gradle**
* **IntelliJ IDEA**
* **Git / GitHub**

## 📦 Installation

### Requirements

* Minecraft **1.20.1**
* Minecraft Forge **47.4.0** or a compatible Forge 1.20.1 version
* Java version required by Minecraft/Forge 1.20.1

### Installing the Mod

1. Install Minecraft Forge 1.20.1.
2. Download the latest ScuffedSouls `.jar` from the [Releases](../../releases) page.
3. Place the `.jar` file into your Minecraft `mods` folder.
4. Launch Minecraft using the Forge profile.

> **Note:** ScuffedSouls is currently in development, so releases may be unstable.

## 🔧 Development Setup

### Clone the Repository

```bash
git clone https://github.com/Yaseru023/ScuffedSouls.git
cd ScuffedSouls
```

### Import into IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Select **Open**.
3. Select the cloned repository.
4. Allow IntelliJ to import the Gradle project.
5. Wait for Gradle to finish downloading dependencies.

### Run the Development Client

On Windows:

```powershell
.\gradlew runClient
```

On Linux/macOS:

```bash
./gradlew runClient
```

### Run the Development Server

On Windows:

```powershell
.\gradlew runServer
```

On Linux/macOS:

```bash
./gradlew runServer
```

## 🏗️ Project Structure

```text
src/
└── main/
    ├── java/
    │   └── net/yaseruxd/scuffedsouls/
    │       ├── buildup/
    │       ├── block/
    │       ├── event/
    │       ├── network/
    │       ├── playerclass/
    │       └── registry/
    │
    └── resources/
        ├── assets/
        └── data/
```

### Major Systems

| Package       | Purpose                                     |
| ------------- | ------------------------------------------- |
| `buildup`     | Buildup definitions, storage, and mechanics |
| `block`       | Soul Anchor functionality                   |
| `event`       | Player and server event handling            |
| `network`     | Client/server packet communication          |
| `playerclass` | Player class management                     |
| `registry`    | Mod blocks and other registered content     |

## 🧠 Current Development

ScuffedSouls is actively being developed.

Current development areas include:

* [x] Player death handling
* [x] XP Soul Anchor system
* [x] Item durability death penalty
* [x] Buildup data storage
* [x] Buildup client/server synchronization
* [x] Player class persistence
* [x] Initial class selection
* [x] Dimension-specific effects
* [ ] Additional player classes
* [ ] Additional buildup effects
* [ ] More Soul Anchor mechanics
* [ ] Additional items and blocks
* [ ] Balancing and gameplay testing
* [ ] Public release

## 📸 Screenshots

> TODO: Add screenshots of the mod here.

Example:

```markdown
![Class Selection](screenshots/class-selection.png)
```

Recommended screenshots:

* Class selection screen
* Soul Anchor
* Buildup/status effect UI
* Gameplay
* Custom items/blocks

## 🗺️ Roadmap

### Phase 1 — Core Systems

* [x] Player data
* [x] Buildup system
* [x] Soul Anchor XP storage
* [x] Player classes
* [x] Networking

### Phase 2 — Content

* [ ] More classes
* [ ] More buildup effects
* [ ] Custom weapons/items
* [ ] Additional blocks
* [ ] Additional dimensions/content

### Phase 3 — Polish

* [ ] Balance gameplay
* [ ] Improve UI
* [ ] Add sound effects
* [ ] Add particles
* [ ] Improve documentation
* [ ] Public beta release

## 🤝 Contributing

Contributions, suggestions, and bug reports are welcome.

Before submitting a pull request:

1. Make sure the project builds successfully.
2. Test your changes in the Forge development environment.
3. Keep commits focused on a specific change.
4. Explain the purpose of significant changes.

## 🐛 Bug Reports

If you encounter a bug, please open an issue and include:

* Minecraft version
* Forge version
* ScuffedSouls version
* Steps to reproduce the problem
* Expected behavior
* Actual behavior
* Relevant crash logs or screenshots

## 📜 License

> TODO: Choose a license.

This project is currently under development. Licensing information will be added before public distribution.

## 👤 Author

**Yaseru023**

GitHub: [@Yaseru023](https://github.com/Yaseru023)

## ⭐ Project Status

ScuffedSouls is a personal Minecraft mod project created to experiment with Java development, Minecraft Forge modding, custom gameplay systems, client/server networking, and persistent player data.

The project is currently under active development.

```
```
