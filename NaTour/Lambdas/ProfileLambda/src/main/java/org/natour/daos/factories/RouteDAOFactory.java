package org.natour.daos.factories;

import org.natour.daos.RouteDAO;
import org.natour.daosimpl.RouteDAOMySql;

import java.sql.Connection;

public class RouteDAOFactory {

    private Connection connection;

    public RouteDAOFactory(Connection connection){
        this.connection = connection;
    }

    public RouteDAO getRouteDAO(String type){
        if (type.equalsIgnoreCase("mysql")){
            return new RouteDAOMySql(connection);
        }else{
           throw new IllegalArgumentException("The DAO implementation specified has not been implemented yet");
        }
    }

}
