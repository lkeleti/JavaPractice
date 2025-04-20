package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreateInventoryCommand;
import dev.lkeleti.invotraxapp.dto.InventoryDto;
import dev.lkeleti.invotraxapp.dto.UpdateInventoryCommand;
import dev.lkeleti.invotraxapp.model.Inventory;
import dev.lkeleti.invotraxapp.model.Partner;
import dev.lkeleti.invotraxapp.repository.InventoryRepository;
import dev.lkeleti.invotraxapp.repository.PartnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@AllArgsConstructor
@Service
public class InventoryService {
    private InventoryRepository inventoryRepository;
    private PartnerRepository partnerRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<InventoryDto> getAllInventories() {
        Type targetListType = new TypeToken<List<InventoryDto>>(){}.getType();
        return modelMapper.map(inventoryRepository.findAll(), targetListType);
    }

    @Transactional(readOnly = true)
    public InventoryDto getInventoryById(Long id) {
        return modelMapper.map(inventoryRepository.findById(id), InventoryDto.class);
    }

    @Transactional
    public InventoryDto createInventory(CreateInventoryCommand createInventoryCommand) {
        Partner supplier = partnerRepository.findById(createInventoryCommand.getSupplierId()).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find Supplier")
        );

        Inventory inventory = new Inventory(
                supplier,
                createInventoryCommand.getReceivedAt(),
                createInventoryCommand.getInvoiceNUmber()
        );
        return modelMapper.map(inventoryRepository.save(inventory), InventoryDto.class);
    }

    @Transactional
    public InventoryDto updateInventory(Long id, UpdateInventoryCommand updateInventoryCommand) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find inventory")
        );

        Partner supplier = partnerRepository.findById(updateInventoryCommand.getSupplierId()).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find Supplier")
        );

        inventory.setSupplier(supplier);
        inventory.setInvoiceNUmber(updateInventoryCommand.getInvoiceNUmber());
        inventory.setReceivedAt(updateInventoryCommand.getReceivedAt());
        return modelMapper.map(inventory, InventoryDto.class);
    }

    @Transactional
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}
