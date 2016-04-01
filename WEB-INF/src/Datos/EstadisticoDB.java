/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Servicios.Conexion;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import Modelos.Estadistico;

/**
 *
 * @author TIA
 */
public class EstadisticoDB {

    private List listaEstadistico;
    
    public EstadisticoDB(){
        listaEstadistico = new ArrayList();
    }

    /**
     * Obtiene la lista de estadisticos dado un sector ó una seccion pero no ambas.
     * Tabla: DW_ESTADISTICO_DIM
     * @param sector
     * @param seccion
     * @return la lista de familias con su nombre y código
     * @see Subfamilia
     */
    public List getEstadisticos(
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
                                , int subfamilia
                                , String periodo_inicial
                                , String periodo_final
                                )
    {
        Connection c = null;
        
        /*
        String query="";

        
         String queryEstadistico;
        String querySucursal;
        String condicionEst="";
        String condicionSucursal="";
        String condicionProveedor="";
        boolean flagEstadistico = false;
        boolean flagSucursal = false;

        
        if(!subgerente.equalsIgnoreCase("-1") ){
            condicionEst = " where cod_subgerente = '"+subgerente+"' ";
            flagEstadistico = true;
        }
        if(!supervisor.equalsIgnoreCase("-1") ){
            condicionEst = " where cod_supervisor = '"+supervisor+"' ";
            flagEstadistico = true;
        }
        if(!sector.equalsIgnoreCase("-1") && !sector.equalsIgnoreCase("-9")){
            condicionEst=" where sector = '"+sector+"'";
            flagEstadistico = true;
        }
        if(seccion!=-1 && seccion!=-9){
            condicionEst=" where cod_seccion = "+seccion+"";
            flagEstadistico = true;
        }
        if(familia!=-1 && familia!=-9){
            condicionEst=" where cod_familia = "+familia+"";
            flagEstadistico = true;
        }
        if(subfamilia!=-1 && subfamilia!=-9){
            condicionEst=" where cod_subfamilia = "+subfamilia+"";
            flagEstadistico = true;
        }
        if(!gerenteRegion.equalsIgnoreCase("-1") ){
            condicionSucursal = " where cod_subgerente = '"+gerenteRegion+"' ";
            flagSucursal = true;
        }
        if(!supervisorZonal.equalsIgnoreCase("-1") ){
            condicionSucursal = " where cod_supervisor = '"+supervisorZonal+"' ";
            flagSucursal = true;
        }
        if(!region.equalsIgnoreCase("-1") ){
            condicionSucursal =" where centro_distribucion = '"+region+"'";
            flagSucursal = true;
        }
        if(!formato.equalsIgnoreCase("-1") ){
            condicionSucursal =" where formato = '"+formato+"' ";
            flagSucursal = true;
        }
        if(sucursal!=-1 ){
            condicionSucursal =" where cod_sucursal = "+sucursal+" ";
            flagSucursal = true;
        }

        if(proveedor!=-1)
            condicionProveedor = "where FC_COD_PROVEEDOR = "+proveedor+" \n";


        if(flagSucursal&&!flagEstadistico){
            //query solo de sucursal
            if(proveedor!=-1)
                condicionProveedor = "where FC_COD_PROVEEDOR = "+proveedor+" and \n";
            else
                condicionProveedor=" where ";

            query = " SELECT T1.COD_ESTADISTICO, E.NOM_PRODUCTO \n"
                + " FROM( \n"
                + " SELECT distinct fc_cod_estadistico COD_ESTADISTICO \n"
                + " FROM dw_iproduct_mm_fact2 \n"
                +condicionProveedor+" \n"
                + " fc_cod_sucursal in ( \n"
                + " select cod_sucursal from dw_sucursal_dim "+condicionSucursal+" ) \n"
                + " )T1 inner join dw_estadistico_dim E ON T1.COD_ESTADISTICO = E.COD_ESTADISTICO;";
        }else if(!flagSucursal&&flagEstadistico){
            //query solo de estadistico
            query = " SELECT T1.COD_ESTADISTICO, T2.NOM_PRODUCTO \n"
                + " FROM( \n"
                + " SELECT distinct fc_cod_estadistico COD_ESTADISTICO \n"
                + " FROM dw_iproduct_mm_fact2 \n"
                +condicionProveedor+" \n"
                + ") T1 inner join ( \n"
                + " select distinct E.cod_estadistico,E.nom_producto \n"
                + " from dw_estadistico_dim E "+condicionEst+")T2 \n"
                + " ON T1.COD_ESTADISTICO = T2.COD_ESTADISTICO ";

        }else if(flagSucursal&&flagEstadistico){
             if(proveedor!=-1)
                condicionProveedor = "where FC_COD_PROVEEDOR = "+proveedor+" and \n";
            else
                condicionProveedor=" where ";

            querySucursal = "SELECT distinct fc_cod_estadistico as cod_estadistico from dw_iproduct_mm_fact2 \n"
                 +condicionProveedor+" fc_cod_sucursal in ( \n"
                 + "  select cod_sucursal from dw_sucursal_dim  "+condicionSucursal+" ) \n";

            queryEstadistico = " SELECT distinct cod_estadistico, nom_producto FROM DW_ESTADISTICO_DIM d \n"
              +condicionEst+"  ";

            query = "SELECT T1.COD_ESTADISTICO, T2.NOM_PRODUCTO \n"
                + " FROM( "+querySucursal+" ) T1 \n"
                + " INNER JOIN ("+queryEstadistico+" )T2 \n"
                + " ON T1.COD_ESTADISTICO = T2.COD_ESTADISTICO\n";

        }else{
            query = " SELECT T1.COD_ESTADISTICO, E.NOM_PRODUCTO \n"
                + " FROM( \n"
                + " SELECT distinct fc_cod_estadistico COD_ESTADISTICO \n"
                + " FROM dw_iproduct_mm_fact2 \n"
                +condicionProveedor+" \n"
                + ") T1 inner join dw_estadistico_dim E ON T1.COD_ESTADISTICO = E.COD_ESTADISTICO ";
        }
        */
        
                ArrayList arreglo_filtros_estadisticos = new ArrayList();
                  arreglo_filtros_estadisticos.add(sector);
                  arreglo_filtros_estadisticos.add(seccion);
                  arreglo_filtros_estadisticos.add(familia);
                  arreglo_filtros_estadisticos.add(subfamilia);
                  arreglo_filtros_estadisticos.add(-1);

        String sql_filtros = FiltroEstadisticosDB.obtenerSqlFiltros
                                (
                                    arreglo_filtros_estadisticos
                                    , 5
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
        
        String query = " select de.cod_estadistico, de.nom_producto \n";
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
        query += " group by de.cod_estadistico, de.nom_producto \n";
        query += " order by 1, 2 asc ";


        try {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            Estadistico estad = new Estadistico();
            while (rs.next())
            {
                estad = new Estadistico();
                estad.setCodigo(rs.getLong("cod_estadistico"));
                estad.setDescripcion(rs.getString("nom_producto"));
                listaEstadistico.add(estad);
            }
            
             if(listaEstadistico.size()>1)
             {
               estad = new Estadistico();
               estad.setCodigo(-1);
               estad.setDescripcion("TODOS");
               listaEstadistico.add(0,estad);
            }
            /*
            estad = new Estadistico();
            estad.setCodigo(-1);
            estad.setDescripcion("TODOS");
            listaEstadistico.add(estad);
            */
         }
        catch (SQLException e)
        {
                System.out.println("Error en query de estadisticos: "+e.toString());
        } 
        finally
        {
                Conexion.close(c);
        }

     return listaEstadistico;

    }

    public String generaJoinEstadistico(String subgerente,String supervisor,
                                           String sector,int seccion,
                                           int familia,long subfamilia,
                                           boolean esJerarquia){
        String join = "";

        if(!subgerente.equalsIgnoreCase("-1") )
            join = " where cod_subgerente = '"+subgerente+"' ";
        if(!supervisor.equalsIgnoreCase("-1") )
            join = " where cod_supervisor = '"+supervisor+"' ";
        if(!sector.equalsIgnoreCase("-1") && !sector.equalsIgnoreCase("-9"))
            join = " where sector = '"+sector+"'";
        if(seccion!=-1 && seccion!=-9)
            join = " where cod_seccion = "+seccion+"";
        if(familia!=-1 && familia!=-9)
            join = " where cod_familia = "+familia+"";

        //No se incluye puesto que se forma datos sin jeraquia
        if(!esJerarquia){
           if(subfamilia!=-1 && subfamilia!=-9)
              join=" where cod_subfamilia = "+subfamilia+"";
        }
        
        return join;
    }


    public String generaJoinEstadisticoSucursal(String subgerente,String supervisor,
                                           String sector,int seccion,
                                           int familia,long subfamilia,long estadistico)
    {
        String join = "";

        if(!subgerente.equalsIgnoreCase("-1") )
            join = " where cod_subgerente = '"+subgerente+"' ";
        if(!supervisor.equalsIgnoreCase("-1") )
            join = " where cod_supervisor = '"+supervisor+"' ";
        if(!sector.equalsIgnoreCase("-1") && !sector.equalsIgnoreCase("-9"))
            join = " where sector = '"+sector+"'";
        if(seccion!=-1 && seccion!=-9)
            join = " where cod_seccion = "+seccion+"";
        if(familia!=-1 && familia!=-9)
            join = " where cod_familia = "+familia+"";
        if(subfamilia!=-1 && subfamilia!=-9)
            join=" where cod_subfamilia = "+subfamilia+"";
        if(estadistico!=-1 && estadistico!=-9)
            join=" where E.cod_estadistico = "+estadistico+"";


        return join;
    }

}
