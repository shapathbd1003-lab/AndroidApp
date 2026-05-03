package com.example.serviceapp.utils

data class ServiceCategory(
    val id:       String,
    val bnLabel:  String,
    val enLabel:  String,
    val icon:     String,
    val problems: List<ServiceProblem>
)

data class ServiceProblem(
    val bnLabel:     String,
    val enLabel:     String,
    val isWarning:   Boolean = false,
    val problemType: String  = "normal"
)

object ServiceData {

    val categories = listOf(
        ServiceCategory("electrician", "বিদ্যুৎ মিস্ত্রি", "Electrician", "⚡", listOf(
            ServiceProblem("সুইচ কাজ করছে না",       "Switch not working",             problemType = "normal"),
            ServiceProblem("লাইট কাজ করছে না",        "Light not working",              problemType = "normal"),
            ServiceProblem("সকেটে বিদ্যুৎ নেই",       "Socket no power",                problemType = "normal"),
            ServiceProblem("ফ্যান কাজ করছে না",        "Fan not working",                problemType = "normal"),
            ServiceProblem("ফ্যান ধীরে ঘুরছে",         "Fan running slow",               problemType = "normal"),
            ServiceProblem("বাল্ব বারবার পুড়ে যাচ্ছে", "Bulb keeps blowing",             problemType = "normal"),
            ServiceProblem("সার্কিট ব্রেকার পড়ছে",    "Circuit breaker tripping",       problemType = "advanced"),
            ServiceProblem("ভোল্টেজ সমস্যা",           "Voltage fluctuation",            problemType = "advanced"),
            ServiceProblem("লোডশেডিং সমস্যা নয়",      "Not a load-shedding issue",      problemType = "advanced"),
            ServiceProblem("নতুন ওয়্যারিং",            "New wiring",                     problemType = "advanced"),
            ServiceProblem("সুইচবোর্ড ইন্সটল",         "Switchboard install",            problemType = "advanced"),
            ServiceProblem("লাইট/ফ্যান লাগানো",         "Light/fan installation",         problemType = "advanced"),
            ServiceProblem("মিটার সমস্যা",              "Meter issue",                    problemType = "advanced"),
            ServiceProblem("শর্ট সার্কিট",             "Short circuit",                  problemType = "critical"),
            ServiceProblem("পোড়া গন্ধ",               "Burning smell",                  problemType = "critical"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("plumbing", "প্লাম্বিং ও স্যানিটারি", "Plumbing & Sanitary", "🚰", listOf(
            ServiceProblem("ট্যাপ থেকে পানি পড়ছে",   "Tap leaking",                    problemType = "normal"),
            ServiceProblem("টয়লেট ফ্লাশ হচ্ছে না",    "Toilet not flushing",            problemType = "normal"),
            ServiceProblem("বেসিন বন্ধ",               "Basin choke",                    problemType = "normal"),
            ServiceProblem("বাথরুমে পানি জমছে",        "Water pooling in bathroom",      problemType = "normal"),
            ServiceProblem("ট্যাংক ভরছে না",           "Tank not filling",               problemType = "normal"),
            ServiceProblem("পাইপ লিকেজ",               "Pipe leakage",                   problemType = "advanced"),
            ServiceProblem("কমোড ইন্সটল",              "Commode installation",            problemType = "advanced"),
            ServiceProblem("শাওয়ার সমস্যা",             "Shower problem",                 problemType = "advanced"),
            ServiceProblem("গিজার সমস্যা",              "Water heater issue",              problemType = "advanced"),
            ServiceProblem("পানির চাপ কম",              "Low water pressure",              problemType = "advanced"),
            ServiceProblem("পাম্প কাজ করছে না",         "Pump not working",               problemType = "advanced"),
            ServiceProblem("বাথরুম ফিটিং লাগানো",       "Bathroom fitting installation",  problemType = "advanced"),
            ServiceProblem("ড্রেন বন্ধ",                "Drain blockage",                 problemType = "critical"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("ac_fridge", "এসি / ফ্রিজ টেকনিশিয়ান", "AC / Fridge Technician", "❄️", listOf(
            ServiceProblem("এসি ঠান্ডা করছে না",       "AC not cooling",                 problemType = "normal"),
            ServiceProblem("এসি থেকে পানি পড়ছে",      "AC water leaking",               problemType = "normal"),
            ServiceProblem("অস্বাভাবিক শব্দ",          "Noise issue",                    problemType = "normal"),
            ServiceProblem("এসি পরিষ্কার",             "AC cleaning",                    problemType = "normal"),
            ServiceProblem("এসি চালু হচ্ছে না",        "AC not turning on",              problemType = "normal"),
            ServiceProblem("রিমোট কাজ করছে না",        "Remote not working",             problemType = "normal"),
            ServiceProblem("ফ্রিজ ঠান্ডা করছে না",     "Fridge not cooling",             problemType = "normal"),
            ServiceProblem("ফ্রিজ থেকে পানি পড়ছে",    "Fridge water leaking",           problemType = "normal"),
            ServiceProblem("গ্যাস রিফিল",              "Gas refill",                     problemType = "advanced"),
            ServiceProblem("এসি সার্ভিসিং",            "AC servicing",                   problemType = "advanced"),
            ServiceProblem("বরফ জমছে না",              "Ice issue",                      problemType = "advanced"),
            ServiceProblem("কম্প্রেসার সমস্যা",         "Compressor problem",              problemType = "critical"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("electronics", "ইলেকট্রনিক্স ও যন্ত্রপাতি", "Electronics & Appliance", "📺", listOf(
            ServiceProblem("টিভি কাজ করছে না",         "TV not working",                 problemType = "normal"),
            ServiceProblem("টিভি পিকচার নেই",           "TV no picture",                  problemType = "normal"),
            ServiceProblem("টিভি সাউন্ড নেই",           "TV no sound",                    problemType = "normal"),
            ServiceProblem("ওয়াশিং মেশিন সমস্যা",      "Washing machine issue",          problemType = "normal"),
            ServiceProblem("মাইক্রোওয়েভ সমস্যা",       "Microwave problem",              problemType = "normal"),
            ServiceProblem("রাইস কুকার সমস্যা",         "Rice cooker issue",              problemType = "normal"),
            ServiceProblem("ইন্ডাকশন কুকার সমস্যা",     "Induction cooker issue",         problemType = "normal"),
            ServiceProblem("ব্লেন্ডার/মিক্সার সমস্যা",   "Blender/mixer issue",            problemType = "normal"),
            ServiceProblem("পাওয়ার সমস্যা",            "Power issue",                    problemType = "advanced"),
            ServiceProblem("সার্কিট বোর্ড সমস্যা",      "Circuit board problem",          problemType = "advanced"),
            ServiceProblem("ডিসপ্লে সমস্যা",            "Display issue",                  problemType = "advanced"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("gas_stove", "গ্যাসের চুলা মিস্ত্রি", "Gas Stove Technician", "🔥", listOf(
            ServiceProblem("চুলা জ্বলছে না",           "Burner not lighting",             problemType = "normal"),
            ServiceProblem("আগুন কম",                  "Low flame",                      problemType = "normal"),
            ServiceProblem("ইগনিশন সমস্যা",            "Ignition problem",               problemType = "normal"),
            ServiceProblem("চুলা পরিষ্কার",             "Stove cleaning",                 problemType = "normal"),
            ServiceProblem("বার্নার ক্ষতিগ্রস্ত",        "Damaged burner",                 problemType = "normal"),
            ServiceProblem("রেগুলেটর সমস্যা",           "Regulator issue",                problemType = "advanced"),
            ServiceProblem("নতুন চুলা ইন্সটল",          "New stove installation",         problemType = "advanced"),
            ServiceProblem("গ্যাস লিকেজ", "Gas leakage", isWarning = true, problemType = "critical"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("carpenter", "কাঠের কাজ", "Carpenter", "🪵", listOf(
            ServiceProblem("দরজা মেরামত",              "Door repair",                    problemType = "normal"),
            ServiceProblem("দরজা আটকে যাচ্ছে",         "Door jamming",                   problemType = "normal"),
            ServiceProblem("তালা ঠিক করা",             "Lock fixing",                    problemType = "normal"),
            ServiceProblem("ফার্নিচার মেরামত",          "Furniture repair",               problemType = "normal"),
            ServiceProblem("জানালা মেরামত",             "Window repair",                  problemType = "normal"),
            ServiceProblem("চেয়ার/টেবিল ঠিক করা",       "Chair/table repair",             problemType = "normal"),
            ServiceProblem("ক্যাবিনেট কাজ",             "Cabinet work",                   problemType = "advanced"),
            ServiceProblem("নতুন ফার্নিচার",             "New furniture",                  problemType = "advanced"),
            ServiceProblem("পার্টিশন তৈরি",             "Partition/divider work",         problemType = "advanced"),
            ServiceProblem("ছাদের কাঠের কাজ",           "Ceiling wood work",              problemType = "advanced"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("painter", "রং ও পলিশ", "Painter / Polish", "🎨", listOf(
            ServiceProblem("দেয়ালে রং করা",            "Wall painting",                  problemType = "normal"),
            ServiceProblem("রং পরিবর্তন",              "Color change",                   problemType = "normal"),
            ServiceProblem("কাঠের পলিশ",               "Wood polish",                    problemType = "normal"),
            ServiceProblem("গ্রিল/রড রং করা",           "Grill/rod painting",             problemType = "normal"),
            ServiceProblem("পুরনো রং তুলে নতুন রং",     "Remove old paint, apply new",   problemType = "normal"),
            ServiceProblem("ফাটল মেরামত ও রং",          "Crack repair & paint",           problemType = "advanced"),
            ServiceProblem("পানির দাগ পরিষ্কার",         "Water stain removal",            problemType = "advanced"),
            ServiceProblem("বাইরের দেয়ালে রং",          "Exterior wall painting",         problemType = "advanced"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("cctv", "সিসিটিভি / নিরাপত্তা", "CCTV / Security", "📹", listOf(
            ServiceProblem("ক্যামেরা কাজ করছে না",      "Camera not working",             problemType = "normal"),
            ServiceProblem("DVR সমস্যা",               "DVR issue",                      problemType = "normal"),
            ServiceProblem("রেকর্ডিং হচ্ছে না",         "Not recording",                  problemType = "normal"),
            ServiceProblem("ছবি ঝাপসা",                "Blurry image",                   problemType = "normal"),
            ServiceProblem("নতুন ক্যামেরা লাগানো",      "Camera installation",            problemType = "advanced"),
            ServiceProblem("মোবাইলে দেখা যাচ্ছে না",    "Mobile app setup",               problemType = "advanced"),
            ServiceProblem("ক্যাবল লাগানো",             "Cable installation",             problemType = "advanced"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("internet", "ইন্টারনেট / নেটওয়ার্ক", "Internet / Network", "🌐", listOf(
            ServiceProblem("WiFi কাজ করছে না",          "WiFi not working",               problemType = "normal"),
            ServiceProblem("ইন্টারনেট ধীর",             "Slow speed",                     problemType = "normal"),
            ServiceProblem("WiFi সংযোগ বারবার কাটছে",   "WiFi keeps disconnecting",       problemType = "normal"),
            ServiceProblem("রাউটার সেটআপ",             "Router setup",                   problemType = "advanced"),
            ServiceProblem("ক্যাবল সমস্যা",             "Cable issue",                    problemType = "advanced"),
            ServiceProblem("নেটওয়ার্ক কনফিগার",          "Network configuration",          problemType = "advanced"),
            ServiceProblem("নতুন সংযোগ লাগানো",          "New connection setup",           problemType = "advanced"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("mason", "রাজমিস্ত্রি / টাইলস", "Mason / Tiles", "🧱", listOf(
            ServiceProblem("টাইলস মেরামত",             "Tile fixing",                    problemType = "normal"),
            ServiceProblem("টাইলস উঠে গেছে",           "Tiles came off",                 problemType = "normal"),
            ServiceProblem("মেঝে মেরামত",              "Floor repair",                   problemType = "normal"),
            ServiceProblem("দেয়ালে ফাটল",             "Wall crack",                     problemType = "normal"),
            ServiceProblem("প্লাস্টার খসে পড়ছে",       "Plaster falling off",            problemType = "normal"),
            ServiceProblem("নতুন টাইলস লাগানো",         "New tile installation",          problemType = "advanced"),
            ServiceProblem("বাথরুম কাজ",               "Bathroom work",                  problemType = "advanced"),
            ServiceProblem("সিমেন্ট কাজ",              "Cement work",                    problemType = "advanced"),
            ServiceProblem("ছাদ থেকে পানি চুঁয়ানো",    "Roof leakage",                   problemType = "critical"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
        ServiceCategory("cleaning", "ক্লিনিং সার্ভিস", "Cleaning Service", "🧹", listOf(
            ServiceProblem("সোফা পরিষ্কার",            "Sofa cleaning",                  problemType = "normal"),
            ServiceProblem("রান্নাঘর পরিষ্কার",         "Kitchen cleaning",               problemType = "normal"),
            ServiceProblem("বাথরুম পরিষ্কার",           "Bathroom cleaning",              problemType = "normal"),
            ServiceProblem("কার্পেট পরিষ্কার",          "Carpet cleaning",                problemType = "normal"),
            ServiceProblem("বিছানার চাদর/বালিশ ধোয়া",   "Bedding wash",                   problemType = "normal"),
            ServiceProblem("ডিপ ক্লিনিং",              "Deep cleaning",                  problemType = "advanced"),
            ServiceProblem("নির্মাণ পরবর্তী পরিষ্কার",   "Post-construction cleaning",     problemType = "advanced"),
            ServiceProblem("এসি ফিল্টার পরিষ্কার",      "AC filter cleaning",             problemType = "advanced"),
            ServiceProblem("অন্যান্য",                 "Other",                          problemType = "normal"),
        )),
    )

    fun categoryById(id: String) = categories.find { it.id == id }

    fun categoryLabel(id: String): String {
        val cat = categoryById(id) ?: return id
        return if (AppStrings.lang == AppLanguage.BN) cat.bnLabel else cat.enLabel
    }

    fun allowedTypes(skillLevel: String): Set<String> = when (skillLevel) {
        "expert"       -> setOf("normal", "advanced", "critical")
        "professional" -> setOf("normal", "advanced")
        else           -> setOf("normal")
    }
}
