package dev.lkeleti.invotraxapp.service;

import dev.lkeleti.invotraxapp.dto.CreateManufacturerCommand;
import dev.lkeleti.invotraxapp.dto.ManufacturerDto;
import dev.lkeleti.invotraxapp.dto.UpdateManufacturerCommand;
import dev.lkeleti.invotraxapp.model.Manufacturer;
import dev.lkeleti.invotraxapp.repository.ManufacturerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ManufacturerService {
    private ManufacturerRepository manufacturerRepository;
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<ManufacturerDto> getAllManufacturers() {
        Type targetListType = new TypeToken<List<ManufacturerDto>>(){}.getType();
        return modelMapper.map(manufacturerRepository.findAll(), targetListType);
    }



    @Transactional(readOnly = true)
    public ManufacturerDto getManufacturerById(Long id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find manufacturer")
        );
        return modelMapper.map(manufacturer, ManufacturerDto.class);
    }

    @Transactional
    public ManufacturerDto createManufacturer(CreateManufacturerCommand command) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(command.getName());
        manufacturer.setWebsite(command.getWebsite());
        manufacturer.setProducts(new ArrayList<>());
        return modelMapper.map(manufacturer, ManufacturerDto.class);
    }

    @Transactional
    public void deleteManufacturer(Long id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manufacturer not found"));

        if (!manufacturer.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete manufacturer with existing products.");
        }
        manufacturerRepository.delete(manufacturer);
    }

    @Transactional
    public ManufacturerDto updateManufacturer(long id, UpdateManufacturerCommand command) {
        Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Cannot find manufacturer")
        );

        manufacturer.setName(command.getName());
        manufacturer.setWebsite(command.getWebsite());
        return modelMapper.map(manufacturer, ManufacturerDto.class);
    }

    public Page<ManufacturerDto> getAllManufacturers(Pageable pageable, String searchTerm) {
        Page<Manufacturer> manufacturers;
        if (searchTerm == null || searchTerm.isBlank()) {
            manufacturers = manufacturerRepository.findAll(pageable);
        } else {
            manufacturers = manufacturerRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
        }

        return manufacturers.map(manufacturer -> modelMapper.map(manufacturer, ManufacturerDto.class));
    }
}
