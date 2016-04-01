/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Modelos.GerenteRegional;
import java.util.ArrayList;
import java.util.List;
import Servicios.Conexion;
import java.sql.*;
/**
 *
 * @author TIA
 */
public class GerenteRegionalDB {

    private List listaSubgerentes;

    public GerenteRegionalDB(){
        listaSubgerentes = new ArrayList();
    }

    /**
     * Obtiene la lista de subgerentes zonales.
     * Tabla: DW_SUCURSAL_DIM
     * @return la lista de subgerentes zonales con su código
     */
    public List getSubgerentes(String periodo_inicial, String periodo_final)
     {


        Connection c = null;
        String query;

        /*
        query = "SELECT DISTINCT cod_subgerente,subgerente_zonal "
              +"FROM dw_sucursal_dim ";

        */

        query = "  select cod_subgerente,subgerente_zonal \n";
        query += " from \n";
        query += " ( \n";
        query += "      select fc_cod_sucursal \n";
        query += "      from bi_dwh.dw_iproduct_mm_fact2 \n";
        query += "      where fc_fecha >= date_format('" + periodo_inicial + "','%Y-%m-01') \n";
        query += "            and fc_fecha <= date_format('"+  periodo_final + "','%Y-%m-01') \n";
        query += "      group by fc_cod_sucursal \n";
        query += " ) t1    \n";
        query += " inner join bi_dwh.dw_sucursal_dim  \n";
        query += " on fc_cod_sucursal = cod_sucursal  \n";
        query += " group by cod_subgerente,subgerente_zonal \n";
        query += " order by 1 asc  \n";

        GerenteRegional subgerente = new GerenteRegional();

        try
        {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next())
            {
                subgerente = new GerenteRegional();
                subgerente.setCodigo(rs.getString("cod_subgerente"));
                subgerente.setNombre(rs.getString("subgerente_zonal"));
                listaSubgerentes.add(subgerente);
            }

            if(listaSubgerentes.size()>1)
            {
                subgerente = new GerenteRegional();
                subgerente.setNombre("TODOS");
                subgerente.setCodigo("-1");
                listaSubgerentes.add(0,subgerente);
            }
            
         } catch (SQLException e) {
                System.out.println("Error en query gerente regional: "+e.toString());
         } finally {
                Conexion.close(c);
         }
            return listaSubgerentes;

    }//Fin getSubgerentes

}//FIN CLASE
