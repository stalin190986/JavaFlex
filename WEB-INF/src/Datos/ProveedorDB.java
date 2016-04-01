/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import Modelos.Proveedor;
import Modelos.Distribuidor;
import java.util.ArrayList;
import java.util.List;
import Servicios.Conexion;
import java.sql.*;
/**
 *
 * @author TIA
 */
public class ProveedorDB
{

    private List listaProveedores;

    public ProveedorDB()
    {
        listaProveedores = new ArrayList();

    }// FIN CONSTRUCTOR

    
    public List getProveedor(
                                String gerenteRegion, String region
                                , String supervisorZonal, String formato, int codSucursal
                                , String periodo_inicial
                                , String periodo_final
                             )
    {

        Connection c = null;
        String query = "";
        int bandera = 1;

        boolean flagEstadistico = false;
        boolean flagSucursal = false;

        String condicionEst="";
        String condicionSucursal="";
        
      
/*
        if(!subgerenteCom.equalsIgnoreCase("-1")){
            condicionEst = " where cod_subgerente = '"+subgerenteCom+"' \n";
            flagEstadistico = true;
        }
        if(!supervisorCom.equalsIgnoreCase("-1")){
            condicionEst = " where cod_supervisor = '"+supervisorCom+"' \n";
            flagEstadistico = true;
        }
        if(!sector.equalsIgnoreCase("-1") && !sector.equalsIgnoreCase("-9") ){
            condicionEst=" where sector = '"+sector+"' \n";
            flagEstadistico = true;
        }
        if(codSeccion!=-1 && codSeccion!=-9){
            condicionEst=" where cod_seccion = "+codSeccion+" \n";
            flagEstadistico = true;
        }
        if(familia!=-1 && familia!=-9){
            condicionEst=" where cod_familia = "+familia+" \n";
            flagEstadistico = true;
        }
        if(subfamilia!=-1 && subfamilia!=-9){
            condicionEst=" where cod_subfamilia = "+subfamilia+" \n";
            flagEstadistico = true;
        }
        */
        /*
        if(!gerenteRegion.equalsIgnoreCase("-1")){
            condicionSucursal = " where cod_subgerente = '"+gerenteRegion+"' \n";
            flagSucursal = true;
        }
        if(!supervisorZonal.equalsIgnoreCase("-1")){
            condicionSucursal = " where cod_supervisor = '"+supervisorZonal+"' \n";
            flagSucursal = true;
        }
        if(!region.equalsIgnoreCase("-1")){
            condicionSucursal =" where centro_distribucion = '"+region+"' \n";
            flagSucursal = true;
        }
        if(!formato.equalsIgnoreCase("-1")){
            condicionSucursal =" where formato = '"+formato+"' \n";
            flagSucursal = true;
        }
        if(codSucursal!=-1){
            condicionSucursal =" where cod_sucursal = "+codSucursal+" \n";
            flagSucursal = true;
        }

         */
        /*
        if(flagSucursal&&!flagEstadistico){
            //query solo de sucursal
            query = " select distinct fc_cod_proveedor as folio,PRO.razon_social_prin \n"
                    + " from dw_iproduct_mm_fact2 IP \n"
                    + " inner join dw_proveedor_dim PRO \n"
                    + " ON pro.id_proveedor = IP.fc_cod_proveedor \n"
                    + "  and pro.folio_principal = IP.fc_cod_proveedor \n"
                    + " where fc_cod_sucursal in \n"
                    + " (select cod_sucursal from dw_sucursal_dim "+condicionSucursal+" )\n"
                    + " order by 2 ";
      
        }else if(!flagSucursal&&flagEstadistico){
            //query solo de estadistico
            query = " select distinct T.cod_proveedor as folio,PRO.razon_social_prin \n"
                    + " from \n"
                    + " (select distinct fc_cod_estadistico as cod_estadistico \n"
                    + " ,fc_cod_proveedor as cod_proveedor \n"
                    + " from dw_iproduct_mm_fact2 IP \n"
                    + " inner join dw_estadistico_dim E \n"
                    + " on E.cod_estadistico = IP.fc_cod_estadistico \n"
                    + condicionEst+ " )T \n"
                    + " inner join dw_proveedor_dim PRO \n"
                    + " ON pro.id_proveedor = T.cod_proveedor \n"
                    + " and pro.folio_principal = T.cod_proveedor \n"
                    + " order by 2 ";

        }else if(flagSucursal&&flagEstadistico){
             query = " select distinct T.cod_proveedor as folio,PRO.razon_social_prin \n"
                    + " from \n"
                    + " (select distinct fc_cod_estadistico as cod_estadistico \n"
                    + " ,fc_cod_proveedor as cod_proveedor \n"
                    + " from dw_iproduct_mm_fact2 IP \n"
                    + " inner join dw_estadistico_dim E \n"
                    + " on E.cod_estadistico = IP.fc_cod_estadistico \n"
                    + condicionEst+ "\n"
                    + " and fc_cod_sucursal in \n"
                    + " (select cod_sucursal from dw_sucursal_dim "+condicionSucursal+" )\n"
                    + " )T \n"
                    + " inner join dw_proveedor_dim PRO \n"
                    + " ON pro.id_proveedor = T.cod_proveedor \n"
                    + " and pro.folio_principal = T.cod_proveedor \n"
                    + " order by 2 ";
       
        }else{
          query = " select distinct fc_cod_proveedor as folio,PRO.razon_social_prin \n"
                  + " from dw_iproduct_mm_fact2 IP \n"
                  + " inner join dw_proveedor_dim PRO \n"
                  + " ON pro.id_proveedor = IP.fc_cod_proveedor \n"
                  + " and pro.folio_principal = IP.fc_cod_proveedor \n"
                  + " order by 2 ";
        }
       */

        /*
         
        query = " select folio_principal as folio, razon_social_prin \n";
        query += " from bi_dwh.dw_proveedor_dim \n";
        query += " group by folio_principal, razon_social_prin \n ";
        query += " order by 1, 2 asc \n";

        */


        query = " select dp.folio_principal as folio, dp.razon_social_prin \n";
        query += " from \n";
        query += " ( \n";
        query += FiltroEstadisticosDB.obtenerSqlGeneral(periodo_inicial, periodo_final);
        query += " ) t1 \n";
        query += " inner join bi_dwh.dw_proveedor_dim dp \n";
        query += " on t1.codigo_proveedor = dp.id_proveedor \n";
        query += " group by dp.folio_principal, dp.razon_social_prin \n";
        query += " order by 1 asc ";
       

        /*
        Proveedor prov = new Proveedor();
        prov.setFolioPrincipal(-1);
        prov.setRazonSocialPrin("Todos");
        listaProveedores.add(prov);
        */
        
        Proveedor prov;
        
        try
        {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next())
            {
                prov = new Proveedor();
                prov.setFolioPrincipal(rs.getInt("FOLIO"));
                prov.setRazonSocialPrin(rs.getString("RAZON_SOCIAL_PRIN"));
                listaProveedores.add(prov);

            }//endwhile

            if(listaProveedores.size()>1)
            {
                 prov = new Proveedor();
                 prov.setFolioPrincipal(-1);
                 prov.setRazonSocialPrin("TODOS");
                listaProveedores.add(0,prov);
            }

         }
         catch (SQLException e)
         {
                System.out.println("Error en query: getProveedor - "+e.toString());
         }finally
         {
                Conexion.close(c);
         }
        return listaProveedores;
    }//endfucntion


    public List getDistribuidor(
                                String gerenteRegion, String region
                                , String supervisorZonal
                                , String formato
                                , int codSucursal
                                , int folioPrincipal
                                , String periodo_inicial
                                , String periodo_final
                             )
    {

        Connection c = null;
        String query = "";
        int bandera = 1;

        boolean flagEstadistico = false;
        boolean flagSucursal = false;

        String condicionEst="";
        String condicionSucursal="";
        String condicion="";

/*
        if(!subgerenteCom.equalsIgnoreCase("-1")){
            condicionEst = " where cod_subgerente = '"+subgerenteCom+"' \n";
            flagEstadistico = true;
        }
        if(!supervisorCom.equalsIgnoreCase("-1")){
            condicionEst = " where cod_supervisor = '"+supervisorCom+"' \n";
            flagEstadistico = true;
        }
        if(!sector.equalsIgnoreCase("-1") && !sector.equalsIgnoreCase("-9") ){
            condicionEst=" where sector = '"+sector+"' \n";
            flagEstadistico = true;
        }
        if(codSeccion!=-1 && codSeccion!=-9){
            condicionEst=" where cod_seccion = "+codSeccion+" \n";
            flagEstadistico = true;
        }
        if(familia!=-1 && familia!=-9){
            condicionEst=" where cod_familia = "+familia+" \n";
            flagEstadistico = true;
        }
        if(subfamilia!=-1 && subfamilia!=-9){
            condicionEst=" where cod_subfamilia = "+subfamilia+" \n";
            flagEstadistico = true;
        }
        */
        /*
        if(!gerenteRegion.equalsIgnoreCase("-1")){
            condicionSucursal = " where cod_subgerente = '"+gerenteRegion+"' \n";
            flagSucursal = true;
        }
        if(!supervisorZonal.equalsIgnoreCase("-1")){
            condicionSucursal = " where cod_supervisor = '"+supervisorZonal+"' \n";
            flagSucursal = true;
        }
        if(!region.equalsIgnoreCase("-1")){
            condicionSucursal =" where centro_distribucion = '"+region+"' \n";
            flagSucursal = true;
        }
        if(!formato.equalsIgnoreCase("-1")){
            condicionSucursal =" where formato = '"+formato+"' \n";
            flagSucursal = true;
        }
        if(codSucursal!=-1){
            condicionSucursal =" where cod_sucursal = "+codSucursal+" \n";
            flagSucursal = true;
        }

         */
        /*
        if(flagSucursal&&!flagEstadistico){
            //query solo de sucursal
            query = " select distinct fc_cod_proveedor as folio,PRO.razon_social_prin \n"
                    + " from dw_iproduct_mm_fact2 IP \n"
                    + " inner join dw_proveedor_dim PRO \n"
                    + " ON pro.id_proveedor = IP.fc_cod_proveedor \n"
                    + "  and pro.folio_principal = IP.fc_cod_proveedor \n"
                    + " where fc_cod_sucursal in \n"
                    + " (select cod_sucursal from dw_sucursal_dim "+condicionSucursal+" )\n"
                    + " order by 2 ";

        }else if(!flagSucursal&&flagEstadistico){
            //query solo de estadistico
            query = " select distinct T.cod_proveedor as folio,PRO.razon_social_prin \n"
                    + " from \n"
                    + " (select distinct fc_cod_estadistico as cod_estadistico \n"
                    + " ,fc_cod_proveedor as cod_proveedor \n"
                    + " from dw_iproduct_mm_fact2 IP \n"
                    + " inner join dw_estadistico_dim E \n"
                    + " on E.cod_estadistico = IP.fc_cod_estadistico \n"
                    + condicionEst+ " )T \n"
                    + " inner join dw_proveedor_dim PRO \n"
                    + " ON pro.id_proveedor = T.cod_proveedor \n"
                    + " and pro.folio_principal = T.cod_proveedor \n"
                    + " order by 2 ";

        }else if(flagSucursal&&flagEstadistico){
             query = " select distinct T.cod_proveedor as folio,PRO.razon_social_prin \n"
                    + " from \n"
                    + " (select distinct fc_cod_estadistico as cod_estadistico \n"
                    + " ,fc_cod_proveedor as cod_proveedor \n"
                    + " from dw_iproduct_mm_fact2 IP \n"
                    + " inner join dw_estadistico_dim E \n"
                    + " on E.cod_estadistico = IP.fc_cod_estadistico \n"
                    + condicionEst+ "\n"
                    + " and fc_cod_sucursal in \n"
                    + " (select cod_sucursal from dw_sucursal_dim "+condicionSucursal+" )\n"
                    + " )T \n"
                    + " inner join dw_proveedor_dim PRO \n"
                    + " ON pro.id_proveedor = T.cod_proveedor \n"
                    + " and pro.folio_principal = T.cod_proveedor \n"
                    + " order by 2 ";

        }else{
          query = " select distinct fc_cod_proveedor as folio,PRO.razon_social_prin \n"
                  + " from dw_iproduct_mm_fact2 IP \n"
                  + " inner join dw_proveedor_dim PRO \n"
                  + " ON pro.id_proveedor = IP.fc_cod_proveedor \n"
                  + " and pro.folio_principal = IP.fc_cod_proveedor \n"
                  + " order by 2 ";
        }
       */


        if(folioPrincipal != -1)
            condicion += " where dp.folio_principal=" + folioPrincipal + " \n";


        query = " select dp.id_proveedor as folio, dp.razon_social \n";
        query += " from \n";
        query += " ( \n";
        query += FiltroEstadisticosDB.obtenerSqlGeneral(periodo_inicial, periodo_final);
        query += " ) t1 \n";
        query += " inner join bi_dwh.dw_proveedor_dim dp \n";
        query += " on t1.codigo_proveedor = dp.id_proveedor \n";
        query += condicion;
        query += " group by dp.id_proveedor, dp.razon_social \n";
        query += " order by 1 asc ";

        /*
            query = " select id_proveedor as folio, razon_social \n";
            query += " from bi_dwh.dw_proveedor_dim \n";
            query += condicion;
            query += " group by id_proveedor, razon_social \n ";
            query += " order by 1, 2 asc \n";
        */

        /*
            Distribuidor dis = new Distribuidor();
            dis.setFolio(-1);
            dis.setRazonSocial("Todos");
            listaProveedores.add(dis);
        */
        Distribuidor dis;
        try
        {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            ///-System.out.println("query de filtro proveedores:: " + query);
            while (rs.next())
            {
                dis = new Distribuidor();
                dis.setFolio(rs.getInt("FOLIO"));
                dis.setRazonSocial(rs.getString("RAZON_SOCIAL"));
                listaProveedores.add(dis);

            }//endwhile
            if(listaProveedores.size()>1)
            {
                 dis = new Distribuidor();
                 dis.setFolio(-1);
                 dis.setRazonSocial("TODOS");
                listaProveedores.add(0,dis);
            }

         }
         catch (SQLException e)
         {
                System.out.println("Error en query: getDistribuidor - "+e.toString());
         }finally
         {
                Conexion.close(c);
         }
        return listaProveedores;
    }//endfucntion


}//FIN DE CLASE
