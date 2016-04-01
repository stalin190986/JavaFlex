/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Modelos.Region;
import java.util.ArrayList;
import java.util.List;
import Servicios.Conexion;
import java.sql.*;

/**
 *
 * @author TIA
 */
public class RegionDB {

    private List listaRegion;

    public RegionDB(){
        listaRegion = new ArrayList();
    }

    /**
     * Obtiene la lista de regiones.
     * Tabla: DW_SUCURSAL_DIM
     * @return la lista de subgerentes zonales con su código
     */
    public List getRegion(String subgerente, String periodo_inicial, String periodo_final)
     {

        Connection c = null;
        String query;
        String condicion="";

        if(!subgerente.equalsIgnoreCase("-1") )
            condicion = " and cod_subgerente = '"+subgerente+"' \n";

        /*
        query = "SELECT DISTINCT(CENTRO_DISTRIBUCION) "
            + "FROM dw_sucursal_dim "
            + "WHERE CENTRO_DISTRIBUCION NOT IN ('CD','OFI') "+condicion
            +"ORDER BY CENTRO_DISTRIBUCION";

        */

        query = "  select CENTRO_DISTRIBUCION \n";
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
        query += " where CENTRO_DISTRIBUCION NOT IN ('CD','OFI') \n";
        query += condicion;
        query += " group by CENTRO_DISTRIBUCION \n";
        query += " order by 1 asc  \n";

        Region region = new Region();

        try {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                region = new Region();
                region.setNombre(rs.getString("CENTRO_DISTRIBUCION"));
                region.setCodigo(rs.getString("CENTRO_DISTRIBUCION"));
                listaRegion.add(region);
            }

            if(listaRegion.size()>1)
            {
                region = new Region();
                region.setNombre("TODOS");
                region.setCodigo("-1");
                listaRegion.add(0,region);

            }
            

         } catch (SQLException e) {
                System.out.println("Error en query de region: "+e.toString());
         } finally {
                Conexion.close(c);
         }
            return listaRegion;

    }//Fin getRegion



}
