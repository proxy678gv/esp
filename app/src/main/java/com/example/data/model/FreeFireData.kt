package com.example.data.model

data class FFCharacter(
    val id: String,
    val name: String,
    val alias: String,
    val role: String, // Rusher, In-Game Leader, Sniper, Support, Tank
    val skillType: String, // ACTIVE, PASSIVE
    val skillName: String,
    val skillDescription: String,
    val cooldownSeconds: Int,
    val lore: String,
    val tacticalTips: String,
    val synergyScore: Int, // 1 - 100
    val stats: FFCharacterStats,
    val voiceQuote: String
)

data class FFCharacterStats(
    val agility: Int, // 0 - 100
    val survivability: Int,
    val combatDamage: Int,
    val squadSupport: Int,
    val crowdControl: Int
)

data class FFWeapon(
    val id: String,
    val name: String,
    val category: String, // SMG, SHOTGUN, ASSAULT_RIFLE, MARKSMAN, SNIPER
    val evoName: String?,
    val damage: Int,
    val rateOfFire: Int,
    val range: Int,
    val reloadSpeed: Int,
    val magazine: Int,
    val accuracy: Int,
    val armorPenetration: Int,
    val movementSpeed: Int,
    val headshotMultiplier: Float,
    val tier: String, // S+, S, A, B
    val description: String,
    val bestAttachments: List<String>
)

data class FFPet(
    val id: String,
    val name: String,
    val species: String,
    val skillName: String,
    val skillDescription: String,
    val cooldown: String,
    val bestPairings: List<String>,
    val tier: String,
    val tacticalValue: String
)

data class FFDiamondPack(
    val id: String,
    val diamonds: Int,
    val bonusDiamonds: Int,
    val priceInr: Double,
    val tag: String?,
    val isPopular: Boolean = false
)

data class FFMembership(
    val id: String,
    val title: String,
    val totalDiamonds: Int,
    val instantDiamonds: Int,
    val dailyDiamonds: Int,
    val durationDays: Int,
    val priceInr: Double,
    val badge: String,
    val perks: List<String>
)

data class FFEvoGun(
    val id: String,
    val gunName: String,
    val evoSkinName: String,
    val maxLevel: Int = 7,
    val totalDiamondsRequired: Int = 14500,
    val specialAbilities: List<String>
)

