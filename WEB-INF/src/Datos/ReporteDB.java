/**
*
* SIS EXP REQ 2013 055(SAAM 28-01-2013  25-03-2013)
* SIS EXP REQ 2013 002(SAAM 28-01-2013  19-03-2013)
*
* */
package Datos;

import Modelos.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import flex.messaging.io.ArrayCollection;
import java.util.HashMap;
import java.util.Iterator;
import Servicios.Conexion;
import java.io.*;
import java.math.*;
import java.util.Date;
import javax.xml.bind.ParseConversionEvent;
import net.sourceforge.sizeof.*;
import java.util.Calendar;
import sun.security.util.BigInt;

/**
 *
 * @author STALIN ARROYABE MERCHAN AGOSTO 2012
 */

public class ReporteDB
{

    private ArrayList lista_fechas;
    private ArrayList lista_filtros;
    private ArrayList lista_filtros_adicionales;
    private ArrayList lista_niveles;
    private ArrayList lista_metricas;
    private Fecha fecha_maxima_diaria;
    private ArrayList lista_periodos_fechas;
    private ArrayList lista_metricas_periodo;

    private ArrayList lista_metricas_comparativas;
    private ArrayList lista_marcas_con_valores_vacios;

    private Marca marca_vacia_tipo;
    private String alias_dimension_estadistico = "de";
    private String alias_dimension_sucursal = "ds";
    private String alias_dimension_proveedor = "dp";
    private int cantidad_campos_select = 0;
    private Nivel nivel_raiz = null;
    private int cantidad_niveles = 0;
    private String separador_periodos = "_";
    private String nombre_metrica_de_ultimo_valor = "stock";
    ////private String ruta_archivos_excel = "/pentaho/server/biserver-ce/tomcat/webapps/CrossDomain/archivos_xml/";
    private String ruta_archivos_excel;
    private String id_objeto_en_sesion;
    private String nombre_tmp_archivo_xml = "xml_reporte_jpivot";
    private String nombre_archivo_xml;

    private int dia_maximo_carga_actual_comparativo = 1;
    private int dia_maximo_carga_anterior_comparativo = 1;


    static final int CODIGO_IPRODUCT_MENSUAL = 2;
    static final int CODIGO_IENTRADAS = 1;
    static final int CODIGO_DIMENSIONAL = 0;
    static final int CODIGO_AMBAS_TABLAS_HECHOS = 3;

    private boolean existe_nivel_estadistico = false;

    public ReporteDB()
    {
        ///LLama al Garbage Collector
        System.gc();
    }

    //Si el mes es el mes actual, el comparativo se realiza con los n dias actuales para el
    //año anterior.
    //-9 en los filtros representa NINGUNO
    //-1 en los filtros representa TODOS

