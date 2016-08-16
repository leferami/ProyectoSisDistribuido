/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumer_Client;

/**
 *
 * @author kl
 */
public class SlaveConsume {
    private int id;
    private int capacidad;
    private String estado;
    public SlaveConsume(int id, int capacidad,String estado) {
        this.id = id;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int getCapacidad() {
        return capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }  

    @Override
    public String toString() {
        return "Dispensador: "+ id + ", capacidad =  " + capacidad + " , estado =" + estado;
    }
    
    
    
}
