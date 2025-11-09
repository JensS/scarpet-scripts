# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a collection of **scarpet scripts** for the Minecraft Carpet mod. Scarpet is a scripting language that runs within Minecraft to add custom gameplay features and automation.

## Language: Scarpet

All files use the `.sc` extension and are written in **scarpet**, a custom scripting language for Minecraft's Carpet mod. Key language characteristics:

- **Arrow syntax**: Functions use `->` instead of traditional braces
- **No semicolons**: Statements are separated by semicolons (`;`), expressions by commas
- **Global state**: Variables prefixed with `global_` persist across function calls
- **Event handlers**: Special functions like `__on_start()`, `__on_player_message()`, `__on_player_breaks_block()`, `__on_player_dies()`
- **Config block**: Required `__config()` function defines script behavior (`stay_loaded`, `scope`)
- **Scheduling**: Use `schedule(ticks, 'function_name')` for delayed execution (20 ticks = 1 second)

### Common Scarpet Patterns

```scarpet
// Function definition
function_name(arg1, arg2) -> (
    // function body
    // last expression is return value
);

// Conditional
if(condition,
    expression_if_true,
    expression_if_false
);

// Loops
for(list, /* iterates over list */
    _ /* current item */
);

loop(count, /* repeat count times */
    _ /* iteration index */
);

// Player queries
query(player, 'name')
query(player, 'pos')
query(player, 'dimension')

// Formatted messages
format('g text', 'y text', 'r text') // g=green, y=yellow, r=red, l=light blue, e=light purple, i=italic
```

## Script Architecture

Each script is **standalone and independent**, following a common structure:

1. **`__config()`**: Defines script configuration (always `stay_loaded -> true`)
2. **Global variables**: Persistent state (e.g., `global_enabled`, `global_messages`)
3. **Event handlers**: React to game events (`__on_start`, `__on_player_*`)
4. **Helper functions**: Internal logic (often prefixed with `_`)
5. **Commands**: User-callable functions via `/script in <script_name> invoke <function>`

### Scope Patterns

- **`'scope' -> 'global'`**: Single instance shared across all players (bedtime_reminder, care_packages, dad_jokes, proximity_alerts, hot_potato)
- **`'scope' -> 'player'`**: Separate instance per player (mining_coach)

## Scripts in this Repository

### bedtime_reminder.sc
Sends escalating humorous messages to players after 1:00 AM real-time.
- **Key feature**: Uses `unix_time()` with configurable timezone offset (`global_timezone_offset`)
- **Schedule**: Checks every 5 minutes (6000 ticks), resets at 6 AM
- **Commands**: `__command()` toggles enabled/disabled, `test()` previews all messages

### care_packages.sc
Randomly offers care packages to players with funny confirmation prompts.
- **Key feature**: Listens to `__on_player_message()` for specific responses
- **Loot system**: Tiered random loot (basic, useful, fun, tools)
- **Cooldown**: 30 minutes per player between offers
- **Commands**: `test()` offers to self, `offer_to(player_name)` targets specific player

### dad_jokes.sc
Periodically broadcasts Minecraft-themed dad jokes.
- **Key feature**: Supports multi-part jokes with timing (arrays vs strings)
- **Schedule**: Random 10-20 minute intervals between jokes
- **Commands**: `joke()` tells joke on demand

### mining_coach.sc
Tracks mining progress and provides motivational/sassy feedback.
- **Scope**: `'player'` - each player has separate tracking
- **Events**: `__on_player_breaks_block()`, `__on_player_dies()`
- **State**: Tracks blocks mined, diamonds found, lava deaths per player
- **Commands**: `stats()` shows player statistics

### proximity_alerts.sc
Sends humorous alerts when players get close to each other.
- **Distance tracking**: Calculates 3D distance between players every 5 seconds
- **Cooldown**: 5 minutes between alerts for same player pair
- **Commands**: `set_distance(blocks)` adjusts proximity threshold

### hot_potato.sc
A multiplayer game where players pass a "hot potato" before it explodes.
- **Complex state management**: Active game tracking, pass cooldowns, player statistics
- **Auto-pass mechanic**: Potato passes automatically when players are within range
- **Inventory manipulation**: Uses `inventory_get()` and `inventory_set()` to manage items
- **Auto-spawn**: Games start automatically every hour when 2+ players online
- **Commands**: `start()` begins game, `stop()` ends game, `toggle_auto()` enables/disables auto-spawn

## Common Development Tasks

### Testing Scripts in Minecraft

Scripts are loaded via Carpet mod commands:
```
/script load <script_name>
/script in <script_name> invoke <function_name>
/script in <script_name> invoke <function_name> <arg1> <arg2>
```

### Message Formatting

All scripts use consistent message formatting with Jens as the sender:
```scarpet
formatted_msg = format(
    'g [', 'y Jens', 'g ] ',
    'i message_text'
);
for(player('all'),
    print(_, formatted_msg)
)
```

### Time-based Operations

- **Game time**: Use `world_time()` (0-24000 ticks per day)
- **Real time**: Use `unix_time()` (milliseconds since epoch)
- **Scheduling**: `schedule(ticks, 'function_name')` or `schedule(ticks, 'function_name', arg1, arg2)`

### Player Data Persistence

Use maps with player names as keys:
```scarpet
global_player_data = {};
put(global_player_data, player_name, value);
value = get(global_player_data, player_name);
```

## Event Handler Signatures

Important: Event handler signatures must match exactly:

```scarpet
__on_start() -> (...)
__on_player_message(player, message) -> (...)
__on_player_breaks_block(player, block) -> (...)
__on_player_dies(player) -> (...)  // Note: death message accessed via query(player, 'death_message')
__command() -> (...)  // No arguments
```

## Debugging

- Use `print(player, 'debug message')` to send messages to specific players
- Use `print('debug message')` to log to server console
- Check script errors with `/script in <script_name> globals` to inspect global variables
