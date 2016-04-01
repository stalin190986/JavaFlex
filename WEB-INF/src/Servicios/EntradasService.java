/**
*
* SIS EXP REQ 2013 002(SAAM 28-01-2013  19-03-2013)
*
* */
package Servicios;


import Controlador.Excel;
import java.util.ArrayList;
import java.util.List;
import flex.messaging.io.ArrayCollection;



import Modelos.*;
import Datos.*;
import Controlador.Excel;


/**
 *
 * @author STALIN ARROYABE MERCHAN AGOSTO 2012
 */
public class EntradasService
{

    ///-public String ruta_archivos = "/pentaho/server/biserver-ce/tomcat/webapps/CrossDomain/archivos_xml/";
    ///-public String ruta_archivos = "file:///C:/Archivos de programa/Apache Software Foundation/Apache Tomcat 7.0.22/webapps/";
    ///-public String ruta_archivos = "file:///C:/Program Files/Apache Software Foundation/Apache Tomcat 7.0.22/webapps/CrossDomain/archivos_xml/";
    ///public String ruta_archivos = "c:\\Program Files\\Apache Software Foundation\\Apache Tomcat 7.0.22\\webapps\\CrossDomain\\archivos_xml\\";
    public String ruta_archivos = "C:\\biserver-ce-5.4\\biserver-ce\\tomcat\\webapps\\CrossDomain\\archivos_xml\\";
    /**
     * Obtiene la lista de Sectores.
     * @return la lista de sectores
     * @see Sector
     */
    public List getSectores(String gerenteRegion
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
        SectorDB sector = new SectorDB();
         return sector.getSectores(gerenteRegion
                                    , region
                                    , supervisorZonal
                                    , formato
                                    , sucursal
                                    , folioPrincipal
                                    , distribuidor
                                    , periodo_inicial
                                    , periodo_final
                                    );
    }
    /**
     * Obtiene la lista de secciones dado un sector.
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
        SeccionDB seccion = new SeccionDB();
        return seccion.getSecciones(
                                        gerenteRegion
                                        , region
                                        , supervisorZonal
                                        , formato
                                        , sucursal
                                        , folioPrincipal
                                        , distribuidor
                                        , sector
                                        , periodo_inicial
                                        , periodo_final
                                    );
    }
    

    /**
     * Obtiene la lista de familias dado un sector ó una seccion pero no ambas.
     * @param sector
     * @param seccion
     * @return la lista de familias con su nombre y código
     * @see Familia
     */
    public List getFamilias(
                            String gerenteRegion
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
        FamiliaDB fam = new FamiliaDB();
        return fam.getFamilias(
                                gerenteRegion
                                , region
                                , supervisorZonal
                                , formato
                                , sucursal
                                , folioPrincipal
                                , distribuidor
                                , sector
                                , seccion
                                , periodo_inicial
                                , periodo_final
                                );
    }
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
        SubfamiliaDB subFam = new SubfamiliaDB();
        return subFam.getSubfamilias(
                                        gerenteRegion
                                        , region
                                        , supervisorZonal
                                        , formato
                                        , sucursal
                                        , folioPrincipal
                                        , distribuidor
                                        , sector
                                        , seccion
                                        , familia
                                        , periodo_inicial
                                        , periodo_final

                                        );
    }// FIN getSubfamilias


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
        EstadisticoDB estad = new EstadisticoDB();
        return estad.getEstadisticos(
                                        gerenteRegion
                                        , region
                                        , supervisorZonal
                                        , formato
                                        , sucursal
                                        , folioPrincipal
                                        , distribuidor
                                        , sector
                                        , seccion
                                        , familia
                                        , subfamilia
                                        , periodo_inicial
                                        , periodo_final
                                    );
    }

    /**
      * Obtiene la lista de estadisticos dados los filtros de subfamilia y sucursal
      * @return la lista de gerentes regionales
      * @see GerenteRegional
      */


    public List getProveedor(String gerenteRegion
                                , String region
                                , String supervisorZonal
                                , String formato
                                , int sucursal
                                , String periodo_inicial
                                , String periodo_final
                            )
    {

        ProveedorDB proveedor = new ProveedorDB();
        return proveedor.getProveedor(  gerenteRegion
                                        , region
                                        , supervisorZonal
                                        , formato
                                        , sucursal
                                        , periodo_inicial
                                        , periodo_final
                                       );
    }//endfucntion


    public List getDistribuidor(String gerenteRegion
                                , String region
                                , String supervisorZonal
                                , String formato
                                , int sucursal
                                , int folioPrincipal
                                , String periodo_inicial
                                , String periodo_final
                            )
    {

        ProveedorDB proveedor = new ProveedorDB();
        return proveedor.getDistribuidor(  gerenteRegion
                                        , region
                                        , supervisorZonal
                                        , formato
                                        , sucursal
                                        , folioPrincipal
                                        , periodo_inicial
                                        , periodo_final
                                       );
    }//endfucntion

    

    /**
      * Obtiene la lista de gerentes regionales
      * @return la lista de gerentes regionales
      * @see GerenteRegional
      */
    public List getGerenteRegional(String periodo_inicial, String periodo_final)
    {
        GerenteRegionalDB gerente = new GerenteRegionalDB();
        return gerente.getSubgerentes(periodo_inicial, periodo_final);
    }

    /**
      * Obtiene la lista de regiones
      * @return la lista de regiones
      * @see GerenteRegional
      */
    public List getRegion(String subgerente, String periodo_inicial, String periodo_final)
    {
        RegionDB region = new RegionDB();
        return region.getRegion(subgerente, periodo_inicial, periodo_final);
    }
    /**
      * Obtiene la lista de supervisores zonales
      * @return la lista de supervisores zonales
      * @see SupervisorZonal
      */

    public List getSupervisorZonal(String subgerente,String region, String periodo_inicial, String periodo_final)
    {
        SupervisorZonalDB supervisor = new SupervisorZonalDB();
        return supervisor.getSupervisores(subgerente, region, periodo_inicial, periodo_final);
    }

     /**
      * Obtiene la lista de formatos excluyendo
      * las bodegas y oficinas.
      * @return la lista de formatos
      * @see Formato
      */

     public List getFormatos(String subgerente,String region,String supervisor, String periodo_inicial, String periodo_final)
     {
         FormatoDB formato = new FormatoDB();
         return formato.getFormatos(subgerente, region, supervisor, periodo_inicial, periodo_final);
     }
     
     /**
      * Obtiene la lista de sucursales dado un formato ó un surtido pero no ambos.
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
         SucursalDB suc = new SucursalDB();
         return suc.getSucursales(subgerente, region, supervisor, formato, periodo_inicial, periodo_final);

     }

    /**
    * Obtiene la lista de años del modelo de estructura de surtido.
    * @return la lista de años en forma descendente.
    * @see Fecha
    */
    public List getAnios()
    {
        FechaDB anio = new FechaDB();
        return anio.getAnios();

    }// Fin getAnios
    
    /**
     * Obtiene los meses dado un año.
     * @param anio
     * @return la lista de meses. Si el año es 0
     *         obtienes los meses del ultimo año.
     * @see Fecha
     */
    
    public List getMeses(int anio)
    {
        FechaDB meses = new FechaDB();
         return meses.getMeses(anio);
    }//Fin getMeses

    public String generaFecha()
    {
         FechaDB fecha = new FechaDB();
         return fecha.generaFecha();
    }


    public String getFiltrosAdicionales(int tipo_filtro_adicional)
  {

        DimensionDB dimension_db  = new DimensionDB();

        return dimension_db.getDimension(tipo_filtro_adicional);

  }// FIN getFiltrosAdicionales

  public String obtenerDatosReporte(
                                     ArrayCollection arreglo_fechas
                                    , ArrayCollection arreglo_filtros
                                    , ArrayCollection arreglo_filtros_adicionales
                                    , ArrayCollection arreglo_metricas
                                    , ArrayCollection arreglo_niveles_final
                                    , String extraccion_datos
                                    , int es_bi_tia
                                    , int tipo_reporte
                                    , String id_objeto_en_sesion
                                    )
  {


      ReporteDB reporte_db  = new ReporteDB();
      return reporte_db.getReporte(
                                    arreglo_fechas
                                    , arreglo_filtros
                                    , arreglo_filtros_adicionales
                                    , arreglo_metricas
                                    , arreglo_niveles_final
                                    , extraccion_datos
                                    , es_bi_tia
                                    , tipo_reporte
                                    , id_objeto_en_sesion
                                    , this.ruta_archivos
                                    );
  }// FIN obtenerDatosReporte

  
  

   


    /**
     * Genera un archivo de excel despues de procesar un archivo xml
     */
     public String exportarExcel(   String nombre_archivo_xml
                                    , String nombre_columna_ordenada
                                    , boolean ordenamiento_ascendente
                                    , ArrayCollection lista_campos
                                    , ArrayCollection lista_checkbox_sin_seleccionar
                                    , String nombre_archivo_grupo_metricas_no_seleccionadas
                                    , boolean es_detalle_mensual
                                    , boolean existe_nivel_estadistico_comparativo
                                )
     {

       Excel excel = new Excel();

       return excel.exportarExcel( this.ruta_archivos
                                    , nombre_archivo_xml
                                    , nombre_columna_ordenada
                                    , ordenamiento_ascendente
                                    , lista_campos
                                    , lista_checkbox_sin_seleccionar
                                    , nombre_archivo_grupo_metricas_no_seleccionadas
                                    , es_detalle_mensual
                                    , existe_nivel_estadistico_comparativo
                               );


     }//Fin exportarExcel
     
}//Fin Clase