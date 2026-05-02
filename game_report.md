# Super Word Twins

## Overview
**Super Word Twins** is a fast-paced 2D platformer that combines traditional action mechanics with educational riddle-solving. Players navigate through a city environment, collecting coins, dodging hazards, and battling iconic bosses while searching for letters to solve a secret riddle.

## Controls
| Action | Key |
| :--- | :--- |
| Move Left | `Left Arrow` |
| Move Right | `Right Arrow` |
| Jump | `Space` |
| Crouch / Glide | `Down Arrow` |
| Climb | `Up Arrow` (on ladders/ceilings) |
| Punch | `Z` |
| Kick | `X` |

## Core Mechanics

### 1. Riddle System
Each level features a hidden riddle. Players must explore the map to find and collect **Letter Tiles**.
- Collecting a correct letter fills it into the riddle answer bar.
- The game level only ends once the riddle is fully solved and the player reaches the **Victory Heart**.

### 2. Combat & Enemies
The game features a robust enemy system with unique AI behaviors:
- **Penguin**: Moves in a Sine-wave pattern, oscillating vertically while chasing the player.
- **Joker, Harley, & Riddler**: Aggressive horizontal chasers.
- **Killer Croc**: Large boss that lunges at the player using Bézier curve paths.
- **Red Hood**: Elite hunter that follows the player anywhere, capable of jumping and navigating complex platforms.
- **Combat Logic**: Enemies take **5 hits** to defeat. Players deal damage using punches (`Z`) or kicks (`X`). Colliding with an enemy without attacking deals **10 damage** to the player and causes a knockback effect.

### 3. Mystery Boxes
Special interactive items that provide random effects:
- **Health Boost**: Restores **+20 HP**.
- **Super Speed**: Temporary movement speed increase.
- **Slow Down**: Temporary movement penalty.
- **Enlarge**: Increases player size significantly for a short duration.
- **Letter Hint**: Highlights correct/incorrect letter tiles on the map.

### 4. Hazards
The environment is filled with deadly traps:
- **Pendulum Axes**: Massive swinging blades with real-time collision detection.
- **Spinning Blades**: Stationary circular saws.
- **Ground Spikes**: Stationary hazards that deal significant damage.

## Technical Implementation
- **Engine**: Custom Java Swing / AWT framework.
- **Physics**: Tile-based collision system with support for slopes, gravity, and ceiling walking.
- **Animations**: Frame-based animation engine with dynamic state switching (running, jumping, attacking, death).
- **Sound**: Integrated `SoundManager` for immersive audio feedback.

## Level Structure
- **Level 1**: Introduction to movement and basic riddle solving in a city rooftop setting.
- **Level 2**: Advanced platforming challenges, territory-locked bosses, and more complex hazard layouts.

---
*Developed by the Super Word Twins Team.*
