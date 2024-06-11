package org.iesalandalus.programacion.reservashotel.modelo;

import org.iesalandalus.programacion.reservashotel.modelo.negocio.IFuenteDatos;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.fichero.FuenteDatosFichero;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.memoria.FuenteDatosMemoria;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.mongodb.FuenteDatosMongoDB;
import org.iesalandalus.programacion.reservashotel.modelo.negocio.mysql.FuenteDatosMySQL;

public enum FactoriaFuenteDatos {
    MEMORIA{
        public IFuenteDatos crear(){
            return new FuenteDatosMemoria();
        }
    },
    MONGODB{
        public IFuenteDatos crear(){
            return new FuenteDatosMongoDB();
        }
    },
    FICHERO{
        public IFuenteDatos crear() { return new FuenteDatosFichero(); }
    },
    MYSQL{
        public IFuenteDatos crear() { return new FuenteDatosMySQL(); }
    };
    public abstract IFuenteDatos crear();
}
