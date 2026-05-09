package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.VatCodeCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.VatCodeUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.VatCodeResponse;
import dev.lkeleti.ledgerflow.entity.VatCode;
import dev.lkeleti.ledgerflow.exception.BusinessValidationException;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.mapper.VatCodeMapper;
import dev.lkeleti.ledgerflow.repository.VatCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VatCodeService {

    private final VatCodeRepository repository;
    private final VatCodeMapper mapper;

    @Transactional
    public VatCodeResponse create(VatCodeCreateRequest request) {

        if (repository.existsByCode(request.getCode())) {
            throw new BusinessValidationException(ErrorMessage.VAT_CODE_ALREADY_EXISTS);
        }

        VatCode v = new VatCode();

        v.setCode(request.getCode());
        v.setName(request.getName());
        v.setRate(request.getRate());
        v.setType(request.getType());
        v.setDeductible(request.isDeductible());
        v.setActive(request.isActive());
        v.setDeleted(false);

        return mapper.toResponse(repository.save(v));
    }

    @Transactional(readOnly = true)
    public VatCodeResponse getById(Long id) {

        VatCode v = repository.findById(id)
                .filter(vc -> !vc.isDeleted())
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.VAT_CODE_NOT_FOUND)
                );

        return mapper.toResponse(v);
    }

    @Transactional(readOnly = true)
    public List<VatCodeResponse> getAll() {
        return repository.findAll()
                .stream()
                .filter(v -> !v.isDeleted())
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VatCodeResponse> getAllIncludingDeleted() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public VatCodeResponse update(Long id, VatCodeUpdateRequest request) {

        VatCode v = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.VAT_CODE_NOT_FOUND)
                );

        v.setCode(request.getCode());
        v.setName(request.getName());
        v.setRate(request.getRate());
        v.setType(request.getType());
        v.setDeductible(request.isDeductible());
        v.setActive(request.isActive());

        return mapper.toResponse(repository.save(v));
    }

    @Transactional
    public void delete(Long id) {

        VatCode v = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.VAT_CODE_NOT_FOUND)
                );

        v.setDeleted(true);
        repository.save(v);
    }

    @Transactional
    public VatCodeResponse restore(Long id) {

        VatCode v = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(ErrorMessage.VAT_CODE_NOT_FOUND)
                );

        v.setDeleted(false);
        return mapper.toResponse(repository.save(v));
    }
}
