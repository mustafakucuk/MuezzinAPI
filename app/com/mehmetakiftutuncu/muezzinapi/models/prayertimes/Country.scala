package com.mehmetakiftutuncu.muezzinapi.models.prayertimes

import anorm.NamedParameter
import com.mehmetakiftutuncu.muezzinapi.models.base.Jsonable
import com.mehmetakiftutuncu.muezzinapi.utilities.error.{Errors, SingleError}
import com.mehmetakiftutuncu.muezzinapi.utilities.{Database, Log}
import play.api.libs.json.{JsValue, Json}

/**
 * Represents a country
 *
 * @param id         Id of the country as a number
 * @param name       Name of the country in English
 * @param trName     Name of the country in Turkish
 * @param nativeName Name of the country in their native language
 */
case class Country(id: Int, name: String, trName: String, nativeName: String) extends Jsonable[Country] {
  /**
   * Converts this object to Json
   *
   * @return Json representation of this object
   */
  override def toJson: JsValue = Json.obj("id" -> id, "name" -> name, "trName" -> trName, "nativeName" -> nativeName)
}

/**
 * Companion object of Country
 */
object Country {
  /**
   * Gets all countries from database
   *
   * @return Some errors or a list of countries
   */
  def getAllFromDatabase: Either[Errors, List[Country]] = {
    Log.debug(s"""Getting all countries from database...""", "Country.getAllFromDatabase")

    try {
      val sql = anorm.SQL("SELECT * FROM Country ORDER BY name")

      val countryList = Database.apply(sql) map {
        row =>
          val id: Int            = row[Int]("id")
          val name: String       = row[String]("name")
          val trName: String     = row[String]("trName")
          val nativeName: String = row[String]("nativeName")

          Country(id, name, trName, nativeName)
      }

      Right(countryList)
    } catch {
      case t: Throwable =>
        Log.throwable(t, s"""Failed to get all countries from database!""", "Country.getAllFromDatabase")
        Left(Errors(SingleError.Database.withDetails("Failed to get all countries from database!")))
    }
  }

  /**
   * Saves given countries to database
   *
   * @param countries Countries to save to database
   *
   * @return Non-empty errors if something goes wrong
   */
  def saveAllToDatabase(countries: List[Country]): Errors = {
    if (countries.isEmpty) {
      Log.warn("Not saving empty list of countries...", "Country.saveAllToDatabase")
      Errors.empty
    } else {
      Log.debug(s"""Saving all countries to database...""", "Country.saveAllToDatabase")

      try {
        val valuesToParameters: List[(String, List[NamedParameter])] = countries.zipWithIndex.foldLeft(List.empty[(String, List[NamedParameter])]) {
          case (valuesToParameters: List[(String, List[NamedParameter])], (country: Country, index: Int)) =>
            val idKey: String         = s"id$index"
            val nameKey: String       = s"name$index"
            val trNameKey: String     = s"trName$index"
            val nativeNameKey: String = s"nativeName$index"

            valuesToParameters :+ {
              s"({$idKey}, {$nameKey}, {$trNameKey}, {$nativeNameKey})" -> List(
                NamedParameter(idKey,         country.id),
                NamedParameter(nameKey,       country.name),
                NamedParameter(trNameKey,     country.trName),
                NamedParameter(nativeNameKey, country.nativeName)
              )
            }
        }

        val sql = anorm.SQL(
          s"""
             |INSERT INTO Country (id, name, trName, nativeName)
             |VALUES ${valuesToParameters.map(_._1).mkString(", ")}
          """.stripMargin
        ).on(valuesToParameters.flatMap(_._2):_*)

        val savedCount = Database.executeUpdate(sql)

        if (savedCount != countries.size) {
          Log.error(s"""Failed to save ${countries.size} countries to database, affected row count was $savedCount!""", "Country.saveAllToDatabase")
          Errors(SingleError.Database.withDetails("Failed to save some countries to database!"))
        } else {
          Errors.empty
        }
      } catch {
        case t: Throwable =>
          Log.throwable(t, s"""Failed to save ${countries.size} countries to database!""", "Country.saveAllToDatabase")
          Errors(SingleError.Database.withDetails("Failed to save all countries to database!"))
      }
    }
  }

