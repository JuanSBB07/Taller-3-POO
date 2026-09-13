package uniandes.dpoo.aerolinea.modelo.tarifas;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;

public class CalculadoraTarifasTemporadaAlta extends CalculadoraTarifas {
	public static final int COSTO_POR_KM = 1000;
	
	@Override
	protected int calcularCostoBase(Vuelo vuelo, Cliente cluente) {
		int distancia = calcularDistanciaVuelo(vuelo.getRuta());
		return distancia * COSTO_POR_KM;
	}
	
	@Override
	protected double calcularPorcentajeDescuento(Cliente cliente) {
		return 0.0;
	}
}
