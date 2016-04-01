/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelos;

/**
 *
 * @author sarroyabe
 */
public class CampoPeriodoSelect
{
    private String fecha;
    private int tabla;    //// 1 dw_entradas_fact     2 dw_iproduct_mm_fact2
    private String nombre_campo;
    private String mes;
    private String anio;
    private String dia;
    private String metrica;
    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    

    /**
     * @return the nombre_campo
     */
    public String getNombre_campo() {
        return nombre_campo;
    }

    /**
     * @param nombre_campo the nombre_campo to set
     */
    public void setNombre_campo(String nombre_campo) {
        this.nombre_campo = nombre_campo;
    }

    /**
     * @return the mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * @return the anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * @return the dia
     */
    public String getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(String dia) {
        this.dia = dia;
    }

    /**
     * @return the tabla
     */
    public int getTabla() {
        return tabla;
    }

    /**
     * @param tabla the tabla to set
     */
    public void setTabla(int tabla) {
        this.tabla = tabla;
    }

    /**
     * @return the metrica
     */
    public String getMetrica() {
        return metrica;
    }

    /**
     * @param metrica the metrica to set
     */
    public void setMetrica(String metrica) {
        this.metrica = metrica;
    }


}//// CampoPeriodoSelect
