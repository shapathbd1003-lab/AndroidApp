package com.example.serviceapp.utils

data class ServiceCategory(
    val id:       String,   // stored in Firestore
    val bnLabel:  String,
    val enLabel:  String,
    val icon:     String,
    val problems: List<ServiceProblem>
)

data class ServiceProblem(
    val bnLabel:   String,
    val enLabel:   String,
    val isWarning: Boolean = false
)

object ServiceData {

    val categories = listOf(
        ServiceCategory("electrician", "বিদ্যুৎ মিস্ত্রি", "Electrician", "⚡", listOf(
            ServiceProblem("সুইচ কাজ করছে না",       "Switch not working"),
            ServiceProblem("লাইট কাজ করছে না",        "Light not working"),
            ServiceProblem("সকেটে বিদ্যুৎ নেই",       "Socket no power"),
            ServiceProblem("ফ্যান কাজ করছে না",        "Fan not working"),
            ServiceProblem("সার্কিট ব্রেকার পড়ছে",    "Circuit breaker tripping"),
            ServiceProblem("শর্ট সার্কিট",             "Short circuit"),
            ServiceProblem("পোড়া গন্ধ",               "Burning smell"),
            ServiceProblem("ভোল্টেজ সমস্যা",           "Voltage fluctuation"),
            ServiceProblem("নতুন ওয়্যারিং",            "New wiring"),
            ServiceProblem("সুইচবোর্ড ইন্সটল",         "Switchboard install"),
            ServiceProblem("লাইট/ফ্যান লাগানো",         "Light/fan installation"),
            ServiceProblem("অন্যান্য",                 "Other"),
        )),
        ServiceCategory("plumbing", "প্লাম্বিং ও স্যানিটারি", "Plumbing & Sanitary", "🚰", listOf(
            ServiceProblem("ট্যাপ থেকে পানি পড়ছে",   "Tap leaking"),
            ServiceProblem("পাইপ লিকেজ",               "Pipe leakage"),
            ServiceProblem("টয়লেট ফ্লাশ হচ্ছে না",    "Toilet not flushing"),
            ServiceProblem("বেসিন বন্ধ",                "Basin choke"),
            ServiceProblem("কমোড ইন্সটল",              "Commode installation"),
            ServiceProblem("শাওয়ার সমস্যা",             "Shower problem"),
            ServiceProblem("গিজার সমস্যা",              "Water heater issue"),
            ServiceProblem("পানির চাপ কম",              "Low water pressure"),
            ServiceProblem("পাম্প কাজ করছে না",         "Pump not working"),
            ServiceProblem("ড্রেন বন্ধ",                "Drain blockage"),
            ServiceProblem("অন্যান্য",                 "Other"),
        )),
        ServiceCategory("ac_fridge", "এসি / ফ্রিজ টেকনিশিয়ান", "AC / Fridge Technician", "❄️", listOf(
            ServiceProblem("এসি ঠান্ডা করছে না",        "AC not cooling"),
            ServiceProblem("এসি থেকে পানি পড়ছে",       "AC water leaking"),
            ServiceProblem("অস্বাভাবিক শব্দ",           "Noise issue"),
            ServiceProblem("গ্যাস রিফিল",               "Gas refill"),
            ServiceProblem("এসি সার্ভিসিং",             "AC servicing"),
            ServiceProblem("এসি পরিষ্কার",              "AC cleaning"),
            ServiceProblem("ফ্রিজ ঠান্ডা করছে না",      "Fridge not cooling"),
            ServiceProblem("বরফ জমছে না",               "Ice issue"),
            ServiceProblem("কম্প্রেসার সমস্যা",          "Compressor problem"),
            ServiceProblem("অন্যান্য",                  "Other"),
        )),
        ServiceCategory("electronics", "ইলেকট্রনিক্স ও যন্ত্রপাতি", "Electronics & Appliance", "📺", listOf(
            ServiceProblem("টিভি কাজ করছে না",          "TV not working"),
            ServiceProblem("ওয়াশিং মেশিন সমস্যা",       "Washing machine issue"),
            ServiceProblem("মাইক্রোওয়েভ সমস্যা",        "Microwave problem"),
            ServiceProblem("রাইস কুকার সমস্যা",          "Rice cooker issue"),
            ServiceProblem("ইন্ডাকশন কুকার সমস্যা",      "Induction cooker issue"),
            ServiceProblem("পাওয়ার সমস্যা",              "Power issue"),
            ServiceProblem("সার্কিট বোর্ড সমস্যা",       "Circuit board problem"),
            ServiceProblem("ডিসপ্লে সমস্যা",             "Display issue"),
            ServiceProblem("অন্যান্য",                  "Other"),
        )),
        ServiceCategory("gas_stove", "গ্যাসের চুলা মিস্ত্রি", "Gas Stove Technician", "🔥", listOf(
            ServiceProblem("চুলা জ্বলছে না",             "Burner not lighting"),
            ServiceProblem("গ্যাস লিকেজ",               "Gas leakage", isWarning = true),
            ServiceProblem("আগুন কম",                   "Low flame"),
            ServiceProblem("ইগনিশন সমস্যা",             "Ignition problem"),
            ServiceProblem("রেগুলেটর সমস্যা",            "Regulator issue"),
            ServiceProblem("অন্যান্য",                  "Other"),
        )),
        ServiceCategory("carpenter", "কাঠের কাজ", "Carpenter", "🪵", listOf(
            ServiceProblem("দরজা মেরামত",               "Door repair"),
            ServiceProblem("তালা ঠিক করা",              "Lock fixing"),
            ServiceProblem("ফার্নিচার মেরামত",           "Furniture repair"),
            ServiceProblem("ক্যাবিনেট কাজ",              "Cabinet work"),
            ServiceProblem("নতুন ফার্নিচার",             "New furniture"),
            ServiceProblem("জানালা মেরামত",              "Window repair"),
            ServiceProblem("অন্যান্য",                  "Other"),
        )),
        ServiceCategory("painter", "রং ও পলিশ", "Painter / Polish", "🎨", listOf(
            ServiceProblem("দেয়ালে রং করা",              "Wall painting"),
            ServiceProblem("রং পরিবর্তন",               "Color change"),
            ServiceProblem("ফাটল মেরামত",               "Crack repair"),
            ServiceProblem("পানির দাগ",                 "Water damage"),
            ServiceProblem("কাঠের পলিশ",                "Wood polish"),
            ServiceProblem("অন্যান্য",                  "Other"),
        )),
        ServiceCategory("cctv", "সিসিটিভি / নিরাপত্তা", "CCTV / Security", "📹", listOf(
            ServiceProblem("ক্যামেরা কাজ করছে না",       "Camera not working"),
            ServiceProblem("DVR সমস্যা",                "DVR issue"),
            ServiceProblem("নতুন ক্যামেরা লাগানো",       "Camera installation"),
            ServiceProblem("মোবাইলে দেখা যাচ্ছে না",     "Mobile app setup"),
            ServiceProblem("অন্যান্য",                  "Other"),
        )),
        ServiceCategory("internet", "ইন্টারনেট / নেটওয়ার্ক", "Internet / Network", "🌐", listOf(
            ServiceProblem("WiFi কাজ করছে না",           "WiFi not working"),
            ServiceProblem("ইন্টারনেট ধীর",             "Slow speed"),
            ServiceProblem("রাউটার সেটআপ",              "Router setup"),
            ServiceProblem("ক্যাবল সমস্যা",             "Cable issue"),
            ServiceProblem("নেটওয়ার্ক কনফিগার",          "Network configuration"),
            ServiceProblem("অন্যান্য",                  "Other"),
        )),
        ServiceCategory("mason", "রাজমিস্ত্রি / টাইলস", "Mason / Tiles", "🧱", listOf(
            ServiceProblem("টাইলস মেরামত",              "Tile fixing"),
            ServiceProblem("মেঝে মেরামত",               "Floor repair"),
            ServiceProblem("দেয়ালে ফাটল",              "Wall crack"),
            ServiceProblem("ছাদ থেকে পানি চুঁয়ানো",     "Roof leakage"),
            ServiceProblem("বাথরুম কাজ",                "Bathroom work"),
            ServiceProblem("অন্যান্য",                  "Other"),
        )),
        ServiceCategory("cleaning", "ক্লিনিং সার্ভিস", "Cleaning Service", "🧹", listOf(
            ServiceProblem("ডিপ ক্লিনিং",               "Deep cleaning"),
            ServiceProblem("সোফা পরিষ্কার",             "Sofa cleaning"),
            ServiceProblem("রান্নাঘর পরিষ্কার",          "Kitchen cleaning"),
            ServiceProblem("বাথরুম পরিষ্কার",            "Bathroom cleaning"),
            ServiceProblem("অন্যান্য",                  "Other"),
        )),
    )

    fun categoryById(id: String) = categories.find { it.id == id }

    fun categoryLabel(id: String): String {
        val cat = categoryById(id) ?: return id
        return if (AppStrings.lang == AppLanguage.BN) cat.bnLabel else cat.enLabel
    }
}
