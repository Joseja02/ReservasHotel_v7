package org.iesalandalus.programacion.reservashotel.modelo.negocio.memoria;

import org.iesalandalus.programacion.reservashotel.modelo.dominio.*;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.IReservas;

import javax.naming.OperationNotSupportedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Reservas implements IReservas {
    private List<Reserva> coleccionReservas;

    public Reservas() {
        coleccionReservas = new ArrayList<>();
    }

    public List<Reserva> get() {
        return copiaProfundaReservaes();
    }

    private List<Reserva> copiaProfundaReservaes() {

        List<Reserva> copiaReservas = new ArrayList<>();

        Iterator<Reserva> iterador = coleccionReservas.iterator();
        while (iterador.hasNext()) {
            Reserva reserva = iterador.next();
            copiaReservas.add(new Reserva(reserva));
        }
        return copiaReservas;
    }

    public int getTamano() {
        int counter = 0;
        Iterator<Reserva> iterador = coleccionReservas.iterator();
        while (iterador.hasNext()){
            iterador.next();
            counter++;
        }
        return counter;
    }

    public void insertar(Reserva reserva) throws OperationNotSupportedException {

        if (reserva == null) {
            throw new NullPointerException("ERROR: No se puede insertar una reserva nula.");
        }
        if (coleccionReservas.contains(reserva)) {
            throw new OperationNotSupportedException("ERROR: Ya existe una reserva igual.");
        }
        coleccionReservas.add(reserva);
    }

    public Reserva buscar(Reserva reserva) {

        if (reserva == null) {
            throw new NullPointerException("ERROR: No se puede buscar una reserva nula.");
        }

        if (coleccionReservas.contains(reserva)) {
            int i = coleccionReservas.indexOf(reserva);
            reserva = coleccionReservas.get(i);
            return new Reserva(reserva);
        } else {
            return null;
        }
    }

    public void borrar(Reserva reserva) throws OperationNotSupportedException {
        if (reserva == null) {
            throw new NullPointerException("ERROR: No se puede borrar una reserva nula.");
        }

        if (!coleccionReservas.contains(reserva)) {
            throw new OperationNotSupportedException("ERROR: No existe ninguna reserva como la indicada.");
        }
        coleccionReservas.remove(reserva);
    }

    public List<Reserva> getReservas(Huesped huesped) {
        if (huesped == null) {
            throw new NullPointerException("ERROR: No se pueden buscar reservas de un huésped nulo.");
        }
        List<Reserva> reservasHuesped = new ArrayList<>();
        Iterator<Reserva> iterador = get().iterator();
        while (iterador.hasNext()) {
            Reserva reserva = iterador.next();
            if (reserva.getHuesped().getDni().equals(huesped.getDni())) {
                reservasHuesped.add(new Reserva(reserva));
            }
        }
        return reservasHuesped;
    }

    public List<Reserva> getReservas(TipoHabitacion tipoHabitacion) {
        if (tipoHabitacion == null) {
            throw new NullPointerException("ERROR: No se pueden buscar reservas de un tipo de habitación nula.");
        }
        List<Reserva> reservasHuesped = new ArrayList<>();
        Iterator<Reserva> iterador = get().iterator();
        while (iterador.hasNext()) {
            Reserva reserva = iterador.next();
            if (reserva.getHabitacion() instanceof Simple && tipoHabitacion.equals(TipoHabitacion.SIMPLE)){
                reservasHuesped.add(new Reserva(reserva));
            }
            if (reserva.getHabitacion() instanceof Doble && tipoHabitacion.equals(TipoHabitacion.DOBLE)){
                reservasHuesped.add(new Reserva(reserva));
            }
            if (reserva.getHabitacion() instanceof Triple && tipoHabitacion.equals(TipoHabitacion.TRIPLE)){
                reservasHuesped.add(new Reserva(reserva));
            }
            if (reserva.getHabitacion() instanceof Suite && tipoHabitacion.equals(TipoHabitacion.SUITE)){
                reservasHuesped.add(new Reserva(reserva));
            }
        }
        return reservasHuesped;
    }
    public List<Reserva> getReservas(Habitacion habitacion) {
        if (habitacion == null) {
            throw new NullPointerException("ERROR: No se pueden buscar reservas de una habitación nulo.");
        }
        List<Reserva> reservasHabitacion = new ArrayList<>();
        Iterator<Reserva> iterador = get().iterator();
        while (iterador.hasNext()) {
            Reserva reserva = iterador.next();
            if (reserva.getHabitacion().getIdentificador().equals(habitacion.getIdentificador())) {
                reservasHabitacion.add(new Reserva(reserva));
            }
        }
        return reservasHabitacion;
    }
    public List<Reserva> getReservasFuturas(Habitacion habitacion) {
        if (habitacion == null)
            throw new NullPointerException("ERROR: No se pueden buscar reservas de una habitación nula.");

        List<Reserva> reservasHabitacion = new ArrayList<>();
        Iterator<Reserva> iterador = get().iterator();
        while (iterador.hasNext()){
            Reserva reserva = iterador.next();
            if (reserva.getHabitacion().getIdentificador().equals(habitacion.getIdentificador()) &&
                    reserva.getFechaInicioReserva().isAfter(LocalDate.now())) {
                reservasHabitacion.add(new Reserva(reserva));
            }
        }
        return reservasHabitacion;
    }

    public void realizarCheckin(Reserva reserva, LocalDateTime fecha) {

        if (reserva == null){
            throw new NullPointerException("ERROR: La reserva de un Checkin no puede ser nula.");
        }
        if (fecha == null){
            throw new NullPointerException("ERROR: La fecha de un Checkin no puede ser nula.");
        }
        reserva.setCheckIn(fecha);
        Iterator<Reserva> iterador = coleccionReservas.iterator();
        int i = 0;
        boolean reservaEncontrada = false;
        while (iterador.hasNext() && !reservaEncontrada) {
            Reserva reservaIterator = iterador.next();
            if (reservaIterator.equals(reserva)){
                coleccionReservas.set(i, reserva);
                reservaEncontrada = true;
            }
            i++;
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
            throw new NullPointerException("ERROR: No se puede realizar el Checkout sin haber hecho antes el Checkin.");
        }
        reserva.setCheckOut(fecha);
        Iterator<Reserva> iterador = coleccionReservas.iterator();
        int i = 0;
        boolean reservaEncontrada = false;
        while (iterador.hasNext() && !reservaEncontrada) {
            Reserva reservaIterator = iterador.next();
            if (reservaIterator.equals(reserva)){
                coleccionReservas.set(i, reserva);
                reservaEncontrada = true;
            }
            i++;
        }
    }
    public void comenzar() {

    }
    public void terminar() {

    }
}
