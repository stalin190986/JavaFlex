/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelos;

/**
 *
 * @author user
 */
public class ObjetoFiltroFlex
{
        public String campo_tabla = "";
        public String valor = "";
        public String label = "";
        public String campo_nombre_tabla = "";
        public boolean es_tipo_numerico = true;
        public String alias_campo_tabla = "";
        public String alias_campo_nombre_tabla = "";
        public boolean tiene_campo_nombre = true;
        public boolean es_filtro_dimension = false;
        public int tipo_tabla = 0; /// 0 existe solo dimension	 1 dw_entradas_fact     2  dw_iproduct_mm_fact2		3 ambas tablas
        public String alias_dimension = "";
        public String campo_tabla_entradas = "";
        public boolean es_multiple_checkbox = false;
        public int indice_multiple_checkbox = 0;
        public boolean sumarizado_tiempo = true;

        public ObjetoFiltroFlex(String campo_tabla, String valor)
        {
            this.campo_tabla = campo_tabla;
            this.valor = valor;

        }/// FIN CONSTRUCTOR

}//FIN ObjetoFlex
