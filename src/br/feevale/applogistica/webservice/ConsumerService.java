package br.feevale.applogistica.webservice;

import java.util.HashMap;
import java.util.Map;

public class ConsumerService {

	public static final String URL_DADOS = "https://online.viamarte.com.br/projetoandroid/dadosentrega/";
	
	
	public String buscaDadosEntregas(String id){

	    //Busca produtos do webservice
	    String response = "teste";
		WebService webService = new WebService(URL_DADOS);
		
		Map<String, String> params = new HashMap<String, String>();
	    params.put("id", id ); //String.valueOf(extras.getInt("id"))); 

	    response = webService.webGet("", params);
	    
	    return response;

	}
	
	
}