  /** A mapping of country ids of Diyanet to their English names */
  val countryIdToNameMap: Map[Int, String] = Map(
    166 -> "Afghanistan",
    13  -> "Germany",
    33  -> "United States of America",
    17  -> "Andorra",
    140 -> "Angola",
    125 -> "Anguilla",
    90  -> "Antigua and Barbuda",
    199 -> "Argentina",
    25  -> "Albania",
    153 -> "Aruba",
    59  -> "Australia",
    35  -> "Austria",
    5   -> "Azerbaijan",
    54  -> "Bahamas",
    132 -> "Bahrain",
    177 -> "Bangladesh",
    188 -> "Barbados",
    11  -> "Belgium",
    182 -> "Belize",
    181 -> "Benin",
    51  -> "Bermuda",
    208 -> "Belarus",
    93  -> "United Arab Emirates",
    83  -> "Bolivia",
    9   -> "Bosnia Herzegovina",
    167 -> "Botswana",
    146 -> "Brazil",
    97  -> "Brunei",
    44  -> "Bulgaria",
    91  -> "Burkina Faso",
    65  -> "Burundi",
    156 -> "Chad",
    16  -> "Czech Republic",
    86  -> "Algeria",
    160 -> "Djibouti",
    61  -> "People's Republic of China (PRC)",
    26  -> "Denmark",
    176 -> "East Timor",
    72  -> "Dominican Republic",
    123 -> "Dominica",
    139 -> "Ecuador",
    63  -> "Equatorial Guinea",
    165 -> "El Salvador",
    117 -> "Indonesia",
    175 -> "Eritrea",
    104 -> "Armenia",
    6   -> "Estonia",
    95  -> "Ethiopia",
    145 -> "Morocco",
    197 -> "Fiji",
    120 -> "Côte d'Ivoire",
    126 -> "Philippines",
    204 -> "Palestine",
    41  -> "Finland",
    21  -> "France",
    79  -> "Gabon",
    109 -> "Gambia",
    143 -> "Ghana",
    111 -> "Guinea",
    58  -> "Grenada",
    48  -> "Greenland",
    171 -> "Guadeloupe",
    169 -> "Guam",
    99  -> "Guatemala",
    67  -> "South Africa",
    128 -> "South Korea",
    62  -> "Georgia",
    70  -> "Haiti",
    187 -> "India",
    30  -> "Croatia",
    4   -> "Netherlands",
    66  -> "Netherlands Antilles",
    105 -> "Honduras",
    15  -> "England",
    124 -> "Iraq",
    202 -> "Iran",
    32  -> "Republic of Ireland",
    23  -> "Spain",
    205 -> "Israel",
    12  -> "Sweden",
    49  -> "Switzerland",
    8   -> "Italy",
    122 -> "Iceland",
    119 -> "Jamaica",
    116 -> "Japan",
    161 -> "Cambodia",
    184 -> "Cameroon",
    52  -> "Canada",
    34  -> "Montenegro",
    94  -> "Qatar",
    92  -> "Kazakhstan",
    114 -> "Kenya",
    168 -> "Kyrgyzstan",
    1   -> "Cyprus",
    57  -> "Colombia",
    88  -> "Comoros",
    180 -> "Republic of Congo",
    18  -> "Kosovo",
    162 -> "Costa Rica",
    209 -> "Cuba",
    133 -> "Kuwait",
    142 -> "North Korea",
    134 -> "Laos",
    174 -> "Lesotho",
    20  -> "Latvia",
    73  -> "Liberia",
    203 -> "Libya",
    38  -> "Liechtenstein",
    47  -> "Lithuania",
    42  -> "Lebanon",
    31  -> "Luxembourg",
    7   -> "Hungary",
    98  -> "Madagascar",
    28  -> "Republic of Macedonia",
    55  -> "Malawi",
    103 -> "Maldives",
    107 -> "Malaysia",
    152 -> "Mali",
    24  -> "Malta",
    87  -> "Martinique",
    164 -> "Mauritius",
    157 -> "Mayotte",
    53  -> "Mexico",
    189 -> "Egypt",
    60  -> "Mongolia",
    46  -> "Moldova",
    3   -> "Monaco",
    147 -> "Montserrat",
    106 -> "Mauritania",
    151 -> "Mozambique",
    154 -> "Myanmar",
    196 -> "Namibia",
    76  -> "Nepal",
    84  -> "Niger",
    127 -> "Nigeria",
    178 -> "Niue",
    36  -> "Norway",
    80  -> "Central African Republic",
    131 -> "Uzbekistan",
    77  -> "Pakistan",
    149 -> "Palau",
    89  -> "Panama",
    185 -> "Papua New Guinea",
    194 -> "Paraguay",
    69  -> "Peru",
    183 -> "Pitcairn Islands",
    39  -> "Poland",
    45  -> "Portugal",
    68  -> "Puerto Rico",
    112 -> "Réunion",
    37  -> "Romania",
    81  -> "Rwanda",
    207 -> "Russia",
    198 -> "Samoa",
    102 -> "Senegal",
    138 -> "Seychelles",
    200 -> "Chile",
    179 -> "Singapore",
    27  -> "Serbia",
    14  -> "Slovakia",
    19  -> "Slovenia",
    150 -> "Somalia",
    74  -> "Sri Lanka",
    129 -> "Sudan",
    172 -> "Suriname",
    191 -> "Syria]",
    64  -> "Saudi Arabia",
    163 -> "Svalbard",
    170 -> "Swaziland",
    101 -> "Tajikistan",
    110 -> "Tanzania",
    137 -> "Thailand",
    108 -> "Taiwan",
    71  -> "Togo",
    130 -> "Tonga",
    96  -> "Trinidad and Tobago",
    118 -> "Tunisia",
    2   -> "Turkey",
    159 -> "Turkmenistan",
    75  -> "Uganda",
    40  -> "Ukraine",
    173 -> "Oman",
    192 -> "Jordan",
    201 -> "Uruguay",
    56  -> "Vanuatu",
    10  -> "Vatican City",
    186 -> "Venezuela",
    135 -> "Vietnam",
    148 -> "Yemen",
    115 -> "New Caledonia",
    193 -> "New Zealand",
    144 -> "Cape Verde",
    22  -> "Greece",
    158 -> "Zambia",
    136 -> "Zimbabwe"
  )

