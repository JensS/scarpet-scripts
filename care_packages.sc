// Care Packages by Jens
// Randomly offers players care packages with... interesting confirmation requirements

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global'
};

// Global state
global_enabled = true;
global_pending_packages = {}; // Track who has pending offers
global_last_package_time = {}; // Cooldown per player

// Funny confirmation prompts and their required responses
global_prompts = [
    ['Wanna receive a gift from daddy? Say "yes daddy"', 'yes daddy'],
    ['Are you feeling lucky, punk? Type "I\'m feeling lucky"', 'i\'m feeling lucky'],
    ['Do you solemnly swear you are up to no good? Reply "I solemnly swear"', 'i solemnly swear'],
    ['Would you like fries with that? Say "supersize me"', 'supersize me'],
    ['Quick! What\'s the magic word? Type "please and thank you"', 'please and thank you'],
    ['Wanna box of mystery? Say "surprise me, Jens"', 'surprise me, jens'],
    ['Care package incoming! Confirm with "I need this"', 'i need this'],
    ['Free loot? Type "shut up and take my coords"', 'shut up and take my coords'],
    ['Want some goodies? Say "pretty please with a cherry on top"', 'pretty please with a cherry on top'],
    ['Ready for presents? Type "it\'s my birthday"', 'it\'s my birthday'],
    ['Feeling generous today. Reply "Jens is awesome"', 'jens is awesome'],
    ['Gift time! But first, say "I love this server"', 'i love this server'],
    ['Package available! Type "yeet it to me"', 'yeet it to me'],
    ['Want mystery items? Say "I trust you"', 'i trust you'],
    ['Care package ready! Confirm with "make it rain"', 'make it rain'],
    ['Free stuff alert! Type "gimme gimme"', 'gimme gimme'],
    ['Present incoming if you say "you\'re my favorite"', 'you\'re my favorite'],
    ['Wanna loot box? Say "open sesame"', 'open sesame'],
    ['Gift delivery! But only if you type "special delivery"', 'special delivery'],
    ['Ready to receive? Say "I\'m ready, I\'m ready"', 'i\'m ready, i\'m ready']
];

// Loot table - different tiers of care packages
global_loot_tiers = {
    'basic' -> [
        ['bread', 16, null],
        ['cooked_beef', 12, null],
        ['torch', 32, null],
        ['coal', 16, null],
        ['oak_planks', 32, null]
    ],
    'useful' -> [
        ['iron_ingot', 8, null],
        ['gold_ingot', 4, null],
        ['diamond', 2, null],
        ['emerald', 3, null],
        ['ender_pearl', 4, null],
        ['experience_bottle', 16, null]
    ],
    'fun' -> [
        ['tnt', 8, null],
        ['cake', 3, null],
        ['firework_rocket', 16, null],
        ['name_tag', 2, null],
        ['music_disc_cat', 1, null],
        ['cookie', 64, null]
    ],
    'tools' -> [
        ['iron_pickaxe', 1, null],
        ['iron_sword', 1, null],
        ['iron_axe', 1, null],
        ['iron_shovel', 1, null],
        ['bucket', 2, null]
    ]
};

// Pick random items from loot table
generate_care_package() -> (
    items = [];
    
    // Determine package tier (50% basic, 30% useful, 15% tools, 5% fun)
    rand_val = rand(1);
    tier = if(rand_val < 0.5, 'basic',
             if(rand_val < 0.8, 'useful',
             if(rand_val < 0.95, 'tools', 'fun')));
    
    tier_items = global_loot_tiers:tier;
    
    // Pick 2-4 random items from the tier
    num_items = 2 + floor(rand(3));
    
    loop(num_items,
        item = tier_items:(floor(rand(length(tier_items))));
        put(items, length(items), item)
    );
    
    [tier, items]
);

