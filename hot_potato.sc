// Hot Potato Game by Jens
// Pass the potato before it explodes!

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global'
};

// Global game state
global_game_active = false;
global_potato_holder = null;
global_game_start_time = 0;
global_potato_duration = 45; // seconds until explosion
global_pass_cooldown = {}; // Track who can receive potato
global_pass_distance = 3; // blocks needed to pass
global_passes = {}; // Track successful passes per player
global_auto_spawn_enabled = true; // Enable automatic game spawning
global_last_auto_game = 0; // Track last auto-spawned game

// Public humiliation messages when potato explodes
global_explosion_messages = [
    '{} couldn\'t handle the heat! What a potato!',
    '{} has been BAKED! Better luck next time!',
    'BOOM! {} just became mashed potatoes!',
    '{} failed to pass the potato. Shame. Shame. Shame.',
    'The potato chose violence, and {} chose poorly!',
    '{} held onto that potato like it was the last one on Earth. It wasn\'t.',
    'Breaking news: {} just lost a fight with a potato!',
    'RIP {} - Defeated by a humble potato. Let that sink in.',
    '{} and the potato had a disagreement. The potato won.',
    'Achievement unlocked for {}: "Got Roasted by a Potato"',
    'The potato gods are disappointed in {}.',
    '{} will forever be known as the person who couldn\'t pass a potato.',
    'Ladies and gentlemen, {} just proved potatoes are dangerous!',
    '{} is now suffering from severe potato-related embarrassment.',
    'The potato has spoken. {} was not worthy.',
    '{} got absolutely ROASTED! Literally!',
    'KABOOM! {} lost at hot potato! Classic!',
    '{} tried to befriend the potato. It backfired. Explosively.',
    'The potato won this round. {} lost. Badly.',
    '{} just learned a valuable lesson about hot potatoes!',
    'BOOM! {} is now a cautionary tale!',
    '{} got potato-ed! That\'s not even a word but it happened!',
    'Explosion at {}\' location! Cause: Potato!',
    '{} speedran losing at hot potato!',
    'History will remember {} as "The Potato Victim"',
    '{} became one with the potato. Explosively.',
    'Hot potato: 1, {}: 0',
    '{} just earned the title "Potato Holder" (Derogatory)',
    'The potato claimed another victim: {}!',
    'F in chat for {}. They got potato-bombed!',
    '{} learned that some potatoes fight back!',
    'BOOM! {} will never look at potatoes the same way!',
    '{} forgot the "hot" part of "hot potato"!',
    'The potato explosion heard \'round the server: {}!',
    '{} got served... by a potato!'
];

// Messages during the game
global_warning_messages = {
    35 -> 'The potato is getting warm...',
    25 -> 'The potato is heating up! Pass it quick!',
    15 -> 'THE POTATO IS HOT! GET RID OF IT!',
    10 -> 'TEN SECONDS! THE POTATO IS BURNING!',
    5 -> 'FIVE! FOUR! THREE! TWO! ONE!',
    3 -> 'THREE SECONDS!!!',
    1 -> 'ONE SECOND!!!'
};

// Give player the hot potato item
give_potato(player) -> (
    player_name = query(player, 'name');

    // Clear inventory of any existing potatoes first
    loop(36,
        slot = _;
        item_data = inventory_get(player, slot);
        if(item_data != null && item_data:0 == 'baked_potato',
            inventory_set(player, slot, 0)
        )
    );

    // Give glowing hot potato in hand
    inventory_set(player, query(player, 'selected_slot'), 1, 'baked_potato',
        '{display:{Name:\'{"text":"Hot Potato","color":"red","bold":true,"italic":false}\'},Enchantments:[{id:"minecraft:fire_aspect",lvl:1}]}'
    );

    // Visual effects
    particle('flame', query(player, 'pos') + [0, 1, 0], 20, 0.3, 0.5);
    sound('entity.blaze.ambient', query(player, 'pos'))
);

// Remove potato from player
remove_potato(player) -> (
    loop(36,
        slot = _;
        item_data = inventory_get(player, slot);
        if(item_data != null && item_data:0 == 'baked_potato',
            inventory_set(player, slot, 0)
        )
    )
);

// Check if player has the potato
has_potato(player) -> (
    loop(36,
        slot = _;
        item_data = inventory_get(player, slot);
        if(item_data != null && item_data:0 == 'baked_potato',
            return(true)
        )
    );
    false
);

