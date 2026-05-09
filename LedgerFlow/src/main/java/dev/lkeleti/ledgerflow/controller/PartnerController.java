package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.PartnerCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.PartnerUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.PartnerResponse;
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

    @GetMapping
    public List<PartnerResponse> getAll() {

        return partnerService.getAll();
    }

    @GetMapping("/{id}")
    public PartnerResponse getById(
            @PathVariable Long id
    ) {

        return partnerService.getById(id);
    }

    @PostMapping
    public PartnerResponse create(
            @Valid @RequestBody PartnerCreateRequest request
    ) {
        return partnerService.create(request);
    }


    @PutMapping("/{id}")
    public PartnerResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PartnerUpdateRequest request
    ) {

        return partnerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        partnerService.delete(id);
    }

    @PatchMapping("/{id}/restore")
    public PartnerResponse restore(@PathVariable Long id) {
        return partnerService.restore(id);
    }
}