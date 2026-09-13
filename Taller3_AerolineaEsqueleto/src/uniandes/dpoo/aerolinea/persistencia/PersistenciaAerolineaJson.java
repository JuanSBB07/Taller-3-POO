package uniandes.dpoo.aerolinea.persistencia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.json.JSONArray;
import org.json.JSONObject;

import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteException;
import uniandes.dpoo.aerolinea.exceptions.AeropuertoDuplicadoException;
import uniandes.dpoo.aerolinea.modelo.Aerolinea;
import uniandes.dpoo.aerolinea.modelo.Aeropuerto;
import uniandes.dpoo.aerolinea.modelo.Avion;
import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;

public class PersistenciaAerolineaJson implements IPersistenciaAerolinea {
	private static final String NOMBRE_AEROPUERTO = "nombre";
    private static final String CODIGO_AEROPUERTO = "codigo";
    private static final String NOMBRE_CIUDAD = "nombreCiudad";
    private static final String LATITUD = "latitud";
    private static final String LONGITUD = "longitud";
    private static final String NOMBRE_AVION = "nombre";
    private static final String CAPACIDAD_AVION = "capacidad";
    private static final String CODIGO_RUTA = "codigoRuta";
    private static final String HORA_SALIDA = "horaSalida";
    private static final String HORA_LLEGADA = "horaLlegada";
    private static final String ORIGEN = "origen";
    private static final String DESTINO = "destino";
    private static final String FECHA_VUELO = "fecha";
    private static final String AVION_VUELO = "avion";

