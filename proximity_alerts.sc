// Proximity Alerts by Jens
// Sends funny messages when players get close to each other

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global'
};

// Global state
global_enabled = true;
global_proximity_distance = 10; // blocks
global_last_alerts = {}; // Track last alert time for each player pair
global_alert_cooldown = 300000; // 5 minutes between alerts for same pair (in ms)

// Proximity alert messages - now just need two player name insertions
global_proximity_messages = [
    ['Warning: Social interaction imminent between ', ' and ', '!'],
    [' and ', ' are dangerously close! Awkward small talk in 3... 2... 1...'],
    ['ALERT: ', ' spotted near ', '. Initiate greeting protocols!'],
    ['Personal space violation detected! ', ' and ', ' within hugging distance.'],
    [' and ', ': Now kiss! Just kidding. Unless...?'],
    ['Breaking news: ', ' and ', ' are in the same postal code!'],
    ['Proximity alert! ', ' can probably smell what ', ' is cooking.'],
    [' and ', ' are close enough to share snacks. Are you sharing snacks?'],
    ['BEEP BEEP BEEP! ', ' backing up near ', '!'],
    ['Collision warning! ', ' and ', ' might actually have to talk to each other!'],
    ['Social distancing failure: ', ' and ', ' are suspiciously close.'],
    ['Achievement unlocked: ', ' and ', ' discovered they have a friend!'],
    [' and ', ': Together at last. This is either adorable or concerning.'],
    ['Red alert! ', ' and ', ' entering the danger zone of human interaction!'],
    ['Someone put ', ' and ', ' in the same room. What could go wrong?'],
    [' casually stalking ', '... or is ', ' stalking you? Hard to tell.'],
    ['The simulation has placed ', ' and ', ' in proximity. Proceed with caution.'],
    ['Two players enter: ', ' and ', '. One conversation leaves... maybe.'],
    ['Friendship detector activated: ', ' + ', ' = potential shenanigans ahead!'],
    ['Server log: ', ' approaching ', '. Likelihood of chaos: High.']
];

// Calculate distance between two players
distance_between(player1, player2) -> (
    pos1 = query(player1, 'pos');
    pos2 = query(player2, 'pos');
    
    dx = pos1:0 - pos2:0;
    dy = pos1:1 - pos2:1;
    dz = pos1:2 - pos2:2;
    
    sqrt(dx*dx + dy*dy + dz*dz)
);

// Get a unique key for a player pair (order-independent)
get_pair_key(p1, p2) -> (
    name1 = query(p1, 'name');
    name2 = query(p2, 'name');
    
    // Sort names to make key order-independent
    if(name1 < name2,
        name1 + '|' + name2,
        name2 + '|' + name1
    )
);

// Check for proximity between all players
check_proximity() -> (
    if(!global_enabled, 
        schedule(100, check_proximity);
        return()
    );
    
    current_time = unix_time();
    players = player('all');
    
    // Check each pair of players
    for(players,
        p1 = _;
        for(players,
            p2 = _;
            
            // Skip if same player or if p2 comes before p1 (to avoid duplicates)
            if(p1 == p2 || query(p1, 'name') >= query(p2, 'name'), continue());
            
            // Check if in same dimension
            if(query(p1, 'dimension') != query(p2, 'dimension'), continue());
            
            // Calculate distance
            dist = distance_between(p1, p2);
            
            // If close enough, send alert
            if(dist <= global_proximity_distance,
                pair_key = get_pair_key(p1, p2);
                last_alert = get(global_last_alerts, pair_key);
                
                // Only alert if enough time has passed since last alert for this pair
                if(last_alert == null || (current_time - last_alert) > global_alert_cooldown,
                    // Send proximity alert
                    name1 = query(p1, 'name');
                    name2 = query(p2, 'name');
                    
                    // Pick random message
                    msg_parts = global_proximity_messages:(floor(rand(length(global_proximity_messages))));
                    
                    // Build message by concatenating parts with player names
                    message = msg_parts:0 + name1 + msg_parts:1 + name2 + msg_parts:2;
                    
                    formatted_msg = format(
                        'g [', 'y Jens', 'g ] ',
                        'e ' + message
                    );
                    
                    // Broadcast to all players
                    for(player('all'),
                        print(_, formatted_msg)
                    );
                    
                    // Update last alert time
                    put(global_last_alerts, pair_key, current_time)
                )
            )
        )
    );
    
    // Check again in 5 seconds (100 ticks)
    schedule(100, check_proximity)
);

// Start checking when script loads
__on_start() -> (
    print('Proximity Alerts initialized! Watching for close encounters...');
    check_proximity()
);

// Command to toggle the system
__command() -> (
    global_enabled = !global_enabled;
    print(format(
        'g Proximity Alerts ',
        if(global_enabled, 'l enabled', 'r disabled')
    ))
);

// Command to adjust proximity distance
set_distance(blocks) -> (
    global_proximity_distance = blocks;
    print(format('g Proximity distance set to ', 'y ' + blocks, 'g  blocks'))
);