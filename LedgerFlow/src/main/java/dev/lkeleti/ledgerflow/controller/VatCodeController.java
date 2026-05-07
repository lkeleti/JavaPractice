package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.VatCodeCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.VatCodeUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.VatCodeResponse;
import dev.lkeleti.ledgerflow.entity.VatCode;
import dev.lkeleti.ledgerflow.service.VatCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vat-codes")
@RequiredArgsConstructor
public class VatCodeController {

    private final VatCodeService vatCodeService;

    @PostMapping
    public VatCodeResponse create(
            @Valid @RequestBody VatCodeCreateRequest request
    ) {

        return map(vatCodeService.create(request));
    }

    @GetMapping("/{id}")
    public VatCodeResponse getById(
            @PathVariable Long id
    ) {

        return map(vatCodeService.getById(id));
    }

    @GetMapping
    public List<VatCodeResponse> getAll() {

        return vatCodeService.getAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @PutMapping("/{id}")
    public VatCodeResponse update(
            @PathVariable Long id,
            @Valid @RequestBody VatCodeUpdateRequest request
    ) {

        return map(vatCodeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        vatCodeService.delete(id);
    }

    // =========================
    // MAPPER
    // =========================

    private VatCodeResponse map(
            VatCode vatCode
    ) {

        VatCodeResponse response =
                new VatCodeResponse();

        response.setId(vatCode.getId());
        response.setCode(vatCode.getCode());
        response.setName(vatCode.getName());
        response.setRate(vatCode.getRate());
        response.setType(vatCode.getType());
        response.setDeductible(vatCode.isDeductible());
        response.setActive(vatCode.isActive());

        return response;
    }
}