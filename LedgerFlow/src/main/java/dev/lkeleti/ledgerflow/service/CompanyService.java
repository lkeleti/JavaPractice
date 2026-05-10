package dev.lkeleti.ledgerflow.service;

import dev.lkeleti.ledgerflow.dto.request.CompanyCreateRequest;
import dev.lkeleti.ledgerflow.dto.request.CompanyUpdateRequest;
import dev.lkeleti.ledgerflow.dto.response.CompanyResponse;
import dev.lkeleti.ledgerflow.entity.Company;
import dev.lkeleti.ledgerflow.exception.BusinessValidationException;
import dev.lkeleti.ledgerflow.exception.ErrorMessage;
import dev.lkeleti.ledgerflow.exception.NotFoundException;
import dev.lkeleti.ledgerflow.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyResponse create(CompanyCreateRequest request) {

        if (companyRepository.count() > 0) {
            throw new BusinessValidationException(ErrorMessage.COMPANY_ALREADY_EXISTS);
        }

        Company company = new Company();
        company.setName(request.getName());
        company.setTaxNumber(request.getTaxNumber());
        company.setPostalCode(request.getPostalCode());
        company.setCity(request.getCity());
        company.setStreetName(request.getStreetName());
        company.setStreetType(request.getStreetType());
        company.setHouseNumber(request.getHouseNumber());
        company.setClosedAccountingPeriod(LocalDate.of(1900, 1, 1));

        return toResponse(companyRepository.save(company));
    }

    @Transactional(readOnly = true)
    public CompanyResponse get() {
        return companyRepository.findAll()
                .stream()
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMPANY_NOT_FOUND));
    }

    @Transactional
    public CompanyResponse update(Long id, CompanyUpdateRequest request) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMPANY_NOT_FOUND));

        company.setName(request.getName());
        company.setTaxNumber(request.getTaxNumber());
        company.setPostalCode(request.getPostalCode());
        company.setCity(request.getCity());
        company.setStreetName(request.getStreetName());
        company.setStreetType(request.getStreetType());
        company.setHouseNumber(request.getHouseNumber());

        return toResponse(companyRepository.save(company));
    }

    @Transactional
    public CompanyResponse closePeriod(LocalDate newClosedDate) {

        Company company = companyRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMPANY_NOT_FOUND));

        if (newClosedDate.isBefore(company.getClosedAccountingPeriod())) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CANNOT_MOVE_BACK);
        }

        company.setClosedAccountingPeriod(newClosedDate);
        return toResponse(companyRepository.save(company));
    }

    @Transactional
    public CompanyResponse reopenPeriod(LocalDate newClosedDate) {

        Company company = companyRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessage.COMPANY_NOT_FOUND));

        if (newClosedDate.isAfter(company.getClosedAccountingPeriod())) {
            throw new BusinessValidationException(ErrorMessage.ACCOUNTING_PERIOD_CANNOT_MOVE_FORWARD);
        }

        company.setClosedAccountingPeriod(newClosedDate);
        return toResponse(companyRepository.save(company));
    }

    private CompanyResponse toResponse(Company c) {
        CompanyResponse r = new CompanyResponse();
        r.setId(c.getId());
        r.setName(c.getName());
        r.setTaxNumber(c.getTaxNumber());
        r.setPostalCode(c.getPostalCode());
        r.setCity(c.getCity());
        r.setStreetName(c.getStreetName());
        r.setStreetType(c.getStreetType());
        r.setHouseNumber(c.getHouseNumber());
        r.setClosedAccountingPeriod(c.getClosedAccountingPeriod());
        return r;
    }
}
