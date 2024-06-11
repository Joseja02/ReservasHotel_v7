package org.iesalandalus.programacion.reservashotel.modelo.negocio.mysql;

import org.iesalandalus.programacion.reservashotel.modelo.dominio.*;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.IHabitaciones;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.mongodb.utilidades.MongoDB;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.mysql.utilidades.MySQL;

import javax.naming.OperationNotSupportedException;
import java.sql.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Habitaciones implements IHabitaciones {
    private static Connection conexion;
    private final String NOMBRE_TABLA = "habitacion";

    public Habitaciones() {
        comenzar();
    }

    public List<Habitacion> get() {
        List<Habitacion> listaHabitaciones = new ArrayList<>();
        String query = "SELECT * FROM " + NOMBRE_TABLA;

        try {
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Habitacion habitacion = MySQL.getHabitacionFromResultSet(rs);
                listaHabitaciones.add(habitacion);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        listaHabitaciones.sort(Comparator.comparing(Habitacion::getIdentificador));
        return listaHabitaciones;
    }

    public List<Habitacion> get(TipoHabitacion tipoHabitacion) {

        if (tipoHabitacion == null){
            throw new NullPointerException("ERROR: El tipo de habitación no puede ser nulo");
        }

        String query = "SELECT * FROM " + NOMBRE_TABLA + " WHERE tipo = ?";
        List<Habitacion> listaHabitaciones = new ArrayList<>();

        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, tipoHabitacion.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Habitacion habitacion = MySQL.getHabitacionFromResultSet(rs);
                    listaHabitaciones.add(habitacion);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        listaHabitaciones.sort(Comparator.comparing(Habitacion::getIdentificador));
        return listaHabitaciones;
    }

    public int getTamano() {
        String query = "SELECT COUNT(*) AS total FROM " + NOMBRE_TABLA;
        try {
            Statement statement = conexion.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void insertar(Habitacion habitacion) throws OperationNotSupportedException {
        if (habitacion == null) {
            throw new NullPointerException("ERROR: No se puede insertar una habitación nula.");
        }

        String tipoHabitacionString = "";
        String query = null;

        if (habitacion instanceof Simple){
            query = "INSERT INTO " + NOMBRE_TABLA + " (planta, puerta, precio) VALUES (?, ?, ?)";
        }
        if (habitacion instanceof Doble){
            query = "INSERT INTO " + NOMBRE_TABLA + " (planta, puerta, precio, camas_individuales, camas_dobles) VALUES (?, ?, ?, ?, ?)";
        }
        if (habitacion instanceof Triple){
            query = "INSERT INTO " + NOMBRE_TABLA + " (planta, puerta, precio, banos, camas_individuales, camas_dobles) VALUES (?, ?, ?, ?, ?, ?)";
        }
        if (habitacion instanceof Suite){
            query = "INSERT INTO " + NOMBRE_TABLA + " (planta, puerta, precio, banos, jacuzzi) VALUES (?, ?, ?, ?, ?)";
        }

        try {
            PreparedStatement ps = MySQL.getPreparedStatement(conexion, query, habitacion);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new OperationNotSupportedException(e.getMessage());
        }
    }

    public Habitacion buscar(Habitacion habitacion) {
        if (habitacion == null) {
            throw new NullPointerException("ERROR: No se puede buscar una habitación nula.");
        }

        String query = "SELECT * FROM " + NOMBRE_TABLA + " WHERE identificador = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, habitacion.getIdentificador());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return MySQL.getHabitacionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void borrar(Habitacion habitacion) throws OperationNotSupportedException {
        if (habitacion == null) {
            throw new NullPointerException("ERROR: No se puede borrar una habitación nula.");
        }

        String query = "DELETE FROM " + NOMBRE_TABLA + " WHERE identificador = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, habitacion.getIdentificador());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new OperationNotSupportedException("ERROR: No existe ninguna habitación como la indicada.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void comenzar(){
        conexion = MySQL.getBD();
    }

    public void terminar(){
        MySQL.cerrarConexion();
        System.out.println("Conexión con MySQL cerrada con éxito.");
    }
}