object FreeFireDatabase {
    val characters: List<FFCharacter> = listOf(
        FFCharacter(
            id = "alok",
            name = "DJ Alok",
            alias = "The Sound Maestro",
            role = "Support & Rusher",
            skillType = "ACTIVE",
            skillName = "Drop the Beat",
            skillDescription = "Creates a 5m aura that increases movement speed by 15% and restores 3 HP/sec for 10 seconds. Effects do not stack. Cooldown: 45s.",
            cooldownSeconds = 45,
            lore = "Using the power of music, Alok left Brazil and traveled the world to bring unity to war-torn survivor zones.",
            tacticalTips = "Best used while initiating close-quarter squad pushes or escaping the shrinking blue zone.",
            synergyScore = 98,
            stats = FFCharacterStats(agility = 90, survivability = 85, combatDamage = 75, squadSupport = 98, crowdControl = 65),
            voiceQuote = "\"Feel the rhythm, let's take the Booyah!\""
        ),
        FFCharacter(
            id = "chrono",
            name = "Chrono",
            alias = "Time Traveler",
            role = "Tank & Defense",
            skillType = "ACTIVE",
            skillName = "Time Turner",
            skillDescription = "Creates an impenetrable spherical force field that blocks 800 damage from all weapons. Lasts for 6s. Cooldown: 75s.",
            cooldownSeconds = 75,
            lore = "Chrono hails from a parallel futuristic universe where he was the supreme protector of Earth.",
            tacticalTips = "Deploy in open fields when caught without Gloo Walls to revive teammates or take safe sniper shots.",
            synergyScore = 94,
            stats = FFCharacterStats(agility = 70, survivability = 98, combatDamage = 70, squadSupport = 88, crowdControl = 82),
            voiceQuote = "\"Time bends to my will.\""
        ),
        FFCharacter(
            id = "tatsuya",
            name = "Tatsuya",
            alias = "The Rebel Dash",
            role = "Primary Rusher",
            skillType = "ACTIVE",
            skillName = "Rebel Rush",
            skillDescription = "Dashes forward at lightning speed for 0.3s. Can be accumulated for up to 2 consecutive uses. Cooldown: 45s per charge.",
            cooldownSeconds = 45,
            lore = "A shy boy who lived in the shadow of his older brother Griza until the city's corruption forced him to fight.",
            tacticalTips = "Double dash behind enemy Gloo Walls to surprise opponents with an M1887 or MP40 one-tap.",
            synergyScore = 96,
            stats = FFCharacterStats(agility = 99, survivability = 75, combatDamage = 95, squadSupport = 60, crowdControl = 50),
            voiceQuote = "\"Too slow! Try to keep up.\""
        ),
        FFCharacter(
            id = "homer",
            name = "Homer",
            alias = "The Blind Assassin",
            role = "In-Game Leader",
            skillType = "ACTIVE",
            skillName = "Senses Shockwave",
            skillDescription = "Releases a homing drone towards the nearest enemy within 100m frontal distance, creating a 5m pulse that reduces enemy move speed by 60% and firing rate by 35%, dealing 25 DMG. Cooldown: 60s.",
            cooldownSeconds = 60,
            lore = "Founder of the tech syndicate, Homer lost his vision in combat but engineered high-frequency auditory drones.",
            tacticalTips = "Launch drone right before squad assault. Debuffed enemies cannot react in time against your SMG spray.",
            synergyScore = 92,
            stats = FFCharacterStats(agility = 70, survivability = 78, combatDamage = 88, squadSupport = 90, crowdControl = 96),
            voiceQuote = "\"My drones see what eyes cannot.\""
        ),
        FFCharacter(
            id = "dimitri",
            name = "Dimitri",
            alias = "Sound Healer",
            role = "Full Squad Medic",
            skillType = "ACTIVE",
            skillName = "Healing Heartbeat",
            skillDescription = "Creates a 3.5m healing zone. Inside, users and allies recover 10 HP/s. When knocked down, users and allies can self-heal/revive. Lasts 12s. Cooldown: 60s.",
            cooldownSeconds = 60,
            lore = "A world-class sound engineer and DJ who uses acoustic resonance to stimulate cellular regeneration.",
            tacticalTips = "Combine with Olivia and Thiva for instantaneous full-HP squad self-revivals inside Gloo cover.",
            synergyScore = 95,
            stats = FFCharacterStats(agility = 65, survivability = 95, combatDamage = 65, squadSupport = 99, crowdControl = 60),
            voiceQuote = "\"Music is the ultimate medicine.\""
        ),
        FFCharacter(
            id = "k",
            name = "K (Captain Booyah)",
            alias = "The Master of All",
            role = "Sustain Fighter",
            skillType = "ACTIVE",
            skillName = "Master of All",
            skillDescription = "Max EP increases by 50 (up to 250 EP). Jiu-Jitsu Mode: Allies within 6m get 500% EP conversion rate. Psychology Mode: Recover 3 EP every 2s, up to 250 EP. Mode switch CD: 6s.",
            cooldownSeconds = 6,
            lore = "An accomplished professor and jiujitsu black belt with an unyielding will to protect life.",
            tacticalTips = "Switch to Jiu-Jitsu mode right as combat starts to instantly convert 250 EP into continuous HP regeneration.",
            synergyScore = 97,
            stats = FFCharacterStats(agility = 75, survivability = 96, combatDamage = 82, squadSupport = 88, crowdControl = 70),
            voiceQuote = "\"Mind and body in perfect balance.\""
        ),
        FFCharacter(
            id = "wukong",
            name = "Wukong",
            alias = "Monkey King",
            role = "Ambush Specialist",
            skillType = "ACTIVE",
            skillName = "Camouflage",
            skillDescription = "Transforms into a bush with 10% reduced movement speed for 15s. Attacking terminates the effect. Skill cooldown resets upon knocking down an enemy. Cooldown: 200s.",
            cooldownSeconds = 200,
            lore = "An artificially engineered simian super-soldier with unmatched tactical adaptability.",
            tacticalTips = "Knock an enemy, immediately re-transform into bush, reload your shotgun, and ambush the next squad member.",
            synergyScore = 90,
            stats = FFCharacterStats(agility = 80, survivability = 85, combatDamage = 92, squadSupport = 50, crowdControl = 75),
            voiceQuote = "\"Catch me if you can!\""
        ),
        FFCharacter(
            id = "kelly",
            name = "Kelly 'The Swift'",
            alias = "High School Sprinter",
            role = "Secondary Rusher",
            skillType = "PASSIVE",
            skillName = "Deadly Velocity",
            skillDescription = "Sprint speed increased by 6%. After 4 seconds of sprinting, activates Deadly Velocity: first shot on target deals 106% damage. Lasts 5s.",
            cooldownSeconds = 0,
            lore = "Shimada Kiriko, an athletic high school track champion who never gives up on her friends.",
            tacticalTips = "Mandatory passive for all competitive tournament squads for swift rotations and first-shot headshot boost.",
            synergyScore = 99,
            stats = FFCharacterStats(agility = 100, survivability = 70, combatDamage = 88, squadSupport = 65, crowdControl = 40),
            voiceQuote = "\"I run for victory!\""
        ),
        FFCharacter(
            id = "hayato",
            name = "Hayato 'Firebrand'",
            alias = "Legendary Samurai",
            role = "Armor Shredder",
            skillType = "PASSIVE",
            skillName = "Bushido & Art of Blades",
            skillDescription = "For every 10% decrease in max HP, armor penetration increases by 5%. Awakened: reduces frontal damage taken by 3% for every 10% max HP lost.",
            cooldownSeconds = 0,
            lore = "Heir to the legendary Shimada samurai clan, carrying his ancestral katana and cyber-enhanced armor.",
            tacticalTips = "As you take damage in 1v1 gunfights, your bullets shred enemy level 3 vests like paper.",
            synergyScore = 96,
            stats = FFCharacterStats(agility = 75, survivability = 85, combatDamage = 98, squadSupport = 60, crowdControl = 55),
            voiceQuote = "\"Honor lives in the blade.\""
        ),
        FFCharacter(
            id = "moco",
            name = "Moco 'Enigma'",
            alias = "Cyber Hacker",
            role = "Tactical Scout",
            skillType = "PASSIVE",
            skillName = "Hacker's Eye",
            skillDescription = "Tags enemies shot for 5s. Awakened: Tag duration on moving enemies extends by up to 6.5s. Tag info shared with entire squad.",
            cooldownSeconds = 0,
            lore = "A master hacker of the cyber network known as 'Chat Noir', uncovering Bermuda's deepest secrets.",
            tacticalTips = "Tag an enemy behind cover so your sniper can wallbang them or grenade their exact position.",
            synergyScore = 95,
            stats = FFCharacterStats(agility = 80, survivability = 72, combatDamage = 80, squadSupport = 96, crowdControl = 85),
            voiceQuote = "\"Everything is connected.\""
        ),
        FFCharacter(
            id = "orion",
            name = "Orion",
            alias = "The Crimson Beast",
            role = "Close-Combat Breaker",
            skillType = "ACTIVE",
            skillName = "Crimson Crush",
            skillDescription = "Consumes 300 EP to become invulnerable for 3s. Cannot shoot but absorbs 15 HP/s from all enemies within 5m. Cooldown: 3s.",
            cooldownSeconds = 3,
            lore = "A genetically augmented warrior cursed with an insatiable crimson energy core in his chest.",
            tacticalTips = "Pair with Miguel (gains 200 EP per knock) to chain infinite Crimson Crush invulnerability pulses in Clash Squad.",
            synergyScore = 93,
            stats = FFCharacterStats(agility = 78, survivability = 99, combatDamage = 90, squadSupport = 55, crowdControl = 88),
            voiceQuote = "\"My wrath cannot be contained!\""
        )
    )

