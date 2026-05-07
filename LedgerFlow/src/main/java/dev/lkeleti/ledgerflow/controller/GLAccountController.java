package dev.lkeleti.ledgerflow.controller;

import dev.lkeleti.ledgerflow.dto.request.GLAccountCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.GLAccountUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.GLAccountResponse;
import dev.lkeleti.ledgerflow.entity.GLAccount;
import dev.lkeleti.ledgerflow.service.GLAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gl-accounts")
@RequiredArgsConstructor
public class GLAccountController {

    private final GLAccountService glAccountService;

    @PostMapping
    public GLAccountResponse create(
            @Valid @RequestBody GLAccountCreateRequest request
    ) {

        return map(glAccountService.create(request));
    }

    @GetMapping("/{id}")
    public GLAccountResponse getById(
            @PathVariable Long id
    ) {

        return map(glAccountService.getById(id));
    }

    @GetMapping
    public List<GLAccountResponse> getAll() {

        return glAccountService.getAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @PutMapping("/{id}")
    public GLAccountResponse update(
            @PathVariable Long id,
            @Valid @RequestBody GLAccountUpdateRequest request
    ) {

        return map(
                glAccountService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        glAccountService.delete(id);
    }

    // =========================
    // MAPPER
    // =========================

    private GLAccountResponse map(
            GLAccount account
    ) {

        GLAccountResponse response =
                new GLAccountResponse();

        response.setId(account.getId());
        response.setNumber(account.getNumber());
        response.setName(account.getName());
        response.setType(account.getType());

        response.setVatRelated(
                account.isVatRelated()
        );

        response.setCustomerRelated(
                account.isCustomerRelated()
        );

        response.setSupplierRelated(
                account.isSupplierRelated()
        );

        response.setBookable(
                account.isBookable()
        );

        response.setActive(
                account.isActive()
        );

        return response;
    }
}