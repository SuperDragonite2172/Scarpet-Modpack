// Power Tools - Broadaxe
// Version: 1.0
// Author: SuperDragonite2172
// Scarpet Version: 1.7
// Description: Convert a axe into a broadaxe to clear a larger area.

// Documented to aid novice Scarpet scripters in learning how to use the language (like myself).

__config() ->
(
    m(l('stay_loaded', true))
);

// Global Variables
global_tool_list = l('wooden_axe', 'stone_axe','iron_axe', 'diamond_axe', 'netherite_axe');
global_blocks_axe = l('oak_planks', 'spruce_planks', 'birch_planks', 'jungle_planks', 'acacia_planks', 'dark_oak_planks', 'crimson_planks', 'warped_planks', 'oak_log', 'spruce_log', 'birch_log', 'jungle_log', 'acacia_log', 'dark_oak_log', 'crimson_stem', 'warped_stem', 'stripped_oak_log', 'stripped_spruce_log', 'stripped_birch_log', 'stripped_jungle_log', 'stripped_acacia_log', 'stripped_dark_oak_log', 'stripped_crimson_stem', 'stripped_warped_stem', 'oak_wood', 'spruce_wood', 'birch_wood', 'jungle_wood', 'acacia_wood', 'dark_oak_wood', 'crimson_hyphae', 'warped_hyphae', 'stripped_oak_wood', 'stripped_spruce_wood', 'stripped_birch_wood', 'stripped_jungle_wood', 'stripped_acacia_wood', 'stripped_dark_oak_wood', 'stripped_crimson_hyphae', 'stripped_warped_hyphae', 'oak_slab', 'spruce_slab', 'birch_slab', 'jungle_slab', 'acacia_slab', 'dark_oak_slab', 'crimson_slab', 'warped_slab', 'oak_stairs', 'spruce_stairs', 'birch_stairs', 'jungle_stairs', 'acacia_stairs', 'dark_oak_stairs', 'crimson_stairs', 'warped_stairs', 'oak_pressure_plate', 'spruce_pressure_plate', 'birch_pressure_plate', 'jungle_pressure_plate', 'acacia_pressure_plate', 'dark_oak_pressure_plate', 'crimson_pressure_plate', 'warped_pressure_plate', 'oak_fence', 'spruce_fence', 'birch_fence', 'jungle_fence', 'acacia_fence', 'dark_oak_fence', 'crimson_fence', 'warped_fence', 'oak_trapdoor', 'spruce_trapdoor', 'birch_trapdoor', 'jungle_trapdoor', 'acacia_trapdoor', 'dark_oak_trapdoor', 'crimson_trapdoor', 'warped_trapdoor', 'oak_fence_gate', 'spruce_fence_gate', 'birch_fence_gate', 'jungle_fence_gate', 'acacia_fence_gate', 'dark_oak_fence_gate', 'crimson_fence_gate', 'warped_fence_gate', 'oak_button', 'spruce_button', 'birch_button', 'jungle_button', 'acacia_button', 'dark_oak_button', 'crimson_button', 'warped_button', 'oak_door', 'spruce_door', 'birch_door', 'jungle_door', 'acacia_door', 'dark_oak_door', 'crimson_door', 'warped_door', 'oak_sign', 'spruce_sign', 'birch_sign', 'jungle_sign', 'acacia_sign', 'dark_oak_sign', 'crimson_sign', 'warped_sign', 'chest', 'trapped_chest', 'lectern', 'smithing_table', 'loom', 'cartography_table', 'fletching_table', 'barrel', 'jukebox', 'campfire', 'bookshelf', 'white_banner', 'orange_banner', 'magenta_banner', 'light_blue_banner', 'yellow_banner', 'lime_banner', 'pink_banner', 'gray_banner', 'light_gray_banner', 'cyan_banner', 'purple_banner', 'blue_banner', 'brown_banner', 'green_banner', 'red_banner', 'black_banner', 'jack_o_lantern', 'melon', 'pumpkin', 'carved_pumpkin', 'note_block', 'ladder', 'bee_nest', 'composter', 'bamboo', 'bed', 'cocoa', 'daylight_detector', 'brown_mushroom_block', 'red_mushroom_block', 'mushroom_stem', 'vines');
global_mode = '3x3x3';
global_area = null;

__on_player_switches_slot(player, from, to) ->
(
    // Item setup.
    hand_item = inventory_get(player, to);
    if(hand_item == null, return());
    l(item, count, tags) = hand_item;
    name = tags:'display.Name';
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'broadaxe', // If the item swapped to is a valid broadaxe, display the mode.
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
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'broadaxe', // If the item is a valid broadaxe, calculate the mining area.
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
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'broadaxe',
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
    
    if(global_tool_list ~ item != null, // If the item is a valid broadaxe, swap the mode.
        if(global_mode == '3x3x3',
            global_mode = 'Tree';,
        global_mode == 'Tree',
            global_mode = '3x3x3';
        );
        __display_mode();
    );
);

__display_mode() -> run('title @s actionbar {"text":"[' + global_mode + ']","color":"red"}'); // Displays the broadaxe mode in the "action bar"

__calc_range(block_pos, face) -> // Calculates the area the broadaxe should mine, returns a rectangle range.
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
    // Tree mode (24 blocks up from block broken).
    global_mode == 'Tree',
        mining_area = if(
            face == 'up'    || face == 'down',  rect(x, y, z, 0, 0, 0, 0, 24, 0),
            face == 'north' || face == 'south', rect(x, y, z, 0, 0, 0, 0, 24, 0),
            face == 'east'  || face == 'west',  rect(x, y, z, 0, 0, 0, 0, 24, 0)
        );
    );
    return(mining_area);
);

__is_whitelisted(block) -> // Checks if a block should be broken by the broadaxe.  Returns the answer as a boolean.
(
    for(global_blocks_axe, if(block == _, return(true)));
    return(false);
);