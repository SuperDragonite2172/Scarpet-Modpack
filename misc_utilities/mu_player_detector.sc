// Misc Utilities - Player Detector
// Version: 1.1
// Author: SuperDragonite2172
// Scarpet Version: 1.7
// Description: Adds the ability to "use" a named compass to locate a player.  Name the compass to their username and right click to get their coordinates and cardinal direction.

// Documented to aid novice Scarpet scripters in learning how to use the language (like myself).

// Note: Swap the 'run' commands for the 'print' commands if you want the script to print to chat instead of the actionbar.

__config() ->
(
    m(l('stay_loaded', true))
);

__on_player_uses_item(player, item_tuple, hand) ->
(
    l(item, count, nbt) = item_tuple || l('None', 0, null); // Maps the item tuple to variables. Lifted from gnembon's event_text.sc script.
    
    if (item ~ 'compass',
        
        if (nbt == null || nbt:'display' == null, return()); // If the compass is unnamed, do nothing.
        
        // Get the name from the compass and store it in "target".
        name = nbt:'display':'Name';
        if (type(name) != 'list', target = nbt(name));
        target = target:'text';
        
        if (target == 0, return());
        
        if (target == player, // If the target is the user, ask why they're tracking themselves.
            run('title @s actionbar {"text":"Why are you tracking yourself?","color":"red"}');
            // print('Why are you tracking yourself?');
            return();
        );
        if (__is_online(target) == 'true', // If the targeted player is found-
            
            target_dim = query(player(target), 'dimension'); // Current dimension of target.
            
            // Print the tracked player's name, dimension, coordinates, and direction relative to the player.
            run('title @s actionbar {"text":"' + query(player(target), 'pos') + ' - ' + title(target_dim) + '","color":"red"}');
            // print('Tracking ' + target + ':');
            // print('Dimension: ' + title(target_dim));
            // print('X: ' + floor(query(player(target), 'x')));
            // print('Y: ' + floor(query(player(target), 'y')));
            // print('Z: ' + floor(query(player(target), 'z')));
            // print('Direction: [' + __cardinal(player, target) + ']');
            
            // Variables to construct the new NBT.
            lode_pos = m(l('X', floor(query(player(target), 'x'))), l('Y', floor(query(player(target), 'y'))), l('Z', floor(query(player(target), 'z'))));
            new_nbt = str('{LodestoneDimension: "minecraft:%s", LodestoneTracked: 0b, LodestonePos: %s, display: {Name: \'{"text":"%s"}\'}}', target_dim, lode_pos, target);
            
            slot = player() ~ 'selected_slot'; // Slot of the compass
            
            inventory_set(player(), slot, 1, 'compass', new_nbt); // Updating the compass with the new coordinates.
        );
        if (__is_online(target) == 'false', // If the targeted player is not found, display an error.
            run('title @s actionbar {"text":"Error: Offline or invalid player","color":"red"}');
            // print('Error: Offline or invalid player');
        );
    );
);

__cardinal(p1, p2) -> // Calculates the cardinal direction from p1 to p2.  Returns a string containing the cardinal directions.
(
    // Sets coordinates of players to variables.
    l(p1x, p1y, p1z) = query(player(p1), 'pos');
    l(p2x, p2y, p2z) = query(player(p2), 'pos');
    
    // Calculate the angles based on player coordinates.
    p1m = (p2x - p1x);
    p2m = (p2z - p1z);
    angle = atan2(p1m, p2m);
    
    // Calculates the cardinal direction based on angle. 
    // Note: Scarpet if statements work as if else statements.
    if (-22.5  < angle && angle <=  22.5,  direction = 'South',
        22.5   < angle && angle <=  67.5,  direction = 'South-East',
        67.5   < angle && angle <=  112.5, direction = 'East',
        112.5  < angle && angle <=  167.5, direction = 'North-East',
        167.5  < angle || angle <= -167.5, direction = 'North',
        -167.5 < angle && angle <= -112.5, direction = 'North-West',
        -112.5 < angle && angle <= -67.5,  direction = 'West',
        -67.5  < angle && angle <= -22.5,  direction = 'South-West'
    );
    direction
);

__is_online(user) -> // Checks if the player is online, returns true if online, otherwise returns false.
(
    if ((player(user) == null), return('false'));
    return('true');
);