    val weapons: List<FFWeapon> = listOf(
        // SMGs
        FFWeapon(
            id = "mp40",
            name = "MP40",
            category = "SMG",
            evoName = "Predatory Cobra (Lv.7)",
            damage = 48,
            rateOfFire = 83,
            range = 22,
            reloadSpeed = 48,
            magazine = 32,
            accuracy = 17,
            armorPenetration = 0,
            movementSpeed = 88,
            headshotMultiplier = 3.5f,
            tier = "S+",
            description = "The undisputed king of close-range rapid fire in Free Fire tournaments. Devastating burst damage.",
            bestAttachments = listOf("Stock Lv.3", "Magazine Lv.3")
        ),
        FFWeapon(
            id = "ump",
            name = "UMP",
            category = "SMG",
            evoName = "Art of War",
            damage = 50,
            rateOfFire = 75,
            range = 36,
            reloadSpeed = 77,
            magazine = 30,
            accuracy = 36,
            armorPenetration = 63,
            movementSpeed = 79,
            headshotMultiplier = 3.2f,
            tier = "S+",
            description = "Unmatched armor penetration among SMGs. Shreds level 3 vests and has easy drag-headshot recoil.",
            bestAttachments = listOf("Silencer", "Muzzle Lv.3", "Foregrip Lv.3", "Magazine Lv.3", "Scope 2x")
        ),
        FFWeapon(
            id = "vector",
            name = "Vector Akimbo",
            category = "SMG",
            evoName = "Aquablaze Wrath",
            damage = 47,
            rateOfFire = 81,
            range = 32,
            reloadSpeed = 62,
            magazine = 30,
            accuracy = 61,
            armorPenetration = 0,
            movementSpeed = 78,
            headshotMultiplier = 3.0f,
            tier = "S",
            description = "Dual-wielded Akimbo SMG delivering double the fire rate for instant point-blank squad wipes.",
            bestAttachments = listOf("Muzzle Lv.3", "Magazine Lv.3", "Stock Lv.3")
        ),
        FFWeapon(
            id = "mp5",
            name = "MP5-X",
            category = "SMG",
            evoName = "Champion Boxer",
            damage = 48,
            rateOfFire = 76,
            range = 41,
            reloadSpeed = 77,
            magazine = 48,
            accuracy = 54,
            armorPenetration = 0,
            movementSpeed = 81,
            headshotMultiplier = 3.2f,
            tier = "A",
            description = "Equipped with electrical booster chip for superior mid-range stability and high ammo capacity.",
            bestAttachments = listOf("Electrical Booster", "Silencer", "Magazine Lv.3", "Scope 4x")
        ),

        // Shotguns
        FFWeapon(
            id = "m1887",
            name = "M1887",
            category = "SHOTGUN",
            evoName = "Sterling Conqueror (Lv.7)",
            damage = 100,
            rateOfFire = 42,
            range = 21,
            reloadSpeed = 76,
            magazine = 2,
            accuracy = 10,
            armorPenetration = 54,
            movementSpeed = 79,
            headshotMultiplier = 4.0f,
            tier = "S+",
            description = "Double-barrel shotgun with 100 base damage and high armor penetration. 2 quick shots can eliminate any player.",
            bestAttachments = listOf("Shotgun Muzzle")
        ),
        FFWeapon(
            id = "m1014",
            name = "M1014",
            category = "SHOTGUN",
            evoName = "Green Flame Draco (Lv.7)",
            damage = 94,
            rateOfFire = 39,
            range = 10,
            reloadSpeed = 20,
            magazine = 6,
            accuracy = 10,
            armorPenetration = 0,
            movementSpeed = 60,
            headshotMultiplier = 3.8f,
            tier = "S",
            description = "Semi-automatic shotgun with 6 rounds. Upgradable with Stock chips for high-speed combat reloading.",
            bestAttachments = listOf("Shotgun Stock Lv.3")
        ),
        FFWeapon(
            id = "mag7",
            name = "MAG-7",
            category = "SHOTGUN",
            evoName = "Fiery Rush",
            damage = 89,
            rateOfFire = 53,
            range = 15,
            reloadSpeed = 55,
            magazine = 8,
            accuracy = 17,
            armorPenetration = 0,
            movementSpeed = 73,
            headshotMultiplier = 3.5f,
            tier = "A",
            description = "Magazine-fed shotgun offering faster rate of fire and greater mobility than traditional shotguns.",
            bestAttachments = listOf("Muzzle Lv.3", "Magazine Lv.3")
        ),

        // Assault Rifles
        FFWeapon(
            id = "ak47",
            name = "AK-47",
            category = "ASSAULT_RIFLE",
            evoName = "Blue Flame Draco (Lv.7)",
            damage = 61,
            rateOfFire = 56,
            range = 72,
            reloadSpeed = 41,
            magazine = 30,
            accuracy = 41,
            armorPenetration = 0,
            movementSpeed = 68,
            headshotMultiplier = 3.6f,
            tier = "S+",
            description = "High single-shot damage assault rifle. Rewards players who master recoil control with one-tap headshots.",
            bestAttachments = listOf("Muzzle Lv.3", "Foregrip Lv.3", "Magazine Lv.3", "Scope 4x", "Stock Lv.3")
        ),
        FFWeapon(
            id = "scar",
            name = "SCAR",
            category = "ASSAULT_RIFLE",
            evoName = "Megalodon Alpha (Lv.7)",
            damage = 53,
            rateOfFire = 61,
            range = 60,
            reloadSpeed = 41,
            magazine = 30,
            accuracy = 42,
            armorPenetration = 0,
            movementSpeed = 70,
            headshotMultiplier = 3.4f,
            tier = "S",
            description = "Extremely balanced AR with low recoil, making it the top choice for beginners and pro tournament players alike.",
            bestAttachments = listOf("Silencer", "Foregrip Lv.3", "Magazine Lv.3", "Scope 4x")
        ),
        FFWeapon(
            id = "groza",
            name = "Groza",
            category = "ASSAULT_RIFLE",
            evoName = "Bang! Pop! Boom!",
            damage = 61,
            rateOfFire = 58,
            range = 77,
            reloadSpeed = 48,
            magazine = 30,
            accuracy = 54,
            armorPenetration = 34,
            movementSpeed = 63,
            headshotMultiplier = 3.7f,
            tier = "S+",
            description = "Airdrop-exclusive powerhouse combining the damage of an AK with the fire rate and stability of an SMG.",
            bestAttachments = listOf("Silencer", "Magazine Lv.3", "Scope 4x", "Stock Lv.3")
        ),
        FFWeapon(
            id = "m4a1",
            name = "M4A1-Z",
            category = "ASSAULT_RIFLE",
            evoName = "Infernal Carnage",
            damage = 53,
            rateOfFire = 56,
            range = 68,
            reloadSpeed = 48,
            magazine = 30,
            accuracy = 54,
            armorPenetration = 0,
            movementSpeed = 62,
            headshotMultiplier = 3.4f,
            tier = "A",
            description = "Long-range AR capable of 3 chip upgrades to reach supreme rate of fire and laser-beam precision.",
            bestAttachments = listOf("Upgrade Chip x3", "Muzzle Lv.3", "Foregrip Lv.3", "Magazine Lv.3", "Scope 4x")
        ),

        // Marksman & Snipers
        FFWeapon(
            id = "woodpecker",
            name = "Woodpecker",
            category = "MARKSMAN",
            evoName = "Fierce Demolition",
            damage = 85,
            rateOfFire = 38,
            range = 63,
            reloadSpeed = 48,
            magazine = 12,
            accuracy = 69,
            armorPenetration = 77,
            movementSpeed = 62,
            headshotMultiplier = 3.8f,
            tier = "S+",
            description = "Semi-automatic Marksman Rifle with massive 77 Armor Penetration. Capable of dropping armored enemies in 2 hits.",
            bestAttachments = listOf("Silencer", "Muzzle Lv.3", "Magazine Lv.3", "Stock Lv.3")
        ),
        FFWeapon(
            id = "awm",
            name = "AWM",
            category = "SNIPER",
            evoName = "Duke Swallowtail",
            damage = 90,
            rateOfFire = 27,
            range = 91,
            reloadSpeed = 34,
            magazine = 5,
            accuracy = 90,
            armorPenetration = 0,
            movementSpeed = 65,
            headshotMultiplier = 4.5f,
            tier = "S+",
            description = "The ultimate bolt-action sniper rifle. One headshot knocks down any player regardless of helmet tier.",
            bestAttachments = listOf("Sniper Muzzle", "Magazine Lv.3", "Sniper Stock")
        ),
        FFWeapon(
            id = "m82b",
            name = "M82B",
            category = "SNIPER",
            evoName = "Dragon Mob",
            damage = 90,
            rateOfFire = 27,
            range = 85,
            reloadSpeed = 41,
            magazine = 8,
            accuracy = 90,
            armorPenetration = 67,
            movementSpeed = 55,
            headshotMultiplier = 4.0f,
            tier = "S+",
            description = "Anti-material sniper rifle capable of penetrating Gloo Walls and dealing extra damage to vehicles.",
            bestAttachments = listOf("Sniper Muzzle", "Magazine Lv.3")
        )
    )

