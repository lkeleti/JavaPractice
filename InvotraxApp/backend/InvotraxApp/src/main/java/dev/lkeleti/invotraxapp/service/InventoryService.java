package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.*;
import dev.lkeleti.invotraxapp.model.Inventory;
import dev.lkeleti.invotraxapp.model.InventoryItem;
import dev.lkeleti.invotraxapp.model.Partner;
import dev.lkeleti.invotraxapp.model.Product;
import dev.lkeleti.invotraxapp.repository.InventoryRepository;
import dev.lkeleti.invotraxapp.repository.PartnerRepository;
import dev.lkeleti.invotraxapp.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class InventoryService {
    private InventoryRepository inventoryRepository;
    private PartnerRepository partnerRepository;
    private ProductRepository productRepository;
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

                productRepository.save(product);
                inventory.getItems().add(newItem);
            }
        }
        Inventory savedInventory = inventoryRepository.save(inventory);
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
        for (InventoryItem oldItem : oldItems) {
            Product product = oldItem.getProduct();
            if (product.getProductType().isManagesStock()) {
                stockAdjustments.put(product.getId(), stockAdjustments.getOrDefault(product.getId(), 0) - oldItem.getQuantity());
            }
        }

        if (updateInventoryCommand.getUpdateInventoryItemCommands() != null) {
            for (UpdateInventoryItemCommand newItemCommand : updateInventoryCommand.getUpdateInventoryItemCommands()) {
                Product product = productRepository.findById(newItemCommand.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Cannot find Product with id: " + newItemCommand.getProductId()));
                if (product.getProductType().isManagesStock()) {
                    stockAdjustments.put(product.getId(), stockAdjustments.getOrDefault(product.getId(), 0) + newItemCommand.getQuantity());
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : stockAdjustments.entrySet()) {
            Long productId = entry.getKey();
            Integer adjustment = entry.getValue();

            if (adjustment != 0) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new EntityNotFoundException("Cannot find Product with id: " + productId + " for stock adjustment")); // Hiba, ha közben törlődött

                int currentStock = product.getStockQuantity();
                int newStock = currentStock + adjustment;

                if (newStock < 0) {
                    throw new IllegalStateException("Stock update for product ID " + productId + " [" + product.getName() + "] results in negative quantity (" + newStock + ")");
                }
                product.setStockQuantity(newStock);
                productRepository.save(product);
            }
        }

        inventory.getItems().clear();

        if (updateInventoryCommand.getUpdateInventoryItemCommands() != null) {
            for (UpdateInventoryItemCommand newItemCommand : updateInventoryCommand.getUpdateInventoryItemCommands()) {
                Product product = productRepository.findById(newItemCommand.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Cannot find Product with id: " + newItemCommand.getProductId()));

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
            }
        }

        Inventory savedInventory = inventoryRepository.save(inventory);
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