// Spawn a chest with items at player location
spawn_care_package(player, items) -> (
    pos = query(player, 'pos');
    
    // Find a safe spot near the player (on ground, not in player)
    spawn_x = round(pos:0) + if(rand(1) > 0.5, 1, -1);
    spawn_y = round(pos:1);
    spawn_z = round(pos:2) + if(rand(1) > 0.5, 1, -1);
    
    // Make sure there's ground below
    max_down = 0;
    while(air([spawn_x, spawn_y, spawn_z]) && max_down < 5,
        spawn_y = spawn_y - 1;
        max_down = max_down + 1
    );
    
    // Place one block above ground
    chest_pos = [spawn_x, spawn_y + 1, spawn_z];
    
    // Check if position is air, if not, place it at player's feet
    if(!air(chest_pos),
        chest_pos = [round(pos:0), round(pos:1), round(pos:2)]
    );
    
    // Place chest
    set(chest_pos, 'chest');
    
    // Add items to chest
    slot = 0;
    for(items,
        [item_name, count, nbt] = _;
        inventory_set(chest_pos, slot, count, item_name, nbt);
        slot = slot + 1
    );
    
    // Spawn some particles for effect
    particle('heart', chest_pos + [0.5, 1, 0.5], 10, 0.5, 0.5);
    sound('entity.player.levelup', chest_pos)
);

// Offer a care package to a random player
offer_care_package() -> (
    if(!global_enabled,
        schedule(6000, 'offer_care_package');
        return()
    );
    
    players = player('all');
    if(length(players) == 0,
        // No players online, try again later
        schedule(6000, 'offer_care_package');
        return()
    );
    
    // Pick random player
    lucky_player = players:(floor(rand(length(players))));
    player_name = query(lucky_player, 'name');
    
    // Check cooldown (30 minutes per player)
    current_time = unix_time();
    last_time = get(global_last_package_time, player_name);
    if(last_time != null && (current_time - last_time) < 1800000,
        // Player got one recently, try again later
        schedule(6000, 'offer_care_package');
        return()
    );
    
    // Pick random prompt
    prompt_pair = global_prompts:(floor(rand(length(global_prompts))));
    prompt = prompt_pair:0;
    required_response = prompt_pair:1;
    
    // Generate package
    package_data = generate_care_package();
    
    // Store pending package
    put(global_pending_packages, player_name, {
        'response' -> required_response,
        'items' -> package_data:1,
        'tier' -> package_data:0,
        'timestamp' -> current_time
    });
    
    // Send prompt to player
    formatted_msg = format(
        'g [', 'y Jens', 'g ] ',
        'e ' + prompt
    );
    print(lucky_player, formatted_msg);
    print(lucky_player, format('i (You have 60 seconds to respond in chat)'));
    
    // Clear pending after 60 seconds if no response
    schedule(1200, '_clear_pending_package', player_name);
    
    // Schedule next package offer (10-30 minutes)
    random_delay = 12000 + floor(rand(24000));
    schedule(random_delay, 'offer_care_package')
);

// Clear pending package if expired
_clear_pending_package(player_name) -> (
    package = get(global_pending_packages, player_name);
    if(package != null,
        current_time = unix_time();
        if((current_time - package:'timestamp') > 60000,
            delete(global_pending_packages, player_name);
            p = player(player_name);
            if(p != null,
                print(p, format('g [', 'y Jens', 'g ] ', 'r Too slow! Package offer expired. :('))
            )
        )
    )
);

