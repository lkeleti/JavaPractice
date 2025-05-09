package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.*;
import dev.lkeleti.invotraxapp.model.*;
import dev.lkeleti.invotraxapp.repository.InventoryRepository;
import dev.lkeleti.invotraxapp.repository.PartnerRepository;
import dev.lkeleti.invotraxapp.repository.ProductRepository;
import dev.lkeleti.invotraxapp.repository.SerialNumberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.*;

@AllArgsConstructor
@Service
public class InventoryService {
    private InventoryRepository inventoryRepository;
    private PartnerRepository partnerRepository;
    private ProductRepository productRepository;
    private SerialNumberRepository serialNumberRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<InventoryDto> getAllInventories(Pageable pageable, String searchTerm) {
        Page<Inventory> inventories;
        if (searchTerm == null || searchTerm.isBlank()) {
            inventories = inventoryRepository.findAll(pageable);
        } else {
            inventories = inventoryRepository.searchBySupplierNameOrSupplierTaxOrReceivedAtOrInvoiceNumber(searchTerm, pageable);
        }

        Type targetListType = new TypeToken<List<InventoryDto>>(){}.getType();
        return modelMapper.map(inventories, targetListType);
    }

    @Transactional(readOnly = true)
    public InventoryDto getInventoryById(Long id) {
        return modelMapper.map(inventoryRepository.findByIdWithAllData(id), InventoryDto.class);
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

        List<SerialNumber> serialsToSave = new ArrayList<>();

        if (createInventoryCommand.getCreateInventoryItemCommands() != null) {
            for (CreateInventoryItemCommand itemCommand : createInventoryCommand.getCreateInventoryItemCommands()) {
                Product product = productRepository.findById(itemCommand.getProductId()).orElseThrow(
                        () -> new EntityNotFoundException("Cannot find Product with id: " + itemCommand.getProductId())
                );

                InventoryItem newItem = new InventoryItem();
                newItem.setInventory(inventory);
                newItem.setProduct(product);
                newItem.setQuantity(itemCommand.getQuantity());
                newItem.setNetPurchasePrice(itemCommand.getNetPurchasePrice());
                newItem.setGrossPurchasePrice(itemCommand.getGrossPurchasePrice());
                newItem.setNetSellingPrice(itemCommand.getNetSellingPrice());
                newItem.setGrossSellingPrice(itemCommand.getGrossSellingPrice());
                newItem.setWarrantyPeriodMonths(itemCommand.getWarrantyPeriodMonths());

                if (product.getProductType().isManagesStock()) {
                    int currentStock = product.getStockQuantity();
                    product.setStockQuantity(currentStock + itemCommand.getQuantity());
                }

                product.setNetPurchasePrice(itemCommand.getNetPurchasePrice());
                product.setGrossPurchasePrice(itemCommand.getGrossPurchasePrice());
                product.setNetSellingPrice(itemCommand.getNetSellingPrice());
                product.setGrossSellingPrice(itemCommand.getGrossSellingPrice());
                product.setWarrantyPeriodMonths(itemCommand.getWarrantyPeriodMonths());

                if (product.isSerialNumberRequired()) {
                    if (itemCommand.getSerialNumbers() == null || itemCommand.getSerialNumbers().isEmpty()) {
                        throw new ValidationException("Serial numbers are required for product: " + product.getName() + " (ID: " + product.getId() + ")");
                    }
                    if (itemCommand.getSerialNumbers().size() != itemCommand.getQuantity()) {
                        throw new ValidationException("Number of serial numbers (" + itemCommand.getSerialNumbers().size() +
                                ") does not match quantity (" + itemCommand.getQuantity() + ") for product: " + product.getName());
                    }

                    Set<String> uniqueSerials = new HashSet<>(itemCommand.getSerialNumbers());
                    if (uniqueSerials.size() != itemCommand.getSerialNumbers().size()) {
                        throw new ValidationException("Duplicate serial numbers provided for product: " + product.getName());
                    }

                    for (String serialStr : itemCommand.getSerialNumbers()) {
                        if (serialNumberRepository.existsBySerial(serialStr)) {
                            throw new ValidationException("Serial number already exists: " + serialStr);
                        }
                    }

                    for (String serialStr : itemCommand.getSerialNumbers()) {
                        SerialNumber newSerial = new SerialNumber();
                        newSerial.setSerial(serialStr);
                        newSerial.setProduct(product);
                        newSerial.setInventoryItem(newItem);
                        newSerial.setUsed(false);
                        serialsToSave.add(newSerial);
                    }
                } else {
                    if (itemCommand.getSerialNumbers() != null && !itemCommand.getSerialNumbers().isEmpty()) {
                        throw new ValidationException("Serial numbers provided for product that does not require them: " + product.getName());
                    }
                }

                productRepository.save(product);
                inventory.getItems().add(newItem);
            }
        }
        Inventory savedInventory = inventoryRepository.save(inventory);
        if (!serialsToSave.isEmpty()) {
            serialNumberRepository.saveAll(serialsToSave);
        }
        return modelMapper.map(savedInventory, InventoryDto.class);
    }

