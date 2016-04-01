/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelos;

import java.util.ArrayList;

/**
 *
 * @author sarroyabe SEPTIEMBRE 2012
 * VER VER http://es.wikipedia.org/wiki/Ordenamiento_por_mezcla
 */

public class Ordenamiento
{

     ///private double A[];
    public int campo_a_ordenar;
    public boolean ordenamiento_ascendente;
    public boolean es_ordenamiento_numero;

    public ArrayList OrdenaMerge(ArrayList L)
    {
        int n = L.size();
        if (n > 1)
        {
            int m = (int) (Math.ceil(n/2.0));
            ArrayList L1 = new ArrayList();
            ArrayList L2 = new ArrayList();
 
            for(int i = 0; i < m; i++)
            {
                L1.add((RegistroExcel)L.get(i));
            }

            for(int j = m; j < n; j++)
            {
                L2.add((RegistroExcel)L.get(j));
            }

            L = mergeNumeros(OrdenaMerge(L1), OrdenaMerge(L2));
        }
        
        return L;

    }////FIN OrdenaMergeNumeros


         private ArrayList mergeNumeros(ArrayList L1, ArrayList L2)
         {
                 ///int[] L = new int[L1.length+L2.length];
                 ArrayList L = new ArrayList();
                 int i = 0;
                 boolean es_menor_valor_1 = false;
                 Object valor_1;
                 Object valor_2;

                 while( (L1.size() != 0) && (L2.size() != 0) )
                 {
                     RegistroExcel obj_1 = (RegistroExcel)L1.get(0);
                     RegistroExcel obj_2 = (RegistroExcel)L2.get(0);

                     if(this.es_ordenamiento_numero)
                     {
                        valor_1 = (Object)obj_1.lista_campos.get(this.campo_a_ordenar);
                        valor_2 = (Object)obj_2.lista_campos.get(this.campo_a_ordenar);
                     }
                     else
                     {
                        if(this.campo_a_ordenar == 0)
                        {
                            valor_1 = (Object)obj_1.codigo;
                            valor_2 = (Object)obj_2.codigo;
                        }
                        else
                        {   
                            if(this.campo_a_ordenar == 1)
                            {
                                valor_1 = (Object)obj_1.nombre;
                                valor_2 = (Object)obj_2.nombre;
                            }
                            else
                            {
                               int indice_marca = this.campo_a_ordenar - 2;
                               valor_1 = (Object)obj_1.lista_marcas.get(indice_marca);
                               valor_2 = (Object)obj_2.lista_marcas.get(indice_marca);
                            }
                        }
                     }///FIN IF ORDENAMIENTO NUMERICO
                     
                     es_menor_valor_1 = this.esMenorPrimerValor(valor_1, valor_2);

                     if(this.ordenamiento_ascendente)
                     {
                             
                            if(es_menor_valor_1)
                            {
                                 ////L = this.siCumpleCondicion(L, L1, L2, i);
                                    L.add(i++, L1.get(0));
                                    L1 = eliminar(L1);
                                    if (L1.size() == 0)
                                    {
                                        while (L2.size() != 0)
                                        {
                                             ///L[i++] = L2[0];
                                             L.add(i++, L2.get(0));
                                             L2 = eliminar(L2);
                                        }
                                    }
                             }
                             else
                             {
                                 ///L = this.noCumpleCondicion(L, L1, L2, i);
                                   L.add(i++, L2.get(0));
                                   L2 = eliminar(L2);
                                   if (L2.size() == 0)
                                   {
                                        while (L1.size() != 0)
                                        {
                                                                 ////L[i++] = L1[0];
                                                                L.add(i++, L1.get(0));
                                                                L1 = eliminar(L1);
                                        }
                                   }
                             }
                    }
                    else
                    {
                            if(es_menor_valor_1)
                             {
                                 ////L = this.siCumpleCondicion(L, L1, L2, i);

                                    L.add(i++, L1.get(0));
                                    L1 = eliminar(L1);
                                    if (L1.size() == 0)
                                    {
                                        while (L2.size() != 0)
                                        {
                                             ///L[i++] = L2[0];
                                             L.add(i++, L2.get(0));
                                             L2 = eliminar(L2);
                                        }
                                    }
                             }
                             else
                             {
                                ////L = this.noCumpleCondicion(L, L1, L2, i);
                                   L.add(i++, L2.get(0));
                                   L2 = eliminar(L2);
                                   if (L2.size() == 0)
                                   {
                                        while (L1.size() != 0)
                                        {
                                                                 ////L[i++] = L1[0];
                                                                L.add(i++, L1.get(0));
                                                                L1 = eliminar(L1);
                                        }
                                   }
                             }

                    }////FIN ORDENAMIENTO


                 }///FIN WHILE

                 return L;


    }///FIN mergeNumeros

    private boolean esMenorPrimerValor(Object valor_1, Object valor_2)
    {
        boolean resultado = false;
        
        if(this.es_ordenamiento_numero)
        {
             Double valor_numerico_1 = (Double) valor_1;
             Double valor_numerico_2 = (Double) valor_2;
             if(this.ordenamiento_ascendente)
             {
                 if(valor_numerico_1 < valor_numerico_2)
                    resultado = true;
                 else
                    resultado = false;
             }
             else
             {
                 if(valor_numerico_2 < valor_numerico_1)
                     resultado = true;
                 else
                     resultado = false;
             }
        }
        else
        {
            String cadena_1 = (String) valor_1;
            String cadena_2 = (String) valor_2;
            if(this.ordenamiento_ascendente)
            {
                if(cadena_1.compareToIgnoreCase(cadena_2) < 0)
                    resultado = true;
                else
                    resultado = false;
            }
            else
            {
                if(cadena_2.compareToIgnoreCase(cadena_1) < 0)
                    resultado = true;
                else
                    resultado = false;
            }
        }

        return resultado;

    }////// esMenorValor1

   

         
    private ArrayList eliminar(ArrayList l)
    {
        ArrayList L = new ArrayList();

        for(int i = 1; i < l.size(); i++)
        {
            ///L[i-1] = l[i];
            L.add(i-1, l.get(i));
        }

        return L;
        
    }/// eliminar



}////FIN CLASE Ordenamiento
