package org.natour.daosimpl;

import org.natour.daos.RouteDAO;
import org.natour.entities.LatLng;
import org.natour.entities.Route;
import org.natour.entities.RoutesCompilation;
import org.natour.exceptions.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RouteDAOMySql implements RouteDAO {

    private Connection connection;
    private PreparedStatement get_all_routes_statement, route_coordinates_statement, user_routes_statement, user_favourites_statement, get_routes_by_level_statement, get_tovisit_routes_statement;

    //Used for testing
    public RouteDAOMySql(){
    }

    public RouteDAOMySql(Connection connection) {
        this.connection = connection;
        prepareFrequentStatements();
    }

    private void prepareFrequentStatements() {
        try {

            String query_routes = "SELECT * from Routes r JOIN Coordinates c ON c.route_name=r.name ORDER BY r.creation_time DESC, r.name,c.seq_num";
            get_all_routes_statement = connection.prepareStatement(query_routes);

            String query_coordinates = "SELECT latitude, longitude FROM Coordinates WHERE route_name =? ORDER BY seq_num,route_name";
            route_coordinates_statement = connection.prepareStatement(query_coordinates, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String query_user_routes_statement = "SELECT * FROM Routes WHERE creator_username=? ORDER BY creation_time";
            user_routes_statement = connection.prepareStatement(query_user_routes_statement);

            String query_user_favourites_statement = "SELECT * FROM Favourites JOIN Routes ON Favourites.route_name=Routes.name WHERE Favourites.username=? ORDER BY Routes.creation_time";
            user_favourites_statement = connection.prepareStatement(query_user_favourites_statement);

            String query_get_routes_by_level = "SELECT * FROM Routes WHERE level=? ORDER BY creation_time";
            get_routes_by_level_statement = connection.prepareStatement(query_get_routes_by_level);

            String query_get_tovisit_routes = "SELECT * FROM ToVisit JOIN Routes ON ToVisit.route_name=Routes.name WHERE ToVisit.username=? ORDER BY Routes.creation_time";
            get_tovisit_routes_statement = connection.prepareStatement(query_get_tovisit_routes);

        } catch (SQLException e) {

            throw new RuntimeException("Something bad happened");

        }
    }


    @Override
    public List<Route> getUserRoutes(String username) throws PersistenceException {


        List<Route> routes = new ArrayList<>();
        ResultSet rs = null;
        ResultSet rs1 = null;

        try {

            user_routes_statement.setString(1, username);
            rs = user_routes_statement.executeQuery();

            while (rs.next()) {

                String route_name = rs.getString("name");

                Route route = new Route(route_name, rs.getString("description"), username, rs.getString("level"), rs.getInt("duration"),
                        rs.getInt("report_count"), rs.getBoolean("disability_access"), rs.getString("tags"), rs.getFloat("length"), rs.getInt("likes"));


                List<LatLng> coordinates = route.getCoordinates();

                route_coordinates_statement.setString(1, route_name);

                rs1 = route_coordinates_statement.executeQuery();

                while (rs1.next())
                    coordinates.add(new LatLng(rs1.getFloat("latitude"), rs1.getFloat("longitude")));

                routes.add(route);
            }

            return routes;

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        }finally{
            try {
                if(rs!=null)
                    rs.close();
                if(rs1!=null)
                    rs1.close();
            }catch(SQLException e){
                throw new PersistenceException("Something wrong happened");
            }
        }

    }

    @Override
    public List<Route> getUserFavourites(String username) throws PersistenceException {


        List<Route> routes = new ArrayList<>();
        ResultSet rs = null;
        ResultSet rs1 = null;

        try {

            user_favourites_statement.setString(1, username);
            rs = user_favourites_statement.executeQuery();

            while (rs.next()) {

                String route_name = rs.getString("name");

                Route route = new Route(route_name, rs.getString("description"), rs.getString("creator_username"), rs.getString("level"), rs.getInt("duration"),
                        rs.getInt("report_count"), rs.getBoolean("disability_access"), rs.getString("tags"), rs.getFloat("length"), rs.getInt("likes"));

                List<LatLng> coordinates = route.getCoordinates();

                route_coordinates_statement.setString(1, route_name);

                rs1 = route_coordinates_statement.executeQuery();

                while (rs1.next())
                    coordinates.add(new LatLng(rs1.getFloat("latitude"), rs1.getFloat("longitude")));

                routes.add(route);
            }

            return routes;

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        }finally{
            try {
                if(rs!=null)
                    rs.close();
                if(rs1!=null)
                    rs1.close();
            }catch(SQLException e){
                throw new PersistenceException("Something wrong happened");
            }
        }
    }

    @Override
    public List<Route> getUserToVisit(String username) throws PersistenceException {

        List<Route> routes = new ArrayList<>();
        ResultSet rs = null;
        ResultSet rs1 = null;

        try {

            get_tovisit_routes_statement.setString(1, username);
            rs = get_tovisit_routes_statement.executeQuery();

            while (rs.next()) {

                String route_name = rs.getString("name");

                Route route = new Route(route_name, rs.getString("description"), rs.getString("creator_username"), rs.getString("level"), rs.getInt("duration"),
                        rs.getInt("report_count"), rs.getBoolean("disability_access"), rs.getString("tags"), rs.getFloat("length"), rs.getInt("likes"));

                List<LatLng> coordinates = route.getCoordinates();

                route_coordinates_statement.setString(1, route_name);

                rs1 = route_coordinates_statement.executeQuery();

                while (rs1.next())
                    coordinates.add(new LatLng(rs1.getFloat("latitude"), rs1.getFloat("longitude")));

                routes.add(route);
            }

            return routes;

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        }finally{
            try {
                if(rs!=null)
                    rs.close();
                if(rs1!=null)
                    rs1.close();
            }catch(SQLException e){
                throw new PersistenceException("Something wrong happened");
            }
        }
    }



    @Override
    public void deleteFavourite(String username, String route_name) throws PersistenceException {

        String delete_route = "DELETE FROM Favourites WHERE username=? AND route_name=?";
        String update = "UPDATE Routes SET likes=likes - 1 WHERE name =?";
        PreparedStatement prepared_statement = null;

        try {
            prepared_statement = connection.prepareStatement(delete_route);
            prepared_statement.setString(1, username);
            prepared_statement.setString(2, route_name);
            prepared_statement.executeUpdate();

            prepared_statement = connection.prepareStatement(update);
            prepared_statement.setString(1, route_name);
            prepared_statement.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        } finally {
            try {
                prepared_statement.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something Went Wrong");
            }
        }

    }

    @Override
    public void deleteToVisit(String username, String route_name) throws PersistenceException {

        String delete_route = "DELETE FROM ToVisit WHERE username=? AND route_name=?";
        PreparedStatement prepared_statement = null;

        try {
            prepared_statement = connection.prepareStatement(delete_route);
            prepared_statement.setString(1, username);
            prepared_statement.setString(2, route_name);
            prepared_statement.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        } finally {
            try {
                prepared_statement.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something Went Wrong");
            }
        }

    }

    @Override
    public void insertToVisit(String username, String route_name) throws PersistenceException {

        String query_route = "INSERT INTO ToVisit (username, route_name) VALUES (?,?)";
        PreparedStatement prepared_statement = null;

        try {
            prepared_statement = connection.prepareStatement(query_route);

            prepared_statement.setString(1, username);
            prepared_statement.setString(2, route_name);

            prepared_statement.execute();

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        } finally {
            try {
                prepared_statement.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something Went Wrong");
            }
        }
    }


    public boolean hasUserAlreadyLiked(String username, String route_name) throws SQLException {

        String query = "SELECT * FROM Favourites WHERE username =? AND route_name =?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, route_name);

        ResultSet rs = statement.executeQuery();

        return rs.next();

    }

    @Override
    public void insertFavourite(String username, String route_name) throws PersistenceException {


        String insert = "INSERT INTO Favourites VALUES (?,?)";
        String update = "UPDATE Routes SET likes=likes + 1 WHERE name =?";
        PreparedStatement prepared_statement = null;

        try {

            if (hasUserAlreadyLiked(username, route_name))
                return;

            prepared_statement = connection.prepareStatement(update);
            prepared_statement.setString(1, route_name);
            prepared_statement.execute();

            prepared_statement = connection.prepareStatement(insert);
            prepared_statement.setString(1, username);
            prepared_statement.setString(2, route_name);
            prepared_statement.execute();

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        } finally {
            try {
                prepared_statement.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something Went Wrong");
            }
        }

    }

    @Override
    public void insertRouteIntoCompilation(String id, String route_name) throws PersistenceException {

        String insert = "INSERT INTO RoutesInCompilation VALUES (?, ?)";

        PreparedStatement prepared_statement = null;

        try {
            prepared_statement = connection.prepareStatement(insert);
            prepared_statement.setInt(1, Integer.parseInt(id));
            prepared_statement.setString(2, route_name);

            prepared_statement.execute();

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        } finally {
            try {
                prepared_statement.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something Went Wrong");
            }
        }

        return;
    }

    public List<RoutesCompilation> getUserRoutesCompilations(String username) throws PersistenceException {

        String get_user_compilations = "SELECT * FROM RoutesCompilations WHERE creator_username = ?";

        PreparedStatement prepared_statement = null;

        ResultSet rs = null;

        List<RoutesCompilation> routes_compilations = new ArrayList<>();

        try {

            //statement to fetch user compilations
            prepared_statement = connection.prepareStatement(get_user_compilations);
            prepared_statement.setString(1, username);

            rs = prepared_statement.executeQuery();

            //Cycling on user's routes compilations
            while(rs.next()){

                RoutesCompilation routes_compilation = new RoutesCompilation(String.valueOf(rs.getInt("id")), username, rs.getString("title"), rs.getString("description"));

                routes_compilations.add(routes_compilation);
            }

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        } finally {
            try {
                if(rs!=null)
                    rs.close();
                prepared_statement.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something Went Wrong");
            }
        }

        return routes_compilations;

    }

    public List<Route> getUserRoutesCompilation(String id) throws PersistenceException {

        String get_routes_in_compilation = "SELECT * FROM RoutesInCompilation AS C JOIN Routes AS R ON R.name = C.route_name WHERE C.id =? ORDER BY Routes.creation_time";

        PreparedStatement prepared_statement = null;

        List<Route> routes_in_compilation = new ArrayList<>();

        ResultSet rs = null;
        ResultSet rs2 = null;

        try {

            //statement to fetch routes in a given compilation
            prepared_statement = connection.prepareStatement(get_routes_in_compilation);

            prepared_statement.setInt(1, Integer.parseInt(id));

            rs = prepared_statement.executeQuery();

                //Cycling on routes of user compilation with given id
                while (rs.next()) {

                    String route_name = rs.getString("name");

                    Route route = new Route(route_name, rs.getString("description"), rs.getString("creator_username"), rs.getString("level"), rs.getInt("duration"),
                            rs.getInt("report_count"), rs.getBoolean("disability_access"), rs.getString("tags"), rs.getFloat("length"), rs.getInt("likes"));

                    List<LatLng> coordinates = route.getCoordinates();

                    route_coordinates_statement.setString(1, route_name);

                    rs2 = route_coordinates_statement.executeQuery();

                    //Cycling on coordinates of current route
                    while (rs2.next())
                        coordinates.add(new LatLng(rs2.getFloat("latitude"), rs2.getFloat("longitude")));

                    routes_in_compilation.add(route);

                }

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        } finally {
            try {
                if(rs!=null)
                    rs.close();
                if(rs2!=null)
                    rs2.close();
                prepared_statement.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something Went Wrong");
            }
        }

        return routes_in_compilation;

    }


}