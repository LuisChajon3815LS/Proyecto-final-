package com.gestionventas;
import java.io.*; import java.nio.file.*; import java.util.*;
public class Repositorio<T extends Serializable> {
 private final Path archivo;
 public Repositorio(String nombre){archivo=Paths.get("datos",nombre+".dat");}
 @SuppressWarnings("unchecked") public List<T> cargar(){
  if(!Files.exists(archivo))return new ArrayList<>();
  try(ObjectInputStream in=new ObjectInputStream(Files.newInputStream(archivo))){return (List<T>)in.readObject();}
  catch(Exception e){throw new IllegalStateException("Error leyendo "+archivo+": "+e.getMessage(),e);}
 }
 public void guardar(List<T> lista){
  try{Files.createDirectories(archivo.getParent());try(ObjectOutputStream out=new ObjectOutputStream(Files.newOutputStream(archivo))){out.writeObject(new ArrayList<>(lista));}}
  catch(IOException e){throw new IllegalStateException("Error guardando "+archivo+": "+e.getMessage(),e);}
 }
}
