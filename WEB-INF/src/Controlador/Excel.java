/**
*
* SIS EXP REQ 2013 002(SAAM 28-01-2013  19-03-2013)
*
* */

package Controlador;


import Modelos.Ordenamiento;
import Modelos.RegistroExcel;
import Modelos.MapArrayCollection;
import Modelos.ObjetoMetricaNoSeleccionada;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Handler;
import java.io.PrintWriter;
import flex.messaging.io.ArrayCollection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.BufferedWriter;
import java.io.File;

import org.xml.sax.helpers.DefaultHandler;

////
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.Date;

import flex.messaging.io.ArrayCollection;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author STALIN ARROYABE MERCHAN SEPTIEMBRE 2012
 */
public class Excel extends DefaultHandler
{

    private String tempVal;
    public ArrayList lista_atributos;
    public ArrayList lista_registros;
    private int indice_mas_bajo = -1;
    private int indice_tmp_mas_bajo = -1;
    private boolean es_detalle_mensual;
    private boolean existe_nivel_estadistico_comparativo;

    public String exportarExcel(
                                    String ruta_archivos
                                    , String nombre_archivo_xml
                                    , String nombre_columna_ordenada
                                    , boolean ordenamiento_ascendente
                                    , ArrayCollection lista_campos
                                    , ArrayCollection lista_checkbox_sin_seleccionar
                                    , String nombre_archivo_checkbox_no_elegidos
                                    , boolean es_detalle_mensual
                                    , boolean existe_nivel_estadistico_comparativo
                               )
    {
        this.existe_nivel_estadistico_comparativo = existe_nivel_estadistico_comparativo;
       ///String nombre_archivo_atributos = nombre_archivo_xml.replaceAll(".xml", ".dat");
       String nombre_archivo_txt = nombre_archivo_xml.replaceAll(".xml", ".txt");
       String ruta_nombre_txt = "";
       this.es_detalle_mensual = es_detalle_mensual;

       this.lista_registros = new ArrayList();
       ///this.leerArchivoAtributos(ruta_archivos, nombre_archivo_atributos);
       this.lista_atributos = new ArrayList();
       ////-System.out.println("INICIO LA :: " + nombre_archivo_xml + "      " + new Date());
       this.obtenerObjetoArrayCollectionDesdeFlex(lista_campos);
       ////-System.out.println("INICIO OCR:: " + nombre_archivo_xml + "      " + new Date());
       this.obtenerCamposDelReporte(ruta_archivos, nombre_archivo_xml);
       this.indice_tmp_mas_bajo = this.indice_mas_bajo;
       ////-System.out.println("INICIO RLR:: " + nombre_archivo_xml + "      " + new Date());
       this.recorrerListaRegistro(this.indice_tmp_mas_bajo);
       ////-System.out.println("INICIO OCO:: "  + nombre_archivo_xml + "      " + new Date());
       int columna_a_ordenar = this.obtenerColumnaOrdenamiento(nombre_columna_ordenada);
       ////System.out.println("numero de columna a ordenar: " + String.valueOf(columna_a_ordenar));
       RegistroExcel obj_re = (RegistroExcel)this.lista_registros.get(0);
       ////-System.out.println("INICIO OLRPE:: " + nombre_archivo_xml + "      " + new Date());
                    obj_re.lista_hijos = this.ordenarListaRegistrosParaExcel( obj_re.lista_hijos
                                                                                , columna_a_ordenar
                                                                                , ordenamiento_ascendente
                                                                                , this.existe_nivel_estadistico_comparativo
                                                                               );

       
       ////-System.out.println("INICIO GTR:: " + nombre_archivo_xml + "      " + new Date());
       ////-ruta_archivos = "c:\\Archivos de programa\\Apache Software Foundation\\Apache Tomcat 6.0.26\\webapps\\";
       this.generarTxtReporte(ruta_archivos, nombre_archivo_txt, obj_re);
       
       ruta_nombre_txt = ruta_archivos + nombre_archivo_txt;

       ///Llamar al Garbage collector
       obj_re = null;
       this.lista_registros = null;
       System.gc();

       ///SIS SIS REQ 2012 074
       ////- System.out.println("INICIO GAMNE:: " + nombre_archivo_xml + "      " + new Date());
       this.generarArchivoMetricasNoElegidas(lista_checkbox_sin_seleccionar, ruta_archivos, nombre_archivo_checkbox_no_elegidos);

       return ruta_nombre_txt;

    }///    FIN FIN  exportarExcel



