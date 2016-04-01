/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Servicios.Conexion;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import Modelos.Familia;

/**
 *
 * @author TIA
 */
public class FamiliaDB {

    private List listaFamilia;

    public FamiliaDB()
    {
        listaFamilia = new ArrayList();
    }//FIN CONSTRUCTOR

    /**
     * Obtiene la lista de familias dado un sector ó una seccion pero no ambas.
     * Tabla: DW_ESTADISTICO_DIM
     * @param sector
     * @param seccion
     * @return la lista de familias con su nombre y código
     * @see Familia
     */
    public List getFamilias(String gerenteRegion
                                , String region
                                , String supervisorZonal
                                , String formato
                                , int sucursal
                                , int folioPrincipal
                                , int distribuidor
                                , String sector
                                , int seccion
                                , String periodo_inicial
                                , String periodo_final
                            )
    {

        Connection c = null;
        //String query;
        String condicion="";


        ArrayList arreglo_filtros_estadisticos = new ArrayList();
                  arreglo_filtros_estadisticos.add(sector);
                  arreglo_filtros_estadisticos.add(seccion);
                  arreglo_filtros_estadisticos.add(-1);
                  arreglo_filtros_estadisticos.add(-1);
                  arreglo_filtros_estadisticos.add(-1);

        String sql_filtros = FiltroEstadisticosDB.obtenerSqlFiltros
                                (
                                    arreglo_filtros_estadisticos
                                    , 3
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
         
        if(!sector.equalsIgnoreCase("-1"))
            condicion=" where d.sector = '"+sector+"'";
        if(seccion!=-1)
            condicion=" where d.cod_seccion = "+seccion+"";


        query = " SELECT distinct cod_familia, nom_familia FROM DW_ESTADISTICO_DIM d "
              +condicion+" order by nom_familia ";

         */


        String query = " select de.cod_familia, de.nom_familia \n";
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
        query += " group by de.cod_familia, de.nom_familia \n";
        query += " order by 1, 2 asc ";



        

        /*
        familia = new Familia();
        familia.setCodigo(-1);
        familia.setNombre("TODAS");
        listaFamilia.add(familia);
        */


        try
        {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);

            Familia familia = new Familia();
            while (rs.next())
            {
                familia = new Familia();
                familia.setCodigo(rs.getInt("cod_familia"));
                familia.setNombre(rs.getString("nom_familia"));
                listaFamilia.add(familia);
            }

            if(listaFamilia.size()>1)
            {
                familia = new Familia();
                familia.setCodigo(-1);
                familia.setNombre("TODAS");
                listaFamilia.add(0,familia);
            }

         }
        catch (SQLException e)
        {
                System.out.println("Error en query de familias: "+e.toString());
        }
        finally
        {
                Conexion.close(c);
        }

         return listaFamilia;

    }//FIN getFamilias

}//FIN CLASE
