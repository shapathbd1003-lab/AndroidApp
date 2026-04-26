package com.example.serviceapp.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage { EN, BN }

object AppStrings {
    var lang by mutableStateOf(AppLanguage.BN)
    private val isBn get() = lang == AppLanguage.BN
    fun toggle() { lang = if (isBn) AppLanguage.EN else AppLanguage.BN }

    // Entry / Auth
    val appName       get() = if (isBn) "মিস্ত্রি চাই" else "Mistri Chai"
    val appTagline    get() = if (isBn) "সেবা প্রদানকারী প্ল্যাটফর্ম" else "Find Skilled Professionals"
    val signIn        get() = if (isBn) "সাইন ইন" else "Sign In"
    val selectRole    get() = if (isBn) "আপনি কে? আপনার ভূমিকা বেছে নিন" else "Who are you? Select your role"
    val iAmProvider   get() = if (isBn) "আমি একজন মিস্ত্রি" else "I am a Provider"
    val providerSubtitle get() = if (isBn) "কাজ পান, আয় করুন" else "Get jobs, earn money"
    val iAmClient     get() = if (isBn) "আমি একজন গ্রাহক" else "I am a Client"
    val clientSubtitle get() = if (isBn) "সেবা বুক করুন" else "Book a service"
    val register      get() = if (isBn) "নিবন্ধন" else "Register"
    val alreadyHaveAccount get() = if (isBn) "ইতিমধ্যে অ্যাকাউন্ট আছে?" else "Already have an account?"
    val newProvider   get() = if (isBn) "নতুন প্রদানকারী?" else "New Provider?"
    val createAccount get() = if (isBn) "অ্যাকাউন্ট তৈরি করুন" else "Create Account"
    val loginSubtitle get() = if (isBn) "আবার স্বাগত! ইমেইল ও পাসওয়ার্ড দিয়ে লগইন করুন" else "Welcome back! Sign in with email and password"
    val enterPhone    get() = if (isBn) "ফোন নম্বর লিখুন" else "Enter phone number"
    val enterPhoneOrEmail get() = if (isBn) "ফোন নম্বর অথবা ইমেইল" else "Phone number or Email"
    val phoneNotFound get() = if (isBn) "ফোন বা ইমেইল পাওয়া যায়নি" else "Phone or email not found"
    val signOut       get() = if (isBn) "সাইন আউট" else "Sign Out"

    // Registration form
    val profilePhoto  get() = if (isBn) "প্রোফাইল ছবি" else "Profile Photo"
    val personalInfo  get() = if (isBn) "ব্যক্তিগত তথ্য" else "Personal Information"
    val fullName      get() = if (isBn) "পুরো নাম" else "Full Name"
    val phoneNumber   get() = if (isBn) "ফোন নম্বর" else "Phone Number"
    val emailAddress   get() = if (isBn) "ইমেইল (ঐচ্ছিক)" else "Email (Optional)"
    val emailRequired  get() = if (isBn) "ইমেইল" else "Email"
    val emailHint      get() = if (isBn) "লগইনে ব্যবহার করা যাবে" else "Can be used to sign in"
    val password       get() = if (isBn) "পাসওয়ার্ড" else "Password"
    val confirmPassword get() = if (isBn) "পাসওয়ার্ড নিশ্চিত করুন" else "Confirm Password"
    val passwordHint   get() = if (isBn) "কমপক্ষে ৬ অক্ষর" else "Minimum 6 characters"
    val passwordMismatch   get() = if (isBn) "পাসওয়ার্ড মিলছে না" else "Passwords do not match"

