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
    ['What do you call a line of rabbits hopping backwards?', 'A receding hare line!', 'Also works in Minecraft!'],
    'Why do Minecraft players make terrible comedians? Their jokes are too blocky!',
    ['What did the dirt block say to the grass block?', 'You\'re looking sharp today!'],
    'Why don\'t iron golems ever get invited to parties? They\'re too rigid!',
    ['What\'s a creeper\'s favorite music genre?', 'Boom-bap!'],
    'Why did the player bring a map to the restaurant? To find their way to the food court!',
    ['What do you call a nervous mine?', 'A mine field!', '...wait.'],
    'Why don\'t skeletons use phones? They have no body to call!',
    ['What\'s a zombie\'s favorite shampoo?', 'Head and Shoulders... because they keep losing theirs!'],
    'Why was the Enderman such a good basketball player? He was always teleporting to the hoop!',
    ['What do you call a pig that does karate?', 'A pork chop!'],
    'Why don\'t zombies eat clowns? They taste funny!',
    ['What\'s a witch\'s favorite subject?', 'Spelling!'],
    'How do you make a redstone torch? You give it a really inspiring speech!',
    ['Why did the creeper fail art class?', 'It kept blowing up its projects!'],
    'What do you call a baby enderman? A short teleporter!',
    ['Why don\'t villagers ever win at hide and seek?', 'Because they always go "Hmmmm"!'],
    'What\'s Steve\'s favorite exercise? Block-ups!',
    ['Why did the zombie go to the doctor?', 'He was coffin!'],
    'What do you call a Minecraft player who never mines? A lazy Susan... or just lazy!',
    ['What\'s a skeleton\'s least favorite room?', 'The living room!'],
    'Why do creepers make terrible friends? They always blow up at you!',
    ['What did one ocean say to the other ocean?', 'Nothing, they just waved!', 'Works in Minecraft too!'],
    'Why are ghasts always crying? Because the Nether is so hot!',
    ['What do you call a pile of cats?', 'A meow-ntain!'],
    'Why did the player eat their clock? They wanted to go back four seconds!',
    ['What\'s orange and sounds like a parrot?', 'A carrot!', 'Classic, but good!'],
    'Why don\'t blazes ever lose at poker? They always have a hot hand!',
    ['What do you call a sleeping dinosaur in Minecraft?', 'A dino-snore!', 'If we had dinos...'],
    'Why was the math book sad? It had too many problems!',
    ['What\'s a spider\'s favorite pastime?', 'Web surfing!'],
    'Why did Steve break up with his girlfriend? She took him for granite!',
    ['What do you call a cow with a twitch?', 'Beef jerky!'],
    'Why don\'t eggs tell jokes? They\'d crack up!',
    ['What\'s the best thing about Switzerland?', 'I don\'t know, but the flag is a big plus!', 'Wrong game again...'],
    'Why did the scarecrow win an award? He was outstanding in his field!',
    ['What do you call a bear with no teeth?', 'A gummy bear!'],
    'Why don\'t scientists trust atoms? Because they make up everything!',
    ['What did the ocean say to the beach?', 'Nothing, it just waved!'],
    'Why do bees have sticky hair? Because they use honeycombs!',
    ['What do you call a fake stone?', 'A sham rock!'],
    'Why did the bicycle fall over? It was two-tired!',
    ['What do you call a can opener that doesn\'t work?', 'A can\'t opener!'],
    'Why don\'t oysters donate to charity? Because they\'re shellfish!',
    ['What do you call a belt made of watches?', 'A waist of time!'],
    'Why did the coffee file a police report? It got mugged!',
    ['What do you call a snowman in summer?', 'A puddle!'],
    'Why don\'t skeletons fight each other? They don\'t have the guts!',
    ['What do you call a parade of rabbits hopping backwards?', 'A receding hare line!'],
    'Why did the player bring a ladder to the bar? Drinks were on the house!',
    ['What\'s the difference between a poorly dressed man on a bike and a well-dressed man on a unicycle?', 'Attire!'],
    'Why did the mushroom go to the party? He was a fungi!',
    ['What do you call a pile of kittens?', 'A meow-tain!'],
    'Why don\'t programmers like nature? It has too many bugs!',
    ['What did the left eye say to the right eye?', 'Between you and me, something smells!'],
    'Why did the player go to the bank? To check their balance!',
    ['What do you call a fish wearing a crown?', 'King of the sea!'],
    'Why don\'t mountains ever get cold? They wear snow caps!',
    ['What do you call a sleeping bull?', 'A bulldozer!'],
    'Why did the golfer bring two pairs of pants? In case he got a hole in one!',
    ['What do you call a dinosaur that crashes his car?', 'Tyrannosaurus Wrecks!'],
    'Why can\'t you hear a pterodactyl using the bathroom? The P is silent!',
    ['What do you call a boomerang that doesn\'t come back?', 'A stick!'],
    'Why did the player eat his homework? The teacher said it was a piece of cake!',
    ['What do you call a magic dog?', 'A labracadabrador!'],
    'Why do cows wear bells? Because their horns don\'t work!',
    ['What do you call a sad coffee?', 'Depresso!'],
    'Why did the Enderman refuse to fight in the rain? He didn\'t want to get washed up!',
    ['What\'s a ghast\'s favorite game?', 'Cry and seek!'],
    'Why don\'t mobs ever win spelling bees? They can\'t spell "survival"!',
    ['What did the dirt say when it rained?', 'If this keeps up, my name will be mud!'],
    'Why was the creeper invited to every party? He always brought the BOOM!',
    ['What do you call a villager who sells maps?', 'A navigation specialist!', 'Or just... a cartographer.'],
    'Why did the chicken join a band? Because it had the drumsticks!',
    ['What\'s a skeleton\'s favorite instrument?', 'The trom-BONE!', 'Yes, I used this one already. Sue me.'],
    'Why don\'t trees use computers? They prefer to log in naturally!',
    ['What do you call a hot dog in Minecraft?', 'A frank discussion!', 'If we had hot dogs...'],
    'Why did the player bring a pencil to bed? To draw the curtains!',
    ['What\'s the best way to watch a fly fishing tournament?', 'Live stream!'],
    'Why don\'t eggs tell each other secrets? They might crack up!',
    ['What do you call a sleeping pizza?', 'A piZZZZa!'],
    'Why did Steve install a doorbell? He wanted to know when opportunity knocked!',
    ['What do you call a knight who\'s afraid to fight?', 'Sir Render!'],
    'Why do bees have sticky hair in Minecraft? Honey combs!',
    ['What\'s brown and sticky?', 'A stick!', 'Literally the most dad joke ever.'],
    'Why did the player take a ruler to bed? To see how long they slept!',
    ['What do you call a shoe made of a banana?', 'A slipper!'],
    'Why don\'t some couples go to the Nether? They don\'t want to make a scene!',
    ['What do you call a pony with a cough?', 'A little horse!'],
    'Why did the tomato turn red? It saw the salad dressing!',
    ['What do you call an alligator in a vest?', 'An investigator!'],
    'Why don\'t ants ever get sick? They have tiny ant-ibodies!',
    ['What do you call a deer with no eyes?', 'No eye-deer!', 'I have no idea!'],
    'Why did the player wear sunglasses in the mine? The future was so bright!',
    ['What do you call a factory that makes okay products?', 'A satisfactory!']
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