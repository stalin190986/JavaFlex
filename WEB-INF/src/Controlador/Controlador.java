/**
*
* SIS EXP REQ 2013 002(SAAM 28-01-2013  19-03-2013)
* 
* */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Controlador;

import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Date;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.URLDecoder;
import javax.servlet.ServletOutputStream;

import java.io.FileOutputStream;
/*
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.Orientation;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableFont.FontName;
*/
/*
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
*/
/*
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
*/
import java.util.Map;
import java.util.HashMap;
import javax.jws.soap.SOAPBinding.Style;
import java.util.List;
import Modelos.GrupoMetricasOcultas;

/**
 *
 * @author STALIN ARROYABE
 */
public class Controlador extends HttpServlet
{
   static final int NUMERO_COLUMNAS_DESCRIPCION = 2;
   private ArrayList lista_colores;
   private String file_path;
   private String lista_clases_css[] = {"fila_1"
                                        , "fila_2"
                                        , "fila_3"
                                        , "fila_4"
                                        , "fila_5"
                                        , "fila_6"
                                        , "fila_7"
                                        };
   

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
            response.reset();
            String contenido_html = "";
            String sufijo = "";
            String es_detalle_mensual = request.getParameter("esDetalleMensual");
            boolean es_excel_detalle_mensual = es_detalle_mensual.equalsIgnoreCase("true");
            if(es_excel_detalle_mensual == true)
                sufijo = "_dm_";
            else
                sufijo = "_com_";
            String nombre_archivo_txt = request.getParameter("rutaArchivoTxt");
            System.out.println("Inicio de generacion de archivo excel: " + nombre_archivo_txt + "  " + new Date());
            
            String ip_servidor_window = request.getParameter("ipServidorWindow");
            String ruta_fuente_vb6 = request.getParameter("rutaFuenteVb6");
            String carpeta_excel_vb6 = request.getParameter("carpetaExcelVb6");
            String ip_servidor_actual = request.getParameter("ipServidorActual").split(":")[0];
            int tiempo_espera = Integer.parseInt(request.getParameter("delay"));
	    
            
            String ruta_archivo_grupo_metricas_ocultas = request.getParameter("rutaArchivoGrupoMetricasOcultas");
            String titulo_reporte = request.getParameter("titulo");
            String nombre_numero = request.getParameter("idReporte");
            String columna_ordenamiento = request.getParameter("columnaOrdenamiento");
            String forma_ordenamiento = request.getParameter("formaOrdenamiento");

            String archivo_excel = "reporte_multiples_jerarquias_metricas" + sufijo + nombre_numero;
            String nombre_archivo_excel = archivo_excel + ".xlsx";
            response.setHeader("Content-type","application/vnd.ms-excel");
            response.setHeader("Content-disposition","inline; filename=" + nombre_archivo_excel);
            
           String ruta_archivo_excel = this.obtenerRutaArchivosTmp(nombre_archivo_txt);
           String archivo_tmp = ruta_archivo_excel + archivo_excel;
           FileWriter fstream = new FileWriter(archivo_tmp + ".html");
           BufferedWriter out_writer = new BufferedWriter(fstream);
           if( es_excel_detalle_mensual == true)
           {
                int numero_metricas_por_grupo = Integer.parseInt(request.getParameter("numeroMetricasPorGrupo"));
                String numero_columnas_totales_tmp = request.getParameter("numeroColumnasTotales");
                String[] lista_columnas_cabeceras = request.getParameter("columnasCabeceras").split(";");
                String[] lista_periodos = request.getParameter("periodosTiempo").split(";");
                contenido_html = this.generarHtmlParaReporteDetalleMensual
                                                        (
                                                         nombre_archivo_txt
                                                        ,  ruta_archivo_grupo_metricas_ocultas
                                                        ,  titulo_reporte
                                                        ,  columna_ordenamiento
                                                        ,  forma_ordenamiento
                                                        ,  numero_columnas_totales_tmp
                                                        ,  lista_columnas_cabeceras
                                                        ,  lista_periodos
                                                        ,  archivo_excel
                                                        );
           }
           else
           {
                String[] lista_grupo_metrica_comparativo = request.getParameter("grupoMetricasComparativo").split(";");
                String[] lista_metricas_comparativo = request.getParameter("metricasComparativo").split(";");
                String[] lista_cantidad_metrica_grupo_comparativo = request.getParameter("cantidadMetricaGrupoComparativo").split(";");
                String cantidad_flag_descriptivos = request.getParameter("cantidadFlagDescriptivos");
                contenido_html = this.generarHtmlParaReporteComparativo
                                                    (
                                                         nombre_archivo_txt
                                                        ,  ruta_archivo_grupo_metricas_ocultas
                                                        ,  titulo_reporte
                                                        ,  columna_ordenamiento
                                                        ,  forma_ordenamiento
                                                        ,  lista_cantidad_metrica_grupo_comparativo
                                                        ,  lista_grupo_metrica_comparativo
                                                        ,  lista_metricas_comparativo
                                                        ,  archivo_excel
                                                        ,  Integer.parseInt(cantidad_flag_descriptivos)
                                                        );
           }
           
