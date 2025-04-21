package dev.lkeleti.invotraxapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seller_company_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerCompanyProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Partner partner;

    @Column(name = "head_office_address")
    private String headOfficeAddress;

    @Column(name = "default_branch_address")
    private String defaultBranchAddress;

    @Column(name = "company_registration_number")
    private String companyRegistrationNumber;
}
