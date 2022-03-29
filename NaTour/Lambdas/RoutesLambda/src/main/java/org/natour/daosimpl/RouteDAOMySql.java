package org.natour.daosimpl;

import org.natour.daos.RouteDAO;
import org.natour.entities.*;
import org.natour.exceptions.PersistenceException;
import org.natour.s3.NatourS3Bucket;
import org.natour.utilities.GeoDistance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RouteDAOMySql implements RouteDAO {

    private Connection connection;
    private PreparedStatement get_all_routes_statement, route_coordinates_statement, get_routes_by_level_statement;

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

            String query_get_routes_by_level = "SELECT * FROM Routes WHERE level=? ORDER BY creation_time";
            get_routes_by_level_statement = connection.prepareStatement(query_get_routes_by_level);

        } catch (SQLException e) {

            throw new RuntimeException("Something bad happened");

        }
    }

    @Override
    public List<Route> getAll() throws PersistenceException {

        List<Route> routes = new ArrayList<>();

        String old_route_name = "";

        String new_route_name;

        List<LatLng> coordinates = null;

        ResultSet rs = null;

        try {

            rs = get_all_routes_statement.executeQuery();

            while (rs.next()) //Scorro riga per riga
            {
                new_route_name = rs.getString("name");

                if (old_route_name.equals(new_route_name)) { //Se l itinerario precedentemente letto è uguale a quello corrente (Alla prima iterazione sono sempre diversi)

                    //L oggetto route è gia stato creato, devo solo aggiungere le coordinate
                    coordinates.add(new LatLng(rs.getFloat("latitude"), rs.getFloat("longitude")));

                } else {//Se iniziano i record di un itinerario diverso


                    //Creo l oggetto route
                    Route route = new Route(new_route_name, rs.getString("description"), rs.getString("creator_username"), rs.getString("level"), rs.getInt("duration"),
                            rs.getInt("report_count"), rs.getBoolean("disability_access"), rs.getString("tags"), rs.getFloat("length"), rs.getInt("likes"));

                    //coordinates punta all array list interno dell oggetto route appena creato
                    coordinates = route.getCoordinates();

                    //aggiungo le coordinate in route
                    coordinates.add(new LatLng(rs.getFloat("latitude"), rs.getFloat("longitude")));

                    //aggiungo route all array list
                    routes.add(route);

                }

                old_route_name = new_route_name;

            }

            return routes;

        } catch (SQLException e) {

            throw new PersistenceException(e.getMessage());
        }finally {
            try {
                if(rs!=null)
                    rs.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something Went Wrong");
            }
        }

    }

    //Modify statement in case recycler view scroll is enabled
    @Override
    public List<Route> getN(int start, int end) throws PersistenceException {

        String query_routes = "SELECT * FROM Routes LIMIT " + start + ", " + end;

        PreparedStatement prepared_statement = null;

        ResultSet rs = null;
        ResultSet rs1 = null;

        List<Route> routes = new ArrayList<>();


        try {
            prepared_statement = connection.prepareStatement(query_routes);

            rs = prepared_statement.executeQuery();

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
        } finally {
            try {
                if(rs!=null)
                    rs.close();
                if(rs1!=null)
                    rs1.close();
                prepared_statement.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something Went Wrong");
            }
        }

    }


    @Override
    public List<Route> getRoutesByLevel(String level) throws PersistenceException {

        List<Route> routes = new ArrayList<>();

        ResultSet rs = null;
        ResultSet rs1 = null;

        try {

            get_routes_by_level_statement.setString(1, level);

            rs = get_routes_by_level_statement.executeQuery();

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
    public void insertReport(Report report) throws PersistenceException {

        String table_name = report.getType().contains("Wrong") ? "WrongInfoReport" : "ObsoleteInfoReport";

        String insert_sql = "INSERT INTO " + table_name + " VALUES (?,?,?,?)";
        String update_report_count_sql = "UPDATE Routes SET report_count=report_count + 1 WHERE name =?";
        PreparedStatement prepared_statement = null;

        try {
            prepared_statement = connection.prepareStatement(insert_sql);

            prepared_statement.setString(1, report.getTitle());
            prepared_statement.setString(2, report.getDescription());
            prepared_statement.setString(3, report.getIssuer());
            prepared_statement.setString(4, report.getRoute_name());
            prepared_statement.execute();

            prepared_statement = connection.prepareStatement(update_report_count_sql);
            prepared_statement.setString(1, report.getRoute_name());
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
    public void insert(Route route) throws PersistenceException {

        NatourS3Bucket natour_bucket = new NatourS3Bucket();

        String query_route = "INSERT INTO Routes (name,creator_username,description,level, duration,report_count,disability_access, tags, length, creation_time)" + "VALUES (?,?,?,?,?,?,?,?,?,?)";

        String query_coordinates = "INSERT INTO Coordinates (latitude, longitude, route_name, seq_num) VALUES (?,?,?,?)";

        String route_name = route.getName();

        PreparedStatement prepared_statement = null;

        try {

            //Insert into route table
            prepared_statement = connection.prepareStatement(query_route);
            prepared_statement.setString(1, route_name);
            prepared_statement.setString(2, route.getCreator_username());
            prepared_statement.setString(3, route.getDescription());
            prepared_statement.setString(4, route.getLevel());
            prepared_statement.setFloat(5, route.getDuration());
            prepared_statement.setInt(6, route.getReport_count());
            prepared_statement.setBoolean(7, route.isDisability_access());
            prepared_statement.setString(8, route.getTags());
            prepared_statement.setFloat(9, route.getLength());
            prepared_statement.setLong(10, Instant.now().toEpochMilli());

            byte decoded_image[] = Base64.getDecoder().decode(route.getImage_base64());

            natour_bucket.putRouteImage(route.getName(), decoded_image);

            prepared_statement.execute();

            //Insert into coordinate table
            prepared_statement = connection.prepareStatement(query_coordinates);

            int count = 0;
            for (LatLng coordinates : route.getCoordinates()) {
                prepared_statement.setFloat(1, coordinates.getLatitude());
                prepared_statement.setFloat(2, coordinates.getLongitude());
                prepared_statement.setString(3, route_name);
                prepared_statement.setInt(4, count++);
                prepared_statement.addBatch();
            }

            prepared_statement.executeBatch();

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
    public void createRoutesCompilation(RoutesCompilation routes_compilation) throws PersistenceException {

        String create = "INSERT INTO RoutesCompilations VALUES (DEFAULT, ?, ?, ?)";

        PreparedStatement prepared_statement = null;

        try {
            prepared_statement = connection.prepareStatement(create);
            prepared_statement.setString(1, routes_compilation.getCreator_username());
            prepared_statement.setString(2, routes_compilation.getTitle());
            prepared_statement.setString(3, routes_compilation.getDescription());

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
    public List<Route> getFilteredRoutes(QueryFilters query_filters) throws PersistenceException {


        String filter_sql = getFilteredSql(query_filters);

        List<Route> routes = new ArrayList<>();

        PreparedStatement prepared_statement = null;

        ResultSet rs = null;
        ResultSet rs1 = null;

        try {

            prepared_statement = connection.prepareStatement(filter_sql);

            rs = prepared_statement.executeQuery();

            if (!query_filters.getRoute_name().equals("")) {

                while (rs.next()) {

                    Route route1 = new Route(rs.getString("name"), rs.getString("description"), rs.getString("creator_username"), rs.getString("level"), rs.getInt("duration"),
                            rs.getInt("report_count"), rs.getBoolean("disability_access"), rs.getString("tags"), rs.getFloat("length"), rs.getInt("likes"));

                    routes.add(route1);
                }

            } else {

                double radius_length = query_filters.getRadius();

                if (query_filters.getCentre_latitude() == 0.0f && query_filters.getCentre_longitude() == 0.0f) {

                    while(rs.next())
                    {
                        String route_name = rs.getString("name");

                        Route route1 = new Route(route_name, rs.getString("description"), rs.getString("creator_username"), rs.getString("level"), rs.getInt("duration"),
                                rs.getInt("report_count"), rs.getBoolean("disability_access"), rs.getString("tags"), rs.getFloat("length"), rs.getInt("likes"));

                        route_coordinates_statement.setString(1, route_name);

                        rs1 = route_coordinates_statement.executeQuery();

                        List<LatLng> coordinates = route1.getCoordinates();

                        while (rs1.next())
                            coordinates.add(new LatLng(rs1.getFloat("latitude"), rs1.getFloat("longitude")));

                        routes.add(route1);

                    }

                }
                else{

                    while (rs.next()) {

                        String route_name = rs.getString("name");

                        LatLng start_point_latlng = null;
                        LatLng end_point_latlng = null;

                        route_coordinates_statement.setString(1, route_name);

                        rs1 = route_coordinates_statement.executeQuery();

                            //Setting start point latitude and longitude
                            if (rs1.first())
                                start_point_latlng = new LatLng(rs1.getFloat("latitude"), rs1.getFloat("longitude"));

                            //Setting end point latitude and longitude
                            if (rs1.last())
                                end_point_latlng = new LatLng(rs1.getFloat("latitude"), rs1.getFloat("longitude"));

                            //Setting centre latitude and longitude
                            LatLng centre_latlng = new LatLng(query_filters.getCentre_latitude(), query_filters.getCentre_longitude());

                            //Distance between start point and centre
                            double distance_between_start_point_and_centre = GeoDistance.haversineDistance(start_point_latlng, centre_latlng);

                            //Distance between end point and centre
                            double distance_between_end_point_and_centre = GeoDistance.haversineDistance(end_point_latlng, centre_latlng);

                            //If start point and end point are beneath radius range then this route can be added
                            if (distance_between_start_point_and_centre <= radius_length && distance_between_end_point_and_centre <= radius_length) {

                                Route route1 = new Route(route_name, rs.getString("description"), rs.getString("creator_username"), rs.getString("level"), rs.getInt("duration"),
                                        rs.getInt("report_count"), rs.getBoolean("disability_access"), rs.getString("tags"), rs.getFloat("length"), rs.getInt("likes"));

                                List<LatLng> coordinates = route1.getCoordinates();
                                //Move back to before first row
                                rs1.beforeFirst();
                                //Cycling on route's coordinates
                                while (rs1.next())
                                    coordinates.add(new LatLng(rs1.getFloat("latitude"), rs1.getFloat("longitude")));

                                routes.add(route1);
                            }

                    }

                }

            }

        } catch (SQLException e) {
            throw new PersistenceException(e.getMessage());
        } catch (NullPointerException e) {
            throw new PersistenceException(e.getMessage());
        }finally{
            try {
                if(rs!=null)
                    rs.close();
                if(rs1!=null)
                    rs1.close();
                prepared_statement.close();
            } catch (SQLException e) {
                throw new PersistenceException("Something went wrong");
            }
        }
        return routes;
    }


    @Override
    public String getFilteredSql(QueryFilters query_filters) {

        String filter_sql = "SELECT * FROM Routes WHERE ";

        String route_name = query_filters.getRoute_name();

        String levels = query_filters.getLevel();

        String tags = query_filters.getTags();

        if(route_name==null||levels==null||tags==null)
            throw new NullPointerException();

        float duration = query_filters.getDuration();

        boolean disability_access = query_filters.isDisableAccessible();

        if (!route_name.equals("")) {

            filter_sql += "name like '%" + query_filters.getRoute_name() + "%'";

            return filter_sql+" ORDER BY creation_time";

        } else {

            if (!levels.equals("")) {

                List<String> levels_as_list = Stream.of(levels.split(";", -1))
                        .collect(Collectors.toList());
                filter_sql += "(";
                for (String lvl : levels_as_list)
                    filter_sql += "level = " + "\"" + lvl + "\"" + " OR ";
                filter_sql = filter_sql.substring(0, filter_sql.length() - 4);
                filter_sql += ") AND ";

            }

            if (duration != 0.0f)
                filter_sql += "duration >= " + Math.abs(duration) + " AND ";

            if (disability_access)
                filter_sql += "disability_access = " + true + " AND ";

            if (!tags.equals("")) {

                List<String> tags_as_list = Stream.of(tags.split(";", -1))
                        .collect(Collectors.toList());
                filter_sql += "(";
                for (String tag : tags_as_list)
                    filter_sql += "tags like '%" + tag + "%' OR ";
                filter_sql = filter_sql.substring(0, filter_sql.length() - 4);
                filter_sql += ")";
                return filter_sql;

            }

        }

        return filter_sql.length()==27 ? "SELECT * FROM Routes ORDER BY creation_time": filter_sql.substring(0, filter_sql.length() - 5);

    }

}