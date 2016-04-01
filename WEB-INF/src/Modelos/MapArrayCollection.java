
package Modelos;


import flex.messaging.io.ArrayCollection;
import flex.messaging.io.amf.ASObject;
import flex.messaging.io.amf.translator.ASTranslator;

import java.util.*;

/**
 * Realiza un mapeo a una lista de HashMap del ArrayCollection de FLEX
 * @version 1.7
 * @author Arturo Hernándeez Peter
 * @see PagoVsStockService
 */
public class MapArrayCollection
{

   public MapArrayCollection()
   {

   }

  @SuppressWarnings("unchecked")
  public static List<HashMap<Object, Object>> convertArrayCollectionToList(ArrayCollection arrayCollection)
  {
        ArrayList<Object> objectList = new ArrayList<Object>();
        ASTranslator ast = new ASTranslator();
        Object obj;
        ASObject aso;
    
        ////System.out.println("lista de flex: "+arrayCollection.get(2).toString());
        ////System.out.println("tam lista de flex: "+arrayCollection.size());

        for(int i=0;i< arrayCollection.size(); i++)
        {
            obj = new Object();
            aso = new ASObject();

            aso = (ASObject) arrayCollection.get(i);
            aso.setType("java.lang.Object");
            
            /////aso.setType("Modelos.ObjetoFlex");
            obj = (Object) ast.convert(aso, Object.class);
            objectList.add(obj);
            
        }

        List<HashMap<Object, Object>> maplist=new ArrayList<HashMap<Object,Object>>();

        for(Object tempObj:objectList)
        {
            HashMap<Object, Object> hashmap=new HashMap<Object, Object>();
            hashmap=(HashMap<Object, Object>)tempObj;
            //Removing action script metadata
            hashmap.remove("mx_internal_uid");
            maplist.add(hashmap);


        }

        return maplist;

  } // FIN MapArrayCollection

  
  
  public static String intercambiarHeaders(String header,int []posiciones,int tamanio,
                                           boolean esConsumo,boolean esGanancia,
                                           boolean esMargen,boolean esStock,boolean esDiasStock,
                                           boolean esEntradas,boolean esEntradasProv,
                                           boolean esNuevoEliminado,
                                           boolean esOferta,boolean esMarcaPropia,
                                           boolean esExclusion,boolean esBasico)
  {
          int i,j;

          String tmp="";
          String res = "";

          String [] arreglo = new String[tamanio];

          //System.out.println("tamanio en intercambiar Headers: "+tamanio);

          arreglo[0]="DESCRIPCION";  arreglo[1]="CODIGO";



          String []celda = header.split(";");
          String []celdaNueva = new String[tamanio];

          for(i=0;i<tamanio;i++)
              celdaNueva[i]=" ";

          i=0;
          j=0;

          for(j=0;j<celda.length;j++)
          {
              posiciones[j] = buscarXPosicion(arreglo[j], celda);
              celdaNueva[j] = celda[posiciones[j]];
          }

          for(i=0;i<celdaNueva.length;i++)
          {
              if(i<celdaNueva.length-1)
                res+=celdaNueva[i]+";";
              else
                res+=celdaNueva[i];
          }

          //System.out.println("headers intercambiados: "+res);
          return res;
      
  }// FIN intercambiarHeaders

  
  public static String intercambiarCeldas(String cell,int []posiciones,int tam){

      int i,j;

      String tmp="";
      String res = "";

      String []celda = cell.split(";");
      String []celdaNueva = new String[tam];
      
       for(i=0;i<tam;i++)
        celdaNueva[i]="";

       i=0;
       j=0;

      //Intercambio
      for(j=0;j<celda.length;j++){
          celdaNueva[j] = celda[posiciones[j]];
      }

      for(i=0;i<celdaNueva.length;i++){
          if(i<celdaNueva.length-1)
            res+=celdaNueva[i]+";";
          else
            res+=celdaNueva[i];
      }

      return res;
  } // FIN intercambiarCeldas
  
  private static int buscarXPosicion(String s,String[] arreglo)
  {

      int i=0;
      int pos=-1;

      while(i<arreglo.length)
      {
         // System.out.println("Compara: "+s+" con "+arreglo[i]);
          if(s.equalsIgnoreCase(arreglo[i]))
               return i;
          i++;
      }
      return pos;

  }// FIN buscarXPosicion

  public static String headersExcel(String header)
  {
      
      if(header.equalsIgnoreCase("nuevoEliminado")) return "NUEVO O ELIM.";
      else if(header.equalsIgnoreCase("marcaPropia")) return "MARCA PROPIA";
      else if(header.equalsIgnoreCase("partConsumo")) return "PART. C.";
      else if(header.equalsIgnoreCase("consumoActual")) return "ACTUAL C.";
      else if(header.equalsIgnoreCase("consumoAnterior")) return "ANTERIOR C.";
      else if(header.equalsIgnoreCase("evolucionConsumo")) return "EVOL. C.";
      //else if(header.equalsIgnoreCase("diferenciaConsumo")) return "DIFERENCIA C.";
      else if(header.equalsIgnoreCase("partGanancia")) return "PART. G.";
      else if(header.equalsIgnoreCase("gananciaActual")) return "ACTUAL G.";
      else if(header.equalsIgnoreCase("gananciaAnterior")) return "ANTERIOR G.";
      else if(header.equalsIgnoreCase("evolucionGanancia")) return "EVOL. G.";
      //else if(header.equalsIgnoreCase("diferenciaGanancia")) return "DIFERENCIA G.";
      else if(header.equalsIgnoreCase("margenActual")) return "ACTUAL M.";
      else if(header.equalsIgnoreCase("margenAnterior")) return "ANTERIOR M.";
      else if(header.equalsIgnoreCase("partStock")) return "PART. S.";
      else if(header.equalsIgnoreCase("stockActual")) return "ACTUAL S.";
      else if(header.equalsIgnoreCase("stockAnterior")) return "ANTERIOR S.";
      else if(header.equalsIgnoreCase("evolucionStock")) return "EVOL. S.";
      else if(header.equalsIgnoreCase("diasStockActual")) return "ACTUAL D.";
      else if(header.equalsIgnoreCase("diasStockAnterior")) return "ANTERIOR D.";
      else if(header.equalsIgnoreCase("partEntradas")) return "PART. E.";
      else if(header.equalsIgnoreCase("entradasActual")) return "ACTUAL E.";
      else if(header.equalsIgnoreCase("entradasAnterior")) return "ANTERIOR E.";
      else if(header.equalsIgnoreCase("evolucionEntradas")) return "EVOL. E.";

      else if(header.equalsIgnoreCase("partEntradasProv")) return "PART. E.P.";
      else if(header.equalsIgnoreCase("entradasProvActual")) return "ACTUAL E.P.";
      else if(header.equalsIgnoreCase("entradasProvAnterior")) return "ANTERIOR E.P.";
      else if(header.equalsIgnoreCase("evolucionEntradasProv")) return "EVOL. E.P.";


      else  return header;
      
   }// FIN headersExcel
  


  
}// FIN MapArrayCollection