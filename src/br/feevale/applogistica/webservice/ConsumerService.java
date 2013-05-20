package br.feevale.applogistica.webservice;

import android.annotation.SuppressLint;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("SimpleDateFormat")
public class ConsumerService {

	public static final String URL_DADOS = "https://online.viamarte.com.br/projetoandroid/dadosentrega/";
	public static final String URL_ENTREGA = "http://online.viamarte.com.br/projetoandroid/confirmaentrega/";
	
	public String buscaDadosEntregas(String id){

	    //Busca produtos do webservice
	    String response = "teste";
		WebService webService = new WebService(URL_DADOS);
		
		Map<String, String> params = new HashMap<String, String>();
	    params.put("id", id ); //String.valueOf(extras.getInt("id"))); 

	    response = webService.webGet("", params);
	    
	    return response;

	}
	
	public int registroEntrega(String id) throws JSONException{
		
		String response = "teste";
		WebService webService = new WebService(URL_ENTREGA);
		Format formatter = new SimpleDateFormat("yyyyMMdd HHmm:ss:SS");
		String dhEntrega = formatter.format(Calendar.getInstance().getTime()).replaceAll(" ", "%");
		
		
		Map<String, String> params = new HashMap<String, String>();
	    params.put("id_entrega", id );
	    params.put("dh_entrega", dhEntrega );
		
	    response = webService.webGet("", params);
	    
	    JSONObject job = new JSONObject(response);
	    
	    if(Integer.parseInt(job.get("erro").toString()) != 0){
	    	throw new JSONException("O Webservice não conseguiu concluir a entrega.");
	    }
	    
		return Integer.parseInt(job.get("registros").toString());
	}
	
}