    val pets: List<FFPet> = listOf(
        FFPet(
            id = "waggor",
            name = "Mr. Waggor",
            species = "Gentleman Penguin",
            skillName = "Smooth Gloo",
            skillDescription = "Produces 1 Gloo Wall every 100 seconds when the player has less than 2 Gloo Walls in inventory.",
            cooldown = "100s",
            bestPairings = listOf("Chrono", "Alok", "Tatsuya"),
            tier = "S+",
            tacticalValue = "Crucial for tournament late-zones when Gloo Wall supplies run dry."
        ),
        FFPet(
            id = "beaston",
            name = "Beaston",
            species = "Cyber Baboon",
            skillName = "Helping Hand",
            skillDescription = "Throwing distance of Grenades, Gloo Walls, Flashbangs, and Smoke Grenades increases by 30%.",
            cooldown = "Passive",
            bestPairings = listOf("Alvaro", "Tatsuya", "Homer"),
            tier = "S+",
            tacticalValue = "Enables pinpoint long-range grenade knocks into enemy compound windows."
        ),
        FFPet(
            id = "falco",
            name = "Falco",
            species = "Sky Falcon",
            skillName = "Skyline Spree",
            skillDescription = "45% increase in gliding speed upon parachute dive, and 50% increase in diving speed after parachute opens.",
            cooldown = "Passive (Squad)",
            bestPairings = listOf("Kelly", "Alok", "Hayato"),
            tier = "S+",
            tacticalValue = "Mandatory for 1 squad member in esports to guarantee first landing at hot-drop loot crates."
        ),
        FFPet(
            id = "rockie",
            name = "Rockie",
            species = "Punk Raccoon",
            skillName = "Stay Chill",
            skillDescription = "Reduces the cooldown time of equipped active character skills by 15%.",
            cooldown = "Passive",
            bestPairings = listOf("Chrono", "Alok", "Tatsuya", "Dimitri"),
            tier = "S",
            tacticalValue = "Shaves 11s off Chrono and 7s off Alok, allowing more frequent skill activations per fight."
        ),
        FFPet(
            id = "ottero",
            name = "Ottero",
            species = "DJ Otter",
            skillName = "Double Blubber",
            skillDescription = "When using Treatment Gun or Med Kit, also restores EP amount equal to 65% of HP restored.",
            cooldown = "Passive",
            bestPairings = listOf("K", "A124", "Dimitri"),
            tier = "S",
            tacticalValue = "Provides instant fuel for K's EP conversion engine, ensuring max HP during zone rotations."
        ),
        FFPet(
            id = "flash",
            name = "Flash",
            species = "Steel Turtle",
            skillName = "Steel Shell",
            skillDescription = "Reduces damage taken from behind (FF Shotgun/Knife) by 25% up to 150 points. Cooldown: 90s.",
            cooldown = "90s",
            bestPairings = listOf("Hayato", "Kelly"),
            tier = "A",
            tacticalValue = "Protects your back against unexpected third-party flanks while looting crates."
        ),
        FFPet(
            id = "dreki",
            name = "Dreki",
            species = "Baby Dragon",
            skillName = "Dragon Glare",
            skillDescription = "Detects up to 4 opponents who are using Medkits within a 30m radius for 5 seconds.",
            cooldown = "Passive",
            bestPairings = listOf("Moco", "Homer", "Tatsuya"),
            tier = "A",
            tacticalValue = "Reveals injured enemies hiding behind walls so your squad can rush for easy wipes."
        )
    )

