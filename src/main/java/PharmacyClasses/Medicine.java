package PharmacyClasses;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
/** Программный класс лекарств*/
@Entity
@Table(name = "pharmacy_bd.medicine")
public class Medicine {
    @Id
    @Column(name = "mID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int medId;
    @Column(name = "mName")
    private String nameOfMedicine;
    @Column(name = "mPrice")
    private int price;
    @Column(name = "mCount")
    private int countOfMedicine;
    @Column(name = "mAvailability")
    private boolean availability;

    @ManyToMany(mappedBy = "medicines", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Disease> diseases = new ArrayList<>();

    /** Метод получения значения ID лекарства*/
    public int getMedId() {
        return medId;
    }
    /** Метод задания значения ID лекарства*/
    public void setMedId(int medId) {
        this.medId = medId;
    }
    /** Метод получения названия лекарства*/
    public String getNameOfMedicine() {
        return nameOfMedicine;
    }
    /** Метод задания названия лекарства*/
    public void setNameOfMedicine(String nameOfMedicine) {
        this.nameOfMedicine = nameOfMedicine;
    }
    /** Метод получения значения количества лекарства*/
    public int getCountOfMedicine() {
        return countOfMedicine;
    }
    /** Метод задания значения количества лекарства*/
    public void setCountOfMedicine(int countOfMedicine) {
        this.countOfMedicine = countOfMedicine;
    }
    /** Метод получения цены лекарства*/
    public int getPrice() {
        return price;
    }
    /** Метод задания цены лекарства*/
    public void setPrice(int price) {
        this.price = price;
    }
    /** Метод получения информации о наличии лекарства в аптеке*/
    public boolean getAvailability() {
        return availability;
    }
    /** Метод задания информации о наличии лекарства в аптеке*/
    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public Medicine(){

    }
}


