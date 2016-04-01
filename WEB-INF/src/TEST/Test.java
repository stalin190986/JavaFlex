/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TEST;
import Datos.ReporteDB;
import Servicios.*;
import Modelos.*;
import Datos.*;
import flex.messaging.io.ArrayCollection;
import java.awt.geom.Dimension2D;
import java.util.List;
import java.util.ArrayList;
import flex.messaging.io.ArrayCollection;
import flex.messaging.io.amf.ASObject;
import flex.messaging.io.amf.translator.ASTranslator;


/**
 *
 * @author TIA
 */
public class Test {

    public static void main(String arg[])
    {
        EntradasService evol = new EntradasService();

        //List<Sector> lista =evol.getSectores("-1","-1");
//        List<Sector> lista  = evol.getSectores("FAM", "-1", "-1", "-1", 101, 14003, -1, "2011-09-01","2011-09-01");
        //List<Seccion> lista =evol.getSecciones("-1","-1","-1");
 //       List<Seccion> lista  = evol.getSecciones("FAM", "-1", "-1", "-1", 101, 14003, -1, "COMESTIBLES (SECTOR)","2011-09-01","2011-09-01");
//        List<Seccion> lista  = evol.getFamilias("-1", "-1", "-1", "-1", -1, 16024, -1, "-1",-1,"2012-08-01","2012-08-01");
        //List<Familia> lista =evol.getFamilias("-1","-1","-1",-1);
        //List<Subfamilia> lista =evol.getSubfamilias("-1","-1","-1",-1,-1);
        //  List<Proveedor> lista = evol.getProveedor("-1","-1","-1","-1",-1,"2012-08-01","2012-08-01");


//          List<Proveedor> lista = evol.getDistribuidor("-1","-1","-1","-1",-1,14003,"2012-08-01","2012-08-01");

//        List<Estadistico> lista =evol.getEstadisticos("-1", "-1", "-1", "-1", -1, -1, -1, "-1",-1, -1,-1
 //                                                       ,"2012-08-01","2012-08-01"
 //                                                       );

      // List<GerenteRegional> lista =evol.getGerenteRegional("2011-09-01","2011-09-01");
//        List<Region> lista =evol.getRegion("RDM","2011-09-01","2011-09-01");
//        List<SupervisorZonal> lista =evol.getSupervisorZonal("-1","-1","2011-09-01","2011-09-01");
        //List<Formato> lista =evol.getFormatos("RDM","-1","-1");
//        List<Sucursal> lista =evol.getSucursales("FAM","CND","-1","-1","2011-09-01","2011-09-01");

//        List<Proveedor> lista = evol.getProveedor("-1", "-1", "-1", "-1", -1, "2011-09-01","2011-09-01");

///        List<Proveedor> lista = evol.getDistribuidor("-1", "-1", "-1", "-1", -1, 3006, "2012-01-01","2012-12-01");

       // List<Fecha> lista = evol.getAnios();
       // List<Fecha> lista = evol.getMeses(0);

        //CentroDistribucion cd = new CentroDistribucion();
       // String s = cd.getExcluidosBasicos("2010-11-01");


       
        //// String fecha_anterior = new Fecha().obtenereFechaMensualAnterior("2012-02-01");

         /*
        ArrayCollection arreglo_filtros = new ArrayCollection();
        ObjetoFiltroFlex obj;
        flex.messaging.io.amf.ASObject obj_nativo_flex;
        obj = new ObjetoFiltroFlex("109","s1");
        //obj_nativo_flex = (flex.messaging.io.amf.ASObject)obj;
        arreglo_filtros.add(obj);

        
        obj = new ObjetoFiltroFlex("25","e4");
        //obj_nativo_flex = (flex.messaging.io.amf.ASObject)obj;
        arreglo_filtros.add(obj);

        */
        
        ArrayCollection arreglo_fechas = new ArrayCollection();
        generarArregloFechas(arreglo_fechas);
        

        
        ArrayCollection arreglo_filtros_adicionales = new ArrayCollection();
        generarArregloFiltrosAdicionales(arreglo_filtros_adicionales);


        ArrayCollection arreglo_metricas = new ArrayCollection();
        generarArregloMetricas(arreglo_metricas);

        ArrayCollection arreglo_niveles_final = new ArrayCollection();
        generarArregloNivelesFinal(arreglo_niveles_final);

        ArrayCollection arreglo_filtros = new ArrayCollection();
        generarArregloFiltros(arreglo_filtros);
        String resultado =  evol.obtenerDatosReporte(arreglo_fechas
                                                     ,arreglo_filtros
                                                     ,arreglo_filtros_adicionales
                                                     ,arreglo_metricas
                                                     , arreglo_niveles_final
                                                     , "m"
                                                     , 1
                                                     , 2
                                                     , "1234567"
                                                     );

        /*
        ArrayCollection arreglo_campos_xml = new ArrayCollection();
        generarMetricasXml(arreglo_campos_xml);

        ArrayCollection arreglo_chbox_no_seleccionados = new ArrayCollection();
        generarGrupoMetricasNoSeleccionadas(arreglo_chbox_no_seleccionados);

        String resultado = evol.exportarExcel
                        ("xml_reporte_jpivot_com_5905120.xml"
                            , "flag_exi"
                            , false
                            , arreglo_campos_xml
                            , arreglo_chbox_no_seleccionados
                            , "gmne_reporte_jpivot_com_5905120.gmne"
                            , false
                            , true
                            );
         *
         */
/*
        DimensionDB d = new DimensionDB();
        String xml = d.getDimension(2);
        System.out.println(xml);
 */
/*
        String nombre_excel = evol.exportarExcel("xml_reporte_jpivot_2110833.xml"
                                                    , "g_pvsi_2012_09_01"
                                                    , false
                                                 );
 * */
 
       



    }////FIN FIN MAIN

