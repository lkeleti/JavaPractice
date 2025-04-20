package dev.lkeleti.invotraxapp.controller;

import dev.lkeleti.invotraxapp.dto.InventoryDto;
import dev.lkeleti.invotraxapp.dto.UpdateInventoryCommand;
import dev.lkeleti.invotraxapp.dto.CreateInventoryCommand;
import dev.lkeleti.invotraxapp.repository.InventoryRepository;
import dev.lkeleti.invotraxapp.service.InventoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@AllArgsConstructor
public class InventoryController {
    private InventoryRepository inventoryRepository;
    private InventoryService inventoryService;

    @GetMapping
    public List<InventoryDto> getAllInventories() {
        return inventoryService.getAllInventories();
    }

    @GetMapping("/{id}")
    public InventoryDto getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }

    @PostMapping
    public InventoryDto createInventory(@RequestBody CreateInventoryCommand createInventoryCommand) {
        return inventoryService.createInventory(createInventoryCommand);
    }

    @PutMapping("/{id}")
    public InventoryDto updateInventory(@PathVariable Long id, @RequestBody UpdateInventoryCommand updateInventoryCommand) {
        return inventoryService.updateInventory(id, updateInventoryCommand);
    }

    @DeleteMapping("/{id}")
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}