    val diamondPacks: List<FFDiamondPack> = listOf(
        FFDiamondPack("p1", 100, 10, 80.0, "STARTER", false),
        FFDiamondPack("p2", 310, 31, 240.0, "+10% EXTRA", false),
        FFDiamondPack("p3", 520, 52, 400.0, "MOST POPULAR", true),
        FFDiamondPack("p4", 1060, 106, 800.0, "BEST VALUE", false),
        FFDiamondPack("p5", 2180, 218, 1600.0, "PRO ESPORTS", false),
        FFDiamondPack("p6", 5600, 560, 4000.0, "EVO MAX PACK", false)
    )

    val memberships: List<FFMembership> = listOf(
        FFMembership(
            id = "weekly_lite",
            title = "Weekly Lite",
            totalDiamonds = 100,
            instantDiamonds = 20,
            dailyDiamonds = 12,
            durationDays = 7,
            priceInr = 50.0,
            badge = "Lite Pass",
            perks = listOf("Total 100 Diamonds", "Instant 20 💎 on activation", "Daily 12 💎 claim for 7 days")
        ),
        FFMembership(
            id = "weekly_vip",
            title = "Weekly VIP Membership",
            totalDiamonds = 450,
            instantDiamonds = 100,
            dailyDiamonds = 50,
            durationDays = 7,
            priceInr = 159.0,
            badge = "Weekly VIP",
            perks = listOf("Total 450 Diamonds", "Instant 100 💎", "Daily 50 💎 claim", "VIP Shop discount badge", "Special chat bubble icon")
        ),
        FFMembership(
            id = "monthly_vip",
            title = "Monthly Super Pass",
            totalDiamonds = 2600,
            instantDiamonds = 500,
            dailyDiamonds = 70,
            durationDays = 30,
            priceInr = 799.0,
            badge = "Monthly Master",
            perks = listOf("Total 2600 Diamonds", "Instant 500 💎", "Daily 70 💎 claim for 30 days", "5x Universal Super Medics", "Discount Store Privilege", "Exclusive Booyah Profile Banner")
        )
    )