    private static void generarMetricasXml(ArrayList arreglo_campos_xml)
    {

        ASObject obj_1 = new ASObject();
                 obj_1.put("data","flag_exi");
                 arreglo_campos_xml.add(obj_1);

        ASObject obj_2 = new ASObject();
                 obj_2.put("data","p_c_pcsi_ac");
                 arreglo_campos_xml.add(obj_2);

        ASObject obj_3 = new ASObject();
                 obj_3.put("data","c_pcsi_ac");
                 arreglo_campos_xml.add(obj_3);

        ASObject obj_4 = new ASObject();
                 obj_4.put("data","c_pcsi_an");
                 arreglo_campos_xml.add(obj_4);

        ASObject obj_5 = new ASObject();
                 obj_5.put("data","evo_c_pcsi_ac");
                 arreglo_campos_xml.add(obj_5);

        ASObject obj_6 = new ASObject();
                 obj_6.put("data","p_s_unidades_ac");
                 arreglo_campos_xml.add(obj_6);

        ASObject obj_7 = new ASObject();
                 obj_7.put("data","s_unidades_ac");
                 arreglo_campos_xml.add(obj_7);

        ASObject obj_8 = new ASObject();
                 obj_8.put("data","s_unidades_an");
                 arreglo_campos_xml.add(obj_8);

        ASObject obj_9 = new ASObject();
                 obj_9.put("data","evo_s_unidades_ac");
                 arreglo_campos_xml.add(obj_9);

        ASObject obj_10 = new ASObject();
                 obj_10.put("data","p_e_pcsi_ac");
                 arreglo_campos_xml.add(obj_10);

        ASObject obj_11 = new ASObject();
                 obj_11.put("data","e_pcsi_ac");
                 arreglo_campos_xml.add(obj_11);

        ASObject obj_12 = new ASObject();
                 obj_12.put("data","e_pcsi_an");
                 arreglo_campos_xml.add(obj_12);

        ASObject obj_13 = new ASObject();
                 obj_13.put("data","evo_e_pcsi_ac");
                 arreglo_campos_xml.add(obj_13);
                 /*

        arreglo_campos_xml.add("");
        arreglo_campos_xml.add("");
        arreglo_campos_xml.add("");
        arreglo_campos_xml.add("");
        */

    }////FIN FIN generarMetricasXml

    
    private static void generarGrupoMetricasNoSeleccionadas(ArrayList arreglo_chbox_no_seleccionados)
    {
            ObjetoMetricaNoSeleccionada obj = new ObjetoMetricaNoSeleccionada();
            obj.periodo = " Stock  unidades ";
            obj.posicion_inicial = 7;
            obj.posicion_final = 10;
            arreglo_chbox_no_seleccionados.add(obj);
    }////FIN FIN FIN generarGrupoMetricasNoSeleccionadas

    private static void generarArregloFechas(ArrayList arreglo_fechas)
    {
        ///ArrayList lista = new ArrayList();
        /*
        ObjetoFiltroJava obj_desde_flex_f1 = new ObjetoFiltroJava();
                         obj_desde_flex_f1.label = "f1";
                         obj_desde_flex_f1.data = "2013-01-01";
                         obj_desde_flex_f1.filtro = "";
                         obj_desde_flex_f1.tabla = 0;
                         obj_desde_flex_f1.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f1);

        ObjetoFiltroJava obj_desde_flex_f2 = new ObjetoFiltroJava();
                         obj_desde_flex_f2.label = "f2";
                         obj_desde_flex_f2.data = "2013-02-01";
                         obj_desde_flex_f2.filtro = "";
                         obj_desde_flex_f2.tabla = 0;
                         obj_desde_flex_f2.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f2);


        ObjetoFiltroJava obj_desde_flex_f3 = new ObjetoFiltroJava();
                         obj_desde_flex_f3.label = "f3";
                         obj_desde_flex_f3.data = "2013-03-01";
                         obj_desde_flex_f3.filtro = "";
                         obj_desde_flex_f3.tabla = 0;
                         obj_desde_flex_f3.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f3);
        */
        
        ObjetoFiltroJava obj_desde_flex_f4 = new ObjetoFiltroJava();
                         obj_desde_flex_f4.label = "f4";
                         obj_desde_flex_f4.data = "2013-04-01";
                         obj_desde_flex_f4.filtro = "";
                         obj_desde_flex_f4.tabla = 0;
                         obj_desde_flex_f4.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f4);

