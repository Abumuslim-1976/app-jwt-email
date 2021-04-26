package uz.pdp.appjwtemail.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @CreationTimestamp
    private Timestamp createdAt;        // qachon va qaysi vaqtda creat qilingani

    @UpdateTimestamp
    private Timestamp updatedAt;        // qachon va qaysi vaqtda update qilingani

    @CreatedBy
    private UUID createdBy;             // kim mahsulotni creat qilgani

    @LastModifiedBy
    private UUID updatedBy;             // kim mahsulotni update qilgani
}