    private void leerArchivoAtributos(String ruta_archivo, String nombre_archivo_atributos)
    {
             try
            {
               FileReader fr = new FileReader(ruta_archivo + nombre_archivo_atributos);

               BufferedReader entrada = new BufferedReader(fr);
               String linea;

               //RECORRE LAS FILAS
               if((linea = entrada.readLine()) != null)
               {
                   this.lista_atributos.add("nombre");
                   this.lista_atributos.add("codigo");
                   String[] arreglo_celdas = linea.split(";");
                   int tamanio_linea = arreglo_celdas.length;
                   for(int i = 0; i < tamanio_linea; i++)
                   {
                       this.lista_atributos.add(arreglo_celdas[i]);
                   }
               }///FIN WHILE

                entrada.close();

                /*
                File archivo_txt_a_borrar = new File(nombre_archivo_txt);
                if(archivo_txt_a_borrar.delete())
                    System.out.println("Archivo txt: " + nombre_archivo_txt + " borrado con exito");

                 */

            }
            catch(java.io.FileNotFoundException fnfex)
            {
                System.out.println("Error FileNotFoundException en " + nombre_archivo_atributos + " al cargar archivo de atributos: " + fnfex.toString());
            }
            catch(java.io.IOException ioex)
            {
                System.out.println("Error IOException al cargar archivo de atributos: " + ioex.toString());
            }


    }///// leerArchivoAtributos

    private void recorrerListaRegistro(int indice_nivel)
    {
        
        if(indice_nivel > 0)
        {
            int i;
            int tamanio = this.lista_registros.size();
            RegistroExcel obj_ultimo_padre = null;
            for( i = 0; i < tamanio; i++)
            {
                   RegistroExcel obj_actual = (RegistroExcel)this.lista_registros.get(i);
                   if((obj_actual.nivel  == this.indice_tmp_mas_bajo) && (obj_actual.nivel > 0))
                   {
                       obj_ultimo_padre.lista_hijos.add((RegistroExcel)this.lista_registros.remove(i));
                       tamanio = this.lista_registros.size();
                       i = i - 1;
                   }
                   else
                   {
                       obj_ultimo_padre = obj_actual;
                   }

            }
            
            this.indice_tmp_mas_bajo = this.indice_tmp_mas_bajo - 1;
            this.recorrerListaRegistro(this.indice_tmp_mas_bajo);
        }


    }/// FIN recorrerListaRegistro

