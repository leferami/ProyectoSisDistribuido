/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slave_dispenser;

/**
 *
 * @author josanvel
 */
public class Slave {
    private int id;
    private int capacidad;
    
    public Slave(int id, int capacidad) {
        this.id = id;
        this.capacidad = capacidad;
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

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }  
}
