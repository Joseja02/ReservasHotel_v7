package org.iesalandalus.programacion.reservashotel.modelo.negocio.memoria;

import org.iesalandalus.programacion.reservashotel.modelo.dominio.*;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.IHabitaciones;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Habitaciones implements IHabitaciones {
    private List<Habitacion> coleccionHabitaciones;

    public Habitaciones() {
        coleccionHabitaciones = new ArrayList<>();
    }

    public List<Habitacion> get() {
        return copiaProfundaHabitaciones();
    }

    public List<Habitacion> get(TipoHabitacion tipoHabitacion) {

        if (tipoHabitacion == null){
            throw new NullPointerException("ERROR: El tipo de habitación no puede ser nulo");
        }
        List<Habitacion> habitacionesTipo = new ArrayList<>();

        Iterator<Habitacion> iterador = coleccionHabitaciones.iterator();
        while (iterador.hasNext()) {
            Habitacion habitacion = iterador.next();
            if (habitacion instanceof Simple && tipoHabitacion.equals(TipoHabitacion.SIMPLE)) {
                habitacionesTipo.add(habitacion);
            } else if (habitacion instanceof Doble && tipoHabitacion.equals(TipoHabitacion.DOBLE)) {
                habitacionesTipo.add(habitacion);
            }else if (habitacion instanceof Triple && tipoHabitacion.equals(TipoHabitacion.TRIPLE)) {
                habitacionesTipo.add(habitacion);
            }else if (habitacion instanceof Suite && tipoHabitacion.equals(TipoHabitacion.SUITE)) {
                habitacionesTipo.add(habitacion);
            }
        }
        return habitacionesTipo;
    }

    private List<Habitacion> copiaProfundaHabitaciones() {

        List<Habitacion> copiaHabitaciones = new ArrayList<>();

        Iterator<Habitacion> iterador = coleccionHabitaciones.iterator();
        while (iterador.hasNext()) {
            Habitacion habitacion = iterador.next();
            if (habitacion instanceof Simple) {
                copiaHabitaciones.add(new Simple (habitacion.getPlanta(), habitacion.getPuerta(), habitacion.getPrecio()));
            } else if (habitacion instanceof Doble) {
                copiaHabitaciones.add(new Doble (habitacion.getPlanta(), habitacion.getPuerta(), habitacion.getPrecio(), ((Doble) habitacion).getNumCamasIndividuales(), ((Doble) habitacion).getNumCamasDobles()));
            } else if (habitacion instanceof Triple) {
                copiaHabitaciones.add(new Triple (habitacion.getPlanta(), habitacion.getPuerta(), habitacion.getPrecio(), ((Triple) habitacion).getNumBanos(), ((Triple) habitacion).getNumCamasIndividuales(), ((Triple) habitacion).getNumCamasDobles()));
            } else if (habitacion instanceof Suite) {
                copiaHabitaciones.add(new Suite (habitacion.getPlanta(), habitacion.getPuerta(), habitacion.getPrecio(), ((Suite) habitacion).getNumBanos(), ((Suite) habitacion).isTieneJacuzzi()));
            }
        }
        return copiaHabitaciones;
    }

    public void insertar(Habitacion habitacion) throws OperationNotSupportedException {

        if (habitacion == null) {
            throw new NullPointerException("ERROR: No se puede insertar una habitación nula.");
        }
        if (coleccionHabitaciones.contains(habitacion)) {
            throw new OperationNotSupportedException("ERROR: Ya existe una habitación con ese identificador.");
        }
        coleccionHabitaciones.add(habitacion);
    }

    public Habitacion buscar(Habitacion habitacion) {

        if (habitacion == null) {
            throw new NullPointerException("ERROR: No se puede buscar una habitación nula.");
        }
        if (coleccionHabitaciones.contains(habitacion)) {
            int i = coleccionHabitaciones.indexOf(habitacion);
            return coleccionHabitaciones.get(i);
        } else {
            return null;
        }
    }

    public void borrar(Habitacion habitacion) throws OperationNotSupportedException {
        if (habitacion == null) {
            throw new NullPointerException("ERROR: No se puede borrar una habitación nula.");
        }

        if (!coleccionHabitaciones.contains(habitacion)) {
            throw new OperationNotSupportedException("ERROR: No existe ninguna habitación como la indicada.");
        }
        coleccionHabitaciones.remove(habitacion);
    }
    public void comenzar() {
    }
    public void terminar() {
    }

    public int getTamano() {
        int counter = 0;
        Iterator<Habitacion> iterador = coleccionHabitaciones.iterator();
        while (iterador.hasNext()){
            iterador.next();
            counter++;
        }
        return counter;
    }



}
