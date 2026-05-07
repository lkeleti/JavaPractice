package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.PartnerCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.PartnerUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.PartnerResponse;
import dev.lkeleti.ledgerflow.entity.Partner;
import dev.lkeleti.ledgerflow.service.PartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping
    public PartnerResponse create(
            @Valid @RequestBody PartnerCreateRequest request
    ) {

        Partner partner = partnerService.create(request);

        return map(partner);
    }

    @GetMapping("/{id}")
    public PartnerResponse getById(
            @PathVariable Long id
    ) {

        return map(partnerService.getById(id));
    }

    @GetMapping
    public List<PartnerResponse> getAll() {

        return partnerService.getAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @PutMapping("/{id}")
    public PartnerResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PartnerUpdateRequest request
    ) {

        return map(partnerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        partnerService.delete(id);
    }

    // =========================
    // MAPPER
    // =========================

    private PartnerResponse map(Partner partner) {

        PartnerResponse response = new PartnerResponse();

        response.setId(partner.getId());
        response.setName(partner.getName());
        response.setPrivatePerson(partner.isPrivatePerson());
        response.setTaxNumber(partner.getTaxNumber());
        response.setEmail(partner.getEmail());
        response.setPhone(partner.getPhone());
        response.setDeleted(partner.isDeleted());

        return response;
    }
}