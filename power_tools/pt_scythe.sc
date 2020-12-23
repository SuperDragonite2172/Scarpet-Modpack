// Power Tools - Scythe
// Version: 1.0
// Author: SuperDragonite2172
// Scarpet Version: 1.7
// Description: Convert a hoe into a scythe to clear a larger area.

// Documented to aid novice Scarpet scripters in learning how to use the language (like myself).

__config() ->
(
    m(l('stay_loaded', true))
);

// Global Variables
global_tool_list = l('wooden_hoe', 'stone_hoe','iron_hoe', 'diamond_hoe', 'netherite_hoe');
global_blocks_hoe = l('nether_wart_block', 'warped_wart_block', 'shroomlight', 'hay_bale', 'target', 'dried_kelp_block', 'sponge', 'wet_sponge', 'oak_leaves', 'spruce_leaves', 'birch_leaves', 'jungle_leaves', 'acacia_leaves', 'dark_oak_leaves', 'wheat', 'melon_stem', 'pumpkin_stem', 'beetroots');
global_mode = '3x3x3';
global_area = null;

__on_player_switches_slot(player, from, to) ->
(
    // Item setup.
    hand_item = inventory_get(player, to);
    if(hand_item == null, return());
    l(item, count, tags) = hand_item;
    name = tags:'display.Name';
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'scythe', // If the item swapped to is a valid scythe, display the mode.
        __display_mode();
    );
);

__on_player_clicks_block(player, block, face) ->
(
    // Item setup.
    hand_item = player ~ 'holds';
    if(hand_item == null, return());
    l(item, count, tags) = hand_item;
    name = tags:'display.Name';
    global_area = null;
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'scythe', // If the item is a valid scythe, calculate the mining area.
        global_area = __calc_range(pos(block), face);
    );
);

__on_player_breaks_block(player, block) ->
(
    // Item setup.
    hand_item = player ~ 'holds';
    if(hand_item == null, return());
    l(item, count, tags) = hand_item;
    name = tags:'display.Name';
    tier = global_tool_list ~ item; // Store the tool tier for a later check.
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'scythe',
        for(global_area, 
            if(__is_whitelisted(_),
                if(hardness(_) <= hardness(block),
                    harvest(player, _)
                );
            );
        );
    );
);

__on_player_uses_item(player, item_tuple, hand) ->
(
    l(item, count, nbt) = item_tuple || l('None', 0, null);
    
    if(global_tool_list ~ item != null, // If the item is a valid scythe, swap the mode.
        if(global_mode == '3x3x3',
            global_mode = '5x5x5';,
        global_mode == '5x5x5',
            global_mode = '3x3x3';
        );
        __display_mode();
    );
);

__display_mode() -> run('title @s actionbar {"text":"[' + global_mode + ']","color":"red"}'); // Displays the scythe mode in the "action bar"

__calc_range(block_pos, face) -> // Calculates the area the scythe should mine, returns a rectangle range.
(
    l(x, y, z) = block_pos;
    if(
    // 3x3x3 centered around block broken.
    global_mode == '3x3x3',
        mining_area = if(
            face == 'up'    || face == 'down',  rect(x, y, z, 1, 1, 1),
            face == 'north' || face == 'south', rect(x, y, z, 1, 1, 1),
            face == 'east'  || face == 'west',  rect(x, y, z, 1, 1, 1)
        );,
    // 5x5x5 centered above block broken.
    global_mode == '5x5x5',
        mining_area = if(
            face == 'up'    || face == 'down',  rect(x, y, z, 2, 2, 2),
            face == 'north' || face == 'south', rect(x, y, z, 2, 2, 2),
            face == 'east'  || face == 'west',  rect(x, y, z, 2, 2, 2)
        );
    );
    return(mining_area);
);

__is_whitelisted(block) -> // Checks if a block should be broken by the scythe.  Returns the answer as a boolean.
(
    for(global_blocks_hoe, if(block == _, return(true)));
    return(false);
);