// Power Tools - Excavator
// Version: 1.0
// Author: SuperDragonite2172
// Scarpet Version: 1.7
// Description: Convert a shovel into a excavator to clear a larger area.

// Documented to aid novice Scarpet scripters in learning how to use the language (like myself).

__config() ->
(
    m(l('stay_loaded', true))
);

// Global Variables
global_tool_list = l('wooden_shovel', 'stone_shovel','iron_shovel', 'diamond_shovel', 'netherite_shovel');
global_blocks_shovel = l('dirt', 'grass_block', 'grass_path', 'coarse_dirt', 'farmland', 'clay', 'sand', 'red_sand', 'soul_sand', 'soul_soil', 'white_concrete_powder', 'orange_concrete_powder', 'magenta_concrete_powder', 'light_blue_concrete_powder', 'yellow_concrete_powder', 'lime_concrete_powder', 'pink_concrete_powder', 'gray_concrete_powder', 'light_gray_concrete_powder', 'cyan_concrete_powder', 'purple_concrete_powder', 'blue_concrete_powder', 'brown_concrete_powder', 'green_concrete_powder', 'red_concrete_powder', 'black_concrete_powder', 'snow', 'snow_block');
global_mode = '3x3';
global_area = null;

__on_player_switches_slot(player, from, to) ->
(
    // Item setup.
    hand_item = inventory_get(player, to);
    if(hand_item == null, return());
    l(item, count, tags) = hand_item;
    name = tags:'display.Name';
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'excavator', // If the item swapped to is a valid excavator, display the mode.
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
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'excavator', // If the item is a valid excavator, calculate the mining area.
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
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'excavator',
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
    
    if(global_tool_list ~ item != null, // If the item is a valid excavator, swap the mode.
        if(global_mode == '3x3',
            global_mode = '5x5';,
        global_mode == '5x5',
            global_mode = '3x3';
        );
        __display_mode();
    );
);

__display_mode() -> run('title @s actionbar {"text":"[' + global_mode + ']","color":"red"}'); // Displays the excavator mode in the "action bar"

__calc_range(block_pos, face) -> // Calculates the area the excavator should mine, returns a rectangle range.
(
    l(x, y, z) = block_pos;
    if(
    // 3x3 centered around block broken.
    global_mode == '3x3',
        mining_area = if(
            face == 'up'    || face == 'down',  rect(x, y, z, 1, 0, 1),
            face == 'north' || face == 'south', rect(x, y, z, 1, 1, 0),
            face == 'east'  || face == 'west',  rect(x, y, z, 0, 1, 1)
        );,
    // 5x5 centered above block broken.
    global_mode == '5x5',
        mining_area = if(
            face == 'up'    || face == 'down',  rect(x, y, z, 2, 0, 2),
            face == 'north' || face == 'south', rect(x, y, z, -2, -1, 0, 2, 3, 0),
            face == 'east'  || face == 'west',  rect(x, y, z, 0, -1, -2, 0, 3, 2)
        );
    );
    return(mining_area);
);

__is_whitelisted(block) -> // Checks if a block should be broken by the excavator.  Returns the answer as a boolean.
(
    for(global_blocks_shovel, if(block == _, return(true)));
    return(false);
);