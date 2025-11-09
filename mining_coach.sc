// Motivational Mining Coach by Jens
// Tracks your mining progress and gives you encouragement (and sass)

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'player'
};

// Player-specific tracking
global_blocks_mined = {};
global_diamonds_found = {};
global_last_encouragement = {};
global_deaths_by_lava = {};

// Encouragement messages for general mining
global_mining_messages = {
    10 -> 'Nice start! You\'re really digging this whole mining thing!',
    25 -> 'Twenty-five blocks! You\'re on a roll!',
    50 -> 'Fifty blocks! Your pickaxe must be getting jealous of all the attention.',
    100 -> 'ONE HUNDRED BLOCKS! You absolute legend! Steve would be proud.',
    200 -> 'Two hundred blocks! Are you okay? Do you need water? Snacks? A hug?',
    500 -> 'FIVE HUNDRED BLOCKS! Sir/Madam, this is impressive. And slightly concerning.',
    1000 -> 'ONE THOUSAND BLOCKS! You\'ve officially mined more than most people will in their lifetime.',
    2000 -> 'Two THOUSAND blocks?! At this point you\'re not mining, you\'re excavating.',
    5000 -> 'FIVE THOUSAND BLOCKS! You\'ve essentially created a new cave system. Name it after yourself!',
    10000 -> 'TEN THOUSAND BLOCKS! I don\'t even have words. You\'re a mining deity.'
};

// Special messages for finding valuable ores
global_ore_messages = {
    'diamond_ore' -> [
        'DIAMONDS! You found diamonds! Quick, screenshot it before it disappears!',
        'DIAMONDS! Time to make that pickaxe you\'ve been dreaming about!',
        'Oooh shiny blue rocks! Your Minecraft life just got an upgrade!',
        'DIAMONDS! Fun fact: Statistically you were more likely to find love, but here we are!',
        'Diamonds found! Remember: Fortune III or cry forever.'
    ],
    'deepslate_diamond_ore' -> [
        'Deep slate diamonds! You went DEEP for that bling!',
        'Deepslate diamonds! You worked hard for those sparkly bois!',
        'Diamonds from the depths! Your persistence paid off!',
        'Found diamonds in deepslate? That\'s commitment right there!'
    ],
    'ancient_debris' -> [
        'ANCIENT DEBRIS! You\'re basically rich now! Netherite here we come!',
        'Ancient debris! Time to flex on your friends with that netherite gear!',
        'You found ancient debris! The flex-iest material in all of Minecraft!',
        'ANCIENT DEBRIS! You mad lad, you actually went mining in the Nether!'
    ],
    'emerald_ore' -> [
        'An emerald! Wow! These are rarer than your chances of beating the Ender Dragon first try!',
        'EMERALD! Time to trade with villagers like the boss you are!',
        'Emerald ore! This is rarer than finding friends who play Minecraft at the same time!',
        'An emerald! You\'re basically a treasure hunter at this point!'
    ],
    'gold_ore' -> [
        'Gold! Shiny but squishy, just like your resolve at 2 AM!',
        'Gold ore! Perfect for making tools that break faster than my patience!',
        'Found gold! Too bad it\'s about as useful as a chocolate teapot... but SHINY!',
        'Gold! The beautiful, disappointing cousin of diamond!'
    ],
    'iron_ore' -> [
        'Iron! The backbone of Minecraft civilization!',
        'Iron ore! Time to fuel your industrial empire!',
        'Found iron! This is what real miners look for. Diamonds are just for show.',
        'Iron! The most honest, hardworking ore in the game. Respect.'
    ],
    'coal_ore' -> [
        'Coal! The unsung hero of your torches!',
        'Coal ore! Keeping the darkness at bay, one block at a time.',
        'Found coal! Not glamorous, but absolutely essential. Like me.',
        'Coal! The friend you didn\'t know you needed until the lights went out.'
    ],
    'lapis_lazuli_ore' -> [
        'Lapis! Time to enchant stuff and look fancy!',
        'Lapis lazuli! The bluest of the blue rocks!',
        'Found lapis! Now you can make your enchantments even more random!',
        'Lapis ore! Because sometimes you just need blue dye and disappointment.'
    ],
    'redstone_ore' -> [
        'Redstone! Time to build something that definitely won\'t work on the first try!',
        'Redstone ore! The beginning of every overcomplicated contraption!',
        'Found redstone! Your inner engineer is awakening... for better or worse.',
        'Redstone! Warning: May cause symptoms of megalomania and sleep deprivation.'
    ]
};

// Sassy messages for common mining mistakes
global_oops_messages = {
    'lava' -> [
        'Ouch! That\'s hot lava, not a hot tub! Be more careful!',
        'Lava: 1, You: 0. Maybe look before you leap next time?',
        'Well, that was a warm welcome! Watch out for lava!',
        'RIP your items. The lava thanks you for the donation.',
        'And that, kids, is why we don\'t swim in lava. Stay in school.'
    ],
    'fall' -> [
        'Gravity wins again! Try using blocks next time?',
        'That was a long way down... Should\'ve brought a water bucket!',
        'Fun fact: Humans can\'t fly in Minecraft. Or in real life. Hope that helps!',
        'The ground sends its regards. Also, its condolences.'
    ],
    'creeper' -> [
        'That creeper had YOUR name on it! Stay alert out there!',
        'Creeper: 1, You: 0. They\'re quiet but deadly!',
        'Boom goes the creeper! And you. Mostly you.',
        'A creeper hugged you to death. How sweet. And explosive.'
    ]
};

