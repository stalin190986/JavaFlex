/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Servicios.Conexion;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import Modelos.Seccion;

/**
 *
 * @author TIA
 */
public class SeccionDB
{

    private List listaSecciones;

    public SeccionDB()
    {
        listaSecciones = new ArrayList();
    }//FIN CONSTRUCTOR

    /**
     * Obtiene la lista de secciones dado un sector.
     * Tabla: DW_ESTADISTICO_DIM
     * @param sector
     * @return la lista de secciones con su codigo y nombre
     * @see Seccion
     */
    public List getSecciones( String gerenteRegion
                                , String region
                                , String supervisorZonal
                                , String formato
                                , int sucursal
                                , int folioPrincipal
                                , int distribuidor
                                , String sector
                                , String periodo_inicial
                                , String periodo_final
                             )
    {
        Connection c = null;

        String condicion="";


         ArrayList arreglo_filtros_estadisticos = new ArrayList();
                  arreglo_filtros_estadisticos.add(sector);
                  arreglo_filtros_estadisticos.add(-1);
                  arreglo_filtros_estadisticos.add(-1);
                  arreglo_filtros_estadisticos.add(-1);
                  arreglo_filtros_estadisticos.add(-1);
                  
        String sql_filtros = FiltroEstadisticosDB.obtenerSqlFiltros
                                (
                                    arreglo_filtros_estadisticos
                                    , 2
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
            condicion = "where sector = '"+sector+"' ";

        
            query = "Select distinct cod_seccion, nom_seccion from DW_ESTADISTICO_DIM "
              +condicion+" order by nom_seccion;";


         */
        
        

        /*
         //cambio: 17-11-2011

        seccion = new Seccion();
        seccion.setCodigo(-1);
        seccion.setNombre("TODAS");
        listaSecciones.add(seccion);
        //fin cambio: 17-11-2011
        */

        String query = " select de.cod_seccion, de.nom_seccion \n";
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
        query += " group by de.cod_seccion, de.nom_seccion \n";
        query += " order by 1, 2 asc ";


        try
        {

            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);

            Seccion seccion = new Seccion();
            
            while (rs.next())
            {
                seccion = new Seccion();
                seccion.setCodigo(rs.getInt("cod_seccion"));
                seccion.setNombre(rs.getString("nom_seccion"));
                listaSecciones.add(seccion);
            }

            if(listaSecciones.size()>1)
            {
                 seccion = new Seccion();
                 seccion.setCodigo(-1);
                 seccion.setNombre("TODAS");
                 listaSecciones.add(0,seccion);
            }


         }
         catch (SQLException e)
         {
                System.out.println("Error en query de secciones: "+e.toString());
         }
         finally
         {
                Conexion.close(c);
         }

         return listaSecciones;

    }// FIN getSecciones

} //FIN DE CLASE
