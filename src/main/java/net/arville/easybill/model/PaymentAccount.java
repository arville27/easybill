package net.arville.easybill.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_account")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PaymentAccount {
    @Id
    @SequenceGenerator(name = "payment_account_id_seq", sequenceName = "payment_account_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "payment_account_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "payment_account_label", nullable = false)
    private String paymentAccountLabel;

    @Column(name = "paymentAccount", nullable = false)
    private String paymentAccount;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    private User user;

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