     private int obtenerColumnaOrdenamiento(String nombre_columna_ordenada)
     {
            int numero_columna = 0;
            String nombre_columna = "";

            for(int i = 0; i < this.lista_atributos.size(); i++)
            {
                nombre_columna = (String)this.lista_atributos.get(i);
                if(nombre_columna.equalsIgnoreCase(nombre_columna_ordenada))
                    numero_columna = i ;

            }
            

            return numero_columna;

     }////obtenerColumnaOrdenamiento

     
    private ArrayList ordenarListaRegistrosParaExcel(
                                                ArrayList lista
                                                , int columna_ordenar
                                                , boolean ordenamiento_ascendente
                                                , boolean existe_nivel_estadistico_comparativo
                                                )
    {
        ////ArrayList lista_nueva = null;
        int i;
        int maximo_columnas_descriptivas;
        if(existe_nivel_estadistico_comparativo == true)
           maximo_columnas_descriptivas = 2;
        else
           maximo_columnas_descriptivas = 1;

        ///Ordenar Lista Actual
        Ordenamiento obj_ordenamiento = new Ordenamiento();
                     obj_ordenamiento.ordenamiento_ascendente = ordenamiento_ascendente;
        if( columna_ordenar > maximo_columnas_descriptivas)
        {
            obj_ordenamiento.campo_a_ordenar = columna_ordenar - 2;
            obj_ordenamiento.es_ordenamiento_numero = true;
            
        }
        else
        {
            obj_ordenamiento.campo_a_ordenar = columna_ordenar;
            obj_ordenamiento.es_ordenamiento_numero = false;
        }
                     
        lista = obj_ordenamiento.OrdenaMerge(lista);


        ///Ordenar Hijos
        for (i = 0; i < lista.size(); i++)
        {
                RegistroExcel obj_re = (RegistroExcel)lista.get(i);
                              obj_re.lista_hijos =  this.ordenarListaRegistrosParaExcel(obj_re.lista_hijos
                                                                                        , columna_ordenar
                                                                                        , ordenamiento_ascendente
                                                                                        , existe_nivel_estadistico_comparativo
                                                                                        );

        }

        return lista;
    }///// ordenarListaRegistrosParaExcel
    

    private void generarTxtReporte(String ruta_archivos
                                    , String nombre_archivo_txt
                                    , RegistroExcel registro_excel_raiz
                                    )
    {
           String delimitador = ";";
           try
           {
                ////-ruta_archivos = "c:\\Archivos de programa\\Apache Software Foundation\\Apache Tomcat 6.0.26\\webapps\\";
                String ruta_nombre_txt = ruta_archivos + nombre_archivo_txt;
                BufferedWriter file_txt = new BufferedWriter(new FileWriter(ruta_nombre_txt));
                file_txt.write(this.registrarLinea(registro_excel_raiz, delimitador));
                file_txt.newLine();
                this.registrarLineasEnTxt(file_txt, registro_excel_raiz.lista_hijos, delimitador);
                file_txt.close();
           }
           catch (IOException e)
           {
                System.out.println("Error al Generar txt  " + nombre_archivo_txt + "   ERROR: " + e.getMessage());
           }


    }//// generarTxtReporte

    private void registrarLineasEnTxt(BufferedWriter file_txt
                                        , ArrayList lista_hijos
                                        , String delimitador
                                    ) 
    {
            try
            {
                for(int i = 0; i < lista_hijos.size(); i++)
                {
                    RegistroExcel obj = (RegistroExcel)lista_hijos.get(i);
                    file_txt.write(this.registrarLinea(obj, delimitador));
                    file_txt.newLine();
                    this.registrarLineasEnTxt(file_txt, obj.lista_hijos, delimitador);
                }

            }
            catch(Exception e)
            {
                System.out.println("Error al Registrar linea en el Txt " + "   ERROR: " + e.getMessage());
            }
    }///// registrarLineaEnTxt

    private String registrarLinea(RegistroExcel registro_excel, String delimitador)
    {
     
        StringBuilder cadena =  new StringBuilder();
                      cadena.append(registro_excel.nombre);
                      cadena.append(delimitador);
                      cadena.append(registro_excel.codigo);
                      cadena.append(delimitador);

        if(this.es_detalle_mensual == false)
        {
            int tamanio_marcas = registro_excel.lista_marcas.size();
            for(int a = 0; a < tamanio_marcas; a++)
            {
                String marca = (String)registro_excel.lista_marcas.get(a);
                cadena.append(marca);
                cadena.append(delimitador);
            }
        }
                      
        int tamanio = registro_excel.lista_campos.size();
        for(int i = 0; i < tamanio; i++)
        {
                Double valor = (Double)registro_excel.lista_campos.get(i);
                cadena.append(String.valueOf(valor.doubleValue()));
                cadena.append(delimitador);
                
        }////FIN FOR
        cadena.append(String.valueOf(registro_excel.nivel));
        return cadena.toString();
    }//// FIN FIN registrarLinea
    