        /*
        ObjetoFiltroJava obj_desde_flex_f5 = new ObjetoFiltroJava();
                         obj_desde_flex_f5.label = "f5";
                         obj_desde_flex_f5.data = "2012-05-01";
                         obj_desde_flex_f5.filtro = "";
                         obj_desde_flex_f5.tabla = 0;
                         obj_desde_flex_f5.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f5);

        ObjetoFiltroJava obj_desde_flex_f6 = new ObjetoFiltroJava();
                         obj_desde_flex_f6.label = "f6";
                         obj_desde_flex_f6.data = "2012-06-01";
                         obj_desde_flex_f6.filtro = "";
                         obj_desde_flex_f6.tabla = 0;
                         obj_desde_flex_f6.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f6);

        ObjetoFiltroJava obj_desde_flex_f7 = new ObjetoFiltroJava();
                         obj_desde_flex_f7.label = "f7";
                         obj_desde_flex_f7.data = "2012-07-01";
                         obj_desde_flex_f7.filtro = "";
                         obj_desde_flex_f7.tabla = 0;
                         obj_desde_flex_f7.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f7);

        ObjetoFiltroJava obj_desde_flex_f8 = new ObjetoFiltroJava();
                         obj_desde_flex_f8.label = "f8";
                         obj_desde_flex_f8.data = "2012-08-01";
                         obj_desde_flex_f8.filtro = "";
                         obj_desde_flex_f8.tabla = 0;
                         obj_desde_flex_f8.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f8);

        ObjetoFiltroJava obj_desde_flex_f9 = new ObjetoFiltroJava();
                         obj_desde_flex_f9.label = "f9";
                         obj_desde_flex_f9.data = "2012-09-01";
                         obj_desde_flex_f9.filtro = "";
                         obj_desde_flex_f9.tabla = 0;
                         obj_desde_flex_f9.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f9);

        ObjetoFiltroJava obj_desde_flex_f10 = new ObjetoFiltroJava();
                         obj_desde_flex_f10.label = "f10";
                         obj_desde_flex_f10.data = "2012-10-01";
                         obj_desde_flex_f10.filtro = "";
                         obj_desde_flex_f10.tabla = 0;
                         obj_desde_flex_f10.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f10);


        ObjetoFiltroJava obj_desde_flex_f11 = new ObjetoFiltroJava();
                         obj_desde_flex_f11.label = "f11";
                         obj_desde_flex_f11.data = "2012-11-01";
                         obj_desde_flex_f11.filtro = "";
                         obj_desde_flex_f11.tabla = 0;
                         obj_desde_flex_f11.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f11);

        ObjetoFiltroJava obj_desde_flex_f12 = new ObjetoFiltroJava();
                         obj_desde_flex_f12.label = "f12";
                         obj_desde_flex_f12.data = "2012-12-01";
                         obj_desde_flex_f12.filtro = "";
                         obj_desde_flex_f12.tabla = 0;
                         obj_desde_flex_f12.grupo_checkbox = 0;
        arreglo_fechas.add(obj_desde_flex_f12);
        */
        ////return lista;


    }////FIN generarArregloFechas

    private static void generarArregloFiltrosAdicionales(ArrayList arreglo_filtros_adicionales)
    {
/*
            ObjetoFiltroJava obj_desde_flex_f1 = new ObjetoFiltroJava();
                         obj_desde_flex_f1.label = "ESTA EN EL MAESTRO";
                         obj_desde_flex_f1.data = "1";
                         obj_desde_flex_f1.filtro = "existe_maestro";
                         obj_desde_flex_f1.tabla = 3;
                         obj_desde_flex_f1.grupo_checkbox = 1;
            arreglo_filtros_adicionales.add(obj_desde_flex_f1);

            ObjetoFiltroJava obj_desde_flex_f2 = new ObjetoFiltroJava();
                         obj_desde_flex_f2.label = "SOLO TIA";
                         obj_desde_flex_f2.data = "1";
                         obj_desde_flex_f2.filtro = "tipo_empresa";
                         obj_desde_flex_f2.tabla = 3;
                         obj_desde_flex_f2.grupo_checkbox = 2;
            arreglo_filtros_adicionales.add(obj_desde_flex_f2);

            ObjetoFiltroJava obj_desde_flex_f3 = new ObjetoFiltroJava();
                         obj_desde_flex_f3.label = "AMBOS";
                         obj_desde_flex_f3.data = "3";
                         obj_desde_flex_f3.filtro = "tipo_empresa";
                         obj_desde_flex_f3.tabla = 3;
                         obj_desde_flex_f3.grupo_checkbox = 2;
            arreglo_filtros_adicionales.add(obj_desde_flex_f3);

            ObjetoFiltroJava obj_desde_flex_f4 = new ObjetoFiltroJava();
                         obj_desde_flex_f4.label = "NINGUNO";
                         obj_desde_flex_f4.data = "0";
                         obj_desde_flex_f4.filtro = "tipo_empresa";
                         obj_desde_flex_f4.tabla = 3;
                         obj_desde_flex_f4.grupo_checkbox = 2;
            arreglo_filtros_adicionales.add(obj_desde_flex_f4);
*/
            ObjetoFiltroJava obj_desde_flex_f5 = new ObjetoFiltroJava();
                         obj_desde_flex_f5.label = "NO ES FOLIO INTERNO";
                         obj_desde_flex_f5.data = "0";
                         obj_desde_flex_f5.filtro = "folio_interno";
                         obj_desde_flex_f5.tabla = 1;
                         obj_desde_flex_f5.grupo_checkbox = 4;
            arreglo_filtros_adicionales.add(obj_desde_flex_f5);

    }////FIN FIN generarArregloFiltrosAdicionales


    private static void generarArregloMetricas(ArrayList arreglo_metricas)
    {

            ObjetoFiltroJava obj_desde_flex_f1 = new ObjetoFiltroJava();
                         obj_desde_flex_f1.label = "Entradas Ultimo Costo";
                         obj_desde_flex_f1.data = "e_pcsi";
                         obj_desde_flex_f1.filtro = "";
                         obj_desde_flex_f1.tabla = 0;
                         obj_desde_flex_f1.grupo_checkbox = 0;
            arreglo_metricas.add(obj_desde_flex_f1);

            ObjetoFiltroJava obj_desde_flex_f2 = new ObjetoFiltroJava();
                         obj_desde_flex_f2.label = "Consumo Ultimo Costo";
                         obj_desde_flex_f2.data = "c_pcsi";
                         obj_desde_flex_f2.filtro = "";
                         obj_desde_flex_f2.tabla = 0;
                         obj_desde_flex_f2.grupo_checkbox = 0;
            arreglo_metricas.add(obj_desde_flex_f2);

            ObjetoFiltroJava obj_desde_flex_f3 = new ObjetoFiltroJava();
                            obj_desde_flex_f3.label = "Stock Unidades";
                            obj_desde_flex_f3.data = "s_unid";
                            obj_desde_flex_f3.filtro = "";
                            obj_desde_flex_f3.tabla = 0;
                            obj_desde_flex_f3.grupo_checkbox = 0;
                         
            arreglo_metricas.add(obj_desde_flex_f3);


            

    }////FIN FIN generarArregloMetricas


    private static void generarArregloFiltros(ArrayList arreglo_filtros)
    {

            ObjetoFiltroJava obj_desde_flex_f1 = new ObjetoFiltroJava();
                            obj_desde_flex_f1.label = "e1";
                            obj_desde_flex_f1.data = "132515000";
                            obj_desde_flex_f1.filtro = "";
                            obj_desde_flex_f1.tabla = 0;
                            obj_desde_flex_f1.grupo_checkbox = 0;
            arreglo_filtros.add(obj_desde_flex_f1);


    }////FIN FIN generarArregloNivelesFinal

    private static void generarArregloNivelesFinal(ArrayList arreglo_nivel_final)
    {

            ObjetoFiltroJava obj_desde_flex_f1 = new ObjetoFiltroJava();
                            obj_desde_flex_f1.label = "s1";
                            obj_desde_flex_f1.data = "n1";
                            obj_desde_flex_f1.filtro = "";
                            obj_desde_flex_f1.tabla = 0;
                            obj_desde_flex_f1.grupo_checkbox = 0;
            arreglo_nivel_final.add(obj_desde_flex_f1);

            /*
            ObjetoFiltroJava obj_desde_flex_f2 = new ObjetoFiltroJava();
                         obj_desde_flex_f2.label = "e1";
                         obj_desde_flex_f2.data = "n2";
                         obj_desde_flex_f2.filtro = "";
                         obj_desde_flex_f2.tabla = 0;
                         obj_desde_flex_f2.grupo_checkbox = 0;
            arreglo_nivel_final.add(obj_desde_flex_f2);
*/
    }////FIN FIN generarArregloNivelesFinal


}///FIN FIN CLASE
