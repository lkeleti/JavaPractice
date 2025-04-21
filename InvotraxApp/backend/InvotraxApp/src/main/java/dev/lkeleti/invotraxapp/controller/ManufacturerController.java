package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.CreateManufacturerCommand;
import dev.lkeleti.invotraxapp.dto.ManufacturerDto;
import dev.lkeleti.invotraxapp.dto.UpdateManufacturerCommand;
import dev.lkeleti.invotraxapp.service.ManufacturerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturers")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class ManufacturerController {
    private ManufacturerService manufacturerService;

    @GetMapping
    public List<ManufacturerDto> getAllManufacturers() {
        return manufacturerService.getAllManufacturers();
    }

    @GetMapping("/{id}")
    public ManufacturerDto getManufacturerById(@PathVariable Long id) {
        return manufacturerService.getManufacturerById(id);
    }

    @PutMapping("/{id}")
    public ManufacturerDto updateManufacturer(@PathVariable Long id, @RequestBody UpdateManufacturerCommand command) {
        return manufacturerService.updateManufacturer(id, command);
    }

    @PostMapping
    public ManufacturerDto createManufacturer(@RequestBody CreateManufacturerCommand command) {
        return manufacturerService.createManufacturer(command);
    }

    @DeleteMapping("/{id}")
    public void deleteManufacturer(@PathVariable Long id) {
        manufacturerService.deleteManufacturer(id);
    }
}