    /********************************************************************************************************************
     * ******************************************* OBTENER COLUMNAS DEL ARREGLO *****************************************
     * ******************************************************************************************************************
     */

        private void obtenerCamposDelReporte(String ruta_archivos, String nombre_archivo_xml)
        {
            /////ruta_archivos = "file:///C:/Archivos de programa/Apache Software Foundation/Apache Tomcat 6.0.26/webapps/";
            this.parseDocument(ruta_archivos, nombre_archivo_xml);
            
        }//// camposDelReporte

    	private void parseDocument(String ruta_archivos, String nombre_archivo_xml)
        {
                String nombre_archivo;
                /// ruta_archivos = "file:///C:/Archivos de programa/Apache Software Foundation/Apache Tomcat 6.0.26/webapps/";
                /// ruta_archivos = "http://132.142.160.187/pentaho/server/biserver-ce/tomcat/webapps/CrossDomain/archivos_xml/";

                ///nombre_archivo_xml = "xml_reporte_jpivot_7654664.xml";

                nombre_archivo = ruta_archivos + nombre_archivo_xml;

		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try
                {

			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			//parse the file and also register this class for call backs
			sp.parse(nombre_archivo, this);

                      
		}
                catch(SAXException se)
                {
			System.out.println("Error al leer xml para atributos: " + nombre_archivo + "   ERROR: " + se.getMessage());
		}
                catch(ParserConfigurationException pce)
                {
			System.out.println("Error al leer atributos del xml: " + nombre_archivo + "   ERROR: " + pce.getMessage());
		}
                catch (IOException ie)
                {
			System.out.println("Error al leer atributos del xml: " + nombre_archivo + "   ERROR: " + ie.getMessage());
		}
                
	}///FIN     parseDocument



        //Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
		//reset
		tempVal = "";
		
