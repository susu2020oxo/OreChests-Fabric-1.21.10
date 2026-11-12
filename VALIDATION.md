# v1.2.0 Verification

Minecraft 1.21.10 / Java 21 / Fabric Loader 0.17.3 / Fabric API 0.138.4+1.21.10

Build: PASS
Dedicated server integration: PASS
Client renderer + integrated world: PASS
Texture alpha/size and native diamond identity: PASS
All 18 ore variants mineral RGBA retention: PASS

Health checks exercise real food consumption, milk, potion exclusion, placed cake, full player NBT save/load and fresh-player death/alive replacement. Gear checks exercise all tool/armor materials and real bow/crossbow/trident entity hits. Other hostile traits remain as in v1.1; no claim that each has been individually combat-tested. Client test used software graphics without an audio device.

```text
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: wooden pickaxe durability doubled
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: diamond mining speed boosted
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: stone tool attack damage boosted
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: grass drops diamond fragment
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: stone still drops cobblestone
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: wood progression preserved
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: three fragments craft one diamond
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: diamond beef smelting recipe loaded
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: diamond beef applies regeneration on consumption
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: cow actually drops diamond beef
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: cow also drops species essence
[05:37:48] [Server thread/INFO] (diamondworld) CHECK PASS: sheep actually drops diamond mutton
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.sheep
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.goat
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.wolf
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.evoker
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.tadpole
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zombie
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.stray
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.strider
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.cat
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.creeper
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.turtle
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.llama
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.blaze
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.skeleton
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.salmon
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.iron_golem
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.armadillo
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.ender_dragon
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.shulker
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zombified_piglin
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.polar_bear
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.elder_guardian
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.vex
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.witch
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.trader_llama
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.spider
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.piglin_brute
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.chicken
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.pufferfish
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.ravager
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.frog
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.phantom
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.wandering_trader
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.donkey
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.husk
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.wither_skeleton
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.bee
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.cod
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.axolotl
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.enderman
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.guardian
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.piglin
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.pillager
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.bogged
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.dolphin
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.skeleton_horse
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.slime
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.happy_ghast
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.horse
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.vindicator
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.giant
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.panda
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.allay
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.mule
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.warden
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.parrot
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.ghast
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.tropical_fish
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.copper_golem
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.rabbit
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.sniffer
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.cow
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.villager
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.wither
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.mooshroom
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.illusioner
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.pig
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.cave_spider
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.endermite
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.camel
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.magma_cube
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.drowned
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.squid
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.fox
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zoglin
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.hoglin
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.creaking
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zombie_villager
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.bat
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.silverfish
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zombie_horse
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.ocelot
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.glow_squid
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.snow_golem
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.breeze
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.bat
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: bat essence grants timed flight and night vision
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: bat essence enables survival flight permission
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: autosave serializes original survival flight permissions while bat ability is active
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: autosave restores live temporary flight immediately after serialization
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: flight expiration revokes flight and grants a safe slow-fall landing
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.bat
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: drinking actual milk removes essence flight and survival permission
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.bat
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: creative flight is preserved after essence expiration
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.chicken
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: chicken essence grants slow falling
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.spider
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: spider essence makes a survival player climb a contacted wall
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.enderman
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: enderman essence enables blink ability
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.guardian
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: guardian essence grants thorns and water breathing
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: actual creeper explosion crystallizes its exposed crater (110 ores)
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: crystallization leaves an open crater rather than filling the blast with ore
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: crystallization preserves containers and bedrock
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: mobGriefing=false disables direct crystallization
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: mobGriefing=false disables explosion crater conversion and terrain destruction
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: actual skeleton arrow ticks leave airborne diamond-ore trail
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: player arrows do not create hostile ore trails
[05:37:49] [Server thread/INFO] (diamondworld) DIAMONDWORLD V1.1 INTEGRATION CHECKS PASSED
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: test item has a real consumption component: item.minecraft.bread
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: last item of a food stack grants exactly one permanent health point before decrement
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: test item has a real consumption component: item.minecraft.bread
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: eating the same food again permanently stacks another health point
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: test item has a real consumption component: item.diamondworld.diamond_beef
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: diamond food adds permanent health while retaining its original regeneration effect
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: test item has a real consumption component: item.diamondworld.cow_essence
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: edible mob essence also grants permanent health
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: test item has a real consumption component: item.minecraft.milk_bucket
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: actual milk removes temporary effects but neither removes nor grants permanent health
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: test item has a real consumption component: item.minecraft.potion
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: drinking a non-food potion does not grant permanent health
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: real player NBT save and load preserves food health without a status effect
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: clearing all effects after reconnect preserves permanent food health
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: actual death-respawn copy preserves accumulated permanent food health
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: alive player replacement does not duplicate the permanent modifier
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: eating a placed cake slice grants one permanent health point
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: a refused cake interaction grants no health
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: test item has a real consumption component: item.minecraft.bread
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: test item has a real consumption component: item.minecraft.bread
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: permanent food health respects the vanilla 1024-point maximum
[05:37:49] [Server thread/INFO] (diamondworld) DIAMONDWORLD PERMANENT HEALTH CHECKS PASSED
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier doubles durability: wooden
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier mining speed vanilla×1.5+3: wooden
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier attack damage vanilla+4: wooden
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: wooden_sword
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: wooden_pickaxe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: wooden_axe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: wooden_shovel
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: wooden_hoe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier doubles durability: stone
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier mining speed vanilla×1.5+3: stone
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier attack damage vanilla+4: stone
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: stone_sword
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: stone_pickaxe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: stone_axe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: stone_shovel
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: stone_hoe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier doubles durability: copper
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier mining speed vanilla×1.5+3: copper
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier attack damage vanilla+4: copper
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: copper_sword
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: copper_pickaxe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: copper_axe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: copper_shovel
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: copper_hoe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier doubles durability: iron
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier mining speed vanilla×1.5+3: iron
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier attack damage vanilla+4: iron
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: iron_sword
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: iron_pickaxe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: iron_axe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: iron_shovel
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: iron_hoe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier doubles durability: golden
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier mining speed vanilla×1.5+3: golden
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier attack damage vanilla+4: golden
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: golden_sword
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: golden_pickaxe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: golden_axe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: golden_shovel
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: golden_hoe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier doubles durability: diamond
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier mining speed vanilla×1.5+3: diamond
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier attack damage vanilla+4: diamond
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: diamond_sword
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: diamond_pickaxe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: diamond_axe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: diamond_shovel
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: diamond_hoe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier doubles durability: netherite
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier mining speed vanilla×1.5+3: netherite
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool tier attack damage vanilla+4: netherite
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: netherite_sword
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: netherite_pickaxe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: netherite_axe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: netherite_shovel
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: tool item not double-counted: netherite_hoe
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: diamond sword attack modifier 6→10 (+4)
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: diamond pickaxe attack modifier 4→8 (+4)
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: diamond pickaxe stone speed 8→15
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: shears wool speed 5→8
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor material receives +3 defense: 0
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor durability doubled once: 0
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor material receives +3 defense: 1
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor durability doubled once: 1
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor material receives +3 defense: 2
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor durability doubled once: 2
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor material receives +3 defense: 3
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor durability doubled once: 3
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor material receives +3 defense: 4
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor durability doubled once: 4
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor material receives +3 defense: 5
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor durability doubled once: 5
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor material receives +3 defense: 6
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor durability doubled once: 6
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor material receives +3 defense: 7
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor durability doubled once: 7
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor material receives +3 defense: 8
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: armor durability doubled once: 8
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: diamond chestplate armor 8→11
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: diamond armor toughness 2→3
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: diamond chestplate durability 528→1056
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: diamond horse armor 11→14
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: wolf body armor 11→14
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: wolf armor durability 64→128
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: shield adds 3 offhand armor
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: elytra adds 3 chest armor
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.bow
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.crossbow
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.trident
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.mace
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.shield
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.shears
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.brush
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.fishing_rod
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.flint_and_steel
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: utility durability doubled once: item.minecraft.elytra
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: trident melee modifier 8→12
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: mace melee modifier 5→9
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: shield and elytra modifiers have independent identities and can stack
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: bow arrow actual hit 2→6 (+4 after velocity)
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: crossbow arrow actual hit 2→6 (+4 after velocity)
[05:37:49] [Server thread/INFO] (diamondworld) CHECK PASS: thrown trident actual hit 8→12
[05:37:49] [Server thread/INFO] (diamondworld) DIAMONDWORLD ALL GEAR CHECKS PASSED
[05:37:49] [Server thread/INFO] (diamondworld) DIAMONDWORLD ALL INTEGRATION CHECKS PASSED
```
