package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.VatCodeCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.VatCodeUpdateRequest;
import dev.lkeleti.ledgerflow.entity.VatCode;
import dev.lkeleti.ledgerflow.repository.VatCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VatCodeService {

    private final VatCodeRepository repository;

    @Transactional
    public VatCode create(VatCodeCreateRequest request) {

        if (repository.existsByCode(request.getCode())) {
            throw new IllegalStateException(
                    "Vat code already exists"
            );
        }

        VatCode vatCode = new VatCode();

        map(request, vatCode);

        return repository.save(vatCode);
    }

    @Transactional(readOnly = true)
    public VatCode getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vat code not found"));
    }

    @Transactional(readOnly = true)
    public List<VatCode> getAll() {

        return repository.findAll();
    }

    @Transactional
    public VatCode update(
            Long id,
            VatCodeUpdateRequest request
    ) {

        VatCode vatCode = getById(id);

        map(request, vatCode);

        return repository.save(vatCode);
    }

    @Transactional
    public void delete(Long id) {

        VatCode vatCode = getById(id);

        vatCode.setActive(false);

        repository.save(vatCode);
    }

    // =========================
    // MAPPER
    // =========================

    private void map(
            VatCodeCreateRequest request,
            VatCode vatCode
    ) {

        vatCode.setCode(request.getCode());
        vatCode.setName(request.getName());
        vatCode.setRate(request.getRate());
        vatCode.setType(request.getType());
        vatCode.setDeductible(request.isDeductible());
        vatCode.setActive(request.isActive());
    }
}