                /*
                if(qName.equalsIgnoreCase("N_0"))
                {
                            for(int i = 0; i < attributes.getLength(); i++)
                            {
                                 this.lista_atributos.add(attributes.getQName(i));
                            }
                      
                }
                */
                this.obtenerRegistrosParaExcel(this.lista_atributos, this.es_detalle_mensual , attributes, qName);
		
	}//// FIN startElement


	public void characters(char[] ch, int start, int length) throws SAXException
        {
		tempVal = new String(ch,start,length);
	}/// characters

	public void endElement(String uri, String localName, String qName) throws SAXException
        {
                /*
		if(qName.equalsIgnoreCase("N_0"))
                {
                        System.out.println(uri + " -- " + localName + " -- " + qName );

		}
                */
	}///    endElement

        private void obtenerRegistrosParaExcel(ArrayList lista_atributos
                                                , boolean es_detalle_mensual
                                                , Attributes attributes
                                                , String nombre_etiqueta)
        {
                RegistroExcel obj_registro_excel = null;
                String nombre_atributo = "";
                String valor_atributo = "";
                for(int i = 0; i < lista_atributos.size(); i++)
                {
                    ////nombre_atributo = attributes.getQName(i);
                    nombre_atributo = (String)lista_atributos.get(i);
                    valor_atributo = attributes.getValue(nombre_atributo);
                    
                    if( i == 0 )
                    {
                       obj_registro_excel = new RegistroExcel();
                       obj_registro_excel.codigo = valor_atributo;
                       obj_registro_excel.nivel = Integer.parseInt(nombre_etiqueta.split("_")[1]);
                       if(obj_registro_excel.nivel > this.indice_mas_bajo)
                           this.indice_mas_bajo = obj_registro_excel.nivel;
                    }
                    else
                    {
                        if( i == 1 )
                        {
                           obj_registro_excel.nombre = valor_atributo;
                        }
                        else
                        {
                            if(valor_atributo.equalsIgnoreCase(" ") == true)
                                obj_registro_excel.lista_marcas.add(valor_atributo);
                            else
                            {
                                 if (this.tieneLetra(valor_atributo) == true)
                                    obj_registro_excel.lista_marcas.add(valor_atributo);
                                 else
                                 {
                                        ///Float number = new Float(valor_atributo);
                                        ////float tmp = number.floatValue();
                                        obj_registro_excel.lista_campos.add(new Double(valor_atributo));
                                 }
                            }
                        }
                    }
                ////System.out.print(nombre_atributo + ";");
                }///// FIN FOR
                
                ////System.out.print("\n");
                this.lista_registros.add(obj_registro_excel);

        }////obtenerDatosParaExcel


        private void obtenerObjetoArrayCollectionDesdeFlex(
                                                ArrayCollection arreglo_elementos
                                                 )
        {

                    this.lista_atributos.add("codigo");
                    this.lista_atributos.add("nombre");
                    List<HashMap<Object, Object>> maplist;
                    HashMap<Object,Object> mapa;
                    int i;



                    maplist = MapArrayCollection.convertArrayCollectionToList(arreglo_elementos);

                          for(i=0; i < maplist.size(); i++)
                          {
                                mapa = maplist.get(i);
                                Iterator iterator = mapa.keySet().iterator();
                                while(iterator.hasNext())
                                {
                                       Object valor = iterator.next();
                                       ////Object nombre = iterator.next();
                                       ////System.out.println("campo desde adg flex: " + mapa.get(valor).toString());
                                       this.lista_atributos.add(mapa.get(valor).toString());
                                }//fin while lista de mapas
                           }//Fin For


        }////FIN obtenerObjetoArrayCollectionDesdeFlex


        private boolean tieneLetra(String valor_atributo)
        {
                boolean encontro_letra = false;
                char[] arreglo_letra_va =  valor_atributo.toCharArray();
                char[] letra;
                       letra =new char[26];
                for(int i = 0; i < arreglo_letra_va.length; i++)
                {
                  
                    for (int j = 0; j < 26; j++)
                    {
                        letra[j] = (char) ('A' + j );

                        if(arreglo_letra_va[i] == letra[j])
                        {
                            i = arreglo_letra_va.length + 1000;
                            encontro_letra = true;
                            break;
                        }
                    }///FIN DE FOR LETRA
                }///FIN FOR DE VALOR_ATRIBUTO
            return encontro_letra;
        }////FIN FIN tieneLetra
    /*********************************************************************************************
     **************** SIS SIS REQ 2012 074 Para archivo de grupo metricas no elegidos ************
     *********************************************************************************************/



    private void generarArchivoMetricasNoElegidas(ArrayCollection arreglo_elementos
                                                        , String ruta
                                                        , String nombre_archivo_checkbox_no_elegidos
                                                     )
    {

           String delimitador = ";";
           try
           {
                ////-ruta = "c:\\Archivos de programa\\Apache Software Foundation\\Apache Tomcat 6.0.26\\webapps\\";
                String ruta_nombre_gmne = ruta + nombre_archivo_checkbox_no_elegidos;
                BufferedWriter file_gmne = new BufferedWriter(new FileWriter(ruta_nombre_gmne));
                for(int i = 0; i < arreglo_elementos.size(); i++)
                {
                    ObjetoMetricaNoSeleccionada obj_m_n_s = (ObjetoMetricaNoSeleccionada)arreglo_elementos.get(i);
                    file_gmne.write(obj_m_n_s.periodo.trim() + delimitador);
                    file_gmne.write(String.valueOf(obj_m_n_s.posicion_inicial) + delimitador);
                    file_gmne.write(String.valueOf(obj_m_n_s.posicion_final));
                    file_gmne.newLine();
                }
                file_gmne.close();
           }
           catch (IOException e)
           {
                System.out.println("Error al Generar archivo  " + nombre_archivo_checkbox_no_elegidos + "   ERROR: " + e.getMessage());
           }

    }//// FIN FIN generarArchivoMetricasNoElegidas
    
}///FIN CLASE Excel
