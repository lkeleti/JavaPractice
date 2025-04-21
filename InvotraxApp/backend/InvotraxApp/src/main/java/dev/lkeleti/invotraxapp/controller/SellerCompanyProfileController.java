package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreateSellerCompanyProfileCommand;
import dev.lkeleti.invotraxapp.dto.SellerCompanyProfileDto;
import dev.lkeleti.invotraxapp.dto.UpdateSellerCompanyProfileCommand;
import dev.lkeleti.invotraxapp.service.SellerCompanyProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller-profile")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Saját cég adatainak kezelése")
public class SellerCompanyProfileController {

    private final SellerCompanyProfileService sellerService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "A saját cég adatainak lekérdezése")
    @ApiResponse(responseCode = "200", description = "Sikeres lekérdezés")
    public SellerCompanyProfileDto getCompanyProfile() {
        return sellerService.getProfile();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Saját cég létrehozása, ha még nem létezik",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Cég létrehozás adatai",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateSellerCompanyProfileCommand.class)))
    )
    @ApiResponse(responseCode = "201", description = "Cég létrehozva")
    public SellerCompanyProfileDto createCompanyProfile(@RequestBody CreateSellerCompanyProfileCommand command) {
        return sellerService.createProfile(command);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Saját cég adatainak frissítése",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Frissítési adatok",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UpdateSellerCompanyProfileCommand.class)))
    )
    @ApiResponse(responseCode = "200", description = "Sikeres frissítés")
    public SellerCompanyProfileDto updateCompanyProfile(@RequestBody UpdateSellerCompanyProfileCommand command) {
        return sellerService.updateProfile(command);
    }
}