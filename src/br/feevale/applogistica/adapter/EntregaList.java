package br.feevale.applogistica.adapter;

import java.util.ArrayList;
import java.util.List;

public class EntregaList {
	private String Fantasia;
	private String Endereco;
	private int Numero;
	private String Bairro;
	private String Cidade;
	private String Cliente;
	private String Melhor_rota;
	private String Dh_maxima;
	private String Dh_entrega;
	private String Dh_sincronismo;
	
	public EntregaList(){
		
	}
	
	public EntregaList(String fantasia, String endereco, int numero,
			String bairro, String cidade, String cliente, String melhor_rota,
			String dh_maxima, String dh_entregue,  String dh_sincronismo) {
		Fantasia = fantasia;
		Endereco = endereco;
		Numero = numero;
		Bairro = bairro;
		Cidade = cidade;
		Cliente = cliente;
		Melhor_rota = melhor_rota;
		Dh_maxima = dh_maxima;
		Dh_entrega = dh_entregue;
		Dh_sincronismo = dh_sincronismo;
	}
	
	public static List<EntregaList> ordenarEntrega(List<EntregaList> listaEntregas){
		List<EntregaList> listaOrdenadas = new ArrayList<EntregaList>();
		List<EntregaList> listaEntAFazer = new ArrayList<EntregaList>();
		List<EntregaList> listaEntFeitas = new ArrayList<EntregaList>();
		
		for(int i = 0; i < listaEntregas.size(); i++){
			if(listaEntregas.get(i).getDh_entrega() != null){
				listaEntFeitas.add(listaEntregas.get(i));
			}
			else{
				listaEntAFazer.add(listaEntregas.get(i));
			}
		}

		for(int i = 0; i < listaEntAFazer.size(); i++){
			listaOrdenadas.add(listaEntAFazer.get(i));
		}

		for(int i = 0; i < listaEntFeitas.size(); i++){
			listaOrdenadas.add(listaEntFeitas.get(i));
		}
		
		return listaOrdenadas;
	}

	public String getFantasia() {
		return Fantasia;
	}
	
	public void setFantasia(String fantasia) {
		Fantasia = fantasia;
	}
	
	public String getEndereco() {
		return Endereco;
	}
	
	public void setEndereco(String endereco) {
		Endereco = endereco;
	}
	
	public int getNumero() {
		return Numero;
	}
	
	public void setNumero(int numero) {
		Numero = numero;
	}
	
	public String getBairro() {
		return Bairro;
	}
	
	public void setBairro(String bairro) {
		Bairro = bairro;
	}
	
	public String getCidade() {
		return Cidade;
	}
	
	public void setCidade(String cidade) {
		Cidade = cidade;
	}
	
	public String getCliente() {
		return Cliente;
	}
	
	public void setCliente(String cliente) {
		Cliente = cliente;
	}
	
	public String getMelhor_rota() {
		return Melhor_rota;
	}
	
	public void setMelhor_rota(String melhor_rota) {
		Melhor_rota = melhor_rota;
	}
	
	public String getDh_maxima() {
		return Dh_maxima;
	}
	
	public void setDh_maxima(String dh_maxima) {
		Dh_maxima = dh_maxima;
	}
	
	public String getDh_sincronismo() {
		return Dh_sincronismo;
	}
	
	public void setDh_sincronismo(String dh_sincronismo) {
		Dh_sincronismo = dh_sincronismo;
	}
	
	public String getDh_entrega(){
		return Dh_entrega;
	}
	
	public void setDh_entrega(String dhEntregue){
		Dh_entrega = dhEntregue;
	}
}
