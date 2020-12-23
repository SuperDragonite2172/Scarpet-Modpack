// Power Tools - Hammer
// Version: 1.0
// Author: SuperDragonite2172
// Scarpet Version: 1.7
// Description: Convert a pickaxe into a hammer to clear a larger area.

// Documented to aid novice Scarpet scripters in learning how to use the language (like myself).

__config() ->
(
    m(l('stay_loaded', true))
);

// Global Variables
global_tool_list = l('wooden_pickaxe', 'stone_pickaxe','iron_pickaxe', 'diamond_pickaxe', 'netherite_pickaxe');
global_blocks_pick = l('polished_granite', 'polished_diorite', 'polished_andesite', 'ender_chest', 'anvil', 'coal_block', 'redstone_block', 'enchantment_table', 'iron_bars', 'iron_door', 'iron_trapdoor', 'spawner', 'bell', 'dispenser', 'dropper', 'observer', 'furnace', 'blast_furnace', 'smoker', 'stonecutter', 'lodestone', 'lantern', 'conduit', 'coal_ore', 'dragon_egg', 'end_stone', 'hopper', 'nether_quartz_ore', 'nether_gold_ore', 'grindstone', 'bone_block', 'brick_stairs', 'bricks', 'cauldron', 'cobblestone', 'cobblestone_stairs', 'cobblestone_walls', 'mossy_cobblestone', 'nether_bricks', 'red_nether_bricks', 'nether_brick_fence', 'nether_brick_stairs', 'end_stone_brick_slab', 'blackstone_slab', 'polished_blackstone_slab', 'polished_blackstone_brick_slab', 'brick_slab', 'cobblestone_slab', 'mossy_cobblestone_slab', 'red_nether_brick_slab', 'nether_brick_slab', 'petrified_oak_slab', 'purpur_slab', 'smooth_quartz_slab', 'quartz_slab', 'cut_sandstone_slab', 'smooth_sandstone_slab', 'sandstone_slab', 'cut_red_sandstone_slab', 'smooth_red_sandstone_slab', 'red_sandstone_slab', 'smooth_stone_slab', 'stone_slab', 'stone_brick_slab', 'andesite_slab', 'polished_andesite_slab', 'polished_diorite_slab', 'diorite_slab', 'granite_slab', 'polished_granite_slab', 'mossy_stone_brick_slab', 'prismarine_slab', 'prismarine_brick_slab', 'dark_prismarine_slab', 'polished_blackstone', 'white_concrete', 'orange_concrete', 'magenta_concrete', 'light_blue_concrete', 'yellow_concrete', 'lime_concrete', 'pink_concrete', 'gray_concrete', 'light_gray_concrete', 'cyan_concrete', 'purple_concrete', 'blue_concrete', 'brown_concrete', 'green_concrete', 'red_concrete', 'black_concrete', 'shulker_box', 'white_shulker_box', 'orange_shulker_box', 'magenta_shulker_box', 'light_blue_shulker_box', 'yellow_shulker_box', 'lime_shulker_box', 'pink_shulker_box', 'gray_shulker_box', 'light_gray_shulker_box', 'cyan_shulker_box', 'purple_shulker_box', 'blue_shulker_box', 'brown_shulker_box', 'green_shulker_box', 'red_shulker_box', 'black_shulker_box', 'andesite', 'dark_prismarine', 'diorite', 'granite', 'prismarine', 'prismarine_bricks', 'stone', 'smooth_stone', 'purpur_block', 'purpur_pillar', 'stone_bricks', 'stone_brick_stairs', 'blackstone', 'chiseled_polished_blackstone', 'polished_blackstone_bricks', 'gilded_blackstone', 'white_glazed_terracotta', 'orange_glazed_terracotta', 'magenta_glazed_terracotta', 'light_blue_glazed_terracotta', 'yellow_glazed_terracotta', 'lime_glazed_terracotta', 'pink_glazed_terracotta', 'gray_glazed_terracotta', 'light_gray_glazed_terracotta', 'cyan_glazed_terracotta', 'purple_glazed_terracotta', 'blue_glazed_terracotta', 'brown_glazed_terracotta', 'green_glazed_terracotta', 'red_glazed_terracotta', 'black_glazed_terracotta', 'white_terracotta', 'orange_terracotta', 'magenta_terracotta', 'light_blue_terracotta', 'yellow_terracotta', 'lime_terracotta', 'pink_terracotta', 'gray_terracotta', 'light_gray_terracotta', 'cyan_terracotta', 'purple_terracotta', 'blue_terracotta', 'brown_terracotta', 'green_terracotta', 'red_terracotta', 'black_terracotta', 'terracotta', 'basalt', 'polished_basalt', 'block_of_quartz', 'quartz_stairs', 'red_sandstone', 'red_standstone_stairs', 'sandstone', 'smooth_sandstone', 'sandstone_stairs', 'crimson_nylium', 'warped_nylium', 'rail', 'powered_rail', 'detector_rail', 'activator_rail', 'brewing_stand', 'stone_button', 'polished_blackstone_button', 'blue_ice', 'ice', 'magma_block', 'packed_ice', 'stone_pressure_plate', 'netherrack', 'end_rod');
global_blocks_pick_tier1 = l('iron_ore', 'iron_block', 'lapis_ore', 'lapis_block');
global_blocks_pick_tier2 = l('gold_ore', 'gold_block', 'redstone_ore','diamond_ore', 'diamond_block', 'emerald_ore', 'emerald_block');
global_blocks_pick_tier3 = l('ancient_debris', 'netherite_block', 'obsidian', 'crying_obsidian', 'respawn_anchor');
global_mode = '3x3';
global_area = null;

__on_player_switches_slot(player, from, to) ->
(
    // Item setup.
    hand_item = inventory_get(player, to);
    if(hand_item == null, return());
    l(item, count, tags) = hand_item;
    name = tags:'display.Name';
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'hammer', // If the item swapped to is a valid hammer, display the mode.
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
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'hammer', // If the item is a valid hammer, calculate the mining area.
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
    
    if(global_tool_list ~ item && lower(parse_nbt(name):'text') ~ 'hammer',
        for(global_area, 
            if(__is_whitelisted(_, tier),
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
    
    if(global_tool_list ~ item != null, // If the item is a valid hammer, swap the mode.
        if(global_mode == '3x3',
            global_mode = '5x5';,
        global_mode == '5x5',
            global_mode = '3x3';
        );
        __display_mode();
    );
);

__display_mode() -> run('title @s actionbar {"text":"[' + global_mode + ']","color":"red"}'); // Displays the hammer mode in the "action bar"

__calc_range(block_pos, face) -> // Calculates the area the hammer should mine, returns a rectangle range.
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

__is_whitelisted(block, tier) -> // Checks if a block should be broken by the hammer.  Returns the answer as a boolean.
(
    for(global_blocks_pick, if(block == _, return(true)));
    if(tier >= 1, for(global_blocks_pick_tier1, if(block == _, return(true))));
    if(tier >= 2, for(global_blocks_pick_tier2, if(block == _, return(true))));
    if(tier >= 3, for(global_blocks_pick_tier3, if(block == _, return(true))));
    return(false);
);