// Listen for player chat messages
__on_player_message(player, message) -> (
    player_name = query(player, 'name');
    package = get(global_pending_packages, player_name);
    
    if(package == null, return());
    
    // Normalize message for comparison
    msg_lower = lower(message);
    required = package:'response';
    
    // Check if message matches required response
    if(msg_lower == required,
        // Success! Spawn package
        items = package:'items';
        tier = package:'tier';
        
        spawn_care_package(player, items);
        
        // Send success message
        success_messages = [
            'Delivery successful! Check near your feet!',
            'BOOM! Care package deployed! Enjoy!',
            'Package incoming! *thud* It\'s here!',
            'Your gift has arrived! Open with care!',
            'Special delivery! Don\'t spend it all in one place!',
            'Loot acquired! You\'re welcome!',
            'Package delivered! I expect a 5-star review.',
            'There you go! Use it wisely... or don\'t.',
            'Gift deployed! Try not to die immediately.',
            'Enjoy your loot! (No refunds)'
        ];
        success_msg = success_messages:(floor(rand(length(success_messages))));
        
        print(player, format('g [', 'y Jens', 'g ] ', 'l ' + success_msg));
        print(player, format('i Tier: ', 'y ' + tier));
        
        // Update cooldown
        put(global_last_package_time, player_name, unix_time());
        
        // Clear pending
        delete(global_pending_packages, player_name)
    ,
        // Wrong response
        wrong_messages = [
            'Nope! Wrong response. Read carefully!',
            'Not quite... Try again!',
            'That\'s not what I asked for!',
            'Close, but no cigar. Try exactly what I said!',
            'Reading comprehension: 0/10. Try again!'
        ];
        wrong_msg = wrong_messages:(floor(rand(length(wrong_messages))));
        print(player, format('g [', 'y Jens', 'g ] ', 'r ' + wrong_msg))
    )
);

// Start offering packages when script loads
__on_start() -> (
    print('Care Packages initialized! Random gifts incoming...');
    
    // First package offer after 5-10 minutes
    schedule(6000 + floor(rand(6000)), 'offer_care_package')
);

// Command to toggle system
__command() -> (
    global_enabled = !global_enabled;
    print(format(
        'g Care Packages ',
        if(global_enabled, 'l enabled', 'r disabled')
    ))
);

// Command for testing (ops only) - offers to yourself
test() -> (
    p = player();
    if(p == null, 
        print('Must be run by a player!');
        return()
    );
    
    player_name = query(p, 'name');
    
    // Generate and offer package immediately
    prompt_pair = global_prompts:(floor(rand(length(global_prompts))));
    prompt = prompt_pair:0;
    required_response = prompt_pair:1;
    
    package_data = generate_care_package();
    
    put(global_pending_packages, player_name, {
        'response' -> required_response,
        'items' -> package_data:1,
        'tier' -> package_data:0,
        'timestamp' -> unix_time()
    });
    
    print(p, format('g [', 'y Jens', 'g ] ', 'e ' + prompt));
    print(p, format('i (You have 60 seconds to respond in chat)'));
    
    schedule(1200, '_clear_pending_package', player_name)
);

// Offer package to specific player (call from console or as op)
offer_to(target_name) -> (
    target_player = player(target_name);
    
    if(target_player == null,
        print(str('Player %s not found or not online!', target_name));
        return()
    );
    
    // Check cooldown (30 minutes per player)
    current_time = unix_time();
    last_time = get(global_last_package_time, target_name);
    if(last_time != null && (current_time - last_time) < 1800000,
        print(str('Player %s got a package recently. Cooldown remaining.', target_name));
        return()
    );
    
    // Pick random prompt
    prompt_pair = global_prompts:(floor(rand(length(global_prompts))));
    prompt = prompt_pair:0;
    required_response = prompt_pair:1;
    
    // Generate package
    package_data = generate_care_package();
    
    // Store pending package
    put(global_pending_packages, target_name, {
        'response' -> required_response,
        'items' -> package_data:1,
        'tier' -> package_data:0,
        'timestamp' -> current_time
    });
    
    // Send prompt to player
    formatted_msg = format(
        'g [', 'y Jens', 'g ] ',
        'e ' + prompt
    );
    print(target_player, formatted_msg);
    print(target_player, format('i (You have 60 seconds to respond in chat)'));
    
    print(str('Care package offered to %s!', target_name));
    
    // Clear pending after 60 seconds if no response
    schedule(1200, '_clear_pending_package', target_name)
);