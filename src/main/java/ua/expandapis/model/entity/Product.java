package ua.expandapis.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1465750432759670540L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = ProductLocalDateConverter.class)
    @Column(name = "entry_date")
    private LocalDate entryDate;
    @Column(name = "item_code")
    private Integer itemCode;
    @Column(name = "item_name")
    private String itemName;
    @Column(name = "item_quantity")
    private Integer itemQuantity;

    @Convert(converter = ProductStateConverter.class)
    @Column(name = "status")
    private ProductState status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product booking = (Product) o;

        return id.equals(booking.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