           out_writer.write(contenido_html);
           out_writer.close();
           

           try
           {
                   ////String ip_servidor_window = "132.142.160.6";
                   ////String ip_servidor_actual = "132.142.160.187";
                   ////String ruta_fuente_vb6 = "c:\\BI\\HtmlToExcel\\";
                   ////String carpeta_excel_vb6 = "archivos_excel\\";

                   Process process;

                   String shell_compresion_descompresion = "/pentaho/server/biserver-ce/tomcat/webapps/CrossDomain/sh_comprimir_descomprimir.sh ";
                   process = Runtime.getRuntime().exec(shell_compresion_descompresion + ruta_archivo_excel + " " + archivo_excel + " 1");

                   String parametros = ip_servidor_window + " " + ip_servidor_actual;
                          parametros += " " + archivo_excel + " " + ruta_fuente_vb6 + " " + carpeta_excel_vb6;
                          parametros += " " + ruta_archivo_excel;

                   System.out.println("Inicia transferencia de HTML ZIP para conversion a excel del archivo: " + ruta_archivo_excel + "   " + archivo_excel + "  " + new Date());

                   process = Runtime.getRuntime().exec("/usr/bin/expect /pentaho/server/biserver-ce/tomcat/webapps/CrossDomain/exp_convertir_html_excel.exp " + parametros);

                   Thread.sleep(tiempo_espera + (10 * 1000));
                   System.out.println("Fin de conversion HTML a EXCEL " + archivo_excel + "  " + new Date());


                   File file_excel = new File(ruta_archivo_excel + archivo_excel + ".xlsx");
                   byte[] arreglo_byte = new byte[(int) file_excel.length()];
                   FileInputStream file_input_stream = new FileInputStream(file_excel);
                   file_input_stream.read(arreglo_byte);
                   ServletOutputStream out = response.getOutputStream();
                   out.write(arreglo_byte);
                   out.close();
                   System.out.println("Fin de generacion de archivo excel: " + archivo_excel + "  " + new Date());
        }
        catch (Exception ex)
        {
            System.out.println("Error en el delay: " + ex.toString());
        }

        
    }////   FIN FIN processRequest

    private String generarHtmlParaReporteDetalleMensual(
                                                         String nombre_archivo_txt
                                                        , String ruta_archivo_grupo_metricas_ocultas
                                                        , String titulo_reporte
                                                        , String columna_ordenamiento
                                                        , String forma_ordenamiento
                                                        , String numero_columnas_totales_tmp
                                                        , String[]  lista_columnas_cabeceras
                                                        , String[] lista_periodos
                                                        , String archivo_excel
                                                        )
    throws ServletException, IOException
    {

        String contenido_html = "";
        
        
        int numero_columnas_totales = Integer.parseInt(numero_columnas_totales_tmp);


        String titulo_ordenamiento = " ORDENADO " + forma_ordenamiento +"   POR LA COLUMNA: " + columna_ordenamiento;



        String col_span = String.valueOf(numero_columnas_totales + 5);
        StringBuilder tabla_tmp = new StringBuilder();


           tabla_tmp.append("<html>" + "\n");
           tabla_tmp.append("<head>" + "\n");
           tabla_tmp.append("<style type='" + "text/css" + "'>\n");
           tabla_tmp.append(" ." + lista_clases_css[0] + " {text-align:center; font-weight:bold; font-size:13px; background-color: #F3F781} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[1] + " {text-align:center; background-color: #A9BCF5} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[2] + " {text-align:center; background-color: #D8D8D8} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[3] + " {text-align:center; background-color: #CEE3F6} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[4] + " {text-align:center; background-color: #F8E0E0} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[5] + " {text-align:center; background-color: #FFFFFF} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[6] + " {text-align:center; background-color: #D8CEF6} " + "\n");
           tabla_tmp.append("</style>" + "\n");
           tabla_tmp.append("</head>" + "\n");
           tabla_tmp.append("<body>" + "\n");
           tabla_tmp.append("<table id = 'tabla' border = '1' STYLE='font-size: 10px;'>" + "\n");
           tabla_tmp.append("<thead id = 'cabecera'>" + "\n");
           tabla_tmp.append("<tr> <th style='background-color:#FDFCD5' colspan='");
           tabla_tmp.append(col_span);
           tabla_tmp.append("'>");
           tabla_tmp.append(titulo_reporte);
           tabla_tmp.append("</th> </tr> " + "\n");
           tabla_tmp.append("<tr> <th style='background-color:#FDFCD5' colspan='");
           tabla_tmp.append(col_span);
           tabla_tmp.append("'>");
           tabla_tmp.append(titulo_ordenamiento);
           tabla_tmp.append("</th> </tr> " + "\n");

           /// PERIODOS DE COLUMNAS CABECERAS
           tabla_tmp.append("<tr>");
           tabla_tmp.append("<th style='background-color:#FFFFDF' colspan='2'>");
           tabla_tmp.append("</th>" + "\n");
           String color_celda_total = "#F78181";
           String color_celda_periodo = "#81BEF7";
           String color_celda = "";

           
           ArrayList arreglo_gmo = this.leerArchivoGrupoMetricasOcultas(ruta_archivo_grupo_metricas_ocultas);
           for(int a = 0; a < lista_periodos.length; a++)
           {
               if( a == 0)
                   color_celda = color_celda_total;
               else
                   color_celda = color_celda_periodo;
               String nombre_columna_periodo = lista_periodos[a];
               if(this.esGrupoMetricaAOcultar(nombre_columna_periodo, arreglo_gmo) == false)
               {
                    tabla_tmp.append("<th style='background-color:" + color_celda  + "; ' colspan='" + numero_columnas_totales + "'>" + "\n");
                    tabla_tmp.append(nombre_columna_periodo);
                    tabla_tmp.append("</th>" + "\n");
               }
               /////System.out.println(lista_periodos[a]);
           }
           tabla_tmp.append("</tr>" + "\n");

           ///NOMBRE DE COLUMNAS CABECERAS
           tabla_tmp.append("<tr>");
           for(int b = 0; b < lista_columnas_cabeceras.length; b++)
           {
               //numero_metricas_por_grupo
                if(this.esMetricaAOcultar(b,arreglo_gmo) == false)
                {
                   if( b <= ((numero_columnas_totales + NUMERO_COLUMNAS_DESCRIPCION) - 1 ) )
                       color_celda = color_celda_total;
                   else
                       color_celda = color_celda_periodo;

                   tabla_tmp.append("<th style='background-color:" + color_celda + "; font-color:#0B2161'>");
                   tabla_tmp.append("<label>" + lista_columnas_cabeceras[b] + "</label>");
                   tabla_tmp.append("</th>" + "\n" );
                   ////System.out.println(lista_columnas_cabeceras[b]);
               }
           }
           tabla_tmp.append("</tr>" + "\n");
           tabla_tmp.append("</thead>" + "\n");

        /////out.flush();
        /////out.write(tabla_tmp.toString());


        try
        {
           FileReader fr = new FileReader(nombre_archivo_txt);

           BufferedReader entrada = new BufferedReader(fr);
           String linea;

           //RECORRE LAS FILAS
           this.llenarListaColores();

           String estilo_fijo = " style = '";
           ///-String alineamiento = " text-align:center; ";
           String negrita = " font-weight:bold; ";
           String estilo_celda = "";
           int numero_linea_txt = 1;
           tabla_tmp.append("<tbody>" + "\n");
           while((linea = entrada.readLine()) != null)
           {
                   String[] arreglo_celdas = linea.split(";");
                   int tamanio_linea = arreglo_celdas.length;
                   int nivel = Integer.parseInt(arreglo_celdas[tamanio_linea - 1]);
                   String clase_css =  lista_clases_css[nivel];
                   tabla_tmp.append("<tr>");
                   String background= " background-color:" + (String)this.lista_colores.get(nivel);

                   String valor_celda = "";
                   for(int k = 0; k < tamanio_linea - 1; k++)
                   {
                         ///SIS-SIS-REQ-2012-074
                         if(this.esMetricaAOcultar(k,arreglo_gmo) == false)
                         {
                                valor_celda = arreglo_celdas[k];
                                tabla_tmp.append(" <td ");

                                tabla_tmp.append(" class ='" + clase_css + "'");
                                tabla_tmp.append(">");

                                if( k > 1)
                                    tabla_tmp.append(Double.parseDouble(valor_celda));
                                else
                                    tabla_tmp.append(valor_celda);

                                tabla_tmp.append("</td>");
                        }

                   }////FIN FOR
                   tabla_tmp.append("</tr>" + "\n");
                   numero_linea_txt++;

           }///FIN WHILE
           /////out.flush();
           tabla_tmp.append("</tbody>" + "\n");
           tabla_tmp.append("</body>" + "\n");
           tabla_tmp.append("</html>");
           entrada.close();

           contenido_html = String.valueOf(tabla_tmp);
           tabla_tmp = null;
           System.gc();
        }///FIN TRY
        catch(java.io.FileNotFoundException fnfex)
        {
            System.out.println("Error FileNotFoundException en " + archivo_excel + " al Generar Excel en el Controlador: " + fnfex.toString());
        }
        catch(java.io.IOException ioex)
        {
            System.out.println("Error IOException al Generar Excel en el Controlador: " + ioex.toString());
        }
        catch (Exception ex)
        {
            System.out.println("Error en el delay: " + ex.toString());
        }

        return contenido_html;
    }////FIN FIN generarHtmlParaReporteDetalleMensual


      private String generarHtmlParaReporteComparativo(
                                                         String nombre_archivo_txt
                                                        , String ruta_archivo_grupo_metricas_ocultas
                                                        , String titulo_reporte
                                                        , String columna_ordenamiento
                                                        , String forma_ordenamiento
                                                        , String[] lista_cantidad_metrica_grupo_comparativo
                                                        , String[] lista_grupo_metrica_comparativo
                                                        , String[] lista_metricas_comparativo
                                                        , String archivo_excel
                                                        , int cantidad_flag_descriptivos
                                                        )
    throws ServletException, IOException
    {

        String contenido_html = "";
        
        ArrayList arreglo_gmo = this.leerArchivoGrupoMetricasOcultas(ruta_archivo_grupo_metricas_ocultas);
        int numero_columnas_totales = this.obtenerTotalCeldasUsadas
                                                        (
                                                            lista_cantidad_metrica_grupo_comparativo
                                                            , arreglo_gmo
                                                        )
                                                      ;


        String titulo_ordenamiento = " ORDENADO " + forma_ordenamiento +"   POR LA COLUMNA: " + columna_ordenamiento;



        String col_span = String.valueOf(numero_columnas_totales);
        StringBuilder tabla_tmp = new StringBuilder();


           tabla_tmp.append("<html>" + "\n");
           tabla_tmp.append("<head>" + "\n");
           tabla_tmp.append("<style type='" + "text/css" + "'>\n");
           tabla_tmp.append(" ." + lista_clases_css[0] + " {text-align:center; font-weight:bold; font-size:13px; background-color: #F3F781} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[1] + " {text-align:center; background-color: #A9BCF5} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[2] + " {text-align:center; background-color: #D8D8D8} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[3] + " {text-align:center; background-color: #CEE3F6} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[4] + " {text-align:center; background-color: #F8E0E0} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[5] + " {text-align:center; background-color: #FFFFFF} " + "\n");
           tabla_tmp.append(" ." + lista_clases_css[6] + " {text-align:center; background-color: #D8CEF6} " + "\n");
           tabla_tmp.append("</style>" + "\n");
           tabla_tmp.append("</head>" + "\n");
           tabla_tmp.append("<body>" + "\n");
           tabla_tmp.append("<table id = 'tabla' border = '1' STYLE='font-size: 10px;'>" + "\n");
           tabla_tmp.append("<thead id = 'cabecera'>" + "\n");
           tabla_tmp.append("<tr> <th style='background-color:#FDFCD5' colspan='");
           tabla_tmp.append(col_span);
           tabla_tmp.append("'>");
           tabla_tmp.append(titulo_reporte);
           tabla_tmp.append("</th> </tr> " + "\n");
           tabla_tmp.append("<tr> <th style='background-color:#FDFCD5' colspan='");
           tabla_tmp.append(col_span);
           tabla_tmp.append("'>");
           tabla_tmp.append(titulo_ordenamiento);
           tabla_tmp.append("</th> </tr> " + "\n");

           String color_celda_total = "#F78181";
           String color_celda_periodo = "#81BEF7";
           String color_celda = "";
           
           
           int numero_columnas = 0;
           for(int a = 0; a < lista_grupo_metrica_comparativo.length; a++)
           {
               String nombre_columna_periodo = lista_grupo_metrica_comparativo[a];

               
               if(this.esGrupoMetricaAOcultar(nombre_columna_periodo, arreglo_gmo) == false)
               {
                       if( a == 0)
                       {
                           color_celda = color_celda_total;
                           numero_columnas = cantidad_flag_descriptivos;
                       }
                       else
                       {
                           color_celda = color_celda_periodo;
                           numero_columnas = this.buscarCantidadMetricas(lista_cantidad_metrica_grupo_comparativo
                                                                            , nombre_columna_periodo
                                                                         );
                       }
                        tabla_tmp.append("<th style='background-color:" + color_celda  + "; ' colspan='" + numero_columnas + "'>" + "\n");
                        tabla_tmp.append(nombre_columna_periodo);
                        tabla_tmp.append("</th>" + "\n");
               }
               /////System.out.println(lista_periodos[a]);
           }
           tabla_tmp.append("</tr>" + "\n");

           ///NOMBRE DE COLUMNAS METRICAS
           tabla_tmp.append("<tr>");
           for(int b = 0; b < lista_metricas_comparativo.length; b++)
           {
                if(this.esMetricaAOcultar(b,arreglo_gmo) == false)
                {
                   if( b <= ( cantidad_flag_descriptivos - 1 ) )
                       color_celda = color_celda_total;
                   else
                       color_celda = color_celda_periodo;

                   tabla_tmp.append("<th style='background-color:" + color_celda + "; font-color:#0B2161'>");
                   tabla_tmp.append("<label>" + lista_metricas_comparativo[b] + "</label>");
                   tabla_tmp.append("</th>" + "\n" );
                   ////System.out.println(lista_columnas_cabeceras[b]);
               }
           }
           /*
           tabla_tmp.append("</tr>" + "\n");
           tabla_tmp.append("</thead>" + "\n");
           tabla_tmp.append("</html>");
           contenido_html = String.valueOf(tabla_tmp);
           */
        
        try
        {
           FileReader fr = new FileReader(nombre_archivo_txt);

           BufferedReader entrada = new BufferedReader(fr);
           String linea;

           //RECORRE LAS FILAS
           this.llenarListaColores();

           String estilo_fijo = " style = '";
           ///-String alineamiento = " text-align:center; ";
           String negrita = " font-weight:bold; ";
           String estilo_celda = "";
           int numero_linea_txt = 1;
           tabla_tmp.append("<tbody>" + "\n");
           while((linea = entrada.readLine()) != null)
           {
                   String[] arreglo_celdas = linea.split(";");
                   int tamanio_linea = arreglo_celdas.length;
                   int nivel = Integer.parseInt(arreglo_celdas[tamanio_linea - 1]);
                   String clase_css =  lista_clases_css[nivel];
                   tabla_tmp.append("<tr>");
                   String background= " background-color:" + (String)this.lista_colores.get(nivel);

                   String valor_celda = "";
                   for(int k = 0; k < tamanio_linea - 1; k++)
                   {
                         ///SIS-SIS-REQ-2012-074
                         if(this.esMetricaAOcultar(k,arreglo_gmo) == false)
                         {
                                valor_celda = arreglo_celdas[k];
                                tabla_tmp.append(" <td ");

                                tabla_tmp.append(" class ='" + clase_css + "'");
                                tabla_tmp.append(">");

                                if( k > ( cantidad_flag_descriptivos - 1))
                                    tabla_tmp.append(Double.parseDouble(valor_celda));
                                else
                                    tabla_tmp.append(valor_celda);

                                tabla_tmp.append("</td>");
                        }

                   }////FIN FOR
                   tabla_tmp.append("</tr>" + "\n");
                   numero_linea_txt++;

           }///FIN WHILE
           /////out.flush();
           tabla_tmp.append("</tbody>" + "\n");
           tabla_tmp.append("</body>" + "\n");
           tabla_tmp.append("</html>");
           entrada.close();

           contenido_html = String.valueOf(tabla_tmp);
           tabla_tmp = null;
           System.gc();
        }///FIN TRY
        catch(java.io.FileNotFoundException fnfex)
        {
            System.out.println("Error FileNotFoundException en " + archivo_excel + " al Generar Excel en el Controlador: " + fnfex.toString());
        }
        catch(java.io.IOException ioex)
        {
            System.out.println("Error IOException al Generar Excel en el Controlador: " + ioex.toString());
        }
        catch (Exception ex)
        {
            System.out.println("Error en el delay: " + ex.toString());
        }

        
        return contenido_html;
        
    }////FIN FIN generarHtmlParaReporteComparativo

    private int obtenerTotalCeldasUsadas(
                                            String[] lista_cantidad_metrica_grupo_comparativo
                                            , ArrayList arreglo_gmo
                                        )
    {
            int numero_celdas = 0;
            for(int i = 0; i < lista_cantidad_metrica_grupo_comparativo.length; i++)
            {
                String[] arreglo = lista_cantidad_metrica_grupo_comparativo[i].split(":");
                ///-System.out.println(lista_cantidad_metrica_grupo_comparativo[i] + "--" + arreglo[0] + "----" + arreglo[1]);
                numero_celdas += Integer.parseInt(arreglo[1]);
            }

            for(int j = 0; j < arreglo_gmo.size(); j++)
            {
                GrupoMetricasOcultas obj = (GrupoMetricasOcultas)arreglo_gmo.get(j);
                numero_celdas = numero_celdas - (obj.getPosicion_final() - obj.getPosicion_inicial() + 1);
            }
        return numero_celdas;
    }////FIN FIN obtenerTotalCeldasUsadas

    private int buscarCantidadMetricas( String[] lista_cantidad_metrica_grupo_comparativo
                                        , String nombre_columna_periodo
                                      )
    {
        int numero_celdas = 0;
        int tamanio = lista_cantidad_metrica_grupo_comparativo.length;
        for(int i = 0; i < tamanio; i++)
        {
            String[] arreglo = lista_cantidad_metrica_grupo_comparativo[i].split(":");
            if(nombre_columna_periodo.equalsIgnoreCase(arreglo[0]) == true)
            {
                numero_celdas = Integer.parseInt(arreglo[1]);
                i = tamanio + 1000;
            }
        }

        return numero_celdas;
    }///FIN FIN buscarCantidadMetricas


    private ArrayList leerArchivoGrupoMetricasOcultas(String ruta_archivo_grupo_metricas_ocultas)
    {
            ArrayList  arreglo_gmo = new ArrayList();
            try
            {
               FileReader fr = new FileReader(ruta_archivo_grupo_metricas_ocultas);

               BufferedReader entrada = new BufferedReader(fr);
               String linea;
               while((linea = entrada.readLine()) != null)
               {
                   String[] arreglo_celdas = linea.split(";");
                   GrupoMetricasOcultas obj_gmo = new GrupoMetricasOcultas();
                   obj_gmo.setPeriodo(arreglo_celdas[0]);
                   obj_gmo.setPosicion_inicial(Integer.parseInt(arreglo_celdas[1]));
                   obj_gmo.setPosicion_final(Integer.parseInt(arreglo_celdas[2]));
                   arreglo_gmo.add(obj_gmo);
               }

            }
            catch(Exception ex)
            {
                System.out.println("Error en Lectura de Archivo:" + ruta_archivo_grupo_metricas_ocultas + "   " + ex.toString());
            }
            return arreglo_gmo;
    }///FIN FIN leerArchivoGrupoMetricasOcultas

    private boolean esGrupoMetricaAOcultar(String nombre_periodo, ArrayList arreglo_gmo)
    {
            /////boolean resultado = false;

            for(int i = 0; i < arreglo_gmo.size(); i++)
            {
                GrupoMetricasOcultas gmo = (GrupoMetricasOcultas)arreglo_gmo.get(i);
                nombre_periodo = nombre_periodo.trim();
                if(nombre_periodo.equalsIgnoreCase(gmo.getPeriodo()))
                {
                    ////System.out.println("v1: " + nombre_periodo + "   v2:" + gmo.getPeriodo());
                    return true;
                }
            }

            return false;

    }////FIN FIN esGrupoMetricaAOcultar

    private boolean esMetricaAOcultar(int posicion_metrica, ArrayList arreglo_gmo)
    {
            for(int i = 0; i < arreglo_gmo.size(); i++)
            {
                GrupoMetricasOcultas gmo = (GrupoMetricasOcultas)arreglo_gmo.get(i);
                if((posicion_metrica >= gmo.getPosicion_inicial()) && (posicion_metrica <= gmo.getPosicion_final()))
                {
                    return true;
                }
            }

            return false;
    }///FIN FIN esMetricaAOcultar




    private String obtenerRutaArchivosTmp(String ruta_nombre_txt)
    {
            String resultado = "";
            String caracter = "/";
            String[] arreglo = ruta_nombre_txt.split(caracter);

            for(int i = 0; i < arreglo.length - 1; i++)
            {
                resultado += arreglo[i] + caracter;
            }

            return resultado;

    }/////FIN obtenerRutaArchivosTmp

   
   
    private void llenarListaColores()
    {
        this.lista_colores = new ArrayList();

        
        this.lista_colores.add("#F3F781");
        this.lista_colores.add("#A9BCF5");
        this.lista_colores.add("#D8D8D8");
        this.lista_colores.add("#CEE3F6");
        this.lista_colores.add("#F8E0E0");
        this.lista_colores.add("#FFFFFF");
        this.lista_colores.add("#D8CEF6");
        
        /*
        this.lista_colores.add(Colour.GOLD);
        this.lista_colores.add(Colour.GRAY_25);
        this.lista_colores.add(Colour.GREY_25_PERCENT);
        this.lista_colores.add(Colour.LAVENDER);
        this.lista_colores.add(Colour.LIGHT_ORANGE);
        this.lista_colores.add(Colour.VERY_LIGHT_YELLOW);
        this.lista_colores.add(Colour.WHITE);
        */
        /*
        this.lista_colores.add(IndexedColors.BRIGHT_GREEN.getIndex());
        this.lista_colores.add(IndexedColors.LIGHT_YELLOW.getIndex());
        this.lista_colores.add(IndexedColors.LIGHT_TURQUOISE.getIndex());
        this.lista_colores.add(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        this.lista_colores.add(IndexedColors.LIGHT_GREEN.getIndex());
        this.lista_colores.add(IndexedColors.CORAL.getIndex());
        this.lista_colores.add(IndexedColors.WHITE.getIndex());
        */

    }////llenarListaColores


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
