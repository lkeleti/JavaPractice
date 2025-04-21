package dev.lkeleti.invotraxapp.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateSellerCompanyProfileCommand {
    private String headOfficeAddress;
    private String defaultBranchAddress;
    private String companyRegistrationNumber;
}
