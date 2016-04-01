/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;


import Servicios.Conexion;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import Modelos.Sector;

/**
 *
 * @author STALIN ARROYABE
 */
public class SectorDB
{

    private List listaSectores;

    public SectorDB()
    {
        listaSectores = new ArrayList();
    }

    /**
     * Obtiene la lista de Sectores.
     * Tabla: DW_ESTADISTICO_DIM
     * @return la lista de sectores
     * @see Sector
     */
    public List getSectores( String gerenteRegion
                                , String region
                                , String supervisorZonal
                                , String formato
                                , int sucursal
                                , int folioPrincipal
                                , int distribuidor
                                , String periodo_inicial
                                , String periodo_final
                           )
    {
        
        Connection c = null;

        ArrayList arreglo_filtros_estadisticos = new ArrayList();
                  arreglo_filtros_estadisticos.add("-1");
                  arreglo_filtros_estadisticos.add(-1);
                  arreglo_filtros_estadisticos.add(-1);
                  arreglo_filtros_estadisticos.add(-1);
                  arreglo_filtros_estadisticos.add(-1);
        String sql_filtros = FiltroEstadisticosDB.obtenerSqlFiltros
                                (
                                    arreglo_filtros_estadisticos
                                    , 1
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


        /*
         
        String query =" select distinct sector from DW_ESTADISTICO_DIM "
                        + " ORDER BY SECTOR; ";

        */

       
        String query = " select de.sector \n";
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
        query += " group by sector \n";
        query += " order by 1 asc ";

        
        try
        {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);

            Sector  sector;
            while (rs.next())
            {
               sector = new Sector(rs.getString("sector"));
               sector.setCodigo(rs.getString("sector"));
               listaSectores.add(sector);
            }

            if(listaSectores.size()>1)
            {
                 sector = new Sector();
                 sector.setCodigo("-1");
                 sector.setNombre("TODOS");
                 listaSectores.add(0,sector);
            }

         } 
         catch (SQLException e)
         {
                System.out.println("Error en query de sectores: "+e.toString());
         } finally
         {
                Conexion.close(c);
         }
         return listaSectores;

    }//FIN FUNCION getSectores

}// FIN CLASS SectorDB
