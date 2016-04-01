/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modelos;

import java.util.ArrayList;

/**
 *
 * @author sarroyabe
 */

public class RegistroExcel
{
        public String codigo;
        public String nombre;
        public int nivel;
        public ArrayList <String> lista_marcas;
        public ArrayList <Double> lista_campos;
        public ArrayList <RegistroExcel> lista_hijos;
        
        public RegistroExcel()
        {
             lista_campos = new ArrayList();
             lista_hijos = new ArrayList();
             lista_marcas = new ArrayList();
             
        }///Fin Constructor

}/// FIN CLASE RegistroExcel
