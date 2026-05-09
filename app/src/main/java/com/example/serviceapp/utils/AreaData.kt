package com.example.serviceapp.utils

object AreaData {
    data class City(val enName: String, val bnName: String, val areas: List<String>)

    val cities = listOf(
        City("Dhaka", "ঢাকা", listOf(
            "Adabor", "Azimpur", "Badda", "Banani", "Baridhara", "Bashundhara",
            "Chawkbazar", "Dhanmondi", "Demra", "Elephant Road", "Gulshan",
            "Hazaribagh", "Jatrabari", "Kakrail", "Kalabagan", "Khilgaon",
            "Khilkhet", "Kotwali", "Lalbagh", "Lalmatia", "Malibagh",
            "Mirpur 1", "Mirpur 2", "Mirpur 10", "Mirpur 11", "Mirpur 12",
            "Mirpur 13", "Mirpur 14", "Mohakhali", "Mohammadpur", "Motijheel",
            "New Market", "Niketan", "Pallabi", "Rampura", "Ramna", "Rayerbazar",
            "Segunbagicha", "Shantinagar", "Shyamoli", "Tejgaon",
            "Uttara Sector 1", "Uttara Sector 3", "Uttara Sector 4",
            "Uttara Sector 6", "Uttara Sector 7", "Uttara Sector 10",
            "Uttara Sector 11", "Uttara Sector 12", "Wari"
        )),
        City("Gazipur", "গাজীপুর", listOf(
            "Gazipur Sadar", "Tongi", "Kaliakoir", "Sreepur", "Kapasia"
        )),
        City("Narayanganj", "নারায়ণগঞ্জ", listOf(
            "Narayanganj Sadar", "Fatullah", "Siddhirganj", "Rupganj", "Araihazar"
        )),
        City("Savar", "সাভার", listOf(
            "Savar", "Ashulia", "Hemayetpur", "Birulia", "Amin Bazar"
        )),
        City("Keraniganj", "কেরানীগঞ্জ", listOf(
            "Keraniganj", "Zinzira", "Shubhadya", "Tetuljhora"
        )),
        City("Chittagong", "চট্টগ্রাম", listOf(
            "Agrabad", "Bakalia", "Bayazid", "Chandgaon", "Double Mooring",
            "Halishahar", "Khulshi", "Nasirabad", "Pahartali", "Panchlaish",
            "Patenga", "Sitakund"
        )),
        City("Sylhet", "সিলেট", listOf(
            "Sylhet Sadar", "Jalalabad", "Osmani Nagar", "Bianibazar", "Golapganj"
        )),
        City("Rajshahi", "রাজশাহী", listOf(
            "Rajshahi City", "Boalia", "Motihar", "Shah Makhdum", "Paba"
        )),
        City("Khulna", "খুলনা", listOf(
            "Khulna City", "Sonadanga", "Khalishpur", "Khan Jahan Ali", "Daulatpur"
        )),
        City("Barisal", "বরিশাল", listOf(
            "Barisal City", "Kotwali", "Bandhar", "Kashipur", "Charbaria"
        )),
        City("Mymensingh", "ময়মনসিংহ", listOf(
            "Mymensingh Sadar", "Trishal", "Bhaluka", "Muktagacha", "Phulbaria"
        )),
        City("Comilla", "কুমিল্লা", listOf(
            "Comilla Sadar", "Adarsha Sadar", "Laksam", "Chauddagram", "Burichang"
        )),
        City("Rangpur", "রংপুর", listOf(
            "Rangpur City", "Mithapukur", "Pirganj", "Badarganj", "Gangachara"
        )),
        City("Jessore", "যশোর", listOf(
            "Jessore Sadar", "Chanchra", "Benapole", "Monirampur"
        )),
        City("Bogura", "বগুড়া", listOf(
            "Bogura Sadar", "Sherpur", "Gabtali", "Sariakandi"
        )),
    )

    val allAreas: List<String> get() = cities.flatMap { it.areas }
}
