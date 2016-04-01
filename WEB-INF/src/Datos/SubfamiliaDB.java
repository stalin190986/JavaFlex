/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Servicios.Conexion;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import Modelos.Subfamilia;


/**
 *
 * @author TIA
 */
public class SubfamiliaDB
{
    private List listaSubfamilia;

    public SubfamiliaDB()
    {
        listaSubfamilia = new ArrayList();
    }//FIN CONSTRUCTOR

    /**
     * Obtiene la lista de subfamilias dado un sector ó una seccion pero no ambas.
     * Tabla: DW_ESTADISTICO_DIM
     * @param sector
     * @param seccion
     * @return la lista de familias con su nombre y código
     * @see Subfamilia
     */
    public List getSubfamilias(
                                String gerenteRegion
                                , String region
                                , String supervisorZonal
                                , String formato
                                , int sucursal
                                , int folioPrincipal
                                , int distribuidor
                                , String sector
                                , int seccion
                                , int familia
                                , String periodo_inicial
                                , String periodo_final
                               )
    {

        Connection c = null;
        /*
        String query;
        String condicion="";

        if(!sector.equalsIgnoreCase("-1"))
            condicion=" where d.sector = '"+sector+"' ";
        if(seccion!=-1)
            condicion=" where cod_seccion = "+seccion+" ";
        if(familia!=-1)
            condicion=" where d.cod_familia = "+familia+" ";


        query = " SELECT distinct cod_subfamilia, nom_subfamilia FROM DW_ESTADISTICO_DIM d "
              +condicion+" order by nom_subfamilia ";
        */


        ArrayList arreglo_filtros_estadisticos = new ArrayList();
                  arreglo_filtros_estadisticos.add(sector);
                  arreglo_filtros_estadisticos.add(seccion);
                  arreglo_filtros_estadisticos.add(familia);
                  arreglo_filtros_estadisticos.add(-1);
                  arreglo_filtros_estadisticos.add(-1);

        String sql_filtros = FiltroEstadisticosDB.obtenerSqlFiltros
                                (
                                    arreglo_filtros_estadisticos
                                    , 4
                                    , gerenteRegion
                                    , region
                                    , supervisorZonal
                                    , formato
                                    , sucursal
                                    , folioPrincipal
                                    , distribuidor
                                    , periodo_inicial
                                    , periodo_final
                                );
        
        String query = " select de.cod_subfamilia, de.nom_subfamilia \n";
        query += " from \n";
        query += " ( \n";
        query += FiltroEstadisticosDB.obtenerSqlGeneral(periodo_inicial, periodo_final);
        query += " ) t1 \n";
        query += " inner join bi_dwh.dw_sucursal_dim ds \n";
        query += " on t1.codigo_sucursal = ds.cod_sucursal \n";
        query += " inner join bi_dwh.dw_proveedor_dim dp \n";
        query += " on t1.codigo_proveedor = dp.id_proveedor \n";
        query += " inner join bi_dwh.dw_estadistico_dim de \n";
        query += " on t1.codigo_estadistico = de.cod_estadistico \n";
        query += sql_filtros;
        query += " group by de.cod_subfamilia, de.nom_subfamilia \n";
        query += " order by 1, 2 asc ";
        


        try
        {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            Subfamilia subfam = new Subfamilia();
            while (rs.next())
            {
                subfam = new Subfamilia();
                subfam.setCodigo(rs.getLong("cod_subfamilia"));
                subfam.setNombre(rs.getString("nom_subfamilia"));
                listaSubfamilia.add(subfam);
            }

            if(listaSubfamilia.size()>1)
            {
                subfam = new Subfamilia();
                subfam.setCodigo(-1);
                subfam.setNombre("TODAS");
                listaSubfamilia.add(0,subfam);
            }

         } catch (SQLException e) {
                System.out.println("Error en query de subfamilias: "+e.toString());
         } finally {
                Conexion.close(c);
         }

       return listaSubfamilia;

    }//FIN getSubfamilias

}// FIN CLASE
