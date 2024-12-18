package PharmacyClasses;

import javax.persistence.*;
import java.time.LocalDateTime;
/** Программный класс операций*/
@Entity
@Table(name = "pharmacy_bd.sales")
public class Sales {
    @Id
    @Column(name = "sID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int salesId;
    @Column(name = "date")
    private LocalDateTime date;
    @Column(name = "sCount")
    private int salesCount;
    @Column(name = "sType")
    private boolean salesType;
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "mID", referencedColumnName = "mID")
    private Medicine medID;
    /** Метод получения значения ID операции*/
    public int getSalesId() {
        return salesId;
    }
    /** Метод задания значения ID операции*/
    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }
    /** Метод получения даты операции*/
    public LocalDateTime getDate() {
        return date;
    }
    /** Метод задания даты операции*/
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    /** Метод получения количества лекарств операции*/
    public int getCount() {
        return salesCount;
    }
    /** Метод задания количества лекарств операции*/
    public void setCount(int salesCount) {
        this.salesCount = salesCount;
    }
    /** Метод получения типа операции*/
    public boolean getType() {
        return salesType;
    }
    /** Метод задания типа операции*/
    public void setType(boolean salesType) {
        this.salesType = salesType;
    }
    /** Метод получения лекарства операции*/
    public Medicine getMID() {
        return medID;
    }
    /** Метод задания лекарства операции*/
    public void setMID(Medicine medID) {
        this.medID = medID;
    }

    public Sales(){

    }
}

