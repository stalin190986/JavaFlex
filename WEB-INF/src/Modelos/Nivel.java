/**
*
* SIS EXP REQ 2013 002(SAAM 28-01-2013  19-03-2013)
*
* */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelos;

import java.util.ArrayList;

/**
 *
 * @author sarroyabe
 */
public class Nivel
{
        public String nombre_xml;
	public String codigo;
	public String nombre;
	public ArrayList lista_metricas_total;
	public ArrayList lista_metricas_periodo;
	public ArrayList lista_niveles;
        public ArrayList lista_marcas;
        public int indice_nivel;
        public Nivel padre;

        public Nivel()
        {
            this.lista_metricas_periodo = new ArrayList();
            this.lista_metricas_total = new ArrayList();
            this.lista_niveles = new ArrayList();
            this.lista_marcas = new ArrayList();
        }
        
}//// FIN CLASE NIVEL
