package uniandes.dpoo.aerolinea.modelo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uniandes.dpoo.aerolinea.exceptions.VueloSobrevendidoException;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.tarifas.CalculadoraTarifas;
import uniandes.dpoo.aerolinea.tiquetes.GeneradorTiquetes;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

public class Vuelo {
	private String fecha;
	private Ruta ruta;
	private Avion avion;
	private Map<String, Tiquete> tiquetes;
	
	public Vuelo(Ruta ruta, String fecha, Avion avion) {
		this.ruta = ruta;
		this.fecha = fecha;
		this.avion = avion;
		this.tiquetes = new HashMap<String, Tiquete>();
	}
	
	public String getFecha() {
		return fecha;
	}
	
	public Ruta getRuta() {
		return ruta;
	}
	
	public Avion getAvion() {
		return avion;
	}
	
	public Collection<Tiquete> getTiquetes() {
		return tiquetes.values();
	}
	
	public int venderTiquetes(Cliente cliente, CalculadoraTarifas calculadora, int cantidad) throws VueloSobrevendidoException {
		if (tiquetes.size() + cantidad > avion.getCapacidad()) {
			throw new VueloSobrevendidoException(this);
		}
		int tarifaUnitario = calculadora.calcularTarifa(this, cliente);
		int costoTotal = 0;
		for(int i = 0; i < cantidad; i++) {
			Tiquete tiquete = GeneradorTiquetes.generarTiquete(this, cliente, tarifaUnitario);
			GeneradorTiquetes.registrarTiquete(tiquete);
			tiquetes.put(tiquete.getCodigo(), tiquete);
			cliente.agregarTiquete(tiquete);
			costoTotal += tarifaUnitario;
		}
		return costoTotal;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Vuelo other = (Vuelo) obj;
		return fecha.equals(other.fecha) && ruta.getCodigoRuta().equals(other.ruta.getCodigoRuta());
	}
}
