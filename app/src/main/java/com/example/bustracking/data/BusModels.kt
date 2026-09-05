package com.example.bustracking.data

data class BusStop(
    val id: String,
    val name: String,
    val scheduledTime: String,
    val isPassed: Boolean = false,
    val isCurrent: Boolean = false
)

data class Bus(
    val id: String,
    val busNumber: String,
    val routeName: String,
    val origin: String,
    val destination: String,
    val status: String,
    val etaMinutes: Int,
    val distanceRemainingKm: Double,
    val speedKmh: Int,
    val occupancy: String,
    val driverName: String,
    val driverPhone: String,
    val stops: List<BusStop>,
    var isFavorite: Boolean = false
)

object BusRepository {
    private val buses = mutableListOf(
        Bus(
            id = "101",
            busNumber = "Bus 101",
            routeName = "Campus Express",
            origin = "Main University Gate",
            destination = "City Central Junction",
            status = "On Route",
            etaMinutes = 8,
            distanceRemainingKm = 3.2,
            speedKmh = 42,
            occupancy = "Seats Available (45%)",
            driverName = "Rajesh Sharma",
            driverPhone = "+91 98765 43210",
            isFavorite = true,
            stops = listOf(
                BusStop("s1", "Main University Gate", "08:00 AM", isPassed = true),
                BusStop("s2", "Engineering Block B", "08:12 AM", isPassed = true),
                BusStop("s3", "South Lake Point", "08:25 AM", isPassed = false, isCurrent = true),
                BusStop("s4", "Tech Park Crossing", "08:38 AM", isPassed = false),
                BusStop("s5", "City Central Junction", "08:50 AM", isPassed = false)
            )
        ),
        Bus(
            id = "204",
            busNumber = "Bus 204",
            routeName = "Metro Feeder",
            origin = "North Terminal",
            destination = "Central Metro Hub",
            status = "Approaching",
            etaMinutes = 4,
            distanceRemainingKm = 1.4,
            speedKmh = 35,
            occupancy = "Crowded (85%)",
            driverName = "Amit Verma",
            driverPhone = "+91 98111 22334",
            isFavorite = false,
            stops = listOf(
                BusStop("s201", "North Terminal", "08:10 AM", isPassed = true),
                BusStop("s202", "Market Circle", "08:22 AM", isPassed = false, isCurrent = true),
                BusStop("s203", "Hospital Road", "08:31 AM", isPassed = false),
                BusStop("s204", "Central Metro Hub", "08:45 AM", isPassed = false)
            )
        ),
        Bus(
            id = "305B",
            busNumber = "Bus 305B",
            routeName = "Suburban Shuttle",
            origin = "Greenwood Heights",
            destination = "Civic Center",
            status = "On Schedule",
            etaMinutes = 14,
            distanceRemainingKm = 6.8,
            speedKmh = 50,
            occupancy = "Few Seats (70%)",
            driverName = "Suresh Patil",
            driverPhone = "+91 99220 54321",
            isFavorite = false,
            stops = listOf(
                BusStop("s301", "Greenwood Heights", "08:05 AM", isPassed = true),
                BusStop("s302", "Pine Ridge Crossing", "08:18 AM", isPassed = false, isCurrent = true),
                BusStop("s303", "Ring Road Junction", "08:30 AM", isPassed = false),
                BusStop("s304", "Civic Center", "08:42 AM", isPassed = false)
            )
        ),
        Bus(
            id = "42A",
            busNumber = "Bus 42A",
            routeName = "Night Transit",
            origin = "Railway Station",
            destination = "Airport Link Terminal",
            status = "Delayed (5m)",
            etaMinutes = 21,
            distanceRemainingKm = 9.5,
            speedKmh = 28,
            occupancy = "Empty (20%)",
            driverName = "Deepak Nair",
            driverPhone = "+91 97444 88990",
            isFavorite = true,
            stops = listOf(
                BusStop("s401", "Railway Station", "07:50 AM", isPassed = true),
                BusStop("s402", "Clock Tower Square", "08:05 AM", isPassed = true),
                BusStop("s403", "Harbor Overbridge", "08:24 AM", isPassed = false, isCurrent = true),
                BusStop("s404", "Airport Link Terminal", "08:45 AM", isPassed = false)
            )
        )
    )

    fun getAllBuses(): List<Bus> = buses

    fun getBusById(id: String): Bus? = buses.find { it.id == id } ?: buses.firstOrNull()

    fun toggleFavorite(busId: String): Boolean {
        val index = buses.indexOfFirst { it.id == busId }
        if (index != -1) {
            val updated = buses[index].copy(isFavorite = !buses[index].isFavorite)
            buses[index] = updated
            return updated.isFavorite
        }
        return false
    }

    fun getFavorites(): List<Bus> = buses.filter { it.isFavorite }

    fun searchBuses(query: String): List<Bus> {
        if (query.isBlank()) return buses
        return buses.filter {
            it.busNumber.contains(query, ignoreCase = true) ||
            it.routeName.contains(query, ignoreCase = true) ||
            it.destination.contains(query, ignoreCase = true) ||
            it.origin.contains(query, ignoreCase = true)
        }
    }
}
