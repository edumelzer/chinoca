package br.feevale.applogistica.database.orm;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table PRODUTOS.
 */
public class Produtos {

    private Long id;
    private Integer id_entrega;
    private String descricao;
    private String especie;
    private Long valor;
    private String sscc;
    private String dh_leitura;
    private String dh_sincronismo;

    public Produtos() {
    }

    public Produtos(Long id) {
        this.id = id;
    }

    public Produtos(Long id, Integer id_entrega, String descricao, String especie, Long valor, String sscc, String dh_leitura, String dh_sincronismo) {
        this.id = id;
        this.id_entrega = id_entrega;
        this.descricao = descricao;
        this.especie = especie;
        this.valor = valor;
        this.sscc = sscc;
        this.dh_leitura = dh_leitura;
        this.dh_sincronismo = dh_sincronismo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getId_entrega() {
        return id_entrega;
    }

    public void setId_entrega(Integer id_entrega) {
        this.id_entrega = id_entrega;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public Long getValor() {
        return valor;
    }

    public void setValor(Long valor) {
        this.valor = valor;
    }

    public String getSscc() {
        return sscc;
    }

    public void setSscc(String sscc) {
        this.sscc = sscc;
    }

    public String getDh_leitura() {
        return dh_leitura;
    }

    public void setDh_leitura(String dh_leitura) {
        this.dh_leitura = dh_leitura;
    }

    public String getDh_sincronismo() {
        return dh_sincronismo;
    }

    public void setDh_sincronismo(String dh_sincronismo) {
        this.dh_sincronismo = dh_sincronismo;
    }

}