    // Pending / Approval
    val pendingTitle       get() = if (isBn) "আবেদন পর্যালোচনাধীন" else "Application Under Review"
    val pendingDesc        get() = if (isBn) "আপনার আবেদন যাচাই করা হচ্ছে। অ্যাডমিন অনুমোদনের পরে আপনি কাজ শুরু করতে পারবেন।" else "Your application is being reviewed. You can start working after admin approval."
    val pendingBadge       get() = if (isBn) "⏳ অপেক্ষমান" else "⏳ Pending"
    val waitingForApproval get() = if (isBn) "অনুমোদনের অপেক্ষায়..." else "Waiting for approval..."
    val rejectedTitle      get() = if (isBn) "আবেদন প্রত্যাখ্যাত" else "Application Rejected"
    val rejectedDesc       get() = if (isBn) "দুঃখিত, আপনার আবেদন গ্রহণ করা হয়নি। আরও তথ্যের জন্য অ্যাডমিনের সাথে যোগাযোগ করুন।" else "Sorry, your application was not accepted. Contact the admin for more information."
    val rejected           get() = if (isBn) "❌ প্রত্যাখ্যাত" else "❌ Rejected"
    val nidNumber     get() = if (isBn) "জাতীয় পরিচয়পত্র নম্বর" else "NID Number"
    val nidHint       get() = if (isBn) "১০ বা ১৭ সংখ্যার NID" else "10 or 17 digit NID"
    val baseFee       get() = if (isBn) "মূল সেবা ফি (৳)" else "Base Service Fee (৳)"
    val baseFeeHint   get() = if (isBn) "প্রতি সেবার মূল্য" else "Your fee per service"
    val serviceSpec   get() = if (isBn) "সেবার বিশেষত্ব" else "Service Specialization"
    val serviceHint   get() = if (isBn) "শুধুমাত্র এই সেবার কাজ পাবেন" else "You will only receive jobs for this service"
    val getStarted    get() = if (isBn) "শুরু করুন →" else "Get Started →"
    val fillAll       get() = if (isBn) "সব তথ্য পূরণ করুন" else "Fill all fields to continue"

    // Dashboard
    val welcomeBack   get() = if (isBn) "আবার স্বাগতম," else "Welcome back,"
    val overview      get() = if (isBn) "সারসংক্ষেপ" else "Overview"
    val rating        get() = if (isBn) "রেটিং" else "Rating"
    val earnings      get() = if (isBn) "আয়" else "Earnings"
    val due           get() = if (isBn) "বাকি" else "Due"
    val liveJobs      get() = if (isBn) "লাইভ কাজ" else "Live Jobs"
    val completed     get() = if (isBn) "সম্পন্ন" else "Completed"
    val generated     get() = if (isBn) "মোট" else "Generated"
    val recentActivity get() = if (isBn) "সাম্প্রতিক কার্যক্রম" else "Recent Activity"

    // Availability
    val availability  get() = if (isBn) "প্রাপ্যতা" else "Availability"
    val available     get() = if (isBn) "ফ্রি আছি" else "Free"
    val working       get() = if (isBn) "ব্যস্ত" else "Busy"
    val notAvailable  get() = if (isBn) "নেই" else "Away"

    // Jobs
    val availableJobs get() = if (isBn) "উপলব্ধ কাজ" else "Available Jobs"
    val pending       get() = if (isBn) "অপেক্ষমান" else "Pending"
    val accept        get() = if (isBn) "গ্রহণ করুন" else "Accept"
    val accepted      get() = if (isBn) "✓ গৃহীত" else "✓ Accepted"
    val acceptJob     get() = if (isBn) "কাজ গ্রহণ করুন" else "Accept Job"
    val jobAccepted   get() = if (isBn) "✓ গৃহীত" else "✓ Accepted"
    val problemOverview get() = if (isBn) "সমস্যার বিবরণ" else "Problem Overview"
    val address       get() = if (isBn) "ঠিকানা" else "Address"
    val contact       get() = if (isBn) "যোগাযোগ" else "Contact"
    val serviceFee    get() = if (isBn) "সেবা ফি" else "Service Fee"
    val jobLocation   get() = if (isBn) "কাজের অবস্থান" else "Job Location"
    val jobDetail     get() = if (isBn) "কাজের বিবরণ" else "Job Detail"
    val noJobs        get() = if (isBn) "এখনো কোনো কাজ নেই" else "No jobs yet"
    val jobsAppear    get() = if (isBn) "কাজ স্বয়ংক্রিয়ভাবে আসবে" else "Jobs appear automatically"
    val simControls   get() = if (isBn) "সিমুলেশন নিয়ন্ত্রণ" else "Simulation Controls"
    val fastModeLabel get() = if (isBn) "⚡ দ্রুত মোড (৪ সে.)" else "⚡ Fast Mode (4s)"
    val normalModeLabel get() = if (isBn) "🕐 স্বাভাবিক (১৫ সে.)" else "🕐 Normal Mode (15s)"
    val spawnNow      get() = if (isBn) "এখন কাজ যোগ করুন" else "Spawn Job Now"
    val jobNoLonger   get() = if (isBn) "কাজটি আর উপলব্ধ নেই" else "Job no longer available"
    val goBack        get() = if (isBn) "ফিরে যান" else "Go Back"

