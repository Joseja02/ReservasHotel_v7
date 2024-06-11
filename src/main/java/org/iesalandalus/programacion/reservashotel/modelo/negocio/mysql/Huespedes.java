package org.iesalandalus.programacion.reservashotel.modelo.negocio.mysql;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.iesalandalus.programacion.reservashotel.modelo.dominio.Huesped;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.IHuespedes;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.mongodb.utilidades.MongoDB;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.mysql.utilidades.MySQL;

import javax.naming.OperationNotSupportedException;
import java.sql.*;
import java.util.*;

public class Huespedes implements IHuespedes {
    private static Connection conexion;
    private final String NOMBRE_TABLA = "huesped";

    public Huespedes() {
        comenzar();
    }

    public List<Huesped> get() {
        List<Huesped> listaHuespedes = new ArrayList<>();
        String query = "SELECT * FROM " + NOMBRE_TABLA;

        try {
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Huesped huesped = MySQL.getHuespedFromResultSet(rs);
                listaHuespedes.add(huesped);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        listaHuespedes.sort(Comparator.comparing(Huesped::getDni));
        return listaHuespedes;
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

    public void insertar(Huesped huesped) throws OperationNotSupportedException {
        if (huesped == null) {
            throw new NullPointerException("ERROR: No se puede insertar un huésped nulo.");
        }

        String query = "INSERT INTO " + NOMBRE_TABLA + " (dni, nombre, telefono, correo, fecha_nacimiento) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = MySQL.getPreparedStatement(conexion, query, huesped);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new OperationNotSupportedException("ERROR: No se pudo insertar el huésped.");
        }
    }

    public Huesped buscar(Huesped huesped) {
        if (huesped == null) {
            throw new NullPointerException("ERROR: No se puede buscar un huésped nulo.");
        }

        String query = "SELECT * FROM " + NOMBRE_TABLA + " WHERE dni = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, huesped.getDni());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return MySQL.getHuespedFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void borrar(Huesped huesped) throws OperationNotSupportedException {
        if (huesped == null) {
            throw new NullPointerException("ERROR: No se puede borrar un huésped nulo.");
        }

        String query = "DELETE FROM " + NOMBRE_TABLA + " WHERE dni = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, huesped.getDni());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new OperationNotSupportedException("ERROR: No existe ningún huésped como el indicado.");
            }
        } catch (SQLException e) {
            throw new OperationNotSupportedException("ERROR: No se pudo borrar el huésped.");
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