    @Override
    public void cargarAerolinea(String archivo, Aerolinea aerolinea) throws IOException, InformacionInconsistenteException {
        String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath( )));
        JSONObject raiz = new JSONObject( jsonCompleto );
        cargarAeropuertos(aerolinea, raiz.getJSONArray("aeropuertos" ));
        cargarAviones(aerolinea, raiz.getJSONArray("aviones" ));
        cargarRutas(aerolinea, raiz.getJSONArray("rutas" ));
        cargarVuelos(aerolinea, raiz.getJSONArray("vuelos" ));
    }

    @Override
    public void salvarAerolinea(String archivo, Aerolinea aerolinea) throws IOException {
        JSONObject raiz = new JSONObject( );
        salvarAeropuertos(aerolinea, raiz);
        salvarAviones(aerolinea, raiz);
        salvarRutas(aerolinea, raiz);
        salvarVuelos(aerolinea, raiz);
        try (PrintWriter pw = new PrintWriter(archivo )) {
            pw.write( raiz.toString( 2 ));
        }
    }

    private void cargarAeropuertos(Aerolinea aerolinea, JSONArray jAeropuertos) throws InformacionInconsistenteException {
        for(int i = 0; i < jAeropuertos.length( ); i++) {
            JSONObject jAeropuerto = jAeropuertos.getJSONObject(i);
            String nombre = jAeropuerto.getString(NOMBRE_AEROPUERTO);
            String codigo = jAeropuerto.getString(CODIGO_AEROPUERTO);
            String ciudad = jAeropuerto.getString(NOMBRE_CIUDAD);
            double latitud = jAeropuerto.getDouble(LATITUD);
            double longitud = jAeropuerto.getDouble(LONGITUD);
            try {
                Aeropuerto aeropuerto = new Aeropuerto(nombre, codigo, ciudad, latitud, longitud);
                aerolinea.agregarAeropuerto(aeropuerto);
            }
            catch (AeropuertoDuplicadoException e) {
                throw new InformacionInconsistenteException("El aeropuerto con código " + codigo + " ya existe o está duplicado.");
            }
        }
    }

    private void cargarAviones(Aerolinea aerolinea, JSONArray jAviones) {
        for(int i = 0; i < jAviones.length( ); i++) {
            JSONObject jAvion = jAviones.getJSONObject (i);
            String nombre = jAvion.getString(NOMBRE_AVION);
            int capacidad = jAvion.getInt(CAPACIDAD_AVION);
            Avion avion = new Avion (nombre, capacidad);
            aerolinea.agregarAvion(avion);
        }
    }

    private void cargarRutas(Aerolinea aerolinea, JSONArray jRutas) throws InformacionInconsistenteException {
        for(int i = 0; i < jRutas.length( ); i++) {
            JSONObject jRuta = jRutas.getJSONObject(i);
            String codigoRuta = jRuta.getString(CODIGO_RUTA);
            String horaSalida = jRuta.getString(HORA_SALIDA);
            String horaLlegada = jRuta.getString(HORA_LLEGADA);
            String codigoOrigen = jRuta.getString(ORIGEN);
            String codigoDestino = jRuta.getString(DESTINO);
            Aeropuerto origen = aerolinea.getAeropuerto(codigoOrigen);
            Aeropuerto destino = aerolinea.getAeropuerto(codigoDestino);
            if(origen == null || destino == null) {
                throw new InformacionInconsistenteException("Los aeropuertos de la ruta " + codigoRuta + " no están registrados.");
            }
            Ruta ruta = new Ruta(origen, destino, horaSalida, horaLlegada, codigoRuta);
            aerolinea.agregarRuta(ruta);
        }
    }

    private void cargarVuelos(Aerolinea aerolinea, JSONArray jVuelos) throws InformacionInconsistenteException {
    	for(int i = 0; i < jVuelos.length( ); i++) {
            JSONObject jVuelo = jVuelos.getJSONObject(i);
            String codigoRuta = jVuelo.getString(CODIGO_RUTA);
            String fecha = jVuelo.getString( FECHA_VUELO);
            String nombreAvion = jVuelo.getString(AVION_VUELO);
            Ruta ruta = aerolinea.getRuta( codigoRuta);
            Avion avion = aerolinea.getAvion( nombreAvion);
            if(ruta == null || avion == null) {
                throw new InformacionInconsistenteException("La ruta o el avión especificado para el vuelo no existe.");
            }
            try {
                aerolinea.programarVuelo( fecha, codigoRuta, nombreAvion);
            }
            catch (Exception e) {
                throw new InformacionInconsistenteException("Error al programar el vuelo: " + e.getMessage( ));
            }
        }
    }

    private void salvarAeropuertos(Aerolinea aerolinea, JSONObject raiz) {
        JSONArray jAeropuertos = new JSONArray( );
        for(Aeropuerto aeropuerto : aerolinea.getAeropuertos( )) {
            JSONObject jAeropuerto = new JSONObject( );
            jAeropuerto.put(NOMBRE_AEROPUERTO, aeropuerto.getNombre( ));
            jAeropuerto.put(CODIGO_AEROPUERTO, aeropuerto.getCodigo( ));
            jAeropuerto.put(NOMBRE_CIUDAD, aeropuerto.getNombreCiudad( ));
            jAeropuerto.put(LATITUD, aeropuerto.getLatitud( ) );
            jAeropuerto.put(LONGITUD, aeropuerto.getLongitud( ) );
            jAeropuertos.put(jAeropuerto);
        }
        raiz.put("aeropuertos", jAeropuertos);
    }

    private void salvarAviones(Aerolinea aerolinea, JSONObject raiz) {
        JSONArray jAviones = new JSONArray( );
        for(Avion avion : aerolinea.getAviones( )) {
            JSONObject jAvion = new JSONObject( );
            jAvion.put(NOMBRE_AVION, avion.getNombre( ));
            jAvion.put(CAPACIDAD_AVION, avion.getCapacidad( ));
            jAviones.put(jAvion );
        }
        raiz.put("aviones", jAviones);
    }

    private void salvarRutas(Aerolinea aerolinea, JSONObject raiz) {
        JSONArray jRutas = new JSONArray( );
        for(Ruta ruta : aerolinea.getRutas( )) {
            JSONObject jRuta = new JSONObject( );
            jRuta.put(CODIGO_RUTA, ruta.getCodigoRuta( ));
            jRuta.put(HORA_SALIDA, ruta.getHoraSalida( ));
            jRuta.put(HORA_LLEGADA, ruta.getHoraLlegada( ));
            jRuta.put(ORIGEN, ruta.getOrigen( ).getCodigo( ));
            jRuta.put(DESTINO, ruta.getDestino( ).getCodigo( ));
            jRutas.put(jRuta);
        }
        raiz.put("rutas", jRutas);
    }

    private void salvarVuelos(Aerolinea aerolinea, JSONObject raiz) {
        JSONArray jVuelos = new JSONArray( );
        for(Vuelo vuelo : aerolinea.getVuelos( )) {
            JSONObject jVuelo = new JSONObject( );
            jVuelo.put(CODIGO_RUTA, vuelo.getRuta( ).getCodigoRuta( ));
            jVuelo.put(FECHA_VUELO, vuelo.getFecha( ));
            jVuelo.put(AVION_VUELO, vuelo.getAvion( ).getNombre( ));
            jVuelos.put(jVuelo);
        }
        raiz.put("vuelos", jVuelos);
    }
}
