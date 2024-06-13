package org.iesalandalus.programacion.reservashotel.modelo.negocio.mysql;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.iesalandalus.programacion.reservashotel.modelo.dominio.*;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.IReservas;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.mongodb.utilidades.MongoDB;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.mysql.utilidades.MySQL;

import javax.naming.OperationNotSupportedException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Reservas implements IReservas {
    private static Connection conexion;
    private final String NOMBRE_TABLA = "reserva";

    public Reservas() {
        comenzar();
    }

    public List<Reserva> get() {
        List<Reserva> listaReservas = new ArrayList<>();
        String query = "SELECT * FROM " + NOMBRE_TABLA;

        try {
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Reserva reserva = MySQL.getReservaFromResultSet(rs);
                listaReservas.add(reserva);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        listaReservas.sort(Comparator.comparing(Reserva::getFechaInicioReserva));
        return listaReservas;
    }

    public int getTamano() {
        String query = "SELECT COUNT(*) AS total FROM " + NOMBRE_TABLA;
        try {
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void insertar(Reserva reserva) throws OperationNotSupportedException {
        if (reserva == null) {
            throw new NullPointerException("ERROR: No se puede insertar una reserva nula.");
        }

        String query = "INSERT INTO " + NOMBRE_TABLA + " (dni_huesped, identificador_habitacion, regimen, fecha_inicio_reserva, fecha_fin_reserva, checkin, checkout, precio_reserva, numero_personas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = MySQL.getPreparedStatement(conexion, query, reserva);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new OperationNotSupportedException("ERROR: No se pudo insertar la reserva.");
        }
    }

    public Reserva buscar(Reserva reserva) {
        if (reserva == null) {
            throw new NullPointerException("ERROR: No se puede buscar una reserva nula.");
        }

        String query = "SELECT * FROM " + NOMBRE_TABLA + " WHERE fecha_inicio_reserva = ? AND identificador_habitacion = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, reserva.getFechaInicioReserva().toString());
            ps.setString(2, reserva.getHabitacion().getIdentificador());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return MySQL.getReservaFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void borrar(Reserva reserva) throws OperationNotSupportedException {
        if (reserva == null) {
            throw new NullPointerException("ERROR: No se puede borrar una reserva nula.");
        }

        String query = "DELETE FROM " + NOMBRE_TABLA + " WHERE fecha_inicio_reserva = ? AND identificador_habitacion = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, reserva.getFechaInicioReserva().toString());
            ps.setString(2, reserva.getHabitacion().getIdentificador());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new OperationNotSupportedException("ERROR: No existe ningúna reserva como la indicada.");
            }
        } catch (SQLException e) {
            throw new OperationNotSupportedException("ERROR: No se pudo borrar la reserva.");
        }
    }

    public List<Reserva> getReservas(Huesped huesped) {
        if (huesped == null) {
            throw new NullPointerException("ERROR: No se pueden buscar reservas de un huésped nulo.");
        }

        List<Reserva> reservasHuesped = new ArrayList<>();
        String query = "SELECT * FROM " + NOMBRE_TABLA + " WHERE dni_huesped = ?";

        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, huesped.getDni());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reserva reserva = MySQL.getReservaFromResultSet(rs);
                reservasHuesped.add(reserva);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        reservasHuesped.sort(Comparator.comparing(Reserva::getFechaInicioReserva));
        return reservasHuesped;
    }

    public List<Reserva> getReservas(TipoHabitacion tipoHabitacion) {
        if (tipoHabitacion == null) {
            throw new NullPointerException("ERROR: No se pueden buscar reservas de un tipo de habitación nula.");
        }

        List<Reserva> reservasHuesped = new ArrayList<>();
        String query = "SELECT * FROM" + NOMBRE_TABLA + " r " +
                "JOIN habitacion h ON r.identificador_habitacion = h.identificador " +
                "WHERE h.tipo = ?";

        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, tipoHabitacion.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reserva reserva = MySQL.getReservaFromResultSet(rs);
                reservasHuesped.add(reserva);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        reservasHuesped.sort(Comparator.comparing(Reserva::getFechaInicioReserva));
        return reservasHuesped;
    }

    public List<Reserva> getReservas(Habitacion habitacion){

        if (habitacion == null) {
            throw new NullPointerException("ERROR: No se pueden buscar reservas de una habitación nula.");
        }

        List<Reserva> reservasHuesped = new ArrayList<>();
        String query = "SELECT * FROM " + NOMBRE_TABLA + " r " +
                "JOIN habitacion h ON r.identificador_habitacion = h.identificador " +
                "WHERE h.identificador = ?";

        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, habitacion.getIdentificador());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reserva reserva = MySQL.getReservaFromResultSet(rs);
                reservasHuesped.add(reserva);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        reservasHuesped.sort(Comparator.comparing(Reserva::getFechaInicioReserva));
        return reservasHuesped;
    }

    public List<Reserva> getReservasFuturas(Habitacion habitacion) {
        if (habitacion == null)
            throw new NullPointerException("ERROR: No se pueden buscar reservas de una habitación nula.");

        List<Reserva> reservasHuesped = new ArrayList<>();
        Iterator<Reserva> iterador = get().iterator();
        while (iterador.hasNext()){
            Reserva reserva = iterador.next();
            if (reserva.getHabitacion().getIdentificador().equals(habitacion.getIdentificador()) &&
                    reserva.getFechaInicioReserva().isAfter(LocalDate.now())) {
                reservasHuesped.add(new Reserva(reserva));
            }
        }
        return reservasHuesped;
    }

    public void realizarCheckin(Reserva reserva, LocalDateTime fecha) {

        if (reserva == null){
            throw new NullPointerException("ERROR: La reserva de un Checkin no puede ser nula.");
        }
        if (fecha == null){
            throw new NullPointerException("ERROR: La fecha de un Checkin no puede ser nula.");
        }

        String query = "UPDATE " + NOMBRE_TABLA + " SET checkin = ? WHERE identificador_habitacion = ? AND fecha_inicio_reserva = ?";
        reserva.setCheckIn(fecha);
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, reserva.getCheckIn().format(MySQL.FORMATO_DIA_HORA));
            ps.setString(2, reserva.getHabitacion().getIdentificador());
            ps.setString(3, reserva.getFechaInicioReserva().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void realizarCheckout(Reserva reserva, LocalDateTime fecha) {
        if (reserva == null){
            throw new NullPointerException("ERROR: La reserva de un Checkout no puede ser nula.");
        }
        if (fecha == null){
            throw new NullPointerException("ERROR: La fecha de un Checkout no puede ser nula.");
        }
        if (reserva.getCheckIn() == null){
            throw new IllegalArgumentException("ERROR: No se puede realizar el Checkout sin haber hecho antes el Checkin.");
        }

        String query = "UPDATE " + NOMBRE_TABLA + " SET checkout = ? WHERE identificador_habitacion = ? AND fecha_inicio_reserva = ?";
        reserva.setCheckOut(fecha);
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, reserva.getCheckOut().format(MySQL.FORMATO_DIA_HORA));
            ps.setString(2, reserva.getHabitacion().getIdentificador());
            ps.setString(3, reserva.getFechaInicioReserva().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void comenzar(){
        conexion = MySQL.getBD();
    }

    public void terminar(){
        MySQL.cerrarConexion();
    }
}