  /** A mapping of country ids of Diyanet to their Turkish names */
  val countryIdToTurkishNameMap: Map[Int, String] = Map(
    166 -> "Afganistan",
    13  -> "Almanya",
    33  -> "Amerika Birleşik Devletleri",
    17  -> "Andorra",
    140 -> "Angola",
    125 -> "Anguilla",
    90  -> "Antigua ve Barbuda",
    199 -> "Arjantin",
    25  -> "Arnavutluk",
    153 -> "Aruba",
    59  -> "Avustralya",
    35  -> "Avusturya",
    5   -> "Azerbaycan",
    54  -> "Bahamalar",
    132 -> "Bahreyn",
    177 -> "Bangladeş",
    188 -> "Barbados",
    11  -> "Belçika",
    182 -> "Belize",
    181 -> "Benin",
    51  -> "Bermuda",
    208 -> "Beyaz Rusya",
    93  -> "Birleşik Arap Emirlikleri",
    83  -> "Bolivya",
    9   -> "Bosna-Hersek",
    167 -> "Botsvana",
    146 -> "Brezilya",
    97  -> "Bruney",
    44  -> "Bulgaristan",
    91  -> "Burkina Faso",
    65  -> "Burundi",
    156 -> "Çad",
    16  -> "Çek Cumhuriyeti",
    86  -> "Cezayir",
    160 -> "Cibuti",
    61  -> "Çin (PRC)",
    26  -> "Danimarka",
    176 -> "Doğu Timor",
    72  -> "Dominik Cumhuriyeti",
    123 -> "Dominika",
    139 -> "Ekvador",
    63  -> "Ekvator Ginesi",
    165 -> "El Salvador",
    117 -> "Endonezya",
    175 -> "Eritre",
    104 -> "Ermenistan",
    6   -> "Estonya",
    95  -> "Etiyopya",
    145 -> "Fas",
    197 -> "Fiji",
    120 -> "Fildişi Sahili",
    126 -> "Filipinler",
    204 -> "Filistin",
    41  -> "Finlandiya",
    21  -> "Fransa",
    79  -> "Gabon",
    109 -> "Gambiya",
    143 -> "Gana",
    111 -> "Gine",
    58  -> "Grenada",
    48  -> "Grönland",
    171 -> "Guadeloupe",
    169 -> "Guam",
    99  -> "Guatemala",
    67  -> "Güney Afrika",
    128 -> "Güney Kore",
    62  -> "Gürcistan",
    70  -> "Haiti",
    187 -> "Hindistan",
    30  -> "Hırvatistan",
    4   -> "Hollanda",
    66  -> "Hollanda Antilleri",
    105 -> "Honduras",
    15  -> "İngiltere (Birleşik Krallık)",
    124 -> "Irak",
    202 -> "İran",
    32  -> "İrlanda",
    23  -> "İspanya",
    205 -> "İsrail",
    12  -> "İsveç",
    49  -> "İsviçre",
    8   -> "İtalya",
    122 -> "İzlanda",
    119 -> "Jamaika",
    116 -> "Japonya",
    161 -> "Kamboçya",
    184 -> "Kamerun",
    52  -> "Kanada",
    34  -> "Karadağ",
    94  -> "Katar",
    92  -> "Kazakistan",
    114 -> "Kenya",
    168 -> "Kırgızistan",
    1   -> "KKTC",
    57  -> "Kolombiya",
    88  -> "Komorlar",
    180 -> "Kongo",
    18  -> "Kosova",
    162 -> "Kosta Rika",
    209 -> "Küba",
    133 -> "Kuveyt",
    142 -> "Kuzey Kore",
    134 -> "Laos",
    174 -> "Lesotho",
    20  -> "Letonya",
    73  -> "Liberya",
    203 -> "Libya",
    38  -> "Lihtenştayn",
    47  -> "Litvanya",
    42  -> "Lübnan",
    31  -> "Lüksemburg",
    7   -> "Macaristan",
    98  -> "Madagaskar",
    28  -> "Makedonya",
    55  -> "Malavi",
    103 -> "Maldivler",
    107 -> "Malezya",
    152 -> "Mali",
    24  -> "Malta",
    87  -> "Martinique",
    164 -> "Mauritius",
    157 -> "Mayotte",
    53  -> "Meksika",
    189 -> "Mısır",
    60  -> "Moğolistan",
    46  -> "Moldova",
    3   -> "Monako",
    147 -> "Montserrat",
    106 -> "Moritanya",
    151 -> "Mozambik",
    154 -> "Myanmar",
    196 -> "Namibya",
    76  -> "Nepal",
    84  -> "Nijer",
    127 -> "Nijerya",
    178 -> "Niue",
    36  -> "Norveç",
    80  -> "Orta Afrika Cumhuriyeti",
    131 -> "Özbekistan",
    77  -> "Pakistan",
    149 -> "Palau",
    89  -> "Panama",
    185 -> "Papua Yeni Gine",
    194 -> "Paraguay",
    69  -> "Peru",
    183 -> "Pitcairn Adaları",
    39  -> "Polonya",
    45  -> "Portekiz",
    68  -> "Porto Riko",
    112 -> "Réunion",
    37  -> "Romanya",
    81  -> "Ruanda",
    207 -> "Rusya",
    198 -> "Samoa",
    102 -> "Senegal",
    138 -> "Seyşeller",
    200 -> "Şili",
    179 -> "Singapur",
    27  -> "Sırbistan",
    14  -> "Slovakya",
    19  -> "Slovenya",
    150 -> "Somali",
    74  -> "Sri Lanka",
    129 -> "Sudan",
    172 -> "Surinam",
    191 -> "Suriye",
    64  -> "Suudi Arabistan",
    163 -> "Svalbard",
    170 -> "Svaziland",
    101 -> "Tacikistan",
    110 -> "Tanzanya",
    137 -> "Tayland",
    108 -> "Tayvan",
    71  -> "Togo",
    130 -> "Tonga",
    96  -> "Trinidad ve Tobago",
    118 -> "Tunus",
    2   -> "Türkiye",
    159 -> "Türkmenistan",
    75  -> "Uganda",
    40  -> "Ukrayna",
    173 -> "Umman",
    192 -> "Ürdün",
    201 -> "Uruguay",
    56  -> "Vanuatu",
    10  -> "Vatikan",
    186 -> "Venezuela",
    135 -> "Vietnam",
    148 -> "Yemen",
    115 -> "Yeni Kaledonya",
    193 -> "Yeni Zelanda",
    144 -> "Yeşil Burun",
    22  -> "Yunanistan",
    158 -> "Zambiya",
    136 -> "Zimbabve"
  )

