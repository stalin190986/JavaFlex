/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelos;

/**
 *
 * @author TIA
 */
public class Estadistico {

    private long codigo;
    private String descripcion;
    private String esBasico;
    private String esMarcaPropia;
    private String esOferta;
    private String esExclusion;
    private String nuevoEliminado;

    public Estadistico(){

    }

    /**
     * @return the codigo
     */
    public long getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the esBasico
     */
    public String getEsBasico() {
        return esBasico;
    }

    /**
     * @param esBasico the esBasico to set
     */
    public void setEsBasico(String esBasico) {
        this.esBasico = esBasico;
    }

    /**
     * @return the esMarcaPropia
     */
    public String getEsMarcaPropia() {
        return esMarcaPropia;
    }

    /**
     * @param esMarcaPropia the esMarcaPropia to set
     */
    public void setEsMarcaPropia(String esMarcaPropia) {
        this.esMarcaPropia = esMarcaPropia;
    }

    /**
     * @return the esOferta
     */
    public String getEsOferta() {
        return esOferta;
    }

    /**
     * @param esOferta the esOferta to set
     */
    public void setEsOferta(String esOferta) {
        this.esOferta = esOferta;
    }

    /**
     * @return the esExclusion
     */
    public String getEsExclusion() {
        return esExclusion;
    }

    /**
     * @param esExclusion the esExclusion to set
     */
    public void setEsExclusion(String esExclusion) {
        this.esExclusion = esExclusion;
    }

    /**
     * @return the nuevoEliminado
     */
    public String getNuevoEliminado() {
        return nuevoEliminado;
    }

    /**
     * @param nuevoEliminado the nuevoEliminado to set
     */
    public void setNuevoEliminado(String nuevoEliminado) {
        this.nuevoEliminado = nuevoEliminado;
    }

    


}
