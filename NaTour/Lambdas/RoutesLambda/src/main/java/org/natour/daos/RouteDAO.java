package org.natour.daos;

import org.natour.entities.QueryFilters;
import org.natour.entities.Report;
import org.natour.entities.Route;
import org.natour.entities.RoutesCompilation;
import org.natour.exceptions.PersistenceException;

import java.util.List;

public interface RouteDAO {

    public void insert(Route route) throws PersistenceException;

    public void insertReport(Report report) throws PersistenceException;

    public void createRoutesCompilation(RoutesCompilation routes_compilation) throws PersistenceException;

    public List<Route> getAll() throws PersistenceException;

    public List<Route> getN(int start, int end) throws PersistenceException;

    public List<Route> getFilteredRoutes(QueryFilters query_filters) throws PersistenceException;

    public List<Route> getRoutesByLevel(String level) throws PersistenceException;

    public String getFilteredSql(QueryFilters query_filters);

}
