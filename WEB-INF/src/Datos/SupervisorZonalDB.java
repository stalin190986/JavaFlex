/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Modelos.SupervisorZonal;
import java.util.ArrayList;
import java.util.List;
import Servicios.Conexion;
import java.sql.*;

/**
 *
 * @author TIA
 */
public class SupervisorZonalDB {

    private List listaSupervisores;

    public SupervisorZonalDB(){
        listaSupervisores = new ArrayList();
    }

    /**
     * Obtiene la lista de subgerentes zonales.
     * Tabla: DW_SUCURSAL_DIM
     * @return la lista de subgerentes zonales con su código
     */
    public List getSupervisores(String subgerente, String region, String periodo_inicial, String periodo_final)
     {

        Connection c = null;
        String query;
        String condicion_where = " where ";
        String condicion = "";
        boolean existe_1er_filtro = false;
        String filtrado_padre_anterior = " and ";

                
        if(!subgerente.equalsIgnoreCase("-1"))
        {
            if(!existe_1er_filtro)
                condicion += condicion_where;
            
            condicion += " cod_subgerente = '" + subgerente + "' \n";
            existe_1er_filtro = true;
        }

        
        if(!region.equalsIgnoreCase("-1"))
        {
            if(!existe_1er_filtro)
                condicion += condicion_where;
            else
                condicion += filtrado_padre_anterior;
            
            condicion += " centro_distribucion = '"+region+"' \n";
        }
        
        /*
            query = "SELECT DISTINCT cod_supervisor,supervisor "
              +"FROM dw_sucursal_dim "+condicion;
        */

        query = "  select cod_supervisor, supervisor \n";
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
        query += condicion;
        query += " group by cod_supervisor, supervisor \n";
        query += " order by 1 asc  \n";


        SupervisorZonal supervisor = new SupervisorZonal();

        try {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                supervisor = new SupervisorZonal();
                supervisor.setCodigo(rs.getString("cod_supervisor"));
                supervisor.setNombre(rs.getString("supervisor"));
                listaSupervisores.add(supervisor);
            }

            if(listaSupervisores.size()>1){
                supervisor = new SupervisorZonal();
                supervisor.setNombre("TODOS");
                supervisor.setCodigo("-1");
                listaSupervisores.add(0,supervisor);

            }

         } catch (SQLException e) {
                System.out.println("Error en query de supervisor zonal: "+e.toString());
         } finally {
                Conexion.close(c);
         }
            return listaSupervisores;

    }//Fin getSupervisores

}