    val evoGuns: List<FFEvoGun> = listOf(
        FFEvoGun(
            id = "mp40_cobra",
            gunName = "MP40",
            evoSkinName = "Predatory Cobra",
            maxLevel = 7,
            totalDiamondsRequired = 14500,
            specialAbilities = listOf("Lv.1: New Look", "Lv.2: Custom Kill Feed", "Lv.3: Exclusive Emote", "Lv.4: Hit Effect & Extra Damage when hitting from behind", "Lv.5: Kill Effect & Firing Effect", "Lv.6: Exclusive Venom Ability", "Lv.7: Max Cobra Dragon Evolution & Cobra Strike Emote")
        ),
        FFEvoGun(
            id = "ak47_draco",
            gunName = "AK-47",
            evoSkinName = "Blue Flame Draco",
            maxLevel = 7,
            totalDiamondsRequired = 14500,
            specialAbilities = listOf("Lv.1: Dragon Form", "Lv.2: Custom Kill Broadcast", "Lv.3: Draco Wings Wingspan", "Lv.4: Extra Damage against Gloo Walls", "Lv.5: Blue Flame Fire Effect", "Lv.6: Max Damage & Armor Shred", "Lv.7: Draco Awakening Emote")
        ),
        FFEvoGun(
            id = "m1014_draco",
            gunName = "M1014",
            evoSkinName = "Green Flame Draco",
            maxLevel = 7,
            totalDiamondsRequired = 14500,
            specialAbilities = listOf("Lv.1: Emerald Drake Look", "Lv.2: Special Elimination Sound", "Lv.3: Drake Wings", "Lv.4: Rate of Fire Boost when hitting moving targets", "Lv.5: Toxic Breath Fire Effect", "Lv.6: Max Reload Speed Upgrade", "Lv.7: Green Draco Ultimate Emote")
        )
    )
}