    //List
    public String   getReporte( ArrayCollection arreglo_fechas
                                    , ArrayCollection arreglo_filtros
                                    , ArrayCollection arreglo_filtros_adicionales
                                    , ArrayCollection arreglo_metricas
                                    , ArrayCollection arreglo_niveles_final
                                    , String extraccion_datos
                                    , int es_bi_tia
                                    , int tipo_reporte
                                    , String id_objeto_en_sesion
                                    , String ruta_archivos
                              )
    {
        
        String xml = "";
        ArrayList lista_registros;

        this.id_objeto_en_sesion = id_objeto_en_sesion;
        this.ruta_archivos_excel = ruta_archivos;
        this.existe_nivel_estadistico = false;

        this.lista_fechas = this.obtenerValoresEnviadosFiltrosFlex(arreglo_fechas);
        ////this.imprimirArreglosEnArchivo(this.lista_fechas, "txt_filtros_fechas.txt");

        this.lista_filtros = this.obtenerValoresEnviadosFiltrosFlex(arreglo_filtros);
        ////this.imprimirArreglosEnArchivo(this.lista_filtros, "txt_filtros.txt");

        this.lista_filtros_adicionales = this.obtenerValoresEnviadosFiltrosFlex(arreglo_filtros_adicionales);
        ////this.imprimirArreglosEnArchivo(this.lista_filtros_adicionales, "txt_filtros_adicionales.txt");

        this.lista_metricas = this.obtenerValoresEnviadosFiltrosFlex(arreglo_metricas);
        //// this.imprimirArreglosEnArchivo(this.lista_metricas, "txt_filtros_metricas.txt");

        ///System.out.println("4" + this.id_objeto_en_sesion + " " + new Date());
       
        this.lista_niveles = this.obtenerValoresEnviadosFiltrosFlex(arreglo_niveles_final);
        ////this.imprimirArreglosEnArchivo(this.lista_niveles, "txt_filtros_niveles_final.txt");

        System.out.println("\n\nTermino de generar listas de todos los filtros enviados de flex: " + this.id_objeto_en_sesion + " " + new Date());

        ////  Generar el Arreglo de fechas
            
        this.lista_periodos_fechas = this.generarArregloFechasParaQueryReporte(this.lista_fechas);
        ///System.out.println("Arreglo de Fechas para query " + this.id_objeto_en_sesion + " " + new Date());

        this.fecha_maxima_diaria = this.obtenerFechaMaximaDiaria(this.lista_periodos_fechas);
        ///System.out.println("Obtener maxima fecha diaria " + this.id_objeto_en_sesion + " " + new Date());


            String nombre_ruta_archivo_xml = "";
             String nombre_query = "";
            if(tipo_reporte == 1)
            {
                 System.out.println("Inicia Generacion de Query del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());
                 String query =  this.generarQueryReporteDetalle();

                 /*
                 this.imprimirFiltrosYQuery(this.lista_fechas
                                           , this.lista_filtros
                                           , this.lista_filtros_adicionales
                                           , this.lista_metricas
                                           , this.lista_niveles
                                           );
                 */
                  nombre_query =  "query_dm_" + this.id_objeto_en_sesion + ".sql";
                  this.imprimirStringEnArchivo(query, nombre_query);
                  System.out.println("Se imprimio el query_dm: " + nombre_query);

                  System.out.println("Inicia Ejecucion del Query DM: " + nombre_query + " " + new Date());
                  lista_registros = this.ejecutarQuery(query, true);

                  ///LLama al Garbage Collector
                    System.gc();
                    

                    if(lista_registros != null)
                    {

                            ///StringBuilder xml_reporte = new StringBuilder("<?xml version=\"" + "1.0" + "\" encoding=\"" +  "utf-8" + "\"?>" + "\n");
                            ///StringBuilder xml_reporte_detalle = new StringBuilder(this.generarXmlReporte(lista_registros));
                            ///xml_reporte.append(xml_reporte_detalle);

                            xml = this.generarXmlReporte(lista_registros, true);
                            nombre_ruta_archivo_xml = this.nombre_tmp_archivo_xml + "_" + this.id_objeto_en_sesion;
                            this.nombre_archivo_xml = nombre_ruta_archivo_xml + ".xml";
                            System.out.println("Inicia Impresion de  XML del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());

                                  /////this.imprimirStringEnArchivo(xml.replaceAll("[\n\r]",""), nombre_archivo_xml);
                                       this.imprimirStringEnArchivo(xml, nombre_archivo_xml);
                            System.out.println("Finaliza Impresion de  XML del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());


                            System.out.println("Inicia Impresion de Atributos del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());
                                       this.imprimirStringDeAtributos(this.nivel_raiz, nombre_ruta_archivo_xml + ".dat", true);
                            System.out.println("Finaliza Impresion de Atributos del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());

                          ///Elimina objetos pesados y llama al Garbage Collector
                            lista_registros = null;
                            this.nivel_raiz = null;
                            System.gc();
                    }
                    else
                        nombre_ruta_archivo_xml = "-1";
        }
        else
        {
               this.lista_metricas_comparativas = new ArrayList();
               System.out.println("Inicia Generacion de Query del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());
               String query =  this.generarQueryReporteComparativo();
               nombre_query =  "query_ca_" + this.id_objeto_en_sesion + ".sql";
               this.imprimirStringEnArchivo(query, nombre_query);
               System.out.println("Inicia Ejecucion del Query COM: " + nombre_query + " " + new Date());
               lista_registros = this.ejecutarQuery(query, false);
               if(lista_registros != null)
               {
                        ////System.out.println(lista_registros.size());
                        ///LLama al Garbage Collector
                        System.gc();

                        System.out.println("Inicia Generacion de Lista Niveles del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());
                        int indice_ultimo_nivel = ((Registro)lista_registros.get(0)).lista_atributos.size();
                        Nivel nivel_0 = this.obtenerSubtotalesVerticalesComparativo(lista_registros, indice_ultimo_nivel);
                        System.out.println("Finaliza Generacion de Lista de Niveles del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());

                        System.out.println("Inicia Calculo de Participacion y Evolucion del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());
                        this.generacionParticipacionEvolucion(nivel_0, nivel_0.lista_metricas_periodo, 2);
                        this.generarParticipaciones(nivel_0
                                                , nivel_0.lista_metricas_periodo
                                                , 2
                                                );
                        this.generarEvoluciones(nivel_0
                                                , 3
                                                );
                        
                        System.out.println("Finaliza Calculo de Participacion y Evolucion del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());
                        System.gc();
                        System.out.println("Inicia Generacion del String XML del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());
                        String cadena_xml = "";
                        
                            cadena_xml = generarXmlNivel(nivel_0, indice_ultimo_nivel, false);
                            ////-System.out.println("A COM: " + this.id_objeto_en_sesion + " " + new Date() + "   " + cadena_xml + "\n");
                            cadena_xml += this.generarXmlLista(nivel_0.lista_niveles, indice_ultimo_nivel, false).toString();
                            ///-System.out.println("B COM: " + this.id_objeto_en_sesion + " " + new Date());
                            cadena_xml += "\n</" + "N_" + String.valueOf(nivel_0.indice_nivel) + ">";
                            System.out.println("Finaliza Generacion del String XML del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());
                        
                        nombre_ruta_archivo_xml = this.nombre_tmp_archivo_xml + "_com_" + this.id_objeto_en_sesion;
                        this.nombre_archivo_xml = nombre_ruta_archivo_xml + ".xml";
                        System.out.println("Inicia Impresion de  XML del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());
                            this.imprimirStringEnArchivo(cadena_xml, nombre_archivo_xml);
                        System.out.println("Finaliza Impresion de  XML del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());


                        System.out.println("Inicia Impresion de Atributos del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());
                            this.imprimirStringDeAtributos(this.nivel_raiz, nombre_ruta_archivo_xml + ".dat", false);
                        System.out.println("Finaliza Impresion de Atributos del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());

                        ///Elimina objetos pesados y llama al Garbage Collector
                        lista_registros = null;
                        this.nivel_raiz = null;
                        System.gc();

                }
                else
                    nombre_ruta_archivo_xml = "-1";


        }

      return nombre_ruta_archivo_xml;

      
    }// FIN FIN getReporte

    /**************************************************************************************************
     ******************************REPORTE COMPARATIVO TIPO 2 ****************************************
     *************************************************************************************************/

    private void generacionParticipacionEvolucion(Nivel nivel
                                                    , ArrayList lista_metricas_nivel_total
                                                    , int cantidad_metricas_por_tipo
                                                 )
    {
            
            if(nivel.lista_niveles == null)
            {

            }
            else
            {
                for(int i = 0; i < nivel.lista_niveles.size(); i++)
                {
                    Nivel nivel_actual = (Nivel) nivel.lista_niveles.get(i);
                    this.generarParticipaciones(nivel_actual
                                                , lista_metricas_nivel_total
                                                , cantidad_metricas_por_tipo
                                                );
                    this.generarEvoluciones(
                                                nivel_actual
                                                , cantidad_metricas_por_tipo + 1
                                            );
                    this.generacionParticipacionEvolucion(nivel_actual
                                                            , lista_metricas_nivel_total
                                                            , cantidad_metricas_por_tipo
                                                         );
                }
            }
            
    }////FIN FIN generacionParticipacionEvolucion

    private void generarEvoluciones(Nivel nivel_actual
                                    , int cantidad_metricas_por_tipo)
    {
            ArrayList arreglo_evolucion = new ArrayList();
            for(int i = 0; i < nivel_actual.lista_metricas_periodo.size() ; i = i + cantidad_metricas_por_tipo)
            {

                    Metrica metrica_actual = (Metrica) nivel_actual.lista_metricas_periodo.get(i + 1);
                    Metrica metrica_anterior = (Metrica) nivel_actual.lista_metricas_periodo.get(i + 2);
                    arreglo_evolucion.add(this.generarMetricaEvolucion(metrica_actual, metrica_anterior));

            }
            this.insertarEvolucion(nivel_actual, arreglo_evolucion, cantidad_metricas_por_tipo);

    }////FIN FIN generarEvoluciones
    
   private void insertarEvolucion(Nivel nivel
                                            , ArrayList arreglo_evoluciones
                                            , int cantidad_metricas_por_tipo
                                        )
    {
        
            ///int cantidad_inicial_metricas = nivel.lista_metricas_periodo.size();
            int indice_ultimo_insercion = 0;
            for(int i = 0; i < arreglo_evoluciones.size(); i++)
            {
                    indice_ultimo_insercion = ((i + 1) * cantidad_metricas_por_tipo) + i;
                    Metrica metrica_evo = (Metrica) arreglo_evoluciones.get(i);
                    nivel.lista_metricas_periodo.add(indice_ultimo_insercion
                                                    , metrica_evo
                                                    );
                    
            }
            
    }///FIN FIN insertarEvolucion



    private Metrica generarMetricaEvolucion(Metrica metrica_actual, Metrica metrica_anterior)
    {
            Metrica metrica_evo = new Metrica();

            metrica_evo.nombre_compuesto = "evo_" + this.nombreResumidoMetricasCalculado(metrica_actual.nombre_compuesto);
            metrica_evo.nombre_metrica = "evo_" + metrica_actual.nombre_metrica;
            metrica_evo.nombre_periodo = "evo_" + metrica_actual.nombre_periodo;
            metrica_evo.nombre_periodo_completo = "evo_" + metrica_actual.nombre_periodo_completo;
            if(metrica_anterior.valor != 0.00)
            {
                
                    metrica_evo.valor = ( (metrica_actual.valor / metrica_anterior.valor) - 1 ) * 100;

            }
            else
                metrica_evo.valor = 0.00;

            
            return metrica_evo;
            
    }///FIN FIN generarMetricaParticipacion

    private void generarParticipaciones( Nivel nivel_actual
                                         , ArrayList lista_metricas_nivel_total
                                         , int cantidad_metricas_por_tipo
                                        )
    {
            ArrayList arreglo_participacion = new ArrayList();
            
            for(int j = 0; j < lista_metricas_nivel_total.size(); j = j + cantidad_metricas_por_tipo)
            {
                        Metrica metrica_nivel = (Metrica) nivel_actual.lista_metricas_periodo.get(j);
                        Metrica metrica_total = (Metrica) lista_metricas_nivel_total.get(j);
                        arreglo_participacion.add(this.generarMetricaParticipacion(metrica_nivel, metrica_total));

            }
            this.insertarParticipaciones(nivel_actual, arreglo_participacion, cantidad_metricas_por_tipo);
    }////FIN FIN generarParticipaciones

    

    private Metrica generarMetricaParticipacion(Metrica metrica_hija, Metrica metrica_total)
    {
        Metrica metrica_pa = new Metrica();
        
        ////metrica_pa.nombre_compuesto = "p_" + metrica_hija.nombre_compuesto;
        metrica_pa.nombre_compuesto = "p_" + this.nombreResumidoMetricasCalculado(metrica_hija.nombre_compuesto);
        metrica_pa.nombre_metrica = "p_" + metrica_hija.nombre_metrica;
	metrica_pa.nombre_periodo = "p_" + metrica_hija.nombre_periodo;
	metrica_pa.nombre_periodo_completo = "p_" + metrica_hija.nombre_periodo_completo;
        if(metrica_total.valor != 0.00)
            metrica_pa.valor = (metrica_hija.valor * 100 / metrica_total.valor);
        else
            metrica_pa.valor = 0.00;
                    
        return metrica_pa;
    }///FIN generarParticipacion

    
    private void insertarParticipaciones(Nivel nivel
                                            , ArrayList arreglo_participaciones
                                            , int cantidad_metricas_por_tipo
                                        )
    {
        
            ///int cantidad_inicial_metricas = nivel.lista_metricas_periodo.size();
            int indice_ultimo_insercion = 0;
            for(int i = 0; i < arreglo_participaciones.size(); i++)
            {
                    indice_ultimo_insercion = (i * cantidad_metricas_por_tipo ) + i;
                    Metrica metrica_pa = (Metrica) arreglo_participaciones.get(i);
                    nivel.lista_metricas_periodo.add(indice_ultimo_insercion
                                                    , metrica_pa
                                                    );
                    
            }
            
    }///FIN FIN insertarParticipaciones


    private String nombreResumidoMetricasCalculado(String nombre)
    {

            String[] arreglo = nombre.split("_");

            int cantidad = arreglo.length;
            String nombre_faltante = "";
            for(int i = 1; i < cantidad; i++)
            {
                nombre_faltante += "_" + arreglo[i];
            }

            return this.nuevoNombre(arreglo[0]) + nombre_faltante;

    }/// FIN FIN nombreResumidoMetricasCalculado

    private Nivel obtenerSubtotalesVerticalesComparativo(ArrayList lista_registros, int indice_ultimo_nivel)
    {
                    System.out.println("Inicia Generacion de Arbol del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());

                    Nivel nivel_0 = this.generarListaNiveles(lista_registros, indice_ultimo_nivel);

                    System.out.println("Finaliza Generacion de Arbol del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());


                    ///GENERACION DE SUBOTALES VERTICALES
                        System.gc();
                        ArrayList lista_metricas_0 = null;
                                  lista_metricas_0 = ((Registro)lista_registros.get(0)).lista_metricas;
                        int indice_penultimo_nivel = indice_ultimo_nivel - 1;
                        ////if(indice_penultimo_nivel == 0)
                            ////indice_penultimo_nivel = indice_ultimo_nivel;
                        for(int i = 0; i < lista_metricas_0.size() ; i++)
                        {
                            if(indice_penultimo_nivel > 0)
                                this.sumarValoresNiveles(i, nivel_0.lista_niveles, indice_penultimo_nivel);

                            /////Suma los valores de los hijos del 1er nivel
                            Metrica metrica_padre = this.sumarHijosDeNivelPadre(i, this.nivel_raiz);
                            this.nivel_raiz.lista_metricas_periodo.add(metrica_padre);
                        }
                        System.out.println("Finaliza Generacion de Subtotales Verticales del objeto COM: " + this.id_objeto_en_sesion + " " + new Date());

                        /*
                    ///GENERACION DE SUBOTALES HORIZONTALES
                        System.gc();
                        this.obtenerSubtotalesHorizontalesNivel(this.nivel_raiz);
                        this.obtenerSubtotalesHorizontalesDeListas(this.nivel_raiz.lista_niveles, indice_ultimo_nivel);

                        System.out.println("Finaliza Generacion de Subtotales Horizontales del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());
                        */
                    return this.nivel_raiz;

        }///// obtenerSubtotalesVerticalesHorizontales

    private String generarQueryReporteComparativo()
    {
            String query = "";
            ArrayList arreglo_filtros = lista_filtros;
            ArrayList arreglo_filtros_adicionales = lista_filtros_adicionales;
            ArrayList arreglo_niveles = lista_niveles;
            ArrayList arreglo_metricas = lista_metricas;
            Fecha fecha_diaria_maxima =  fecha_maxima_diaria;
            ArrayList arreglo_fechas = lista_periodos_fechas;
            int cantidad_espacios = 16;

            
            ////String alias_tabla_producto_diaria = "thd";
            ////String alias_tabla_producto_mensual = "thm";
            String alias_tp = "tp";
            String alias_te = "te";
            String alias_t_ri = "t_right";
            String alias_t_final = "t_final";
            String alias_tf = "tf";
            String alias_t_te = "tte";

            String query_union_producto = this.generarQueryUnionProducto(arreglo_metricas
                                                                            , arreglo_fechas
                                                                            , arreglo_filtros_adicionales
                                                                            , cantidad_espacios - 2
                                                                         );
            String query_producto = this.generarQueryProductoComparativo(query_union_producto
                                                                , arreglo_metricas
                                                                , arreglo_fechas
                                                                , cantidad_espacios - 4
                                                                );

            String query_nivel_bajo_entradas = this.generarQueryNivelBajoEntradas(arreglo_metricas
                                                                            , arreglo_fechas
                                                                            , arreglo_filtros_adicionales
                                                                            , cantidad_espacios - 2
                                                                         );
            String query_entrada = this.generarQueryEntradasComparativo( query_nivel_bajo_entradas
                                                                        , arreglo_metricas
                                                                        , arreglo_fechas
                                                                        , cantidad_espacios - 4
                                                                        );



            ////INICIO DE SUPER QUERY
            query += this.generarEspaciosTab(cantidad_espacios - 12) + " SELECT \n"
                                                                     + this.generarCamposSelectGroupByFinal(arreglo_niveles, 1 , cantidad_espacios - 12);
            if(this.existe_nivel_estadistico == true)
            {
                String campo_te = alias_t_te + ".tipo_existencia ";
                ////query += this.generarEspaciosTab(cantidad_espacios - 12) + "    , max(ifnull(" + alias_t_te + ".tipo_existencia,' '))     as  tipo_existencia_te \n";
                query += this.generarEspaciosTab(cantidad_espacios - 12) + "    , max(if(" + campo_te + " IS NULL,' '," + campo_te + "))     as  tipo_existencia_te \n";
               
            }
            query += this.generarMetricasComparativo(this.lista_metricas_comparativas
                                                        , 0
                                                        , alias_tf
                                                        , true
                                                        , cantidad_espacios - 12
                                                        );
    
            query +=  this.generarEspaciosTab(cantidad_espacios - 12) + " FROM \n";
            query +=  this.generarEspaciosTab(cantidad_espacios - 12) + " ( \n";
            query +=  this.generarEspaciosTab(cantidad_espacios - 10) + " select " + alias_t_final + ".codigo_proveedor       as codigo_proveedor \n";
            query +=  this.generarEspaciosTab(cantidad_espacios - 10) + "        , " + alias_t_final + ".codigo_sucursal      as codigo_sucursal \n";
            query +=  this.generarEspaciosTab(cantidad_espacios - 10) + "        , " + alias_t_final + ".codigo_estadistico   as codigo_estadistico \n";
            query +=  this.generarMetricasComparativo(this.lista_metricas_comparativas
                                                        , 0
                                                        , alias_t_final
                                                        , false
                                                        , cantidad_espacios - 10
                                                        );
            query +=  this.generarEspaciosTab(cantidad_espacios - 10) + " from \n";
            query +=  this.generarEspaciosTab(cantidad_espacios - 10) + " ( \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " select " + alias_tp + ".codigo_proveedor        as codigo_proveedor \n"
                    + this.generarEspaciosTab(cantidad_espacios - 8) + "        , " + alias_tp + ".codigo_sucursal      as codigo_sucursal \n"
                    + this.generarEspaciosTab(cantidad_espacios - 8) + "        , " + alias_tp + ".codigo_estadistico   as codigo_estadistico \n"
                    + this.generarMetricasComparativo(this.lista_metricas_comparativas
                                                        , CODIGO_IPRODUCT_MENSUAL
                                                        , alias_tp
                                                        , false
                                                        , cantidad_espacios - 8
                                                        )
                    + this.generarMetricasComparativo(this.lista_metricas_comparativas
                                                        , CODIGO_IENTRADAS
                                                        , alias_te
                                                        , false
                                                        , cantidad_espacios - 8
                                                        )
                    
                    ;

            query += this.generarEspaciosTab(cantidad_espacios - 8) + " from \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " ( \n";
            query += query_producto;
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " ) " + alias_tp + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " left join \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " ( \n";
            query += query_entrada;
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " ) " + alias_te  + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " on  " + alias_tp + ".codigo_proveedor = " + alias_te + ".codigo_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + "     and " + alias_tp + ".codigo_sucursal = " + alias_te + ".codigo_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + "     and " + alias_tp + ".codigo_estadistico = " + alias_te + ".codigo_estadistico \n";



            query += this.generarEspaciosTab(cantidad_espacios - 9) + " UNION ALL \n";
            
            

            query += this.generarEspaciosTab(cantidad_espacios - 8) + " select " + alias_t_ri + ".codigo_proveedor          as codigo_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + "         , " + alias_t_ri + ".codigo_sucursal        as codigo_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + "         , " + alias_t_ri + ".codigo_estadistico     as codigo_estadistico \n";
            query += this.generarMetricasComparativo(this.lista_metricas_comparativas
                                                        , 0
                                                        , alias_t_ri
                                                        , false
                                                        , cantidad_espacios - 6
                                                        )
                                                        ;
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " from \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " ( \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " select " + alias_te + ".codigo_proveedor       as codigo_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "        , " + alias_te + ".codigo_sucursal        as codigo_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "        , " + alias_te + ".codigo_estadistico     as codigo_estadistico \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "        , " + alias_tp + ".codigo_proveedor       as codigo_tmp \n";
            query += this.generarMetricasComparativo(this.lista_metricas_comparativas
                                                        , CODIGO_IENTRADAS
                                                        , alias_te
                                                        , false
                                                        , cantidad_espacios - 5
                                                        )
                                                        ;
            query += this.generarMetricasComparativo(this.lista_metricas_comparativas
                                                        , CODIGO_IPRODUCT_MENSUAL
                                                        , alias_tp
                                                        , false
                                                        , cantidad_espacios - 5
                                                        )
                                                        ;
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " from \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " ( \n";
            query += query_entrada;
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " ) " + alias_te + "\n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " LEFT JOIN \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " ( \n";
            query += query_producto;
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " ) " + alias_tp + "\n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " on  " + alias_te + ".codigo_proveedor = " + alias_tp + ".codigo_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "     and " + alias_te + ".codigo_sucursal = " + alias_tp + ".codigo_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "     and " + alias_te + ".codigo_estadistico = " + alias_tp + ".codigo_estadistico \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " )  " + alias_t_ri + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " where t_right.codigo_tmp IS NULL \n";
            query += this.generarEspaciosTab(cantidad_espacios - 10) + " ) " + alias_t_final + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 12) + " ) " + alias_tf + " \n";

            if(this.existe_nivel_estadistico == true)
            {
                query += this.generarEspaciosTab(cantidad_espacios - 12) + " LEFT JOIN \n";
                query += this.generarEspaciosTab(cantidad_espacios - 12) + " ( \n";
                query += this.generarQueryTipoExistencia( arreglo_fechas
                                                          , arreglo_filtros_adicionales
                                                          , cantidad_espacios - 10
                                                        )
                                                        ;
                query += this.generarEspaciosTab(cantidad_espacios - 12) + " ) " +  alias_t_te + " \n";
                query += this.generarEspaciosTab(cantidad_espacios - 12) + " ON " + alias_tf + ".codigo_estadistico = " + alias_t_te +".codigo_estadistico \n";
            }

            query += this.generarEspaciosTab(cantidad_espacios - 12) + " INNER JOIN bi_dwh.dw_estadistico_dim  " + this.alias_dimension_estadistico + " \n ";
            query += this.generarEspaciosTab(cantidad_espacios - 12) + " ON " + alias_tf + "." + "codigo_estadistico = " + this.alias_dimension_estadistico + ".cod_estadistico \n";
            query += this.generarEspaciosTab(cantidad_espacios - 12) + " INNER JOIN bi_dwh.dw_sucursal_dim  " + this.alias_dimension_sucursal + " \n ";
            query += this.generarEspaciosTab(cantidad_espacios - 12) + " ON " + alias_tf + "." + "codigo_sucursal = " + this.alias_dimension_sucursal + ".cod_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 12) + " INNER JOIN bi_dwh.dw_proveedor_dim  " + this.alias_dimension_proveedor + " \n ";
            query += this.generarEspaciosTab(cantidad_espacios - 12) + " ON " + alias_tf + "." + "codigo_proveedor = " + this.alias_dimension_proveedor + ".id_proveedor \n";
            query +=  this.generarEspaciosTab(cantidad_espacios - 12)
                        + this.generarFiltrosUltimoNivel(arreglo_filtros_adicionales
                                                         , arreglo_filtros
                                                         , arreglo_niveles
                                                         , cantidad_espacios - 12
                                                         )
                                                                                                    ;

            /*
            query = query_producto
                    + " ----------------------------------------------------------------------------------- \n "
                    + " ----------------------------------------------------------------------------------- \n "
                    + " ----------------------------------------------------------------------------------- \n "
                    + query_entrada
                    ;
            */
            
            return query;


    }/// FIN generarQueryReporteComparativo

    private String generarQueryTipoExistencia( ArrayList arreglo_fechas
                                                      , ArrayList arreglo_filtros_adicionales
                                                      , int cantidad_espacios
                                              )
    {
            String query_tipo_existencia = "";

            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios - 1) + " select t_te.codigo_estadistico as codigo_estadistico \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios - 1) + "         , t_te.tipo_existencia as tipo_existencia \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios - 1) + " from \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios - 1) + " ( \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " select tac.codigo_estadistico \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "        , if((tan.codigo_estadistico IS NOT NULL) and (tac.codigo_estadistico IS NOT NULL) \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "             , ' ' \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "             , if(tan.codigo_estadistico IS NULL \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "                     , 'N' \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "                     , 'E' \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "                 ) \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "             ) as tipo_existencia \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " from \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " ( \n";

            query_tipo_existencia += this.generarSubqueryTipoExistencia( arreglo_fechas
                                                , arreglo_filtros_adicionales
                                                , true
                                                , cantidad_espacios + 2
                                                ) ;

            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " ) tac \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " LEFT JOIN \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " ( \n";
            query_tipo_existencia += this.generarSubqueryTipoExistencia( arreglo_fechas
                                                , arreglo_filtros_adicionales
                                                , false
                                                , cantidad_espacios + 2
                                                ) ;
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " ) tan \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " ON tac.codigo_estadistico = tan.codigo_estadistico \n";
            
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " UNION ALL \n";

            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " select tt.codigo_estadistico \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "        , 'E'  as tipo_existencia \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " from \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " ( \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios + 2) + " select tan.codigo_estadistico \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios + 2) + "       , tac.codigo_estadistico as codigo_tmp \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios + 2) + " from \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios + 2) + " ( \n";
            query_tipo_existencia += this.generarSubqueryTipoExistencia( arreglo_fechas
                                                , arreglo_filtros_adicionales
                                                , true
                                                , cantidad_espacios + 2
                                                ) ;
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios + 2) + " ) tac \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios + 2) + " RIGHT JOIN \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios + 2) + " ( \n";
            query_tipo_existencia += this.generarSubqueryTipoExistencia( arreglo_fechas
                                                , arreglo_filtros_adicionales
                                                , false
                                                , cantidad_espacios + 2
                                                ) ;
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios + 2) + " ) tan \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios + 2) + " ON tac.codigo_estadistico = tan.codigo_estadistico \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " ) tt \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " where tt.codigo_tmp IS NULL \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios - 1) + " ) t_te \n";

            
            return query_tipo_existencia;
            
            
    }////FIN FIN generarQueryTipoExistencia

    private String generarSubqueryTipoExistencia(
                                                    ArrayList arreglo_fechas
                                                    , ArrayList arreglo_filtros_adicionales
                                                    , boolean genero_fechas_actuales
                                                    , int cantidad_espacios
                                                )
    {
            String query_tipo_existencia = "";
            String alias_thm = "thm";
            String alias_te = "te";

            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " select " + alias_thm + ".fc_cod_estadistico     as codigo_estadistico \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " from dw_iproduct_mm_fact2  " + alias_thm + " \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " where ( \n";
            query_tipo_existencia += this.generarWhereFechasTipoExistencia
                                                                        (
                                                                        arreglo_fechas
                                                                        , alias_thm
                                                                        , CODIGO_IPRODUCT_MENSUAL
                                                                        , genero_fechas_actuales
                                                                        , "fc_fecha"
                                                                        , cantidad_espacios + 3
                                                                        );

            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "       ) \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios)
                                    + " " + this.queryFiltrosAdicionales
                                                (
                                                    arreglo_filtros_adicionales
                                                    , CODIGO_IPRODUCT_MENSUAL
                                                    , cantidad_espacios + 3
                                                )
                                     + " \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " group by codigo_estadistico \n";

            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " UNION \n";

            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " select " + alias_te + ".cod_estadistico      as codigo_estadistico \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " from dw_entradas_fact " + alias_te + " \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " where ( \n";
            query_tipo_existencia += this.generarWhereFechasTipoExistencia
                                                                        ( arreglo_fechas
                                                                            , alias_te
                                                                            , CODIGO_IENTRADAS
                                                                            , genero_fechas_actuales
                                                                            , "fecha"
                                                                            , cantidad_espacios + 3
                                                                        );
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + "       ) \n";
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios)
                                    + this.queryFiltrosAdicionales
                                                (
                                                    arreglo_filtros_adicionales
                                                    , CODIGO_IENTRADAS
                                                    , cantidad_espacios + 3
                                                );
            query_tipo_existencia += this.generarEspaciosTab(cantidad_espacios) + " group by codigo_estadistico \n";
                                               

            return query_tipo_existencia;
    }////FIN FIN generarSubqueryTipoExistencia


    private String generarMetricasComparativo(ArrayList arreglo_metricas_comparativas
                                                , int tipo_tabla
                                                , String alias_tabla
                                                , boolean es_ultimo_nivel
                                                , int cantidad_espacios
                                                )
    {
                String query_metricas = "";
                String inicio_select_final = "sum(ifnull(";
                String fin_select_final = ",0))";
                String linea_campo = "";

                for(int i = 0; i < arreglo_metricas_comparativas.size(); i++)
                {
                      MetricaSelectComparativo obj_metrica = (MetricaSelectComparativo) arreglo_metricas_comparativas.get(i);
                      linea_campo = "";
                      if(obj_metrica.tipo_tabla == tipo_tabla || tipo_tabla == 0)
                      {
                            
                            query_metricas += this.generarEspaciosTab(cantidad_espacios) + " , ";
                            
                            linea_campo = alias_tabla + "." +  obj_metrica.nombre_metrica;
                            
                            if(es_ultimo_nivel == true)
                                query_metricas += inicio_select_final + linea_campo + fin_select_final;
                            else
                                query_metricas += linea_campo;
                            
                            query_metricas += "     as  " + obj_metrica.nombre_metrica + " \n";


                      }
                }

                return query_metricas;
    }////FIN FIN

    private String generarQueryUnionProducto( ArrayList arreglo_metricas
                                              , ArrayList arreglo_fechas
                                              , ArrayList arreglo_filtros_adicionales
                                              , int cantidad_espacios
                                            )
    {
            String query_union_producto = "";
            String alias_tpm = "thm";
            
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + " select " + alias_tpm + ".fc_fecha             as  fecha \n" ;
            query_union_producto += this.generarEspaciosTab(cantidad_espacios + 2) + " , " + alias_tpm + ".fc_cod_sucursal       as  codigo_sucursal \n";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios + 2) + " , " + alias_tpm + ".fc_cod_estadistico    as  codigo_estadistico \n";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios + 2) + " , " + alias_tpm + ".fc_cod_proveedor      as  codigo_proveedor \n";
            query_union_producto += this.generarMetricasNivelBajoComparativo(
                                                                           arreglo_metricas
                                                                           , alias_tpm
                                                                           , CODIGO_IPRODUCT_MENSUAL
                                                                           , cantidad_espacios + 2
                                                                         );
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + " from dw_iproduct_mm_fact2 " + alias_tpm + "\n";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + " where ( \n";
            query_union_producto += this.generarWhereFechasNivelMasBajoComparativo(arreglo_fechas
                                                                        , alias_tpm
                                                                        , 2
                                                                        , "fc_fecha"
                                                                        , cantidad_espacios + 3);
                                 
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + "       ) \n";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios)
                                    + " " + this.queryFiltrosAdicionales
                                                (
                                                    arreglo_filtros_adicionales
                                                    , CODIGO_IPRODUCT_MENSUAL
                                                    , cantidad_espacios + 3
                                                )
                                     + " \n";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios - 1)
                                 + " UNION ALL \n\n";

            alias_tpm = "thd";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + " select " + alias_tpm + ".fc_fecha              as  fecha \n" ;
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + "      , " + alias_tpm + ".fc_cod_sucursal       as  codigo_sucursal \n";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + "      , " + alias_tpm + ".fc_cod_estadistico    as  codigo_estadistico \n";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + "      , " + alias_tpm + ".fc_cod_proveedor      as  codigo_proveedor \n";
            query_union_producto += this.generarMetricasNivelBajoComparativo(
                                                                           arreglo_metricas
                                                                           , alias_tpm
                                                                           , CODIGO_IPRODUCT_MENSUAL
                                                                           , cantidad_espacios + 2
                                                                         );
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + " from dw_iproduct_fact " + alias_tpm + "\n";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + " where ( \n";
            query_union_producto += this.generarWhereFechasNivelMasBajoComparativo(arreglo_fechas
                                                                        , alias_tpm
                                                                        , 1
                                                                        , "fc_fecha"
                                                                        , cantidad_espacios + 3);

            query_union_producto += this.generarEspaciosTab(cantidad_espacios) + "       ) \n";
            query_union_producto += this.generarEspaciosTab(cantidad_espacios)
                                    + " " + this.queryFiltrosAdicionales
                                                (
                                                    arreglo_filtros_adicionales
                                                    , CODIGO_IPRODUCT_MENSUAL
                                                    , cantidad_espacios + 3
                                                )
                                     + " \n";

            return query_union_producto;

    }////FIN FIN generarQueryUnionProducto



    private String generarQueryNivelBajoEntradas(ArrayList arreglo_metricas
                                                  , ArrayList arreglo_fechas
                                                  , ArrayList arreglo_filtros_adicionales
                                                  , int cantidad_espacios
                                                  )
    {
            String query_nivel_bajo = "";
            String alias_tabla = " the";

            query_nivel_bajo += this.generarEspaciosTab(cantidad_espacios) + " select date_format(" + alias_tabla  + ".fecha,'%Y-%m-01')    as  fecha \n" ;
            query_nivel_bajo += this.generarEspaciosTab(cantidad_espacios + 2) + "      , " + alias_tabla + ".cod_sucursal                  as  codigo_sucursal \n";
            query_nivel_bajo += this.generarEspaciosTab(cantidad_espacios + 2) + "      , " + alias_tabla + ".cod_estadistico               as  codigo_estadistico \n";
            query_nivel_bajo += this.generarEspaciosTab(cantidad_espacios + 2) + "      , " + alias_tabla + ".cod_proveedor                 as  codigo_proveedor \n";
            query_nivel_bajo += this.generarMetricasNivelBajoComparativo(
                                                                           arreglo_metricas
                                                                           , alias_tabla
                                                                           , CODIGO_IENTRADAS
                                                                           , cantidad_espacios + 2
                                                                         );
            query_nivel_bajo += this.generarEspaciosTab(cantidad_espacios) + " from dw_entradas_fact " + alias_tabla + " \n";
            query_nivel_bajo += this.generarEspaciosTab(cantidad_espacios) + " where ( \n";
            query_nivel_bajo += this.generarWhereFechasNivelMasBajoComparativo(arreglo_fechas
                                                                        , alias_tabla
                                                                        , 3
                                                                        , "fecha"
                                                                        , cantidad_espacios + 3);
            query_nivel_bajo += this.generarEspaciosTab(cantidad_espacios) + "       ) \n";
            query_nivel_bajo += this.generarEspaciosTab(cantidad_espacios) + this.queryFiltrosAdicionales
                                                (
                                                    arreglo_filtros_adicionales
                                                    , CODIGO_IENTRADAS
                                                    , cantidad_espacios + 3
                                                )
                                                ;

            return query_nivel_bajo;
    } ///FIN FIN generarQueryNivelBajoEntradas




    private String generarMetricasNivelBajoComparativo(ArrayList arreglo_metricas
                                                        , String alias_tabla
                                                        , int tipo_tabla
                                                        , int cantidad_espacios
                                                    )
    {
            String query_metricas = "";

            for(int i = 0; i < arreglo_metricas.size() ;i++)
            {
                ObjetoFiltroFlex obj_filtro_flex = (ObjetoFiltroFlex)arreglo_metricas.get(i);
                if(obj_filtro_flex.tipo_tabla == tipo_tabla)
                {
                    query_metricas += this.generarEspaciosTab(cantidad_espacios)
                                         + ", "
                                         + alias_tabla + "." + obj_filtro_flex.campo_tabla
                                         + "  as  " + obj_filtro_flex.alias_campo_tabla + "\n";
                }
            }

            return query_metricas;
    }////FIN FIN generacionMetricasUnionProducto


    private String generarQueryEntradasComparativo(String query_nivel_bajo_entradas
                                                   , ArrayList arreglo_metricas
                                                   , ArrayList arreglo_fechas
                                                   , int cantidad_espacios
                                                    )
    {
            String query_entradas_comparativo = "";
            String alias_tmp = "tmp";
            
                   query_entradas_comparativo += generarEspaciosTab(cantidad_espacios) + " select tmp.codigo_sucursal         as codigo_sucursal \n";
                   query_entradas_comparativo += generarEspaciosTab(cantidad_espacios) + "         , tmp.codigo_proveedor     as codigo_proveedor \n";
                   query_entradas_comparativo += generarEspaciosTab(cantidad_espacios) + "         , tmp.codigo_estadistico   as codigo_estadistico \n";

                   query_entradas_comparativo += this.querySelectMetricasComparativo(
                                                                    arreglo_metricas
                                                                    , arreglo_fechas
                                                                    , alias_tmp
                                                                    , CODIGO_IENTRADAS
                                                                    , cantidad_espacios + 2
                                                                  );
                   query_entradas_comparativo += generarEspaciosTab(cantidad_espacios) + " from \n";
                   query_entradas_comparativo += generarEspaciosTab(cantidad_espacios) + " ( \n";
                   query_entradas_comparativo += query_nivel_bajo_entradas;
                   query_entradas_comparativo += generarEspaciosTab(cantidad_espacios) + " ) " + alias_tmp + " \n";
                   query_entradas_comparativo += generarEspaciosTab(cantidad_espacios) + " group by codigo_sucursal \n"
                                                + generarEspaciosTab(cantidad_espacios) + "          , codigo_proveedor \n"
                                                + generarEspaciosTab(cantidad_espacios) + "          , codigo_estadistico \n"
                                ;
            return query_entradas_comparativo;

    }////FIN FIN generarQueryEntradasComparativo


    private String generarWhereFechasTipoExistencia(
                                                        ArrayList arreglo_periodos
                                                        , String alias_tabla
                                                        , int tipo_tabla
                                                        , boolean solo_genero_actual
                                                        , String campo_fecha
                                                        , int cantidad_espacios

            )
    {


            String query_filtro_fechas = "";
            String operador = " or ";
            String espacio_nueva_linea = "";
            int cantidad_fechas = arreglo_periodos.size();
            for(int i = 0; i < cantidad_fechas; i++)
            {

                    Fecha obj_fecha = (Fecha)arreglo_periodos.get(i);
                    if( i > 0)
                       espacio_nueva_linea = this.generarEspaciosTab(cantidad_espacios) + operador;
                    else
                       espacio_nueva_linea = this.generarEspaciosTab(cantidad_espacios);
                    if(tipo_tabla == CODIGO_IPRODUCT_MENSUAL)
                    {
                            ///FECHAS PARA IPRODUCT MENSUAL

                            query_filtro_fechas += espacio_nueva_linea + " ( " + alias_tabla + "." + campo_fecha
                                                    + " = '";
                            if(solo_genero_actual == true)
                                query_filtro_fechas += obj_fecha.getFecha();
                            else
                                query_filtro_fechas += obj_fecha.getFecha_anterior();

                            query_filtro_fechas += "') \n";

                }
                else
                {
                    
                        ///ENTRADAS
                        ////System.out.println("tipo tabla EN::" + tipo_tabla);
                        query_filtro_fechas += espacio_nueva_linea;

                        query_filtro_fechas += " (" + alias_tabla + "." + campo_fecha
                                                    + " >='";
                        if(solo_genero_actual == true)
                            query_filtro_fechas += obj_fecha.getFecha();
                        else
                            query_filtro_fechas += obj_fecha.getFecha_anterior();

                        query_filtro_fechas += "'  and  " + alias_tabla + "." + campo_fecha
                                                    + " <='";

                        if(solo_genero_actual == true)
                            query_filtro_fechas += obj_fecha.getFecha_ultimo_dia();
                        else
                            query_filtro_fechas += obj_fecha.getFecha_anterior_ultimo_dia();
                        
                         query_filtro_fechas += "') \n";
                    
                }


            }///FIN FOR
            ////System.out.println("tipo tabla " + tipo_tabla + "\n" + query_filtro_fechas);
            return query_filtro_fechas;

    }///FIN FIN 


    private String generarWhereFechasNivelMasBajoComparativo
                                                    (
                                                        ArrayList arreglo_periodos
                                                        , String alias_tabla
                                                        , int tipo_tabla
                                                        , String campo_fecha
                                                        , int cantidad_espacios
                                                    
                                                    )
    {
            
            String query_filtro_fechas = "";
            String operador = " or ";
            String espacio_nueva_linea = "";
            int cantidad_fechas = arreglo_periodos.size();
            for(int i = 0; i < cantidad_fechas; i++)
            {
                    
                    Fecha obj_fecha = (Fecha)arreglo_periodos.get(i);
                    if( i > 0)
                       espacio_nueva_linea = this.generarEspaciosTab(cantidad_espacios) + operador;
                    else
                       espacio_nueva_linea = this.generarEspaciosTab(cantidad_espacios);
                    if(tipo_tabla == 2)
                    {
                            ///FECHAS PARA IPRODUCT MENSUAL
                            
                            query_filtro_fechas += espacio_nueva_linea + " ( " + alias_tabla + "." + campo_fecha
                                                    + " = '" + obj_fecha.getFecha() + "') ";
                            if( i == cantidad_fechas - 1)
                            {
                                query_filtro_fechas += "\n";
                            }
                            else
                            {
                                query_filtro_fechas += " or ("
                                                    +  alias_tabla + "." + campo_fecha + " = '"
                                                    + obj_fecha.getFecha_anterior() +  "') \n"
                                                    ;
                            }
                }
                else
                {
                    if(tipo_tabla == 1)
                    {
                            
                            ///IPRODUCT DIARIA
                            if(i == cantidad_fechas - 1)
                            {

                                query_filtro_fechas += this.generarEspaciosTab(cantidad_espacios)
                                                    + "  " + alias_tabla + "." + campo_fecha
                                                    + " >='" + obj_fecha.getFecha_anterior() + "' "
                                                    + " and " + alias_tabla + "." + campo_fecha + " <='"
                                                    + obj_fecha.getFecha_anterior_ultimo_dia()
                                                    ////+ fecha_diaria_maxima
                                                    + "' \n"

                                                    ;
                            }
                    }
                    else
                    {
                        ///ENTRADAS
                        ////System.out.println("tipo tabla EN::" + tipo_tabla);
                        String periodo_1 = " (" + alias_tabla + "." + campo_fecha
                                                    + " >='" + obj_fecha.getFecha() + "' "
                                                    + " and " + alias_tabla + "." + campo_fecha 
                                                    + " <='" + obj_fecha.getFecha_ultimo_dia() + "') \n";
                        query_filtro_fechas += espacio_nueva_linea + periodo_1;
                        
                        String periodo_2 = this.generarEspaciosTab(cantidad_espacios)
                                                    + " or (" + alias_tabla + "." + campo_fecha
                                                    + " >='" + obj_fecha.getFecha_anterior() + "' "
                                                    + " and " + alias_tabla + "." + campo_fecha
                                                    + " <='" + obj_fecha.getFecha_anterior_ultimo_dia() + "') \n";
                        query_filtro_fechas += periodo_2;   

                    }
                }
                

            }///FIN FOR
            ////System.out.println("tipo tabla " + tipo_tabla + "\n" + query_filtro_fechas);
            return query_filtro_fechas;
    }///FIN FIN generarWhereFechasUnionProducto
    



    private String generarQueryProductoComparativo(String query_union_producto
                                                    , ArrayList arreglo_metricas
                                                    , ArrayList arreglo_fechas
                                                    , int cantidad_espacios
                                                )
    {
            String query_producto = "";
            String alias_tmp ="tmp";
            query_producto += generarEspaciosTab(cantidad_espacios) + " select round(tmp.codigo_sucursal,0)         as codigo_sucursal \n";
            query_producto += generarEspaciosTab(cantidad_espacios) + "         , round(tmp.codigo_proveedor,0)     as codigo_proveedor \n";
            query_producto += generarEspaciosTab(cantidad_espacios) + "         , round(tmp.codigo_estadistico,0)   as codigo_estadistico \n";
            query_producto += this.querySelectMetricasComparativo( arreglo_metricas
                                                                    , arreglo_fechas
                                                                    , alias_tmp
                                                                    , CODIGO_IPRODUCT_MENSUAL
                                                                    , cantidad_espacios + 2
                                                                  );

            query_producto += generarEspaciosTab(cantidad_espacios) + " from \n";
            query_producto += generarEspaciosTab(cantidad_espacios) + " ( \n";
            query_producto += query_union_producto;
            query_producto += generarEspaciosTab(cantidad_espacios) + " ) " + alias_tmp + " \n";
            query_producto += generarEspaciosTab(cantidad_espacios) + " group by codigo_sucursal \n"
                              + generarEspaciosTab(cantidad_espacios) + "          , codigo_proveedor \n"
                              + generarEspaciosTab(cantidad_espacios) + "          , codigo_estadistico \n"
                              ;

            return query_producto;

    }///FIN FIN generarQueryProductoComparativo


    
    private String querySelectMetricasComparativo(
                                                     ArrayList arreglo_metricas
                                                    , ArrayList arreglo_fechas
                                                    , String alias_tmp
                                                    , int tipo_tabla
                                                    , int cantidad_espacios
                                                    )
    {
            String query_metricas = "";
            String inicio_linea = " sum(round(if(\n";
            String fin_linea = "),3)) as ";
            cantidad_espacios = cantidad_espacios + 1;

            String condicion_fechas_ac = "";
            String condicion_fechas_an = "";
            String operador_or = "";
            int cantidad_fechas = arreglo_fechas.size();
            MetricaSelectComparativo metrica_sc_ac;
            MetricaSelectComparativo metrica_sc_an;
            Fecha obj_fecha;

            for(int i = 0; i < arreglo_metricas.size(); i++)
            {
                
                               
                ObjetoFiltroFlex obj_metrica = (ObjetoFiltroFlex) arreglo_metricas.get(i);

                
                if(obj_metrica.tipo_tabla == tipo_tabla)
                {
                    metrica_sc_ac = new MetricaSelectComparativo();
                    metrica_sc_ac.nombre_metrica = obj_metrica.alias_campo_tabla + "_ac";
                    metrica_sc_ac.tipo_tabla = tipo_tabla;
                    this.lista_metricas_comparativas.add(metrica_sc_ac);

                    metrica_sc_an = new MetricaSelectComparativo();
                    metrica_sc_an.nombre_metrica = obj_metrica.alias_campo_tabla + "_an";
                    metrica_sc_an.tipo_tabla = tipo_tabla;
                    this.lista_metricas_comparativas.add(metrica_sc_an);

                    condicion_fechas_ac = inicio_linea;
                    condicion_fechas_an = inicio_linea;
                    if(obj_metrica.sumarizado_tiempo == true)
                    {
                                for(int j = 0; j < cantidad_fechas; j++)
                                {
                                    obj_fecha = (Fecha)arreglo_fechas.get(j);

                                    if(j > 0)
                                        operador_or = "  or  (";
                                    else
                                        operador_or = "  (";

                                    condicion_fechas_ac += generarEspaciosTab(cantidad_espacios) + operador_or;
                                    condicion_fechas_an += generarEspaciosTab(cantidad_espacios) + operador_or;

                                    condicion_fechas_ac += " fecha = '" + obj_fecha.getFecha() + "') \n";
                                    if((j == cantidad_fechas - 1) && (tipo_tabla != CODIGO_IENTRADAS))
                                    {
                                        condicion_fechas_an += " fecha >= '"
                                                            + obj_fecha.getFecha_anterior() + "' "
                                                            + " and fecha <= '" + obj_fecha.getFecha_anterior_ultimo_dia() + "' "
                                                            + ") \n";
                                    }
                                    else
                                        condicion_fechas_an += " fecha = '" + obj_fecha.getFecha_anterior() + "') \n";



                                }///Fin For de Fechas

                                

                                condicion_fechas_ac += generarEspaciosTab(cantidad_espacios)
                                                        + " , " + alias_tmp + "." + obj_metrica.alias_campo_tabla + "\n"
                                                        + generarEspaciosTab(cantidad_espacios)
                                                        + " , 0 \n"
                                                        + generarEspaciosTab(cantidad_espacios)
                                                        + fin_linea + metrica_sc_ac.nombre_metrica + " \n"
                                                        ;

                                condicion_fechas_an += generarEspaciosTab(cantidad_espacios)
                                                        + " , " + alias_tmp + "." + obj_metrica.alias_campo_tabla + "\n"
                                                        + generarEspaciosTab(cantidad_espacios)
                                                        + " , 0 \n"
                                                        + generarEspaciosTab(cantidad_espacios)
                                                        + fin_linea + metrica_sc_an.nombre_metrica + " \n"
                                                        ;
                    }///IF Metricas NO STOCK
                    else
                    {
                        ///METRICAS ULTIMO VALOR EN EL TIEMPO EJ:: STOCK
                        obj_fecha = (Fecha)arreglo_fechas.get(cantidad_fechas - 1);
                        condicion_fechas_ac += generarEspaciosTab(cantidad_espacios)
                                                ///-+ " fecha = '" + obj_fecha.getFecha_ultimo_dia() + "' \n"
                                                + " fecha = '" + obj_fecha.getFecha() + "' \n"
                                                + generarEspaciosTab(cantidad_espacios)
                                                + " , " + alias_tmp  + "." + obj_metrica.alias_campo_tabla + "\n"
                                                + generarEspaciosTab(cantidad_espacios) + " , 0 \n"
                                                + generarEspaciosTab(cantidad_espacios)
                                                + fin_linea + metrica_sc_ac.nombre_metrica + " \n"
                                                ;
                        condicion_fechas_an += generarEspaciosTab(cantidad_espacios)
                                                + " fecha = '" + obj_fecha.getFecha_anterior_ultimo_dia() + "' \n"
                                                + generarEspaciosTab(cantidad_espacios)
                                                + " , " + alias_tmp  + "." + obj_metrica.alias_campo_tabla + " \n"
                                                + generarEspaciosTab(cantidad_espacios) + " , 0 \n"
                                                + generarEspaciosTab(cantidad_espacios)
                                                + fin_linea + metrica_sc_an.nombre_metrica + " \n"
                                                ;
                        
                    }
               query_metricas += generarEspaciosTab(cantidad_espacios) + " , " + condicion_fechas_ac;
               query_metricas += generarEspaciosTab(cantidad_espacios) + " , " + condicion_fechas_an;
               }///IF de Tipo de Tabla
               
            }///Fin For Metricas

            return query_metricas;
    }////FIN FIN querySelectMetricasComparativo
    


    /**************************************************************************************************
     **************************FIN FIN REPORTE COMPARATIVO TIPO 2 *************************************
     *************************************************************************************************/

    /*
    private void mostrarTamanioObjeto(String objeto)
    {
         SizeOf.skipStaticField(true); //java.sizeOf will not compute static fields
         SizeOf.skipFinalField(true); //java.sizeOf will not compute final fields
         SizeOf.skipFlyweightObject(true); //java.sizeOf will not compute well-known flyweight objects
         System.out.println("Tamanio en la memoria del XML enviado a flex: " + SizeOf.deepSizeOf(objeto)); //this will print the object size in bytes
         System.out.println("Tamanio en megas: " + SizeOf.humanReadable(SizeOf.deepSizeOf(objeto)) );

    }////mostrarTamanioObjeto

     */

    private void imprimirFiltrosYQuery(ArrayList lista_fechas
                                , ArrayList lista_filtros
                                , ArrayList lista_filtros_adicionales
                                , ArrayList lista_metricas
                                , ArrayList lista_niveles
                                )
    {
        this.imprimirArreglosEnArchivo(lista_fechas, "txt_filtros_fechas.txt");
        this.imprimirArreglosEnArchivo(lista_filtros, "txt_filtros.txt");
        this.imprimirArreglosEnArchivo(lista_filtros_adicionales, "txt_filtros_adicionales.txt");
        this.imprimirArreglosEnArchivo(lista_metricas, "txt_filtros_metricas.txt");
        this.imprimirArreglosEnArchivo(lista_niveles, "txt_filtros_niveles.txt");
        ////this.imprimirStringEnArchivo(query, "query.sql");
    }


    

    /***********************************************************************************************************************
     *************************************************** FUNCIONES QUE GENERAN EL XML **************************************
     ***********************************************************************************************************************
     ****/

                /* *************************************************************************************************************
                ****************************************GENERAN XML DE LISTA DE NIVELES ************************************
                *************************************************************************************************************
                 * */

                        public String generarXmlReporte(ArrayList lista_registros, boolean es_reporte_detalle_mensual)
                        {

                                
                             
                                String cadena_xml = "";
                                int indice_ultimo_nivel = ((Registro)lista_registros.get(0)).lista_atributos.size();
                                Nivel nivel_0 = this.obtenerSubtotalesVerticalesHorizontales(lista_registros, indice_ultimo_nivel);

                                
                                System.out.println("Inicia Generacion del String XML del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());

                                    cadena_xml = generarXmlNivel(nivel_0, indice_ultimo_nivel, es_reporte_detalle_mensual);

                                    cadena_xml += this.generarXmlLista(nivel_0.lista_niveles, indice_ultimo_nivel, es_reporte_detalle_mensual).toString();

                                    ///cadena_xml += "\n</" + nivel_0.nombre_xml + ">";
                                    cadena_xml += "\n</" + "N_" + String.valueOf(nivel_0.indice_nivel) + ">";
                                System.out.println("Finaliza Generacion del String XML del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());


                              return cadena_xml;
                        }////generarXmlReporte

                        private StringBuilder generarXmlLista(ArrayList lista_niveles
                                                                , int ultimo_indice_nivel
                                                                , boolean es_reporte_detalle_mensual
                                                              )
                        {
                                ///String nueva_cadena = "";
                                ///String cadena_tmp = "";
                                StringBuilder nueva_cadena =  new StringBuilder();
                                Nivel nivel;
                                /*try
                                {*/
                                        
                                        if(lista_niveles != null)
                                        {
                                                for(int i = 0; i < lista_niveles.size(); i++)
                                                {
                                                    nivel = (Nivel)lista_niveles.get(i);


                                                    if(nivel.indice_nivel == ultimo_indice_nivel)
                                                        nueva_cadena.append(this.generarXmlNivel(nivel, ultimo_indice_nivel, es_reporte_detalle_mensual));
                                                    else
                                                    {
                                                        nueva_cadena.append(this.generarXmlNivel(nivel, ultimo_indice_nivel, es_reporte_detalle_mensual));
                                                        nueva_cadena.append(this.generarXmlLista(nivel.lista_niveles, ultimo_indice_nivel, es_reporte_detalle_mensual));
                                                        ////String etiqueta_final = "\n</" + nivel.nombre_xml + ">\n";
                                                        String etiqueta_final = "\n</" +  "N_" + String.valueOf(nivel.indice_nivel) + ">\n";
                                                        nueva_cadena.append(etiqueta_final);
                                                    }

                                                }
                                        }
                                /*}
                                catch(Exception e)
                                {
                                    System.out.println("Error en generacion String xml : " + this.id_objeto_en_sesion + " " + new Date() + "\n" + e.toString());
                                }*/
                                
                                return nueva_cadena;

                        }//// FIN FIN generarXmlLista


                        private String generarXmlNivel(Nivel nivel
                                                        , int ultimo_indice_nivel
                                                        , boolean es_reporte_detalle_mensual)
                        {
                                Metrica metrica_total;
                                Metrica metrica_periodo;


                                ////String cadena = "<"+ nivel.nombre_xml ;
                                String cadena = "<"+ "N_" + String.valueOf(nivel.indice_nivel);
                                       cadena += " codigo=\"" + nivel.codigo + "\" ";
                                       cadena += " nombre=\"" + this.reemplazarCaracteres(nivel.nombre) + "\" ";

                               
                                if(es_reporte_detalle_mensual == true)
                                {
                                        for(int i = 0; i < nivel.lista_metricas_total.size(); i++)
                                        {

                                            metrica_total = (Metrica)nivel.lista_metricas_total.get(i);
                                            cadena += " " + this.extraerMinimoNombre(metrica_total.nombre_compuesto) + "=\"" + this.redondearNumero(metrica_total.valor) +"\" ";

                                        }
                                }
                                else
                                {
                                           for(int k = 0; k < nivel.lista_marcas.size(); k++)
                                           {
                                               Marca marca = (Marca) nivel.lista_marcas.get(k);
                                               cadena += " flag_exi=\"" + this.reemplazarCaracteres(marca.valor) + "\" ";
                                           }
                                }

                                       
                                for(int j =0; j < nivel.lista_metricas_periodo.size(); j++)
                                {
                                        metrica_periodo = (Metrica)nivel.lista_metricas_periodo.get(j);
                                        cadena += " " + this.extraerMinimoNombre(metrica_periodo.nombre_compuesto) + "=\"" + this.redondearNumero(metrica_periodo.valor) + "\" ";
                                }


                               if(nivel.indice_nivel == ultimo_indice_nivel)
                                   cadena += " />\n";
                               else
                                   cadena += " >\n";

                               return cadena;

                               
                        }//// generarXmlNivel


                        private String extraerMinimoNombre(String nombre)
                        {
                            String nuevo_nombre = "";


                             String [] arreglo_nombres = nombre.split("_");
                             
                             String nombre_tmp = arreglo_nombres[0];

                             nuevo_nombre = this.nuevoNombre(nombre_tmp);

                             int primera_subguion = nombre.indexOf("_");
                             return nuevo_nombre + nombre.substring(primera_subguion);
                             
                        }///// extraerMinimoNombre


                        private String nuevoNombre(String nombre)
                        {
                             String nuevo_nombre;
                             if(nombre.equalsIgnoreCase("consumo"))
                                 nuevo_nombre = "c";
                             else
                             {
                                 if(nombre.equalsIgnoreCase("entradas"))
                                    nuevo_nombre = "e";
                                 else
                                 {
                                        if(nombre.equalsIgnoreCase("ganancia"))
                                            nuevo_nombre = "g";
                                        else
                                        {
                                            if(nombre.equalsIgnoreCase("stock"))
                                                nuevo_nombre = "s";
                                            else
                                            {
                                                if(nombre.equalsIgnoreCase("merma"))
                                                    nuevo_nombre = "m";
                                                else
                                                {
                                                    if(nombre.equalsIgnoreCase("botado"))
                                                        nuevo_nombre = "b";
                                                    else
                                                        nuevo_nombre = nombre;
                                                }
                                            }

                                        }
                                 }
                             }

                             return nuevo_nombre;

                        }///FIN FIN nuevoNombre

                        private String reemplazarCaracteres(String cadena)
                        {
                                String nueva_cadena;
                                
                                nueva_cadena = cadena.replace("&", "&amp;");
                                nueva_cadena = nueva_cadena.replace("<", "&lt;");
                                nueva_cadena = nueva_cadena.replace(">", "&gt;");
                                nueva_cadena = nueva_cadena.replace("Ñ", "N");
                                nueva_cadena = nueva_cadena.replace("ñ", "n");
                                nueva_cadena = nueva_cadena.replace("á", "a");
                                nueva_cadena = nueva_cadena.replace("é", "e");
                                nueva_cadena = nueva_cadena.replace("í", "i");
                                nueva_cadena = nueva_cadena.replace("ó", "o");
                                nueva_cadena = nueva_cadena.replace("ú", "u");
                                nueva_cadena = nueva_cadena.replace("Á", "A");
                                nueva_cadena = nueva_cadena.replace("É", "E");
                                nueva_cadena = nueva_cadena.replace("Í", "I");
                                nueva_cadena = nueva_cadena.replace("Ó", "O");
                                nueva_cadena = nueva_cadena.replace("Ú", "U");
                                nueva_cadena = nueva_cadena.replace("Ð", "N");
                                nueva_cadena = nueva_cadena.replace("´", " ");
                                
                                char caracter_comilla_doble = (char)34;
                                String cadena_comilla_doble = String.valueOf(caracter_comilla_doble);
                                nueva_cadena = nueva_cadena.replace("\"", " ");
                                nueva_cadena = nueva_cadena.replace("°", "CG");
                                nueva_cadena = nueva_cadena.replace("ü", "u");

                                ////return nueva_cadena.toLowerCase();
                                return nueva_cadena;
                        }///// reemplazarCaracteres

                        private String redondearNumero(double numero)
                        {
                            //////http://javafox.wordpress.com/2009/10/20/redondear-un-double-en-java/
                                 /// double valor = 1254.625;
                                  String valor = numero+"";
                                  BigDecimal big = new BigDecimal(valor);
                                  big = big.setScale(3, RoundingMode.HALF_UP);
                                  return String.valueOf(big);
                        }//// redondearNumero


                /* **********************************************************************************************************
                ****************************************GENERAN SUBTOTALES DE LISTA DE NIVELES ******************************
                *************************************************************************************************************
                 * */

                private Nivel obtenerSubtotalesVerticalesHorizontales(ArrayList lista_registros, int indice_ultimo_nivel)
                {
                    System.out.println("Inicia Generacion de Arbol del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());

                    Nivel nivel_0 = this.generarListaNiveles(lista_registros, indice_ultimo_nivel);

                    System.out.println("Finaliza Generacion de Arbol del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());

                     
                    ///GENERACION DE SUBOTALES VERTICALES
                        System.gc();
                        ArrayList lista_metricas_0 = null;
                                  lista_metricas_0 = ((Registro)lista_registros.get(0)).lista_metricas;
                        int indice_penultimo_nivel = indice_ultimo_nivel - 1;
                        ////if(indice_penultimo_nivel == 0)
                            ////indice_penultimo_nivel = indice_ultimo_nivel;
                        for(int i = 0; i < lista_metricas_0.size() ; i++)
                        {
                            if(indice_penultimo_nivel > 0)
                                this.sumarValoresNiveles(i, nivel_0.lista_niveles, indice_penultimo_nivel);
                            
                            /////Suma los valores de los hijos del 1er nivel
                            Metrica metrica_padre = this.sumarHijosDeNivelPadre(i, this.nivel_raiz);
                            this.nivel_raiz.lista_metricas_periodo.add(metrica_padre);
                        }
                        System.out.println("Finaliza Generacion de Subtotales Verticales del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());
                     
                    ///GENERACION DE SUBOTALES HORIZONTALES
                        System.gc();
                        this.obtenerSubtotalesHorizontalesNivel(this.nivel_raiz);
                        this.obtenerSubtotalesHorizontalesDeListas(this.nivel_raiz.lista_niveles, indice_ultimo_nivel);

                        System.out.println("Finaliza Generacion de Subtotales Horizontales del objeto DM: " + this.id_objeto_en_sesion + " " + new Date());
                        
                    return this.nivel_raiz;
                    
                }///// obtenerSubtotalesVerticalesHorizontales


                private void obtenerSubtotalesHorizontalesDeListas(ArrayList lista_niveles, int indice_ultimo_nivel)
                {
                    
                        for(int i = 0; i < lista_niveles.size(); i++ )
                        {
                            Nivel obj_nivel = (Nivel) lista_niveles.get(i);
                            if(obj_nivel.indice_nivel == indice_ultimo_nivel)
                            {
                                this.obtenerSubtotalesHorizontalesNivel(obj_nivel);
                            }
                            else
                            {
                                this.obtenerSubtotalesHorizontalesDeListas(obj_nivel.lista_niveles, indice_ultimo_nivel);
                                this.obtenerSubtotalesHorizontalesNivel(obj_nivel);
                            }
                        }

                }///// obtenerSubtotalesHorizontalesDeListas

                private void obtenerSubtotalesHorizontalesNivel(Nivel nivel)
                {
                        ArrayList lista_variables_periodos = nivel.lista_metricas_periodo;
                        
                        Metrica metrica_1;
                        Metrica metrica_2;
                        String nombre_metrica_1;
                        String nombre_metrica_2;
                        String ultimo_periodo;
                        String solo_nombre_metrica;
                        double valor;

                        for(int i = 0; i < lista_variables_periodos.size(); i++)
                        {
                            if( !((Metrica)nivel.lista_metricas_periodo.get(i)).fue_leido_horizontalmente )
                            {
                                    metrica_1 = (Metrica)lista_variables_periodos.get(i);
                                    nombre_metrica_1 = metrica_1.nombre_metrica;
                                    Metrica nueva_metrica = new Metrica();
                                    valor = 0.0;
                                    ultimo_periodo = "";
                                    for(int j = 0; j < lista_variables_periodos.size(); j++)
                                    {
                                        if( !((Metrica)nivel.lista_metricas_periodo.get(j)).fue_leido_horizontalmente )
                                        {
                                            metrica_2 = (Metrica) lista_variables_periodos.get(j);
                                            nombre_metrica_2 = metrica_2.nombre_metrica;
                                            if(nombre_metrica_1.equalsIgnoreCase(nombre_metrica_2))
                                            {
                                                ( (Metrica)nivel.lista_metricas_periodo.get(j) ).fue_leido_horizontalmente = true;
                                                solo_nombre_metrica = metrica_2.nombre_metrica.split("_")[0];
                                                if(!solo_nombre_metrica.equalsIgnoreCase(this.nombre_metrica_de_ultimo_valor))
                                                    valor = valor + metrica_2.valor;
                                                else
                                                    valor = metrica_2.valor;
                                                
                                                ultimo_periodo = metrica_2.nombre_periodo;
                                            }
                                        }
                                    }
                                    nueva_metrica.nombre_metrica = nombre_metrica_1;
                                    solo_nombre_metrica = nombre_metrica_1.split("_")[0];
                                    if(!solo_nombre_metrica.equalsIgnoreCase(this.nombre_metrica_de_ultimo_valor))
                                        nueva_metrica.nombre_periodo_completo = metrica_1.nombre_periodo + this.separador_periodos + ultimo_periodo;
                                    else
                                        nueva_metrica.nombre_periodo_completo = "ultimo_" + ultimo_periodo;
                                    
                                    nueva_metrica.nombre_compuesto = nombre_metrica_1 + "_" + nueva_metrica.nombre_periodo_completo;
                                    nueva_metrica.nombre_compuesto = nueva_metrica.nombre_compuesto.replace("-", "_");
                                    nueva_metrica.valor = valor;

                                    nivel.lista_metricas_total.add(nueva_metrica);
                                    
                                    ( (Metrica)nivel.lista_metricas_periodo.get(i) ).fue_leido_horizontalmente = true;
                            }
                        }
                        
                }///// obtenerSubtotalesHorizontales
             

                private void sumarValoresNiveles(int indice_metrica, ArrayList lista_nivel, int indice_penultimo_nivel)
                {
                    for(int i = 0; i < lista_nivel.size(); i++)
                    {
                        Nivel nivel_actual = (Nivel)lista_nivel.get(i);
                        if(nivel_actual.indice_nivel == indice_penultimo_nivel)
                        {
                               nivel_actual.lista_metricas_periodo.add(this.sumarHijosDeNivelPadre(indice_metrica, nivel_actual));
                        }
                        else
                        {
                            this.sumarValoresNiveles(indice_metrica, nivel_actual.lista_niveles, indice_penultimo_nivel);
                            nivel_actual.lista_metricas_periodo.add(this.sumarHijosDeNivelPadre(indice_metrica, nivel_actual));
                        }
                    }
                }/////sumarValoresNiveles
                

                private Metrica sumarHijosDeNivelPadre(int indice_metrica, Nivel nivel_padre)
                {
                       
                        Metrica nueva_metrica = new Metrica();
                        ArrayList lista_nivel_padre = nivel_padre.lista_niveles;

                        for(int i = 0; i < lista_nivel_padre.size(); i++)
                        {
                            Nivel obj_nivel_hijo = (Nivel)lista_nivel_padre.get(i);
                            Metrica metrica_hijo = (Metrica)obj_nivel_hijo.lista_metricas_periodo.get(indice_metrica);
                            if(i==0)
                            {
                                        nueva_metrica.nombre_compuesto = metrica_hijo.nombre_compuesto;
                                        nueva_metrica.nombre_metrica = metrica_hijo.nombre_metrica;
                                        nueva_metrica.nombre_periodo = metrica_hijo.nombre_periodo;
                            }
                            
                            nueva_metrica.valor = nueva_metrica.valor + metrica_hijo.valor;
                            
                        }

                        return nueva_metrica;
                        
                }//// sumarHijosDeNivelPadre

                /* **********************************************************************************************************
                ****************************************GENERAN LISTA DE NIVELES ******************************************
                ***********************************************************************************************************
                 * */
                private Nivel generarListaNiveles(ArrayList lista_registros, int indice_ultimo_nivel)
                {
                        int i = 0;
                        ArrayList lista_grupos_atributos;
                        ArrayList lista_metricas_registro;
                        ArrayList lista_marcas_registro;

                        lista_grupos_atributos = new ArrayList(((Registro)lista_registros.get(0)).lista_atributos);
                        lista_metricas_registro = new ArrayList(((Registro)lista_registros.get(0)).lista_metricas);
                        lista_marcas_registro = new ArrayList(((Registro)lista_registros.get(0)).lista_marcas);
                        this.nivel_raiz = this.inicializarArbol(lista_grupos_atributos, lista_metricas_registro, lista_marcas_registro);
                        this.cantidad_niveles = lista_grupos_atributos.size();
                        Nivel nivel_padre = null;
                        Nivel nuevo_nivel = null;
                        int nivel_padre_mas_bajo = (this.cantidad_niveles - 1);
                        
                        for(i=1; i < lista_registros.size(); i++)
                        {
                            lista_grupos_atributos = new ArrayList(((Registro)lista_registros.get(i)).lista_atributos);
                            lista_metricas_registro = new ArrayList(((Registro)lista_registros.get(i)).lista_metricas);
                            lista_marcas_registro = new ArrayList(((Registro)lista_registros.get(i)).lista_marcas);
                            nivel_padre = this.buscarPadreNivel(1, lista_grupos_atributos, this.nivel_raiz.lista_niveles, indice_ultimo_nivel);
                            if(nivel_padre.indice_nivel == nivel_padre_mas_bajo)
                            {
                                
                                nuevo_nivel = this.crearNivel( lista_grupos_atributos
                                                                , nivel_padre.indice_nivel
                                                                , nivel_padre
                                                                , lista_metricas_registro
                                                                , lista_marcas_registro
                                                                );

                                
                            }
                            else
                            {
                                
                                nuevo_nivel = this.crearNuevosNiveles(lista_grupos_atributos
                                                                        , lista_metricas_registro
                                                                        , lista_marcas_registro
                                                                        , nivel_padre
                                                                        , nivel_padre.indice_nivel
                                                                        , this.cantidad_niveles
                                                                      );
                               


                            }
                            nivel_padre.lista_niveles.add(nuevo_nivel);
     
                        }

                        
                        return this.nivel_raiz;

                }/// generarListaNiveles

                
                private Nivel crearNuevosNiveles( ArrayList lista_grupos_atributos
                                                    , ArrayList lista_metricas
                                                    , ArrayList lista_marcas
                                                    , Nivel nivel_padre
                                                    , int indice_nivel_padre
                                                    , int maximo_nivel)
                {
                    Nivel nuevo_nivel_1 = null;
                    Nivel nuevo_nivel_2 = null;
                    
                    int nuevo_indice = indice_nivel_padre + 1;

                    if( nuevo_indice == maximo_nivel)
                    {
                        nuevo_nivel_2 = this.crearNivel(lista_grupos_atributos, indice_nivel_padre, nivel_padre, lista_metricas, lista_marcas);

                        return nuevo_nivel_2;
                    }
                    else
                    {
                        nuevo_nivel_1 = this.crearNivel(lista_grupos_atributos, indice_nivel_padre, nivel_padre, null, null);
                        nuevo_nivel_2 = this.crearNuevosNiveles(lista_grupos_atributos
                                                                , lista_metricas
                                                                , lista_marcas
                                                                , nuevo_nivel_1
                                                                , nuevo_indice
                                                                , maximo_nivel);
                        
                    }

                    nuevo_nivel_1.lista_niveles.add(nuevo_nivel_2);
                    
                    return nuevo_nivel_1;

                }//crearNuevosNiveles


                private Nivel crearNivel(ArrayList lista_grupos_atributos
                                            , int indice
                                            , Nivel nivel_padre
                                            , ArrayList lista_metricas
                                            , ArrayList lista_marcas_registro
                                            )
                {
                        Nivel nuevo_nivel;
                        GrupoAtributos obj_ga = (GrupoAtributos)lista_grupos_atributos.get(indice);

                         nuevo_nivel = new Nivel();
                         nuevo_nivel.codigo = obj_ga.valor_codigo;
                         nuevo_nivel.nombre = obj_ga.valor_nombre;
                         nuevo_nivel.nombre_xml = obj_ga.nombre_xml;
                         nuevo_nivel.indice_nivel = indice + 1;
                         nuevo_nivel.padre = nivel_padre;

                         if(lista_metricas != null)
                            nuevo_nivel.lista_metricas_periodo = lista_metricas;

                         if(lista_marcas_registro != null)
                            nuevo_nivel.lista_marcas = lista_marcas_registro;
                         else
                            nuevo_nivel.lista_marcas = this.lista_marcas_con_valores_vacios;

                         return nuevo_nivel;

                }/////crearNivel
                

                private Nivel buscarPadreNivel(int indice_nivel , ArrayList lista_grupos_atributos, ArrayList lista_niveles, int indice_ultimo_nivel)
                {
                    Nivel obj_padre_nivel = null;
                    
                    GrupoAtributos obj_ga = (GrupoAtributos)lista_grupos_atributos.get(indice_nivel - 1);

                    for(int i = lista_niveles.size() - 1; i >=0; i--)
                    {
                        Nivel obj_nivel = (Nivel)lista_niveles.get(i);
                        if(!obj_nivel.codigo.equalsIgnoreCase(obj_ga.valor_codigo))
                            obj_padre_nivel = obj_nivel.padre;
                        else
                        {
                            if(indice_ultimo_nivel > 1 && obj_nivel.indice_nivel < indice_ultimo_nivel)
                            {
                                obj_padre_nivel = this.buscarPadreNivel(indice_nivel + 1 , lista_grupos_atributos, obj_nivel.lista_niveles, indice_ultimo_nivel);
                            }
                            else
                            {
                                if(!obj_nivel.nombre.equalsIgnoreCase(obj_ga.nombre))
                                {
                                    ///Este unico nivel tiene 2 codigos iguales Ejemplo Codigo Supervisor "No Asignado"
                                    obj_padre_nivel = obj_nivel.padre;
                                }
                                else
                                {
                                    ///Este unico nivel tiene 2 codigos con sus nombre iguales
                                    System.out.println("REVISAR....No se inserto objeto con codigo: " + obj_nivel.codigo + " nombre: " + obj_nivel.nombre  + "Verifique esta consulta de un solo nivel de jerarquia");
                                }
                            }
                        }
                        
                        if(obj_padre_nivel != null)
                        {
                           
                            i = -1000;
                        }
                    }
                    
                    return obj_padre_nivel;
                    
                } ///// buscarPadreNivel

                
                private Nivel inicializarArbol(ArrayList lista_grupo_atributos, ArrayList lista_metricas, ArrayList lista_marcas_registro)
                {
                        Nivel nivel;
                        Nivel obj_nivel_padre = this.crearNivelPadre();
                        Nivel obj_nivel_anterior = null;

                        int numero_niveles = lista_grupo_atributos.size();
                        for(int i = 0; i < numero_niveles; i++)
                        {
                            GrupoAtributos obj_ga = (GrupoAtributos)(lista_grupo_atributos.get(i));

                            nivel = new Nivel();
                            nivel.codigo = obj_ga.valor_codigo;
                            nivel.nombre = obj_ga.valor_nombre;
                            nivel.nombre_xml = obj_ga.nombre_xml;
                            nivel.indice_nivel = i + 1;
                            nivel.lista_marcas = this.lista_marcas_con_valores_vacios;

                            if( i == (numero_niveles - 1) )
                            {
                                nivel.lista_metricas_periodo = lista_metricas;
                                nivel.lista_marcas = lista_marcas_registro;
                                
                            }
                                                      
                            if(i == 0)
                            {
                                    nivel.padre = obj_nivel_padre;
                                    obj_nivel_padre.lista_niveles.add(nivel);
                            }
                            else
                            {
                                    nivel.padre = obj_nivel_anterior;
                                    obj_nivel_anterior.lista_niveles.add(nivel);
                            }
                            

                            obj_nivel_anterior = nivel;

                        }
                        
                      return obj_nivel_padre;
                }//// inicializarArbol

                private Nivel crearNivelPadre()
                {
                    
                              Nivel obj_nivel_padre = new Nivel();

                              obj_nivel_padre.codigo = "";
                              obj_nivel_padre.nombre = "TOTAL";
                              obj_nivel_padre.nombre_xml = "TOTAL";
                              obj_nivel_padre.padre = null;
                              obj_nivel_padre.indice_nivel = 0;
                              obj_nivel_padre.lista_marcas = this.lista_marcas_con_valores_vacios;

                              return obj_nivel_padre;
                              
                }///// crearNivelPadre

    /***********************************************************************************************************************
     *************************************************** FUNCIONES PARA GENERAR EL QUERY ***********************************
     ***********************************************************************************************************************
     ****/

     /**
     * SIS OPE REQ 2012 019
     **/

    public ArrayList ejecutarQuery(String query, boolean es_reporte_detalle_mensual)
    {
            Connection c = null;
            ArrayList lista_registros = new ArrayList();
            Registro registro;
            ArrayList lista_cabeceras = null;
            ////query = this.leerScriptSQL("query.sql");
            int columna = 0;
            boolean ultimo_atributo = false;
            ////int bandera_cantidad_registro = 0;

            this.lista_marcas_con_valores_vacios = new ArrayList();

            try
                    {
                        c = Conexion.getConnection();
                        Statement s = c.createStatement();
                        ResultSet rs = s.executeQuery(query);

                        lista_cabeceras = this.obtenerCabecerasQuery(rs);
                        int maximo_columnas = lista_cabeceras.size();
                        while (rs.next())
                        {
                            ////bandera_cantidad_registro++;
                            ////if(bandera_cantidad_registro == 903)
                            ////    System.out.println("STALIN STALIN");

                            

                            registro = new Registro();
                            columna = 0;
                            String nombre_columna = "";
                            String parte_nombre = "";
                            String valor_columna = "";
                            Double valor_columna_numerico = 0.00;
                            GrupoAtributos obj_atributo = null;
                            Metrica obj_metrica = null;
                            ultimo_atributo = false;
                            while(columna < maximo_columnas)
                            {
                                    if(columna == 0)
                                        obj_atributo = new GrupoAtributos();

                                    CampoResultSet obj_crs = (CampoResultSet)lista_cabeceras.get(columna);
                                    nombre_columna = obj_crs.nombre;

                                    parte_nombre = nombre_columna.split("_")[0];

                                    if( parte_nombre.equalsIgnoreCase("codigo") == true
                                            || parte_nombre.equalsIgnoreCase("folio") == true
                                        
                                       )
                                    {

                                        if(columna > 0)
                                        {
                                            if(obj_atributo.nombre == null)
                                            {
                                                ////obj_atributo.nombre = obj_atributo.codigo;
                                                obj_atributo.valor_nombre = obj_atributo.valor_codigo;
                                            }
                                            registro.lista_atributos.add(obj_atributo);
                                            obj_atributo = new GrupoAtributos();
                                        }

                                        if(obj_crs.tipo_dato == 1)
                                            valor_columna = rs.getString(nombre_columna);
                                        else
                                            valor_columna = String.valueOf(rs.getLong(nombre_columna));

                                        obj_atributo.codigo = nombre_columna;
                                        obj_atributo.valor_codigo = valor_columna;
                                        obj_atributo.nombre_xml = this.generarNombreXml(nombre_columna);

                                    }
                                    else
                                    {

                                        if(parte_nombre.equalsIgnoreCase("nombre") == true
                                                || parte_nombre.equalsIgnoreCase("razon") == true)
                                        {
                                            valor_columna = rs.getString(nombre_columna);

                                            obj_atributo.nombre = nombre_columna;
                                            obj_atributo.valor_nombre = valor_columna;
                                            obj_atributo.nombre_xml = this.generarNombreXml(nombre_columna);
                                        }
                                        else
                                        {
                                            if(parte_nombre.equalsIgnoreCase("tipo") == true)
                                            {
                                                ///DEBE DEFINIRSE DE LA SIGTE MANERA
                                                ///EJ: tipo_existencia_te;  flag_oferta_fo
                                                Marca marca = new Marca();
                                                      marca.valor = rs.getString(nombre_columna);
                                                      marca.nombre_xml = nombre_columna.split("_")[2];
                                                      registro.lista_marcas.add(marca);

                                                if(this.marca_vacia_tipo == null)
                                                {
                                                    this.marca_vacia_tipo = new Marca();
                                                    this.marca_vacia_tipo.valor = " ";
                                                    this.marca_vacia_tipo.nombre_xml = marca.nombre_xml;
                                                    this.lista_marcas_con_valores_vacios.add(this.marca_vacia_tipo);
                                                }
                                            }
                                            else
                                            {
                                                    if(!ultimo_atributo)
                                                    {
                                                        if(obj_atributo.nombre == null)
                                                        {
                                                            ///obj_atributo.nombre = obj_atributo.codigo;
                                                            obj_atributo.valor_nombre = obj_atributo.valor_codigo;
                                                        }
                                                        registro.lista_atributos.add(obj_atributo);
                                                    }

                                                    valor_columna_numerico = rs.getDouble(nombre_columna);
                                                    if(nombre_columna.indexOf("merma") != -1 || nombre_columna.indexOf("botado") != -1)
                                                        valor_columna_numerico = valor_columna_numerico * -1;

                                                    obj_metrica = new Metrica();
                                                    obj_metrica.nombre_compuesto = nombre_columna;
                                                    obj_metrica.valor = valor_columna_numerico;
                                                    String arreglo_nombres[] = nombre_columna.split("_");

                                                    if(es_reporte_detalle_mensual == true)
                                                    {
                                                        obj_metrica.nombre_metrica = arreglo_nombres[0] + "_" + arreglo_nombres[1];
                                                        obj_metrica.nombre_periodo = arreglo_nombres[2] + "-" + arreglo_nombres[3] + "-" + arreglo_nombres[4] ;
                                                    }
                                                    else
                                                    {
                                                        obj_metrica.nombre_metrica = arreglo_nombres[0] + "_" + arreglo_nombres[1] + "_" + arreglo_nombres[2];
                                                    }

                                                    registro.lista_metricas.add(obj_metrica);

                                                    ultimo_atributo = true;
                                            }///ELSE METRICA

                                        }///FIN FOLIO RAZON

                                    }///FIN ELSE

                                columna ++;

                            }

                            lista_registros.add(registro);
                        }
                        ///Para recuperar la memoria tomada por el query
                        /////c = Conexion.getConnection();
                        ////s = c.createStatement();
                        s.execute("select calflushcache()");
                        

                     }
                    catch (SQLException e)
                    {
                            System.out.println("Error en query de Reporte: "+e.toString());
                            lista_registros = null;
                    }
                    finally
                    {
                            Conexion.close(c);
                    }

            return lista_registros;
    }//// ejecutarQuery


    
    private String generarNombreXml(String nombre_columna_rs)
    {
            String nombre_xml = "";

            String arreglo[] = nombre_columna_rs.split("_");
            if(arreglo.length == 3)
                nombre_xml = arreglo[1] + "_"  + arreglo[2];
            else
                nombre_xml = arreglo[1];
                

            return nombre_xml;

    }//// generarNombreXml

    private ArrayList obtenerCabecerasQuery(ResultSet rs)
    {
            ArrayList lista_cabeceras = new ArrayList();
            try
            {
                ResultSetMetaData rs_meta_data =  rs.getMetaData();
                for(int i = 1; i <=  rs_meta_data.getColumnCount();  i++)
                {
                    String alias_campo = rs_meta_data.getColumnLabel(i);
                    String nombre_tipo_dato = rs_meta_data.getColumnTypeName(i);

                    ///System.out.println("tipo:" + nombre_tipo_dato);
                    
                    CampoResultSet crs = new CampoResultSet();

                    crs.nombre = alias_campo;
                    crs.nombre_tipo_dato = nombre_tipo_dato;
                    if(nombre_tipo_dato.equalsIgnoreCase("VARCHAR"))
                        crs.tipo_dato = 1;
                    else
                        crs.tipo_dato = 2;
                    
                    
                    lista_cabeceras.add(crs);
                }
            }
            catch (Exception e)
            {
                System.out.println("Error en extraer cabeceras del ResultSet: "+e.toString());
            }
            


            return lista_cabeceras;
    }


    private ArrayList obtenerValoresEnviadosFiltrosFlex(ArrayCollection arreglo_elementos)
    {
            ArrayList lista_filtros_tmp = new ArrayList();

            for(int i = 0; i < arreglo_elementos.size(); i++)
            {
                    ObjetoFiltroJava obj_filtro_java = (ObjetoFiltroJava)arreglo_elementos.get(i);
                    /*System.out.println(" label:: " + obj_filtro_java.label
                                        + " data:: " + obj_filtro_java.data
                                        + " filtro:: " + obj_filtro_java.filtro
                                        + " tabla:: " + obj_filtro_java.tabla
                                        + " grupo_checkbox:: " + obj_filtro_java.grupo_checkbox
                                        );*/
                    //String label = (String)obj_filtro_java.label;
                    //String data = (String)obj_filtro_java.data;
                    ObjetoFiltroFlex obj_filtro_flex = obtenerCampoTabla(obj_filtro_java);
                    lista_filtros_tmp.add(obj_filtro_flex);
            }
            
            /*System.out.println("-----------------------------------------------");
            System.out.println("-----------------------------------------------");*/
            return lista_filtros_tmp;
    }////obtenerValoresFiltrosDesdeFlex


    

    private ObjetoFiltroFlex obtenerCampoTabla(ObjetoFiltroJava obj_filtro_java)
    {
          ObjetoFiltroFlex obj_filtro_flex = null;

          String label_flex = obj_filtro_java.label;
          String data_flex = obj_filtro_java.data;
          String filtro = obj_filtro_java.filtro;
          int tabla = obj_filtro_java.tabla;


          obj_filtro_flex = this.obtenerCampoTablaParaFiltroJerarquia(label_flex, data_flex);
          if(obj_filtro_flex == null)
          {
              obj_filtro_flex = this.obtenerCampoTablaParaMetrica(label_flex, data_flex);
              if(obj_filtro_flex == null)
              {
                  ///obj_filtro_flex = this.obtenerCampoTablaParaFiltrosAdicionales(label_flex, data_flex);
                  obj_filtro_flex = this.obtenerCampoTablaParaFiltrosAdicionales(obj_filtro_java);
                  if(obj_filtro_flex == null)
                     obj_filtro_flex = this.obtenerFechas(label_flex, data_flex);
              }
          }
                                                      
        return obj_filtro_flex;

    }// obtenerCampoTabla


    private ObjetoFiltroFlex obtenerCampoTablaParaFiltroJerarquia(String label_flex, String data_flex)
    {
          ObjetoFiltroFlex obj_filtro_flex = null;

          if(label_flex.equalsIgnoreCase("e1"))
          {
              this.existe_nivel_estadistico = true;
              ///---  SIS SIS REQ 2012 014  ---///
              ///-obj_filtro_flex = new ObjetoFiltroFlex("cod_estadistico", data_flex);
              ///---  SIS SIS REQ 2013 055  ---///
              ///-obj_filtro_flex = new ObjetoFiltroFlex("cod_estadistico_tia", data_flex);
              obj_filtro_flex = new ObjetoFiltroFlex("cod_estadistico_tia", this.obtenerCodigoEstadistico(data_flex));

              ///---  FIN SIS SIS REQ 2012 014  ---///
              obj_filtro_flex.label = label_flex;
              obj_filtro_flex.alias_campo_tabla = "codigo_estadistico";
              obj_filtro_flex.campo_nombre_tabla = "nom_producto";
              ////////obj_filtro_flex.alias_campo_nombre_tabla = "descripcion";
              obj_filtro_flex.alias_campo_nombre_tabla = "nombre_estadistico";
              ///obj_filtro_flex.es_tipo_numerico = true;
              obj_filtro_flex.alias_dimension = this.alias_dimension_estadistico;
              obj_filtro_flex.es_filtro_dimension = true;
          }
          else
          {
              if(label_flex.equalsIgnoreCase("e2"))
              {
                  obj_filtro_flex = new ObjetoFiltroFlex("cod_subfamilia", data_flex);
                  obj_filtro_flex.label = label_flex;
                  obj_filtro_flex.alias_campo_tabla = "codigo_subfamilia";
                  obj_filtro_flex.campo_nombre_tabla = "nom_subfamilia";
                  obj_filtro_flex.alias_campo_nombre_tabla = "nombre_subfamilia";
                  ///obj_filtro_flex.es_tipo_numerico = true;
                  obj_filtro_flex.alias_dimension = this.alias_dimension_estadistico;
                  obj_filtro_flex.es_filtro_dimension = true;
              }
              else
              {
                  if(label_flex.equalsIgnoreCase("e3"))
                  {
                      
                        obj_filtro_flex = new ObjetoFiltroFlex("cod_familia", data_flex);
                        obj_filtro_flex.label = label_flex;
                        obj_filtro_flex.alias_campo_tabla = "codigo_familia";
                        obj_filtro_flex.campo_nombre_tabla = "nom_familia";
                        obj_filtro_flex.alias_campo_nombre_tabla = "nombre_familia";
                        ///obj_filtro_flex.es_tipo_numerico = true;
                        obj_filtro_flex.alias_dimension = this.alias_dimension_estadistico;
                        obj_filtro_flex.es_filtro_dimension = true;
                  }
                  else
                  {
                      if(label_flex.equalsIgnoreCase("e4"))
                      {
                            obj_filtro_flex = new ObjetoFiltroFlex("cod_seccion", data_flex);
                            obj_filtro_flex.label = label_flex;
                            obj_filtro_flex.alias_campo_tabla = "codigo_seccion";
                            obj_filtro_flex.campo_nombre_tabla = "nom_seccion";
                            obj_filtro_flex.alias_campo_nombre_tabla = "nombre_seccion";
                            ///obj_filtro_flex.es_tipo_numerico = true;
                            obj_filtro_flex.alias_dimension = this.alias_dimension_estadistico;
                            obj_filtro_flex.es_filtro_dimension = true;
                      }
                      else
                      {
                          if(label_flex.equalsIgnoreCase("e5"))
                          {
                                obj_filtro_flex = new ObjetoFiltroFlex("sector", data_flex);
                                obj_filtro_flex.label = label_flex;
                                obj_filtro_flex.alias_campo_tabla = "codigo_sector";
                                obj_filtro_flex.campo_nombre_tabla = "sector";
                                obj_filtro_flex.alias_campo_nombre_tabla = "nombre_sector";
                                obj_filtro_flex.es_tipo_numerico = false;
                                obj_filtro_flex.tiene_campo_nombre = false;
                                obj_filtro_flex.alias_dimension = this.alias_dimension_estadistico;
                                obj_filtro_flex.es_filtro_dimension = true;
                          }
                          else
                          {
                              if(label_flex.equalsIgnoreCase("p1"))
                              {
                                    obj_filtro_flex = new ObjetoFiltroFlex("id_proveedor", data_flex);
                                    obj_filtro_flex.label = label_flex;
                                    obj_filtro_flex.alias_campo_tabla = "folio_secundario";
                                    obj_filtro_flex.campo_nombre_tabla = "razon_social";
                                    obj_filtro_flex.alias_campo_nombre_tabla = "razon_social";
                                    ///obj_filtro_flex.es_tipo_numerico = true;
                                    obj_filtro_flex.alias_dimension = this.alias_dimension_proveedor;
                                    obj_filtro_flex.es_filtro_dimension = true;
                              }
                              else
                              {
                                  if(label_flex.equalsIgnoreCase("p2"))
                                  {
                                        obj_filtro_flex = new ObjetoFiltroFlex("folio_principal", data_flex);
                                        obj_filtro_flex.label = label_flex;
                                        obj_filtro_flex.alias_campo_tabla = "folio_principal";
                                        obj_filtro_flex.campo_nombre_tabla = "razon_social_prin";
                                        obj_filtro_flex.alias_campo_nombre_tabla = "razon_social_principal";
                                        ///obj_filtro_flex.es_tipo_numerico = true;
                                        obj_filtro_flex.alias_dimension = this.alias_dimension_proveedor;
                                        obj_filtro_flex.es_filtro_dimension = true;
                                  }
                                  else
                                  {
                                      if(label_flex.equalsIgnoreCase("s1"))
                                      {
                                            obj_filtro_flex = new ObjetoFiltroFlex("cod_sucursal", data_flex);
                                            obj_filtro_flex.label = label_flex;
                                            obj_filtro_flex.alias_campo_tabla = "codigo_sucursal";
                                            obj_filtro_flex.campo_nombre_tabla = "nom_sucursal";
                                            obj_filtro_flex.alias_campo_nombre_tabla = "nombre_sucursal";
                                            ///obj_filtro_flex.es_tipo_numerico = true;
                                            obj_filtro_flex.alias_dimension = this.alias_dimension_sucursal;
                                            obj_filtro_flex.es_filtro_dimension = true;
                                      }
                                      else
                                      {
                                          if(label_flex.equalsIgnoreCase("s2"))
                                          {
                                                obj_filtro_flex = new ObjetoFiltroFlex("formato", data_flex);
                                                obj_filtro_flex.label = label_flex;
                                                obj_filtro_flex.alias_campo_tabla = "codigo_formato";
                                                obj_filtro_flex.campo_nombre_tabla = "formato";
                                                obj_filtro_flex.alias_campo_nombre_tabla = "nombre_formato";
                                                obj_filtro_flex.es_tipo_numerico = false;
                                                obj_filtro_flex.tiene_campo_nombre = false;
                                                obj_filtro_flex.alias_dimension = this.alias_dimension_sucursal;
                                                obj_filtro_flex.es_filtro_dimension = true;
                                          }
                                          else
                                          {
                                              if(label_flex.equalsIgnoreCase("s3"))
                                              {
                                                    obj_filtro_flex = new ObjetoFiltroFlex("cod_supervisor", data_flex);
                                                    obj_filtro_flex.label = label_flex;
                                                    obj_filtro_flex.alias_campo_tabla = "codigo_supervisor_zonal";
                                                    obj_filtro_flex.campo_nombre_tabla = "supervisor";
                                                    obj_filtro_flex.alias_campo_nombre_tabla = "nombre_supervisor_zonal";
                                                    obj_filtro_flex.es_tipo_numerico = false;
                                                    obj_filtro_flex.alias_dimension = this.alias_dimension_sucursal;
                                                    obj_filtro_flex.es_filtro_dimension = true;
                                              }
                                              else
                                              {
                                                  if(label_flex.equalsIgnoreCase("s4"))
                                                  {
                                                        obj_filtro_flex = new ObjetoFiltroFlex("centro_distribucion", data_flex);
                                                        obj_filtro_flex.label = label_flex;
                                                        obj_filtro_flex.alias_campo_tabla = "codigo_centro_distribucion";
                                                        obj_filtro_flex.campo_nombre_tabla = "centro_distribucion";
                                                        obj_filtro_flex.alias_campo_nombre_tabla = "nombre_centro_distribucion";
                                                        obj_filtro_flex.es_tipo_numerico = false;
                                                        obj_filtro_flex.tiene_campo_nombre = false;
                                                        obj_filtro_flex.alias_dimension = this.alias_dimension_sucursal;
                                                        obj_filtro_flex.es_filtro_dimension = true;
                                                  }
                                                  else
                                                  {
                                                      if(label_flex.equalsIgnoreCase("s5"))
                                                      {
                                                            obj_filtro_flex = new ObjetoFiltroFlex("cod_subgerente", data_flex);
                                                            obj_filtro_flex.label = label_flex;
                                                            obj_filtro_flex.alias_campo_tabla = "codigo_gerente_regional";
                                                            obj_filtro_flex.campo_nombre_tabla = "subgerente_zonal";
                                                            obj_filtro_flex.alias_campo_nombre_tabla = "nombre_gerente_regional";
                                                            obj_filtro_flex.es_tipo_numerico = false;
                                                            obj_filtro_flex.alias_dimension = this.alias_dimension_sucursal;
                                                            obj_filtro_flex.es_filtro_dimension = true;
                                                      }

                                                  }
                                              }
                                          }

                                      }

                                  }

                              }

                          }

                      }
                  }
              }
          }



        return obj_filtro_flex;
    }///FIN obtenerCampoTablaParaFiltroJerarquia

    private String obtenerCodigoEstadistico(String codigo_estadistico)
    {
        String nuevo_codigo="";
        int cantidad_digitos = codigo_estadistico.length();
        if(cantidad_digitos == 8 || cantidad_digitos == 9)
            nuevo_codigo = codigo_estadistico;
        else
            nuevo_codigo = codigo_estadistico.substring(0, cantidad_digitos - 2);

        return nuevo_codigo;
    }///FIN FIN obtenerCodigoEstadistico
    
    /*
     * SIS OPE REQ 2012 019: Se agrego las metricas de  ajuste y botado a la basura
     * SE  DEBE COLOCAR UN NOMBRE DE 2 PALABRAS CONCATENADAS CON SUBGUION PORQUE SE UTILIZA DICHO FORMATO
     * EN LA FUNCION "ejecutarQuery"
     *
     */
    private ObjetoFiltroFlex obtenerCampoTablaParaMetrica(String label_flex, String data_flex)
    {
        ObjetoFiltroFlex obj_filtro_flex = null;

        if(data_flex.equalsIgnoreCase("e_unid"))
        {
            obj_filtro_flex = new ObjetoFiltroFlex("unidades","");
            obj_filtro_flex.label = label_flex;
            obj_filtro_flex.alias_campo_tabla = "entradas_unidades";
            obj_filtro_flex.tipo_tabla = 1;
        }
        else
        {
            if(data_flex.equalsIgnoreCase("e_pcsi"))
            {
                obj_filtro_flex = new ObjetoFiltroFlex("tot_costo_sin_iva","");
                obj_filtro_flex.label = label_flex;
                obj_filtro_flex.alias_campo_tabla = "entradas_pcsi";
                obj_filtro_flex.tipo_tabla = 1;
            }
            else
            {
                if(data_flex.equalsIgnoreCase("e_pvci"))
                {
                    obj_filtro_flex = new ObjetoFiltroFlex("tot_precio_con_iva","");
                    obj_filtro_flex.label = label_flex;
                    obj_filtro_flex.alias_campo_tabla = "entradas_pvci";
                    obj_filtro_flex.tipo_tabla = 1;
                }
                else
                {
                    if(data_flex.equalsIgnoreCase("e_pvsi"))
                    {
                        obj_filtro_flex = new ObjetoFiltroFlex("tot_precio_sin_iva","");
                        obj_filtro_flex.label = label_flex;
                        obj_filtro_flex.alias_campo_tabla = "entradas_pvsi";
                        obj_filtro_flex.tipo_tabla = 1;
                    }
                    else
                    {
                        if(data_flex.equalsIgnoreCase("c_unid"))
                        {
                            obj_filtro_flex = new ObjetoFiltroFlex("fc_consumo_unid","");
                            obj_filtro_flex.label = label_flex;
                            obj_filtro_flex.alias_campo_tabla = "consumo_unidades";
                            obj_filtro_flex.tipo_tabla = 2;
                        }
                        else
                        {
                            if(data_flex.equalsIgnoreCase("c_pcsi"))
                            {
                                obj_filtro_flex = new ObjetoFiltroFlex("fc_consumo_ult_cos","");
                                obj_filtro_flex.label = label_flex;
                                obj_filtro_flex.alias_campo_tabla = "consumo_pcsi";
                                obj_filtro_flex.tipo_tabla = 2;
                            }
                            else
                            {
                                if(data_flex.equalsIgnoreCase("c_pvci"))
                                {
                                    obj_filtro_flex = new ObjetoFiltroFlex("fc_consumo_pvp","");
                                    obj_filtro_flex.label = label_flex;
                                    obj_filtro_flex.alias_campo_tabla = "consumo_pvci";
                                    obj_filtro_flex.tipo_tabla = 2;
                                }
                                else
                                {
                                    if(data_flex.equalsIgnoreCase("c_pvsi"))
                                    {
                                        obj_filtro_flex = new ObjetoFiltroFlex("fc_consumo_pre_sin_iva","");
                                        obj_filtro_flex.label = label_flex;
                                        obj_filtro_flex.alias_campo_tabla = "consumo_pvsi";
                                        obj_filtro_flex.tipo_tabla = 2;
                                    }
                                    else
                                    {
                                        if(data_flex.equalsIgnoreCase("g_pvsi"))
                                        {
                                            obj_filtro_flex = new ObjetoFiltroFlex("fc_ganancia_vta_neta","");
                                            obj_filtro_flex.label = label_flex;
                                            obj_filtro_flex.alias_campo_tabla = "ganancia_pvsi";
                                            obj_filtro_flex.tipo_tabla = 2;
                                        }
                                        else
                                        {
                                            if(data_flex.equalsIgnoreCase("s_unid"))
                                            {
                                                obj_filtro_flex = new ObjetoFiltroFlex("fc_stock_unid","");
                                                obj_filtro_flex.label = label_flex;
                                                obj_filtro_flex.alias_campo_tabla = "stock_unidades";
                                                obj_filtro_flex.tipo_tabla = 2;
                                                obj_filtro_flex.sumarizado_tiempo = false;
                                            }
                                            else
                                            {
                                                if(data_flex.equalsIgnoreCase("s_pcsi"))
                                                {
                                                    obj_filtro_flex = new ObjetoFiltroFlex("fc_stock_ult_cos","");
                                                    obj_filtro_flex.label = label_flex;
                                                    obj_filtro_flex.alias_campo_tabla = "stock_pcsi";
                                                    obj_filtro_flex.tipo_tabla = 2;
                                                    obj_filtro_flex.sumarizado_tiempo = false;
                                                }
                                                else
                                                {
                                                    if(data_flex.equalsIgnoreCase("s_pvci"))
                                                    {
                                                        obj_filtro_flex = new ObjetoFiltroFlex("fc_stock_pvp","");
                                                        obj_filtro_flex.label = label_flex;
                                                        obj_filtro_flex.alias_campo_tabla = "stock_pvci";
                                                        obj_filtro_flex.tipo_tabla = 2;
                                                        obj_filtro_flex.sumarizado_tiempo = false;
                                                    }
                                                    else
                                                    {
                                                        if(data_flex.equalsIgnoreCase("s_pvsi"))
                                                        {
                                                            obj_filtro_flex = new ObjetoFiltroFlex("fc_stock_pre_sin_iva","");
                                                            obj_filtro_flex.label = label_flex;
                                                            obj_filtro_flex.alias_campo_tabla = "stock_pvsi";
                                                            obj_filtro_flex.tipo_tabla = 2;
                                                            obj_filtro_flex.sumarizado_tiempo = false;
                                                        }
                                                        else
                                                        {
                                                            if(data_flex.equalsIgnoreCase("m_unid"))
                                                            {
                                                                obj_filtro_flex = new ObjetoFiltroFlex("fc_ajustes_unid","");
                                                                obj_filtro_flex.label = label_flex;
                                                                obj_filtro_flex.alias_campo_tabla = "merma_unidades";
                                                                obj_filtro_flex.tipo_tabla = 2;
                                                            }
                                                            else
                                                            {
                                                                if(data_flex.equalsIgnoreCase("m_pcsi"))
                                                                {
                                                                    obj_filtro_flex = new ObjetoFiltroFlex("fc_ajustes_ult_cos","");
                                                                    obj_filtro_flex.label = label_flex;
                                                                    obj_filtro_flex.alias_campo_tabla = "merma_pcsi";
                                                                    obj_filtro_flex.tipo_tabla = 2;
                                                                }
                                                                else
                                                                {
                                                                    if(data_flex.equalsIgnoreCase("m_pvci"))
                                                                    {
                                                                        obj_filtro_flex = new ObjetoFiltroFlex("fc_ajustes_pvp","");
                                                                        obj_filtro_flex.label = label_flex;
                                                                        obj_filtro_flex.alias_campo_tabla = "merma_pvci";
                                                                        obj_filtro_flex.tipo_tabla = 2;
                                                                    }
                                                                    else
                                                                    {
                                                                        if(data_flex.equalsIgnoreCase("m_pvsi"))
                                                                        {
                                                                            obj_filtro_flex = new ObjetoFiltroFlex("fc_ajustes_pre_sin_iva","");
                                                                            obj_filtro_flex.label = label_flex;
                                                                            obj_filtro_flex.alias_campo_tabla = "merma_pvsi";
                                                                            obj_filtro_flex.tipo_tabla = 2;
                                                                        }
                                                                        else
                                                                        {
                                                                            if(data_flex.equalsIgnoreCase("b_unid"))
                                                                            {
                                                                                obj_filtro_flex = new ObjetoFiltroFlex("fc_botadas_unid","");
                                                                                obj_filtro_flex.label = label_flex;
                                                                                obj_filtro_flex.alias_campo_tabla = "botado_unidades";
                                                                                obj_filtro_flex.tipo_tabla = 2;
                                                                            }
                                                                            else
                                                                            {
                                                                                if(data_flex.equalsIgnoreCase("b_pcsi"))
                                                                                {
                                                                                    obj_filtro_flex = new ObjetoFiltroFlex("fc_botadas_ult_cos","");
                                                                                    obj_filtro_flex.label = label_flex;
                                                                                    obj_filtro_flex.alias_campo_tabla = "botado_pcsi";
                                                                                    obj_filtro_flex.tipo_tabla = 2;
                                                                                }
                                                                                else
                                                                                {
                                                                                    if(data_flex.equalsIgnoreCase("b_pvci"))
                                                                                    {
                                                                                        obj_filtro_flex = new ObjetoFiltroFlex("fc_botadas_pvp","");
                                                                                        obj_filtro_flex.label = label_flex;
                                                                                        obj_filtro_flex.alias_campo_tabla = "botado_pvci";
                                                                                        obj_filtro_flex.tipo_tabla = 2;
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        if(data_flex.equalsIgnoreCase("b_pvsi"))
                                                                                        {
                                                                                            obj_filtro_flex = new ObjetoFiltroFlex("fc_botadas_pre_sin_iva","");
                                                                                            obj_filtro_flex.label = label_flex;
                                                                                            obj_filtro_flex.alias_campo_tabla = "botado_pvsi";
                                                                                            obj_filtro_flex.tipo_tabla = 2;
                                                                                        }
                                                                                    }
                                                                                }

                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                }

            }

        }


        return obj_filtro_flex;
    }


    
    private ObjetoFiltroFlex obtenerCampoTablaParaFiltrosAdicionales(ObjetoFiltroJava obj_filtro_java)
    {
        
        ObjetoFiltroFlex obj_filtro_flex = null;

        String label_flex = obj_filtro_java.label;
        String data_flex = obj_filtro_java.data;
        String tipo_filtro  = obj_filtro_java.filtro;
        int grupo_checkbox = obj_filtro_java.grupo_checkbox;
        int tipo_tabla = obj_filtro_java.tabla;


            if(tipo_filtro.equalsIgnoreCase("existe_maestro"))
            {

                obj_filtro_flex = new ObjetoFiltroFlex("fc_existe_maestro", data_flex);
                obj_filtro_flex.campo_tabla_entradas = "existe_maestro";
                obj_filtro_flex.label = label_flex;
                obj_filtro_flex.alias_campo_tabla = "es_existe_maestro";
                obj_filtro_flex.tiene_campo_nombre = false;
                obj_filtro_flex.es_multiple_checkbox = true;
                obj_filtro_flex.indice_multiple_checkbox = grupo_checkbox;
                obj_filtro_flex.tipo_tabla = tipo_tabla;
                
            }
            else
            {
                    if(tipo_filtro.equalsIgnoreCase("tipo_empresa"))
                    {

                            obj_filtro_flex = new ObjetoFiltroFlex("fc_tipo_empresa", data_flex);
                            obj_filtro_flex.campo_tabla_entradas = "tipo_empresa";
                            obj_filtro_flex.label = label_flex;
                            obj_filtro_flex.alias_campo_tabla = "es_tipo_empresa";
                            obj_filtro_flex.tiene_campo_nombre = false;
                            obj_filtro_flex.es_multiple_checkbox = true;
                            obj_filtro_flex.indice_multiple_checkbox = grupo_checkbox;
                            obj_filtro_flex.tipo_tabla = tipo_tabla;
                    }
                    else
                    {
                            if(tipo_filtro.equalsIgnoreCase("tipo_exclusion"))
                            {


                                    obj_filtro_flex = new ObjetoFiltroFlex("fc_es_exclusion", data_flex);
                                    obj_filtro_flex.campo_tabla_entradas = "es_exclusion";
                                    obj_filtro_flex.label = label_flex;
                                    obj_filtro_flex.alias_campo_tabla = "es_intencion_exclusion";
                                    obj_filtro_flex.tiene_campo_nombre = false;
                                    obj_filtro_flex.es_tipo_numerico = false;
                                    obj_filtro_flex.es_multiple_checkbox = true;
                                    obj_filtro_flex.indice_multiple_checkbox = grupo_checkbox;
                                    obj_filtro_flex.tipo_tabla = tipo_tabla;
                            }
                            else
                            {
                                    if(tipo_filtro.equalsIgnoreCase("folio_interno"))
                                    {
                                        obj_filtro_flex = new ObjetoFiltroFlex("", data_flex);
                                        obj_filtro_flex.campo_tabla_entradas = "es_folio_interno";
                                        obj_filtro_flex.label = label_flex;
                                        obj_filtro_flex.alias_campo_tabla = "es_folio_interno";
                                        obj_filtro_flex.tiene_campo_nombre = false;
                                        obj_filtro_flex.es_multiple_checkbox = true;
                                        obj_filtro_flex.indice_multiple_checkbox = grupo_checkbox;
                                        obj_filtro_flex.tipo_tabla = tipo_tabla;
                                    }
                                    else
                                    {
                                        if(tipo_filtro.equalsIgnoreCase("marca_propia"))
                                        {

                                                obj_filtro_flex = new ObjetoFiltroFlex("es_marca_propia", data_flex);
                                                obj_filtro_flex.label = label_flex;
                                                obj_filtro_flex.alias_campo_tabla = "es_marca_propia";
                                                obj_filtro_flex.es_filtro_dimension = true;
                                                obj_filtro_flex.es_tipo_numerico = false;
                                                obj_filtro_flex.tiene_campo_nombre = false;
                                                obj_filtro_flex.es_multiple_checkbox = true;
                                                obj_filtro_flex.indice_multiple_checkbox = grupo_checkbox;
                                                obj_filtro_flex.tipo_tabla = tipo_tabla;
                                        }
                                    }
                            }
                    }
            }



        return obj_filtro_flex;


    }/// FIN obtenerCampoTablaParaFiltrosAdicionales

    
    private ObjetoFiltroFlex obtenerFechas(String label_flex, String data_flex)
    {

            ObjetoFiltroFlex obj_filtro_flex = null;

             ////System.out.println("valor filtro : " + data_flex + " pepe: " + label_flex );
             obj_filtro_flex = new ObjetoFiltroFlex("fecha", data_flex);
             obj_filtro_flex.label = label_flex;
             obj_filtro_flex.alias_campo_tabla = "fecha";
             obj_filtro_flex.tiene_campo_nombre = false;
             obj_filtro_flex.es_tipo_numerico = false;

            ////System.out.println("valor filtro : " + data_flex + " campo base: " + obj_filtro_flex.campo_tabla );

            return obj_filtro_flex;

    }/// FIN obtenerFechas


    
    private ArrayList generarArregloFechasParaQueryReporte(ArrayList arreglo_fechas)
    {
        
            ArrayList lista = new ArrayList();

            ///-this.fecha_maxima_diaria = this.obtenerFechaMaximaDiaria(this.lista_periodos_fechas);
            Fecha obj_fecha_actual = this.verSiEstaMesActual(arreglo_fechas);
                                  ///boolean escoger_dia_maximo_carga = false;
            boolean esta_mes_actual = false;
            if(obj_fecha_actual != null)
            {
                ArrayList arreglo_list_tmp = new ArrayList();
                          arreglo_list_tmp.add(obj_fecha_actual);
                Fecha fecha_tmp = obtenerFechaMaximaDiaria(arreglo_list_tmp);
                //// System.out.println(fecha_tmp.getFecha() + " -- " + fecha_tmp.getFecha_anterior());
                this.dia_maximo_carga_actual_comparativo = Integer.parseInt(fecha_tmp.getFecha().substring(8));
                this.dia_maximo_carga_anterior_comparativo = Integer.parseInt(fecha_tmp.getFecha_anterior().substring(8));
                ///escoger_dia_maximo_carga = true;
                esta_mes_actual = true;
            }

            
            int cantidad_fechas = arreglo_fechas.size();
            for(int i=0; i < cantidad_fechas; i++)
            {
                
                ObjetoFiltroFlex  obj = (ObjetoFiltroFlex)arreglo_fechas.get(i);
                String[] arreglo_tmp =  obj.valor.split("-");
                ////System.out.println(obj.valor + obj.label);
                Fecha fecha = new Fecha();
                      fecha.setAnio(arreglo_tmp[0]);
                      fecha.setMes(arreglo_tmp[1]);
                      fecha.setDia(arreglo_tmp[2]);
                      fecha.setFecha(obj.valor);



                      
                      fecha.setFecha_ultimo_dia(
                                                    fecha.calcularFechaUltimoDia
                                                    (
                                                     fecha.getFecha()
                                                     , esta_mes_actual
                                                     , this.dia_maximo_carga_actual_comparativo
                                                    )
                                                );

                     String fecha_anterior = fecha.obtenereFechaMensualAnterior(obj.valor);
                            fecha.setAnio_anterior(fecha_anterior.substring(0,4));
                            fecha.setMes_anterior(fecha_anterior.substring(5,7));
                            fecha.setDia_anterior(fecha_anterior.substring(8));
                            fecha.setFecha_anterior(fecha_anterior);


                            fecha.setFecha_anterior_ultimo_dia
                                                    (
                                                        fecha.calcularFechaUltimoDia
                                                                    (
                                                                        fecha.getFecha_anterior()
                                                                        , esta_mes_actual
                                                                        , dia_maximo_carga_anterior_comparativo
                                                                    )
                                                     );
                    
                    lista.add(fecha);
            }

            return lista;


    }/// FIN generarArregloFechasParaComparativo

   private Fecha verSiEstaMesActual(ArrayList arreglo_fechas)
   {
            Fecha fecha_mes_actual = null;

            Calendar calendario_dia_hoy = Calendar.getInstance();
            int dia_hoy = calendario_dia_hoy.get(Calendar.DAY_OF_MONTH) - 1;
            int mes_hoy = calendario_dia_hoy.get(Calendar.MONTH) + 1;
            int anio_hoy= calendario_dia_hoy.get(Calendar.YEAR);

            int cantidad_fechas = arreglo_fechas.size();
            for(int i=0; i < cantidad_fechas; i++)
            {

                ObjetoFiltroFlex  obj = (ObjetoFiltroFlex)arreglo_fechas.get(i);
                String[] arreglo_tmp =  obj.valor.split("-");
                int anio = Integer.parseInt(arreglo_tmp[0]);
                int mes = Integer.parseInt(arreglo_tmp[1]);
                int dia = Integer.parseInt(arreglo_tmp[2]);


                if( (anio == anio_hoy) && (mes == mes_hoy) )

                {
                      fecha_mes_actual = new Fecha();
                      fecha_mes_actual.setAnio(arreglo_tmp[0]);
                      fecha_mes_actual.setMes(arreglo_tmp[1]);
                      fecha_mes_actual.setDia(arreglo_tmp[2]);
                      fecha_mes_actual.setFecha(obj.valor);
                      i = cantidad_fechas + 1000;
                }

            }
        return fecha_mes_actual;

   }////FIN FIN verSiEstaMesActual



    private Fecha obtenerFechaMaximaDiaria(ArrayList arreglo_fechas)
    {
        Fecha fecha = null;
        String ultimo_mes;
        Connection c = null;
        String query;


        ultimo_mes = ( (Fecha)arreglo_fechas.get(arreglo_fechas.size() - 1)).getFecha();


        query = "  select t1.fecha_maxima_actual    as fecha_maxima_actual \n";
        query += "      , date_add(t1.fecha_maxima_actual, interval -1 year)    as fecha_maxima_anterior \n";
        query += " from \n";
        query += "  ( " ;
        query += "      select max(fc_fecha) as fecha_maxima_actual \n";
        query += "      from bi_dwh.dw_iproduct_fact \n ";
        query += "      where fc_fecha >= date_format('" + ultimo_mes + "','%Y-%m-01') \n";
        query += "              and fc_fecha <= last_day('" +  ultimo_mes +"') \n";
        query += "  ) t1 ";

        ///-System.out.println(query);

        try
        {
            c = Conexion.getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next())
            {
                fecha  = new Fecha();
                fecha.setFecha(rs.getString("fecha_maxima_actual"));
                fecha.setFecha_anterior(rs.getString("fecha_maxima_anterior"));
            }


         }
        catch (SQLException e)
         {
                System.out.println("Error en query de obtener fechas diarias maximas: "+e.toString());
         } finally {
                Conexion.close(c);
         }


        return fecha;


    }//// FIN obtenerFechaMaximaDiaria



    private String generarQueryReporteDetalle()
    {
            String query = "";
            String alias_tabla_entrada = "te";
            String alias_tabla_producto = "tp";
            String alias_tabla_right = "t_right";
            String alias_tabla_final = "t_final";
            ArrayList arreglo_filtros = lista_filtros;
            ArrayList arreglo_filtros_adicionales = lista_filtros_adicionales;
            ArrayList arreglo_niveles = lista_niveles;
            ArrayList arreglo_metricas = lista_metricas;
            Fecha fecha_diaria_maxima =  fecha_maxima_diaria;
            ArrayList arreglo_fechas = lista_periodos_fechas;
            int cantidad_espacios = 12;
            


            this.lista_metricas_periodo =  this.generarListaMetricasPeriodo(arreglo_fechas, arreglo_metricas);


            String query_entradas = this.generarQueryEntradas(  arreglo_filtros_adicionales
                                                                , arreglo_metricas
                                                                , arreglo_fechas
                                                                , fecha_diaria_maxima
                                                                , cantidad_espacios
                                                                );

            String query_iproduct = this.queryQueryIproduct(  arreglo_filtros_adicionales
                                                                , arreglo_metricas
                                                                , arreglo_fechas
                                                                , fecha_diaria_maxima
                                                                , cantidad_espacios
                                                                );


            /////String query_select_group_by = this.generarCamposSelectGroupByFinal(arreglo_niveles, 1 ,cantidad_espacios - 8);
            
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " SELECT \n" + this.generarCamposSelectGroupByFinal(arreglo_niveles, 1 ,cantidad_espacios - 8);
            query += this.generarEspaciosTab(cantidad_espacios - 8) + "          " + this.generarQueryMetricasNivel_4(this.lista_metricas_periodo, alias_tabla_final, cantidad_espacios - 8) + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " FROM ( \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " SELECT " + alias_tabla_entrada + ".codigo_proveedor     as codigo_proveedor     \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "        , " + alias_tabla_entrada + ".codigo_sucursal    as codigo_sucursal      \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "        , " + alias_tabla_entrada + ".codigo_estadistico as codigo_estadistico   \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "         " + this.generarQueryMetricasNivel_3(
                                                                                                        this.lista_metricas_periodo
                                                                                                        , false
                                                                                                        , alias_tabla_right
                                                                                                        , alias_tabla_entrada
                                                                                                        , alias_tabla_producto
                                                                                                        , cantidad_espacios);
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " FROM ( \n";
            query += query_entradas + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "      ) " + alias_tabla_entrada  + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " LEFT JOIN \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " ( \n";
            query += query_iproduct + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " ) " + alias_tabla_producto  + "\n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " ON \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "     " + alias_tabla_entrada + ".codigo_proveedor = " + alias_tabla_producto + ".codigo_proveedor "  + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "     and " + alias_tabla_entrada + ".codigo_sucursal = " + alias_tabla_producto + ".codigo_sucursal "  + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "     and " + alias_tabla_entrada + ".codigo_estadistico = " + alias_tabla_producto + ".codigo_estadistico "  + " \n \n";
            
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " UNION ALL \n \n";

            query += this.generarEspaciosTab(cantidad_espacios - 6) + " SELECT " + alias_tabla_right  +  ".codigo_proveedor     as codigo_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "        , " + alias_tabla_right + ".codigo_sucursal      as codigo_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "        , " + alias_tabla_right + ".codigo_estadistico   as codigo_estadistico \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "         " + this.generarQueryMetricasNivel_3(
                                                                                                        this.lista_metricas_periodo
                                                                                                        , true
                                                                                                        , alias_tabla_right
                                                                                                        , alias_tabla_entrada
                                                                                                        , alias_tabla_producto
                                                                                                        , cantidad_espacios);
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " FROM ( \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "     SELECT " + alias_tabla_producto + ".codigo_proveedor        as codigo_proveedor     \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "            , " + alias_tabla_producto + ".codigo_sucursal       as codigo_sucursal      \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "            , " + alias_tabla_producto + ".codigo_estadistico    as codigo_estadistico   \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "            , " + alias_tabla_entrada + ".codigo_proveedor       as codigo_proveedor_tmp   \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "             " + this.generarQueryMetricasNivel_3(
                                                                                                        this.lista_metricas_periodo
                                                                                                        , false
                                                                                                        , alias_tabla_right
                                                                                                        , alias_tabla_entrada
                                                                                                        , alias_tabla_producto
                                                                                                        , cantidad_espacios);
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " FROM ( \n";
            query += query_iproduct + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "      ) " + alias_tabla_producto  + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " LEFT JOIN \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " ( \n";
            query += query_entradas + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " ) " + alias_tabla_entrada  + "\n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " ON \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "     " + alias_tabla_producto + ".codigo_proveedor = " + alias_tabla_entrada + ".codigo_proveedor "  + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "     and " + alias_tabla_producto + ".codigo_sucursal = " + alias_tabla_entrada + ".codigo_sucursal "  + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "     and " + alias_tabla_producto + ".codigo_estadistico = " + alias_tabla_entrada + ".codigo_estadistico "  + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + "      ) " + alias_tabla_right  + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 6) + " WHERE " + alias_tabla_right + ".codigo_proveedor_tmp IS NULL \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " ) " + alias_tabla_final + " \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " INNER JOIN bi_dwh.dw_estadistico_dim  " + this.alias_dimension_estadistico + " \n ";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " INNER JOIN bi_dwh.dw_sucursal_dim  " + this.alias_dimension_sucursal + " \n ";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " INNER JOIN bi_dwh.dw_proveedor_dim  " + this.alias_dimension_proveedor + " \n ";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + " ON " + alias_tabla_final + "." + "codigo_proveedor = " + this.alias_dimension_proveedor + ".id_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + "     AND " + alias_tabla_final + "." + "codigo_sucursal = " + this.alias_dimension_sucursal + ".cod_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + "     AND " + alias_tabla_final + "." + "codigo_estadistico = " + this.alias_dimension_estadistico + ".cod_estadistico \n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + "\n";
            query += this.generarEspaciosTab(cantidad_espacios - 8) + this.generarFiltrosUltimoNivel(arreglo_filtros_adicionales
                                                                                                        , arreglo_filtros
                                                                                                        , arreglo_niveles
                                                                                                        , cantidad_espacios - 8
                                                                                                    )
                                                                                                    ;


            ////query += query_entradas + " \n \n \n \n \n \n" + query_iproduct;
            
            return query;

            
    }/// FIN generarQueryReporteDetalle


    private String generarFiltrosUltimoNivel(ArrayList arreglo_filtros_adicionales
                                                , ArrayList arreglo_filtros
                                                , ArrayList arreglo_niveles
                                                , int cantidad_espacios
                                            )
    {
            String query_filtros = "";
            String filtros_adicionales = this.queryFiltrosAdicionales
                                            ( arreglo_filtros_adicionales
                                            , CODIGO_DIMENSIONAL
                                            , cantidad_espacios
                                            );
            String filtros_generales = this.queryFiltros(arreglo_filtros, cantidad_espacios);

            ////System.out.println("FA:  "+filtros_adicionales);
            ////System.out.println("FG: " + filtros_generales);


            ///if(filtros_adicionales.isEmpty())
            if(filtros_adicionales == "")
            {
                ///if(filtros_generales.isEmpty())
                if(filtros_generales == "")
                    query_filtros += "  ";
                else
                    query_filtros += " WHERE " + filtros_generales + " \n";
            }
            else
            {
                ////if(filtros_generales.isEmpty())
                if(filtros_generales == "")
                    query_filtros += " WHERE " + filtros_adicionales + " \n";
                else
                {
                    query_filtros += " WHERE " + filtros_generales;
                    query_filtros += this.generarEspaciosTab(cantidad_espacios)
                                    + "             AND "
                                    + filtros_adicionales + " \n";
                }
            }
            query_filtros += " \n";
            query_filtros += this.generarEspaciosTab(cantidad_espacios)
                            + " GROUP BY \n "
                            + this.generarCamposSelectGroupByFinal(arreglo_niveles, 2 ,cantidad_espacios) 
                            + " \n "
                            ;
            query_filtros += this.generarEspaciosTab(cantidad_espacios)
                            + " ORDER BY "
                            + this.generarOrderBy(this.cantidad_campos_select)
                            + " \n"
                            ;

            return query_filtros;
            
    }////FIN FIN generarFiltrosUltimoNivel

    
    private String generarOrderBy(int cantidad_campos_select)
    {
            String query = "";
            for(int i=1; i <= cantidad_campos_select; i++)
            {
                query += String.valueOf(i);
                if(i<cantidad_campos_select)
                    query += " , ";
            }

            query += "  ASC  ";
            return query;

    }////generarOrderBy


    private String queryFiltros( ArrayList arreglo_filtros
                                , int cantidad_espacios )
    {

            String query = "";
            boolean es_primero = true;
            String caracter ="";
            
            for(int i = 0; i < arreglo_filtros.size(); i++)
            {

                ObjetoFiltroFlex obj_filtro = (ObjetoFiltroFlex) arreglo_filtros.get(i);

                if(obj_filtro.es_filtro_dimension)
                {
                      if(!es_primero)
                            caracter = this.generarEspaciosTab(cantidad_espacios + 2) +  " AND ";
                      else
                            caracter = "  ";

                      query += caracter + obj_filtro.alias_dimension + "." + obj_filtro.campo_tabla;
                      if(obj_filtro.es_tipo_numerico)
                            query += " = " + obj_filtro.valor + " \n";
                      else
                            query += " = '" + obj_filtro.valor + "' \n";

                      es_primero = false;
                }
            }


            return query;

    }////// queryFiltros


    private String queryFiltrosAdicionales(ArrayList arreglo_filtros_adicionales
                                            , int tipo_query
                                            , int cantidad_espacios)
    {
            String query = "";
            boolean es_primero = true;
            String caracter ="";
            int var_indice_multiple_checkbox = 0;
            int cantidad_chb = 0;
            int contador_cb = 0;
            boolean bandera = false;

            for(int i = 0; i < arreglo_filtros_adicionales.size() ; i++)
            {
                bandera = false;
                ObjetoFiltroFlex obj_filtro = (ObjetoFiltroFlex) arreglo_filtros_adicionales.get(i);

                ///if(!obj_filtro.valor.isEmpty())
                if(obj_filtro.valor != "")
                {

                                if(es_primero)
                                {
                                    if(tipo_query == CODIGO_DIMENSIONAL)
                                    {
                                        contador_cb = 0;
                                        caracter = "    ";
                                    }
                                    else
                                        caracter = "  AND  ";

                                    caracter += " ( ";
                                    cantidad_chb = this.cantidadCheckBoxDeUnFiltro(arreglo_filtros_adicionales, obj_filtro.indice_multiple_checkbox);
                                    var_indice_multiple_checkbox = obj_filtro.indice_multiple_checkbox;                                    
                                    contador_cb ++;
                                }
                                else
                                {
                                    caracter =  this.generarEspaciosTab(cantidad_espacios);
                                    if(obj_filtro.es_multiple_checkbox)
                                    {
                                        if(obj_filtro.indice_multiple_checkbox != var_indice_multiple_checkbox)
                                        {
                                            contador_cb = 0;
                                            cantidad_chb = this.cantidadCheckBoxDeUnFiltro(arreglo_filtros_adicionales, obj_filtro.indice_multiple_checkbox);
                                            caracter += "  AND (  ";
                                            var_indice_multiple_checkbox = obj_filtro.indice_multiple_checkbox;
                                            
                                        }
                                        else
                                            caracter += "  OR  ";

                                        contador_cb ++;
                                    }
                                    else
                                        caracter += "  AND  ";

                                }
                                
                                
                                
                                if(tipo_query == CODIGO_IENTRADAS)
                                {
                                    //Es query de entradas
                                    if(obj_filtro.tipo_tabla != CODIGO_IPRODUCT_MENSUAL && obj_filtro.tipo_tabla != CODIGO_DIMENSIONAL)
                                    {
                                        query += caracter + obj_filtro.campo_tabla_entradas;
                                        bandera = true;
                                    }
                                }
                                else
                                {
                                       if(tipo_query == CODIGO_IPRODUCT_MENSUAL)
                                       {
                                           //Es query de iproduct
                                            if(obj_filtro.tipo_tabla != CODIGO_IENTRADAS && obj_filtro.tipo_tabla != CODIGO_DIMENSIONAL)
                                            {
                                                query += caracter + obj_filtro.campo_tabla;
                                                bandera = true;
                                            }
                                       }
                                       else
                                       {
                                           //Es query de dimension
                                           if(obj_filtro.tipo_tabla == CODIGO_DIMENSIONAL)
                                           {
                                                query += caracter + obj_filtro.campo_tabla;
                                                bandera = true;
                                           }
                                       }

                                }

                                if(bandera)
                                {
                                    if(obj_filtro.es_tipo_numerico)
                                       query += " = " + obj_filtro.valor;
                                    else
                                       query += " = '" + obj_filtro.valor + "' ";

                                    if(obj_filtro.es_multiple_checkbox == true &&  cantidad_chb == contador_cb)
                                        query += " ) \n";
                                    else
                                        query += " \n";

                                     es_primero = false;
                                     
                                     /////System.out.println("cantidad_chb: " + String.valueOf(cantidad_chb) + " contador_cb: " + String.valueOf(contador_cb));

                                }
                                
                               

                    }

                           
                
            }///FIN FOR

            return query;

    }////   queryFiltrosAdicionales

    private int cantidadCheckBoxDeUnFiltro(ArrayList arreglo_filtros_adicionales
                                            , int indice_codigo_filtro)
    {
            int cantidad = 0;

            for(int i = 0; i < arreglo_filtros_adicionales.size() ; i++)
            {
                ObjetoFiltroFlex obj_filtro = (ObjetoFiltroFlex) arreglo_filtros_adicionales.get(i);
                
                ///if(!obj_filtro.valor.isEmpty())
                if(obj_filtro.valor != "")
                {
                   if(obj_filtro.indice_multiple_checkbox == indice_codigo_filtro )
                        cantidad ++;
                }
            }

            return cantidad;
    }

    private String queryQueryIproduct( ArrayList arreglo_filtros_adicionales
                                        , ArrayList arreglo_metricas
                                        , ArrayList arreglo_fechas
                                        , Fecha fecha_diaria_maxima
                                        , int cantidad_espacios)
    {

            String query = "";
            String query_1 = "";
            String tabla_producto = " bi_dwh.dw_iproduct_mm_fact2 ";
            String tabla_producto_diario = " bi_dwh.dw_iproduct_fact ";
            String campo_fecha_tabla = "fc_fecha";
            String alias_tabla_producto_1 = " ttp1";
            String alias_tabla_producto_2 = " ttp2";
            boolean concatenar_round = false;
           
            boolean es_tabla_mensual = true;


             query_1 =  this.generarEspaciosTab(cantidad_espacios) + " select date_format(fc_fecha,'%Y-%m-01')    as fecha \n";
             query_1 += this.generarEspaciosTab(cantidad_espacios) + "       , fc_cod_proveedor      as codigo_proveedor \n";
             query_1 += this.generarEspaciosTab(cantidad_espacios) + "       , fc_cod_estadistico    as codigo_estadistico \n";
             query_1 += this.generarEspaciosTab(cantidad_espacios) + "       , fc_cod_sucursal       as codigo_sucursal  \n";


            query += this.generarEspaciosTab(cantidad_espacios - 4) + " select " + alias_tabla_producto_2 + ".codigo_proveedor         as codigo_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "         , " + alias_tabla_producto_2 + ".codigo_sucursal       as codigo_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "         , " + alias_tabla_producto_2 + ".codigo_estadistico    as codigo_estadistico \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "          "  ;
            query += this.generarQueryMetricasNivel_2(this.lista_metricas_periodo, 2, alias_tabla_producto_2 , "fecha", cantidad_espacios - 4) + " \n ";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " from ( \n";
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + " select ifnull(" + alias_tabla_producto_1 + ".fecha,0)   as fecha \n" ;
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + "        , ifnull(" + alias_tabla_producto_1 + ".codigo_proveedor,0)     as codigo_proveedor \n";
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + "        , ifnull(" + alias_tabla_producto_1 + ".codigo_sucursal,0)      as codigo_sucursal  \n";
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + "        , ifnull(" + alias_tabla_producto_1 + ".codigo_estadistico,0)   as codigo_estadistico \n";
            ////concatenar_round = true;
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + "        , " + this.generarQueryMetricasNivel_1(arreglo_metricas, 2, concatenar_round, alias_tabla_producto_1, cantidad_espacios - 2, campo_fecha_tabla, 0)   + " \n";
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + " from( \n";
            query += query_1;
            ////concatenar_round = false;
            query += this.generarEspaciosTab(cantidad_espacios) + "        " + this.generarQueryMetricasNivel_1(arreglo_metricas, 2, concatenar_round, alias_tabla_producto_1, cantidad_espacios, campo_fecha_tabla, 0) + " \n";
            query += this.generarEspaciosTab(cantidad_espacios) + " from " + tabla_producto + " \n";
            query += this.generarEspaciosTab(cantidad_espacios) + " where " + this.generarQueryFiltroMensual(campo_fecha_tabla, 2, arreglo_fechas, cantidad_espacios) + " \n \n \n";
            query += this.generarEspaciosTab(cantidad_espacios) + " " + this.queryFiltrosAdicionales(arreglo_filtros_adicionales, CODIGO_IPRODUCT_MENSUAL, cantidad_espacios) + " \n";
            ////query += this.generarEspaciosTab(cantidad_espacios) + "  UNION ALL \n \n";
            ////query += query_1;
            ////query += this.generarEspaciosTab(cantidad_espacios) + "       , " + this.generarQueryMetricasNivel_1(arreglo_metricas, 2, concatenar_round, alias_tabla_producto_1, cantidad_espacios, campo_fecha_tabla, 1) + " \n";
            ////concatenar_round = true;
            ////query += this.generarEspaciosTab(cantidad_espacios) + " from " + tabla_producto_diario + " \n";
            ////query += this.generarEspaciosTab(cantidad_espacios) + " where " + this.generarQueryFiltroDiario( campo_fecha_tabla, fecha_diaria_maxima, cantidad_espacios - 2);
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + ") " + alias_tabla_producto_1 + " \n";
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + " group by fecha, codigo_proveedor, codigo_sucursal, codigo_estadistico \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "     ) " + alias_tabla_producto_2 + " \n" ;
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " group by " + alias_tabla_producto_2 + ".codigo_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "             ," + alias_tabla_producto_2 + ".codigo_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "             ," + alias_tabla_producto_2 + ".codigo_estadistico \n";


             return query;


    }/////  queryQueryIproduct






    private String generarQueryEntradas(ArrayList arreglo_filtros_adicionales
                                        , ArrayList arreglo_metricas
                                        , ArrayList arreglo_fechas
                                        , Fecha fecha_diaria_maxima
                                        , int cantidad_espacios
                                        )
    {
            String query = "";
            String query_1 = "";
            String tabla_entradas = " bi_dwh.dw_entradas_fact ";
            String campo_fecha_tabla = "fecha";
            String alias_tabla_entradas_1 = " tte1";
            String alias_tabla_entradas_2 = " tte2";
            boolean concatenar_round = false;
            

        
             ///query_1 =  this.generarEspaciosTab(cantidad_espacios) + " select CAST(date_format(fecha,'%Y-%m-01') AS char(10))   as fecha \n";
             query_1 =  this.generarEspaciosTab(cantidad_espacios) + " select (date_format(fecha,'%Y-%m-01'))   as fecha \n";
             query_1 += this.generarEspaciosTab(cantidad_espacios) + "       , cod_proveedor      as codigo_proveedor \n";
             query_1 += this.generarEspaciosTab(cantidad_espacios) + "       , cod_estadistico    as codigo_estadistico \n";
             query_1 += this.generarEspaciosTab(cantidad_espacios) + "       , cod_sucursal       as codigo_sucursal  \n";
             query_1 += this.generarEspaciosTab(cantidad_espacios) + "        " + this.generarQueryMetricasNivel_1(arreglo_metricas, 1, concatenar_round, alias_tabla_entradas_1, cantidad_espacios, campo_fecha_tabla, 0) + " \n";
             query_1 += this.generarEspaciosTab(cantidad_espacios) + " from  " + tabla_entradas + "  \n";


             
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " select " + alias_tabla_entradas_2 + ".codigo_proveedor         as codigo_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "         , " + alias_tabla_entradas_2 + ".codigo_sucursal       as codigo_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "         , " + alias_tabla_entradas_2 + ".codigo_estadistico    as codigo_estadistico \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "          "  ;
            query += this.generarQueryMetricasNivel_2(this.lista_metricas_periodo, 1, alias_tabla_entradas_2 , "fecha", cantidad_espacios - 4) + " \n ";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " from ( \n";
            
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + " select ifnull(" + alias_tabla_entradas_1 + ".fecha,0)   as fecha \n" ;
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + "        , ifnull(" + alias_tabla_entradas_1 + ".codigo_proveedor,0)     as codigo_proveedor \n";
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + "        , ifnull(" + alias_tabla_entradas_1 + ".codigo_sucursal,0)      as codigo_sucursal  \n";
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + "        , ifnull(" + alias_tabla_entradas_1 + ".codigo_estadistico,0)   as codigo_estadistico \n";
            ////concatenar_round = true;
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + "        , " + this.generarQueryMetricasNivel_1(arreglo_metricas, 1, concatenar_round, alias_tabla_entradas_1, cantidad_espacios - 2, campo_fecha_tabla, 0)   + " \n";
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + " from( \n";
            query += query_1;
            query += this.generarEspaciosTab(cantidad_espacios) + " where " + this.generarQueryFiltroMensual(campo_fecha_tabla, 1, arreglo_fechas, cantidad_espacios) + " \n \n \n";
            query += this.generarEspaciosTab(cantidad_espacios) + " " + this.queryFiltrosAdicionales(arreglo_filtros_adicionales, CODIGO_IENTRADAS, cantidad_espacios) + " \n";
            ///query += this.generarEspaciosTab(cantidad_espacios) + "  UNION ALL \n \n";
            ///query += query_1;
            ///query += this.generarEspaciosTab(cantidad_espacios) + " where " + this.generarQueryFiltroDiario( campo_fecha_tabla, fecha_diaria_maxima, cantidad_espacios - 2);
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + ") " + alias_tabla_entradas_1 + " \n";
            ////query += this.generarEspaciosTab(cantidad_espacios - 2) + " group by fecha, codigo_proveedor, codigo_sucursal, codigo_estadistico \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "     ) " + alias_tabla_entradas_2 + " \n" ;
            query += this.generarEspaciosTab(cantidad_espacios - 4) + " group by " + alias_tabla_entradas_2 + ".codigo_proveedor \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "             ," + alias_tabla_entradas_2 + ".codigo_sucursal \n";
            query += this.generarEspaciosTab(cantidad_espacios - 4) + "             ," + alias_tabla_entradas_2 + ".codigo_estadistico \n";

            
            return query;


    }///    generarQueryEntradas





    private ArrayList generarListaMetricasPeriodo(ArrayList arreglo_fechas, ArrayList arreglo_metricas)
    {
            ArrayList arreglo_metricas_periodo =  new ArrayList();
            String nuevo_nombre_metrica = "";

            for(int i = 0; i < arreglo_metricas.size() ; i++)
            {
                ObjetoFiltroFlex obj_filtro = (ObjetoFiltroFlex) arreglo_metricas.get(i);
                for(int j = 0; j < arreglo_fechas.size() ; j++)
                {
                    nuevo_nombre_metrica = "";
                    Fecha obj_fecha = (Fecha) arreglo_fechas.get(j);
                    ///fecha actual
                    CampoPeriodoSelect obj = new CampoPeriodoSelect();
                    obj.setFecha(obj_fecha.getFecha());
                    obj.setAnio(obj_fecha.getAnio());
                    obj.setMes(obj_fecha.getMes());
                    obj.setDia(obj_fecha.getDia());
                    obj.setTabla(obj_filtro.tipo_tabla);
                    obj.setMetrica(obj_filtro.alias_campo_tabla);
                    nuevo_nombre_metrica = obj.getMetrica() + "_" + obj.getAnio() + "_" + obj.getMes() + "_" + obj.getDia();
                    obj.setNombre_campo(nuevo_nombre_metrica);
                    arreglo_metricas_periodo.add(obj);
                    ///fecha anterior
                    /*
                    obj = new CampoPeriodoSelect();
                    obj.setFecha(obj_fecha.getFecha_anterior());
                    obj.setAnio(obj_fecha.getAnio_anterior());
                    obj.setMes(obj_fecha.getMes_anterior());
                    obj.setDia(obj_fecha.getDia_anterior());
                    obj.setTabla(obj_filtro.tipo_tabla);
                    obj.setMetrica(obj_filtro.alias_campo_tabla);
                    nuevo_nombre_metrica = obj.getMetrica() + "_" + obj.getAnio() + "_" + obj.getMes() + "_" + obj.getDia();
                    obj.setNombre_campo(nuevo_nombre_metrica);
                    arreglo_metricas_periodo.add(obj);
                    */
                }
            }
            

            return arreglo_metricas_periodo;

    }//// generarListaMetricasPeriodo

    private String generarEspaciosTab(int cantidad)
    {
            String espacios = "";

            for(int i=1; i <= cantidad ;i++)
                espacios += "       ";
            
            return espacios;

    }///FIN generarEspaciosTab

    private String generarCamposSelectGroupByFinal(ArrayList arreglo_niveles, int select_group_by, int espacios)
    {
            String query = "";

            String caracter = "  ";
            boolean es_primero = true;
            

            this.cantidad_campos_select = 0;
            for(int i = 0; i < arreglo_niveles.size() ; i++)
            {
                    ObjetoFiltroFlex obj = (ObjetoFiltroFlex)arreglo_niveles.get(i);
                
                    if(!es_primero)
                        caracter = this.generarEspaciosTab(espacios + 2) +  " , ";
                    else
                       caracter = this.generarEspaciosTab(espacios + 2);

                    query += caracter + obj.alias_dimension + "." + obj.campo_tabla;

                    if(select_group_by == 1)
                        query += " as " + obj.alias_campo_tabla + " \n";
                    else
                        query += " \n";

                    this.cantidad_campos_select = this.cantidad_campos_select + 1;

                    if(obj.tiene_campo_nombre)
                    {
                        caracter = this.generarEspaciosTab(espacios + 2) + " , ";
                        query += caracter + obj.alias_dimension + "." + obj.campo_nombre_tabla;
                        if(select_group_by == 1)
                            query += " as "  + obj.alias_campo_nombre_tabla + " \n";
                        else
                            query += " \n";

                        this.cantidad_campos_select = this.cantidad_campos_select + 1;

                    }
                    es_primero = false;
            }

            return query;

    }//generarCamposSelectFinal



    
    private String generarQueryMetricasNivel_1(ArrayList arreglo, int tipo_tabla, boolean concatenar_round, String alias_tabla, int espacios, String campo_fecha_tabla, int tipo_tabla_iproduct)
    {
            String query_metricas = "";
            String caracter;
            boolean es_primera_metricas = true;

            for(int i = 0; i < arreglo.size() ; i++)
            {
                ObjetoFiltroFlex obj = (ObjetoFiltroFlex) arreglo.get(i);
                if(obj.tipo_tabla == tipo_tabla)
                {
                        if(es_primera_metricas)
                        {
                            caracter = " , ";
                            es_primera_metricas = false;
                        }
                        else
                            caracter = this.generarEspaciosTab(espacios + 2) +  ", ";

                        /*
                        if(concatenar_round)
                            query_metricas = query_metricas  + caracter + " sum(round(" + alias_tabla + "." + obj.alias_campo_tabla + ",3))  as  " + obj.alias_campo_tabla  + " \n" ;
                        else
                        {
                            
                            if(obj.alias_campo_tabla.substring(0, 5).equalsIgnoreCase("stock") && tipo_tabla_iproduct == 1)
                            {
                                /////if(thd.fc_fecha = '2011-08-12', thd.fc_stock_ult_cos, 0) as stock_pcsi
                                
                                query_metricas = query_metricas + caracter + " if(" + campo_fecha_tabla + " = '" + this.fecha_maxima_diaria.getFecha_anterior();
                                query_metricas = query_metricas + "', " + obj.campo_tabla + ", 0)  as  " +  obj.alias_campo_tabla + " \n ";

                                
                            }
                            else */
                                String campo = obj.campo_tabla;
                                ////if(obj.campo_tabla.indexOf("ajustes") != -1)
                                ////    campo = " (" + obj.campo_tabla + " * -1) ";
                                query_metricas = query_metricas  + caracter + campo + " as " + obj.alias_campo_tabla + " \n ";
                       ///// }
                }
            }


            return query_metricas;
            
    }/// generarQueryMetricasNivel_1


    private String generarQueryMetricasNivel_2(ArrayList arreglo_metricas_periodo, int tipo_tabla, String alias_tabla, String alias_campo_fecha, int espacios)
    {
        
            String query = "";
            String caracter = "  ";
            boolean es_primero = true;
            /////   sum(round(ifnull(if(x1.fecha = '2012-01-01', x1.entradas_pcsi, 0),0),3))    as entradas_pcsi_1_ac
             
            for(int i = 0; i < arreglo_metricas_periodo.size() ; i++)
            {
                CampoPeriodoSelect obj = (CampoPeriodoSelect) arreglo_metricas_periodo.get(i);
                if(obj.getTabla() == tipo_tabla)
                {
                    if(!es_primero)
                        caracter = this.generarEspaciosTab(espacios + 2) +  " , ";
                    else
                        caracter = " , ";

                    query += caracter + " sum(round(if( cast(" + alias_tabla + "." + alias_campo_fecha + " as char(10)) = '" + obj.getFecha() + "', ";
                    query += alias_tabla + "." + obj.getMetrica() + ",0),3)) as " + obj.getNombre_campo() + " \n";

                    //// query += caracter + " sum(round(if(" + alias_tabla + "." + alias_campo_fecha + " = '" + obj.getFecha() + "', ";
                    //// query += alias_tabla + "." + obj.getMetrica() + ",0),3)) as " + obj.getNombre_campo() + " \n";

                    ///Para evitar el Error de "sum/avg for column type VARCHAR isn't supported."
                    ////query += caracter + " sum(round(ifnull(if(" + alias_tabla + "." + alias_campo_fecha + " = '" + obj.getFecha() + "', ";
                    ////query += alias_tabla + "." + obj.getMetrica() + ", 0),0),3)) as " + obj.getNombre_campo() + " \n";

                    

                    es_primero = false;

                }
            }

            return query;

    }/// generarQueryMetricasNivel_2

    private String generarQueryMetricasNivel_3( ArrayList arreglo_metricas_periodo
                                                , boolean es_tabla_right
                                                , String alias_tabla_right
                                                , String alias_tabla_entrada
                                                , String alias_tabla_producto
                                                , int espacios )
    {
            String query = "";

            String caracter = "  ";
            boolean es_primero = true;
            String tabla = "";
            String espacios_blancos = "";

            for(int i = 0; i < arreglo_metricas_periodo.size() ; i++)
            {
                    CampoPeriodoSelect obj = (CampoPeriodoSelect) arreglo_metricas_periodo.get(i);
                    if(!es_tabla_right)
                    {
                        if(obj.getTabla() == 1)
                            tabla = "te";
                        else
                            tabla = "tp";

                        espacios_blancos = this.generarEspaciosTab(espacios - 2);

                    }
                    else
                    {
                        tabla = alias_tabla_right;
                        espacios_blancos = this.generarEspaciosTab(espacios - 4);
                    }

                    if(!es_primero)
                        caracter = espacios_blancos +  " , ";
                    else
                        caracter = " , ";


                    query += caracter + tabla + "." + obj.getNombre_campo() + " as " + obj.getNombre_campo() + " \n";
                    

                    es_primero = false;
            }

            return query;

    }/// generarQueryMetricasNivel_3



    private String generarQueryMetricasNivel_4(ArrayList arreglo_metricas_periodo
                                                    , String alias_tabla
                                                    , int espacios)
    {

            String query = "";
            String caracter = "  ";
            boolean es_primero = true;
            /////   sum(round(ifnull(if(x1.fecha = '2012-01-01', x1.entradas_pcsi, 0),0),3))    as entradas_pcsi_1_ac

            for(int i = 0; i < arreglo_metricas_periodo.size() ; i++)
            {
                CampoPeriodoSelect obj = (CampoPeriodoSelect) arreglo_metricas_periodo.get(i);
                
                    if(!es_primero)
                        caracter = this.generarEspaciosTab(espacios + 2) +  " , ";
                    else
                        caracter = " , ";

                    query += caracter + " sum(round(ifnull(" + alias_tabla + "." + obj.getNombre_campo() + ",0),3)) as " + obj.getNombre_campo() + " \n";

                    es_primero = false;

                
            }

            return query;

    }/// generarQueryMetricasNivel_4


    private String generarQueryFiltroMensual(String campo_fecha, int tipo_tabla,  ArrayList arreglo_fechas, int espacios)
    {
        /*
            date_format(fecha,'%Y-%m-01') = '2012-01-01'
                or date_format(fecha,'%Y-%m-01') = '2011-01-01'
         */
        
            String query_periodos = "";
            String campo_tmp = "";
            boolean es_primera_fecha = true;
            String operador;

            if(tipo_tabla == 1)
                campo_tmp = " date_format(" + campo_fecha +",'%Y-%m-01') ";
            else
                campo_tmp = campo_fecha;
            
            int tamanio_arreglo = arreglo_fechas.size();
            for(int i=0; i < tamanio_arreglo; i++)
            {

                Fecha fecha = (Fecha) arreglo_fechas.get(i);
                if(es_primera_fecha)
                {
                     operador = "   (";
                     es_primera_fecha = false;
                }
                else
                    operador = this.generarEspaciosTab(espacios) + " or ";

                query_periodos = query_periodos + operador + campo_tmp + " = '" + fecha.getFecha() + "' ";
                if(i == (tamanio_arreglo - 1) )
                   query_periodos = query_periodos + ") \n ";
                else
                    query_periodos = query_periodos + " \n ";
                ////if( i < tamanio_arreglo -1)
                ////    query_periodos = query_periodos + this.generarEspaciosTab(espacios) + " or " + campo_tmp + " = '" + fecha.getFecha_anterior() + "' \n ";

            }

            return query_periodos;

            
    }// generarQueryPeriodo


    private String generarQueryFiltroDiario(String campo_fecha, Fecha maxima_fecha_diaria, int espacios)
    {
            String filtro_fecha_diaria = "";

            ////    fecha >= date_add('2012-08-01', interval -1 year)
            ////              and fecha <= date_add('2012-08-12', interval -1 year)

            filtro_fecha_diaria += campo_fecha + " >= date_format('" + maxima_fecha_diaria.getFecha_anterior() + "', '%Y-%m-%01') \n";
            filtro_fecha_diaria += this.generarEspaciosTab(espacios + 2) + " and " + campo_fecha + " <= '" + maxima_fecha_diaria.getFecha_anterior()  +"' \n";

            
            return filtro_fecha_diaria;

    }// FIN generarQueryFiltroDiario




    private void imprimirArreglosEnArchivo(
                                                ArrayList arreglo_elementos
                                                , String nombre_archivo
                                             )
    {


              ObjetoFiltroFlex mapa;
              int i = 0;


               try
               {
                  
                  File f = new File(this.ruta_archivos_excel + nombre_archivo);
                   //f.deleteOnExit();
                  FileWriter fileOut = new FileWriter(f);
                  BufferedWriter bw = new BufferedWriter(fileOut);
                  PrintWriter salida = new PrintWriter(bw);

                          for(i=0; i < arreglo_elementos.size(); i++)
                          {
                                ObjetoFiltroFlex obj_filtro = (ObjetoFiltroFlex) arreglo_elementos.get(i);

                                       salida.println(  obj_filtro.label.toString()
                                                        + " --- " + obj_filtro.valor.toString()
                                                        + " --- " + obj_filtro.campo_tabla.toString()
                                                        + " --- " + obj_filtro.campo_tabla_entradas.toString()
                                                        + " --- " + String.valueOf(obj_filtro.tipo_tabla)
                                                        + " --- " + obj_filtro.alias_campo_tabla.toString()
                                                        + " --- " + obj_filtro.campo_nombre_tabla.toString()
                                                        + " --- " + obj_filtro.alias_campo_nombre_tabla.toString()
                                                        + " --- " + String.valueOf(obj_filtro.indice_multiple_checkbox)

                                                        );
                           }//Fin For

                 salida.close();
                 fileOut.close();

                 //System.out.println("Archivo creado satisfactoriamente");
               }
               catch (Exception ex)
               {
                 System.out.println("No se pudo crear el archivo final de filtros: "+ex.toString());

               }

    }///ImprimirArreglosEnArchivo

    private void imprimirStringEnArchivo(String cadena, String nombre_archivo)
    {

              try
               {
                  
                  File f = new File(this.ruta_archivos_excel + nombre_archivo);
                  //f.deleteOnExit();
                  FileWriter fileOut = new FileWriter(f);
                  BufferedWriter bw = new BufferedWriter(fileOut);
                  PrintWriter salida = new PrintWriter(bw);

                    salida.println(cadena);


                  salida.close();
                  fileOut.close();

               }
               catch (Exception ex)
               {
                 System.out.println("No se pudo crear el archivo final de filtros: "+ex.toString());

               }

    }/// imprimirQueryEnArchivo


    private void imprimirStringDeAtributos(Nivel nivel_raiz
                                            , String nombre_archivo_atributos
                                            , boolean es_reporte_detalle_mensual)
    {
            String caracter = ";";
            Metrica obj_metrica = null;
            String cadena = "";
            try
               {

                  File f = new File(this.ruta_archivos_excel + nombre_archivo_atributos);
                  FileWriter fileOut = new FileWriter(f);
                  BufferedWriter bw = new BufferedWriter(fileOut);
                  PrintWriter salida = new PrintWriter(bw);


                  if(es_reporte_detalle_mensual == true)
                  {
                          for(int i = 0; i < nivel_raiz.lista_metricas_total.size(); i++)
                          {
                                obj_metrica =(Metrica)nivel_raiz.lista_metricas_total.get(i);

                                if(i > 0)
                                    cadena = caracter + this.extraerMinimoNombre(obj_metrica.nombre_compuesto);
                                else
                                    cadena = this.extraerMinimoNombre(obj_metrica.nombre_compuesto);

                                salida.print(cadena);
                          }///FIN FOR
                   }


                  for(int j = 0; j < nivel_raiz.lista_metricas_periodo.size(); j++)
                  {
                      obj_metrica =(Metrica)nivel_raiz.lista_metricas_periodo.get(j);
                      if((es_reporte_detalle_mensual == false)  && (j == 0))
                      {
                            caracter = "";
                            for(int k = 0; k < nivel_raiz.lista_marcas.size(); k++)
                            {
                                Marca obj_marca = (Marca)nivel_raiz.lista_marcas.get(k);
                                salida.print(obj_marca.nombre_xml + ";");
                            }
                      }
                      else
                      {
                         caracter = ";";
                      }
                      cadena = caracter + this.extraerMinimoNombre(obj_metrica.nombre_compuesto);
                      salida.print(cadena);
                  }///FIN FOR

                  salida.close();
                  fileOut.close();

               }
               catch (Exception ex)
               {
                 System.out.println("No se pudo crear el archivo de atributos del xml: " + ex.toString());

               }

    }///FIN imprimirStringDeAtributos


    /**
    private void obtenerValoresDesdeFlexParaArchivo(
                                                ArrayCollection arreglo_elementos
                                                , String nombre_archivo
                                             )
    {


              List<HashMap<Object, Object>> maplist;
              HashMap<Object,Object> mapa;
              int i = 0;


               try
               {
                  File f = new File(this.ruta_archivos_excel + nombre_archivo);
                  //f.deleteOnExit();
                  FileWriter fileOut = new FileWriter(f);
                  BufferedWriter bw = new BufferedWriter(fileOut);
                  PrintWriter salida = new PrintWriter(bw);


                          maplist = MapArrayCollection.convertArrayCollectionToList(arreglo_elementos);

                          for(i=0; i < maplist.size(); i++)
                          {
                                mapa = maplist.get(i);
                                Iterator iterator = mapa.keySet().iterator();
                                while(iterator.hasNext())
                                {
                                       Object valor = iterator.next();
                                       Object nombre = iterator.next();
                                       salida.println(mapa.get(nombre).toString()+"---"+mapa.get(valor).toString());
                                }//fin while lista de mapas
                           }//Fin For

                 salida.close();
                 fileOut.close();

                 //System.out.println("Archivo creado satisfactoriamente");
               }
               catch (Exception ex)
               {
                 System.out.println("No se pudo crear el archivo inicial: "+ex.toString());

               }

    }/////obtenerValoresDesdeFlexParaArchivo

    */


    private String leerScriptSQL(String nombre_archivo)
    {
            String archivo = "";
            String auxiliar ="";

            try
            {

                    File f;
                    FileReader lectorArchivo;

                    //Creamos el objeto del archivo que vamos a leer
                    ///f = new File(this.ruta_archivos_excel + nombre_archivo);
                    f = new File("C:\\Archivos de programa\\Apache Software Foundation\\Apache Tomcat 6.0.26\\webapps\\" + nombre_archivo);
                    //Creamos el objeto FileReader que abrira el flujo(Stream) de datos para realizar la lectura
                    lectorArchivo = new FileReader(f);

                    //Creamos un lector en buffer para recopilar datos a travez del flujo "lectorArchivo" que hemos creado
                    BufferedReader br = new BufferedReader(lectorArchivo);

                    while(true)
                    {
                                auxiliar=br.readLine();
                                //leemos una linea de texto y la guardamos en la variable auxiliar
                                if(auxiliar!=null)
                                    archivo = archivo + auxiliar+"\n";
                                else
                                    break;
                    }

                    br.close();

                    lectorArchivo.close();


          }
          catch(IOException e)
          {
                System.out.println("Error:"+e.getMessage());
          }

         
           return archivo;



    }////leerScriptSQL


}// FIN ReporteDB