// Start a new game
start_game() -> (
    if(global_game_active,
        print('A game is already running!');
        return()
    );

    players = player('all');
    if(length(players) < 2,
        print('Need at least 2 players to start Hot Potato!');
        return()
    );

    // Reset game state
    global_game_active = true;
    global_passes = {};
    global_pass_cooldown = {};

    // Pick random player to start
    starter = players:(floor(rand(length(players))));
    global_potato_holder = query(starter, 'name');
    global_game_start_time = unix_time();

    // Give them the potato
    give_potato(starter);

    // Announce game start (split into multiple messages to avoid length limits)
    for(player('all'),
        print(_, format('r ========================================'));
        print(_, format('y        🥔 HOT POTATO GAME! 🥔'));
        print(_, format('r ========================================'));
        print(_, '');
        print(_, format('g 👉 ', 'y ' + global_potato_holder, 'r  HAS THE HOT POTATO!'));
        print(_, '');
        print(_, format('g 📋 HOW TO PLAY:'));
        print(_, format('g   • Get within ', 'y ' + global_pass_distance + ' blocks', 'g  of another player'));
        print(_, format('g   • The potato will ', 'r AUTO-PASS', 'g  when you\'re close'));
        print(_, format('g   • Don\'t let it explode on YOU!'));
        print(_, '');
        print(_, format('r ⏰ Time until BOOM: ', 'y ' + global_potato_duration + ' seconds'));
        print(_, '');
        print(_, format('r ========================================'))
    );

    sound('entity.ender_dragon.growl', query(starter, 'pos'));

    // Start game loop
    schedule(20, 'game_tick');
);


game_tick() -> (
    if(!global_game_active, return());

    holder = player(global_potato_holder);

    // Check if holder still exists and is online
    if(holder == null,
        // Holder disconnected, end game
        for(player('all'),
            print(_, format('r Hot Potato game ended - ', 'y ' + global_potato_holder, 'r  disconnected!'))
        );
        global_game_active = false;
        return()
    );

    // Check if holder still has potato
    if(!has_potato(holder),
        // They dropped or lost it somehow, give it back
        give_potato(holder)
    );

    // Calculate time remaining
    current_time = unix_time();
    elapsed_seconds = floor((current_time - global_game_start_time) / 1000);
    time_left = global_potato_duration - elapsed_seconds;

    // Check for explosion
    if(time_left <= 0,
        explode_potato();
        return()
    );

    // Send warning messages
    if(has(global_warning_messages, time_left),
        message = global_warning_messages:time_left;
        for(player('all'),
            print(_, format('r [HOT POTATO] ', 'y ' + message))
        );
        sound('block.note_block.pling', query(holder, 'pos'), 1, if(time_left <= 5, 2, 1))
    );

    // Visual effects on holder
    particle_intensity = if(time_left < 10, 3, if(time_left < 20, 2, 1));
    particle('flame', query(holder, 'pos') + [0, 1, 0], 5 * particle_intensity, 0.3, 0.5);

    // Check for nearby players to pass to
    check_passing(holder);

    // Continue game loop
    schedule(20, 'game_tick')
);

// Check if potato can be passed to nearby players
check_passing(holder) -> (
    holder_name = query(holder, 'name');
    holder_pos = query(holder, 'pos');

    current_time = unix_time();

    // Check all other players
    for(player('all'),
        other = _;
        other_name = query(other, 'name');

        // Skip if same player
        if(other_name == holder_name, continue());

        // Skip if on cooldown (can't receive for 10 seconds after passing)
        cooldown = get(global_pass_cooldown, other_name);
        if(cooldown != null && (current_time - cooldown) < 10000, continue());

        // Check if in same dimension
        if(query(holder, 'dimension') != query(other, 'dimension'), continue());

        // Check distance
        other_pos = query(other, 'pos');
        dx = holder_pos:0 - other_pos:0;
        dy = holder_pos:1 - other_pos:1;
        dz = holder_pos:2 - other_pos:2;
        dist = sqrt(dx*dx + dy*dy + dz*dz);

        // If close enough, pass the potato!
        if(dist <= global_pass_distance,
            pass_potato(holder, other);
            return()
        )
    )
);

// Pass potato from one player to another
pass_potato(from_player, to_player) -> (
    from_name = query(from_player, 'name');
    to_name = query(to_player, 'name');

    // Remove from old holder
    remove_potato(from_player);

    // Give to new holder
    give_potato(to_player);
    global_potato_holder = to_name;

    // Track passes
    passes = get(global_passes, from_name);
    if(passes == null, passes = 0);
    put(global_passes, from_name, passes + 1);

    // Set cooldown so they can't immediately get it back
    put(global_pass_cooldown, from_name, unix_time());

    // Announce pass
    message = format(
        'y ' + from_name, 'g  passed the potato to ', 'y ' + to_name, 'g !'
    );
    for(player('all'),
        print(_, format('g [HOT POTATO] ', message))
    );

    // Effects
    particle('flame', query(to_player, 'pos') + [0, 1, 0], 30, 0.5, 1);
    sound('entity.item.pickup', query(to_player, 'pos'), 1, 0.5);
    sound('entity.player.hurt_on_fire', query(from_player, 'pos'), 0.5, 1.5)
);

