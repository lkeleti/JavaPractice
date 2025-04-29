package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreatePartnerCommand;
import dev.lkeleti.invotraxapp.dto.PartnerDto;
import dev.lkeleti.invotraxapp.dto.UpdatePartnerCommand;
import dev.lkeleti.invotraxapp.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "Műveletek a partnerekkel")

public class PertnerController {
        private PartnerService partnerService;

        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        @Operation(summary = "Összes partner listázása oldalakban (paging)",
                description = "Visszaadja az összes partner listáját még a törölteket is oldalakban, page/size paraméterezéssel és névre, vagy adószámra történő kereséssel/szűkítéssel.")
        @ApiResponse(responseCode = "200", description = "Partner sikeresen listázva")
        public Page<PartnerDto> getAllPartners(Pageable pageable, @RequestParam(required = false) String searchTerm) {
            return partnerService.getAllPartners(pageable, searchTerm);
        }

        @GetMapping("/active")
        @ResponseStatus(HttpStatus.OK)
        @Operation(summary = "Összes aktív partner listázása",
                description = "Visszaadja az összes partner listáját, de csak az aktívakat.")
        @ApiResponse(responseCode = "200", description = "Partnerek sikeresen listázva")
        public List<PartnerDto> getAllActivePartners() {
            return partnerService.getAllActivePartners();
        }

        @GetMapping("/active/{taxnumber}")
        @ResponseStatus(HttpStatus.OK)
        @Operation(summary = "Partner listázása adószám alapján",
                description = "Visszaadja az összes adószám listáját, ami megfelel a kritériumnak.")
        @ApiResponse(responseCode = "200", description = "Partner sikeresen listázva")
        public PartnerDto getPartnerByTaxNumber(@PathVariable String taxNumber) {
            return partnerService.getPartnerByTaxNumber(taxNumber);
        }

        @GetMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        @Operation(summary = "Egy partner lekérdezése azonosító alapján",
                description = "Visszaadja a megadott partner adatait.")
        @ApiResponse(responseCode = "200", description = "Partner sikeresen lekérdezve")
        @ApiResponse(responseCode = "404", description = "Partner nem található")
        public PartnerDto getPartnerById(@PathVariable long id) {
            return partnerService.getPartnerById(id);
        }

        @PutMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        @Operation(summary = "Partner adatainak módosítása",
                description = "Meglévő partner adatainak módosítása az azonosító és a request body-ban megadott adatok alapján.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "A partner adatainak módosításához a szükséges adatok JSON formátumban.",
                        required = true,
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = UpdatePartnerCommand.class))
                )
        )
        @ApiResponse(responseCode = "200", description = "Partber sikeresen frissítve")
        @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
        @ApiResponse(responseCode = "404", description = "Módosítandó partner nem található")
        public PartnerDto updatePartner(@PathVariable Long id, @RequestBody UpdatePartnerCommand command) {
            return partnerService.updatePartner(id, command);
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(summary = "Új partner rögzítése",
                description = "Új partner rögzítése a request body-ban megadott adatok alapján.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Az új partner létrehozásához szükséges adatok JSON formátumban.",
                        required = true,
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = CreatePartnerCommand.class))
                )
        )
        @ApiResponse(responseCode = "201", description = "Partner sikeresen létrehozva")
        @ApiResponse(responseCode = "400", description = "Érvénytelen adatok a kérésben (validációs hiba)")
        public PartnerDto createPartner(@RequestBody CreatePartnerCommand command) {
            return partnerService.createPartner(command);
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @Operation(summary = "Partner törlése",
                description = "Partner törlése a megadott azonosító alapján (logikai törlés)."
        )
        @ApiResponse(responseCode = "204", description = "Partner sikeresen törölve")
        @ApiResponse(responseCode = "404", description = "Törlendő partner nem található")
        public void deletePartner(@PathVariable Long id) {
            partnerService.deletePartner(id);
        }

        @DeleteMapping("/undelete/{id}")
        @ResponseStatus(HttpStatus.OK)
        @Operation(summary = "Partner törlésének megszüntetése",
                description = "Partner visszaállítása aktív állapotba a megadott azonosító alapján."
        )
        @ApiResponse(responseCode = "200", description = "Partne sikeresen visszaállítva")
        @ApiResponse(responseCode = "404", description = "Partner nem található")
        public PartnerDto unDeletePartner(@PathVariable Long id) {
            return partnerService.unDeletePartner(id);
        }
    }
