package dev.lkeleti.invotraxapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManufacturerCommand {
    private String name;
    private String website;
}