  /** A mapping of country ids of Diyanet to their native names */
  val countryIdToNativeNameMap: Map[Int, String] = Map(
    166 -> "افغانستان",
    13  -> "Deutschland",
    33  -> "United States of America",
    17  -> "Andorra",
    140 -> "Angola",
    125 -> "Anguilla",
    90  -> "Antigua and Barbuda",
    199 -> "Argentina",
    25  -> "Shqipëria",
    153 -> "Aruba",
    59  -> "Australia",
    35  -> "Österreich",
    5   -> "Azərbaycan",
    54  -> "Bahamas",
    132 -> "البحرين",
    177 -> "বাংলাদেশ",
    188 -> "Barbados",
    11  -> "België",
    182 -> "Belize",
    181 -> "Bénin",
    51  -> "Bermuda",
    208 -> "Беларусь",
    93  -> "الإمارات العربيّة المتّحدة",
    83  -> "Buliwya",
    9   -> "Босна и Херцеговина",
    167 -> "Botswana",
    146 -> "Brasil",
    97  -> "بروني",
    44  -> "България",
    91  -> "Burkina Faso",
    65  -> "Burundi",
    156 -> "تشاد",
    16  -> "Česká republika",
    86  -> "الجزائر",
    160 -> "جيبوتي",
    61  -> "中国 (中华人民共和国)",
    26  -> "Danmark",
    176 -> "Timor Lorosa'e",
    72  -> "República Dominicana",
    123 -> "Dominica",
    139 -> "Ecuador",
    63  -> "Guinea Ecuatorial",
    165 -> "El Salvador",
    117 -> "Indonesia",
    175 -> "إرتريا",
    104 -> "Հայաստան",
    6   -> "Eesti",
    95  -> "ኢትዮጲያ",
    145 -> "المغرب",
    197 -> "Fiji",
    120 -> "Côte d'Ivoire",
    126 -> "Pilipinas",
    204 -> "فلسطين",
    41  -> "Suomi",
    21  -> "France",
    79  -> "Gabon",
    109 -> "Gambia",
    143 -> "Ghana",
    111 -> "Guinée",
    58  -> "Grenada",
    48  -> "Grønland",
    171 -> "Guadeloupe",
    169 -> "Guam",
    99  -> "Guatemala",
    67  -> "South Africa",
    128 -> "한국 / 韓國",
    62  -> "საქართველო",
    70  -> "Haïti",
    187 -> "India",
    30  -> "Hrvatska",
    4   -> "Nederland",
    66  -> "Nederlandse Antillen",
    105 -> "Honduras",
    15  -> "United Kingdom",
    124 -> "العراق",
    202 -> "ایران",
    32  -> "Éire",
    23  -> "España",
    205 -> "ישראל",
    12  -> "Sverige",
    49  -> "Schweiz",
    8   -> "Italia",
    122 -> "Ísland",
    119 -> "Jamaica",
    116 -> "日本",
    161 -> "Kampuchea",
    184 -> "Cameroon",
    52  -> "Canada",
    34  -> "Црна Гора",
    94  -> "قطر",
    92  -> "Қазақстан",
    114 -> "Kenya",
    168 -> "Кыргызстан",
    1   -> "Kıbrıs",
    57  -> "Colombia",
    88  -> "Komori",
    180 -> "Republic of Congo",
    18  -> "Косово",
    162 -> "Costa Rica",
    209 -> "Cuba",
    133 -> "الكويت",
    142 -> "조선 / 朝鮮",
    134 -> "ລາວ",
    174 -> "Lesotho",
    20  -> "Latvija",
    73  -> "Liberia",
    203 -> "ليبيا",
    38  -> "Liechtenstein",
    47  -> "Lietuva",
    42  -> "لبنان",
    31  -> "Lëtzebuerg",
    7   -> "Magyarország",
    98  -> "Madagasikara",
    28  -> "Македонија",
    55  -> "Malawi",
    103 -> "Dhivehi Raajje",
    107 -> "Malaysia",
    152 -> "Mali",
    24  -> "Malta",
    87  -> "Martinique",
    164 -> "Maurice",
    157 -> "Mayotte",
    53  -> "México",
    189 -> "مصر",
    60  -> "Монгол Улс",
    46  -> "Moldova",
    3   -> "Monaco",
    147 -> "Montserrat",
    106 -> "Mauritanie",
    151 -> "Moçambique",
    154 -> "Myanma",
    196 -> "Namibia",
    76  -> "नेपाल",
    84  -> "Niger",
    127 -> "Nigeria",
    178 -> "Niue",
    36  -> "Norge",
    80  -> "République Centrafricaine",
    131 -> "O'zbekiston",
    77  -> "پاکستان",
    149 -> "Belau",
    89  -> "Panamá",
    185 -> "Papua New Guinea",
    194 -> "Paraguay",
    69  -> "Perú",
    183 -> "Pitcairn Islands",
    39  -> "Polska",
    45  -> "Portugal",
    68  -> "Puerto Rico",
    112 -> "Réunion",
    37  -> "Romania",
    81  -> "Rwanda",
    207 -> "Россия",
    198 -> "Samoa",
    102 -> "Sénégal",
    138 -> "Sesel",
    200 -> "Chile",
    179 -> "Singapura",
    27  -> "Србија",
    14  -> "Slovensko",
    19  -> "Slovenija",
    150 -> "Soomaaliya",
    74  -> "Sri Lankā",
    129 -> "السودان",
    172 -> "Suriname",
    191 -> "سورية",
    64  -> "Saudi Arabia",
    163 -> "Svalbard",
    170 -> "Swaziland",
    101 -> "Тоҷикистон",
    110 -> "Tanzania",
    137 -> "Mueang Thai",
    108 -> "中華民國",
    71  -> "Togo",
    130 -> "Tonga",
    96  -> "Trinidad and Tobago",
    118 -> "تونس",
    2   -> "Türkiye",
    159 -> "Türkmenistan",
    75  -> "Uganda",
    40  -> "Україна",
    173 -> "عُمان",
    192 -> "الأردن",
    201 -> "República Oriental del Uruguay",
    56  -> "Vanuatu",
    10  -> "Civitas Vaticana",
    186 -> "Venezuela",
    135 -> "Việt Nam",
    148 -> "اليمن",
    115 -> "Nouvelle-Calédonie",
    193 -> "New Zealand",
    144 -> "Cabo Verde",
    22  -> "Ελλάδα",
    158 -> "Zambia",
    136 -> "Zimbabwe"
  )
}
