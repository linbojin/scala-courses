package observatory

case class Location(lat: Double, lon: Double)

case class Color(red: Int, green: Int, blue: Int)

case class Station(STN: Int, WBAN: Int, lat: Double, lon: Double)

case class Temperature(STN: Int, WBAN: Int, month: Int, day: Int, temp: Double)

case class Record(loc: Location, date: java.sql.Date, temp: Double)

