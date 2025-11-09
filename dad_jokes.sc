// Dad Jokes Bot by Jens
// Periodically tells terrible Minecraft-themed dad jokes to brighten everyone's day

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global'
};

// Global state
global_enabled = true;
global_last_joke_index = -1;

// Dad jokes collection - add more as you think of them!
// Format: Single strings are one-liners, lists are multi-part jokes with timing
global_jokes = [
    'Why did the Creeper cross the road? To get to the other SSSSSSIDE!',
    'What\'s a Minecraft player\'s favorite cereal? Miner-Os!',
    'Why don\'t Endermen ever win at poker? They always teleport when things get tense!',
    ['How do you make a tissue dance in Minecraft?', 'You put a little boogie in it...', 'wait, wrong dimension.'],
    'What do you call a pig with three eyes? A piiig!',
    'Why did the zombie go to school? To improve his DEAD-ucation!',
    'What\'s a skeleton\'s favorite instrument? The trom-BONE!',
    ['Why don\'t creepers ever go to parties?', 'They always blow up the fun!'],
    ['What do you call a cow with no legs? Ground beef.', 'What do you call a cow IN Minecraft?', 'Dinner.'],
    ['Why did Steve break up with his pickaxe?', 'It was too clingy - always hanging around his belt!'],
    'How does Steve stay in shape? He does block-ups and mine-ups!',
    ['What\'s a ghast\'s favorite music genre?', 'Soul music.', 'Get it? Because they cry... never mind.'],
    ['Why did the chicken cross the Nether?', 'Because it was too chicken to stay!'],
    'What do you call an Enderman who works out? Ripped and teleported!',
    'Why don\'t zombies ever win races? They\'re always dead last!',
    'What\'s a villager\'s favorite type of music? Hmmmm-etal!',
    ['Why did the player bring a ladder to the bar?', 'Because they heard the drinks were on the house!'],
    'What do you call a sheep covered in chocolate? A candy baa!',
    'Why don\'t skeletons fight each other? They don\'t have the guts!',
    'What\'s Steve\'s favorite type of story? A MINE-d blowing tale!',
    ['How do you organize a space party in Minecraft?', 'You planet...', 'in the End!'],
    'Why was the Minecraft player always tired? They kept mining their own business all night!',
    ['What do you call a nervous javelin?', 'Shakespeare!', 'Wait, that\'s a spear... I mean SHEEP!'],
    ['Why did the Enderman get a job at the post office?', 'He was great at handling blocks...', 'I mean packages!'],
    ['What\'s orange and sounds like a parrot?', 'A carrot!', '...This works in Minecraft too, trust me.'],
    'Why don\'t blazes ever get cold? Because they\'re always fired up!',
    ['What do you call a fake noodle in Minecraft?', 'An impasta!', '...made from wheat!'],
    ['Why did the creeper get a job?', 'Because it wanted to make a BANG in the industry!'],
    ['What\'s the best way to watch a Minecraft stream?', 'Sitting in a boat!', 'Because then you\'re IN the current!'],
    'Why don\'t witches ever ride their brooms? They prefer to fly economy class!',
    'What do you call a zombie that writes music? A decomposer!',
    'Why did the player go to art school? To learn how to draw their bow better!',
    'What\'s a mob\'s least favorite room? The living room!',
    'Why don\'t fish in Minecraft ever get stressed? They just go with the flow!',
    ['What did the dirt say to the rain?', 'If you keep this up, my name will be mud!'],
    ['Why was six afraid of seven?', 'Because seven eight nine!', '...Wait, that doesn\'t work in Minecraft. Nevermind.'],
    'What do you call a sleeping bull in Minecraft? A bulldozer!',
    ['Why did the Enderman refuse to fight?', 'He didn\'t want to make eye contact with his problems!'],
    'What\'s a spider\'s favorite thing to do? Spin web designs!',
    ['Why don\'t skeletons ever play music?', 'They have no organs!', 'Or a band... or friends.'],
    ['What do you call cheese that isn\'t yours in Minecraft?', 'Nacho cheese!', '...if we had nachos.'],
    ['I told my friend 10 Minecraft jokes to make him laugh.', 'Sadly, no pun in ten did.'],
    ['What\'s a creeper\'s favorite subject in school?', 'HisSSSSSStory!'],
    ['Why did the Minecraft player go broke?', 'Too many in-app purchases...', 'wait, wrong game.'],
    ['What do you call a line of rabbits hopping backwards?', 'A receding hare line!', 'Also works in Minecraft!']
];

// Function to send a random dad joke
send_random_joke() -> (
    if(!global_enabled, return());
    
    // Make sure we don't repeat the last joke
    available_jokes = length(global_jokes);
    joke_index = floor(rand(available_jokes));
    
    // If we got the same joke as last time and there's more than one joke, try again
    if(joke_index == global_last_joke_index && available_jokes > 1,
        joke_index = (joke_index + floor(rand(available_jokes - 1)) + 1) % available_jokes
    );
    
    global_last_joke_index = joke_index;
    joke = global_jokes:joke_index;
    
    // Check if it's a multi-part joke (list) or single joke (string)
    if(type(joke) == 'list',
        // Multi-part joke with timing
        _send_joke_part(joke, 0),
        // Single joke, send immediately
        _send_single_joke(joke)
    );
    
    // Schedule next joke in 10-20 minutes (12000-24000 ticks)
    random_delay = 12000 + floor(rand(12000));
    schedule(random_delay, 'send_random_joke')
);

// Helper to send a single joke line
_send_single_joke(joke_text) -> (
    formatted_msg = format(
        'g [', 'y Jens', 'g ] ',
        'l ' + joke_text
    );
    
    for(player('all'),
        print(_, formatted_msg)
    )
);

// Helper to send multi-part jokes with timing
_send_joke_part(joke_parts, part_index) -> (
    if(part_index >= length(joke_parts), return());
    
    // Send current part
    part_text = joke_parts:part_index;
    _send_single_joke(part_text);
    
    // Schedule next part after 1.5-2.5 seconds (30-50 ticks)
    if(part_index < length(joke_parts) - 1,
        delay = 30 + floor(rand(20));
        schedule(delay, '_send_joke_part', joke_parts, part_index + 1)
    )
);

// Start sending jokes when script loads
__on_start() -> (
    print('Dad Jokes Bot initialized! Prepare for groan-worthy humor...');
    
    // Send first joke after 5 minutes
    schedule(6000, 'send_random_joke')
);

// Command to toggle the system
__command() -> (
    global_enabled = !global_enabled;
    print(format(
        'g Dad Jokes Bot ',
        if(global_enabled, 'l enabled', 'r disabled')
    ))
);

// Command to tell a joke on demand
joke() -> (
    send_random_joke();
    print('Told a joke! Another one coming in 10-20 minutes...')
);