// Track when player mines a block
__on_player_breaks_block(player, block) -> (
    player_name = query(player, 'name');
    block_type = str(block);
    
    // Increment blocks mined counter
    current_count = get(global_blocks_mined, player_name);
    if(current_count == null, current_count = 0);
    current_count = current_count + 1;
    put(global_blocks_mined, player_name, current_count);
    
    // Check for milestone encouragements
    for(keys(global_mining_messages),
        milestone = _;
        if(current_count == milestone,
            message = global_mining_messages:milestone;
            formatted_msg = format('g [', 'y Mining Coach', 'g ] ', 'l ' + message);
            print(player, formatted_msg)
        )
    );
    
    // Check for special ores
    for(keys(global_ore_messages),
        ore_type = _;
        if(block_type ~ ore_type,
            messages = global_ore_messages:ore_type;
            message = messages:(floor(rand(length(messages))));
            
            formatted_msg = format('g [', 'y Mining Coach', 'g ] ', 'e ' + message);
            print(player, formatted_msg);
            
            // Track diamonds specifically
            if(ore_type ~ 'diamond',
                diamond_count = get(global_diamonds_found, player_name);
                if(diamond_count == null, diamond_count = 0);
                diamond_count = diamond_count + 1;
                put(global_diamonds_found, player_name, diamond_count);
                
                // Extra encouragement for milestones
                if(diamond_count == 5,
                    print(player, format('g [', 'y Mining Coach', 'g ] ', 'l Five diamonds! You could make a pickaxe... or save them. Your choice!'))
                );
                if(diamond_count == 64,
                    print(player, format('g [', 'y Mining Coach', 'g ] ', 'l SIXTY-FOUR DIAMONDS! A full stack! You\'re living the dream!'))
                )
            )
        )
    );
    
    // Random encouragement every ~200 blocks
    if(current_count % 200 == 0 && rand(1) > 0.5,
        generic_encouragements = [
            'You\'re doing great! Keep it up!',
            'Your dedication to mining is truly inspiring!',
            'Wow, you\'re really committing to this mining lifestyle!',
            'Remember to come up for air sometimes!',
            'Your pickaxe is crying. Give it a break... then keep going!',
            'Mining level: Expert. Life choices: Questionable. Overall: Amazing!',
            'At this rate, you\'ll hollow out the entire world!',
            'Someone give this miner a medal! Or at least some snacks.'
        ];
        msg = generic_encouragements:(floor(rand(length(generic_encouragements))));
        print(player, format('g [', 'y Mining Coach', 'g ] ', 'l ' + msg))
    )
);

// Track player deaths and give sassy comments
// FIXED: Signature changed from (player, reason) to (player)
__on_player_dies(player) -> (
    player_name = query(player, 'name');
    
    // Get death message from the player object, as it is no longer a separate argument
    death_msg = query(player, 'death_message'); 
    if(death_msg == null, 
        // If query fails (which can happen depending on when the event fires), default to empty string
        death_msg = ''
    );
    death_msg = str(death_msg); // Ensure it's a string for matching
    
    // Check death type and respond accordingly
    
    if(death_msg ~ 'lava',
        messages = global_oops_messages:'lava';
        msg = messages:(floor(rand(length(messages))));
        schedule(40, '_send_death_message', player_name, msg);
        
        // Track lava deaths
        lava_deaths = get(global_deaths_by_lava, player_name);
        if(lava_deaths == null, lava_deaths = 0);
        lava_deaths = lava_deaths + 1;
        put(global_deaths_by_lava, player_name, lava_deaths);
        
        if(lava_deaths >= 3,
            schedule(60, '_send_death_message', player_name, 
                'That\'s lava death #' + lava_deaths + '. Maybe invest in Fire Resistance potions?')
        )
    );
    
    if(death_msg ~ 'fell' || death_msg ~ 'fall',
        messages = global_oops_messages:'fall';
        msg = messages:(floor(rand(length(messages))));
        schedule(40, '_send_death_message', player_name, msg)
    );
    
    if(death_msg ~ 'creeper',
        messages = global_oops_messages:'creeper';
        msg = messages:(floor(rand(length(messages))));
        schedule(40, '_send_death_message', player_name, msg)
    )
);

// Helper to send death messages (called with schedule to give player time to respawn)
_send_death_message(player_name, message) -> (
    p = player(player_name);
    if(p != null,
        print(p, format('g [', 'y Mining Coach', 'g ] ', 'r ' + message))
    )
);

// Command to check your stats
stats() -> (
    p = player();
    player_name = query(p, 'name');
    
    blocks = get(global_blocks_mined, player_name);
    if(blocks == null, blocks = 0);
    
    diamonds = get(global_diamonds_found, player_name);
    if(diamonds == null, diamonds = 0);
    
    lava_deaths = get(global_deaths_by_lava, player_name);
    if(lava_deaths == null, lava_deaths = 0);
    
    print(p, format('g ===== Your Mining Stats ====='));
    print(p, format('y Blocks mined: ', 'l ' + blocks));
    print(p, format('y Diamonds found: ', 'e ' + diamonds));
    print(p, format('y Lava deaths: ', 'r ' + lava_deaths));
    print(p, format('g =============================='))
);

__on_start() -> (
    print('Motivational Mining Coach initialized! Time to dig, dig, dig!')
);