// Bedtime Reminder Script by Jens
// Sends escalating messages to players who stay up too late
// Messages start at 1:00 AM (in real time, not game time)

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global'
};

// Global variables to track state
global_enabled = true;
global_last_check_hour = -1;

// TIMEZONE SETTING - Change this to match your local UTC offset
// Germany: +1 (winter) or +2 (summer DST)
global_timezone_offset = 1;  // Change to 2 during summer time!

// Message schedule (hour -> message)
// Hours are in 24-hour format
global_messages = {
    1 -> 'It\'s getting late, friends... time to log off and go to bed',
    1.08 -> 'The mobs aren\'t the only thing that should be sleeping right now...',
    1.17 -> 'I\'m not angry, just disappointed... please go to bed',
    1.25 -> 'Your circadian rhythm is crying. Listen to it.',
    1.33 -> 'Even the Endermen are judging your life choices right now',
    1.42 -> 'Please, I\'m begging you, go touch some grass tomorrow',
    1.5 -> 'Fun fact: Sleep deprivation is literally torture. You\'re torturing yourself.',
    1.58 -> 'Your future self will either thank me or hate you. Guess which one?',
    1.67 -> 'The villagers are gossiping about your terrible sleep schedule',
    1.75 -> 'I\'m calling your mothers. Right now.',
    1.83 -> 'Your sleep schedule is as broken as a creeper-damaged house',
    1.92 -> 'Breaking news: Local gamer discovers they have a bed IRL too',
    2 -> 'Even zombies know when to rest',
    2.08 -> 'Your brain cells are filing a complaint with HR',
    2.17 -> 'At this rate, you\'ll see the sunrise. That\'s not a flex.',
    2.25 -> 'The Ender Dragon is judging your life choices',
    2.33 -> 'Minecraft addiction support hotline: 1-800-GO-SLEEP',
    2.42 -> 'Your immune system just sent me a strongly worded letter',
    2.5 -> 'At this point, you\'re basically a zombie yourself',
    2.58 -> 'I\'m telling your doctor. And your therapist. And your dentist for some reason.',
    2.67 -> 'The Iron Golem outside is concerned for your wellbeing',
    2.75 -> 'Your body needs sleep even if your Minecraft character doesn\'t',
    2.83 -> 'Scientists hate this one weird trick: it\'s called GOING TO BED',
    2.92 -> 'Achievement Unlocked: Terrible Life Decisions',
    3 -> 'This is your final warning. BED. NOW.',
    3.08 -> 'Your coffee tomorrow will be 90% regret, 10% caffeine',
    3.17 -> 'Even the server is tired. Look what you\'ve done.',
    3.25 -> 'I\'m logging your terrible life decisions. Go to sleep.',
    3.33 -> 'Your dark circles will have dark circles at this rate',
    3.42 -> 'The sheep are counting YOU now. They\'re up to 1,847.',
    3.5 -> 'Breaking: Local legend ruins tomorrow by staying up tonight',
    3.58 -> 'Your pillow is lonely. Don\'t make your pillow sad.',
    3.67 -> 'I\'m changing the server MOTD to "Sponsored by bad decisions"',
    3.75 -> 'The sun will rise in a few hours. You\'ve made a huge mistake.',
    3.83 -> 'Your ancestors didn\'t survive for millennia for THIS',
    3.92 -> 'You\'ve chosen chaos. Your health bar is depleting. SLEEP!',
    4 -> 'Congratulations! You\'ve achieved: INSOMNIA SPEEDRUN (Any%)',
    4.08 -> 'At this point I\'m just impressed by your commitment to self-destruction',
    4.17 -> 'Your body: "Am I a joke to you?"',
    4.25 -> 'The birds are starting to chirp. They\'re mocking you.',
    4.33 -> 'You know what? Fine. Stay up. See if I care. (I care very much)',
    4.42 -> 'Your tomorrow-self just traveled back in time to slap you. Felt that?',
    4.5 -> 'EMERGENCY BROADCAST: Please advise your bed of your coordinates',
    4.58 -> 'I\'m adding "professional night owl" to your player stats. Not a compliment.',
    4.67 -> 'The monsters under your bed left. Even THEY think you\'re too much.',
    4.75 -> 'You\'ve been awake so long you\'re approaching enlightenment. Or delirium. Hard to tell.',
    4.83 -> 'Your melatonin has filed for divorce. Citing "irreconcilable differences"',
    4.92 -> 'Actual zombies are calling YOU undead at this point',
    5 -> 'You\'ve won. You beat sleep. You also beat your own health bar. Worth it?'
};

// Function to get current real-world hour (with decimal minutes) in local timezone
get_current_hour() -> (
    // Get current Unix timestamp in milliseconds
    current_time = unix_time();
    // Convert to hours since epoch
    hours_since_epoch = current_time / (1000 * 60 * 60);
    // Add timezone offset to convert from UTC to local time
    hours_since_epoch = hours_since_epoch + global_timezone_offset;
    // Get hour of day (0-23) with decimal for minutes
    current_hour = (hours_since_epoch % 24);
    current_hour
);

// Function to send message to all players as "Jens"
send_jens_message(message) -> (
    formatted_msg = format(
        'g [', 'y Jens', 'g ] ',
        'i ' + message
    );
    for(player('all'),
        print(_, formatted_msg)
    )
);

// Main checking function
check_bedtime() -> (
    if(global_enabled,
        current_hour = get_current_hour();
        
        // Check each message time
        for(keys(global_messages),
            message_hour = _;
            
            // If we've passed this hour and haven't sent it yet
            if(current_hour >= message_hour && global_last_check_hour < message_hour,
                // Send the message
                send_jens_message(global_messages:message_hour);
                global_last_check_hour = message_hour
            )
        );
        
        // Reset at 6 AM so messages can fire again next night
        if(current_hour >= 6 && current_hour < 12,
            global_last_check_hour = -1
        )
    );
    
    // Schedule next check in 5 minutes (6000 ticks)
    schedule(6000, 'check_bedtime')
);

// Start the checking loop when script loads
__on_start() -> (
    print('Bedtime Reminder initialized! Jens will watch over your sleep schedule.');
    print(format('g Timezone offset: UTC+', 'y ' + global_timezone_offset));
    check_bedtime()
);

// Command to toggle the system
__command() -> (
    global_enabled = !global_enabled;
    print(format(
        'g Bedtime Reminder ',
        if(global_enabled, 'l enabled', 'r disabled')
    ))
);

// Test function to preview all messages
test() -> (
    print(format('y ===== BEDTIME REMINDER TEST MODE ====='));
    print(format('g Testing all messages from Jens:'));
    print(format('g Timezone: UTC+', 'y ' + global_timezone_offset));
    print('');
    
    // Get sorted list of message times
    message_times = keys(global_messages);
    
    // Send each message
    for(message_times,
        time_key = _;
        message = global_messages:time_key;
        
        // Calculate hours and minutes for display
        hour = floor(time_key);
        minutes = floor((time_key - hour) * 60);
        time_display = str('%d:%02d AM', hour, minutes);
        
        // Print timestamp
        print(format('y [' + time_display + ']'));
        
        // Send the actual message as Jens would
        send_jens_message(message);
        
        print('')
    );
    
    print(format('y ===== TEST COMPLETE ====='));
    print(format('g All ', 'l ' + length(message_times), 'g  messages have been displayed'));
);