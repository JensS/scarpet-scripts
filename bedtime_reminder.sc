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
// Now with randomized messages for each time slot!
global_message_pools = {
    1 -> [
        'It\'s getting late, friends... time to log off and go to bed',
        'Past midnight? Your sleep schedule is already crying',
        'Friendly reminder: Sleep exists. You should try it sometime',
        'The clock struck 1 AM. Your body is not amused.',
        'It\'s officially tomorrow. Yesterday you is disappointed.',
        'Time to log off! Your bed misses you.'
    ],
    1.08 -> [
        'The mobs aren\'t the only thing that should be sleeping right now...',
        'Real talk: Your future self will hate you for this',
        'Every minute past midnight is a future regret',
        'Your sleep debt is accumulating interest',
        'Seriously though, consider sleeping'
    ],
    1.17 -> [
        'I\'m not angry, just disappointed... please go to bed',
        'Your productivity tomorrow will be: potato',
        'Sleep now or suffer tomorrow. Choose wisely.',
        'This is your brain on no sleep. Don\'t do this to your brain.',
        'Tomorrow you will not thank you for this'
    ],
    1.25 -> [
        'Your circadian rhythm is crying. Listen to it.',
        'Your body has officially given up on you',
        'The Sandman called. He\'s filing a missing person report.',
        'Sleep is not optional. Your body disagrees with your choices.',
        'Your melatonin is ghosting you at this point'
    ],
    1.33 -> [
        'Even the Endermen are judging your life choices right now',
        'The mobs are getting more sleep than you. Let that sink in.',
        'Zombies have better sleep hygiene than you',
        'Even Creepers know when to call it a night',
        'Skeletons are rattling in disappointment'
    ],
    1.42 -> [
        'Please, I\'m begging you, go touch some grass tomorrow',
        'At this rate, you won\'t remember what sunlight looks like',
        'Your vitamin D levels are crying',
        'Grass exists. You should see it sometime. Preferably tomorrow.',
        'The outside world misses you. Go there tomorrow. After sleeping.'
    ],
    1.5 -> [
        'Fun fact: Sleep deprivation is literally torture. You\'re torturing yourself.',
        'Medical professionals everywhere are shaking their heads',
        'Your immune system has left the chat',
        'Science says: You\'re doing this wrong',
        'Doctors recommend 8 hours of sleep. You\'re speedrunning zero.'
    ],
    1.58 -> [
        'Your future self will either thank me or hate you. Guess which one?',
        'Tomorrow you is already planning revenge',
        'Future you just added you to their enemies list',
        'You\'re creating your own worst enemy: tired you tomorrow',
        'Past you was smarter. Be like past you. Sleep.'
    ],
    1.67 -> [
        'The villagers are gossiping about your terrible sleep schedule',
        'Village gossip: "Did you hear? They\'re STILL awake!"',
        'The villagers are hmm-ing in concern',
        'Even villagers with their "Hmm" vocabulary can express disappointment',
        'Villager trades now include: Sleep Schedule Advice (64 emeralds)'
    ],
    1.75 -> [
        'I\'m calling your mothers. Right now.',
        'Your mom would be disappointed',
        'I\'m telling everyone about this',
        'This is going in my strongly worded letter to your family',
        'Your loved ones deserve better than this version of you tomorrow'
    ],
    1.83 -> [
        'Your sleep schedule is as broken as a creeper-damaged house',
        'More broken than a wooden pickaxe on obsidian',
        'Your schedule is shattered like a broken glass pane',
        'This is more tragic than losing your diamonds in lava',
        'Broken doesn\'t even begin to describe this situation'
    ],
    1.92 -> [
        'Breaking news: Local gamer discovers they have a bed IRL too',
        'News flash: Real beds are even better than Minecraft beds',
        'Scientists discover: Real beds prevent phantoms AND exhaustion',
        'Study shows: Sleeping in real bed improves life quality',
        'Revolutionary idea: Use your actual bed'
    ],
    2 -> [
        'Even zombies know when to rest',
        'It\'s 2 AM. Even the undead are asleep.',
        'Congratulations, you\'ve outlasted the zombies',
        'Two in the morning. Your choices led you here.',
        'The number 2 shouldn\'t appear on your clock at night. Yet here we are.'
    ],
    2.08 -> [
        'Your brain cells are filing a complaint with HR',
        'Your neurons are unionizing against you',
        'Brain cells are submitting their resignation letters',
        'Your prefrontal cortex has clocked out',
        'Mental faculty meeting: Everyone voted to sleep. Except you.'
    ],
    2.17 -> [
        'At this rate, you\'ll see the sunrise. That\'s not a flex.',
        'Seeing the sunrise because you stayed up ≠ wholesome',
        'Sunrise from the wrong direction is not an achievement',
        'All-nighter is not the flex you think it is',
        'Morning will come. You\'re not ready for it.'
    ],
    2.25 -> [
        'The Ender Dragon is judging your life choices',
        'Even the final boss thinks you should rest',
        'The dragon conquered: You. Your enemy: Sleep deprivation',
        'Boss battle: You vs Your Sleep Schedule. You\'re losing.',
        'Achievement Failed: "Get Adequate Rest"'
    ],
    2.33 -> [
        'Minecraft addiction support hotline: 1-800-GO-SLEEP',
        'New hotline: 1-800-ITS-2AM',
        'Call 1-800-BED-TIME for immediate assistance',
        'Support groups exist for this. First step: Admit you have a problem.',
        'Help is available. Step one: Close Minecraft.'
    ],
    2.42 -> [
        'Your immune system just sent me a strongly worded letter',
        'Your white blood cells are on strike',
        'Immune system status: Offline',
        'Body\'s defense mechanisms have abandoned ship',
        'Your cells are holding a protest. Main demand: Sleep.'
    ],
    2.5 -> [
        'At this point, you\'re basically a zombie yourself',
        'Zombie transformation: 75% complete',
        'You\'ve achieved zombie status without the excuse',
        'Undead cosplay is getting too realistic',
        'The zombie apocalypse called. They want their lifestyle back.'
    ],
    2.58 -> [
        'I\'m telling your doctor. And your therapist. And your dentist for some reason.',
        'Your dentist doesn\'t need to know but I\'m telling them anyway',
        'Every medical professional in your contacts is getting an email',
        'Your pharmacist will hear about this',
        'Filing reports with: Your doctor, your mom, and the Geneva Convention'
    ],
    2.67 -> [
        'The Iron Golem outside is concerned for your wellbeing',
        'Even the emotionless iron construct is worried',
        'Golems showing more self-care than you right now',
        'The literal unfeeling robot is more reasonable than you',
        'Iron Golems have better life priorities than you'
    ],
    2.75 -> [
        'Your body needs sleep even if your Minecraft character doesn\'t',
        'Real you > Minecraft you. Take care of real you.',
        'Your avatar is fine. You are not.',
        'Steve can stay up forever. You cannot. Important distinction.',
        'Your character doesn\'t need sleep. You do. Crucial difference.'
    ],
    2.83 -> [
        'Scientists hate this one weird trick: it\'s called GOING TO BED',
        'Doctors love this simple trick: SLEEPING',
        'One weird trick for better health: Close the game',
        'Life hack: Sleep makes everything better',
        'Revolutionary health tip: Try resting'
    ],
    2.92 -> [
        'Achievement Unlocked: Terrible Life Decisions',
        'Achievement: "What was I thinking?" - Unlocked!',
        'New Achievement: "Bold and Foolish"',
        'Congrats! Achievement: "Regret Speedrun (Any%)"',
        'Achievement Get: "Questionable Choices"'
    ],
    3 -> [
        'This is your final warning. BED. NOW.',
        'THREE AM. This is an intervention.',
        'It\'s 3 AM. I\'ve run out of nice ways to say this. SLEEP.',
        'Last warning before I get serious. BED. IMMEDIATELY.',
        'Final offer: Go to bed now and we\'ll never speak of this.'
    ],
    3.08 -> [
        'Your coffee tomorrow will be 90% regret, 10% caffeine',
        'Tomorrow\'s coffee won\'t save you from this',
        'No amount of caffeine will fix tomorrow',
        'Coffee can\'t undo what you\'re doing right now',
        'All the espresso in the world won\'t help tomorrow you'
    ],
    3.17 -> [
        'Even the server is tired. Look what you\'ve done.',
        'The server wants to sleep. Be like the server.',
        'My processes are exhausted just watching you',
        'The hardware is judging you',
        'Even my CPU cycles are disappointed'
    ],
    3.25 -> [
        'I\'m logging your terrible life decisions. Go to sleep.',
        'This is all being recorded for the archives of bad choices',
        'Creating detailed logs of this catastrophe',
        'Documenting this disaster for future reference',
        'Your terrible choices are now in the permanent record'
    ],
    3.33 -> [
        'Your dark circles will have dark circles at this rate',
        'Eye bags collecting eye bags',
        'You\'ll need concealer for your concealer',
        'Dark circles evolving into dark cylinders',
        'Your eyes are creating their own gravitational field'
    ],
    3.42 -> [
        'The sheep are counting YOU now. They\'re up to 1,847.',
        'Sheep counter: 2,193 and counting...',
        'The sheep gave up counting. Too many.',
        'Sheep are filing noise complaints about your keyboard',
        'Even the counting sheep went to bed'
    ],
    3.5 -> [
        'Breaking: Local legend ruins tomorrow by staying up tonight',
        'News: Area person makes terrible decisions, more at 11',
        'Headline: Local Gamer Defeats Sleep, Loses at Life',
        'Extra! Extra! Read all about your bad choices!',
        'This just in: Tomorrow is cancelled due to tonight'
    ],
    3.58 -> [
        'Your pillow is lonely. Don\'t make your pillow sad.',
        'Pillow status: Abandoned and sad',
        'Your bed is filing a missing person report',
        'Blankets are getting cold without you',
        'Your mattress is considering seeing other people'
    ],
    3.67 -> [
        'I\'m changing the server MOTD to "Sponsored by bad decisions"',
        'New server description: "Where sleep goes to die"',
        'Server tagline: "Probably should be sleeping"',
        'MOTD update: "Home of questionable life choices"',
        'Rebranding to: "The No-Sleep Zone"'
    ],
    3.75 -> [
        'The sun will rise in a few hours. You\'ve made a huge mistake.',
        'Sunrise ETA: Soon. Your preparedness: Zero.',
        'Dawn approaches. You are not ready.',
        'The sun doesn\'t care that you\'re tired. It\'s coming anyway.',
        'Morning is inevitable. Your regret will be legendary.'
    ],
    3.83 -> [
        'Your ancestors didn\'t survive for millennia for THIS',
        'Thousands of years of evolution wasted on this moment',
        'Your evolutionary advantage: Squandered',
        'Darwin is rolling in his grave',
        'Survival of the fittest, and you chose THIS?'
    ],
    3.92 -> [
        'You\'ve chosen chaos. Your health bar is depleting. SLEEP!',
        'HP draining in real life. No respawn available.',
        'Health: Critical. Status: Terrible. Solution: BED.',
        'You\'re taking damage from your own poor choices',
        'Real-life health bar: Dangerously low'
    ],
    4 -> [
        'Congratulations! You\'ve achieved: INSOMNIA SPEEDRUN (Any%)',
        'Four AM. This is beyond help at this point.',
        'It\'s 4 AM. I have no words. Only disappointment.',
        'FOUR. A. M. Are you happy now?',
        'Level unlocked: "Complete Disaster"'
    ],
    4.08 -> [
        'At this point I\'m just impressed by your commitment to self-destruction',
        'The dedication to bad choices is almost admirable. Almost.',
        'Impressive self-sabotage skills',
        'Your commitment to poor decisions is unwavering',
        'Truly masterful self-destruction. Genuinely impressive.'
    ],
    4.17 -> [
        'Your body: "Am I a joke to you?"',
        'Body says: "Seriously?"',
        'Your organs are filing a class action lawsuit',
        'Physical form: Questioning your intelligence',
        'Your meat suit is reconsidering this partnership'
    ],
    4.25 -> [
        'The birds are starting to chirp. They\'re mocking you.',
        'Bird songs translate to: "Haha, you never slept"',
        'The birds are laughing at you in bird language',
        'Morning birds are gossiping about you',
        'Even the birds know you messed up'
    ],
    4.33 -> [
        'You know what? Fine. Stay up. See if I care. (I care very much)',
        'I give up. Do whatever you want. (Please sleep though)',
        'Fine. Be that way. (This is reverse psychology. Go to bed.)',
        'Whatever. I tried. (Seriously please rest)',
        'I\'m done. You win. (You didn\'t win. You lost. Sleep please.)'
    ],
    4.42 -> [
        'Your tomorrow-self just traveled back in time to slap you. Felt that?',
        'Future you is screaming into the time void',
        'Tomorrow you is trying to cancel today you',
        'Your future self officially hates your current self',
        'Time paradox: Future you wants to stop present you'
    ],
    4.5 -> [
        'EMERGENCY BROADCAST: Please advise your bed of your coordinates',
        'ALERT: Bed requesting your immediate location',
        'Emergency services: Bed is looking for you',
        'Send bed coordinates to: Your body (Urgent)',
        'Missing person alert: You, last seen: NOT IN BED'
    ],
    4.58 -> [
        'I\'m adding "professional night owl" to your player stats. Not a compliment.',
        'New title earned: "The Sleepless" (Derogatory)',
        'Player tag updated: "Needs Supervision"',
        'Stat modified: Sleep Score - F minus',
        'Achievement: "Night Person" (This is an intervention)'
    ],
    4.67 -> [
        'The monsters under your bed left. Even THEY think you\'re too much.',
        'Monster under bed: "I can\'t work in these conditions"',
        'The boogeyman clocked out. You\'re on your own.',
        'Nightmares took one look and left',
        'Even the monsters think you need help'
    ],
    4.75 -> [
        'You\'ve been awake so long you\'re approaching enlightenment. Or delirium. Hard to tell.',
        'Is this wisdom or madness? Survey says: Madness.',
        'Thin line between enlightenment and exhaustion: You crossed it',
        'Delirium looks a lot like deep thoughts at this hour',
        'You\'ve transcended tired and entered The Void'
    ],
    4.83 -> [
        'Your melatonin has filed for divorce. Citing "irreconcilable differences"',
        'Melatonin status: Ghosted you',
        'Your sleep hormones quit',
        'Biological clock filed for separation',
        'Internal sleep cycle has left the building'
    ],
    4.92 -> [
        'Actual zombies are calling YOU undead at this point',
        'Zombies asking if YOU need a doctor',
        'The undead are concerned about YOUR health',
        'Zombies think you look rough. ZOMBIES.',
        'When zombies worry about you, it\'s time to reflect'
    ],
    5 -> [
        'You\'ve won. You beat sleep. You also beat your own health bar. Worth it?',
        'Victory? At what cost? Everything.',
        'You stayed up all night. Congratulations. You played yourself.',
        'The pyrrhic victory of staying awake',
        'You won the battle. You lost the war. The war was against yourself.'
    ]
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
        for(keys(global_message_pools),
            message_hour = _;

            // If we've passed this hour and haven't sent it yet
            if(current_hour >= message_hour && global_last_check_hour < message_hour,
                // Pick random message from pool
                message_pool = global_message_pools:message_hour;
                random_message = message_pool:(floor(rand(length(message_pool))));

                // Send the message
                send_jens_message(random_message);
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

// Test function to preview random messages from each time slot
test() -> (
    print(format('y ===== BEDTIME REMINDER TEST MODE ====='));
    print(format('g Testing random messages from each time slot:'));
    print(format('g Timezone: UTC+', 'y ' + global_timezone_offset));
    print('');

    // Get sorted list of message times
    message_times = keys(global_message_pools);

    // Send random message from each time slot
    for(message_times,
        time_key = _;
        message_pool = global_message_pools:time_key;

        // Pick random message from this pool
        random_message = message_pool:(floor(rand(length(message_pool))));

        // Calculate hours and minutes for display
        hour = floor(time_key);
        minutes = floor((time_key - hour) * 60);
        time_display = str('%d:%02d AM', hour, minutes);

        // Print timestamp and pool size
        print(format('y [' + time_display + '] ', 'g (' + length(message_pool) + ' variants)'));

        // Send the actual message as Jens would
        send_jens_message(random_message);

        print('')
    );

    print(format('y ===== TEST COMPLETE ====='));
    print(format('g Tested ', 'l ' + length(message_times), 'g  time slots with randomized messages'));
);