    @Transactional
    public InventoryDto updateInventory(Long id, UpdateInventoryCommand updateInventoryCommand) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find inventory")
        );

        List<InventoryItem> oldItems = new ArrayList<>(inventory.getItems());

        Partner supplier = partnerRepository.findById(updateInventoryCommand.getSupplierId()).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find Supplier")
        );

        inventory.setSupplier(supplier);
        inventory.setInvoiceNumber(updateInventoryCommand.getInvoiceNUmber());
        inventory.setReceivedAt(updateInventoryCommand.getReceivedAt());

        Map<Long, Integer> stockAdjustments = new HashMap<>();
        List<SerialNumber> serialsToDelete = new ArrayList<>();
        List<SerialNumber> serialsToSave = new ArrayList<>(); // Új gyáriszámok gyűjtése

        for (InventoryItem oldItem : oldItems) {
            Product product = oldItem.getProduct();
            if (product.getProductType().isManagesStock()) {
                stockAdjustments.put(product.getId(), stockAdjustments.getOrDefault(product.getId(), 0) - oldItem.getQuantity());
            }
            if (product.isSerialNumberRequired()) {
                List<SerialNumber> oldSerials = serialNumberRepository.findByInventoryItemId(oldItem.getId());
                for (SerialNumber sn : oldSerials) {
                    if (sn.isUsed()) {
                        throw new ValidationException("Cannot update inventory. Serial number " + sn.getSerial() +
                                " linked to item ID " + oldItem.getId() + " is already marked as used (sold).");
                    }
                    serialsToDelete.add(sn);
                }
            }
        }

        if (updateInventoryCommand.getUpdateInventoryItemCommands() != null) {
            for (UpdateInventoryItemCommand newItemCommand : updateInventoryCommand.getUpdateInventoryItemCommands()) {
                Product product = productRepository.findById(newItemCommand.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Cannot find Product with id: " + newItemCommand.getProductId()));

                if (product.getProductType().isManagesStock()) {
                    stockAdjustments.put(product.getId(), stockAdjustments.getOrDefault(product.getId(), 0) + newItemCommand.getQuantity());
                }

                if (product.isSerialNumberRequired()) {
                    if (newItemCommand.getSerialNumbers() == null || newItemCommand.getSerialNumbers().isEmpty()) {
                        throw new ValidationException("Serial numbers required for product: " + product.getName());
                    }
                    if (newItemCommand.getSerialNumbers().size() != newItemCommand.getQuantity()) {
                        throw new ValidationException("Serial number count mismatch for product: " + product.getName());
                    }
                    Set<String> uniqueSerials = new HashSet<>(newItemCommand.getSerialNumbers());
                    if (uniqueSerials.size() != newItemCommand.getSerialNumbers().size()) {
                        throw new ValidationException("Duplicate serial numbers provided for product: " + product.getName());
                    }
                    for (String serialStr : newItemCommand.getSerialNumbers()) {
                        if (serialNumberRepository.existsBySerial(serialStr)) {
                            throw new ValidationException("Serial number already exists: " + serialStr);
                        }
                    }
                } else {
                    if (newItemCommand.getSerialNumbers() != null && !newItemCommand.getSerialNumbers().isEmpty()) {
                        throw new ValidationException("Serial numbers provided for product that does not require them: " + product.getName());
                    }
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : stockAdjustments.entrySet()) {
            Long productId = entry.getKey();
            Integer adjustment = entry.getValue();
            if (adjustment != 0) {
                Product product = productRepository.findById(productId).orElseThrow(/*...*/);
                int currentStock = product.getStockQuantity();
                int newStock = currentStock + adjustment;
                if (newStock < 0) {
                    throw new IllegalStateException("Stock update for product ID " + productId + " [" + product.getName() + "] results in negative quantity (" + newStock + ")");
                }
                product.setStockQuantity(newStock);
                productRepository.save(product);
            }
        }

        if (!serialsToDelete.isEmpty()) {
            serialNumberRepository.deleteAll(serialsToDelete);
        }

        inventory.getItems().clear();

        if (updateInventoryCommand.getUpdateInventoryItemCommands() != null) {
            for (UpdateInventoryItemCommand newItemCommand : updateInventoryCommand.getUpdateInventoryItemCommands()) {
                Product product = productRepository.findById(newItemCommand.getProductId()).orElseThrow(/*...*/);

                InventoryItem newItem = new InventoryItem();
                newItem.setInventory(inventory);
                newItem.setProduct(product);
                newItem.setQuantity(newItemCommand.getQuantity());
                newItem.setNetPurchasePrice(newItemCommand.getNetPurchasePrice());
                newItem.setGrossPurchasePrice(newItemCommand.getGrossPurchasePrice());
                newItem.setNetSellingPrice(newItemCommand.getNetSellingPrice());
                newItem.setGrossSellingPrice(newItemCommand.getGrossSellingPrice());
                newItem.setWarrantyPeriodMonths(newItemCommand.getWarrantyPeriodMonths());

                product.setNetPurchasePrice(newItemCommand.getNetPurchasePrice());
                product.setGrossPurchasePrice(newItemCommand.getGrossPurchasePrice());
                product.setNetSellingPrice(newItemCommand.getNetSellingPrice());
                product.setGrossSellingPrice(newItemCommand.getGrossSellingPrice());
                product.setWarrantyPeriodMonths(newItemCommand.getWarrantyPeriodMonths());
                productRepository.save(product);

                inventory.getItems().add(newItem);
                if (product.isSerialNumberRequired()) {
                    for (String serialStr : newItemCommand.getSerialNumbers()) {
                        SerialNumber newSerial = new SerialNumber();
                        newSerial.setSerial(serialStr);
                        newSerial.setProduct(product);
                        newSerial.setInventoryItem(newItem);
                        newSerial.setUsed(false);
                        serialsToSave.add(newSerial);
                    }
                }
            }
        }

        Inventory savedInventory = inventoryRepository.save(inventory);
        if (!serialsToSave.isEmpty()) {
            serialNumberRepository.saveAll(serialsToSave);
        }
        return modelMapper.map(savedInventory, InventoryDto.class);
    }

    @Transactional
    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Cannot find inventory with id: " + id)
        );

        for (InventoryItem item : inventory.getItems()) {
            Product product = item.getProduct();

            if (product.getProductType().isManagesStock()) {
                int quantityToRemove = item.getQuantity();
                int currentStock = product.getStockQuantity();
                int newStock = currentStock - quantityToRemove;

                if (newStock < 0) {
                    System.err.println("Warning: Product stock for " + product.getId() + " [" + product.getName() + "] would go negative on inventory deletion. Setting to 0.");
                    newStock = 0;
                    //throw new IllegalStateException("Stock inconsistency detected for product ID " + product.getId() + " during inventory deletion.");
                }
                product.setStockQuantity(newStock);
                productRepository.save(product);
            }
        }
        inventoryRepository.delete(inventory);
    }
}
