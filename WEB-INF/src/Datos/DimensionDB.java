/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

/**
 *
 * @author STALIN ARROYABE MERCHAN SEPTIEMBRE 2012
 */

import Servicios.Conexion;
import java.sql.*;

public class DimensionDB
{

        /**
         * Obtiene los registro de la dimension dw_existe_maestro_dim.
         * @return un string xml con los elementos de la tabla en mención
         */
        public String getDimension(int tipo_filtro_adicional)
         {

            String xml_elementos = "";
            Connection c = null;
            String query = "";

            switch(tipo_filtro_adicional)
            {
                case 1:
                    query = " SELECT distinct em_codigo_maestro as codigo, em_descrip_maestro as descripcion\n "
                            + " FROM dw_existe_maestro_dim ";
                    break;
                case 2:
                    query = " SELECT distinct cod_tipo_empresa as codigo, desc_tipo_empresa as descripcion \n"
                            + " FROM dw_es_tipo_empresa_dim ";
                    break;
                case 3:
                    query = " SELECT distinct cod_esclusion as codigo, desc_esclusion as descripcion \n"
                            + " FROM dw_es_esclusion_dim ";
                    break;
                case 4:
                    query = " SELECT distinct es_folio_interno as codigo, des_folio_interno as descripcion \n"
                            + " FROM dw_es_tipo_folio_interno_dim ";
                    break;
                case 5:
                    query = " SELECT distinct es_marca_propia as codigo \n";
                    query += "       , if(es_marca_propia = 'S' \n";
                    query += "              , 'SI' \n";
                    query += "              , if(es_marca_propia = 'N' \n";
                    query += "                      , 'NO' \n";
                    query += "                      , es_marca_propia \n";
                    query += "                   ) \n";
                    query += "          ) as descripcion \n";
                    query += " FROM dw_estadistico_dim ";
                    break;
            }

            try
            {
                c = Conexion.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(query);

                xml_elementos = "<padre>\n";
                while (rs.next())
                {
                    xml_elementos += "<hijo codigo = '" + rs.getString("codigo") + "' ";
                    xml_elementos += " descripcion = '" + rs.getString("descripcion")  + "' ";
                    xml_elementos += " seleccionado = '" + this.seleccionadoPorDefecto(tipo_filtro_adicional, rs.getString("codigo")) + "' /> \n";
                }

                xml_elementos += "</padre>\n";

             } catch (SQLException e)
             {
                    System.out.println("Error en query dimension: "+e.toString());
             }
             finally
             {
                    Conexion.close(c);
             }

             return xml_elementos;

        }//Fin getDimension

        private int seleccionadoPorDefecto( int tipo_consulta, String codigo)
        {
            int resultado = 0;

            switch(tipo_consulta)
            {
                case 1:
                       if(codigo.equalsIgnoreCase("1"))
                           resultado = 1;
                       break;

                case 2:
                       if(codigo.equalsIgnoreCase("0") || codigo.equalsIgnoreCase("1") || codigo.equalsIgnoreCase("3"))
                           resultado = 1;
                        break;

                case 3:
                        resultado = 0;
                        break;

                case 4:
                        if(codigo.equalsIgnoreCase("0"))
                            resultado = 1;
                        break;

                case 5:
                        resultado = 0;
                        break;

            }

            return resultado;
             
        }///FIN seleccionadoPorDefecto

}////FIN DE CLASE DimensionDB
