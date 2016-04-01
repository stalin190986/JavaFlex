/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Datos;

import java.util.ArrayList;

/**
 *
 * @author user
 */
public class FiltroEstadisticosDB
{


    public static String obtenerSqlFiltros(
                                ArrayList arreglo_filtros_estadisticos
                                , int nivel_estadistico
                                , String gerenteRegion
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
        
            String condicion = "";
            boolean filtro_1er_activo = false;


            if(!gerenteRegion.equalsIgnoreCase("-1"))
            {
                if(!filtro_1er_activo)
                    condicion = " where ";

                condicion += " ds.cod_subgerente = '"+gerenteRegion+"' \n";

                filtro_1er_activo = true;
            }

            if(!region.equalsIgnoreCase("-1"))
            {
                if(filtro_1er_activo)
                    condicion += " and ";
                else
                    condicion = " where ";

                condicion += " ds.centro_distribucion = '" + region + "' \n";
                filtro_1er_activo = true;
            }

            if(!supervisorZonal.equalsIgnoreCase("-1"))
            {
                if(filtro_1er_activo)
                    condicion += " and ";
                else
                    condicion = " where ";

                condicion += " ds.cod_supervisor = '"+supervisorZonal+"' \n";
                filtro_1er_activo = true;
            }

            if(!formato.equalsIgnoreCase("-1"))
            {
                if(filtro_1er_activo)
                    condicion += " and ";
                else
                    condicion = " where ";

                condicion += " ds.formato='"+formato+"' \n";
                filtro_1er_activo = true;
            }

            if(sucursal != -1)
            {
                if(filtro_1er_activo)
                    condicion += " and ";
                else
                    condicion = " where ";

                condicion += " ds.cod_sucursal = " + sucursal + " \n ";
                filtro_1er_activo = true;
            }

            if(folioPrincipal != -1)
            {
                if(filtro_1er_activo)
                    condicion += " and ";
                else
                    condicion = " where ";

                condicion += " dp.folio_principal = " + folioPrincipal + " \n ";
                filtro_1er_activo = true;
            }

            if(distribuidor != -1)
            {
                if(filtro_1er_activo)
                    condicion += " and ";
                else
                    condicion = " where ";

                condicion += " dp.id_proveedor = " + distribuidor + " \n ";
                filtro_1er_activo = true;
            }

            /*
             * sector 0, seccion 1, familia 2, subfamilia 3, estadistico 4
             */
            if(nivel_estadistico > 0)
            {
                //Sector
                String sector = (String)arreglo_filtros_estadisticos.get(0);
                if(!sector.equalsIgnoreCase("-1"))
                {
                    if(filtro_1er_activo)
                        condicion += " and ";
                    else
                        condicion = " where ";

                    condicion += " de.sector='"+ sector +"' \n";
                    filtro_1er_activo = true;
                }
                
                if(nivel_estadistico > 1)
                {
                    //Seccion
                    int seccion = (Integer)arreglo_filtros_estadisticos.get(1);
                    if(seccion!=-1)
                    {
                        if(filtro_1er_activo)
                            condicion += " and ";
                        else
                            condicion = " where ";

                        condicion += " de.cod_seccion="+ seccion +" \n";
                        filtro_1er_activo = true;
                    }
                    if(nivel_estadistico > 2)
                    {
                        //Familia
                        int familia = (Integer)arreglo_filtros_estadisticos.get(2);
                        if(familia!=-1)
                        {
                            if(filtro_1er_activo)
                                condicion += " and ";
                            else
                                condicion = " where ";

                            condicion += " de.cod_familia="+ familia +" \n";
                            filtro_1er_activo = true;
                        }
                        if(nivel_estadistico > 3)
                        {
                            //Subfamilia
                            int subfamilia = (Integer)arreglo_filtros_estadisticos.get(3);
                            if(subfamilia!=-1)
                            {
                                if(filtro_1er_activo)
                                    condicion += " and ";
                                else
                                    condicion = " where ";

                                condicion += " de.cod_subfamilia="+ subfamilia +" \n";
                                filtro_1er_activo = true;
                            }
                            
                            if(nivel_estadistico > 4)
                            {
                                //Estadistico
                                int estadistico = (Integer)arreglo_filtros_estadisticos.get(4);
                                if(estadistico!=-1)
                                {
                                    if(filtro_1er_activo)
                                        condicion += " and ";
                                    else
                                        condicion = " where ";

                                    condicion += " de.cod_estadistico="+ estadistico +" \n";
                                    filtro_1er_activo = true;
                                }
                            }

                        }

                    }



                }
            }

            return condicion;
    }// obtenerSqlFiltros




    
    public static String obtenerSqlGeneral(String periodo_inicial, String periodo_final)
    {
        String query = "";


        query += "      select fc_cod_sucursal        as codigo_sucursal \n";
        query += "           , fc_cod_proveedor     as codigo_proveedor \n";
        query += "           , fc_cod_estadistico   as codigo_estadistico\n";
        query += "       from bi_dwh.dw_iproduct_mm_fact2 \n";
        query += "       where fc_fecha >= date_format('" +  periodo_inicial +"','%Y-%m-01') \n ";
        query += "             and fc_fecha <= date_format('" + periodo_final + "' ,'%Y-%m-01') \n";
        query += "       group by fc_cod_sucursal \n";
        query += "               , fc_cod_proveedor \n";
        query += "               , fc_cod_estadistico \n";
        query += "      UNION ALL \n\n";
        query += "       select cod_sucursal      as codigo_sucursal \n";
        query += "             , cod_proveedor    as codigo_proveedor \n";
        query += "             , cod_estadistico  as codigo_estadistico \n";
        query += "       from bi_dwh.dw_entradas_fact \n";
        query += "       where fecha >= date_format('" + periodo_inicial +"','%Y-%m-01') \n";
        query += "             and fecha <= last_day('" + periodo_final  + "') \n";
        query += "        group by cod_sucursal \n ";
        query += "                , cod_proveedor \n ";
        query += "                , cod_estadistico \n" ;
        

        return query;
    }

}
