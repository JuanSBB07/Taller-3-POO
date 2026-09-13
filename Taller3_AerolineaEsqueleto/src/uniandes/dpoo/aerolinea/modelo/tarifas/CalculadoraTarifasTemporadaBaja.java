package uniandes.dpoo.aerolinea.modelo.tarifas;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.cliente.ClienteCorporativo;

public class CalculadoraTarifasTemporadaBaja extends CalculadoraTarifas {
	public static final int COSTO_POR_KM_NATURAL = 240;
	public static final int COSTO_POR_KM_CORPORATIVO = 300;
	public static final double DESCUENTO_PEQ = 0.02;
	public static final double DESCUENTO_MEDIANAS = 0.1;
	public static final double DESCUENTO_GRANDES = 0.2;
	
	@Override
	protected int calcularCostoBase(Vuelo vuelo, Cliente cliente) {
		int distancia = calcularDistanciaVuelo(vuelo.getRuta());
		if(cliente.getTipoCliente().equals(ClienteCorporativo.CORPORATIVO)) {
			return distancia * COSTO_POR_KM_CORPORATIVO;
		}
		return distancia * COSTO_POR_KM_NATURAL;
	}
	
	@Override
	protected double calcularPorcentajeDescuento(Cliente cliente) {
		if(cliente instanceof ClienteCorporativo) {
			ClienteCorporativo corp = (ClienteCorporativo) cliente;
			int tamano = corp.getTamanoEmpresa();
			if(tamano == ClienteCorporativo.PEQUEÑA) {
				return DESCUENTO_PEQ;
			}
			else if(tamano == ClienteCorporativo.MEDIANA) {
				return DESCUENTO_MEDIANAS;
			}
			else if(tamano == ClienteCorporativo.GRANDE) {
				return DESCUENTO_GRANDES;
			}
		}
		return 0.0;
	}
}
