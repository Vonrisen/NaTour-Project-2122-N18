package org.natour.daos;

import org.natour.entities.Route;
import org.natour.entities.RoutesCompilation;
import org.natour.exceptions.PersistenceException;

import java.util.List;

public interface RouteDAO {

    public void insertFavourite(String username, String route_name) throws PersistenceException;

    public void insertToVisit(String username, String route_name) throws PersistenceException;

    public void insertRouteIntoCompilation(String id, String route_name) throws PersistenceException;

    public List<Route> getUserRoutes(String username) throws PersistenceException;

    public List<Route> getUserFavourites(String username) throws PersistenceException;

    public List<Route> getUserToVisit(String username) throws PersistenceException;

    public List<RoutesCompilation> getUserRoutesCompilations(String username) throws PersistenceException;

    public List<Route> getUserRoutesCompilation(String id) throws PersistenceException;

    public void deleteFavourite(String username, String route_name) throws PersistenceException;

    public void deleteToVisit(String username, String route_name) throws PersistenceException;

}