// Explode the potato!
explode_potato() -> (
    holder = player(global_potato_holder);

    if(holder != null,
        // Remove potato
        remove_potato(holder);

        // Explosion effects
        pos = query(holder, 'pos');
        particle('explosion', pos + [0, 1, 0], 20, 1, 1);
        particle('lava', pos + [0, 1, 0], 30, 1, 1);
        sound('entity.generic.explode', pos, 1, 0.8);

        // Give them "Potato Loser" tag for 5 minutes
        run(str('tag %s add potato_loser', global_potato_holder));
        schedule(6000, '_remove_loser_tag', global_potato_holder)
    );

    // Pick random humiliation message
    message_template = global_explosion_messages:(floor(rand(length(global_explosion_messages))));
    message = str(message_template, global_potato_holder);

    // Announce explosion
    for(player('all'),
        print(_, format(
            'r ========== ', 'y BOOM!', 'r  ==========',
            '\n', 'l ' + message,
            '\n', 'r =========================='
        ))
    );

    // Show scoreboard
    schedule(40, '_show_results');

    global_game_active = false
);

// Remove loser tag after time
_remove_loser_tag(player_name) -> (
    run(str('tag %s remove potato_loser', player_name))
);

// Show final results
_show_results() -> (
    for(player('all'),
        print(_, format('y ===== HOT POTATO RESULTS ====='))
    );

    // Sort players by passes
    sorted_players = [];
    for(keys(global_passes),
        put(sorted_players, length(sorted_players), [_, global_passes:_])
    );

    // Simple sort (bubble sort for small lists)
    if(length(sorted_players) > 0,
        loop(length(sorted_players),
            i = _;
            loop(length(sorted_players) - 1,
                j = _;
                if(sorted_players:j:1 < sorted_players:(j+1):1,
                    temp = sorted_players:j;
                    sorted_players:j = sorted_players:(j+1);
                    sorted_players:(j+1) = temp
                )
            )
        );

        // Display results
        for(sorted_players,
            player_data = _;
            player_name = player_data:0;
            passes = player_data:1;

            for(player('all'),
                print(_, format('g ', player_name, ': ', 'y ' + passes, 'g  passes'))
            )
        )
    ,
        for(player('all'),
            print(_, format('r No passes were made!'))
        )
    );

    for(player('all'),
        print(_, format('y =============================='))
    )
);

// Commands
__command() -> (
    global_game_active = !global_game_active;
    print(format(
        'g Hot Potato ',
        if(global_game_active, 'l enabled', 'r disabled')
    ))
);

start() -> start_game();

// End current game
stop() -> (
    if(!global_game_active,
        print('No game is currently running!');
        return()
    );

    global_game_active = false;

    if(global_potato_holder != null,
        holder = player(global_potato_holder);
        if(holder != null,
            remove_potato(holder)
        )
    );

    for(player('all'),
        print(_, format('r Hot Potato game has been stopped!'))
    )
);

__on_start() -> (
    print('Hot Potato game loaded! Use /script in hot_potato invoke start to begin!');

    // Start auto-spawn checker
    schedule(1200, 'check_auto_spawn')
);

// Check if we should auto-spawn a game
check_auto_spawn() -> (
    if(global_auto_spawn_enabled && !global_game_active,
        players = player('all');

        // Only spawn if 2+ players online
        if(length(players) >= 2,
            current_time = unix_time();

            // Check if an hour has passed (3600000 ms)
            if(global_last_auto_game == 0 || (current_time - global_last_auto_game) >= 3600000,
                // Announce incoming game
                for(player('all'),
                    print(_, format('y [Hot Potato] ', 'g Surprise game starting in 10 seconds!'))
                );

                // Start game after 10 seconds
                schedule(200, '_auto_start_game')
            )
        )
    );

    // Check again in 1 minute
    schedule(1200, 'check_auto_spawn')
);

// Auto-start a game
_auto_start_game() -> (
    if(!global_game_active,
        global_last_auto_game = unix_time();
        start_game()
    )
);

// Toggle auto-spawn
toggle_auto() -> (
    global_auto_spawn_enabled = !global_auto_spawn_enabled;
    print(format(
        'g Hot Potato auto-spawn ',
        if(global_auto_spawn_enabled, 'l enabled', 'r disabled')
    ))
);
