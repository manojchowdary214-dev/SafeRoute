package com.example.saferoute.repo

import com.example.saferoute.data.RouteDao
import com.example.saferoute.data.RouteEntity

class RouteRepository(
    private val routeDao: RouteDao
) {

    // insert route
    suspend fun insertRoute(route: RouteEntity) {
        routeDao.insertRoute(route)
    }

    // fetch by id
    suspend fun getRouteById(routeId: String): RouteEntity? {
        return routeDao.getRouteById(routeId)
    }

    // fetch all
    suspend fun getAllRoutes(): List<RouteEntity> {
        return routeDao.getAllRoutes()
    }

    // update route
    suspend fun updateRoute(route: RouteEntity) {
        routeDao.updateRoute(route)
    }

    // delete route
    suspend fun deleteRoute(route: RouteEntity) {
        routeDao.deleteRouteById(route.id)
    }

    // delete by id
    suspend fun deleteRouteById(routeId: String) {
        routeDao.deleteRouteById(routeId)
    }
}