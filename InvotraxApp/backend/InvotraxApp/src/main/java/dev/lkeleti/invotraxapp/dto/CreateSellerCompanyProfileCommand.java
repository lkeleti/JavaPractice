package dev.lkeleti.invotraxapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateSellerCompanyProfileCommand {
    private Long partnerId;
    private String headOfficeAddress;
    private String defaultBranchAddress;
    private String companyRegistrationNumber;
}
