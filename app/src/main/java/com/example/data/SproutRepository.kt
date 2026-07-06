package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class SproutRepository(
    private val plantDao: PlantDao,
    private val userProfileDao: UserProfileDao,
    private val friendDao: FriendDao
) {
    val allPlants: Flow<List<Plant>> = plantDao.getAllPlantsFlow()
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfileFlow()
    val allFriends: Flow<List<Friend>> = friendDao.getAllFriendsFlow()

    suspend fun initializeDatabaseIfEmpty() {
        val existingFriends = friendDao.getAllFriendsFlow().firstOrNull()
        if (existingFriends.isNullOrEmpty()) {
            friendDao.insertFriends(
                listOf(
                    Friend("chloesprouts", "Chloe Sprouts", 14, 3500, "Monstera Albo", 15, avatarColor = 0xFFCD7F32.toInt()),
                    Friend("oliver_cactus", "Oliver Cactus", 9, 1900, "Golden Barrel", 8, avatarColor = 0xFF4CAF50.toInt()),
                    Friend("nova_star", "Nova Celestial", 21, 4800, "Cosmic Vine", 25, avatarColor = 0xFF00F0FF.toInt()),
                    Friend("sunny_grower", "Sunny Lavender", 12, 2100, "Pilea", 11, avatarColor = 0xFFE899A5.toInt())
                )
            )
        }

        val existingPlants = plantDao.getAllPlantsFlow().firstOrNull()
        if (existingPlants == null || existingPlants.size < 10) {
            val defaultPlantsRaw = listOf(
                Plant(
                    id = "monstera_deliciosa",
                    name = "Monstera Deliciosa",
                    scientificName = "Swiss Cheese Plant",
                    origin = "Tropical Rainforests of Central America",
                    about = "Famous for its natural leaf holes, the Monstera Deliciosa brings a bold, tropical vibe to any space. In its natural habitat, it climbs trees to reach sunlight, which is why it loves a moss pole indoors. It's resilient, relatively easy to care for, and incredibly rewarding to watch grow.",
                    light = "Bright Indirect",
                    waterLevel = "Medium",
                    soil = "Well-draining chunky mix. Peat moss, perlite, and orchid bark work well.",
                    stage = "Juvenile",
                    imageUrl = "https://image.pollinations.ai/prompt/Monstera%20Deliciosa%20digital%20art?width=512&height=512&nologo=true",
                    isCollected = true,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.75f,
                    isWidgetActive = true,
                    category = "Tropical",
                    seedPrice = 300,
                    rarity = "Common"
                ),
                Plant(
                    id = "pilea",
                    name = "Pilea",
                    scientificName = "Chinese Money Plant",
                    origin = "Yunnan Province, China",
                    about = "The Pilea Peperomioides is highly sought after for its unique round, coin-like foliage. Also famous as the friendship plant because it shoots off tons of baby plants that are easy to share.",
                    light = "Bright Indirect",
                    waterLevel = "Medium",
                    soil = "Peat moss with excessive perlite for aeration.",
                    stage = "Seedling",
                    imageUrl = "https://image.pollinations.ai/prompt/Pilea%20digital%20art?width=512&height=512&nologo=true",
                    isCollected = true,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.25f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 200,
                    rarity = "Common"
                ),
                Plant(
                    id = "string_of_pearls",
                    name = "String of Pearls",
                    scientificName = "Senecio Rowleyanus",
                    origin = "South West Africa",
                    about = "A beautiful creeping succulent with trailing stems of small, spherical green beads. Excellent as a hanging focus detail, requiring minimal pruning but consistent bright environment.",
                    light = "Bright Indirect",
                    waterLevel = "Low",
                    soil = "Gritty cactus mix containing sand and pumice.",
                    stage = "Juvenile",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAW5LgiCtj5FgYSRTa-NbYty-XLRbF2t5qV3L0sbfC1KOJ0ULKSsgkEndUsirdtH5gKtd5iSJzVuuJf7ueZvRvkJakqVXJweEa11IdP0oyWAVsl6Di_4YCvzkfBrNxwlX9o8yUs0mq8gXPL6U3X3lnJbG_BrL07CJ2HB5UOrDcGjQ2sesWuGTLhftJ89-6U1G6QxrFpKy3JQ37oEFLbC2rxngwxsDnuJ9-713ZJujbYw2LtVzUc2OMH7HUiuDhluvop7xCTjqGpr1I",
                    isCollected = true,
                    isWishlist = false,
                    waterNeeds = "Saturated",
                    waterProgress = 1.0f,
                    isWidgetActive = false,
                    category = "Desert",
                    seedPrice = 450,
                    rarity = "Common"
                ),
                Plant(
                    id = "lunar_fern",
                    name = "Lunar Fern",
                    scientificName = "Athyrium niponicum",
                    origin = "Moon Surface",
                    about = "An ethereal, make-believe plant found on the moon's surface. Its silver-blue fronds glow softly in the dark, absorbing starlight to fuel its growth. It requires a low-gravity environment and lunar regolith to truly flourish.",
                    light = "Starlight",
                    waterLevel = "Low",
                    soil = "Lunar Regolith",
                    stage = "Spore",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD0FlZx_D3-y0HsTA1X07Dx2Lz_89dlWms81qLYQ3BAwMiYT-ZPzQ2p1xTMv1eJ4e-8HOqtOvcA51p9FWxLvMWZWiOtIvYHwXP9C86uNKDk1QEtkmW8OKzK-0fmJljrVJzUtGr02xCACDEY7tn2oRBXl0fj8ZOXZUiD3nOVBf1WKp061ZWuQGGoc6jVP8fo-8WW66oY2DjzYe5Dg9MitbuOP0docF4jEl5QuG67_A06F0S6LQCAfd6L0ZsebdTeGpWRyeN_XQwz0ds",
                    isCollected = false,
                    isWishlist = true,
                    waterNeeds = "Good",
                    waterProgress = 0.50f,
                    isWidgetActive = false,
                    category = "Nebula",
                    seedPrice = 1200,
                    rarity = "Epic"
                ),
                Plant(
                    id = "golden_barrel_cactus",
                    name = "Golden Barrel Cactus",
                    scientificName = "Echinocactus grusonii",
                    origin = "Chihuahuan Desert, Mexico",
                    about = "A resilient desert dweller known for its striking golden spines and spherical shape. It thrives in high heat and requires very little water, making it a perfect companion for sunny window sills.",
                    light = "Sunlight High",
                    waterLevel = "Low",
                    soil = "Well-draining / Sandy gravel mix.",
                    stage = "Juvenile",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAlo97Grd9jkcdYcrWIp1axdSekGbSPeZBwly1b9DCSkJKt3kQiZrq1PwY22hTA07yYEx941ZlmMwB7VmiTlaQEGvg-AmF9SugGquXsXWKj9-a50s2KBuZaz6Hd0iqSiTZbvM3G6fnSbkmu0HsMdnaCTWTioW0QTqErgfeIhFJC1DnJXjOHJ8LuLZkM2oi0HB9Q5w54mU59WRbtwdqzAxRSNXneP6klT2yT_XFSKULQUi5T89XAcC6GtysOOEHzxQ1MJQtc8uW85_k",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.60f,
                    isWidgetActive = false,
                    category = "Desert",
                    seedPrice = 850,
                    rarity = "Rare"
                ),
                Plant(
                    id = "cosmic_vine",
                    name = "Cosmic Vine",
                    scientificName = "Vitis cosmica",
                    origin = "Nebula Environment",
                    about = "A vibrant, iridescent vine that drifts through nebulae, drawing energy from cosmic gases. Its glowing tendrils can reach across light years, weaving through the fabric of space.",
                    light = "Nebula Gas Glow",
                    waterLevel = "Low",
                    soil = "Cosmic Dust and vacuum gas density.",
                    stage = "Cosmic Seed",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBak6Yv7cx29svrQYCqFwN2T6T0LdoNPc6Q7rEjDnfEGvm_zBnUlSpDcsqGxD8C8-WiF-U_sAo5n5y0BMAXnl8LXq5-7x7CW1ZeeFiB6Zw4JyGrGchmlNO91i-G9J_Ie0ggaroOIHqmRIuHyWvl78LlLKWZtl2SY_U8zHC5x0CqYOs2YmY4OtPEOYzxfE0O5J5gwr-rJAXK6b5SMQkSaOA-NHTce4Hh3svCGyY1FIcyuB5bnRoCMbZIZgGHN6oKGlgfoMOEpdLPTE0",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.50f,
                    isWidgetActive = false,
                    category = "Nebula",
                    seedPrice = 1250,
                    rarity = "Epic"
                ),
                Plant(
                    id = "monster_albo",
                    name = "Monstera Albo",
                    scientificName = "Monstera deliciosa Albo variegata",
                    origin = "Specialist Nursery mutation",
                    about = "A pristine Monstera Deliciosa variant featuring striking off-white/variegated leaf patches. Highly popular and rare, requiring high level care to protect vulnerable white areas from browning.",
                    light = "Bright Indirect",
                    waterLevel = "Medium",
                    soil = "Premium organic chunky orchid mixture.",
                    stage = "Juvenile",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAv4smhYlRP9E2JIjp_0dBjz-Be1Q-n_7dLMJhIzIMKBlAcLW2wIMIZylDSk1zxJWUzO6YkY3FvRxtUJkbWhLdIQiZ4I9sdaZIqez-_7m4nJPpsrKggXU8WlxMVMDE1weLH-U7UkeCF6HGnY7W68uJiTzgZSEnFvsO38Akb_Up387BWGZHHBfS9Q1ssR0DDIZGocqe0W_M877YX1uxnK9CcXOFtHXbfwGBkQJ17esLkXJFwsE9WvjOVCdhr697bvCOfSbexE4Odxvs",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.70f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 3400,
                    rarity = "Legendary"
                ),
                Plant(
                    id = "pink_princess",
                    name = "Pink Princess",
                    scientificName = "Philodendron erubescens",
                    origin = "Tropical South America",
                    about = "A striking Philodendron Pink Princess showcasing vivid bubblegum-pink variegation mixed with dark burgundy foliage. Strikingly beautiful and perfect for premium botanical collections.",
                    light = "Bright Indirect",
                    waterLevel = "Medium",
                    soil = "Chunky coco coir and perlite.",
                    stage = "Juvenile",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBfT-UMSxmOo8nhGrgd1XZnSO9FSTGnKnt0j26ImygL0je2_-SJNlbiJKS3YZcMmOTywgtpuZ5aq9MF6991HaRgQaY5VJhOHSHMD42xuV1VbMx9czWglBfEBRf_PWT8WJ25Ig_7sFru6Eq87aZBcG3P7BxZS80ujl1E_L2iR7MlL-dPOfnx6jwFc3-BxQPSYIpYfRJtripP1saypfBh1RypdI_j_LdcMo8F4eMn8To9e4C2Iw-ckBvLt221rAxqNJ1BHcsugKTk12k",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.65f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 2100,
                    rarity = "Epic"
                ),
                Plant(
                    id = "calathea_ornata",
                    name = "Calathea Ornata",
                    scientificName = "Calathea ornata",
                    origin = "Colombia and Venezuela",
                    about = "Admired for its dark green leaves painted with crisp fine pink pinstripes. A stunning indoor plant that raises and lowers its leaves daily, resembling praying hands.",
                    light = "Medium Shadow",
                    waterLevel = "High",
                    soil = "Peat moss compost keeping continuous moisture.",
                    stage = "Juvenile",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBrqWEQRxewCdDwH0rY990LKwaqgGlSdfAYGxPpqJRVv87_AfUhkIrxpkjYuAkec-uqTp4K2_r6e82xd-DGdCpJHpr1WTvKyfmRgWtPOFdYX6PSpeJUOQgWggLLdkZo6qC6ZCSsPRirgeSwfclp7xilJJgFdVUty0P33CXs-Djn6VCdonGvqL6nGNjkUhETM_fe5Yc5qE2dUNZphGGY9nIo00uSqyND2l2QDvlqmTSBUnFTWV2YUwogMtyT4JfMMAw1F_42BmFpcvw",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.80f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 600,
                    rarity = "Common"
                ),
                Plant(
                    id = "golden_pothos",
                    name = "Golden Pothos",
                    scientificName = "Epipremnum aureum",
                    origin = "French Polynesia",
                    about = "A famously rugged, trailing houseplant featuring rich heart-shaped green leaves variegated with splashy golden yellow. Exceptionally clean, purifying, and forgiving to nurture.",
                    light = "Medium / Any Light",
                    waterLevel = "Medium",
                    soil = "Standard potting soil with quick drainage.",
                    stage = "Juvenile",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAVEBOtaVn4znmpeKeZzfqw7M9ommkPOSHKgaSOQ6nL2TQzm-auAKYWvmGql89QJIuLiPOInVpX9XnrhaZANOzBD_u6A0OMRbjXwtk7qnQGG_2iwGOc8Qpy7Vamvs652AZf5ufty9VQjKgmEXADivvpbDNuX0hP0nqaSabDvwkzVYXESnnYo9Ue7-nbiNFpFJXsqAW9F9lsDJe4uFlLli31VAzTFtUi26PKez3COzx3iGdJIqmIdv6MJceOPdR2xSy_59xFJbsvniM",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.50f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 150,
                    rarity = "Common"
                ),
                Plant(
                    id = "snake_plant",
                    name = "Snake Plant",
                    scientificName = "Sansevieria trifasciata",
                    origin = "Tropical West Africa",
                    about = "A tall, structural houseplant with architectural upright sharp leaves margined with golden yellow. Extremely resilient and capable of thriving in deep shade with sparse watering.",
                    light = "Low / Any Light",
                    waterLevel = "Low",
                    soil = "Cactus sand potting mixture.",
                    stage = "Juvenile",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBfnlwgTHf4-l5xTcgYQJgwdHsQPUzyYpwKSmeVN9pzkoiaHruTQTk6JhZKkUJ3ocR2irXej6X3iouIPJa_7PsXlENcwkXJJUVwH3RZR4u1aNk_hmqa4HufuX8sAmKRSmiPi6NNFUz-4YEI4qLQmmTpvExuEUweIN8vlQTgETD6QRAQj3Hp938-mWCZogkiO6qcYCl_SVu77XKf9wT9gYBV0yn8dHW5ZeioY7_BTate1PpRJjbFx3xUWxB53XangRFPXpmM6CWKkco",
                    isCollected = true,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.85f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 350,
                    rarity = "Common"
                ),
                Plant(
                    id = "spider_plant",
                    name = "Spider Plant",
                    scientificName = "Chlorophytum comosum",
                    origin = "Southern Africa",
                    about = "One of the most adaptable and easy-to-grow houseplants. Named for its spider-like plantlets, which dangle down from the mother plant like spiders on a web.",
                    light = "Medium to Bright",
                    waterLevel = "Medium",
                    soil = "Loamy, well-draining soil mix.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1545167622-3a6ac756afa4?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.50f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 180,
                    rarity = "Common"
                ),
                Plant(
                    id = "jade_plant",
                    name = "Jade Plant",
                    scientificName = "Crassula ovata",
                    origin = "South Africa and Mozambique",
                    about = "A popular succulent houseplant with fleshy, oval-shaped leaves and thick woody stems. Believed to bring good luck and financial prosperity to its owners.",
                    light = "Bright Direct",
                    waterLevel = "Low",
                    soil = "Gritty, sharp-draining cactus and succulent mix.",
                    stage = "Seedling",
                    imageUrl = "https://images.unsplash.com/photo-1599599810769-bcde5a160d32?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.60f,
                    isWidgetActive = false,
                    category = "Desert",
                    seedPrice = 250,
                    rarity = "Common"
                ),
                Plant(
                    id = "peace_lily",
                    name = "Peace Lily",
                    scientificName = "Spathiphyllum wallisii",
                    origin = "Tropical America",
                    about = "Renowned for its elegant white blooms (spathes) and dark, glossy green foliage. Excellent at signaling when it's thirsty by drooping dramatically, then bouncing back fully once watered.",
                    light = "Low to Medium",
                    waterLevel = "High",
                    soil = "Moist, rich, well-aerated potting mix containing peat.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1593696140826-c58b021acf8b?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.55f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 320,
                    rarity = "Common"
                ),
                Plant(
                    id = "boston_fern",
                    name = "Boston Fern",
                    scientificName = "Nephrolepis exaltata",
                    origin = "Humid forests and swamps of the Americas",
                    about = "A classic fern featuring lush, feathery green fronds. Thrives in high humidity and damp conditions, making it an ideal choice for hanging baskets or humid bathrooms.",
                    light = "Bright Indirect",
                    waterLevel = "High",
                    soil = "Fibre-rich, peat-heavy mix that retains moisture evenly.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1512428813824-f410f2629ced?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.45f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 280,
                    rarity = "Common"
                ),
                Plant(
                    id = "aloe_vera",
                    name = "Aloe Vera",
                    scientificName = "Aloe barbadensis miller",
                    origin = "Arabian Peninsula",
                    about = "A hardy, stemless succulent prized for the soothing gelatinous sap inside its thick, serrated leaves. Extremely easy to care for and highly tolerant of dry spells.",
                    light = "Bright Direct",
                    waterLevel = "Low",
                    soil = "Cactus mix blended with extra pumice or perlite.",
                    stage = "Seedling",
                    imageUrl = "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.70f,
                    isWidgetActive = false,
                    category = "Desert",
                    seedPrice = 300,
                    rarity = "Common"
                ),
                Plant(
                    id = "african_violet",
                    name = "African Violet",
                    scientificName = "Saintpaulia ionantha",
                    origin = "Cloud forests of Tanzania and Kenya",
                    about = "A delicate houseplant boasting clusters of purple, pink, or white flowers nestled above velvety, dark-green leaves. Prefers bottom watering to protect its soft foliage.",
                    light = "Medium Indirect",
                    waterLevel = "Medium",
                    soil = "Specialist loose, highly porous African Violet potting soil.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1517191434949-5e90cd67d2b6?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.50f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 400,
                    rarity = "Rare"
                ),
                Plant(
                    id = "zz_plant",
                    name = "ZZ Plant",
                    scientificName = "Zamioculcas zamiifolia",
                    origin = "Eastern Africa",
                    about = "Known for its exceptionally glossy, deep-green leaflets growing from sturdy, upright stems. Survives almost any neglect, doing incredibly well in low light and dry environments.",
                    light = "Low to Bright",
                    waterLevel = "Low",
                    soil = "Standard well-draining indoor potting mix.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1632203171982-cc0df6e9ceb4?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.80f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 450,
                    rarity = "Rare"
                ),
                Plant(
                    id = "rubber_plant",
                    name = "Rubber Plant",
                    scientificName = "Ficus elastica",
                    origin = "Northeast India and Southeast Asia",
                    about = "A prominent ornamental foliage plant with broad, leathery leaves of rich burgundy-green. It can eventually grow into a majestic, towering indoor tree.",
                    light = "Bright Indirect",
                    waterLevel = "Medium",
                    soil = "Aerated, fast-draining potting soil with sand.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1598880940375-d14cfce0aaec?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.60f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 500,
                    rarity = "Rare"
                ),
                Plant(
                    id = "cast_iron_plant",
                    name = "Cast Iron Plant",
                    scientificName = "Aspidistra elatior",
                    origin = "Japan and Taiwan",
                    about = "Earning its name from its incredible fortitude, this plant survives extremes of temperature, low light, and dry soil with ease. Its dark green, lance-shaped leaves flow gracefully.",
                    light = "Low to Medium",
                    waterLevel = "Low",
                    soil = "All-purpose indoor soil mix with good drainage.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.70f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 380,
                    rarity = "Common"
                ),
                Plant(
                    id = "swiss_cheese_vine",
                    name = "Swiss Cheese Vine",
                    scientificName = "Monstera adansonii",
                    origin = "Central and South America",
                    about = "A climbing or trailing relative of the Deliciosa, showcasing smaller, elegant leaves riddled with natural elliptical fenestrations. Excellent on trellises or tumbling down bookshelves.",
                    light = "Bright Indirect",
                    waterLevel = "Medium",
                    soil = "Chunky aroid mix with orchid bark and charcoal.",
                    stage = "Seedling",
                    imageUrl = "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.50f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 360,
                    rarity = "Common"
                ),
                Plant(
                    id = "weeping_fig",
                    name = "Weeping Fig",
                    scientificName = "Ficus benjamina",
                    origin = "Asia and Australia",
                    about = "A graceful tree displaying fine, glossy leaves on elegant arching branches. Popular in home design, it requires consistent location and hates drafts or sudden movements.",
                    light = "Bright Indirect",
                    waterLevel = "Medium",
                    soil = "Slightly alkaline loam potting mixture.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1597055181300-e3633a207518?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.55f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 520,
                    rarity = "Rare"
                ),
                Plant(
                    id = "dumb_cane",
                    name = "Dumb Cane",
                    scientificName = "Dieffenbachia seguine",
                    origin = "Tropical Americas and West Indies",
                    about = "Showcases large, broad leaves mottled in striking patterns of cream, yellow, and deep green. Essential focal specimen for classic indoor garden aesthetic.",
                    light = "Filtered light",
                    waterLevel = "Medium",
                    soil = "Standard loamy, porous indoor soil.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1592150621744-aca64f48394a?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.60f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 420,
                    rarity = "Common"
                ),
                Plant(
                    id = "english_ivy",
                    name = "English Ivy",
                    scientificName = "Hedera helix",
                    origin = "Europe and Western Asia",
                    about = "An evergreen climbing vine that acts as an exceptional climber. Features classic lobed foliage that easily trails along wall stakes or spills from decorative baskets.",
                    light = "Bright Indirect",
                    waterLevel = "Medium",
                    soil = "Rich garden compost with sharp sand.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1530968033775-2c92736b131e?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.50f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 220,
                    rarity = "Common"
                ),
                Plant(
                    id = "parlor_palm",
                    name = "Parlor Palm",
                    scientificName = "Chamaedorea elegans",
                    origin = "Rainforests of Southern Mexico and Guatemala",
                    about = "Highly popular since Victorian times, this compact palm displays multiple delicate stems crowned by feathery, vibrant green fronds. Exceptionally forgiving and slow-growing.",
                    light = "Low to Medium",
                    waterLevel = "Medium",
                    soil = "Rich, peat-based soil with moderate drainage.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.48f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 460,
                    rarity = "Rare"
                ),
                Plant(
                    id = "fiddle_leaf_fig",
                    name = "Fiddle Leaf Fig",
                    scientificName = "Ficus lyrata",
                    origin = "Lowland rainforests of West Africa",
                    about = "The quintessential designer tree of modern interiors. Boasts huge, violin-shaped, high-gloss leaves that grow skyward on an architectural wooden stalk.",
                    light = "Bright Consistent",
                    waterLevel = "Medium",
                    soil = "Fast-drying, organic-rich soil blend with perlite.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1597055181300-e3633a207518?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.65f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 1100,
                    rarity = "Epic"
                ),
                Plant(
                    id = "chinese_evergreen",
                    name = "Chinese Evergreen",
                    scientificName = "Aglaonema commutatum",
                    origin = "Tropical regions of Asia",
                    about = "A incredibly hardy plant showing spectacular foliage patterns splashed with cream, pewter, or silver-pink. Great for adding vivid accents to darker rooms.",
                    light = "Low shade",
                    waterLevel = "Low",
                    soil = "Aerated peat soil with perlite.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1612360427546-4022217c2a1d?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.52f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 480,
                    rarity = "Rare"
                ),
                Plant(
                    id = "string_of_dolphins",
                    name = "String of Dolphins",
                    scientificName = "Senecio peregrinus",
                    origin = "Horticultural hybrid",
                    about = "A whimsical creeping succulent whose individual fleshy leaves look exactly like leaping playful dolphins. Unique conversation starter that cascades beautiful trails.",
                    light = "Bright Direct",
                    waterLevel = "Low",
                    soil = "Coarse succulent mix containing pumice and sand.",
                    stage = "Seedling",
                    imageUrl = "https://images.unsplash.com/photo-1510265236892-329bfd7de7a1?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.58f,
                    isWidgetActive = false,
                    category = "Desert",
                    seedPrice = 640,
                    rarity = "Epic"
                ),
                Plant(
                    id = "ponytail_palm",
                    name = "Ponytail Palm",
                    scientificName = "Beaucarnea recurvata",
                    origin = "Semi-desert states of Mexico",
                    about = "Despite the name, it's actually a succulent related to Agave! Stores water in its swollen dome-like bulb base and grows long, curly strap-like green leaves.",
                    light = "Bright Direct",
                    waterLevel = "Low",
                    soil = "Sand-enhanced high-drainage sandy mix.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.72f,
                    isWidgetActive = false,
                    category = "Desert",
                    seedPrice = 580,
                    rarity = "Rare"
                ),
                Plant(
                    id = "zebra_cactus",
                    name = "Zebra Cactus",
                    scientificName = "Haworthiopsis fasciata",
                    origin = "Eastern Cape of South Africa",
                    about = "A stout, slow-growing succulent displaying rosettes of triangular dark green leaves studded with prominent white horizontal zebra stripes. High contrast miniature beauty.",
                    light = "Bright Indirect",
                    waterLevel = "Low",
                    soil = "Gritty, non-clumpy sharp grit mix.",
                    stage = "Seedling",
                    imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.64f,
                    isWidgetActive = false,
                    category = "Desert",
                    seedPrice = 240,
                    rarity = "Common"
                ),
                Plant(
                    id = "birds_nest_fern",
                    name = "Birds Nest Fern",
                    scientificName = "Asplenium nidus",
                    origin = "Tropical regions of Asia and Polynesia",
                    about = "Known for its beautiful crinkly leaf rosettes that look like a bird's nest in the center. Requires central humidity and partial shadow for rich leaf curls.",
                    light = "Medium Shadow",
                    waterLevel = "High",
                    soil = "Forest-floor humus blend layered with bark.",
                    stage = "Juvenile",
                    imageUrl = "https://images.unsplash.com/photo-1545241047-6083a3684587?auto=format&fit=crop&q=80&w=600",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Good",
                    waterProgress = 0.46f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 390,
                    rarity = "Common"
                ),
                Plant(
                    id = "crimson_rose",
                    name = "Crimson Rose",
                    scientificName = "Rosa sanguinea",
                    origin = "Forests",
                    about = "A beautiful red rose.",
                    light = "Medium",
                    waterLevel = "Medium",
                    soil = "Loam",
                    stage = "Seedling",
                    imageUrl = "https://image.pollinations.ai/prompt/Crimson%20Rose%20digital%20art?width=512&height=512&nologo=true",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 1200,
                    rarity = "Epic"
                ),
                Plant(
                    id = "azure_fern",
                    name = "Azure Fern",
                    scientificName = "Filicopsida azurea",
                    origin = "Tundra",
                    about = "A cold resistant blue fern.",
                    light = "Low",
                    waterLevel = "Low",
                    soil = "Peat",
                    stage = "Spore",
                    imageUrl = "https://image.pollinations.ai/prompt/Azure%20Fern%20digital%20art?width=512&height=512&nologo=true",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Tundra",
                    seedPrice = 3000,
                    rarity = "Legendary"
                ),
                Plant(
                    id = "lunar_cactus",
                    name = "Lunar Cactus",
                    scientificName = "Cactaceae lunaris",
                    origin = "Desert",
                    about = "Glows under the moonlight.",
                    light = "High",
                    waterLevel = "Low",
                    soil = "Sand",
                    stage = "Seed",
                    imageUrl = "https://image.pollinations.ai/prompt/Lunar%20Cactus%20digital%20art?width=512&height=512&nologo=true",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Desert",
                    seedPrice = 4500,
                    rarity = "Legendary"
                ),
                Plant(
                    id = "solar_sunflower",
                    name = "Solar Sunflower",
                    scientificName = "Helianthus solaris",
                    origin = "Plains",
                    about = "Absorbs solar energy rapidly.",
                    light = "High",
                    waterLevel = "Medium",
                    soil = "Loam",
                    stage = "Seed",
                    imageUrl = "https://image.pollinations.ai/prompt/Solar%20Sunflower%20digital%20art?width=512&height=512&nologo=true",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Plains",
                    seedPrice = 1800,
                    rarity = "Epic"
                ),
                Plant(
                    id = "nebula_orchid",
                    name = "Nebula Orchid",
                    scientificName = "Orchidaceae nebula",
                    origin = "Nebula",
                    about = "A rare space orchid.",
                    light = "Medium",
                    waterLevel = "High",
                    soil = "Bark",
                    stage = "Cosmic Seed",
                    imageUrl = "https://image.pollinations.ai/prompt/Nebula%20Orchid%20digital%20art?width=512&height=512&nologo=true",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Nebula",
                    seedPrice = 5000,
                    rarity = "Legendary"
                ),
                Plant(
                    id = "ghost_lily",
                    name = "Ghost Lily",
                    scientificName = "Lilium spectra",
                    origin = "Swamp",
                    about = "Transparent petals that fade.",
                    light = "Low",
                    waterLevel = "High",
                    soil = "Mud",
                    stage = "Bulb",
                    imageUrl = "https://image.pollinations.ai/prompt/Ghost%20Lily%20digital%20art?width=512&height=512&nologo=true",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Swamp",
                    seedPrice = 1400,
                    rarity = "Epic"
                ),
                Plant(
                    id = "volcanic_bonsai",
                    name = "Volcanic Bonsai",
                    scientificName = "Bonsai vulcanica",
                    origin = "Volcano",
                    about = "Thrives in extreme heat.",
                    light = "High",
                    waterLevel = "Low",
                    soil = "Ash",
                    stage = "Seedling",
                    imageUrl = "https://image.pollinations.ai/prompt/Volcanic%20Bonsai%20digital%20art?width=512&height=512&nologo=true",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Volcanic",
                    seedPrice = 3200,
                    rarity = "Legendary"
                ),
                Plant(
                    id = "crystal_lotus",
                    name = "Crystal Lotus",
                    scientificName = "Nelumbo crystallina",
                    origin = "Caves",
                    about = "Petals made of organic crystal.",
                    light = "Low",
                    waterLevel = "Medium",
                    soil = "Gravel",
                    stage = "Seed",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAQ_hLnQZvLsJ9Cv3xtBmA7_FzJvGMJjYtfSdlvyckvpuOydfqJtyDcfZac1H2rwKslgnwJ-H-3lx-TjiRYmqwM7__Pvor45UAoPzRlS4arpngwzR6pJEcWydsyKZ07_NOn3WYJ5f9R92SWefwrRsMcbqHzhS4flewWtZwTGIilb5uvyzf3fseTXFGLe4xd_0mRUy8huvxzzrfWzpi1NLSsB6Da0a90jvNhVLxkJVJHlAT7SDKFSEV1Lsamh3I8IAFDL1u5u7_e-UA",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Caves",
                    seedPrice = 2100,
                    rarity = "Epic"
                ),
                Plant(
                    id = "golden_bamboo",
                    name = "Golden Bamboo",
                    scientificName = "Bambusoideae aurea",
                    origin = "Jungle",
                    about = "Grows at an incredible rate.",
                    light = "High",
                    waterLevel = "High",
                    soil = "Loam",
                    stage = "Shoot",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAQ_hLnQZvLsJ9Cv3xtBmA7_FzJvGMJjYtfSdlvyckvpuOydfqJtyDcfZac1H2rwKslgnwJ-H-3lx-TjiRYmqwM7__Pvor45UAoPzRlS4arpngwzR6pJEcWydsyKZ07_NOn3WYJ5f9R92SWefwrRsMcbqHzhS4flewWtZwTGIilb5uvyzf3fseTXFGLe4xd_0mRUy8huvxzzrfWzpi1NLSsB6Da0a90jvNhVLxkJVJHlAT7SDKFSEV1Lsamh3I8IAFDL1u5u7_e-UA",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Tropical",
                    seedPrice = 1100,
                    rarity = "Rare"
                ),
                Plant(
                    id = "frostbite_ivy",
                    name = "Frostbite Ivy",
                    scientificName = "Hedera gelida",
                    origin = "Tundra",
                    about = "Vines that freeze on touch.",
                    light = "Medium",
                    waterLevel = "Medium",
                    soil = "Peat",
                    stage = "Cutting",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAQ_hLnQZvLsJ9Cv3xtBmA7_FzJvGMJjYtfSdlvyckvpuOydfqJtyDcfZac1H2rwKslgnwJ-H-3lx-TjiRYmqwM7__Pvor45UAoPzRlS4arpngwzR6pJEcWydsyKZ07_NOn3WYJ5f9R92SWefwrRsMcbqHzhS4flewWtZwTGIilb5uvyzf3fseTXFGLe4xd_0mRUy8huvxzzrfWzpi1NLSsB6Da0a90jvNhVLxkJVJHlAT7SDKFSEV1Lsamh3I8IAFDL1u5u7_e-UA",
                    isCollected = false,
                    isWishlist = false,
                    waterNeeds = "Thirsty",
                    waterProgress = 0.0f,
                    isWidgetActive = false,
                    category = "Tundra",
                    seedPrice = 900,
                    rarity = "Rare"
                )
            )
            val defaultPlants = defaultPlantsRaw
            val existingIds = existingPlants?.map { it.id }?.toSet() ?: emptySet()
            val newPlants = defaultPlants.filter { !existingIds.contains(it.id) }
            if (newPlants.isNotEmpty()) {
                plantDao.insertPlants(newPlants)
            }
            
            // Delete any plants not in our top 10
            val allowedIds = defaultPlants.map { it.id }.toSet()
            existingPlants?.forEach { plant ->
                if (!allowedIds.contains(plant.id)) {
                    plantDao.deletePlant(plant)
                }
            }
        }
        
        // Force update existing plants to ensure they have the new Pollinations URL format
        plantDao.getAllPlantsFlow().firstOrNull()?.forEach { plant ->
            plantDao.updatePlant(plant.copy(imageUrl = PlantUtils.getPlantImageUrl(plant, "Tropical")))
        }
    }

    suspend fun updatePlant(plant: Plant) {
        plantDao.updatePlant(plant)
    }

    suspend fun waterPlant(plantId: String) {
        val plant = plantDao.getPlantById(plantId)
        val profile = userProfileDao.getUserProfile()
        if (plant != null && profile != null) {
            val currentStage = plant.stage
            val nextStage = when (currentStage) {
                "Seed", "Spore", "Cosmic Seed" -> "Sprout"
                "Sprout", "Seedling" -> "Small Plant"
                "Juvenile", "Small Plant" -> "Mature"
                else -> "Mature"
            }
            val updatedPlant = plant.copy(
                waterNeeds = "Good",
                waterProgress = 1.0f,
                lastWatered = System.currentTimeMillis(),
                stage = nextStage
            )
            plantDao.updatePlant(updatedPlant)

            val newXp = profile.xp + 15
            val newLevel = (newXp / 100) + 1
            val updatedProfile = profile.copy(
                totalWaterings = profile.totalWaterings + 1,
                wateringStreak = profile.wateringStreak + if (Math.random() > 0.6) 1 else 0,
                totalDaysWatered = profile.totalDaysWatered + if (Math.random() > 0.8) 1 else 0,
                xp = newXp,
                userLevel = newLevel
            )
            userProfileDao.updateUserProfile(updatedProfile)
        }
    }

    suspend fun updateWidgetActive(plantId: String) {
        plantDao.setActiveWidget(plantId)
        val profile = userProfileDao.getUserProfile()
        if (profile != null) {
            userProfileDao.updateUserProfile(profile.copy(activePlantId = plantId))
        }
    }

    suspend fun buySeedPackage(seeds: Int) {
        val profile = userProfileDao.getUserProfile()
        if (profile != null) {
            userProfileDao.updateUserProfile(
                profile.copy(seedBalance = profile.seedBalance + seeds)
            )
        }
    }

    suspend fun purchasePlant(plantId: String) : Boolean {
        val plant = plantDao.getPlantById(plantId)
        val profile = userProfileDao.getUserProfile()
        if (plant != null && profile != null && profile.seedBalance >= plant.seedPrice) {
            val initialStage = when (plant.category) {
                "Nebula" -> if (plant.id == "lunar_fern") "Spore" else "Cosmic Seed"
                else -> "Seed"
            }
            val updatedPlant = plant.copy(
                isCollected = true,
                isWishlist = false,
                waterNeeds = "Good",
                waterProgress = 1.0f,
                lastWatered = System.currentTimeMillis(),
                stage = initialStage
            )
            plantDao.updatePlant(updatedPlant)

            val newXp = profile.xp + 50
            val newLevel = (newXp / 100) + 1
            val updatedProfile = profile.copy(
                seedBalance = profile.seedBalance - plant.seedPrice,
                seedsPlanted = profile.seedsPlanted + 1,
                xp = newXp,
                userLevel = newLevel
            )
            userProfileDao.updateUserProfile(updatedProfile)
            return true
        }
        return false
    }

    suspend fun toggleFav(plantId: String) {
        val plant = plantDao.getPlantById(plantId)
        if (plant != null) {
            plantDao.updatePlant(plant.copy(isWishlist = !plant.isWishlist))
        }
    }

    suspend fun resetPlantGrowth(plantId: String) {
        val plant = plantDao.getPlantById(plantId)
        if (plant != null) {
            val resetStage = when (plant.category) {
                "Nebula" -> if (plant.id == "lunar_fern") "Spore" else "Cosmic Seed"
                else -> "Seed"
            }
            val updatedPlant = plant.copy(
                stage = resetStage,
                waterProgress = 0.5f,
                waterNeeds = "Good"
            )
            plantDao.updatePlant(updatedPlant)
        }
    }

    suspend fun updateProfile(profile: UserProfile) {
        userProfileDao.updateUserProfile(profile)
    }

    // MULTI-USER LOGIN & PROFILING SERVICE
    suspend fun createProfile(name: String, username: String, passcode: String, avatarColor: Int, growthPref: String): Boolean {
        val cleanUser = username.lowercase().trim()
        val exists = userProfileDao.getProfileByUsername(cleanUser)
        if (exists != null) return false

        userProfileDao.deactivateAllProfiles()

        val newProfile = UserProfile(
            id = (System.currentTimeMillis() % 100000).toInt(),
            name = name,
            username = cleanUser,
            passcode = passcode,
            isActive = true,
            avatarColor = avatarColor,
            growthPattern = growthPref,
            seedBalance = 500,
            totalWaterings = 0,
            wateringStreak = 0,
            totalDaysWatered = 0,
            plantsMatured = 0,
            seedsPlanted = 0,
            activePlantId = "monstera_deliciosa",
            collectedPlantIds = "monstera_deliciosa,pilea,string_of_pearls,snake_plant",
            wishlistPlantIds = "lunar_fern",
            isDarkMode = false
        )
        userProfileDao.insertUserProfile(newProfile)
        return true
    }

    suspend fun loginToProfile(username: String, passcode: String): Boolean {
        val cleanUser = username.lowercase().trim()
        val profile = userProfileDao.getProfileByUsername(cleanUser)
        if (profile != null && profile.passcode == passcode) {
            userProfileDao.deactivateAllProfiles()
            userProfileDao.updateUserProfile(profile.copy(isActive = true))
            return true
        }
        return false
    }

    suspend fun logoutActiveProfile() {
        userProfileDao.deactivateAllProfiles()
    }

    suspend fun getAllProfiles(): List<UserProfile> {
        return userProfileDao.getAllProfiles()
    }

    // FRIENDS SYSTEM
    suspend fun sendSeedsToFriend(friendUsername: String): Boolean {
        val profile = userProfileDao.getUserProfile() ?: return false
        if (profile.seedBalance < 50) return false

        userProfileDao.updateUserProfile(profile.copy(seedBalance = profile.seedBalance - 50))

        val friends = friendDao.getAllFriendsFlow().firstOrNull() ?: emptyList()
        val friendObj = friends.find { it.username == friendUsername }
        if (friendObj != null) {
            friendDao.updateFriend(
                friendObj.copy(
                    seedBalance = friendObj.seedBalance + 50,
                    lastSeedsSentDate = System.currentTimeMillis()
                )
            )
            return true
        }
        return false
    }

    suspend fun registerNewFriend(username: String, name: String, favPlant: String): Boolean {
        val cleanUser = username.lowercase().trim()
        val friends = friendDao.getAllFriendsFlow().firstOrNull() ?: emptyList()
        if (friends.any { it.username == cleanUser }) return false

        val randomLevel = (5..22).random()
        val randomSeeds = (1000..5000).random()
        val randomBonus = (5..20).random()
        val colors = listOf(0xFF2E5A36, 0xFF00F0FF, 0xFFE899A5, 0xFFCD7F32, 0xFF4CAF50)

        friendDao.insertFriend(
            Friend(
                username = cleanUser,
                name = name,
                level = randomLevel,
                seedBalance = randomSeeds,
                favoritePlantName = favPlant,
                gardenBonusPercentage = randomBonus,
                avatarColor = colors.random().toInt()
            )
        )
        return true
    }
}