    // Profile
    val profile       get() = if (isBn) "প্রোফাইল" else "Profile"
    val editProfile   get() = if (isBn) "প্রোফাইল সম্পাদনা" else "Edit Profile"
    val serviceHistory get() = if (isBn) "সেবার ইতিহাস" else "Service History"
    val nid           get() = if (isBn) "NID" else "NID"
    val service       get() = if (isBn) "সেবা" else "Service"
    val jobs          get() = if (isBn) "কাজ" else "Jobs"
    val saveChanges   get() = if (isBn) "পরিবর্তন সংরক্ষণ" else "Save Changes"
    val noHistory     get() = if (isBn) "এখনো ইতিহাস নেই" else "No history yet"
    val acceptJobsHint get() = if (isBn) "ইতিহাস তৈরি করতে কাজ গ্রহণ করুন" else "Accept jobs to build your history"
    val language      get() = if (isBn) "ভাষা" else "Language"
    val updateInfo    get() = if (isBn) "আপনার তথ্য আপডেট করুন" else "Update your info"

    // Bottom nav
    val dashboard     get() = if (isBn) "ড্যাশবোর্ড" else "Dashboard"

    // Misc
    val orChoosePreset get() = if (isBn) "অথবা প্রিসেট বেছে নিন" else "or choose preset"
    val uploadGallery  get() = if (isBn) "গ্যালারি থেকে আপলোড" else "Upload from Gallery"
    val earned        get() = if (isBn) "আয় হয়েছে" else "earned"
    val baseFeeShort  get() = if (isBn) "মূল ফি" else "Base Fee"
    val perService    get() = if (isBn) "প্রতি সেবায়" else "per service"
    val jobsTotal        get() = if (isBn) "total" else "total"

    // Certificate
    val certificate      get() = if (isBn) "সার্টিফিকেট" else "Certificate"
    val certificateOpt   get() = if (isBn) "সার্টিফিকেট (ঐচ্ছিক)" else "Certificate (Optional)"
    val uploadCertificate get() = if (isBn) "সার্টিফিকেট আপলোড করুন" else "Upload Certificate"
    val noCertificate    get() = if (isBn) "কোনো সার্টিফিকেট নেই" else "No certificate uploaded"
    val viewCertificate  get() = if (isBn) "সার্টিফিকেট দেখুন" else "View Certificate"

    // ── Service type translations ────────────────────────────────────────────
    private val serviceTypeMap = mapOf(
        "এসি রিপেয়ার"       to "AC Repair",
        "প্লাম্বিং"          to "Plumbing",
        "ইলেকট্রিক কাজ"      to "Electrical Work",
        "ডিপ ক্লিনিং"        to "Deep Cleaning",
        "রং করা"             to "Painting",
        "কাঠের কাজ"          to "Carpentry",
        "যন্ত্রপাতি মেরামত"  to "Appliance Repair",
        "পোকামাকড় নিয়ন্ত্রণ" to "Pest Control"
    )

    // Translate stored Bengali key → current language label
    fun serviceTypeName(bnKey: String): String =
        if (isBn) bnKey else serviceTypeMap[bnKey] ?: bnKey

    // All service types as (bnKey, displayLabel) pairs for chips/selectors
    val allServiceTypes: List<Pair<String, String>> get() =
        ServiceData.categories.map { it.id to if (isBn) it.bnLabel else it.enLabel }

    // ── Skill level strings ──────────────────────────────────────────────────
    val skillLevelTitle    get() = if (isBn) "দক্ষতার স্তর" else "Skill Level"
    val skillLevelHint     get() = if (isBn) "আপনার অভিজ্ঞতা অনুযায়ী বেছে নিন" else "Choose based on your experience"

    val generalLabel       get() = if (isBn) "সাধারণ"    else "General"
    val professionalLabel  get() = if (isBn) "পেশাদার"   else "Professional"
    val expertLabel        get() = if (isBn) "বিশেষজ্ঞ"  else "Expert"

    val generalDesc        get() = if (isBn) "নতুন বা কম অভিজ্ঞ। মৌলিক কাজে পারদর্শী।"
                                   else "New or less experienced. Handles basic tasks."
    val professionalDesc   get() = if (isBn) "অভিজ্ঞ ও দক্ষ। বিভিন্ন ধরনের কাজ করতে পারেন।"
                                   else "Experienced & skilled. Handles diverse jobs."
    val expertDesc         get() = if (isBn) "অত্যন্ত অভিজ্ঞ। জটিল কাজেও দক্ষ।"
                                   else "Highly experienced. Expert in complex work."

    fun skillLevelName(level: String) = when (level) {
        "professional" -> professionalLabel
        "expert"       -> expertLabel
        else           -> generalLabel
    }
}
