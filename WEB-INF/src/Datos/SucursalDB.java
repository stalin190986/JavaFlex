/**
*
* SIS EXP REQ 2013 002(SAAM 28-01-2013  19-03-2013)
*
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
import Modelos.Sucursal;

/**
 *
 * @author TIA
 */
public class SucursalDB
{

    private List listaSucursales;

    public SucursalDB(){
        listaSucursales = new ArrayList();
    }

    /**
      * Obtiene la lista de sucursales dado un formato ó un surtido pero no ambos.
      * Tabla: DW_SUCURSAL_DIM
      * @return la lista de sucursales con su nombre y codigo
      * @param formato
      * @param surtido
      * @see Sucursal
      */
     public List getSucursales(String subgerente
                                , String region
                                , String supervisor
                                , String formato
                                , String periodo_inicial
                                , String periodo_final)
     {
        Connection c = null;
        String query;
        String condicion="";


        if(!subgerente.equalsIgnoreCase("-1"))
            condicion += " and cod_subgerente = '"+subgerente+"' \n";
        
        if(!region.equalsIgnoreCase("-1"))
            condicion += " and centro_distribucion = '" + region + "' \n";
        
        if(!supervisor.equalsIgnoreCase("-1"))
            condicion += " and cod_supervisor = '"+supervisor+"' \n";
        
        if(!formato.equalsIgnoreCase("-1"))
            condicion += " and formato='"+formato+"' \n";

        
        /*
        query ="SELECT distinct cod_sucursal, nom_sucursal FROM DW_SUCURSAL_DIM d "
             +" where d.cod_sucursal NOT IN (900,902) "
             +condicion+" order by cod_sucursal; ";
        */

        query = "  select cod_sucursal, nom_sucursal \n";
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
        query += " where cod_sucursal NOT IN (900,902) \n";
        query += condicion;
        query += " group by cod_sucursal, nom_sucursal \n";
        query += " order by 1 asc  \n";


        Sucursal sucursal = new Sucursal();

        try {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);



            while (rs.next()) {
                sucursal = new Sucursal();
                sucursal.setCodigo(rs.getInt("cod_sucursal"));
                sucursal.setNombre(rs.getString("nom_sucursal"));
                listaSucursales.add(sucursal);
            }

            if(listaSucursales.size()>1)
            {
                sucursal = new Sucursal();
                sucursal.setCodigo(-1);
                sucursal.setNombre("TODAS");
                listaSucursales.add(0,sucursal);
            }


         } catch (SQLException e) {
                System.out.println("Error en query de sucursal: "+e.toString());
         } finally {
                Conexion.close(c);
         }
            return listaSucursales;

    }

     public String generaJoinSucursal(String subgerente, String region,
                                 String supervisor,String formato,int sucursal,boolean esLike)
     {
         String join="";

         if(!subgerente.equalsIgnoreCase("-1")){
            join = " and fc_cod_sucursal in "
                    + " (select cod_sucursal from dw_sucursal_dim where cod_subgerente = '"+subgerente+"' ) ";
         }
         if(!region.equalsIgnoreCase("-1")){
            join = " and fc_cod_sucursal in "
                    + " (select cod_sucursal from dw_sucursal_dim where centro_distribucion = '"+region+"' ) ";
         }
         if(!supervisor.equalsIgnoreCase("-1")){
            join = " and fc_cod_sucursal in "
                    + " (select cod_sucursal from dw_sucursal_dim where cod_supervisor = '"+supervisor+"' ) ";
         }
         if(!formato.equalsIgnoreCase("-1")){
            join = " and fc_cod_sucursal in "
                    + " (select cod_sucursal from dw_sucursal_dim where formato='"+formato+"' ) ";
         }
         if(sucursal!=-1){
            join = " and fc_cod_sucursal in "
                    + " (select cod_sucursal from dw_sucursal_dim where cod_sucursal= "+sucursal+" ) ";
         }

         if(esLike)
             join +=" and fc_es_like = 'S' ";
            
         return join;
     }///FIN FIN generaJoinSucursal

}// FIN CLASE
