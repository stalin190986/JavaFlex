/**
*
* SIS EXP REQ 2013 002(SAAM 28-01-2013  19-03-2013)
*
* Se agrego la opcion todos al filtro de formato 
* */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Servicios.Conexion;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import Modelos.Formato;

/**
 *
 * @author TIA
 */
public class FormatoDB
{

    private List listaFormatos;

    public FormatoDB()
    {
        listaFormatos = new ArrayList();
    }

    /**
      * Obtiene la lista de formatos excluyendo
      * las bodegas y oficinas.
      * Tabla: DW_SUCURSAL_DIM
      * @return la lista de formatos
      * @see Formato
      */

    public List getFormatos(String subgerente, String region, String supervisor, String periodo_inicial, String periodo_final)
     {
        Connection c = null;
        String query;
        String condicion = "";

        // QUERY DE FORMATOS
        if(!subgerente.equalsIgnoreCase("-1"))
            condicion += " and cod_subgerente = '"+subgerente+"' \n";

        if(!region.equalsIgnoreCase("-1"))
            condicion += " and centro_distribucion = '"+region+"' \n";
        
        if(!supervisor.equalsIgnoreCase("-1"))
            condicion += " and cod_supervisor = '"+supervisor+"' \n";

       /*
        query ="SELECT DISTINCT FORMATO FROM DW_SUCURSAL_DIM "
              +" WHERE FORMATO NOT IN ('CEN-DISTRIB','','OFICINA') "+condicion;
       */


        query = "  select FORMATO \n";
        query += " from \n";
        query += " ( \n";
        query += "      select fc_cod_sucursal \n";
        query += "      from bi_dwh.dw_iproduct_mm_fact2 \n";
        query += "      where fc_fecha >= date_format('" + periodo_inicial + "','%Y-%m-01') \n";
        query += "            and fc_fecha <= date_format('" +  periodo_final + "','%Y-%m-01') \n";
        query += "      group by fc_cod_sucursal \n";
        query += " ) t1    \n";
        query += " inner join bi_dwh.dw_sucursal_dim  \n";
        query += " on fc_cod_sucursal = cod_sucursal  \n";
        query += " where formato NOT IN ('CEN-DISTRIB','','OFICINA') \n";
        query += condicion;
        query += " group by FORMATO \n";
        query += " order by 1 asc  \n";
        
       Formato formato = new Formato();


        try
        {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);


            while (rs.next())
            {
                formato = new Formato(rs.getString("formato"));
                formato.setCodigo(rs.getString("formato"));
                listaFormatos.add(formato);
            }

            ///if(listaFormatos.size()>1) PARA QUE HORACIO GOMEZ PUEDA NAVEGAR POR SUS SUPERVISORES
            if(listaFormatos.size()>0)
            {
                formato = new Formato("TODOS");
                formato.setCodigo("-1");
                listaFormatos.add(0,formato);
            }

          

         } 
         catch (SQLException e)
        {
                System.out.println("Error en query de formato: "+e.toString());
        } 
        finally
        {
                Conexion.close(c);
        }
            return listaFormatos;

    }// FIN getFormatos

}// FIN CLASE
