package PharmacyClasses;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
/** Программный класс болезней*/
@Entity
@Table(name = "pharmacy_bd.disease")
public class Disease {
    @Id
    @Column(name = "dID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int disID;

    @Column(name = "nameOfDisease")
    private String nameOfDisease;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "pharmacy_bd.disease_medicine", // Имя промежуточной таблицы
            joinColumns = @JoinColumn(name = "disease_id", referencedColumnName = "dID"),
            inverseJoinColumns = @JoinColumn(name = "medicine_id", referencedColumnName = "mID")
    )
    private List<Medicine> medicines = new ArrayList<>();
    /** Метод получения значения ID болезни*/
    public int getDisId() {
        return disID;
    }
    /** Метод задания значения ID болезни*/
    public void setDisId(int disID) {
        this.disID = disID;
    }
    /** Метод получения названия болезни*/
    public String getNameOfDisease() {
        return nameOfDisease;
    }
    /** Метод задания названия болезни*/
    public void setNameOfDisease(String nameOfDisease) {
        this.nameOfDisease = nameOfDisease;
    }
    /** Метод получения списка применяемых лекарств*/
    public List<Medicine> getMed() {
        return medicines;
    }
    /** Метод создания списка применяемых лекарств*/
    public void setMed(List<Medicine> medicines) {
        this.medicines = medicines;
    }

    public Disease(){

    }
}
