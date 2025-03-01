package org.iesalandalus.programacion.reservashotel.modelo.negocio.mysql.utilidades;


import org.iesalandalus.programacion.reservashotel.modelo.dominio.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MySQL {
    public static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATO_DIA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    private static final String SERVIDOR = "localhost";

    private static final int PUERTO = 3306;
    private static final String BD = "reservashotel";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "bd2324";

    public static final String HUESPED = "huesped";
    public static final String NOMBRE = "nombre";
    public static final String DNI = "dni";
    public static final String TELEFONO = "telefono";
    public static final String CORREO = "correo";
    public static final String FECHA_NACIMIENTO = "fecha_nacimiento";
    public static final String DNI_HUESPED = DNI + "_" + HUESPED;
    public static final String HABITACION = "habitacion";
    public static final String IDENTIFICADOR = "identificador";
    public static final String PLANTA = "planta";
    public static final String PUERTA = "puerta";
    public static final String PRECIO = "precio";
    public static final String IDENTIFICADOR_HABITACION = IDENTIFICADOR + "_" + HABITACION;
    public static final String TIPO = "tipo";
    public static final String HABITACION_TIPO = HABITACION + "." + TIPO;
    public static final String TIPO_SIMPLE = "SIMPLE";
    public static final String TIPO_DOBLE = "DOBLE";
    public static final String TIPO_TRIPLE = "TRIPLE";
    public static final String TIPO_SUITE = "SUITE";
    public static final String CAMAS_INDIVIDUALES = "camas_individuales";
    public static final String CAMAS_DOBLE = "camas_dobles";
    public static final String BANOS = "banos";
    public static final String JACUZZI = "jacuzzi";
    public static final String REGIMEN = "regimen";
    public static final String FECHA_INICIO_RESERVA = "fecha_inicio_reserva";
    public static final String FECHA_FIN_RESERVA = "fecha_fin_reserva";
    public static final String CHECKIN = "checkin";
    public static final String CHECKOUT = "checkout";
    public static final String PRECIO_RESERVA = "precio_reserva";
    public static final String NUMERO_PERSONAS = "numero_personas";
    private static Connection conexion;

    private MySQL (){};
    public static Connection getBD(){
        if (conexion == null) {
            establecerConexion();
        }
        return conexion;
    }

    private static void establecerConexion() {
        String conexionString;

        if (!SERVIDOR.equals("localhost")) {
            // Para un servidor remoto, por ejemplo, en AWS
            conexionString = "jdbc:mysql://" + SERVIDOR + ":" + PUERTO + "/" + BD;
        } else {
            // Para un servidor local
            conexionString = "jdbc:mysql://localhost:" + PUERTO + "/" + BD;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(conexionString, USUARIO, CONTRASENA);

            System.out.println("Conexión a base de datos " + SERVIDOR + " ... OK");

        } catch (ClassNotFoundException ex) {
            System.out.println("Error cargando el Driver MySQL JDBC ... FAIL");
        } catch (SQLException ex) {
            System.out.println("Imposible realizar conexion con " + SERVIDOR + " ... FAIL");
        }
    }

    public static void cerrarConexion(){
        try {
            if (conexion != null) {
                conexion.close();
                conexion = null;
                System.out.println("Conexión a MySQL cerrada.");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static PreparedStatement getPreparedStatement(Connection conexion, String query, Huesped huesped) throws SQLException {
        PreparedStatement ps = conexion.prepareStatement(query);
        ps.setString(1, huesped.getDni());
        ps.setString(2, huesped.getNombre());
        ps.setString(3, huesped.getTelefono());
        ps.setString(4, huesped.getCorreo());
        ps.setString(5, huesped.getFechaNacimiento().format(FORMATO_DIA));
        return ps;
    }

    public static Huesped getHuespedFromResultSet(ResultSet rs) throws SQLException {
        String dni = rs.getString(DNI);
        String nombre = rs.getString(NOMBRE);
        String telefono = rs.getString(TELEFONO);
        String correo = rs.getString(CORREO);
        LocalDate fechaNacimiento = LocalDate.parse(rs.getString(FECHA_NACIMIENTO), FORMATO_DIA);
        return new Huesped(nombre, dni, correo, telefono, fechaNacimiento);
    }

    public static PreparedStatement getPreparedStatement(Connection conn, String query, Habitacion habitacion) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, habitacion.getIdentificador());
        ps.setInt(2, habitacion.getPlanta());
        ps.setInt(3, habitacion.getPuerta());
        ps.setDouble(4, habitacion.getPrecio());
        ps.setInt(5, habitacion.getNumeroMaximoPersonas());

        if (habitacion instanceof Simple) {
            ps.setString(6, TipoHabitacion.SIMPLE.name());
        } else if (habitacion instanceof Doble) {
            ps.setString(6, TipoHabitacion.DOBLE.name());
            ps.setInt(7, ((Doble) habitacion).getNumCamasIndividuales());
            ps.setInt(8, ((Doble) habitacion).getNumCamasDobles());
        } else if (habitacion instanceof Triple) {
            ps.setString(6, TipoHabitacion.TRIPLE.name());
            ps.setInt(7, ((Triple) habitacion).getNumCamasIndividuales());
            ps.setInt(8, ((Triple) habitacion).getNumCamasDobles());
            ps.setInt(9, ((Triple) habitacion).getNumBanos());
        } else if (habitacion instanceof Suite) {
            ps.setString(6, TipoHabitacion.SUITE.name());
            ps.setInt(7, ((Suite) habitacion).getNumBanos());
            ps.setBoolean(8, ((Suite) habitacion).isTieneJacuzzi());
        }
        return ps;
    }

    public static Habitacion getHabitacionFromResultSet(ResultSet rs) throws SQLException {
        String tipo = rs.getString(TIPO);
        Habitacion habitacion = null;
        if (tipo.equals(TIPO_SIMPLE)) {
            habitacion = new Simple(rs.getInt(PLANTA), rs.getInt(PUERTA), rs.getDouble(PRECIO));
        } else if (tipo.equals(TIPO_DOBLE)) {
            habitacion = new Doble(rs.getInt(PLANTA), rs.getInt(PUERTA), rs.getDouble(PRECIO), rs.getInt(CAMAS_INDIVIDUALES), rs.getInt(CAMAS_DOBLE));
        } else if (tipo.equals(TIPO_TRIPLE)) {
            habitacion = new Triple(rs.getInt(PLANTA), rs.getInt(PUERTA), rs.getDouble(PRECIO), rs.getInt(BANOS), rs.getInt(CAMAS_INDIVIDUALES), rs.getInt(CAMAS_DOBLE));
        } else if (tipo.equals(TIPO_SUITE)) {
            habitacion = new Suite(rs.getInt(PLANTA), rs.getInt(PUERTA), rs.getDouble(PRECIO), rs.getInt(BANOS), rs.getBoolean(JACUZZI));
        }
        return habitacion;
    }

    public static PreparedStatement getPreparedStatement(Connection conn, String query, Reserva reserva) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, reserva.getHuesped().getDni());
        ps.setString(2, reserva.getHabitacion().getIdentificador());
        ps.setString(3, reserva.getRegimen().name());
        ps.setString(4, reserva.getFechaInicioReserva().format(FORMATO_DIA));
        ps.setString(5, reserva.getFechaFinReserva().format(FORMATO_DIA));

        if (reserva.getCheckIn() != null) {
            ps.setString(6, reserva.getCheckIn().format(FORMATO_DIA_HORA));
        } else {
            ps.setNull(6, Types.VARCHAR);
        }
        if (reserva.getCheckOut() != null) {
            ps.setString(7, reserva.getCheckOut().format(FORMATO_DIA_HORA));
        } else {
            ps.setNull(7, Types.VARCHAR);
        }
        ps.setDouble(8, reserva.getPrecio());
        ps.setInt(9, reserva.getNumeroPersonas());
        return ps;
    }

    public static Reserva getReservaFromResultSet(ResultSet rs) throws SQLException {
        Huesped huesped = leerHuesped(rs.getString(DNI_HUESPED));
        Habitacion habitacion = leerHabitacion(rs.getString(IDENTIFICADOR_HABITACION));
        Regimen regimen = Regimen.valueOf(rs.getString(REGIMEN));
        LocalDate fechaInicioReserva = LocalDate.parse(rs.getString(FECHA_INICIO_RESERVA), FORMATO_DIA);
        LocalDate fechaFinReserva = LocalDate.parse(rs.getString(FECHA_FIN_RESERVA), FORMATO_DIA);
        Reserva reserva = new Reserva(huesped, habitacion, regimen, fechaInicioReserva, fechaFinReserva, rs.getInt(NUMERO_PERSONAS));
        if (rs.getString(CHECKIN) != null) {
            LocalDateTime checkIn = LocalDateTime.parse(rs.getString(CHECKIN), FORMATO_DIA_HORA);
            reserva.setCheckIn(checkIn);
        }
        if (rs.getString(CHECKOUT) != null) {
            reserva.setCheckOut(LocalDateTime.parse(rs.getString(CHECKOUT), FORMATO_DIA_HORA));
        }
        return reserva;
    }

    public static Huesped leerHuesped(String dni) {
        String query = "SELECT * FROM huesped WHERE dni = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getHuespedFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Habitacion leerHabitacion(String identificador) {
        String query = "SELECT * FROM habitacion WHERE identificador = ?";
        try {
            PreparedStatement ps = conexion.prepareStatement(query);
            ps.setString(1, identificador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getHabitacionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}