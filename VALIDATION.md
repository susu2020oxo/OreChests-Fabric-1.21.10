# v1.1.0 Verification

Minecraft 1.21.10 / Java 21 / Fabric Loader 0.17.3 / Fabric API 0.138.4+1.21.10

Build: PASS
Dedicated server integration: PASS
Client resource reload + renderer + integrated world: PASS

The checks exercise actual item consumption, player ability serialization, milk, block loot, crafting, actual explosions and ArrowEntity.tick. Other hostile traits were verified against mapped target methods and runtime class loading, but have not each been individually exercised in automated combat. Client test used software graphics and no audio device.

```text
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: wooden pickaxe durability doubled
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: diamond mining speed boosted
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: stone tool attack damage boosted
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: grass drops diamond fragment
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: stone still drops cobblestone
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: wood progression preserved
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: three fragments craft one diamond
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: diamond beef smelting recipe loaded
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: diamond beef applies regeneration on consumption
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: cow actually drops diamond beef
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: cow also drops species essence
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: sheep actually drops diamond mutton
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.spider
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zombie_horse
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.skeleton
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.breeze
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.ravager
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.vex
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.bogged
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.frog
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.copper_golem
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.ender_dragon
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.llama
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.pufferfish
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.slime
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.salmon
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.shulker
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.ghast
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.silverfish
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.hoglin
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.pig
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.cow
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.iron_golem
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.elder_guardian
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.pillager
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.fox
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.blaze
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.squid
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.magma_cube
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.sheep
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zoglin
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.husk
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.dolphin
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.mooshroom
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.cat
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.panda
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.snow_golem
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.drowned
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.wandering_trader
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.wither_skeleton
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.illusioner
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.glow_squid
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.vindicator
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.bat
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.skeleton_horse
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.sniffer
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.allay
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.trader_llama
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.ocelot
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.creaking
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.mule
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zombie
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.axolotl
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zombie_villager
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.camel
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.zombified_piglin
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.villager
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.polar_bear
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.rabbit
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.turtle
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.armadillo
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.cave_spider
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.donkey
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.wolf
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.endermite
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.piglin
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.cod
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.creeper
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.warden
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.witch
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.tadpole
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.tropical_fish
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.piglin_brute
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.strider
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.goat
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.phantom
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.bee
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.enderman
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.happy_ghast
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.horse
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.giant
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.stray
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.wither
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.evoker
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.guardian
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.parrot
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: species essence has real consumption effects: entity.minecraft.chicken
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.bat
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: bat essence grants timed flight and night vision
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: bat essence enables survival flight permission
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: autosave serializes original survival flight permissions while bat ability is active
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: autosave restores live temporary flight immediately after serialization
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: flight expiration revokes flight and grants a safe slow-fall landing
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.bat
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: drinking actual milk removes essence flight and survival permission
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.bat
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: creative flight is preserved after essence expiration
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.chicken
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: chicken essence grants slow falling
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.spider
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: spider essence makes a survival player climb a contacted wall
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.enderman
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: enderman essence enables blink ability
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: essence has consumable component: entity.minecraft.guardian
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: guardian essence grants thorns and water breathing
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: actual creeper explosion crystallizes its exposed crater (110 ores)
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: crystallization leaves an open crater rather than filling the blast with ore
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: crystallization preserves containers and bedrock
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: mobGriefing=false disables direct crystallization
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: mobGriefing=false disables explosion crater conversion and terrain destruction
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: actual skeleton arrow ticks leave airborne diamond-ore trail
[11:23:34] [Server thread/INFO] (diamondworld) CHECK PASS: player arrows do not create hostile ore trails
[11:23:34] [Server thread/INFO] (diamondworld) DIAMONDWORLD ALL INTEGRATION CHECKS PASSED
```
